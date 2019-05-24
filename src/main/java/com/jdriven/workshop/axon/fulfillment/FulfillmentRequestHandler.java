package com.jdriven.workshop.axon.fulfillment;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventBus;
import org.springframework.stereotype.Component;

import java.util.Random;

import static org.axonframework.eventhandling.GenericEventMessage.asEventMessage;

@Component
public class FulfillmentRequestHandler {

    private final EventBus eventBus;

    public FulfillmentRequestHandler(EventBus eventBus) {
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
