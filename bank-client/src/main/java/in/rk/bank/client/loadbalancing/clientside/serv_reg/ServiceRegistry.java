package in.rk.bank.client.loadbalancing.clientside.serv_reg;

import io.grpc.EquivalentAddressGroup;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceRegistry {
    private static  final Map<String, List<EquivalentAddressGroup>> SER_REG_MAP=new HashMap<>();

    public static void register(String service, List<String> instances)
    {
        List<EquivalentAddressGroup> equivalentAddressGroups
                =instances.stream()
                .map(a->a.split(":"))
                .map(a->new InetSocketAddress(a[0],Integer.parseInt(a[1])))
                .map(EquivalentAddressGroup::new)
                .collect(Collectors.toList());
        SER_REG_MAP.put(service,equivalentAddressGroups);
    }

    public static List<EquivalentAddressGroup> getYnstance(String service)
    {
        return SER_REG_MAP.get(service);
    }
}
