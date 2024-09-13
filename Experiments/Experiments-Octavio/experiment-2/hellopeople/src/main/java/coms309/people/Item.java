package coms309;
import java.util.Random;
// THIS IS THE RIGHT ONE
public class Item {
    private String name;
    private double price;

    public Item (String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getItemName() {
        return name;
    }

    public void setItemName(String namevar) {
        this.name = namevar;
    }

    public double getItemPrice() {
        return price;
    }
    public void setPrice(double value) {
        this.price = value;
    }
}