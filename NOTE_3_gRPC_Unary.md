# gRPC Unary
Client sends single request and server sends single response back.

### gRPC project setup ( IntelliJ )
1) bank-proto module: proto messages & grpc services
2) bank-service module: service implementation
3) bank-server module: server which uses service/services.
4) bank-client module: Client implementation
- Postman client or BloomRPC client can be used for testing.

### 1. bank-proto module
1. Right click on ```gRPC``` project --> New --> Module --> Maven-archtype-quickstart --> bank-proto -->next --> Finish
2. Add grpc-protobuf, grpc-stub dependencies & plugins in  ```pom.xml```
```
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>in.rk</groupId>
    <artifactId>bank-proto</artifactId>
    <version>1.0-SNAPSHOT</version>

    <name>bank-proto</name>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
    </properties>

    <dependencies>
        <!-- To generate language specific compiled Classes for message... -->
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-protobuf</artifactId>
            <version>1.49.0</version>
        </dependency>

        <!-- Used to compile time to generate stub -->
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-stub</artifactId>
            <version>1.49.0</version>
        </dependency>

        <!-- to handle grpc http calls -->
        <!-- Commented as not dealing with http calls
        <dependency>
          <groupId>io.grpc</groupId>
          <artifactId>grpc-netty-shaded</artifactId>
          <version>1.49.0</version>
        </dependency>
        -->
        <!-- Required if we use java 9 or above  -->
        <dependency>
            <groupId>org.apache.tomcat</groupId>
            <artifactId>annotations-api</artifactId>
            <version>6.0.53</version>
            <scope>provided</scope>
        </dependency>

        <!-- To handle Json mappings json to java & java to json -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.13.3</version>
        </dependency>
    </dependencies>
    <build>
        <extensions>
            <extension>
                <groupId>kr.motd.maven</groupId>
                <artifactId>os-maven-plugin</artifactId>
                <version>1.6.2</version>
            </extension>
        </extensions>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>11</source>
                    <target>11</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.xolstice.maven.plugins</groupId>
                <artifactId>protobuf-maven-plugin</artifactId>
                <version>0.6.1</version>
                <configuration>
                    <protocArtifact>
                        com.google.protobuf:protoc:3.19.0:exe:${os.detected.classifier}
                    </protocArtifact>
                    <pluginId>grpc-java</pluginId>
                    <pluginArtifact>
                        io.grpc:protoc-gen-grpc-java:1.49.0:exe:${os.detected.classifier}
                    </pluginArtifact>
                    <protoSourceRoot>
                        ${basedir}/src/main/proto/
                    </protoSourceRoot>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>compile-custom</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```
3. Create required folders under src/main/```proto```/```models``` and src/main/```proto```/```services```
4. Create ```balance-check-request.proto``` file under src/main/```proto```/```models```
```
syntax="proto3";

option java_multiple_files=true;
option java_package="in.rk.bank.models";

package models;

message BalanceCheckRequest
{
  int32 account_number=1;
}
```
5. Create ```balance-check-response.proto``` file under src/main/```proto```/```models```
```
syntax="proto3";

option java_multiple_files=true;
option java_package="in.rk.bank.models";

package models;

message BalanceCheckResponse
{
  int32 amount=1;
}
```
6. Create ```bank-services.proto``` file under src/main/```proto```/```services```
```
syntax="proto3";

option java_multiple_files=true;
option java_package="in.rk.bank.services";

package services;
import "models/balance-check-request.proto";
import "models/balance-check-response.proto";


service BankService
{
  rpc checkBalance(models.BalanceCheckRequest) returns (models.BalanceCheckResponse);
}
```
7. Run ```mvn clean install``` then check generated files in ```target/generated-sources/protobuf/*``` 

### 2. bank-service module
1. Right click on ```gRPC``` project --> New --> Module --> Maven-archtype-quickstart --> bank-service -->next --> Finish
2. Add ```bank-proto``` dependency in  ```pom.xml```
```
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>in.rk</groupId>
  <artifactId>bank-service</artifactId>
  <version>1.0-SNAPSHOT</version>

  <name>bank-service</name>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
  </properties>

  <dependencies>
    <dependency>
      <groupId>in.rk</groupId>
      <artifactId>bank-proto</artifactId>
      <version>1.0-SNAPSHOT</version>
    </dependency>
  </dependencies>

</project>
```
3. Create ```AccountDB.java``` as DB repository
```
package in.rk.bank.db;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AccountDB {
    private static final Map<Integer, Integer> accountsMap = IntStream.rangeClosed(1, 10)
            .boxed()
            .collect(
                    Collectors.toMap(Function.identity(),
                            v -> v * 10)
            );

    public static boolean isAccountAvailable(int accountNumber) {
        return accountsMap.containsKey(accountNumber);
    }

    public static Integer getBalance(int accountNumber) {
        return accountsMap.getOrDefault(accountNumber, 0);
    }

    public static Integer addBalance(int accountNumber, int amount) {
        return accountsMap.computeIfPresent(accountNumber, (k, v) -> v + amount);
    }

    public static Integer deductBalance(int accountNumber, int amount) {
        return accountsMap.computeIfPresent(accountNumber, (k, v) -> v - amount);
    }

}
```
4. Create BankService.java by extending BankServiceGrpc.BankServiceImplBase & override checkBalance(-) method.
```
package in.rk.bank.service;

import in.rk.bank.db.AccountDB;
import in.rk.bank.models.BalanceCheckRequest;
import in.rk.bank.models.BalanceCheckResponse;
import in.rk.bank.services.BankServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class BankService extends BankServiceGrpc.BankServiceImplBase{
    @Override
    public void checkBalance(BalanceCheckRequest request, StreamObserver<BalanceCheckResponse> responseObserver) {
        if(AccountDB.isAccountAvailable(request.getAccountNumber()))
        {
            BalanceCheckResponse balanceCheckResponse=BalanceCheckResponse.newBuilder()
                    .setAmount(AccountDB.getBalance(request.getAccountNumber()))
                    .build();
            responseObserver.onNext(balanceCheckResponse);
            responseObserver.onCompleted();
        }else
        {
            Status status= Status.FAILED_PRECONDITION.withDescription("Account number is not available");
            responseObserver.onError(status.asRuntimeException());
            return;
        }
    }
}
```
5. Run ```mvn clean install```

### 3. bank-server module
1. Right click on ```gRPC``` project --> New --> Module --> Maven-archtype-quickstart --> bank-server -->next --> Finish
2. Add ```bank-service``` , ```grpc-netty-shaded``` dependencies in  ```pom.xml```
```
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>gRPC</artifactId>
        <groupId>in.rk</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>bank-server</artifactId>

    <name>bank-server</name>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>in.rk</groupId>
            <artifactId>bank-service</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-netty-shaded</artifactId>
            <version>1.49.0</version>
        </dependency>
    </dependencies>
</project>
```
3. Create ```GrpcServer.java``` class to build, configure port, service.
```
package in.rk.bank.server;

import in.rk.bank.service.BankService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server grpcServer = ServerBuilder.forPort(6565)
                                         .addService(new BankService())
                                         .build();
        grpcServer.start();
        System.out.println("Server is started with BankService!");
        grpcServer.awaitTermination();
    }
}
```
4. Run ```mvn clean install```

### 4. bank-client module
1. Right click on ```gRPC``` project --> New --> Module --> Maven-archtype-quickstart --> bank-client -->next --> Finish
2. Add ```bank-service``` , ```grpc-netty-shaded``` dependencies in  ```pom.xml```
3. Create BlockingGrpcClient.java to interact with server.
```
package in.rk.bank.client;

import in.rk.bank.models.BalanceCheckRequest;
import in.rk.bank.services.BankServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class BlockingGrpcClient {

    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        BankServiceGrpc.BankServiceBlockingStub blockingStub =BankServiceGrpc.newBlockingStub(managedChannel);

        //1 Unary
        BalanceCheckRequest balanceRequest= BalanceCheckRequest.newBuilder().setAccountNumber(7).build();
        balanceCheckUnary(blockingStub,balanceRequest);

    }

    private static void balanceCheckUnary(BankServiceGrpc.BankServiceBlockingStub blockingStub, BalanceCheckRequest balanceRequest) {
        try {
            System.out.println("Received Response:" + blockingStub.checkBalance(balanceRequest));
        }catch (StatusRuntimeException e)
        {
            System.err.println("Known Error while checking balance:"+e);
        }catch (Exception e)
        {
            System.err.println("Unknown Error while checkig balance"+e);
        }
    }
}
```
4. Run ```mvn clean install```

### Testing-1 : bank-client
1. start Server:  bank-server--> GrpcServer:main(-)
2. Send request from Client: bank-client-->BlockingGrpcClient:main(-)--balanceCheckUnary(-,-)

If server is up
```
Received Response:amount: 70
```
If server is down
```
Known Error while checking balance:io.grpc.StatusRuntimeException: UNAVAILABLE: io exception
```
### Testing-2 : Postman client
```
Open Postman Desktop
Sign in with gmail--> Collection-->new-->gRPC Request--> 
Enter Server URL: localhost:6565
select method: Import a .proto file [select bank-services.proto file]
+Add an import path [src/main/proto]
Next
Import as API
Now select method: choose checkBalance
Click on Message
Click Use Example Message
Update message
Invoke
```
### Testing-3 : BloomRPC
```
Open https://github.com/bloomrpc/bloomrpc/releases in browser
Under Assets--> Download and install s/w as per our OS(BloomRPC-Setup-xx)--> Launch
--> Import path: src/main/proto
--> + Import Proto: bank-service.proto file
--> Env: localhost:6565 [Unary call] 
Editor: Update the request then click on Play button for response.
```

