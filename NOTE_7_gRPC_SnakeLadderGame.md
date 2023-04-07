# Snake Ladder Game (Bi Directional Streaming)

Rules:
```
Roll Die one number from 1 to 6.
Client (A) & Server (B)'s sartes position 0 and ends position 100.
Server maintains both Client(A) and Server(B)'s positions.
A starts 1st turn
A sends die value to B.
B updates A's position
B rolls the die and update it's position.
B sends GameState (A's & B's Position) once B's turn is over or A wins.
A print the game state.
Game over when A or B reaches to 100.
Snake MAP : 97-78, 95-56, 88-24, 63-18, 48-26, 36-6, 32-10 
Ladder MAP: 1-38, 4-14, 8-30, 21-42, 28-76, 50-67, 71-92, 80-99
```

### gRPC project setup ( IntelliJ )
1) Create new maven module ```snake-ladder``` under gRPC project.

### 1. snake-ladder module
1. Right click on ```gRPC``` project --> New --> Module --> Maven-archtype-quickstart --> ```snake-ladder``` -->next --> Finish
2. Add grpc-protobuf, grpc-stub, grpc-netty-shaded dependencies & plugins in  ```pom.xml```
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
4. Create ```snake-ladder-game-service.proto``` file under src/main/```proto```
```
syntax="proto3";
option  java_multiple_files=true;
option java_package="in.rk.game";

message Die
{
  int32 val=1;
}
message Player
{
  string name=1;
  int32 position=2;
}

message GameState
{
  repeated Player players=1;
}

service SnakeLadderGameService
{
  rpc roll(stream Die) returns (stream GameState);
}
```
5. Run ```mvn clean install``` then check generated files in ```target/generated-sources/protobuf/*``` 
6. Create package ```in.rk.game.service``` & ```GameService``` extends SnakeLadderGameServiceGrpc.SnakeLadderGameServiceImplBase. 
   As clients sends multiple Die requests service has to maintain DieStreamObserver.
   Required SnakeAndLadderMap to hold snake & ladder details.
   
7. Create SnakeAndLadderMap class
```
package in.rk.game.service;

import java.util.HashMap;
import java.util.Map;

public class SnakeAndLadderMap {

    public static final Map<Integer,Integer> MAP=new HashMap<>();

    static {
        //Snake MAP : 97-78, 95-56, 88-24, 63-18, 48-26, 36-6, 32-10
        MAP.put(97,78);
        MAP.put(95,56);
        MAP.put(88,24);
        MAP.put(63,18);
        MAP.put(48,26);
        MAP.put(36,6);
        MAP.put(32,10);
        //Ladder MAP: 1-38, 4-14, 8-30, 21-42, 28-76, 50-67, 71-92, 80-99
        MAP.put(1,38);
        MAP.put(4,14);
        MAP.put(8,30);
        MAP.put(21,42);
        MAP.put(28,76);
        MAP.put(50,67);
        MAP.put(71,92);
        MAP.put(80,99);
    }
    public static int getPosition(int position)
    {
        return MAP.getOrDefault(position,position);
    }
}
```

8. Create DieStreamObserver with A, B and StreamObserver<GameState> as instance variables.
```
package in.rk.game.service.streamobserver;

import in.rk.game.Die;
import in.rk.game.GameState;
import in.rk.game.Player;
import in.rk.game.service.SnakeAndLadderMap;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.ThreadLocalRandom;

public class DieStreamObserver implements StreamObserver<Die> {

    StreamObserver<GameState> gameStateStreamObserver;
    Player A;
    Player B;

    public DieStreamObserver(StreamObserver<GameState> gameStateStreamObserver, Player a, Player b) {
        this.gameStateStreamObserver = gameStateStreamObserver;
        this.A = a;
        this.B = b;
    }

    @Override
    public void onNext(Die die) {
        this.A = updatePlayerPosition(this.A, die.getVal());
        if (this.A.getPosition() != 100) {
            this.B = this.updatePlayerPosition(this.B, ThreadLocalRandom.current().nextInt(1, 7));
        }
        this.gameStateStreamObserver.onNext(this.getGameState());
    }

    @Override
    public void onError(Throwable t) {
        System.err.println("Error in DieSreamObserver" + t);
    }

    @Override
    public void onCompleted() {
        this.gameStateStreamObserver.onCompleted();
    }

    private Player updatePlayerPosition(Player player, int dieVal) {
        int position = player.getPosition() + dieVal;
        if (position <= 100) {
            position = SnakeAndLadderMap.getPosition(position);
            player = player.toBuilder().setPosition(position).build();
        }
        return player;
    }

    private GameState getGameState() {
        return GameState.newBuilder().addPlayers(this.A).addPlayers(this.B).build();
    }
}
```

9. Create GameService by extending SnakeLadderGameServiceGrpc.SnakeLadderGameServiceImplBase
```
package in.rk.game.service;

import in.rk.game.Die;
import in.rk.game.GameState;
import in.rk.game.Player;
import in.rk.game.SnakeLadderGameServiceGrpc;
import in.rk.game.service.streamobserver.DieStreamObserver;
import io.grpc.stub.StreamObserver;

public class GameService extends SnakeLadderGameServiceGrpc.SnakeLadderGameServiceImplBase {
    @Override
    public StreamObserver<Die> roll(StreamObserver<GameState> responseObserver) {
        Player A = Player.newBuilder().setPosition(0).setName("A").build();
        Player B = Player.newBuilder().setPosition(0).setName("B").build();
        return new DieStreamObserver(responseObserver, A, B);
    }
}
```
10. Create package ```in.rk.game.server``` & create new class GameServer
```
package in.rk.game.server;

import in.rk.game.service.GameService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GameServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server= ServerBuilder.forPort(6565)
                .addService(new GameService())
                .build();

        server.start();
        System.out.println("Game Server started!");
        server.awaitTermination();;
    }
}
```
11. Create package ```in.rk.game.client``` and create ```GameClient``` class
    Client has to maintain GameState in StreamObserver 
    Required to create ```GameStateStreamObserver``` implements StreamObserver<GameState>
12. Create GameStateStreamObserver
```
package in.rk.game.client.streamobserver;

import com.google.common.util.concurrent.Uninterruptibles;
import in.rk.game.Die;
import in.rk.game.GameState;
import in.rk.game.Player;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class GameStateStreamObserver implements StreamObserver<GameState> {

    CountDownLatch latch;
    StreamObserver<Die> dieStreamObserver;

    public GameStateStreamObserver(CountDownLatch latch) {
        this.latch = latch;
        this.dieStreamObserver = dieStreamObserver;
    }

    public void setDieStreamObserver(StreamObserver<Die> dieStreamObserver) {
        this.dieStreamObserver = dieStreamObserver;
    }

    @Override
    public void onNext(GameState state) {
        List<Player> players = state.getPlayersList();
        players.forEach(x -> System.out.println(x.getName() + ":" + x.getPosition()));

        boolean isGameOver = players.stream().anyMatch(x -> x.getPosition() == 100);
        if (isGameOver) {
            System.out.println("Game over!");
            this.dieStreamObserver.onCompleted();
        } else {
            Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
            this.rollAgain();
        }
        System.out.println("-------");
    }

    public void rollAgain() {
        int dieValue = ThreadLocalRandom.current().nextInt(1, 7);
        Die die = Die.newBuilder().setVal(dieValue).build();
        dieStreamObserver.onNext(die);
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("Client Error:" + t);
        this.latch.countDown();
    }

    @Override
    public void onCompleted() {
        this.latch.countDown();

    }
}

```
13. Create GameClient 
```
package in.rk.game.client;

import in.rk.game.Die;
import in.rk.game.SnakeLadderGameServiceGrpc;
import in.rk.game.client.streamobserver.GameStateStreamObserver;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.stub.StreamObservers;

import java.util.concurrent.CountDownLatch;

public class GameClient {
    public static void main(String[] args) throws InterruptedException {

        ManagedChannel managedChannel = ManagedChannelBuilder
                        .forAddress("localhost", 6565)
                        .usePlaintext()
                        .build();
        SnakeLadderGameServiceGrpc.SnakeLadderGameServiceStub stub=SnakeLadderGameServiceGrpc.newStub(managedChannel);


        CountDownLatch latch =new CountDownLatch(1);
        GameStateStreamObserver gameStateStreamObserver=new GameStateStreamObserver(latch);
        StreamObserver<Die> dieStreamObservers=stub.roll(gameStateStreamObserver);
        gameStateStreamObserver.setDieStreamObserver(dieStreamObservers);
        gameStateStreamObserver.rollAgain();
        latch.await();


    }
}

```
14. Run ```mvn clean install```

### Testing-1 : bank-client
```
1. start Server:  GameServer:main(-)
2. Send request from Client: GameClient:main(-)

check console output.
```
### Testing-2 : Postman client
```
1. Start Server: GameServer:main(-)
2. Open Postman Desktop
Sign in with gmail--> Collection-->new-->gRPC Request--> 
Enter Server URL: localhost:6565
select method: Import a .proto file [select snake-ladder-game-services.proto file]
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
--> + Import Proto: snake-ladder-game-services.proto file
--> Env: localhost:6565 [Unary call] 
Editor: Update the request then click on Play button for response.
```

