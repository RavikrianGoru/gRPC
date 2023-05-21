### gRPC Interceptor
```
Concerns:
1. Core/Functional concerns: Business concerns
2. Cross-cutting concerns: logging, audit, security. rate limit....etc
We have mixed business and cross-cutting (deadline) login in same class. 
We can avoid by using interceptor. 

Global deadline: using interceptor concept
gRPC incerceptors can be applied at client & server side.

We can overide gloabl timeout (deadline) with 
```

### Interceptor with key/value data passing.
```
    CallOptions: Pass some information from client to client interceptor
    Metadata: Pass some information from client/client interceptor to service/server interceptor
    Context: Pass some information from service/server interceptor to Server business 
```


### Case-1 CallOption:Pass some information from client to client interceptor
```
Changes in bank-client module
New package "interceptor.deadline"
New class DeadlineInterceptor
Copy past BlockingGrpcClientDeadline, AsyncGrpcClientDeadline into interceptor.deadline package
Remove deadline stuff from AsyncGrpcClientDeadline & BlockingGrpcClientDeadline clases
Add intercept logic at channel in AsyncGrpcClientDeadline & BlockingGrpcClientDeadline classes
Start server, and clients.
```

1. Create DeadlineInterceptor.java by implementing ClientInterceptor
```
package in.rk.bank.client.interceptor.deadline;

import io.grpc.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DeadlineInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        //return next.newCall(method,callOptions);// do nothing
        Deadline deadline = callOptions.getDeadline();
        if(Objects.isNull(deadline))
        {
            callOptions=callOptions.withDeadlineAfter(20, TimeUnit.SECONDS);
        }
        return next.newCall(method,callOptions);
    }
}
```
2. Add intercept logic at channel in AsyncGrpcClientDeadline & BlockingGrpcClientDeadline classes
```
ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 6565)
                .intercept(new DeadlineInterceptor())
                .usePlaintext()
                .build();
```


### Case-2 Metadata:Pass some information from client/client interceptor to service/server interceptor

```
Changes in bank-client module
New package "interceptor.metadata"
New class ClientConstants
Copy past BlockingGrpcClientDeadline, AsyncGrpcClientDeadline into interceptor.metadata package then rename
Remove deadline stuff from BlockingGrpcClientDeadline, AsyncGrpcClientDeadline
Add intercept logic for passing metada from client/client interceptor to service/server interceptor.
Ref: intercept(MetadataUtils.newAttachHeadersInterceptor(ClientConstants.getClientToken()))

Changes in bank-service module
New packages "interceptor" and "interceptor.metadata"
Copy BankServiceDeadline into "interceptor.metadata" then rename to BankServiceMetadata

Changes in bank-server module
New packages "interceptor" and "interceptor.metadata"
Copy GrpcServerDeadline past into "interceptor.metadata" then rename to  GrpcServerMetadata
Update the service logic
Ref: Server grpcServer = ServerBuilder.forPort(6565)
                                         .addService(new BankServiceMetadata())
                                         .build();
Create new class AuthInterceptor by implementing ServerInterceptor.
Add AuthInterceptor in Server
Ref:         Server grpcServer = ServerBuilder.forPort(6565)
                                              .intercept(new AuthInterceptor())
                                              .addService(new BankServiceMetadata())
                                              .build();

Start server, and clients.
```


1. ClientConstants
```
package in.rk.bank.client.interceptor.metadata;

import io.grpc.Metadata;

public class ClientConstants {
    private static final Metadata METADATA=new Metadata();

    static {
        METADATA.put(
                Metadata.Key.of("client-token",Metadata.ASCII_STRING_MARSHALLER),
                "bank-client-secret"
        );
    }
    public static Metadata getClientToken()
    {
        return METADATA;
    }
}
```

2. Add intercept metadata logic at channel in AsyncGrpcClientDeadline & BlockingGrpcClientDeadline classes

```
 ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 6565)
                .intercept(MetadataUtils.newAttachHeadersInterceptor(ClientConstants.getClientToken()))
                .usePlaintext()
                .build();
```

3. ServerConstants 
```
package in.rk.bank.service.interceptor;

import io.grpc.Context;
import io.grpc.Metadata;

public class ServerConstants {

    public static final Metadata.Key<String> TOKEN=Metadata.Key.of("client-token",Metadata.ASCII_STRING_MARSHALLER);
    public static final Metadata.Key<String> USER_TOKEN=Metadata.Key.of("user-token",Metadata.ASCII_STRING_MARSHALLER);
    public static final Context.Key<UserRole> CTX_USER_ROLE=Context.key("user-role");

}

```
3. AuthInterceptor
```
package in.rk.bank.server.interceptor.matadata;

import in.rk.bank.service.interceptor.ServerConstants;
import io.grpc.*;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class AuthInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        System.out.println("intercept @serverside AuthInterceptor......");
        String receivedClientToken=headers.get(ServerConstants.TOKEN);



        if(validateToken(receivedClientToken))
        {
            System.out.println("Valid client");
            return next.startCall(call,headers);
        }else
        {
            System.out.println("Invalid token");
            Status status = Status.UNAUTHENTICATED.withDescription("Invalid token");
            call.close(status,headers);
        }
        System.out.println("--...--");
        return new ServerCall.Listener<>(){
        };
    }
    private boolean validateToken(String token)
    {
        //return ThreadLocalRandom.current().nextBoolean();
        return Objects.nonNull(token) && Objects.equals(token,"bank-client-secret" );

    }
}

```

### Case-3 Context: Pass some information from service/server interceptor to Server business/service layer
Many users access our application(client app) if server required users session token information.

```
Changes in bank-client
create package interceptor.context
Create ClientConstants class which has Metadata(client-token) and Metadat keys(user-token)
Create UserSessionToken by extending extends CallCredentials. which passes user-token from client
Copy paste BlockingGrpcClientContext, AsyncGrpcClientContext with  .withCallCredentials(new UserSessionToken("user-token:prime")) locig in each stub method.
Ref:
  blockingStub.withDeadline(Deadline.after(10, TimeUnit.SECONDS))
              .withCallCredentials(new UserSessionToken("user-token:prime"))
              .checkBalance(balanceRequest));
Changes in bank-service
Create ServerConstants, UserRole which can be access from bank-server too.
Create BankServiceContext 
Update the logic to retrieve context data in service methods
Ref: UserRole userRole= ServerConstants.CTX_USER_ROLE.get();

Changes in bank-server
create interceptor.context package
Create RoleInterceptor class
Create GrpcServerContext class

```
1. ClientConstants
```
package in.rk.bank.client.interceptor.context;

import io.grpc.Metadata;

public class ClientConstants {
    private static final Metadata METADATA=new Metadata();
    public static final Metadata.Key<String> USER_TOKEN=Metadata.Key.of("user-token",Metadata.ASCII_STRING_MARSHALLER);

    static {
        METADATA.put(
                Metadata.Key.of("client-token",Metadata.ASCII_STRING_MARSHALLER),
                "bank-client-secret"
        );
    }
    public static Metadata getClientToken()
    {
        System.out.println("Client token:"+METADATA);
        return METADATA;
    }
}

```
2. UserSessionToken
```
package in.rk.bank.client.interceptor.context;

import io.grpc.CallCredentials;
import io.grpc.Metadata;

import java.util.concurrent.Executor;

public class UserSessionToken extends CallCredentials {
    private String jwt;
    public UserSessionToken(String jwt)
    {
        this.jwt=jwt;
    }

    @Override
    public void applyRequestMetadata(RequestInfo requestInfo, Executor appExecutor, MetadataApplier applier) {
        appExecutor.execute(()->{
            Metadata metadata=new Metadata();
            metadata.put(ClientConstants.USER_TOKEN,this.jwt);
            applier.apply(metadata);
        });
    }

    @Override
    public void thisUsesUnstableApi() {
        //may change in future
    }
}

```
 3. ServerConstants
 ```
package in.rk.bank.service.interceptor;

import io.grpc.Context;
import io.grpc.Metadata;

public class ServerConstants {

    public static final Metadata.Key<String> TOKEN=Metadata.Key.of("client-token",Metadata.ASCII_STRING_MARSHALLER);
    public static final Metadata.Key<String> USER_TOKEN=Metadata.Key.of("user-token",Metadata.ASCII_STRING_MARSHALLER);
    public static final Context.Key<UserRole> CTX_USER_ROLE=Context.key("user-role");

}
```
4. UserRole
```
package in.rk.bank.service.interceptor;

public enum UserRole {
    PRIME, STANDARD;
}
```
5. RoleInterceptor
```
package in.rk.bank.server.interceptor.context;

import in.rk.bank.service.interceptor.ServerConstants;
import in.rk.bank.service.interceptor.UserRole;
import io.grpc.*;

import java.util.Objects;

public class RoleInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        System.out.println("intercept @serverside AuthInterceptor......");
        String receivedUserToken=headers.get(ServerConstants.USER_TOKEN);


        if(validateToken(receivedUserToken))
        {
            System.out.println("Valid token");
            UserRole userRole = extractUserRole(receivedUserToken);
            Context context = Context.current().withValue(ServerConstants.CTX_USER_ROLE, userRole);
            return Contexts.interceptCall(context, call, headers, next);
        }else
        {
            System.out.println("Invalid token");
            Status status = Status.UNAUTHENTICATED.withDescription("Invalid token");
            call.close(status,headers);
        }
        System.out.println("--...--");
        return new ServerCall.Listener<>(){
        };
    }
    private boolean validateToken(String token)
    {
        //return ThreadLocalRandom.current().nextBoolean();
        return Objects.nonNull(token) && token.startsWith("user-token");

    }
    private UserRole extractUserRole(String receivedUserToken)
    {
        return receivedUserToken.endsWith("prime")?UserRole.PRIME:UserRole.STANDARD;
    }
}

```
6. GrpcServerContext
```
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

```