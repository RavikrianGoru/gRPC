package in.rk.bank.server.deadline;

import in.rk.bank.service.BankService;
import in.rk.bank.service.deadline.BankServiceDeadline;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServerDeadline {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server grpcServer = ServerBuilder.forPort(6565)
                                         .addService(new BankServiceDeadline())
                                         .build();
        grpcServer.start();
        System.out.println("Server is started with BankServiceDeadline!");
        grpcServer.awaitTermination();
    }
}
