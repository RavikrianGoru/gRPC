# gRPC Server Streaming

### gRPC project Setup ( IntelliJ )
1) bank-proto module: proto messages & grpc services
2) bank-service module: service implementation
3) bank-server module: server which uses service/services.
4) bank-client module: Client implementation
- Postman client or BloomRPC client can be used for testing.

### 1. Update bank-proto module
1. Create ```balance-withdraw-request.proto``` file under src/main/```proto```/```models```
```
syntax="proto3";

option java_multiple_files=true;
option java_package="in.rk.bank.models";

package models;

message BalanceWithdrawRequest
{
  int32 account_number=1;
  int32 amount=2;
}
```
2. Create ```balance-withdraw-response.proto``` file under src/main/```proto```/```models```
```
syntax="proto3";

option java_multiple_files=true;
option java_package="in.rk.bank.models";

package models;

message BalanceWithdrawResponse
{
  int32 amount=1;
}
```
3. Update ```bank-services.proto``` with imports and rpc call for server side streaming
```
syntax="proto3";

option java_multiple_files=true;
option java_package="in.rk.bank.services";

package services;
import "models/balance-check-request.proto";
import "models/balance-check-response.proto";


service BankService
{
  rpc checkBalance(models.BalanceCheckRequest) returns (models.BalanceCheckResponse);
syntax="proto3";

option java_multiple_files=true;
option java_package="in.rk.bank.services";

package services;
import "models/balance-check-request.proto";
import "models/balance-check-response.proto";
import "models/balance-withdraw-request.proto";
import "models/balance-withdraw-response.proto";


service BankService
{
  //1. Unary
  rpc checkBalance(models.BalanceCheckRequest) returns (models.BalanceCheckResponse);

  //2. Server side streaming
  rpc withdraw (models.BalanceWithdrawRequest) returns (stream models.BalanceWithdrawResponse);
}
```
4. Run ```mvn clean install``` then check generated files in ```target/generated-sources/protobuf/*``` 

### 2. Update bank-service module
1. Update ```AccountDB.java``` by adding isRequestedAmountAvailable(-)
```
    public static boolean isRequestedAmountAvailable(int accountNbr, int amount)
    {
        boolean results=false;
        if(isAccountAvailable(accountNbr))
        {
            if(getBalance(accountNbr)>=amount)
                results=true;
        }
        return results;
    }
```
2. Add/override withdraw(-) in BankService.java
```
    @Override
    public void withdraw(BalanceWithdrawRequest request, StreamObserver<BalanceWithdrawResponse> responseObserver) {
        int accountNbr = request.getAccountNumber();
        int amount = request.getAmount();
        int finalBalance = AccountDB.getBalance(accountNbr);

        if (AccountDB.isAccountAvailable(accountNbr) && AccountDB.isRequestedAmountAvailable(accountNbr, amount)) {
            for (int i = 0; i < (amount / 10); i++) {
                BalanceWithdrawResponse balanceWithdrawResponse = BalanceWithdrawResponse.newBuilder()
                        .setAmount(10)
                        .build();
                AccountDB.deductBalance(accountNbr, 10);
                responseObserver.onNext(balanceWithdrawResponse);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            responseObserver.onCompleted();
        } else {
            Status status = Status.FAILED_PRECONDITION.withDescription("Insufficient Funds:" + finalBalance);
            responseObserver.onError(status.asRuntimeException());
            return;
        }

    }
```
3. Run ```mvn clean install```

### 3. No Changes in bank-server module
1. Run ```mvn clean install```

### 4. Update bank-client module
1. Update BlockingGrpcClient.java to interact with Server. 
```
        //2. Server side streaming
        BalanceWithdrawRequest balanceWithdrawRequest = BalanceWithdrawRequest.newBuilder().setAccountNumber(7).setAmount(50).build();
        balanceWithdrawServerStreaming(blockingStub, balanceWithdrawRequest);
```

```
    private static void balanceWithdrawServerStreaming(BankServiceGrpc.BankServiceBlockingStub blockingStub, BalanceWithdrawRequest balanceWithdrawRequest) {
        BalanceWithdrawResponse resp = null;
        BalanceWithdrawResponse finalResp = BalanceWithdrawResponse.newBuilder().setAmount(0).build();

        try {
            //1.
            //blockingStub.withdraw(balanceWithdrawRequest).forEachRemaining(eachResp->System.out.println(eachResp));

            //2.
            Iterator<BalanceWithdrawResponse> itr = blockingStub.withdraw(balanceWithdrawRequest);
            while (itr.hasNext()) {
                resp = itr.next();
                System.out.println("Streaming Response:" + resp);
                finalResp = finalResp.toBuilder().setAmount(finalResp.toBuilder().getAmount() + resp.getAmount()).build();
            }
            System.out.println("Final Response:" + finalResp);
        } catch (StatusRuntimeException e) {
            System.err.println("Known Error which withdraw:" + e);
        } catch (Exception e) {
            System.err.println("Unknown Error which withdraw:" + e);
        }
    }
```
2. Run ```mvn clean install```

### Testing-1 : bank-client
1. start Server:  bank-server--> GrpcServer:main(-)
2. Send request from Client: bank-client-->BlockingGrpcClient:main(-)--balanceCheckUnary(-,-)

If server is up
```
Streaming Response:amount: 10
Streaming Response:amount: 10
Streaming Response:amount: 10
Streaming Response:amount: 10
Streaming Response:amount: 10

Final Response:amount: 50
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
Now select method: choose withdraw
Click on Message
Click Use Example Message
Update message
{
    "account_number": 7,
    "amount": 50
}
Invoke
```
### Testing-3 : BloomRPC
```
Launch BloomRPC
--> Import path: src/main/proto
--> + Import Proto: bank-service.proto file
--> Env: localhost:6565 [Unary call] 
Editor: Update the request then click on Play button for response.
{
  "account_number": 10,
  "amount": 50
}
```

