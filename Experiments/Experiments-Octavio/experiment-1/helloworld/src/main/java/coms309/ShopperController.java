package coms309;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/shoppers")
public class ShopperController {
    private HashMap<String, Shopper> shopperList = new HashMap<>();
    private HashMap<String, Item> itemList = new HashMap<>();
    private HashMap<String, Cart> shopperCarts = new HashMap<>();


    // Create a shopper
    @PostMapping
    public String createShopper(@RequestBody Shopper shopper, Cart cart) {
        shopperList.put(shopper.getMemberID(), shopper);
        shopperCarts.put(shopper.getMemberID(), cart);
        return "New shopper " + shopper.getFirstName() + " saved.";
    }

    // Display all of a specific Shopper's cart items
    @GetMapping("/{memberID}/cart")
    public List<Item> getCart(@PathVariable String memberID) {
        Cart cart = shopperCarts.get(memberID);
        if (cart != null) {
            return cart.getItems();
        } else {
            throw new RuntimeException("Cart not found for shopper ID " + memberID);
        }
    }

    // Get item price
    @GetMapping("/items/{itemName}/price")
    public double getItemPrice(@PathVariable String itemName) {
        Item item = itemList.get(itemName);
        if (item != null) {
            return item.getItemPrice();
        } else {
            throw new RuntimeException("Item not found.");
        }
    }

    // Display all items available
    @GetMapping("/items")
    public List<Item> getAllItems() {
        return new ArrayList<>(itemList.values());
    }

    // Update shopper's cart
    @PutMapping("/{memberID}/cart")
    public String updateCart(@PathVariable String memberID, @RequestBody List<Item> items) {
        Cart cart = shopperCarts.get(memberID);
        if (cart != null) {
            cart.getItems().clear();
            for (Item item : items) {
                cart.addItem(item);
            }
            return "Cart updated for shopper " + memberID;
        } else {
            throw new RuntimeException("Cart not found for shopper ID " + memberID);
        }
    }
    // Adds an item to shopping cart
    @PostMapping("/{memberID}/cart/items")
    public String addItemToCart(@PathVariable String memberID, @RequestBody Item item) {
        Cart cart = shopperCarts.get(memberID);
        if (cart != null) {
            cart.addItem(item);
            return "Item " + item.getItemName() + " added to cart of shopper " + memberID;
        } else {
            // Debugging message
            System.out.println("Shopper ID: " + memberID + " not found in shopperCarts.");
            throw new RuntimeException("Cart not found for shopper ID " + memberID);
        }
    }

    // Delete an item from a shopper's cart
    @DeleteMapping("/{memberID}/cart/items/{itemName}")
    public String deleteItemFromCart(@PathVariable String memberID, @PathVariable String itemName) {
        Shopper shopper = shopperList.get(memberID);
        if (shopper != null) {
            Cart cart = shopper.getShoppingCart();
            Item item = itemList.get(itemName);
            if (item != null) {
                cart.removeItem(item);
                return "Item " + itemName + " removed from cart.";
            } else {
                throw new RuntimeException("Item not found.");
            }
        } else {
            throw new RuntimeException("Shopper not found.");
        }
    }

    // Creates an available item
    @PostMapping("/items")
    public String addItem(@RequestBody Item item) {
        if (item != null && item.getItemName() != null && item.getItemPrice() > 0) {
            itemList.put(item.getItemName(), item);
            return "Item " + item.getItemName() + " added with price " + item.getItemPrice() + ".";
        } else {
            return "Item could not be added. Ensure 'name' is provided and 'price' is greater than 0.";
        }
    }
}