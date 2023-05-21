package in.rk.cal.service;

import in.rk.cal.models.*;
import in.rk.cal.services.CalServiceGrpc;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.StreamObserver;

public class CalService extends CalServiceGrpc.CalServiceImplBase {
    @Override
    public void getSquare(Request request, StreamObserver<Response> responseObserver) {
        int number=request.getNumber();
        if(number==2)
        {
            //success with resp
            SuccessResponse resp = SuccessResponse.newBuilder().setResults(number * number).build();
            responseObserver.onNext(Response.newBuilder().setSuccessResponse(resp).build());
            responseObserver.onCompleted();
        }else if(number==1)
        {
            //failed with Status
            Status status=Status.FAILED_PRECONDITION.withDescription("Invlid number :"+number);
            responseObserver.onError(status.asRuntimeException());
            return;
        }else if(number==3)
        {
            //failed with errorCode(Metadata)
            Metadata metadata=new Metadata();
            Metadata.Key<ErrorResponse> responseKey= ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());

            ErrorCode errorCode = ErrorCode.MUST_NOT_THREE;
            ErrorResponse errorRespo=ErrorResponse.newBuilder().setErrorCode(errorCode)
                    .setInput(number)
                    .build();

            //pass error object via metadata
            metadata.put(responseKey,errorRespo);
            responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException(metadata));
            return;
        }else if(number==4)
        {
            //failed with Reponse(oneof [success or error])
            ErrorCode errorCode = ErrorCode.MUST_NOT_FOUR;
            ErrorResponse errorRespo=ErrorResponse.newBuilder().setErrorCode(errorCode)
                    .setInput(number)
                    .build();

            responseObserver.onNext(Response.newBuilder().setErrorResponse(errorRespo).build());
            responseObserver.onCompleted();
        }
    }
}
