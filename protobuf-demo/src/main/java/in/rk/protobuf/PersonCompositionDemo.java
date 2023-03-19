package in.rk.protobuf;

import in.rk.models.Address;
import in.rk.models.BodyStyle;
import in.rk.models.Car;
import in.rk.models.Person;

import java.util.ArrayList;

public class PersonCompositionDemo {
    public static void main(String[] args) {

        Address addr1= Address.newBuilder()
                .setPostbox(23)
                .setStret("Butchaiah thota")
                .setCity("Guntur")
                .build();
        //System.out.println(addr1);
        Car car1= Car.newBuilder()
                .setMake("Toyota")
                .setModel("M32")
                .setYear(1999)
                .setBodyStyle(BodyStyle.COUPE)
                .build();
        //System.out.println(car1);
        Car car2=Car.newBuilder()
                .setMake("Tomato")
                .setModel("M35")
                .setYear(2000)
                .setBodyStyle(BodyStyle.SEDAN)
                .build();
        //System.out.println(car2);
        ArrayList<Car> cars=new ArrayList<>();
        cars.add(car1);
        cars.add(car2);

        Person p1= Person.newBuilder()
                .setName("ravi")
                .setAge(35)
                .setAddr(addr1)
//                .addCar(car1)
//                .addCar(car2)
                .addAllCar(cars)
                .build();
        System.out.println(p1);
    }
}
