package in.rk.bank.client;

import in.rk.bank.models.BalanceCheckRequest;
import in.rk.bank.services.BankServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class BlockingGrpcClient {

    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        BankServiceGrpc.BankServiceBlockingStub blockingStub =BankServiceGrpc.newBlockingStub(managedChannel);

        //1 Unary
        BalanceCheckRequest balanceRequest= BalanceCheckRequest.newBuilder().setAccountNumber(7).build();
        balanceCheckUnary(blockingStub,balanceRequest);

    }

    private static void balanceCheckUnary(BankServiceGrpc.BankServiceBlockingStub blockingStub, BalanceCheckRequest balanceRequest) {
        try {
            System.out.println("Received Response:" + blockingStub.checkBalance(balanceRequest));
        }catch (StatusRuntimeException e)
        {
            System.err.println("Known Error while checking balance:"+e);
        }catch (Exception e)
        {
            System.err.println("Unknown Error while checkig balance"+e);
        }

    }

}
