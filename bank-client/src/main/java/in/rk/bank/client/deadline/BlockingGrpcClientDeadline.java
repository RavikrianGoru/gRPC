package in.rk.bank.client.deadline;

import in.rk.bank.models.BalanceCheckRequest;
import in.rk.bank.models.BalanceWithdrawRequest;
import in.rk.bank.models.BalanceWithdrawResponse;
import in.rk.bank.services.BankServiceGrpc;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class BlockingGrpcClientDeadline {

    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        BankServiceGrpc.BankServiceBlockingStub blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
        //blockingStub=blockingStub.withDeadline(Deadline.after(3, TimeUnit.SECONDS));
        //blockingStub=blockingStub.withDeadlineAfter(3,TimeUnit.SECONDS);

        //1 Unary
        System.out.println("================:Blocking stub Unary RPC :==============");
        BalanceCheckRequest balanceRequest = BalanceCheckRequest.newBuilder().setAccountNumber(8).build();
        balanceCheckUnary(blockingStub, balanceRequest);

        //2. Server side streaming
        System.out.println("================:Blocking stub Server side streaming  RPC :==============");
        BalanceWithdrawRequest balanceWithdrawRequest = BalanceWithdrawRequest.newBuilder().setAccountNumber(8).setAmount(50).build();
        balanceWithdrawServerStreaming(blockingStub, balanceWithdrawRequest);
    }

    private static void balanceCheckUnary(BankServiceGrpc.BankServiceBlockingStub blockingStub, BalanceCheckRequest balanceRequest) {
        try {
            System.out.println("Received Response:" +
                    blockingStub.withDeadline(Deadline.after(3, TimeUnit.SECONDS))
                                .checkBalance(balanceRequest));
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
            Iterator<BalanceWithdrawResponse> itr = blockingStub
                    .withDeadline(Deadline.after(10,TimeUnit.SECONDS))
                    .withdraw(balanceWithdrawRequest);
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
