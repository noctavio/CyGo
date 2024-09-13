package coms309;
public class Item {
    private String name;
    private Long id;
    private double price;

    public Item (String name, Long itemID, double itemPrice) {
        this.name = name;
        this.id = itemID;
        this.price = itemPrice;
    }

    public String getItemName() {
        return name;
    }

    public Long getItemID() {
        return id;
    }

    public double getItemPrice() {
        return price;
    }
}