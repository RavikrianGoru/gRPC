package in.rk.pcbook.service.database;

import in.rk.pcbook.models.Laptop;
import in.rk.pcbook.service.database.exception.AlreadyExistsException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryLaptopStore implements LaptopStore {
    private ConcurrentMap<String, Laptop> data;

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
        if(!data.containsKey(id))
        {
            return null;
        }
        Laptop other = data.get(id).toBuilder().build();
        return other;
    }
}
