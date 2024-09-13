package coms309;
import java.util.List;
import java.util.ArrayList;

class Cart {
    private List<Item> cart = new ArrayList<>();
    private double currentTotal;

    public Cart () {
        this.currentTotal = 0.0;
    }

    public List<Item> getItems() {
        return cart;
    }

    public void addItem(Item someItem) {
        cart.add(someItem);
        currentTotal += someItem.getItemPrice();
    }
    public void removeItem(Item someItem) {
        cart.remove(someItem);
        currentTotal -= someItem.getItemPrice();
    }
}