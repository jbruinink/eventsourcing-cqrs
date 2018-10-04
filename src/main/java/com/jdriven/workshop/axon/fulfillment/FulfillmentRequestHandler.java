package com.jdriven.workshop.axon.fulfillment;

import com.jdriven.workshop.axon.payment.PayProductsReply;
import com.jdriven.workshop.axon.payment.PayProductsRequest;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventBus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Random;

import static org.axonframework.eventhandling.GenericEventMessage.asEventMessage;

@Component
public class FulfillmentRequestHandler {

    private final EventBus eventBus;

    public FulfillmentRequestHandler(@Qualifier("sagaEventBus") EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @CommandHandler(payloadType = FulfillmentRequest.class)
    public void handle(FulfillmentRequest request) {
        if (new Random().nextInt() % 2 == 0) {
            eventBus.publish(
                    asEventMessage(new FulfillmentReply.Accepted(request.getCartId(), request.getFulfillmentRequestId()))
            );
        } else {
            eventBus.publish(
                    asEventMessage(new FulfillmentReply.Rejected(request.getCartId(), request.getFulfillmentRequestId(), "No stock for product"))
            );
        }
    }
}
