package in.rk.pcbook.service.database;

import in.rk.pcbook.models.Filter;
import in.rk.pcbook.models.Laptop;
import in.rk.pcbook.models.Memory;
import in.rk.pcbook.service.database.exception.AlreadyExistsException;
import io.grpc.Context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InMemoryLaptopStore implements LaptopStore {
    private ConcurrentMap<String, Laptop> data;
    private static final Logger log = Logger.getLogger(InMemoryLaptopStore.class.getName());

    public InMemoryLaptopStore() {
        this.data = new ConcurrentHashMap<>(0);
    }

    @Override
    public void save(Laptop laptop) throws Exception {
        if (data.containsKey(laptop.getId()))
            throw new AlreadyExistsException("Laptop id already exist");

        //deep copy
        Laptop other = laptop.toBuilder().build();
        data.put(other.getId(), other);

    }

    @Override
    public Laptop find(String id) {
        if (!data.containsKey(id)) {
            return null;
        }
        Laptop other = data.get(id).toBuilder().build();
        return other;
    }

    @Override
    public void search(Context ctx, Filter filter, LaptopStream stream) {
        for (Map.Entry<String, Laptop> entry : data.entrySet()) {
            if (ctx.isCancelled()) {
                log.info("Context is cancelled");
                return;
            }
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Laptop lap = entry.getValue();
            if (isQualified(filter, lap)) {
                stream.send(lap.toBuilder().build());
            }
        }
    }

    private boolean isQualified(Filter filter, Laptop lap) {
        if (lap.getPriceUsd() > filter.getMaxPriceUsd()) {
            return false;
        }
        if (lap.getCpu().getNumberCores() < filter.getMinCpuCores()) {
            return false;
        }
        if (lap.getCpu().getMinGhz() < filter.getMinCpuGhz()) {
            return false;
        }
        if (toBit(lap.getRam()) < toBit(filter.getMinRam())) {
            return false;
        }

        return true;
    }

    private long toBit(Memory ram) {
        long value = ram.getValue();
        switch (ram.getUnit()) {
            case BIT:
                return value;
            case BYTE:
                return value << 3; //1 BYTE = 8 BITS, 2^3
            case KILOBYTE:
                return value << 13;//1 kb = 1024 bytes, 2^10, 1kb=2^10 * 2^3 bits
            case GIGABYTE:
                return value << 23;
            case MEGABYTE:
                return value << 33;
            case TERABYTE:
                return value << 43;
            default:
                return 0;

        }
    }
}
