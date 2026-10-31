import java.io.*;
import java.math.BigDecimal;

/**
 * Represents a product in the warehouse system.
 */
public class Product implements Serializable {

    private static final long serialVersionUID = 2L;

    private int productID;
    private String name;
    private int quantityInStock;
    private BigDecimal salePrice;

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
                   BigDecimal salePrice) {

        if (productID <= 0 || name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("A positive product ID and name are required.");
        }
        if (quantityInStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
        this.productID = productID;
        this.name = name.trim();
        this.quantityInStock = quantityInStock;
        this.salePrice = Money.positive(salePrice);
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
    public BigDecimal getSalePrice() {
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
                + salePrice.toPlainString();
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
