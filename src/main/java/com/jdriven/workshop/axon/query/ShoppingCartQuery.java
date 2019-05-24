package com.jdriven.workshop.axon.query;

import lombok.Value;

@Value
public class ShoppingCartQuery {
    String productId;
    String cartId;

    public static ShoppingCartQuery byProduct(String productId) {
        return new ShoppingCartQuery(productId, null);
    }

    public static ShoppingCartQuery byCart(String cartId) {
        return new ShoppingCartQuery(null, cartId);
    }
}
