package com.jdriven.workshop.axon.payment;

import lombok.Value;

@Value
public class PayProductsRequest {
    private final String payProductsId;
    private final String cartId;
}
