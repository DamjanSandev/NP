package Napredno.K2.OnlineShop;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

enum COMPARATOR_TYPE {
    NEWEST_FIRST,
    OLDEST_FIRST,
    LOWEST_PRICE_FIRST,
    HIGHEST_PRICE_FIRST,
    MOST_SOLD_FIRST,
    LEAST_SOLD_FIRST
}

class ProductNotFoundException extends Exception {
    ProductNotFoundException(String message) {
        super(message);
    }
}


class Product {
    private final String category;
    private final String id;
    private final String name;
    private final LocalDateTime createdAt;
    private final double price;

    private int quantitySold;

    public Product(String category, String id, String name, LocalDateTime createdAt, double price) {
        this.category = category;
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.price = price;
        this.quantitySold = 0;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getPrice() {
        return price;
    }

    public double buyProduct(int quantitySold) {
        this.quantitySold += quantitySold;
        return price * quantitySold;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", price=" + price +
                ", quantitySold=" + quantitySold +
                '}';
    }
}


class OnlineShop {

    private final Map<String, Product> productsById;


    OnlineShop() {
        productsById = new HashMap<>();
    }

    void addProduct(String category, String id, String name, LocalDateTime createdAt, double price) {
        productsById.put(id, new Product(category, id, name, createdAt, price));
    }

    double buyProduct(String id, int quantity) throws ProductNotFoundException {
        if (!productsById.containsKey(id)) {
            throw new ProductNotFoundException(String.format("Product with id %s does not exist in the online shop!", id));
        }
        return productsById.get(id).buyProduct(quantity);
    }

    List<List<Product>> listProducts(String category, COMPARATOR_TYPE comparatorType, int pageSize) {
        List<Product> products = productsById.values().stream().filter(product -> product.getCategory().equals(category) || category == null)
                .sorted(getComparator(comparatorType))
                .collect(Collectors.toList());

        List<List<Product>> result = new ArrayList<>();
        int start = 0;
        while (start < products.size()) {
            int end = Math.min(start + pageSize, products.size());
            result.add(new ArrayList<>(products.subList(start, end)));
            start += pageSize;
        }
        return result;
    }

    private Comparator<Product> getComparator(COMPARATOR_TYPE comparatorType) {
        if (comparatorType == COMPARATOR_TYPE.NEWEST_FIRST) {
            return Comparator.comparing(Product::getCreatedAt).reversed();
        } else if (comparatorType == COMPARATOR_TYPE.OLDEST_FIRST) {
            return Comparator.comparing(Product::getCreatedAt);
        } else if (comparatorType == COMPARATOR_TYPE.HIGHEST_PRICE_FIRST) {
            return Comparator.comparing(Product::getPrice).reversed();
        } else if (comparatorType == COMPARATOR_TYPE.LOWEST_PRICE_FIRST) {
            return Comparator.comparing(Product::getPrice);
        } else if (comparatorType == COMPARATOR_TYPE.LEAST_SOLD_FIRST) {
            return Comparator.comparing(Product::getQuantitySold);
        }
        return Comparator.comparing(Product::getQuantitySold).reversed();
    }

}

public class OnlineShopTest {

    public static void main(String[] args) {
        OnlineShop onlineShop = new OnlineShop();
        double totalAmount = 0.0;
        Scanner sc = new Scanner(System.in);
        String line;
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String[] parts = line.split("\\s+");
            if (parts[0].equalsIgnoreCase("addproduct")) {
                String category = parts[1];
                String id = parts[2];
                String name = parts[3];
                LocalDateTime createdAt = LocalDateTime.parse(parts[4]);
                double price = Double.parseDouble(parts[5]);
                onlineShop.addProduct(category, id, name, createdAt, price);
            } else if (parts[0].equalsIgnoreCase("buyproduct")) {
                String id = parts[1];
                int quantity = Integer.parseInt(parts[2]);
                try {
                    totalAmount += onlineShop.buyProduct(id, quantity);
                } catch (ProductNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                String category = parts[1];
                if (category.equalsIgnoreCase("null"))
                    category = null;
                String comparatorString = parts[2];
                int pageSize = Integer.parseInt(parts[3]);
                COMPARATOR_TYPE comparatorType = COMPARATOR_TYPE.valueOf(comparatorString);
                printPages(onlineShop.listProducts(category, comparatorType, pageSize));
            }
        }
        System.out.println("Total revenue of the online shop is: " + totalAmount);

    }

    private static void printPages(List<List<Product>> listProducts) {
        for (int i = 0; i < listProducts.size(); i++) {
            System.out.println("PAGE " + (i + 1));
            listProducts.get(i).forEach(System.out::println);
        }
    }
}


