package in.rk.bank.server.executors;

import in.rk.bank.service.BankService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServerDirectExecutor {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server grpcServer = ServerBuilder.forPort(6565)
                                         .directExecutor()
                                         .addService(new BankService())
                                         .build();
        grpcServer.start();
        System.out.println("Server is started with BankService!");

        //shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("gRPC server is shutting down!");
            grpcServer.shutdown();
        }));

        grpcServer.awaitTermination();
    }
}
