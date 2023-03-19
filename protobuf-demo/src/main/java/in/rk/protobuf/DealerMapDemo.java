package in.rk.protobuf;

import in.rk.models.BodyStyle;
import in.rk.models.Car;
import in.rk.models.CarOrBuilder;
import in.rk.models.Dealer;

public class DealerMapDemo {
    public static void main(String[] args) {
        Car car1= Car.newBuilder()
                .setMake("ToYoTo")
                .setModel("T001")
                .setYear(1999)
                .setBodyStyle(BodyStyle.COUPE)
                .build();
        Car car2= Car.newBuilder()
                .setMake("TAYATAo")
                .setModel("T009")
                .setYear(2000)
                .build();

        Dealer dealer= Dealer.newBuilder()
                .putModel(1999,car1)
                .putModel(2000,car2)
                .build();

        System.out.println("dealer.containsModel(2000) :"+dealer.containsModel(2000));
        System.out.println("dealer.containsModel(2001) :"+dealer.containsModel(2001));
        System.out.println("dealer.getModelCount() :"+dealer.getModelCount());
        System.out.println("dealer.getModelMap(): "+dealer.getModelMap());
        System.out.println(dealer.getModelOrThrow(2000).getBodyStyle());

    }
}
