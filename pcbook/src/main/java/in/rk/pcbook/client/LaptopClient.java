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
        System.out.println("=========");
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
        try {
            Iterator<SearchLaptopResponse> respItr = blockingStub.withDeadlineAfter(5, TimeUnit.SECONDS).searchLaptop(req);
            while (respItr.hasNext()) {
                SearchLaptopResponse next = respItr.next();
                Laptop eachLap = next.getLaptop();
                log.info("Each laptop id:" + eachLap.getId());
            }
        }catch(Exception e)
        {
            log.log(Level.SEVERE,"Request Failed"+e.getMessage());
            return;
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
