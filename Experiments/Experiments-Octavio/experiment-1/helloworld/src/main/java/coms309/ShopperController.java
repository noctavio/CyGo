package coms309;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/shoppers")
public class ShopperController {

    // Store shoppers in a simple map
    private Map<String, Shopper> shoppers = new HashMap<>();

    // Creates new shopper
    @PostMapping("/create")
    public String createShopper(@RequestParam String firstname, @RequestParam String lastname, @RequestParam String memberID) {

        Shopper newShopper = new Shopper(firstname, lastname, memberID);
        shoppers.put(memberID, newShopper);
        return "Shopper " + firstname + " " + lastname + " created!";
    }

    // Get shopper details
    @GetMapping("/{memberID}")
    public Shopper getShopper(@PathVariable String memberID) {
        return shoppers.get(memberID);
    }

    // Adds an item to cart
    @PostMapping("/{memberID}/cart/add")
    public String addItemToCart(@PathVariable String memberID, @RequestParam String itemName) {
        Shopper shopper = shoppers.get(memberID);
        if (shopper != null) {
            shopper.getShoppingCart().addItem(itemName);
            return "Added item " + itemName + " to " + shopper.getFirstName() + " " + shopper.getLastName() + "'s cart.\n Your total is" + shopper.getShoppingCart().getCurrentTotal();
        }
        return "Shopper not found!";
    }

    // Remove an item from cart
    @PostMapping("/{memberID}/cart/remove")
    public String removeItemFromCart(@PathVariable String memberID, @RequestParam String itemName) {
        Shopper shopper = shoppers.get(memberID);
        if (shopper != null) {
            Cart cart = shopper.getShoppingCart();
            Item itemToRemove = cart.getItems().stream().filter(i -> i.getItemID().equals(itemID)).findFirst().orElse(null);
            if (itemToRemove != null) {
                cart.removeItem(itemToRemove);
                return "Removed item " + itemToRemove.getItemName() + " from " +  shopper.getFirstName() + " " + shopper.getLastName() + "'s cart. \n Your total is"  shopper.getShoppingCart().getCurrentTotal();
            }
            return "Item not found in cart!";
        }
        return "Shopper not found!";
    }

    // View cart contents
    @GetMapping("/{memberID}/cart")
    public Cart getCart(@PathVariable String memberID) {
        Shopper shopper = shoppers.get(memberID);
        if (shopper != null) {
            return shopper.getShoppingCart();
        }
        return "Shopper not found!";
    }

    //get list of ALL items
    //purchase items(person has infinite money)

}