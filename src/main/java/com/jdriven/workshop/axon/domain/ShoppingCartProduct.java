package com.jdriven.workshop.axon.domain;

import lombok.Value;

@Value
public class ShoppingCartProduct {
    Product product;
    int quanity;
    int priceInCents;
}
