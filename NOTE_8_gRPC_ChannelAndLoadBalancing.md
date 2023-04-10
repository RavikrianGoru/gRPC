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
