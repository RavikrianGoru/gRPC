package in.rk.protobuf;

import in.rk.models.Person;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PersonDemo {
    public static void main(String[] args) throws IOException {
        Person p1= Person.newBuilder()
                .setName("Ravi")
                .setAge(30)
                .build();
        Person p2= Person.newBuilder()
                .setName("Ravi")
                .setAge(30)
                .build();
//        System.out.println(p1.toString() +": Hash code :"+p1.hashCode());
//        System.out.println(p2.toString() +":Hash Code "+p2.hashCode());
//        System.out.println("Equals of p1, p2:"+p1.equals(p2));
//        System.out.println("== of p1, p2:"+(p1==p2));

//Serialize Person
        Path path= Paths.get("person.ser");
        Files.write(path,p1.toByteArray());
//Deserialize Person
        byte[] bytes = Files.readAllBytes(path);
        System.out.println("Deserialized Data:"+Person.parseFrom(bytes));

// Default value
        Person p=Person.newBuilder().build();
        System.out.println("p -->:"+p);
        System.out.println("No Null pointer exception for p.getAddr().getPostbox():"+p.getAddr().getPostbox());
        System.out.println("Check addr is available in p.hasAddr():"+p.hasAddr());
    }
}
