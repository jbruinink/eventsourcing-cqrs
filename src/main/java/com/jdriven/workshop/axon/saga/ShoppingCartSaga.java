package com.jdriven.workshop.axon.saga;

import com.jdriven.workshop.axon.event.CheckoutCompletedEvent;
import com.jdriven.workshop.axon.event.ShoppingCartAcceptedEvent;
import com.jdriven.workshop.axon.event.ShoppingCartRejectedEvent;
import com.jdriven.workshop.axon.payment.PayProductsRequest;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.SagaLifecycle;
import org.axonframework.eventhandling.saga.StartSaga;

import java.util.function.Supplier;

@Slf4j
public class ShoppingCartSaga {

    private static final String ASSOCIATION_CART_ID = "cartId";
    private static final String ASSOCIATION_PAY_ID = "payProductsId";

    @StartSaga
    @SagaEventHandler(associationProperty = ASSOCIATION_CART_ID)
    public void start(CheckoutCompletedEvent evt, Supplier<String> idSupplier, CommandGateway commandGateway) {
        String payProductsId = idSupplier.get();

        SagaLifecycle.associateWith(ASSOCIATION_CART_ID, evt.getCartId());
        SagaLifecycle.associateWith(ASSOCIATION_PAY_ID, payProductsId);

        PayProductsRequest payProductsRequest = new PayProductsRequest(payProductsId, evt.getCartId());

        log.info("Checkout completed, let's pay! {}", payProductsRequest);

        commandGateway.sendAndWait(payProductsRequest);
    }

    @SagaEventHandler(associationProperty = ASSOCIATION_CART_ID)
    public void handle(ShoppingCartRejectedEvent evt) {
        log.info("Shopping cart rejected {}, end saga", evt);

        SagaLifecycle.end();
    }

    @SagaEventHandler(associationProperty = ASSOCIATION_CART_ID)
    public void handle(ShoppingCartAcceptedEvent evt) {
        log.info("Shopping cart accepted {}, end saga", evt);

        SagaLifecycle.end();
    }
}
