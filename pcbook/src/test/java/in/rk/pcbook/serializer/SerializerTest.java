package in.rk.pcbook.serializer;

import in.rk.pcbook.models.Laptop;
import in.rk.pcbook.sample.Generator;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class SerializerTest {


    @Test
    public void writeReadBinaryFile() throws IOException {
        String filename="laptop.bin";
        Laptop laptop1= new Generator().newLaptop();
        Serializer serializer=new Serializer();
        serializer.writeBinaryFile(laptop1,filename);
        Laptop laptop2=serializer.readBinaryFile(filename);

        Assert.assertEquals(laptop1,laptop2);

    }

    @Test
    public void readWriteJsonFile() throws IOException
    {
        Serializer serializer=new Serializer();
        String filename="laptop.bin";
        Laptop laptop2=serializer.readBinaryFile(filename);

        serializer.writeJsonFile(laptop2,"laptop.json");
        Path path= Paths.get("laptop.json");

        Assert.assertEquals(true,Files.isReadable(path));

    }
}