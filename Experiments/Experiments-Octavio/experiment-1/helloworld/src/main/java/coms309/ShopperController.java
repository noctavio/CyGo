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

    // Deletes a Shopper
    @DeleteMapping("/{memberID}")
    public String deleteShopper(@PathVariable String memberID) {
        Shopper removedShopper = shopperList.remove(memberID);
        if (removedShopper != null) {
            shopperCarts.remove(memberID);
            return "Shopper " + memberID + " removed from shopperList.";
        } else {
            throw new RuntimeException("Shopper with ID " + memberID + " not found.");
        }
    }

    // Display all shoppers
    @GetMapping
    public List<Shopper> getAllShoppers() {
        return new ArrayList<>(shopperList.values());
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

    // Display all available items
    @GetMapping("/items")
    public List<Item> getAllItems() {
        return new ArrayList<>(itemList.values());
    }

    // Replace an item in the cart for another
    @PutMapping("/{memberID}/cart/items/{itemName}")
    public String replaceItemInCart(@PathVariable String memberID, @PathVariable String itemName, @RequestBody Item newItem) {
        Cart cart = shopperCarts.get(memberID);
        if (cart != null) {
            cart.getItems().removeIf(item -> item.getItemName().equals(itemName));
            cart.addItem(newItem);
            return "Item " + itemName + " replaced with " + newItem.getItemName() + " in cart of shopper " + memberID;
        } else {
            throw new RuntimeException("Cart not found for shopper ID " + memberID);
        }
    }

    // Add specified item to specified shopper
    @PostMapping("/{memberID}/cart/items")
    public String addItemToCart(@PathVariable String memberID, @RequestBody Item item) {
        // Get the shopper from the HashMap using memberID
        Shopper shopper = shopperList.get(memberID);
        if (shopper != null) {
            Cart cart = shopper.getShoppingCart();
            cart.addItem(item);
            return "Item " + item.getItemName() + " added to cart of shopper " + memberID + ". Total is now " + cart.getCurrentTotal();
        } else {
            System.out.println("Shopper ID: " + memberID + " not found.");
            throw new RuntimeException("Shopper not found for member ID " + memberID);
        }
    }

    // Deletes a specified item from a specified shopper
    @DeleteMapping("/{memberID}/cart/items/{itemName}")
    public String deleteItemFromCart(@PathVariable String memberID, @PathVariable String itemName) {
        // Retrieve the shopper from the shopperList
        Shopper shopper = shopperList.get(memberID);
        if (shopper != null) {
            Cart cart = shopper.getShoppingCart();
            // Find the item in the cart by matching name
            Item itemToRemove = null;
            for (Item item : cart.getItems()) {
                if (item.getItemName().equals(itemName)) {
                    itemToRemove = item;
                    break;
                }
            }
            if (itemToRemove != null) {
                cart.removeItem(itemToRemove);
                return "Item " + itemName + " removed from cart of shopper " + memberID + ". Total is now " + cart.getCurrentTotal();
            } else {
                throw new RuntimeException("Item " + itemName + " not found in cart.");
            }
        } else {
            throw new RuntimeException("Shopper not found for member ID " + memberID);
        }
    }
}