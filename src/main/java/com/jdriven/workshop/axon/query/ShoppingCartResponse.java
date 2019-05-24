package com.jdriven.workshop.axon.query;

import com.jdriven.workshop.axon.projection.ShoppingCart;
import lombok.Value;

import java.util.List;

@Value
public class ShoppingCartResponse {
    List<ShoppingCart> shoppingCarts;
}
