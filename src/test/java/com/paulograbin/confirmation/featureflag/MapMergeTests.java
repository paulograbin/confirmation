package com.paulograbin.confirmation.featureflag;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;


record Product(String productCode, int quantity) {

    @Override
    public String toString() {
        return "Product{" +
                "productCode='" + productCode + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}

public class MapMergeTests {

    private final BiFunction<Product, Product, Product> COMBINE_PRODUCTS = (product1, product2) -> new Product(product1.productCode(), product1.quantity() + product2.quantity());

    @Test
    void name2() {
        List<Product> productList = getListOne();

        Map<String, Product> map = new HashMap<>();

        productList.stream()
                .forEach(p -> map.merge(p.productCode(), p, COMBINE_PRODUCTS));

        assertThat(map.get("300412426").quantity()).isEqualTo(7*4);
        assertThat(map.get("300613271").quantity()).isEqualTo(4*4);
        assertThat(map.get("300617127").quantity()).isEqualTo(5);
        assertThat(map.get("5053744478128").quantity()).isEqualTo(45);
        assertThat(map.get("5054772120812").quantity()).isEqualTo(65);
    }

    private List<Product> getListOne() {
        List<Product> productList = new ArrayList<>();

        productList.add(new Product("300412426", 7));
        productList.add(new Product("300412426", 7));
        productList.add(new Product("300412426", 7));
        productList.add(new Product("300412426", 7));
        productList.add(new Product("300613271", 4));
        productList.add(new Product("300613271", 4));
        productList.add(new Product("300613271", 4));
        productList.add(new Product("300613271", 4));
        productList.add(new Product("300617127", 5));
        productList.add(new Product("5053744478128", 3));
        productList.add(new Product("5053744478128", 42));
        productList.add(new Product("5053744478135", 18));
        productList.add(new Product("5053744478241", 10));
        productList.add(new Product("5053744478265", 10));
        productList.add(new Product("5053744478272", 15));
        productList.add(new Product("5053744478289", 16));
        productList.add(new Product("5053744478289", 4));
        productList.add(new Product("5053744478289", 6));
        productList.add(new Product("5053744478296", 6));
        productList.add(new Product("5053744478302", 2));
        productList.add(new Product("5053744517957", 15));
        productList.add(new Product("5053744517964", 12));
        productList.add(new Product("5053744518183", 6));
        productList.add(new Product("5053744518183", 9));
        productList.add(new Product("5053744518190", 6));
        productList.add(new Product("5053744518190", 6));
        productList.add(new Product("5053744518206", 1));
        productList.add(new Product("5053744518206", 6));
        productList.add(new Product("5053744518206", 6));
        productList.add(new Product("5053744518237", 3));
        productList.add(new Product("5054772120812", 10));
        productList.add(new Product("5054772120812", 10));
        productList.add(new Product("5054772120812", 10));
        productList.add(new Product("5054772120812", 3));
        productList.add(new Product("5054772120812", 3));
        productList.add(new Product("5054772120812", 3));
        productList.add(new Product("5054772120812", 5));
        productList.add(new Product("5054772120812", 7));
        productList.add(new Product("5054772120812", 7));
        productList.add(new Product("5054772120812", 7));
        productList.add(new Product("5054773239568", 6));
        productList.add(new Product("5054773239568", 6));
        productList.add(new Product("5054773239568", 6));
        productList.add(new Product("5054773239568", 6));
        productList.add(new Product("5054773239575", 5));
        productList.add(new Product("5054773239582", 5));
        productList.add(new Product("5054773239605", 5));

        return productList;
    }

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
                .forEach(p -> map.merge(p.productCode(), p, COMBINE_PRODUCTS));

        assertThat(map).containsKey("0000");
        assertThat(map).containsKey("1111");
        assertThat(map).containsKey("2222");
        assertThat(map.get("0000").quantity()).isEqualTo(20);
        assertThat(map.get("1111").quantity()).isEqualTo(12);
        assertThat(map.get("2222").quantity()).isEqualTo(5);
    }
}
