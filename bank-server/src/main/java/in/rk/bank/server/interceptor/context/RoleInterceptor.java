package in.rk.bank.server.interceptor.context;

import in.rk.bank.service.interceptor.ServerConstants;
import in.rk.bank.service.interceptor.UserRole;
import io.grpc.*;

import java.util.Objects;

public class RoleInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        System.out.println("intercept @serverside AuthInterceptor......");
        String receivedUserToken=headers.get(ServerConstants.USER_TOKEN);


        if(validateToken(receivedUserToken))
        {
            System.out.println("Valid token");
            UserRole userRole = extractUserRole(receivedUserToken);
            Context context = Context.current().withValue(ServerConstants.CTX_USER_ROLE, userRole);
            return Contexts.interceptCall(context, call, headers, next);
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
        return Objects.nonNull(token) && token.startsWith("user-token");

    }
    private UserRole extractUserRole(String receivedUserToken)
    {
        return receivedUserToken.endsWith("prime")?UserRole.PRIME:UserRole.STANDARD;
    }
}
