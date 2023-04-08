package in.rk.pcbook.service.database;

import in.rk.pcbook.models.Filter;
import in.rk.pcbook.models.Laptop;
import io.grpc.Context;

public interface LaptopStore {
    void save(Laptop laptop) throws Exception;
    Laptop find(String id);
    void search(Context contex, Filter filter, LaptopStream stream);
}

