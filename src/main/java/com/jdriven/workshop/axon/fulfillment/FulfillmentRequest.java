package com.jdriven.workshop.axon.fulfillment;

import lombok.Value;

@Value
public class FulfillmentRequest {
    private final String fulfillmentRequestId;
    private final String cartId;
}
