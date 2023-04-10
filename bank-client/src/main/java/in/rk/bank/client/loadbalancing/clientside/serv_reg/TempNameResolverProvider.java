package in.rk.bank.client.loadbalancing.clientside.serv_reg;

import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import java.net.URI;

public class TempNameResolverProvider extends NameResolverProvider
{
    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 5;
    }

    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        System.out.println("looking for service:"+targetUri.getAuthority());
        return new TempNameResolver(targetUri.getAuthority());
    }

    @Override
    public String getDefaultScheme() {
        return "grpc";//return "http";
    }
}
