package in.rk.pcbook.serializer;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import in.rk.pcbook.models.Laptop;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Serializer {
    public void writeBinaryFile(Laptop laptop, String fileName) throws IOException {
        FileOutputStream fos =new FileOutputStream(fileName);
        laptop.writeTo(fos);
        fos.close();
    }

    public Laptop readBinaryFile(String fileName) throws IOException {
        FileInputStream fis = new FileInputStream(fileName);
        Laptop laptop = Laptop.parseFrom(fis);
        fis.close();
        return  laptop;
    }
    public void writeJsonFile(Laptop laptop, String fileName) throws IOException {
        JsonFormat.Printer printer=JsonFormat.printer()
                .includingDefaultValueFields()
                .preservingProtoFieldNames();

        String jsonString = printer.print(laptop);
        FileOutputStream fos =new FileOutputStream(fileName);
        fos.write(jsonString.getBytes());
        fos.close();
    }

}

