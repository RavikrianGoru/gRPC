### Secure gRPC call through ssl

1. Changes in bank-server
2. Create package ```ssl```
3. Copy GrpcServer into ssl.GrpcServerSSL(renamed)
4. Update the GrpcServerSSL.java as below then start server GrpcServerSSL
(Server started in secured communication ssl enabled.)
```
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
```
5. Changes in bank-client
6. Create package ```ssl```
7. Copy BlockingGrpcClient & AsyncGrpcClient then rename to BlockingGrpcClientSSL & AsyncGrpcClientSSL.
8. Case-1: No change in client code just start BlockingGrpcClientSSL
```StatusRuntimeException: UNAVAILABLE: Network closed for unknown reason```
9. Case-2: comment .usePlaintext() as below then  start BlockingGrpcClientSSL
```
 ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 6565)
                //.usePlaintext()
                .build();
``` 
```
Known Error while checking balance:io.grpc.StatusRuntimeException: UNAVAILABLE: io exception
Channel Pipeline: [SslHandler#0, ProtocolNegotiators$ClientTlsHandler#0, WriteBufferingAndExceptionHandler#0, DefaultChannelPipeline$TailContext#0]

```
10. Case-3: Update the client code as below
```
SslContext sslContext= GrpcSslContexts.forClient()
                .trustManager(new File("D:\\codebase\\git\\gRPC\\ssl-tls\\ca.cert.pem"))
                .build();

        ManagedChannel managedChannel = NettyChannelBuilder
                .forAddress("localhost", 6565)
                .sslContext(sslContext)
                //.usePlaintext()
                .build();
```