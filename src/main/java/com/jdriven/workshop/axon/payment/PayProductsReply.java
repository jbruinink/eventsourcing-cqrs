package com.jdriven.workshop.axon.payment;

import lombok.Value;

public interface PayProductsReply {

    @Value
    class Accepted implements PayProductsReply {
        private final String cartId;
        private final String payProductsId;
    }

    @Value
    class Rejected implements PayProductsReply {
        private final String cartId;
        private final String payProductsId;
        private final String reason;
    }
}
