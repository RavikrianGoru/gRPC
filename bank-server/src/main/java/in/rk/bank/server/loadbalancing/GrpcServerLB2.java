package in.rk.bank.server.loadbalancing;

import in.rk.bank.service.BankService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServerLB2 {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server grpcServer = ServerBuilder.forPort(7575)
                                         .addService(new BankService())
                                         .build();
        grpcServer.start();
        System.out.println("Server is started with BankService!");
        grpcServer.awaitTermination();
    }
}
