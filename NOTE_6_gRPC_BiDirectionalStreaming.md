# gRPC BiDirectional Streaming
```
Client sends multiple requests and server sends multile responses back.
TransferRequest(from,to,amount)<----->TransferResponse(staus,accounts)
```

### 1. Update bank-proto module
1. Create ```transfer-request.proto``` file under src/main/```proto```/```models```
```
syntax="proto3";

option java_multiple_files=true;
option java_package="in.rk.bank.models";

package models;

message TransferRequest
{
  int32 from_account=1;
  int32 to_account=2;
  int32 amount=3;
}
```
2. Create ```transfer-response.proto``` file under src/main/```proto```/```models```
```
syntax="proto3";

option java_multiple_files=true;
option java_package="in.rk.bank.models";

package models;

message TransferResponse
{
  TransferStatus status=1;
  repeated Account accounts=2;
}

enum TransferStatus
{
  FAILED=0;
  SUCCESS=1;
}
message Account
{
  int32 account_number=1;
  int32 amount=2;
}
```
3. Update ```bank-services.proto``` with imports and rpc call for BiDirectional streaming
```
    //4. BiDirectional streaming
    rpc transfer(stream models.TransferRequest) returns (stream models.TransferResponse);
```
4. Run ```mvn clean install``` then check generated files in ```target/generated-sources/protobuf/*``` 

### 2. Update bank-service module
1. Add/override transfer(-) in BankService.java
```
i)     @Override
       public StreamObserver<TransferRequest> transfer(StreamObserver<TransferResponse> responseObserver) {
           return super.transfer(responseObserver);
       }
 
   Here, return type is StreamObserver<TransferRequest>
ii) We have to create a class TransferRequestStreamObserver which implements StreamObserver<TransferRequest>
   and override required methods.
   add below instance variables:
    StreamObserver<TransferResponse> transferResponseStreamObserver;
```
TransferRequestStreamObserver.java
```
package in.rk.bank.streamobserver;

import in.rk.bank.db.AccountDB;
import in.rk.bank.models.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class TransferRequestStreamObserver implements StreamObserver<TransferRequest> {

    private StreamObserver<TransferResponse> transferResponseStreamObserver;

    public TransferRequestStreamObserver(StreamObserver<TransferResponse> transferResponseStreamObserver) {
        this.transferResponseStreamObserver = transferResponseStreamObserver;
    }

    @Override
    public void onNext(TransferRequest req) {
        int fromAccount = req.getFromAccount();
        int toAccount = req.getToAccount();
        int amount = req.getAmount();
        TransferStatus transferStatus = TransferStatus.FAILED;
        TransferResponse.Builder respBuilder = TransferResponse.newBuilder();

        System.out.println("Each stream call " + fromAccount + "--->" + toAccount);

        if (fromAccount != toAccount && AccountDB.isAccountAvailable(fromAccount) && AccountDB.isAccountAvailable(toAccount)) {
            if (AccountDB.getBalance(fromAccount) < amount) {
                System.err.println("Insufficient funds in account");
                Status status = Status.FAILED_PRECONDITION.withDescription("Insufficient funds in account");
                this.transferResponseStreamObserver.onError(status.asRuntimeException());
                return;
            } else {
                AccountDB.deductBalance(fromAccount, amount);
                AccountDB.addBalance(toAccount, amount);
                transferStatus = TransferStatus.SUCCESS;

                respBuilder.addAccounts(Account.newBuilder().setAccountNumber(fromAccount).setAmount(AccountDB.getBalance(fromAccount)).build());
                respBuilder.addAccounts(Account.newBuilder().setAccountNumber(toAccount).setAmount(AccountDB.getBalance(toAccount)).build());

            }
        } else {
            System.err.println("Invalid Accounts");
            Status status = Status.FAILED_PRECONDITION.withDescription("Invalid Accounts");
            this.transferResponseStreamObserver.onError(status.asRuntimeException());
            return;
        }
        respBuilder.setStatus(transferStatus);
        //send over transferResponseStreamObserver
        this.transferResponseStreamObserver.onNext(respBuilder.build());

    }

    @Override
    public void onError(Throwable t) {
        System.err.println("Error: Unexpected error:" + t);
    }

    @Override
    public void onCompleted() {
        //send over transferResponseStreamObserver
        AccountDB.printAccounts();
        this.transferResponseStreamObserver.onCompleted();
    }
}

```
Create new method in AccountDB.ava
```
    public  static void printAccounts()
    {
        System.out.println(accountsMap);
    }
```
2. Update transfer(-) method in BankServie class.
```
    @Override
    public StreamObserver<TransferRequest> transfer(StreamObserver<TransferResponse> responseObserver) {
          return new TransferRequestStreamObserver(responseObserver);
    }

``` 
3. Run ```mvn clean install```

### 3. No Changes in bank-server module
1. Run ```mvn clean install```

### 4. Update bank-client module
```
* We can only access transfer(-) method through BankServiceStub (async stub). we can't access through BankServiceBlockingStub (sync stub).
* Required to create a class TransferResponseStreamObserver which implements StreamObserver<TransferResponse>.
```
1) Create TransferResponseStreamObserver class and implement StreamObserver<TransferResponse>.
```
package in.rk.bank.client.streamobservers;

import in.rk.bank.models.TransferResponse;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class TransferResponseStreamObserver implements StreamObserver<TransferResponse> {
    CountDownLatch latch;

    public TransferResponseStreamObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(TransferResponse resp) {
        System.out.println("Status:"+resp.getStatus());
        resp.getAccountsList().stream()
                .map(account->account.getAccountNumber()+":"+account.getAmount())
                .forEach(System.out::println);
        System.out.println("----------------------");
    }

    @Override
    public void onError(Throwable t) {
        System.err.println("Some Error:"+t);
        this.latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("All Transfer reqs are processed");
        this.latch.countDown();
    }
}
```
2. Create a method definition to call transfer(-) through bidirectional streaming.

```
    private static void transferAsyncBiDirectional(BankServiceGrpc.BankServiceStub asyncStub) {
        CountDownLatch latch=new CountDownLatch(1);
        TransferResponseStreamObserver transferResponseStreamObserver=new TransferResponseStreamObserver(latch);
        StreamObserver<TransferRequest> transferRequestStreamObserver=asyncStub.transfer(transferResponseStreamObserver);

        for(int i=1; i<=5;i++)
        {
            TransferRequest eachReq= TransferRequest.newBuilder()
                    .setFromAccount(ThreadLocalRandom.current().nextInt(1,11))
                    .setToAccount(ThreadLocalRandom.current().nextInt(1,11))
                    .setAmount(5)
                    .build();
            transferRequestStreamObserver.onNext(eachReq);
        }
        transferRequestStreamObserver.onCompleted();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
```
3. Method call in main(-)
```
    //3. Async transfer through :BankServiceGrpc.BankServiceStub
    transferAsyncBiDirectional(asyncStub);

```
4. Run ```mvn clean install```

### Testing-1 : bank-client
1. start Server:  bank-server--> GrpcServer:main(-)
2. Send request from Client: bank-client-->AsyncGrpcClient:main(-)--transferAsyncBiDirectional(-)

If server is up

Client Logs:
```
Status:SUCCESS
10:95
8:85
----------------------
Status:SUCCESS
9:85
3:35
----------------------
Status:SUCCESS
3:30
1:15
----------------------
Status:SUCCESS
10:90
5:55
----------------------
Status:SUCCESS
10:85
4:45
----------------------
All Transfer reqs are processed
```
Server Logs:
```
Server is started with BankService!
Each stream call 10--->8
Each stream call 9--->3
Each stream call 3--->1
Each stream call 10--->5
Each stream call 10--->4
{1=15, 2=20, 3=30, 4=45, 5=55, 6=60, 7=70, 8=85, 9=85, 10=85}
```

If server is down
```
Some Error:io.grpc.StatusRuntimeException: UNAVAILABLE: io exception
```
### Testing-2 : Postman client
```
Open Postman Desktop
Sign in with gmail--> Collection-->new-->gRPC Request--> 
Enter Server URL: localhost:6565
Under Change Service Definition/ Srvice Definition
select method: Import a .proto file [select bank-services.proto file]
+Add an import path [src/main/proto]
Next
Import as API
Now select method: choose transfer
Click on Message
Click Use Example Message
Update message
{
    "amount": 10,
    "from_account": 2,
    "to_account": 7
}
Invoke
click on send [multiple times]
End streaming
```
### Testing-3 : BloomRPC
```
Launch BloomRPC
--> Import path: src/main/proto
--> + Import Proto: bank-service.proto file
--> Env: localhost:6565 [Unary call] 
--> clieck on transfer
Editor: Update the request then click on Play button for response.
{
  "account_number": 10,
  "amount": 30
}
--> Push Data [multiple times]
--> Commit Stream
```
