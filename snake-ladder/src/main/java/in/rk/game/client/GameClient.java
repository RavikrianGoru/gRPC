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
