package in.rk.bank.client.interceptor.context;

import io.grpc.Metadata;

public class ClientConstants {
    private static final Metadata METADATA=new Metadata();
    public static final Metadata.Key<String> USER_TOKEN=Metadata.Key.of("user-token",Metadata.ASCII_STRING_MARSHALLER);

    static {
        METADATA.put(
                Metadata.Key.of("client-token",Metadata.ASCII_STRING_MARSHALLER),
                "bank-client-secret"
        );
    }
    public static Metadata getClientToken()
    {
        System.out.println("Client token:"+METADATA);
        return METADATA;
    }
}
