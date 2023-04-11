package in.rk.bank.client.deadline;

import com.google.common.util.concurrent.Uninterruptibles;
import in.rk.bank.client.streamobservers.BalanceDepositResponseStreamObserver;
import in.rk.bank.client.streamobservers.BalanceWithdrawResponseStreamObserver;
import in.rk.bank.client.streamobservers.TransferResponseStreamObserver;
import in.rk.bank.models.BalanceDepositRequest;
import in.rk.bank.models.BalanceWithdrawRequest;
import in.rk.bank.models.TransferRequest;
import in.rk.bank.services.BankServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class AsyncGrpcClientDeadline {

    public static void main(String[] args) {
        ManagedChannel managedChannel= ManagedChannelBuilder
                .forAddress("localhost",6565)
                .usePlaintext()
                .build();
        BankServiceGrpc.BankServiceStub asyncStub=BankServiceGrpc.newStub(managedChannel);

        //1. Async withdraw through :BankServiceGrpc.BankServiceStub
        System.out.println("==================Async Server streaming RPC============");
        BalanceWithdrawRequest req=BalanceWithdrawRequest.newBuilder().setAccountNumber(9).setAmount(50).build();
        balanceWithdrawServerStreamingAsync(asyncStub, req);

        //2. Async deposit through :BankServiceGrpc.BankServiceStub
        System.out.println("==================Async Client streaming RPC============");
        BalanceDepositRequest depositReq= BalanceDepositRequest.newBuilder().setAccountNumber(7).setAmount(50).build();
        depositAsyncClientStreaming(asyncStub, depositReq);

        //4. Async transfer through :BankServiceGrpc.BankServiceStub
        System.out.println("==================Async Bidirectional streaming RPC============");
        transferAsyncBiDirectional(asyncStub);
    }

    private static void transferAsyncBiDirectional(BankServiceGrpc.BankServiceStub asyncStub) {
        CountDownLatch latch=new CountDownLatch(1);
        TransferResponseStreamObserver transferResponseStreamObserver=new TransferResponseStreamObserver(latch);
        StreamObserver<TransferRequest> transferRequestStreamObserver=
                asyncStub.withDeadlineAfter(10,TimeUnit.SECONDS)
                        .transfer(transferResponseStreamObserver);

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

    private static void depositAsyncClientStreaming(BankServiceGrpc.BankServiceStub asyncStub, BalanceDepositRequest depositReq) {

        CountDownLatch latch =new CountDownLatch(1);
        StreamObserver<BalanceDepositRequest> depositReqObserver =
                asyncStub.withDeadlineAfter(10,TimeUnit.SECONDS)
                        .deposit(new BalanceDepositResponseStreamObserver(latch));
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

    private static void balanceWithdrawServerStreamingAsync(BankServiceGrpc.BankServiceStub asyncStub, BalanceWithdrawRequest req) {
        CountDownLatch latch =new CountDownLatch(1);
        asyncStub.withDeadlineAfter(3,TimeUnit.SECONDS)
                .withdraw(req,new BalanceWithdrawResponseStreamObserver(latch));
        try
        {
            latch.await();
        }catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
