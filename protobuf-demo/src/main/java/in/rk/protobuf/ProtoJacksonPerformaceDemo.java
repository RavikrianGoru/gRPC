package in.rk.protobuf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import in.rk.models.Person;
import in.rk.protobuf.models.JPerson;

import java.io.IOException;

public class ProtoJacksonPerformaceDemo {
    public static void main(String[] args) {
        //Jackson: Serialization & Deserialization
        JPerson jp1 = new JPerson();
        jp1.setName("Ravi");
        jp1.setAge(35);

        ObjectMapper mapper =new ObjectMapper();
        Runnable jaksonRunnable=()-> {
            try {
                //serialization
                byte[] bytes = mapper.writeValueAsBytes(jp1);
                //System.out.println(bytes.length);         //24
                //Deserialization
                JPerson jp2=mapper.readValue(bytes,JPerson.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        //Proto: Serialization & Deserialization
        Person p1= Person.newBuilder().setName("Ravi").setAge(35).build();

        Runnable protoRunnable=()->{
            try {
                //serialization
                byte[] bytes = p1.toByteArray();
                //System.out.println(bytes.length);         //8
                //Deserialization
                Person p2 = Person.parseFrom(bytes);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        };

        //Run :PT
        for (int i=1; i<=5;i++)
        {
            runPT(jaksonRunnable,"JACKSON");
            runPT(protoRunnable,"PROTO");
        }

    }

    private static void runPT(Runnable r, String method)
    {
        long startTme=System.currentTimeMillis();
        for(int i=1;i<=1000000;i++)
        {
            r.run();
        }
        long endTime=System.currentTimeMillis();
        System.out.println("Method :"+method +" Total Time:"+(endTime-startTme));
    }
}
