package com.jdriven.workshop.axon.payment;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventBus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Random;

import static org.axonframework.eventhandling.GenericEventMessage.asEventMessage;

@Component
public class PayProductRequestHandler {

    private final EventBus eventBus;

    public PayProductRequestHandler(@Qualifier("sagaEventBus") EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @CommandHandler(payloadType = PayProductsRequest.class)
    public void handle(PayProductsRequest request) {
        if (new Random().nextInt() % 2 == 0) {
            eventBus.publish(
                    asEventMessage(new PayProductsReply.Accepted(request.getCartId(), request.getPayProductsId()))
            );
        } else {
            eventBus.publish(
                    asEventMessage(new PayProductsReply.Rejected(request.getCartId(), request.getPayProductsId(), "No funds"))
            );
        }
    }
}
