package com.jdriven.workshop.axon.event;

import lombok.Value;

@Value
public class ShoppingCartRejectedEvent {
    private final String cartId;
    private final String reason;
}
