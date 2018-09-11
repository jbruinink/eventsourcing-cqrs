package com.jdriven.workshop.axon.event;

import lombok.Value;

@Value
public class ProductRemovedEvent {
    String cartId;
    String productId;
    int quantity;
}
