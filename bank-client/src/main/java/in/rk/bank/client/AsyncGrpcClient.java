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
