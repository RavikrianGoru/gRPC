package in.rk.bank.server.interceptor.context;

import in.rk.bank.service.interceptor.context.BankServiceContext;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServerContext {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server grpcServer = ServerBuilder.forPort(6565)
                                         .intercept(new RoleInterceptor())
                                         .addService(new BankServiceContext())
                                         .build();
        grpcServer.start();
        System.out.println("Server is started with BankServiceMetadata!");
        grpcServer.awaitTermination();
    }
}
