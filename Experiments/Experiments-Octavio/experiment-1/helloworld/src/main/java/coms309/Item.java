package coms309;
import java.util.Random;

public class Item {
    private String name;
    private double price;

    public Item (String name) {
        Random random = new Random();

        double min = 1.0;
        double max = 99.99;
        this.price = min + (max - min) * random.nextDouble();

        this.name = name;
    }
    public String getItemName() {
        return name;
    }

    public double getItemPrice() {
        return price;
    }
}