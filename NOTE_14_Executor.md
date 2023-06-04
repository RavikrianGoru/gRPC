### Executor

gRPC thread pool and executor


```
Netty Server has two types of thread pools/groups.
1) Master/boss thread group/pool: accept incoming rpc by listening the target socket and do ssl handshake.
2) Worker/cached thread pool/group: read the rpc request and assign the task to threads. i.e cachedThreadPool.
```
DirectExecutor
```
Worker threads, they accept rpcs themself and process the request. Good for non-blocking rpcs.
Blocking Rpcs: if service communicates the DB and DB acces takes time then it blocks the RPC and it causes performace dip.
```
```
ServerBuilder.forPort(6565)
             .directExecutor()
             .addService(new SomeService())
             .build()
```

CachedThreadPool
```
Does the actual service execution. 
cachedTreadPool checks if all threads are busy 
then it creates a new thread else use the one of the threads.
Configuration : cachedThreadPool(Default configuration)
```
```
ServerBuilder.forPort(6565)
             .addService(new SomeService())
             .build();
```

FixedThreadPool
```
ServerBuilder.forPort(6565)
             .executor(Executors.newFixedThreadPoll(20))
             .addService(new SomeService())
             .build()
```             
Changes in bank-server:
```
1) create executor package under server
2) Copy and rename the GrpcServer to GrpcServerDirectExecutor.java
3) 
   Server grpcServer = ServerBuilder.forPort(6565)
                                    .directExecutor()
                                    .addService(new BankService())
                                    .build();

4) Copy and rename the GrpcServer to GrpcServerCachedThreadPool.java
5) 
   Server grpcServer = ServerBuilder.forPort(6565)
                                    .executor(Executors.newCachedThreadPool())
                                    .addService(new BankService())
                                    .build();
6) Copy and rename the GrpcServer to GrpcServerFixedThreadPool.java
7) 
   Server grpcServer = ServerBuilder.forPort(6565)
                                    .executor(Executors.newFixedThreadPool(20))
                                    .addService(new BankService())
                                    .build();

```