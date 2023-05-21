package in.rk.bank.client.interceptor.deadline;

import io.grpc.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DeadlineInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        //return next.newCall(method,callOptions);// do nothing
        Deadline deadline = callOptions.getDeadline();
        if(Objects.isNull(deadline))
        {
            callOptions=callOptions.withDeadlineAfter(20, TimeUnit.SECONDS);
        }
        return next.newCall(method,callOptions);
    }
}
