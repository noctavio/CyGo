package coms309;

public class Shopper {
    private String firstname;
    private String lastname;
    private String memberID;
    private boolean premiumMember;
    private Cart shoppingCart;

    public Shopper(String firstname, String lastname, String memberID) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.memberID = memberID;
        this.shoppingCart = new Cart();
    }

    public String getFirstName() {
        return firstname;
    }

    public String getLastName() {
        return lastname;
    }

    public String getMemberID() {
        return memberID;
    }

    public Cart getShoppingCart() {
        return shoppingCart;
    }