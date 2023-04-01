# gRPC Server Streaming(Non-Blocking Client or Asynchronous Client)

### 1. Update bank-client module
```
i) Create a child class of StreamObserver<ResponseObjecct> i.e: BalanceWithdrawResponseStreamObserver
   which implements StreamObserver<BalanceWithdrawResponse> & override methods.
ii) Create a client class ```AsyncGrpcClient``` which has non-blocking stub as instance variable.
```

1. Create BalanceWithdrawResponseStreamObserver by implementing StreamObserver<BalanceWithdrawResponse>
```
package in.rk.bank.client.streamobservers;

import in.rk.bank.models.BalanceWithdrawResponse;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class BalanceWithdrawResponseStreamObserver implements StreamObserver<BalanceWithdrawResponse> {
    CountDownLatch latch;

    public BalanceWithdrawResponseStreamObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(BalanceWithdrawResponse resp) {
        System.out.println("Async streaming response:"+resp);
    }

    @Override
    public void onError(Throwable t) {
        System.err.println("Error: "+t.getMessage());
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("Server is Done with final response");
        latch.countDown();
    }
}

```
2. Create a client class ```AsyncGrpcClient``` which has non-blocking stub as instance variable.
```
package in.rk.bank.client;

import com.google.common.util.concurrent.Uninterruptibles;
import in.rk.bank.client.streamobservers.BalanceWithdrawResponseStreamObserver;
import in.rk.bank.models.BalanceWithdrawRequest;
import in.rk.bank.service.BankService;
import in.rk.bank.services.BankServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AsyncGrpcClient {

    public static void main(String[] args) {
        ManagedChannel managedChannel= ManagedChannelBuilder
                .forAddress("localhost",6565)
                .usePlaintext()
                .build();
        BankServiceGrpc.BankServiceStub asyncStub=BankServiceGrpc.newStub(managedChannel);


        //1. Async withdraw through :BankServiceGrpc.BankServiceStub
        BalanceWithdrawRequest req=BalanceWithdrawRequest.newBuilder().setAccountNumber(8).setAmount(50).build();
        balanceWithdrawServerStreamingAsync(asyncStub, req);
    }

    private static void balanceWithdrawServerStreamingAsync(BankServiceGrpc.BankServiceStub asyncStub, BalanceWithdrawRequest req) {
        CountDownLatch latch =new CountDownLatch(1);
        asyncStub.withdraw(req,new BalanceWithdrawResponseStreamObserver(latch));
        //Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        try
        {
            latch.await();
        }catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
```
3. Run ```mvn clean install```

### Testing-1 : bank-client
1. start Server:  bank-server--> GrpcServer:main(-)
2. Send request from Client: bank-client-->AsyncGrpcClient:main(-)--balanceWithdrawServerStreamingAsync(-,-)

If server is up
```
Async streaming response:amount: 10
Async streaming response:amount: 10
Async streaming response:amount: 10
Async streaming response:amount: 10
Async streaming response:amount: 10
Server is Done with final response

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
### Unary vs Server Streaming
Ref: bank-service module
```
1. Unary: BankService.checkBalance(BalanceCheckRequest request, StreamObserver<BalanceCheckResponse> responseObserver)
2. withdraw(BalanceWithdrawRequest request, StreamObserver<BalanceWithdrawResponse> responseObserver)
* The above two methods have same signature & 1st method has onNext(-) call only once 2nd method has multile times.
* If we place onNext(-) call mulitple times in 1st method and then server throws Exception as it expects only ne response back. 
```