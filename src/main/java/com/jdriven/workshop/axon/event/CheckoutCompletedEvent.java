package com.jdriven.workshop.axon.event;

import lombok.Value;

@Value
public class CheckoutCompletedEvent {
    String cartId;
}
