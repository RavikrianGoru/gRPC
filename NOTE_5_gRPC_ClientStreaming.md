# gRPC Client Streaming
Client sends multiple requests and server sends single response back.
Client do multiple deposits requests then server will send final balance as response.

```
i) Create balance-deposit-request.proto, balance-deposit-response.proto files and update bank-service.proto with new rpc.
```

### 1. Update bank-proto module
1. Create ```balance-deposit-request.proto``` file under src/main/```proto```/```models```
```
syntax="proto3";

option java_multiple_files=true;
option java_package="in.rk.bank.models";

package models;

message BalanceDepositRequest
{
  int32 account_number=1;
  int32 amount=2;
}
```
2. Create ```balance-deposit-response.proto``` file under src/main/```proto```/```models```
```
syntax="proto3";

option java_multiple_files=true;
option java_package="in.rk.bank.models";

package models;

message BalanceDepositResponse
{
  int32 amount=1;
}
```
3. Update ```bank-services.proto``` with imports and rpc call for client side streaming
```
syntax="proto3";

option java_multiple_files=true;
option java_package="in.rk.bank.services";

package services;
import "models/balance-check-request.proto";
import "models/balance-check-response.proto";
import "models/balance-withdraw-request.proto";
import "models/balance-withdraw-response.proto";
import "models/balance-deposit-request.proto";
import "models/balance-deposit-response.proto";


service BankService
{
  //1. Unary
  rpc checkBalance(models.BalanceCheckRequest) returns (models.BalanceCheckResponse);

  //2. Server side streaming
  rpc withdraw (models.BalanceWithdrawRequest) returns (stream models.BalanceWithdrawResponse);

  //3. Client side streaming
  rpc deposit(stream models.BalanceDepositRequest) returns (models.BalanceDepositResponse);
}
```
4. Run ```mvn clean install``` then check generated files in ```target/generated-sources/protobuf/*``` 

### 2. Update bank-service module
1. Add/override deposit(-) in BankService.java
```
i) StreamObserver<BalanceDepositRequest> deposit(StreamObserver<BalanceDepositResponse> responseObserver) 
   Here, return type is StreamObserver<BalanceDepositRequest>
ii) We have to create a class BalanceDepositRequestStreamObserver which implements StreamObserver<BalanceDepositRequest>
   and override required methods.
   add below instance variables:
    StreamObserver<BalanceDepositResponse> depositResponseStreamObserver;
    private int accountBalance;
```
BalanceDepositRequestStreamObserver.java
```
package in.rk.bank.streamobserver;

import in.rk.bank.db.AccountDB;
import in.rk.bank.models.BalanceDepositRequest;
import in.rk.bank.models.BalanceDepositResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class BalanceDepositRequestStreamObserver implements StreamObserver<BalanceDepositRequest> {
    StreamObserver<BalanceDepositResponse> depositResponseStreamObserver;
    private int accountBalance;

    public BalanceDepositRequestStreamObserver(StreamObserver<BalanceDepositResponse> depositResponseStreamObserver) {
        this.depositResponseStreamObserver = depositResponseStreamObserver;
    }

    @Override
    public void onNext(BalanceDepositRequest req) {
        System.out.println("Each Request:"+req);
        int accountNbr=req.getAccountNumber();
        int amount=req.getAmount();
        if(AccountDB.isAccountAvailable(accountNbr))
        {
            this.accountBalance=AccountDB.addBalance(accountNbr,amount);
        }else
        {
            Status status=Status.FAILED_PRECONDITION.withDescription("Invalid account");
            this.depositResponseStreamObserver.onError(status.asRuntimeException());
            return;
        }

    }

    @Override
    public void onError(Throwable t) {
        System.err.println("Error:"+t.getMessage());
    }

    @Override
    public void onCompleted() {
        BalanceDepositResponse resp=BalanceDepositResponse.newBuilder().setAmount(this.accountBalance).build();
        this.depositResponseStreamObserver.onNext(resp);
        this.depositResponseStreamObserver.onCompleted();
    }
}
```
2. Update deposit(-) method in BankServie class.
```
    @Override
    public StreamObserver<BalanceDepositRequest> deposit(StreamObserver<BalanceDepositResponse> responseObserver) {
        return new BalanceDepositRequestStreamObserver(responseObserver);
    }

``` 
3. Run ```mvn clean install```

### 3. No Changes in bank-server module
1. Run ```mvn clean install```

### 4. Update bank-client module
```
* We can only access deposit(-) method through BankServiceStub (async stub). we can't access through BankServiceBlockingStub (sync stub).
* Required to create a class BalanceDepositResponseStreamObserver which implements StreamObserver<BalanceDepositResponse>.
```
1) Create BalanceDepositResponseStreamObserver class and implement StreamObserver<BalanceDepositResponse>.
```
package in.rk.bank.client.streamobservers;

import in.rk.bank.models.BalanceDepositResponse;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class BalanceDepositResponseStreamObserver implements StreamObserver<BalanceDepositResponse> {
    CountDownLatch latch;

    public BalanceDepositResponseStreamObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(BalanceDepositResponse resp) {
        System.out.println("Final Response:"+resp);
    }

    @Override
    public void onError(Throwable t) {
        System.err.println("Error:"+t.getMessage());
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("All request are process and shared final response!");
        latch.countDown();
    }
}
```
2. Create a method definition to call deposit(-) through client streaming.

```
    private static void depositAsyncClientStreaming(BankServiceGrpc.BankServiceStub asyncStub, BalanceDepositRequest depositReq) {

        CountDownLatch latch =new CountDownLatch(1);
        StreamObserver<BalanceDepositRequest> depositReqObserver = asyncStub.deposit(new BalanceDepositResponseStreamObserver(latch));
        for(int i=1;i<=5;i++)
        {
            System.out.println("Each stream of request:"+depositReq);
            Uninterruptibles.sleepUninterruptibly(3,TimeUnit.SECONDS);
            depositReqObserver.onNext(depositReq);
        }
        depositReqObserver.onCompleted();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

```
3. Methd call in main(-)
```
    //2. Async deposit through :BankServiceGrpc.BankServiceStub
    BalanceDepositRequest depositReq= BalanceDepositRequest.newBuilder().setAccountNumber(7).setAmount(50).build();
    depositAsyncClientStreaming(asyncStub, depositReq);

```
4. Run ```mvn clean install```

### Testing-1 : bank-client
1. start Server:  bank-server--> GrpcServer:main(-)
2. Send request from Client: bank-client-->AsyncGrpcClient:main(-)--depositAsyncClientStreaming(-,-)

If server is up
```
Each stream of request:account_number: 7
amount: 50
Each stream of request:account_number: 7
amount: 50
Each stream of request:account_number: 7
amount: 50
Each stream of request:account_number: 7
amount: 50
Each stream of request:account_number: 7
amount: 50

Final Response:amount: 320

All request are process and shared final response!
```
If server is down
```
Known Error which withdraw:io.grpc.StatusRuntimeException: UNAVAILABLE: io exception
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
Now select method: choose deposit
Click on Message
Click Use Example Message
Update message
{
    "account_number": 6,
    "amount": 20
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
--> clieck on deposit
Editor: Update the request then click on Play button for response.
{
  "account_number": 10,
  "amount": 30
}
--> Push Data [multiple times]
--> Commit Stream
```
