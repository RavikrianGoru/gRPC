package in.rk.bank.server.executors;

import in.rk.bank.service.BankService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.Executors;

public class GrpcServerCachedThreadPool {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server grpcServer = ServerBuilder.forPort(6565)
                                         .executor(Executors.newCachedThreadPool())
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
