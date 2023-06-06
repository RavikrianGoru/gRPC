package in.rk.cal.service;

import in.rk.cal.models.ErrorCode;
import in.rk.cal.models.ErrorResponse;
import in.rk.cal.models.Request;
import in.rk.cal.models.Response;
import in.rk.cal.services.CalServiceGrpc;
import io.grpc.*;
import io.grpc.protobuf.ProtoUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CalServiceTest {

    private ManagedChannel channel;
    private CalServiceGrpc.CalServiceBlockingStub clientStub;

    @Before
    public void setUp() throws Exception {
        this.channel= ManagedChannelBuilder.forAddress("localhost",6565)
                .usePlaintext()
                .build();
        this.clientStub=CalServiceGrpc.newBlockingStub(channel);
    }

    //@Test
    public void calSquareHappyPath() {
        System.out.println("calSquareHappyPath()---Started");
        Request req= Request.newBuilder().setNumber(2).build();
        Response resp=this.clientStub.getSquare(req);
        System.out.println("Respo:"+resp);
        Assert.assertEquals(resp.getSuccessResponse().getResults(),4);
    }

    //@Test
    public void calSquareErrorWithStatus1() {
        System.out.println("calSquareErrorWithStatus1()---Started");
        Request req= Request.newBuilder().setNumber(1).build();
        try {
            Response resp=this.clientStub.getSquare(req);
            System.out.println("Respo:"+resp);
        }
        catch(StatusRuntimeException e)
        {
            Status status=Status.fromThrowable(e);
            System.out.println(status.getCode()+":"+status.getDescription());
        }
    }
    //@Test(expected = StatusRuntimeException.class)
    public void calSquareErrorWithStatus2() {
        System.out.println("calSquareErrorWithStatus2()---Started");
        Request req= Request.newBuilder().setNumber(1).build();
        Response resp=this.clientStub.getSquare(req);
        System.out.println("Respo:"+resp);
    }

    //@Test
    public void calSquareErrorResponse() {
        System.out.println("calSquareErrorResponse()---Started");
        Request req= Request.newBuilder().setNumber(3).build();

        try
        {
            Response resp=this.clientStub.getSquare(req);
            System.out.println("Respo:"+resp);
        }catch (Exception e)
        {
            Metadata metadata=Status.trailersFromThrowable(e);
            ErrorResponse errorResp=metadata.get(ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance()));
            System.out.println(errorResp);
        }

    }
    //@Test
    public void calSquareOneOfSucsessOrErrorResponse() {
        System.out.println("calSquareHappyPath()---Started");
        Request req= Request.newBuilder().setNumber(4).build();
        Response resp=this.clientStub.getSquare(req);
        System.out.println("Respo:"+resp);
        Assert.assertEquals(resp.getErrorResponse().getErrorCode(), ErrorCode.MUST_NOT_FOUR);
    }


    @After
    public void tearDown() throws Exception {
        this.channel.shutdown();
    }
}