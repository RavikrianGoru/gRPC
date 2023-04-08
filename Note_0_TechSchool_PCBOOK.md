### Reference

- [Different google protobuf types](https://protobuf.dev/reference/protobuf/google.protobuf/)
- [TechShool github repo for grpc java](https://github.com/techschool/pcbook-java)



### The motivation of gRPC:
1. Communication
```
Backend & Frontend applications are written in different languages.
Microservices might be written in different languages.(Go,java,Python,Rust..)
They must agree on the API contract to exchange information.
	Communication Channel: REST,SOAP, Message Queue
	Authentication Machanism: Basic, OAuth, JWT
	Payload format: json,xml, binary
	Date model
	Error Handling
```
2. Communication should be efficient.
```
Huge amount of exchange messages b/w micro-services.
Mobile n/w can be slow with limited bandwidth.
```
3. Communication should be simple.
```
Client & Server should focus on their core service logic
Let the framework handle the rest
```
### gRPC
What is gRPC?
```
gRPC is a high-performance open-source feature-rich RPC framework.
gRPC is originally developed by Google
Now it is a part of the Could Native Computing Foundation-CNCF
g stands for different things in each release gRPC,good,green,glorious,game,gon....
RPC stands for Remote Procedure Calls.
```
What is RPC?
```
It is prototcol that allows a program to execute a procedure of another program located in other computer 
without the developer explicitly coding the details for th remote interaction.
```
How gRPC works?
```
Client has a generated stub that provides the same method as the server.
The stub calls gRPC framework under the hood to exchange information over n/w.
Client & servr use stubs to interact with each other, so they only need to implement their core service logic.
```
gRPC code generation with Protobuf
```
API Contract description
	The services & payload messages are defined using Protocol Buffer
```
```
syntax="proto3";
message HelloRequest
{
	string name=1;
}
message HelloResponse
{
	string gret=1;
}
service WelcomeService
{
	rpc hello(HelloRequest) returns (HelloResponse);
}
```

Why gRPC uses Protocol Buffer?
```
Human-readable Interface Definition Language(IDL)
Programing languages interoperable: code generators for many languages.
Binary data representation: smaller size, faster to transport, more efficient to serialize/deserialize.
Strongly typed contract.
API evolution: Backward & forward compatibility
Alternative options: Google flatbuffers, Microsoft bond.
```
gRPC support 10+ languages.
```
Pure implementation: Go, Java NodeJS.
C/C++, C#, Objective-C, Pythn, Ruby, Dart, PHP.
Swift, Rust, TypeScript,...
```

What makes gRPC efficient?
```
gRPC uses http/2 as its transfer protocol.
http/2: 
	binary framing: more performance, lighter to traansport, safer to decode, greate combination with protobuf.
	Header compression using HPACK: reduce overhead & improve performance.
	Multiplexing: sends multiple reqs & resp in parallel over a single TCP connection. Reduces latency & improve n/w utilization.
	Server Push: one client req, multiple resps. resuce round-trip latency.
```

How http/2 works?
```
Single TCP connection carries multiple bidirectional sreams.
Each stream has a unique ID & carries multipl bidirectional messages.
Each msg broken down into multiple binary frames.
Frame is the smallest unit that carries different types of data:  headers, settings,priority, data...etc.
Frames from different streams are interleaved and then reassembled on the other side.
```

Difference b/w http/2 and http/1.1?

| Difference    | http/2    | http/1.1 |
| ----------------- | ----------- | ------- |
| Transfer Protocol | Binary | Text |
| Headers Compressed | Plain | Text |
| Multiplexing | Yes | No |
| Reqs per Connection | Multiple | 1 |
| Server Push | Yes | No |
| Release Year | 2015 | 1997 |

### Types of gRPC:

1. Unary: like HTTP API
```
	Client sends 1 req------- Server sends 1 resp
```
2. Client Streaming
```
	Clinet sends multiple stream of messages ----- Server sends 1 resp.
```
3. Server Streaming
```
	Client sends 1 req ----Server sends stream messages back.
```
4. BiDirectional Streaming
```
	Client sends stream of messages -------- Server sends back stream of messages in parallel with arbitrary order.
```

gRPC vs REST

| Features |			gRPC |					REST |
| ------------------ | -------------------------- | ------------------------|
| Protocol |		http/2(fast) |			http/1.1(slow) |
| Payload |				Protobuf(binary,small) |	JSON(text,large) |
| APIcontract |			strict,requied(.proto) |	Loose,optional(Open API) |
| Code generation |		Built-in(protoc) |		3rd party tools(Swagger) |
| Security |			TLS/SSL |					TLS/SSL |
| Streaming |			Bidirectional |			client--> server |
| Browser support |		Limited(required gRPC-web) |	Yes |


Where gRPC is well suited to?
```
Microservices(low latency & high throughput communication), strong API contract.
Code generation put of bux for many languages
Point-to-Point realtime communication
Network constrained env(mobile apps as lightweight mgs format)
```
### How to define protocol buffer message?
```
syntax ="proto3";
message <NameOfTheMessage>
{
	<data-type> name_of_field_1 = tag_1;
	<data-type> name_of_field_2 = tag_2;

	<data-type> name_of_field_n = tag_n;
}
```
```
Name of the message : UpperCamelCase
Name of the field: lower_snake_case
Some scalar-value data types:
	string, bool, bytes
	float, double
	int32,int64,uint32,uint64,sint32,sint64...etc
Data types can be user-defined enums or other messages.
Tags are more important than field names.
	is an arbitrary integer
		From 1 to 2^29-1
		Except 19000 to 19999 are reserved
		From 1 to 15 tkes 1 byte. apply for most frequently used fields
		From 15 o 2047 takes 2 bytes
		No need to be in-order or sequetial
		Must be unique for same-level fields.
Custom data types
	Enum
	MessageNested or Not nested?
	Well-known types( google)
	Multiple proto files
		package
		import
	Repeated fields
	Oneof fields
	option 
```
### gRPC project setup ( IntelliJ )
1) Create new maven module ```pcbook``` under gRPC project.

### 1. pcbook module
1. Right click on ```gRPC``` project --> New --> Module --> Maven-archtype-quickstart --> ```pcbook``` -->next --> Finish
2. Add grpc-protobuf, grpc-stub, grpc-netty-shaded, protobuf-java-util, junit dependencies & plugins in  ```pom.xml```
```
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>in.rk</groupId>
    <artifactId>pcbook</artifactId>
    <version>1.0-SNAPSHOT</version>

    <name>pcbook</name>

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
<!--        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.13.3</version>
        </dependency>
-->
        <!-- https://mvnrepository.com/artifact/com.google.protobuf/protobuf-java-util -->
        <dependency>
            <groupId>com.google.protobuf</groupId>
            <artifactId>protobuf-java-util</artifactId>
            <version>3.22.0</version>
            <!--<scope>runtime</scope>-->
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
3. Create required folders under src/main/```proto```
4. Create ```memory_message.proto``` file under src/main/```proto```
```
syntax = "proto3";

package rk.pcbook;
option java_multiple_files=true;
option java_package="in.rk.pcbook.models";

message Memory
{
  enum Unit
  {
    UNKNOWN=0;
    BIT=1;
    BYTE=2;
    KILOBYTE=3;
    MEGABYTE=4;
    GIGABYTE=5;
    TERABYTE=6;
  }
  uint64 value=1;
  Unit unit=2;
}

```
5. Create ```processor_message.proto``` file under src/main/```proto```
```
syntax = "proto3";

package rk.pcbook;
option java_multiple_files=true;
option java_package="in.rk.pcbook.models";

import "memory_message.proto";

message CPU
{
  string brand=1;
  string name=2;
  uint32 number_cores=3;
  uint32 number_threads=4;
  double min_ghz=5;
  double max_ghz=6;
}

message GPU
{
  string brand=1;
  string name=2;
  double min_ghz=3;
  double max_ghz=4;
  Memory memory=5;
}

```
6. Create ```storage_message.proto``` file under src/main/```proto```
```
syntax="proto3";
package rk.pcbook;
option java_multiple_files=true;
option java_package="in.rk.pcbook.models";

import "memory_message.proto";

message Storage
{
  enum Driver
  {
    UNKNOWN=0;
    HDD=1;
    SSD=2;
  }
  Driver driver =1;
  Memory memory=2;
}

```
7. Create ```keyboard_message.proto``` file under src/main/```proto```
```
syntax="proto3";
package rk.pcbook;
option java_multiple_files=true;
option java_package="in.rk.pcbook.models";

message Keyboard
{
  enum Layout
  {
    UNKNOW=0;
    QWERTY=1;
    QWERTZ=2;
    AZERTY=3;
  }
  Layout layout =1;
  bool backlit =2;
}

```
8. Create ```screen_message.proto``` file under src/main/```proto```
```
syntax="proto3";
package rk.pcbook;
option java_multiple_files=true;
option java_package="in.rk.pcbook.models";

message Screen
{
  message Resolution
  {
    uint32 width=1;
    uint32 height=2;
  }
  enum  Panel
  {
    UNKNOWN=0;
    IPS=1;
    OLED=2;
  }

  float size_inche=1;
  Resolution resolution=2;
  Panel panel=3;
  bool multitouch=4;
}

```
9. Create ```laptop_message.proto``` file under src/main/```proto```
```
syntax="proto3";
package rk.pcbook;
option java_multiple_files=true;
option java_package="in.rk.pcbook.models";
import "processor_message.proto";
import "memory_message.proto";
import "storage_message.proto";
import "screen_message.proto";
import "keyboard_message.proto";
import "google/protobuf/timestamp.proto";


message Laptop
{
  string id=1;
  string brand=2;
  string name=3;
  CPU cpu=4;
  Memory memory=5;
  repeated GPU gpus=6;
  repeated Storage storages=7;
  Screen screen=8;
  Keyboard keyboard=9;
  oneof weight
  {
    double weigh_kg=10;
    double weight_lb=11;
  }
  double price_usd=12;
  uint32 release_year=13;
  google.protobuf.Timestamp updated_at=14;
}

```
10. Do maven compile

11. Create Generator.java file
```
package in.rk.pcbook.sample;

import com.google.protobuf.Timestamp;
import in.rk.pcbook.models.*;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

public class Generator {

    private final Random rand;

    public Generator() {
        this.rand = new Random();
    }

    public Keyboard newKeyboard() {
        return Keyboard.newBuilder()
                .setLayout(randomKeyLayout())
                .setBacklit(rand.nextBoolean())
                .build();
    }

    public CPU newCPU() {
        String brand = randomCPUBrand();
        String name = randomCPUName(brand);
        int numberCores = randomInt(2, 8);
        int numberThreads = randomInt(numberCores, 12);
        double minGhz = randomDouble(2.0, 3.5);
        double maxGhz = randomDouble(minGhz, 5.0);

        return CPU.newBuilder()
                .setName(name)
                .setBrand(brand)
                .setNumberCores(numberCores)
                .setNumberThreads(numberThreads)
                .setMinGhz(minGhz)
                .setMaxGhz(maxGhz)
                .build();
    }
    public GPU newGPU()
    {
        String brand=randGPUBrand();
        String name=randomGPUName(brand);
        double minGhz=randomDouble(1.0,1.5);
        double maxGhz=randomDouble(minGhz, 2.0);
        Memory memory = Memory.newBuilder()
                .setValue(randomInt(2,6))
                .setUnit(Memory.Unit.GIGABYTE)
                .build();
        return GPU.newBuilder()
                .setBrand(brand)
                .setName(name)
                .setMinGhz(minGhz)
                .setMaxGhz(maxGhz)
                .setMemory(memory)
                .build();
    }
    public Memory newRAM()
    {
        return Memory.newBuilder()
                .setValue(randomInt(4,64))
                .setUnit(Memory.Unit.GIGABYTE)
                .build();
    }

    public Storage newSSD()
    {
        Memory memory = Memory.newBuilder()
                .setValue(randomInt(128,1024))
                .setUnit(Memory.Unit.GIGABYTE)
                .build();
        return Storage.newBuilder()
                .setDriver(Storage.Driver.SSD)
                .setMemory(memory)
                .build();
    }
    public Storage newHHD()
    {
        Memory memory = Memory.newBuilder()
                .setValue(randomInt(1,6))
                .setUnit(Memory.Unit.TERABYTE)
                .build();
        return Storage.newBuilder()
                .setDriver(Storage.Driver.HDD)
                .setMemory(memory)
                .build();
    }

    public Screen newScreen()
    {
        int height =randomInt(1080, 4320);
        int width=height* 16/9;
        Screen.Resolution resolution=Screen.Resolution.newBuilder()
                .setHeight(height)
                .setWidth(width)
                .build();
        return Screen.newBuilder()
                .setSizeInche(randFloat(13,17))
                .setResolution(resolution)
                .setPanel(randomScreenPannel())
                .setMultitouch(rand.nextBoolean())
                .build();
    }
    public Laptop newLaptop()
    {
        String brand=randomLaptopBrand();
        String name=randomLaptopName(brand);
        double weight=randomDouble(1.4, 3.5);
        double priceUsd=randomDouble(1500,3500);
        int releaseYear=randomInt( 2015,2019);
        return Laptop.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setBrand(brand)
                .setName(name)
                .setCpu(newCPU())
                .setRam(newRAM())
                .addGpus(newGPU())
                .addStorages(newSSD())
                .addStorages(newHHD())
                .setScreen(newScreen())
                .setKeyboard(newKeyboard())
                .setWeighKg(weight)
                .setPriceUsd(priceUsd)
                .setReleaseYear(releaseYear)
                .setUpdatedAt(timestampNow())
                .build();

    }

    private Timestamp timestampNow() {
        Instant now=Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();
    }

    private String randomLaptopName(String brand) {
        switch (brand) {
            case "Apple":
                return randomStringFromSet("Apple Air", "MackBook Pro");
            case ("Dell"):
                return randomStringFromSet("Inspiron", "XPS", "Vostro");
            default:
                return randomStringFromSet("Thinkpad X1","Thinkpad P1", "G570");
        }

    }

    private String randomLaptopBrand() {
        return randomStringFromSet("Apple", "Dell", "Lenovo");
    }

    private Screen.Panel randomScreenPannel() {
        if(rand.nextBoolean())
            return Screen.Panel.IPS;
        return Screen.Panel.OLED;
    }

    private float randFloat(int min, int max) {
    return min + rand.nextFloat() * (max -min);
    }

    private String randomGPUName(String brand) {
        if(brand=="NVIDIA")
        {
            return randomStringFromSet("RTX 2060", "RTX 2070", "GTX 1070");
        }
        else
        {
            return randomStringFromSet("RX 590", "RX 580", "RX Vega-56");
        }
    }


    private String randGPUBrand() {
        return randomStringFromSet("NVIDIA","AMD");
    }

    private double randomDouble(double min, double max) {
        return min + rand.nextDouble() * (max - min);
    }

    private int randomInt(int min, int max) {
        return min + rand.nextInt(max - min + 1);
    }

    private String randomCPUName(String brand) {
        if (brand == "Intel") {
            return randomStringFromSet("Core i3",
                    "Core i5",
                    "Core i7",
                    "Core i9");
        }else {
            return randomStringFromSet("Ryzen 3",
                    "Ryzen 5",
                    "Ryzen 7");
        }
    }

    private String randomCPUBrand() {
        return randomStringFromSet("Intel", "AMD");
    }

    private String randomStringFromSet(String... a) {
        int n = a.length;
        if (n == 0)
            return "";
        return a[rand.nextInt(n)];
    }

    private Keyboard.Layout randomKeyLayout() {
        switch (rand.nextInt(3)) {
            case 1:
                return Keyboard.Layout.QWERTY;
            case 2:
                return Keyboard.Layout.QWERTZ;
            default:
                return Keyboard.Layout.AZERTY;
        }
    }

    public static void main(String[] args) {
        Generator generator=new Generator();
        Laptop laptop =generator.newLaptop();
        System.out.println(laptop);
    }

}
```
12. Create Serializer.java
```
package in.rk.pcbook.serializer;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import in.rk.pcbook.models.Laptop;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Serializer {
    public void writeBinaryFile(Laptop laptop, String fileName) throws IOException {
        FileOutputStream fos =new FileOutputStream(fileName);
        laptop.writeTo(fos);
        fos.close();
    }

    public Laptop readBinaryFile(String fileName) throws IOException {
        FileInputStream fis = new FileInputStream(fileName);
        Laptop laptop = Laptop.parseFrom(fis);
        fis.close();
        return  laptop;
    }
    public void writeJsonFile(Laptop laptop, String fileName) throws IOException {
        JsonFormat.Printer printer=JsonFormat.printer()
                .includingDefaultValueFields()
                .preservingProtoFieldNames();

        String jsonString = printer.print(laptop);
        FileOutputStream fos =new FileOutputStream(fileName);
        fos.write(jsonString.getBytes());
        fos.close();
    }

}
```
13. Write Test cases in SerializerTest.java
```
package in.rk.pcbook.serializer;

import in.rk.pcbook.models.Laptop;
import in.rk.pcbook.sample.Generator;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class SerializerTest {


    @Test
    public void writeReadBinaryFile() throws IOException {
        String filename="laptop.bin";
        Laptop laptop1= new Generator().newLaptop();
        Serializer serializer=new Serializer();
        serializer.writeBinaryFile(laptop1,filename);
        Laptop laptop2=serializer.readBinaryFile(filename);

        Assert.assertEquals(laptop1,laptop2);

    }

    @Test
    public void readWriteJsonFile() throws IOException
    {
        Serializer serializer=new Serializer();
        String filename="laptop.bin";
        Laptop laptop2=serializer.readBinaryFile(filename);

        serializer.writeJsonFile(laptop2,"laptop.json");
        Path path= Paths.get("laptop.json");

        Assert.assertEquals(true,Files.isReadable(path));

    }
}
```
	
### Unary gRPC

1. Create ```laptop_service.proto``` and define unary rpc call.

```
syntax="proto3";
package rk.pcbook;
option java_multiple_files=true;
option java_package="in.rk.pcbook.services";
import "laptop_message.proto";

message CreateLaptopRequest
{
  Laptop laptop=1;
}
message CreateLaptopResponse
{
  string id=1;
}
service LaptopService
{
  rpc createLaptop(CreateLaptopRequest) returns (CreateLaptopResponse){};
}
```

2. Create ```service``` package & class ```LaptopService``` extends LaptopServiceGrpc.LaptopServiceImplBase

```
package in.rk.pcbook.service;

import in.rk.pcbook.models.Laptop;
import in.rk.pcbook.service.database.LaptopStore;
import in.rk.pcbook.service.database.exception.AlreadyExistsException;
import in.rk.pcbook.services.CreateLaptopRequest;
import in.rk.pcbook.services.CreateLaptopResponse;
import in.rk.pcbook.services.LaptopServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.UUID;
import java.util.logging.Logger;

public class LaptopService extends LaptopServiceGrpc.LaptopServiceImplBase
{
    private static final Logger log= Logger.getLogger(LaptopService.class.getName());
    private LaptopStore laptopStore;

    public LaptopService(LaptopStore laptopStore) {
        this.laptopStore = laptopStore;
    }

    @Override
    public void createLaptop(CreateLaptopRequest request, StreamObserver<CreateLaptopResponse> responseObserver) {
        Laptop laptop = request.getLaptop();

        String id=laptop.getId();
        log.info("Got create laptop request with ID:"+id);

        UUID uuid;
        if(id.isEmpty())
        {
            uuid=UUID.randomUUID();
        }else
        {
            try {
                uuid = UUID.fromString(id);
            }catch (IllegalArgumentException ie)
            {
                responseObserver.onError(
                        Status.INVALID_ARGUMENT
                                .withDescription(ie.getMessage())
                        .asRuntimeException()
                );
                return;
            }

        }
        Laptop other = laptop.toBuilder().setId(uuid.toString()).build();
        //Save the laptop data in DB(in-memory DB)
        try {
            laptopStore.save(other);
        }catch (AlreadyExistsException e)
        {
            responseObserver.onError(
                    Status.ALREADY_EXISTS
                    .withDescription(e.getMessage())
                    .asRuntimeException()

            );
            return;
        }
        catch (Exception e) {
         responseObserver.onError(
                 Status.INTERNAL
                 .withDescription(e.getMessage())
                 .asRuntimeException()
         );
         return;
        }
        CreateLaptopResponse resp= CreateLaptopResponse.newBuilder()
                .setId(other.getId())
                .build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
        log.info("Saved laptop with ID :"+other.getId());
    }
}

```
3. Create ```LaptopStore``` inteface and its implementation ```InMemoryLaptopStore```

```
package in.rk.pcbook.service.database;

import in.rk.pcbook.models.Laptop;

public interface LaptopStore {
    void save(Laptop laptop) throws Exception;
    Laptop find(String id);
}

```

```
package in.rk.pcbook.service.database;

import in.rk.pcbook.models.Laptop;
import in.rk.pcbook.service.database.exception.AlreadyExistsException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryLaptopStore implements LaptopStore {
    private ConcurrentMap<String, Laptop> data;

    public InMemoryLaptopStore() {
        this.data = new ConcurrentHashMap<>(0);
    }

    @Override
    public void save(Laptop laptop) throws Exception {
        if (data.containsKey(laptop.getId()))
            throw new AlreadyExistsException("Laptop id already exist");

        //deep copy
        Laptop other = laptop.toBuilder().build();
        data.put(other.getId(), other);

    }

    @Override
    public Laptop find(String id) {
        if(!data.containsKey(id))
        {
            return null;
        }
        Laptop other = data.get(id).toBuilder().build();
        return other;
    }
}
```
4. Create ```exception``` package &  ```AlreadyExistsException``` class

```
package in.rk.pcbook.service.database.exception;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}
```

5. Create ```server``` package and ```LaptopServer``` class
```
package in.rk.pcbook.server;

import in.rk.pcbook.service.LaptopService;
import in.rk.pcbook.service.database.InMemoryLaptopStore;
import in.rk.pcbook.service.database.LaptopStore;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class LaptopServer {
    private static final Logger log= Logger.getLogger(LaptopServer.class.getName());

    private final int port;
    private final Server server;
    public LaptopServer(int port, LaptopStore store) {
    this(ServerBuilder.forPort(port), port, store);
    }

    public LaptopServer(ServerBuilder serverBuilder, int port, LaptopStore laptopStore)
    {
        this.port=port;
        LaptopService laptopService = new LaptopService(laptopStore);
        server=serverBuilder.addService(laptopService).build();
    }

    public void start() throws IOException {
        server.start();
        log.info("Server is started on port :"+port);

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                System.err.println("shout down gRPC server as JVM shout down");
                try
                {
                    LaptopServer.this.stop();
                }catch(InterruptedException e)
                {
                    e.printStackTrace(System.err);
                }
                System.out.println("Server shout down");
            }
        });
    }


    public void  stop() throws InterruptedException {
        if(server!=null)
        {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS) ;
        }
    }
    private void blockUntilShutdown() throws InterruptedException
    {
        if(server!=null)
            server.awaitTermination();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        InMemoryLaptopStore store=new InMemoryLaptopStore();
        LaptopServer server=new LaptopServer(8089,store);
        server.start();
        server.blockUntilShutdown();
    }
}
```
6. Create ```client``` package and ```LaptopClient``` class
```
package in.rk.pcbook.client;

import in.rk.pcbook.models.Laptop;
import in.rk.pcbook.sample.Generator;
import in.rk.pcbook.service.database.exception.AlreadyExistsException;
import in.rk.pcbook.services.CreateLaptopRequest;
import in.rk.pcbook.services.CreateLaptopResponse;
import in.rk.pcbook.services.LaptopServiceGrpc;
import in.rk.pcbook.services.LaptopServiceGrpc.LaptopServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LaptopClient {

    private static final Logger log = Logger.getLogger(LaptopClient.class.getName());
    private final ManagedChannel channel;
    private final LaptopServiceBlockingStub blockingStub;

    public LaptopClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = LaptopServiceGrpc.newBlockingStub(channel);
    }

    public void shoutdown() throws InterruptedException {
        channel.awaitTermination(5, TimeUnit.SECONDS);
    }

    public void createLaptop(Laptop laptop) {
        CreateLaptopRequest req = CreateLaptopRequest.newBuilder().setLaptop(laptop).build();
        CreateLaptopResponse resp = CreateLaptopResponse.getDefaultInstance();
        try {
            resp = blockingStub.createLaptop(req);
        }catch(StatusRuntimeException e)
        {
            if(e.getStatus().getCode() == Status.Code.ALREADY_EXISTS)
            {
                log.info("Laptop Id is already exist.");
                return;
            }
            log.log(Level.SEVERE, "Request failed:" + e.getMessage());
            return;
        }
        catch (Exception e) {
            log.log(Level.SEVERE, "Request failed:" + e.getMessage());
            return;
        }
        log.info("Laptop is created with id:" + resp.getId());
    }

    public static void main(String[] args) throws InterruptedException {
        LaptopClient laptopClient = new LaptopClient("0.0.0.0", 8089);
        Generator generator = new Generator();
        //Laptop laptop = generator.newLaptop();
        //Laptop laptop = generator.newLaptop().toBuilder().setId("").build();
        Laptop laptop = generator.newLaptop().toBuilder().setId("be6049df-f803-4513-bd6a-100d6ec0f56e").build();


        try {
            laptopClient.createLaptop(laptop);
        }catch(Exception e)
        {
            laptopClient.shoutdown();
        }


    }
}

```
### Handle Request Time out

- Add sleep 6 seconds in LaptopService.createLaptop(-)
- Add resp = blockingStub```.withDeadlineAfter(5,TimeUnit.SECONDS).```createLaptop(req); in LaptopClient
```
        try {
            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
```
- Here, Server side laptop is created but Client side failed.
- To handle this add the below code in LaptopService
```
        try {
            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
```
### Unit Test cases for unary RPC
- Click on LaptopServer class name and Create Test
```
package in.rk.pcbook.server;

import in.rk.pcbook.models.Laptop;
import in.rk.pcbook.sample.Generator;
import in.rk.pcbook.service.database.InMemoryLaptopStore;
import in.rk.pcbook.service.database.LaptopStore;
import in.rk.pcbook.services.CreateLaptopRequest;
import in.rk.pcbook.services.CreateLaptopResponse;
import in.rk.pcbook.services.LaptopServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class LaptopServerTest {

    //@Rule
    //public final GrpcCleanupRule grpcCleanupRule=new GrpcCleanupRule();//automatic graceful shout down channel at end of test case.

    private LaptopStore laptopStore;
    private LaptopServer laptopServer;
    private ManagedChannel managedChannel;


    @Before
    public void setUp() throws Exception {
        String serverName = InProcessServerBuilder.generateName();
        InProcessServerBuilder inProcessServerBuilder = InProcessServerBuilder.forName(serverName).directExecutor();

        laptopStore = new InMemoryLaptopStore();
        laptopServer = new LaptopServer(inProcessServerBuilder, 0, laptopStore);
        laptopServer.start();

        managedChannel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
        //managedChannel=  grpcCleanupRule.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());
    }

    @After
    public void tearDown() throws Exception {
        laptopServer.stop();
    }

    @Test
    public void createLaptopWithValidID()
    {
        Generator generator=new Generator();
        Laptop laptop=generator.newLaptop();
        CreateLaptopRequest req= CreateLaptopRequest.newBuilder().setLaptop(laptop).build();

        LaptopServiceGrpc.LaptopServiceBlockingStub stub=LaptopServiceGrpc.newBlockingStub(managedChannel);
        CreateLaptopResponse resp= stub.createLaptop(req);

        assertNotNull(resp);
        assertEquals(laptop.getId(),resp.getId());

        Laptop found=laptopStore.find(resp.getId());
        assertNotNull(found);
    }
    @Test
    public void createLaptopWithEmptyID()
    {
        Generator generator=new Generator();
        Laptop laptop=generator.newLaptop().toBuilder().setId("").build();
        CreateLaptopRequest req= CreateLaptopRequest.newBuilder().setLaptop(laptop).build();

        LaptopServiceGrpc.LaptopServiceBlockingStub stub=LaptopServiceGrpc.newBlockingStub(managedChannel);
        CreateLaptopResponse resp= stub.createLaptop(req);

        assertNotNull(resp);
        assertFalse(resp.getId().isEmpty());

        Laptop found=laptopStore.find(resp.getId());
        assertNotNull(found);
    }
    @Test(expected = StatusRuntimeException.class)
    public void createLaptopWithInValidID()
    {
        Generator generator=new Generator();
        Laptop laptop=generator.newLaptop().toBuilder().setId("invalid").build();
        CreateLaptopRequest req= CreateLaptopRequest.newBuilder().setLaptop(laptop).build();

        LaptopServiceGrpc.LaptopServiceBlockingStub stub=LaptopServiceGrpc.newBlockingStub(managedChannel);
        CreateLaptopResponse resp= stub.createLaptop(req);
    }

    @Test(expected = StatusRuntimeException.class)
    public void createLaptopAlreadyExisted() throws Exception {
        Generator generator=new Generator();
        Laptop laptop=generator.newLaptop().toBuilder().build();
        laptopStore.save(laptop);

        CreateLaptopRequest req= CreateLaptopRequest.newBuilder().setLaptop(laptop).build();
        LaptopServiceGrpc.LaptopServiceBlockingStub stub=LaptopServiceGrpc.newBlockingStub(managedChannel);
        CreateLaptopResponse resp= stub.createLaptop(req);
    }

}
```

### Server Streaming
1. Create ```filter_message.proto```
```
syntax="proto3";
package rk.pcbook;
option java_multiple_files=true;
option java_package="in.rk.pcbook.models";

import "memory_message.proto";

message Filter
{
  double max_price_usd=1;
  uint32 min_cpu_cores=2;
  double min_cpu_ghz=3;
  Memory min_ram=4;

}
```
2. Update  ```laptop_service.proto``` with below messages and rpc call.
```
message SearchLaptopRequest
{
  Filter filter =1;
}
message SearchLaptopResponse
{
  Laptop laptop=1;
}

service LaptopService
{
   ...
   ...
  //2. Server Streaming
  rpc searchLaptop(SearchLaptopRequest) returns (stream SearchLaptopResponse){};
}
```

3. Do mvn clean install

4. Add new method declaration in ```LaptopStore``` interface
```
    void search(Filter filter, LaptopStream stream);
```
5. Create new Funcational interface ```LaptopStream```
```
package in.rk.pcbook.service.database;

import in.rk.pcbook.models.Laptop;

@FunctionalInterface
public interface LaptopStream {
    void send(Laptop laptop);
}
```

6. Implement search method in ```InMemoryLaptopStore```
```
    @Override
    public void search(Filter filter, LaptopStream stream) {
        for (Map.Entry<String, Laptop> entry : data.entrySet()) {
            Laptop lap = entry.getValue();
            if (isQualified(filter, lap)) {
                stream.send(lap.toBuilder().build());
            }
        }
    }

    private boolean isQualified(Filter filter, Laptop lap) {
        if (lap.getPriceUsd() > filter.getMaxPriceUsd()) {
            return false;
        }
        if (lap.getCpu().getNumberCores() < filter.getMinCpuCores()) {
            return false;
        }
        if (lap.getCpu().getMinGhz() < filter.getMinCpuGhz()) {
            return false;
        }
        if (toBit(lap.getRam()) < toBit(filter.getMinRam())) {
            return false;
        }

        return true;
    }

    private long toBit(Memory ram) {
        long value = ram.getValue();
        switch (ram.getUnit()) {
            case BIT:
                return value;
            case BYTE:
                return value << 3; //1 BYTE = 8 BITS, 2^3
            case KILOBYTE:
                return value << 13;//1 kb = 1024 bytes, 2^10, 1kb=2^10 * 2^3 bits
            case GIGABYTE:
                return value << 23;
            case MEGABYTE:
                return value << 33;
            case TERABYTE:
                return value << 43;
            default:
                return 0;

        }
    }
```
7. Override ```searchLaptop``` method in LaptopService
```
    @Override
    public void searchLaptop(SearchLaptopRequest request, StreamObserver<SearchLaptopResponse> responseObserver) {
        Filter filter = request.getFilter();
        log.info("Got search-laptop with filter:"+filter);
        laptopStore.search(filter, new LaptopStream() {
            @Override
            public void send(Laptop laptop) {
                log.info("Found laptop with ID:"+laptop.getId());
                SearchLaptopResponse resp = SearchLaptopResponse.newBuilder().setLaptop(laptop).build();
                responseObserver.onNext(resp);
            }
        });

        responseObserver.onCompleted();
        log.info("Search laptop is completed");
    }

```
8. Update the LaptopClient
```
package in.rk.pcbook.client;

import in.rk.pcbook.models.Filter;
import in.rk.pcbook.models.Laptop;
import in.rk.pcbook.models.Memory;
import in.rk.pcbook.sample.Generator;
import in.rk.pcbook.services.*;
import in.rk.pcbook.services.LaptopServiceGrpc.LaptopServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LaptopClient {

    private static final Logger log = Logger.getLogger(LaptopClient.class.getName());
    private final ManagedChannel channel;
    private final LaptopServiceBlockingStub blockingStub;

    public LaptopClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        blockingStub = LaptopServiceGrpc.newBlockingStub(channel);
    }

    public void shoutdown() throws InterruptedException {
        channel.awaitTermination(5, TimeUnit.SECONDS);
    }

    public void createLaptop(Laptop laptop) {
        CreateLaptopRequest req = CreateLaptopRequest.newBuilder().setLaptop(laptop).build();
        CreateLaptopResponse resp = CreateLaptopResponse.getDefaultInstance();
        try {
//            resp = blockingStub.createLaptop(req);
            resp = blockingStub.withDeadlineAfter(10, TimeUnit.SECONDS).createLaptop(req);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.ALREADY_EXISTS) {
                log.info("Laptop Id is already exist.");
                return;
            }
            log.log(Level.SEVERE, "Request failed:" + e.getMessage());
            return;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Request failed:" + e.getMessage());
            return;
        }
        log.info("Laptop is created with id:" + resp.getId());
    }

    public static void main(String[] args) throws InterruptedException {
        LaptopClient laptopClient = new LaptopClient("0.0.0.0", 8089);
        Generator generator = new Generator();
        //1.
        laptopClient.unaryCall(laptopClient, generator);
        //2.
        laptopClient.serverStreaming(laptopClient, generator);
    }

    private  void serverStreaming(LaptopClient laptopClient, Generator generator) throws InterruptedException {
        try {
            for (int i = 0; i < 10; i++) {
                Laptop laptop = generator.newLaptop();
                laptopClient.createLaptop(laptop);
            }

            Memory ram= Memory.newBuilder()
                    .setValue(8)
                    .setUnit(Memory.Unit.GIGABYTE)
                    .build();
            Filter filter = Filter.newBuilder()
                    .setMaxPriceUsd(3000)
                    .setMinCpuCores(4)
                    .setMinCpuGhz(2.5)
                    .setMinRam(ram)
                    .build();
            laptopClient.searchLaptop(filter);

        } catch (Exception e) {
            laptopClient.shoutdown();
        }
    }

    private  void searchLaptop(Filter filter) {
        log.info("Start started");
        SearchLaptopRequest req= SearchLaptopRequest.newBuilder().setFilter(filter).build();
        Iterator<SearchLaptopResponse> respItr = blockingStub.searchLaptop(req);
        while(respItr.hasNext())
        {
            SearchLaptopResponse next = respItr.next();
            Laptop eachLap=next.getLaptop();
            log.info("Each laptop id:"+eachLap.getId());
        }
        log.info("Search is completed");
    }

    private  void unaryCall(LaptopClient laptopClient, Generator generator) throws InterruptedException {
        Laptop laptop = generator.newLaptop();
        //Laptop laptop = generator.newLaptop().toBuilder().setId("").build();
        //Laptop laptop = generator.newLaptop().toBuilder().setId("be6049df-f803-4513-bd6a-100d6ec0f56e").build();
        //Laptop laptop = generator.newLaptop().toBuilder().setId("invalid").build();

        try {
            laptopClient.createLaptop(laptop);
        } catch (Exception e) {
            laptopClient.shoutdown();
        }
    }
}

```
9.Test it by running server & client
10. Keep sleep logic in 