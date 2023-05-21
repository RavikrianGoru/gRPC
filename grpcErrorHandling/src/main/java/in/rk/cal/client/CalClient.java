package in.rk.cal.client;

import in.rk.cal.models.ErrorResponse;
import in.rk.cal.models.Request;
import in.rk.cal.services.CalServiceGrpc;
import io.grpc.*;
import io.grpc.protobuf.ProtoUtils;

public class CalClient {
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 6565)
                .usePlaintext()
                .build();

        CalServiceGrpc.CalServiceBlockingStub blockingStub =CalServiceGrpc.newBlockingStub(managedChannel);

        //1 Unary-success
        Request request1= Request.newBuilder().setNumber(2).build();
        getSquare(blockingStub,request1);

        //2 Unary-Failed with Status Codes
        Request request2= Request.newBuilder().setNumber(1).build();
        getSquare(blockingStub,request2);

        //3 Unary-Failed with Metadata
        Request request3= Request.newBuilder().setNumber(3).build();
        getSquare(blockingStub,request3);

        //4 Unary-Failed Response oneof Success or error
        Request request4= Request.newBuilder().setNumber(4).build();
        getSquare(blockingStub,request4);



    }

    private static void getSquare(CalServiceGrpc.CalServiceBlockingStub blockingStub, Request request) {
        try {
            System.out.println("Received Response:" + blockingStub.getSquare(request));
        }catch (StatusRuntimeException e)
        {
            System.err.println("Known Error while getting square:"+e);
            Status status=Status.fromThrowable(e);
            System.out.println(status.getCode()+":"+status.getDescription());
        }catch (Exception e)
        {
            System.err.println("Unknown Error while getting square:"+e);
            Metadata metadata=Status.trailersFromThrowable(e);
            ErrorResponse errorResp=metadata.get(ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance()));
            System.out.println(errorResp);

        }
    }
}
