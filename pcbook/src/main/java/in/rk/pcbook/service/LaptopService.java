package in.rk.pcbook.service;

import in.rk.pcbook.models.Laptop;
import in.rk.pcbook.service.database.LaptopStore;
import in.rk.pcbook.service.database.exception.AlreadyExistsException;
import in.rk.pcbook.services.CreateLaptopRequest;
import in.rk.pcbook.services.CreateLaptopResponse;
import in.rk.pcbook.services.LaptopServiceGrpc;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
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
/*
        try {
            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/
        if(Context.current().isCancelled())
        {
            log.info("Request is canceled.");
            responseObserver.onError(
                    Status.CANCELLED
                    .withDescription("Request is cancelled.")
                    .asRuntimeException()
            );
            return;
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
