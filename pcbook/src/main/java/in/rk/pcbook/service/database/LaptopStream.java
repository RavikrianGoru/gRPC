package in.rk.pcbook.service.database;

import in.rk.pcbook.models.Laptop;

@FunctionalInterface
public interface LaptopStream {
    void send(Laptop laptop);
}
