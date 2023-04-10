package in.rk.bank.client.loadbalancing.clientside.serv_reg;

import io.grpc.NameResolver;

public class TempNameResolver  extends NameResolver {
    private final String service;

    public TempNameResolver(String service) {
        this.service = service;
    }

    @Override
    public String getServiceAuthority() {
        return "temp";
    }

    @Override
    public void start(Listener2 listener) {
        System.out.println(this.getClass().getName()+"start.......");
        ResolutionResult resolutionResult = ResolutionResult
                .newBuilder()
                .setAddresses(ServiceRegistry.getYnstance(this.service))
                .build();
        listener.onResult(resolutionResult);
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void refresh() {
        System.out.println(this.getClass().getName()+"refresh.......");
        super.refresh();
    }
}
