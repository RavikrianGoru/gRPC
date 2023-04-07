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
