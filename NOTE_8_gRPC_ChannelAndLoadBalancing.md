# gRPC Channel & Load Balancing

### Channel
- Channel represents the persistent connection(http2)
- Channel abstraction over a connection which establishes the connection between client and server.
- It creates lazy connection during the 1st rpc call.
- 1 connection is enough for client/server communication even for concurrent requests.
- We can create more connections but not required.
- Channel creation is expensive process.
- Close it when server shout down/ app crashes /app stops /idle for some time(can be configured)
- Thread safe, can be shared with multiple stubs for the server.
    
### Lazy connection
```
Stop server
Keep logs after Channel creation, stub creation.
Start client
We will get Exception during rpc call not at channel or stub creation.
i.e Connection is created during 1st rpc call [lazy connection]
```

### Load Balancing
- We follow single responsibility principle in microservice architecture.
- We split big monolithic application into small services/applications based on business sub domain.
- Some services receive a lot of requests compare to other.
Ex: 
    Payment service receives more requests than user register service.
 
 To handle the load & high availability we run the same service in multiple instances/machines.
 Distribute the lod across all instances to balance the load.
 
### Types of Load Balancing
1. Server side Load balancing
```
The client issues RPCs to a load balancer or proxy, such as Nginx or Envoy. 
The load balancer distributes the RPC call to one of the available backend servers.
```
Pros
```
Simple client: client should know the lb/proxy address.
Works with untrusted clients
```
Cons
```
Extra hop
Higher latency
```
Use case
```
Many clients
Clients from opne internet
```

2. Client side Load balancing
```
The client is aware of multiple backend servers and chooses one to use for each RPC. 
Usually, the backend servers register themselves with a service discovery infrastructure, such as Consul or Etcd. 
Then the client communicates with that infrastructure to know the addresses of the servers.
Thick client(simple configuration, Server load in not considered, cient fwd reqs to available severs)
Look-aside LB: service discovery & some LB alogorithm.
```
Pros
```
No extra hop
High performance
```
Cons
```
Complex client(think client implementation)
Client must be trusted or needs look-aside LB for trust boundary 
```
Use case
```
Very high trafic
Microservices
```


* Learn Docker
* Learn Nginx

To work with Server side Load Balancer : Better to use Docker & Nginx or Install Nginx in windows machine
- [Nginx](https://www.nginx.com/nginx-wiki/build/dirhtml/start/topics/tutorials/install/)
- [Nginx Download](https://nginx.org/en/download.html)
- [Download](https://nginx.org/download/nginx-1.22.1.zip) and extract

```
open cmd prompt
cd nginx-1.22.1
> start nginx
> tasklist /fi "imagename eq nginx.exe"
```


Some commands
```
nginx -s stop	fast shutdown
nginx -s quit	graceful shutdown
nginx -s reload	changing configuration, starting new worker processes with a new configuration, graceful shutdown of old worker processes
nginx -s reopen	re-opening log files
```

### Changes  ```bank-server``` modules
1. Create ```in.rk.bank.server.loadbalancing``` 
2. Copy ```GrpcServer``` files into ```in.rk.bank.client.loadbalancing```
3. Rename ```GrpcServer``` to ```GrpcServerLB1``` in  ```in.rk.bank.client.loadbalancing```
4. Save as ```GrpcServerLB1``` ```GrpcServerLB2```  in  ```in.rk.bank.client.loadbalancing```
5. Update the port number ```6565``` , ```7575``` in ```GrpcServerLB1``` ```GrpcServerLB2``` 
6. Start above two servers.

### Changes in Nginx for Server side load balancer. 
1. Install Nginx and update nginx configuration file(windows)

```
worker_processes  1;

events {
    worker_connections  10;
}

http 
{
    upstream bankservers
    {
        server localhost:6565;
        server localhost:7575;
    }
    
    server 
    {
        listen 8585 http2;
    
        location / {
            grpc_pass grpc://bankservers;
        }
    }
}


```
2. start nginx
```
open cmd prompt
cd nginx-1.22.1
> start nginx
List cmd
> tasklist /fi "imagename eq nginx.exe"
Kill cmd
> taskkill /f /im nginx.exe

```

### Changes  ```bank-client``` modules

1. Create ```in.rk.bank.client.loadbalancing``` 
2. Copy ```BlockingGrpcClient, AsyncGrpcClient``` files into ```in.rk.bank.client.loadbalancing```
3. Rename ```BlockingGrpcClient``` to ```BlockingGrpcClientLB``` and update the prt number to 8585(proxy/nginx port number)

### Testing
start Nginx, Servers(1 & 2) Client


Sub Channels:
```
A channel can have many sub chaannels. Each sub channel represents a connecion to the server.
Channel chooses subchannel in round robin fashion(not default).
Pick first, round robin strategy. 
```


### Client side load balancing
```
the client is aware of multiple backend servers and chooses one to use for each RPC. 
Usually, the backend servers register themselves with a service discovery infrastructure, such as Consul or Etcd.
It is fastr than server-side load balancing.

No proxy between client & server in client side load balancing.
Client should know the all IP addresses of server instances.

How the client get to know the IP addresses of server instances?
    Required service registery (Consul or Etcd)
    The services (server instances) on startup register themself in service-registery.

How client handles multiple IP addresses  in ManagedChannel?
    The concept called subchannel will help.
    gRPC SubChannel: 
    A Channel can have many subchannels. Each sub channel represents a connection to the server.
    Channel chosses the sub channels in round robbin fashion(not default). [pick-first strategy by default]
```

```
A thick client implements the load balancing algorithms itself. 
For example, in a simple configuration, where the server load is not considered, the client can just round-robin between available servers.
```
```
a look-aside load balancer, 
where the load balancing smarts are implemented in a special load-balancing server. 
Clients query the look-aside load balancer to get the best server(s) to use. 
The heavy lifting of keeping server state, service discovery, and implementation of a load balancing 
algorithm is consolidated in the look-aside load balancer.
```
* Learn Pub & Sub model.

### Code changes in bank-client

1. Create ServiceRegistry.java 
```
package in.rk.bank.client.loadbalancing.clientside.serv_reg;

import io.grpc.EquivalentAddressGroup;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceRegistry {
    private static  final Map<String, List<EquivalentAddressGroup>> SER_REG_MAP=new HashMap<>();

    public static void register(String service, List<String> instances)
    {
        List<EquivalentAddressGroup> equivalentAddressGroups
                =instances.stream()
                .map(a->a.split(":"))
                .map(a->new InetSocketAddress(a[0],Integer.parseInt(a[1])))
                .map(EquivalentAddressGroup::new)
                .collect(Collectors.toList());
        SER_REG_MAP.put(service,equivalentAddressGroups);
    }

    public static List<EquivalentAddressGroup> getYnstance(String service)
    {
        return SER_REG_MAP.get(service);
    }
}

```
2. Create TempNameResolver.java
```
package in.rk.bank.client.loadbalancing.clientside.serv_reg;

import io.grpc.NameResolver;

public class TempNameResolver  extends NameResolver {
    private final String service;

    public TempNameResolver(String service) {
        this.service = service;
    }

    @Override
    public String getServiceAuthority() {
        return "temp";
    }

    @Override
    public void start(Listener2 listener) {
        System.out.println(this.getClass().getName()+"start.......");
        ResolutionResult resolutionResult = ResolutionResult
                .newBuilder()
                .setAddresses(ServiceRegistry.getYnstance(this.service))
                .build();
        listener.onResult(resolutionResult);
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void refresh() {
        System.out.println(this.getClass().getName()+"refresh.......");
        super.refresh();
    }
}

```
3.  Create TempNameResolverProvider.java 
```
package in.rk.bank.client.loadbalancing.clientside.serv_reg;

import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import java.net.URI;

public class TempNameResolverProvider extends NameResolverProvider
{
    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 5;
    }

    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        System.out.println("looking for service:"+targetUri.getAuthority());
        return new TempNameResolver(targetUri.getAuthority());
    }

    @Override
    public String getDefaultScheme() {
        return "grpc";//return "http";
    }
}

```
4.  Create BlockingGrpcClientSideLB.java 
```
package in.rk.bank.client.loadbalancing.clientside;

import com.google.common.util.concurrent.Uninterruptibles;
import in.rk.bank.client.loadbalancing.clientside.serv_reg.ServiceRegistry;
import in.rk.bank.client.loadbalancing.clientside.serv_reg.TempNameResolverProvider;
import in.rk.bank.models.BalanceCheckRequest;
import in.rk.bank.models.BalanceWithdrawRequest;
import in.rk.bank.models.BalanceWithdrawResponse;
import in.rk.bank.services.BankServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;
import io.grpc.StatusRuntimeException;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BlockingGrpcClientSideLB {

    public static void main(String[] args) {
        //Register
        ServiceRegistry.register("bank-service", List.of("localhost:6565","localhost:7575") );
        NameResolverRegistry.getDefaultRegistry().register(new TempNameResolverProvider());

        ManagedChannel managedChannel = ManagedChannelBuilder
                //.forAddress("localhost", 8585)
                .forTarget("grpc://bank-service")//.forTarget("http://bank-service")
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .build();

        BankServiceGrpc.BankServiceBlockingStub blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);

        //1 Unary
        System.out.println("================:Blocking stub Unary RPC :==============");
        balanceCheckUnaryForMultipleTimes(blockingStub);

        //2. Server side streaming
//        System.out.println("================:Blocking stub Server side streaming  RPC :==============");
//        BalanceWithdrawRequest balanceWithdrawRequest = BalanceWithdrawRequest.newBuilder().setAccountNumber(9).setAmount(50).build();
//        balanceWithdrawServerStreaming(blockingStub, balanceWithdrawRequest);
    }

    private static void balanceCheckUnaryForMultipleTimes(BankServiceGrpc.BankServiceBlockingStub blockingStub) {
        try {
            for(int j=0;j<=2;j++) {
                for (int i = 1; i <= 10; i++) {
                    Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
                    BalanceCheckRequest balanceRequest = BalanceCheckRequest.newBuilder().setAccountNumber(i).build();
                    System.out.println("Received Response:" + blockingStub.checkBalance(balanceRequest));
                }
            }
        } catch (StatusRuntimeException e) {
            System.err.println("Known Error while checking balance:" + e);
        } catch (Exception e) {
            System.err.println("Unknown Error while checkig balance" + e);
        }
    }

    private static void balanceWithdrawServerStreaming(BankServiceGrpc.BankServiceBlockingStub blockingStub, BalanceWithdrawRequest balanceWithdrawRequest) {
        BalanceWithdrawResponse resp = null;
        BalanceWithdrawResponse finalResp = BalanceWithdrawResponse.newBuilder().setAmount(0).build();

        try {
            //1.
            //blockingStub.withdraw(balanceWithdrawRequest).forEachRemaining(eachResp->System.out.println(eachResp));

            //2.
            Iterator<BalanceWithdrawResponse> itr = blockingStub.withdraw(balanceWithdrawRequest);
            while (itr.hasNext()) {
                resp = itr.next();
                System.out.println("Streaming Response:" + resp);
                finalResp = finalResp.toBuilder().setAmount(finalResp.toBuilder().getAmount() + resp.getAmount()).build();
            }
            System.out.println("Final Response:" + finalResp);
        } catch (StatusRuntimeException e) {
            System.err.println("Known Error which withdraw:" + e);
        } catch (Exception e) {
            System.err.println("Unknown Error which withdraw:" + e);
        }
    }


}

```
5.  Create AsyncGrpcClientSideLB.java
```
package in.rk.bank.client.loadbalancing.clientside;

import com.google.common.util.concurrent.Uninterruptibles;
import in.rk.bank.client.loadbalancing.clientside.serv_reg.ServiceRegistry;
import in.rk.bank.client.loadbalancing.clientside.serv_reg.TempNameResolverProvider;
import in.rk.bank.client.streamobservers.BalanceDepositResponseStreamObserver;
import in.rk.bank.client.streamobservers.BalanceWithdrawResponseStreamObserver;
import in.rk.bank.client.streamobservers.TransferResponseStreamObserver;
import in.rk.bank.models.BalanceDepositRequest;
import in.rk.bank.models.BalanceWithdrawRequest;
import in.rk.bank.models.TransferRequest;
import in.rk.bank.services.BankServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class AsyncGrpcClientSideLB {

    public static void main(String[] args) {
        //Register
        ServiceRegistry.register("bank-service", List.of("localhost:6565","localhost:7575") );
        NameResolverRegistry.getDefaultRegistry().register(new TempNameResolverProvider());

        ManagedChannel managedChannel= ManagedChannelBuilder
                //.forAddress("localhost",8585)
                .forTarget("grpc://bank-service")//.forTarget("http://bank-service")
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .build();
        BankServiceGrpc.BankServiceStub asyncStub=BankServiceGrpc.newStub(managedChannel);

        //1. Async withdraw through :BankServiceGrpc.BankServiceStub
        System.out.println("==================Async Server streaming RPC============");
        BalanceWithdrawRequest req=BalanceWithdrawRequest.newBuilder().setAccountNumber(9).setAmount(50).build();
        balanceWithdrawServerStreamingAsync(asyncStub, req);

        //2. Async deposit through :BankServiceGrpc.BankServiceStub
        System.out.println("==================Async Client streaming RPC============");
        BalanceDepositRequest depositReq= BalanceDepositRequest.newBuilder().setAccountNumber(7).setAmount(50).build();
        depositAsyncClientStreaming(asyncStub, depositReq);

        //4. Async transfer through :BankServiceGrpc.BankServiceStub
        System.out.println("==================Async Bidirectional streaming RPC============");
        transferAsyncBiDirectional(asyncStub);
    }

    private static void transferAsyncBiDirectional(BankServiceGrpc.BankServiceStub asyncStub) {
        CountDownLatch latch=new CountDownLatch(1);
        TransferResponseStreamObserver transferResponseStreamObserver=new TransferResponseStreamObserver(latch);
        StreamObserver<TransferRequest> transferRequestStreamObserver=asyncStub.transfer(transferResponseStreamObserver);

        for(int i=1; i<=5;i++)
        {
            TransferRequest eachReq= TransferRequest.newBuilder()
                    .setFromAccount(ThreadLocalRandom.current().nextInt(1,11))
                    .setToAccount(ThreadLocalRandom.current().nextInt(1,11))
                    .setAmount(5)
                    .build();
            transferRequestStreamObserver.onNext(eachReq);
        }
        transferRequestStreamObserver.onCompleted();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static void depositAsyncClientStreaming(BankServiceGrpc.BankServiceStub asyncStub, BalanceDepositRequest depositReq) {

        CountDownLatch latch =new CountDownLatch(1);
        StreamObserver<BalanceDepositRequest> depositReqObserver = asyncStub.deposit(new BalanceDepositResponseStreamObserver(latch));
        for(int i=1;i<=5;i++)
        {
            System.out.println("Each stream of request:"+depositReq);
            Uninterruptibles.sleepUninterruptibly(3,TimeUnit.SECONDS);
            depositReqObserver.onNext(depositReq);
        }
        depositReqObserver.onCompleted();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void balanceWithdrawServerStreamingAsync(BankServiceGrpc.BankServiceStub asyncStub, BalanceWithdrawRequest req) {
        CountDownLatch latch =new CountDownLatch(1);
        asyncStub.withdraw(req,new BalanceWithdrawResponseStreamObserver(latch));
        //Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        try
        {
            latch.await();
        }catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}

``` 
