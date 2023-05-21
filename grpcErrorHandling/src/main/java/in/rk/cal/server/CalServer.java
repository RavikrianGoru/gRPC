package in.rk.cal.server;

import in.rk.cal.service.CalService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server grpcServer = ServerBuilder.forPort(6565)
                .addService(new CalService())
                .build();
        //start
        grpcServer.start();

        //shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("gRPC server is shutting down!");
            grpcServer.shutdown();
        }));

        System.out.println("Server is started with BankService!");
        grpcServer.awaitTermination();
    }
}
