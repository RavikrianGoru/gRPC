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