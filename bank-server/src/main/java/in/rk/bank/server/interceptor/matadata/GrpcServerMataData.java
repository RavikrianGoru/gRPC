package in.rk.bank.server.interceptor.matadata;

import in.rk.bank.service.deadline.BankServiceDeadline;
import in.rk.bank.service.interceptor.metadata.BankServiceMetadata;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServerMataData {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server grpcServer = ServerBuilder.forPort(6565)
                                         .intercept(new AuthInterceptor())
                                         .addService(new BankServiceMetadata())
                                         .build();
        grpcServer.start();
        System.out.println("Server is started with BankServiceMetadata!");

        //shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("gRPC server is shutting down!");
            grpcServer.shutdown();
        }));

        grpcServer.awaitTermination();
    }
}
