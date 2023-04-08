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
