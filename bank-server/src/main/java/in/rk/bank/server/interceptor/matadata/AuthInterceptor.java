package in.rk.bank.server.interceptor.matadata;

import in.rk.bank.service.interceptor.ServerConstants;
import io.grpc.*;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class AuthInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        System.out.println("intercept @serverside AuthInterceptor......");
        String receivedClientToken=headers.get(ServerConstants.TOKEN);



        if(validateToken(receivedClientToken))
        {
            System.out.println("Valid client");
            return next.startCall(call,headers);
        }else
        {
            System.out.println("Invalid token");
            Status status = Status.UNAUTHENTICATED.withDescription("Invalid token");
            call.close(status,headers);
        }
        System.out.println("--...--");
        return new ServerCall.Listener<>(){
        };
    }
    private boolean validateToken(String token)
    {
        //return ThreadLocalRandom.current().nextBoolean();
        return Objects.nonNull(token) && Objects.equals(token,"bank-client-secret" );

    }
}
