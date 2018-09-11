package com.jdriven.workshop.axon.domain;

import com.jdriven.workshop.axon.domain.Product;

import lombok.Value;

@Value
public class ShoppingCartProduct {
    Product product;
    int quanity;
    int priceInCents;
}
