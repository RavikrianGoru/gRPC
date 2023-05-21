package in.rk.bank.client.interceptor.metadata;

import io.grpc.Metadata;

public class ClientConstants {
    private static final Metadata METADATA=new Metadata();

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
