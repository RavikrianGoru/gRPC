package in.rk.bank.client.interceptor.context;

import io.grpc.CallCredentials;
import io.grpc.Metadata;

import java.util.concurrent.Executor;

public class UserSessionToken extends CallCredentials {
    private String jwt;
    public UserSessionToken(String jwt)
    {
        this.jwt=jwt;
    }

    @Override
    public void applyRequestMetadata(RequestInfo requestInfo, Executor appExecutor, MetadataApplier applier) {
        appExecutor.execute(()->{
            Metadata metadata=new Metadata();
            metadata.put(ClientConstants.USER_TOKEN,this.jwt);
            applier.apply(metadata);
        });
    }

    @Override
    public void thisUsesUnstableApi() {
        //may change in future
    }
}
