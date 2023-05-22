package in.rk.bank.client.ssl;

import in.rk.bank.models.BalanceCheckRequest;
import in.rk.bank.models.BalanceWithdrawRequest;
import in.rk.bank.models.BalanceWithdrawResponse;
import in.rk.bank.services.BankServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.Iterator;

public class BlockingGrpcClientSSL {

    public static void main(String[] args) throws SSLException {
        SslContext sslContext= GrpcSslContexts.forClient()
                .trustManager(new File("D:\\codebase\\git\\gRPC\\ssl-tls\\ca.cert.pem"))
                .build();

        ManagedChannel managedChannel = NettyChannelBuilder
                .forAddress("localhost", 6565)
                .sslContext(sslContext)
                //.usePlaintext()
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
            System.out.println("Received Response:" + blockingStub.checkBalance(balanceRequest));
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
