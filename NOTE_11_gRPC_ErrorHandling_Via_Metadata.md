### gRPC - Error Handling

* Error Channel
    - Status Codes: limited set of user-defined codes
    - Metadata: Server can pass data to client through Metadata
* Data Channel
    - OneOf

gRPCs response can be success with data or fail with some error code and error text.

### 1. grpcErrorHandling module
1. Right click on ```gRPC``` project --> New --> Module --> Maven-archtype-quickstart --> grpcErrorHandling -->next --> Finish
2. Add grpc-protobuf, grpc-stub,grpc-netty-shaded,junit dependencies & plugins in  ```pom.xml```

a) pom.xml
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
    <artifactId>grpcErrorHandling</artifactId>
    <name>grpcErrorHandling</name>

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
        <dependency>
          <groupId>io.grpc</groupId>
          <artifactId>grpc-netty-shaded</artifactId>
          <version>1.49.0</version>
        </dependency>
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
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
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
b) proto/models/request.proto
```
syntax="proto3";

option java_multiple_files=true;
option java_package="in.rk.cal.models";

package models;

message Request
{
  int32 number=1;
}
```
c) proto/models/response.proto
```
syntax="proto3";

option java_multiple_files=true;
option java_package="in.rk.cal.models";

package models;


enum ErrorCode
{
  MUST_NOT_ONE =0;
  MUST_NOT_THREE=1;
  MUST_NOT_FOUR=2;
}
message SuccessResponse
{
  int32 results=1;
}
message ErrorResponse
{
  int32 input=1;
  ErrorCode error_code=2;
}
message Response
{
  oneof response {
    SuccessResponse success_response=1;
    ErrorResponse error_response=2;
  }
}
```
d) proto/services/service.proto
```
syntax="proto3";

option java_multiple_files=true;
option java_package="in.rk.cal.services";

package services;
import "models/request.proto";
import "models/response.proto";

service CalService
{
  //1. Unary
  rpc getSquare(models.Request) returns (models.Response);

}
```
e) service/CalService.java
```
package in.rk.cal.service;

import in.rk.cal.models.*;
import in.rk.cal.services.CalServiceGrpc;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.StreamObserver;

public class CalService extends CalServiceGrpc.CalServiceImplBase {
    @Override
    public void getSquare(Request request, StreamObserver<Response> responseObserver) {
        int number=request.getNumber();
        if(number==2)
        {
            //success with resp
            SuccessResponse resp = SuccessResponse.newBuilder().setResults(number * number).build();
            responseObserver.onNext(Response.newBuilder().setSuccessResponse(resp).build());
            responseObserver.onCompleted();
        }else if(number==1)
        {
            //failed with Status
            Status status=Status.FAILED_PRECONDITION.withDescription("Invlid number :"+number);
            responseObserver.onError(status.asRuntimeException());
            return;
        }else if(number==3)
        {
            //failed with errorCode(Metadata)
            Metadata metadata=new Metadata();
            Metadata.Key<ErrorResponse> responseKey= ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());

            ErrorCode errorCode = ErrorCode.MUST_NOT_THREE;
            ErrorResponse errorRespo=ErrorResponse.newBuilder().setErrorCode(errorCode)
                    .setInput(number)
                    .build();

            //pass error object via metadata
            metadata.put(responseKey,errorRespo);
            responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException(metadata));
            return;
        }else if(number==4)
        {
            //failed with Reponse(oneof [success or error])
            ErrorCode errorCode = ErrorCode.MUST_NOT_FOUR;
            ErrorResponse errorRespo=ErrorResponse.newBuilder().setErrorCode(errorCode)
                    .setInput(number)
                    .build();

            responseObserver.onNext(Response.newBuilder().setErrorResponse(errorRespo).build());
            responseObserver.onCompleted();
        }
    }
}

```
f) server/CalServer.java
```
package in.rk.cal.server;

import in.rk.cal.service.CalService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server grpcServer = ServerBuilder.forPort(6565)
                .addService(new CalService())
                .build();
        //start
        grpcServer.start();

        //shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("gRPC server is shutting down!");
            grpcServer.shutdown();
        }));

        System.out.println("Server is started with BankService!");
        grpcServer.awaitTermination();
    }
}

```
g) client/CalClient.java
```
package in.rk.cal.client;

import in.rk.cal.models.ErrorResponse;
import in.rk.cal.models.Request;
import in.rk.cal.services.CalServiceGrpc;
import io.grpc.*;
import io.grpc.protobuf.ProtoUtils;

public class CalClient {
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        CalServiceGrpc.CalServiceBlockingStub blockingStub =CalServiceGrpc.newBlockingStub(managedChannel);

        //1 Unary-success
        Request request1= Request.newBuilder().setNumber(2).build();
        getSquare(blockingStub,request1);

        //2 Unary-Failed with Status Codes
        Request request2= Request.newBuilder().setNumber(1).build();
        getSquare(blockingStub,request2);

        //3 Unary-Failed with Metadata
        Request request3= Request.newBuilder().setNumber(3).build();
        getSquare(blockingStub,request3);

        //4 Unary-Failed Response oneof Success or error
        Request request4= Request.newBuilder().setNumber(4).build();
        getSquare(blockingStub,request4);



    }

    private static void getSquare(CalServiceGrpc.CalServiceBlockingStub blockingStub, Request request) {
        try {
            System.out.println("Received Response:" + blockingStub.getSquare(request));
        }catch (StatusRuntimeException e)
        {
            System.err.println("Known Error while getting square:"+e);
            Status status=Status.fromThrowable(e);
            System.out.println(status.getCode()+":"+status.getDescription());
        }catch (Exception e)
        {
            System.err.println("Unknown Error while getting square:"+e);
            Metadata metadata=Status.trailersFromThrowable(e);
            ErrorResponse errorResp=metadata.get(ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance()));
            System.out.println(errorResp);

        }
    }
}

```
h) test/...../service/CalServiceTest.java
```
package in.rk.cal.service;

import in.rk.cal.models.ErrorCode;
import in.rk.cal.models.ErrorResponse;
import in.rk.cal.models.Request;
import in.rk.cal.models.Response;
import in.rk.cal.services.CalServiceGrpc;
import io.grpc.*;
import io.grpc.protobuf.ProtoUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CalServiceTest {

    private ManagedChannel channel;
    private CalServiceGrpc.CalServiceBlockingStub clientStub;

    @Before
    public void setUp() throws Exception {
        this.channel= ManagedChannelBuilder.forAddress("localhost",6565)
                .usePlaintext()
                .build();
        this.clientStub=CalServiceGrpc.newBlockingStub(channel);
    }

    @Test
    public void calSquareHappyPath() {
        System.out.println("calSquareHappyPath()---Started");
        Request req= Request.newBuilder().setNumber(2).build();
        Response resp=this.clientStub.getSquare(req);
        System.out.println("Respo:"+resp);
        Assert.assertEquals(resp.getSuccessResponse().getResults(),4);
    }

    @Test
    public void calSquareErrorWithStatus1() {
        System.out.println("calSquareErrorWithStatus1()---Started");
        Request req= Request.newBuilder().setNumber(1).build();
        try {
            Response resp=this.clientStub.getSquare(req);
            System.out.println("Respo:"+resp);
        }
        catch(StatusRuntimeException e)
        {
            Status status=Status.fromThrowable(e);
            System.out.println(status.getCode()+":"+status.getDescription());
        }
    }
    @Test(expected = StatusRuntimeException.class)
    public void calSquareErrorWithStatus2() {
        System.out.println("calSquareErrorWithStatus2()---Started");
        Request req= Request.newBuilder().setNumber(1).build();
        Response resp=this.clientStub.getSquare(req);
        System.out.println("Respo:"+resp);
    }

    @Test
    public void calSquareErrorResponse() {
        System.out.println("calSquareErrorResponse()---Started");
        Request req= Request.newBuilder().setNumber(3).build();

        try
        {
            Response resp=this.clientStub.getSquare(req);
            System.out.println("Respo:"+resp);
        }catch (Exception e)
        {
            Metadata metadata=Status.trailersFromThrowable(e);
            ErrorResponse errorResp=metadata.get(ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance()));
            System.out.println(errorResp);
        }
    }
    @Test
    public void calSquareOneOfSucsessOrErrorResponse() {
        System.out.println("calSquareHappyPath()---Started");
        Request req= Request.newBuilder().setNumber(4).build();
        Response resp=this.clientStub.getSquare(req);
        System.out.println("Respo:"+resp);
        Assert.assertEquals(resp.getErrorResponse().getErrorCode(), ErrorCode.MUST_NOT_FOUR);
    }

    @After
    public void tearDown() throws Exception {
        this.channel.shutdown();
    }
}
```