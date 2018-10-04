package com.jdriven.workshop.axon.event;

import lombok.Value;

@Value
public class ShoppingCartAcceptedEvent {
    private final String cartId;
}
