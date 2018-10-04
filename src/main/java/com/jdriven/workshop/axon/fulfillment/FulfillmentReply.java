package com.jdriven.workshop.axon.fulfillment;

import lombok.Value;

public interface FulfillmentReply {

    @Value
    class Accepted implements FulfillmentReply {
        private final String cartId;
        private final String fulfillmentRequestId;
    }

    @Value
    class Rejected implements FulfillmentReply {
        private final String cartId;
        private final String fulfillmentRequestId;
        private final String reason;
    }
}
