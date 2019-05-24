package com.jdriven.workshop.axon.saga;

import com.jdriven.workshop.axon.command.AcceptShoppingCartCommand;
import com.jdriven.workshop.axon.command.RejectShoppingCartCommand;
import com.jdriven.workshop.axon.event.CheckoutCompletedEvent;
import com.jdriven.workshop.axon.event.ShoppingCartAcceptedEvent;
import com.jdriven.workshop.axon.event.ShoppingCartRejectedEvent;
import com.jdriven.workshop.axon.fulfillment.FulfillmentReply;
import com.jdriven.workshop.axon.fulfillment.FulfillmentRequest;
import com.jdriven.workshop.axon.payment.PayProductsReply;
import com.jdriven.workshop.axon.payment.PayProductsRequest;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.saga.SagaEventHandler;
import org.axonframework.eventhandling.saga.SagaLifecycle;
import org.axonframework.eventhandling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;

import java.util.function.Supplier;

@Slf4j
@Saga
public class ShoppingCartSaga {

    private static final String ASSOCIATION_CART_ID = "cartId";
    private static final String ASSOCIATION_PAY_ID = "payProductsId";
    private static final String ASSOCIATION_FULFILLMENT_ID = "fulfillmentRequestId";

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

    @SagaEventHandler(associationProperty = ASSOCIATION_PAY_ID)
    public void handle(PayProductsReply.Accepted reply, Supplier<String> idSupplier, CommandGateway commandGateway) {
        String fulfillmentId = idSupplier.get();

        SagaLifecycle.removeAssociationWith(ASSOCIATION_PAY_ID, reply.getPayProductsId());
        SagaLifecycle.associateWith(ASSOCIATION_FULFILLMENT_ID, fulfillmentId);

        FulfillmentRequest fulfillmentRequest = new FulfillmentRequest(fulfillmentId, reply.getCartId());
        log.info("Payment accepted {}, request a fulfillment {}", reply, fulfillmentRequest);

        commandGateway.sendAndWait(fulfillmentRequest);
    }

    @SagaEventHandler(associationProperty = ASSOCIATION_FULFILLMENT_ID)
    public void handle(FulfillmentReply.Accepted reply, CommandGateway commandGateway) {
        SagaLifecycle.removeAssociationWith(ASSOCIATION_FULFILLMENT_ID, reply.getFulfillmentRequestId());

        AcceptShoppingCartCommand acceptShoppingCartCommand = new AcceptShoppingCartCommand(reply.getCartId());

        log.info("Fulfillment accepted {}, accept the shopping cart {}", reply, acceptShoppingCartCommand);

        commandGateway.sendAndWait(acceptShoppingCartCommand);
    }

    @SagaEventHandler(associationProperty = ASSOCIATION_FULFILLMENT_ID)
    public void handle(FulfillmentReply.Rejected reply, CommandGateway commandGateway) {
        SagaLifecycle.removeAssociationWith(ASSOCIATION_FULFILLMENT_ID, reply.getFulfillmentRequestId());

        RejectShoppingCartCommand rejectShoppingCartCommand = new RejectShoppingCartCommand(reply.getCartId(), reply.getReason());

        log.info("Fulfillment rejected {}, reject the shopping cart {}", rejectShoppingCartCommand, rejectShoppingCartCommand);

        commandGateway.sendAndWait(rejectShoppingCartCommand);
    }

    @SagaEventHandler(associationProperty = ASSOCIATION_PAY_ID)
    public void handle(PayProductsReply.Rejected reply, CommandGateway commandGateway) {
        SagaLifecycle.removeAssociationWith(ASSOCIATION_PAY_ID, reply.getPayProductsId());

        RejectShoppingCartCommand rejectShoppingCartCommand = new RejectShoppingCartCommand(reply.getCartId(), reply.getReason());

        log.info("Payment rejected {}, reject the shopping cart {}", reply, rejectShoppingCartCommand);

        commandGateway.sendAndWait(rejectShoppingCartCommand);
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
