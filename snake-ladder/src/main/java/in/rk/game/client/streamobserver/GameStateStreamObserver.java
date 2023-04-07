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
