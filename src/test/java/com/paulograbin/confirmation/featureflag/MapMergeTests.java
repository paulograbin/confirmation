package com.paulograbin.confirmation.featureflag;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Product {
    private final String productCode;
    private final int quantity;

    public Product(String productCode, int quantity) {
        this.productCode = productCode;
        this.quantity = quantity;
    }

    public String getProductCode() {
        return productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productCode='" + productCode + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}

public class MapMergeTests {

    @Test
    void name() {
        Product aa = new Product("0000", 3);
        Product ab = new Product("0000", 5);
        Product ac = new Product("0000", 12);
        Product b = new Product("1111", 12);
        Product c = new Product("2222", 5);

        List<Product> productList = List.of(aa, ab, ac, b, c);

        Map<String, Product> map = new HashMap<>();

        productList.stream()
                .forEach(p -> map.merge(p.getProductCode(), p, (product, product2) -> new Product(p.getProductCode(), product.getQuantity() + product2.getQuantity())));


        for (Product product : map.values()) {
            System.out.println(product);
        }

        Assertions.assertThat(map).containsKey("0000");
        Assertions.assertThat(map).containsKey("1111");
        Assertions.assertThat(map).containsKey("2222");
        Assertions.assertThat(map.get("0000").getQuantity()).isEqualTo(20);
        Assertions.assertThat(map.get("1111").getQuantity()).isEqualTo(12);
        Assertions.assertThat(map.get("2222").getQuantity()).isEqualTo(5);
    }
}
