package in.rk.protobuf;

import in.rk.models.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TelevisionVersionCompatibilityDemo {
    public static void main(String[] args) throws IOException {
        Television1 t1= Television1.newBuilder()
                .setBrand("Sony")
                .setSize(22)
                .setMake(2000)
                .build();
        //ser
        Path path= Paths.get("t_v1.ser");
        Files.write(path,t1.toByteArray());
        //deser
        byte[] bytes = Files.readAllBytes(path);
        Television1 t11=Television1.parseFrom(bytes);
        System.out.println("//v1- brand(string),size(int32),make(int32)");
        System.out.println("Television1 v1:"+t11);


        System.out.println("//v2- brand(string),size(int32),made(int32)");
        System.out.println("//renamed make-->made");
        Television2 t12=Television2.parseFrom(bytes);
        System.out.println("Television2 v2:"+t12);

        System.out.println("//v3- brand(string),size(int32)");
        System.out.println("//remove make");
        Television3 t13= Television3.parseFrom(bytes);
        System.out.println("Television3 v3:"+t13);

        System.out.println("//v4- brand(string),size(int32),make(int32),price(int32)");
        System.out.println("//added price");
        Television4 t14= Television4.parseFrom(bytes);
        System.out.println("Television4 v4:"+t14);

        System.out.println("//v5- brand(string),size(string),make(int32)");
        System.out.println("// type change for size(int32 to string)");// incompatable types but it gives output 2:22 unable to map size.
        Television5 t15= Television5.parseFrom(bytes);
        System.out.println("Television5 v5:"+t15);


    }
}
