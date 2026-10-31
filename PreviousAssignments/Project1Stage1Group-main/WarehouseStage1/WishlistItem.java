import java.io.*;

/**
 * Represents a product and requested quantity
 * stored in a client's wishlist.
 */
public class WishlistItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Product product;
    private int quantity;

    /**
     * Creates a WishlistItem.
     *
     * @param product the product
     * @param quantity the desired quantity
     */
    public WishlistItem(Product product,
                        int quantity) {

        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Changes the requested quantity.
     *
     * @param quantity the new quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns the product.
     *
     * @return Product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Returns the requested quantity.
     *
     * @return quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Returns wishlist item information.
     *
     * @return wishlist item information
     */
    public String toString() {

        return product.getDetails()
                + ", Wishlist Quantity: "
                + quantity;
    }
}
