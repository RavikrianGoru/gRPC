package in.rk.bank.client.loadbalancing.serverside;

import com.google.common.util.concurrent.Uninterruptibles;
import in.rk.bank.models.BalanceCheckRequest;
import in.rk.bank.models.BalanceWithdrawRequest;
import in.rk.bank.models.BalanceWithdrawResponse;
import in.rk.bank.services.BankServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class BlockingGrpcClientLB {

    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 8585)
                .usePlaintext()
                .build();

        BankServiceGrpc.BankServiceBlockingStub blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);

        //1 Unary
        System.out.println("================:Blocking stub Unary RPC :==============");
        balanceCheckUnaryForMultipleTimes(blockingStub);

        //2. Server side streaming
//        System.out.println("================:Blocking stub Server side streaming  RPC :==============");
//        BalanceWithdrawRequest balanceWithdrawRequest = BalanceWithdrawRequest.newBuilder().setAccountNumber(9).setAmount(50).build();
//        balanceWithdrawServerStreaming(blockingStub, balanceWithdrawRequest);
    }

    private static void balanceCheckUnaryForMultipleTimes(BankServiceGrpc.BankServiceBlockingStub blockingStub) {
        try {
            for(int j=0;j<=2;j++) {
                for (int i = 1; i <= 10; i++) {
                    Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
                    BalanceCheckRequest balanceRequest = BalanceCheckRequest.newBuilder().setAccountNumber(i).build();
                    System.out.println("Received Response:" + blockingStub.checkBalance(balanceRequest));
                }
            }
        } catch (StatusRuntimeException e) {
            System.err.println("Known Error while checking balance:" + e);
        } catch (Exception e) {
            System.err.println("Unknown Error while checkig balance" + e);
        }
    }

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


}
