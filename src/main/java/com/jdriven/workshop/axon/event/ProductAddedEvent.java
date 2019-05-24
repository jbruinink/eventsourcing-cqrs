package com.jdriven.workshop.axon.event;

import com.jdriven.workshop.axon.domain.Product;
import lombok.Value;

@Value
public class ProductAddedEvent {
    String shoppingCartId;
    Product product;
    int quantity;
    int priceInCents;
}
