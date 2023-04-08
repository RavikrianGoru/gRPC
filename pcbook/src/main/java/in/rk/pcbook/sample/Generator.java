package in.rk.pcbook.sample;

import com.google.protobuf.Timestamp;
import in.rk.pcbook.models.*;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

public class Generator {

    private final Random rand;

    public Generator() {
        this.rand = new Random();
    }

    public Keyboard newKeyboard() {
        return Keyboard.newBuilder()
                .setLayout(randomKeyLayout())
                .setBacklit(rand.nextBoolean())
                .build();
    }

    public CPU newCPU() {
        String brand = randomCPUBrand();
        String name = randomCPUName(brand);
        int numberCores = randomInt(2, 8);
        int numberThreads = randomInt(numberCores, 12);
        double minGhz = randomDouble(2.0, 3.5);
        double maxGhz = randomDouble(minGhz, 5.0);

        return CPU.newBuilder()
                .setName(name)
                .setBrand(brand)
                .setNumberCores(numberCores)
                .setNumberThreads(numberThreads)
                .setMinGhz(minGhz)
                .setMaxGhz(maxGhz)
                .build();
    }
    public GPU newGPU()
    {
        String brand=randGPUBrand();
        String name=randomGPUName(brand);
        double minGhz=randomDouble(1.0,1.5);
        double maxGhz=randomDouble(minGhz, 2.0);
        Memory memory = Memory.newBuilder()
                .setValue(randomInt(2,6))
                .setUnit(Memory.Unit.GIGABYTE)
                .build();
        return GPU.newBuilder()
                .setBrand(brand)
                .setName(name)
                .setMinGhz(minGhz)
                .setMaxGhz(maxGhz)
                .setMemory(memory)
                .build();
    }
    public Memory newRAM()
    {
        return Memory.newBuilder()
                .setValue(randomInt(4,64))
                .setUnit(Memory.Unit.GIGABYTE)
                .build();
    }

    public Storage newSSD()
    {
        Memory memory = Memory.newBuilder()
                .setValue(randomInt(128,1024))
                .setUnit(Memory.Unit.GIGABYTE)
                .build();
        return Storage.newBuilder()
                .setDriver(Storage.Driver.SSD)
                .setMemory(memory)
                .build();
    }
    public Storage newHHD()
    {
        Memory memory = Memory.newBuilder()
                .setValue(randomInt(1,6))
                .setUnit(Memory.Unit.TERABYTE)
                .build();
        return Storage.newBuilder()
                .setDriver(Storage.Driver.HDD)
                .setMemory(memory)
                .build();
    }

    public Screen newScreen()
    {
        int height =randomInt(1080, 4320);
        int width=height* 16/9;
        Screen.Resolution resolution=Screen.Resolution.newBuilder()
                .setHeight(height)
                .setWidth(width)
                .build();
        return Screen.newBuilder()
                .setSizeInche(randFloat(13,17))
                .setResolution(resolution)
                .setPanel(randomScreenPannel())
                .setMultitouch(rand.nextBoolean())
                .build();
    }
    public Laptop newLaptop()
    {
        String brand=randomLaptopBrand();
        String name=randomLaptopName(brand);
        double weight=randomDouble(1.4, 3.5);
        double priceUsd=randomDouble(1500,3500);
        int releaseYear=randomInt( 2015,2019);
        return Laptop.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setBrand(brand)
                .setName(name)
                .setCpu(newCPU())
                .setRam(newRAM())
                .addGpus(newGPU())
                .addStorages(newSSD())
                .addStorages(newHHD())
                .setScreen(newScreen())
                .setKeyboard(newKeyboard())
                .setWeighKg(weight)
                .setPriceUsd(priceUsd)
                .setReleaseYear(releaseYear)
                .setUpdatedAt(timestampNow())
                .build();

    }

    private Timestamp timestampNow() {
        Instant now=Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();
    }

    private String randomLaptopName(String brand) {
        switch (brand) {
            case "Apple":
                return randomStringFromSet("Apple Air", "MackBook Pro");
            case ("Dell"):
                return randomStringFromSet("Inspiron", "XPS", "Vostro");
            default:
                return randomStringFromSet("Thinkpad X1","Thinkpad P1", "G570");
        }

    }

    private String randomLaptopBrand() {
        return randomStringFromSet("Apple", "Dell", "Lenovo");
    }

    private Screen.Panel randomScreenPannel() {
        if(rand.nextBoolean())
            return Screen.Panel.IPS;
        return Screen.Panel.OLED;
    }

    private float randFloat(int min, int max) {
    return min + rand.nextFloat() * (max -min);
    }

    private String randomGPUName(String brand) {
        if(brand=="NVIDIA")
        {
            return randomStringFromSet("RTX 2060", "RTX 2070", "GTX 1070");
        }
        else
        {
            return randomStringFromSet("RX 590", "RX 580", "RX Vega-56");
        }
    }


    private String randGPUBrand() {
        return randomStringFromSet("NVIDIA","AMD");
    }

    private double randomDouble(double min, double max) {
        return min + rand.nextDouble() * (max - min);
    }

    private int randomInt(int min, int max) {
        return min + rand.nextInt(max - min + 1);
    }

    private String randomCPUName(String brand) {
        if (brand == "Intel") {
            return randomStringFromSet("Core i3",
                    "Core i5",
                    "Core i7",
                    "Core i9");
        }else {
            return randomStringFromSet("Ryzen 3",
                    "Ryzen 5",
                    "Ryzen 7");
        }
    }

    private String randomCPUBrand() {
        return randomStringFromSet("Intel", "AMD");
    }

    private String randomStringFromSet(String... a) {
        int n = a.length;
        if (n == 0)
            return "";
        return a[rand.nextInt(n)];
    }

    private Keyboard.Layout randomKeyLayout() {
        switch (rand.nextInt(3)) {
            case 1:
                return Keyboard.Layout.QWERTY;
            case 2:
                return Keyboard.Layout.QWERTZ;
            default:
                return Keyboard.Layout.AZERTY;
        }
    }

    public static void main(String[] args) {
        Generator generator=new Generator();
        Laptop laptop =generator.newLaptop();
        System.out.println(laptop);
    }

}
