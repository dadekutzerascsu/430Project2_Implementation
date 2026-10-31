/**
 * Driver used to test the Product class.
 */
public class ProductTest {

    public static void main(String[] args) {

        Product p1 =
                new Product(1, "P1", 10, 1.00);

        Product p2 =
                new Product(2, "P2", 20, 2.00);

        Product p3 =
                new Product(3, "P3", 30, 3.00);

        System.out.println("Product Test");
        System.out.println("------------");

        System.out.println(p1);
        System.out.println(
                "should show P1, quantity 10, price $1.00");

        System.out.println(p2);
        System.out.println(
                "should show P2, quantity 20, price $2.00");

        System.out.println(p3);
        System.out.println(
                "should show P3, quantity 30, price $3.00");
    }
}
