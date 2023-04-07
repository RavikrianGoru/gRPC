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
