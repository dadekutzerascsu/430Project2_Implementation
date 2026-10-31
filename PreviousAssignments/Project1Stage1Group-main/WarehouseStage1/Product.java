import java.io.*;

/**
 * Represents a product in the warehouse system.
 */
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    private int productID;
    private String name;
    private int quantityInStock;
    private double salePrice;

    /**
     * Creates a Product.
     *
     * @param productID the unique product ID
     * @param name the product name
     * @param quantityInStock quantity in stock
     * @param salePrice unit sale price
     */
    public Product(int productID,
                   String name,
                   int quantityInStock,
                   double salePrice) {

        this.productID = productID;
        this.name = name;
        this.quantityInStock = quantityInStock;
        this.salePrice = salePrice;
    }

    /**
     * Returns the product ID.
     *
     * @return product ID
     */
    public int getProductID() {
        return productID;
    }

    /**
     * Returns the product name.
     *
     * @return product name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the quantity in stock.
     *
     * @return quantity in stock
     */
    public int getQuantityInStock() {
        return quantityInStock;
    }

    /**
     * Returns the unit sale price.
     *
     * @return sale price
     */
    public double getSalePrice() {
        return salePrice;
    }

    /**
     * Returns the product information.
     *
     * @return product details
     */
    public String getDetails() {

        return "Product ID: " + productID
                + ", Name: " + name
                + ", Quantity In Stock: "
                + quantityInStock
                + ", Sale Price: $"
                + String.format("%.2f", salePrice);
    }

    /**
     * Returns the product information.
     *
     * @return product information
     */
    public String toString() {
        return getDetails();
    }
}
