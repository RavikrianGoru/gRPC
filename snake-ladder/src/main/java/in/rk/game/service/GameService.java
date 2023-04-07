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
