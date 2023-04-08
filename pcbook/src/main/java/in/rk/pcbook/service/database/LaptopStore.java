package in.rk.pcbook.service.database;

import in.rk.pcbook.models.Laptop;

public interface LaptopStore {
    void save(Laptop laptop) throws Exception;
    Laptop find(String id);
}
