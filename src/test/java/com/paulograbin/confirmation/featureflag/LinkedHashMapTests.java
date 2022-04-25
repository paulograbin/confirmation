package com.paulograbin.confirmation.featureflag;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

class ProductStatistics {

    private final long quantity;
    private final long amount;
    private final String image;

    public ProductStatistics(long quantity, long amount, String image) {
        this.quantity = quantity;
        this.amount = amount;
        this.image = image;
    }

    @Override
    public String toString() {
        return "ProductStatistics{" +
                "quantity=" + quantity +
                ", amount=" + amount +
                ", image='" + image + '\'' +
                '}';
    }
}

//I WAS EXPECTING THIS LINKEDMAP TO ALLOW ME TO INCLUDE MULTIPLE VALUES UNDER THE SAME KEY, BUT THAT IS NOT THE CASE
//SO FOR MY CURRENT USE IN PENTLAND IT WILL NOT BE THE WAY TO GO

public class LinkedHashMapTests {

    @Test
    void name() {
        Map<String, ProductStatistics> map = new LinkedHashMap<>(5);

        String ean1 = "aaaaa";
        ProductStatistics p1 = new ProductStatistics(1, 1, "imageAAAA - 1");
        ProductStatistics p2 = new ProductStatistics(1, 2, "imageAAAA - 2");

        map.put(ean1, p1);
        map.put(ean1, p2);

        map.forEach((s, productStatistics) -> System.out.println(s + " " + productStatistics));
    }
}
