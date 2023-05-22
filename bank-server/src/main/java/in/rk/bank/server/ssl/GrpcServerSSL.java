package in.rk.bank.server.ssl;

import in.rk.bank.service.BankService;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;

import java.io.File;
import java.io.IOException;

public class GrpcServerSSL {
    public static void main(String[] args) throws IOException, InterruptedException {
        SslContext sslContext=
                GrpcSslContexts.configure(
                        SslContextBuilder.forServer(
                                new File("D:\\codebase\\git\\gRPC\\ssl-tls\\localhost.crt"),
                                new File("D:\\codebase\\git\\gRPC\\ssl-tls\\localhost.pem")
                        )
                ).build();

                Server grpcServer = NettyServerBuilder.forPort(6565)
                                         .sslContext(sslContext)
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
