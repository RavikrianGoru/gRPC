package in.rk.protobuf;

import com.google.protobuf.*;
import com.rk.modeles.Student;

import java.nio.charset.Charset;

public class StudentWrapperDemo {
    public static void main(String[] args) {
        Student s= Student.newBuilder()
                .setName(StringValue.newBuilder().setValue("ravi").build())
                .setAddress(BytesValue.newBuilder().setValue(ByteString.copyFrom("Butchaiah thota, gnt", Charset.defaultCharset())).build())
                .setIsStudying(BoolValue.newBuilder().setValue(true).build())
                .setRollNo(Int32Value.newBuilder().setValue(1001).build())
                .setPhoneNo(Int64Value.newBuilder().setValue(9999999999L).build())
                .setFeePaid(FloatValue.newBuilder().setValue(50000.00f).build())
                .setPendingFee(DoubleValue.newBuilder().setValue(20000.00).build())
                .setRank(UInt32Value.newBuilder().setValue(1234567890).buildPartial())
                .setMaxRank(UInt64Value.newBuilder().setValue(9999999999999L).buildPartial())
                .build();

        System.out.println(s);
        if(s.hasName())
            System.out.println(s.getName());
        if(s.hasRank())
            System.out.println(s.getRank());
    }
}
