package com.jdriven.workshop.axon;

import com.jdriven.workshop.axon.command.AcceptShoppingCartCommand;
import com.jdriven.workshop.axon.command.RejectShoppingCartCommand;
import com.jdriven.workshop.axon.event.CheckoutCompletedEvent;
import com.jdriven.workshop.axon.event.ShoppingCartAcceptedEvent;
import com.jdriven.workshop.axon.event.ShoppingCartRejectedEvent;
import com.jdriven.workshop.axon.fulfillment.FulfillmentReply;
import com.jdriven.workshop.axon.fulfillment.FulfillmentRequest;
import com.jdriven.workshop.axon.payment.PayProductsReply;
import com.jdriven.workshop.axon.payment.PayProductsRequest;
import com.jdriven.workshop.axon.saga.ShoppingCartSaga;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

@RunWith(MockitoJUnitRunner.class)
public class SagaHandlingExerciseTest {

    private SagaTestFixture<ShoppingCartSaga> fixture;

    private Queue<String> ids = new ArrayDeque<>();
    private static final String CART_ID = "JDriven";
    private static final  String PAY_PRODUCTS_ID = "Robert betaalt! :-)";
    private static final  String FULFILLMENT_REQUEST_ID = "erik verzamelt :-)";

    private CheckoutCompletedEvent checkoutCompleted = new CheckoutCompletedEvent(CART_ID);
    private PayProductsReply.Accepted paymentAccepted = new PayProductsReply.Accepted(CART_ID, PAY_PRODUCTS_ID);
    private PayProductsReply.Rejected paymentRejected = new PayProductsReply.Rejected(CART_ID, PAY_PRODUCTS_ID, "geen geld");
    private FulfillmentReply.Accepted fulfillmentAccepted = new FulfillmentReply.Accepted(CART_ID, FULFILLMENT_REQUEST_ID);
    private FulfillmentReply.Rejected fulfillmentRejected = new FulfillmentReply.Rejected(CART_ID, FULFILLMENT_REQUEST_ID, "geen voorraad");

    @Before
    public void setUp() {
        fixture = new SagaTestFixture<>(ShoppingCartSaga.class);
        fixture.registerResource((Supplier<String>) ids::remove);
        ids.clear();

        ids.offer(PAY_PRODUCTS_ID);
        ids.offer(FULFILLMENT_REQUEST_ID);
    }

    @Test
    public void whenShoppingCartCheckedOutStartSaga() {
        fixture
                .givenNoPriorActivity()
                .whenPublishingA(checkoutCompleted)
                .expectActiveSagas(1)
                .expectAssociationWith("cartId", checkoutCompleted.getCartId())
                .expectAssociationWith("payProductsId", PAY_PRODUCTS_ID)
                .expectDispatchedCommands(new PayProductsRequest(PAY_PRODUCTS_ID, checkoutCompleted.getCartId()));
    }

    // TODO: Add an event handler to start the fulfillment to ShoppingCartSaga
    @Test
    public void whenPaymentAcceptedStartFulfillment() {
        fixture
                .givenAPublished(checkoutCompleted)
                .whenPublishingA(paymentAccepted)
                .expectAssociationWith("cartId", checkoutCompleted.getCartId())
                .expectAssociationWith("fulfillmentRequestId", FULFILLMENT_REQUEST_ID)
                .expectNoAssociationWith("payProductsId", PAY_PRODUCTS_ID)
                .expectDispatchedCommands(new FulfillmentRequest(FULFILLMENT_REQUEST_ID, checkoutCompleted.getCartId()))
                .expectActiveSagas(1);
    }

    // TODO: Add an event handler to accept the shopping cart to ShoppingCartSaga
    @Test
    public void whenFulfillmentAcceptedAcceptShoppingCart() throws Exception {
        fixture
                .givenAPublished(checkoutCompleted)
                .andThenAPublished(paymentAccepted)
                .whenPublishingA(fulfillmentAccepted)
                .expectAssociationWith("cartId", checkoutCompleted.getCartId())
                .expectNoAssociationWith("fulfillmentRequestId", FULFILLMENT_REQUEST_ID)
                .expectDispatchedCommands(new AcceptShoppingCartCommand(checkoutCompleted.getCartId()))
                .expectActiveSagas(1);
    }

    // TODO: Add an event handler to reject the shopping cart to ShoppingCartSaga
    @Test
    public void whenPaymentRejectedRejectShoppingCart() {
        fixture
                .givenAPublished(checkoutCompleted)
                .whenPublishingA(paymentRejected)
                .expectAssociationWith("cartId", checkoutCompleted.getCartId())
                .expectNoAssociationWith("fulfillmentRequestId", FULFILLMENT_REQUEST_ID)
                .expectDispatchedCommands(new RejectShoppingCartCommand(checkoutCompleted.getCartId(), paymentRejected.getReason()))
                .expectActiveSagas(1);
    }

    // TODO: Add an event handler to reject the shopping cart to ShoppingCartSaga
    @Test
    public void whenFulfillmentRejectedRejectShoppingCart() throws Exception {
        fixture
                .givenAPublished(checkoutCompleted)
                .andThenAPublished(paymentAccepted)
                .whenPublishingA(fulfillmentRejected)
                .expectAssociationWith("cartId", checkoutCompleted.getCartId())
                .expectNoAssociationWith("fulfillmentRequestId", FULFILLMENT_REQUEST_ID)
                .expectDispatchedCommands(new RejectShoppingCartCommand(checkoutCompleted.getCartId(), fulfillmentRejected.getReason()))
                .expectActiveSagas(1);
    }

    @Test
    public void whenShoppingCartRejectedEndSaga() {
        ShoppingCartRejectedEvent rejectedEvent = new ShoppingCartRejectedEvent(checkoutCompleted.getCartId(), "no funds");

        fixture
                .whenPublishingA(rejectedEvent)
                .expectActiveSagas(0);
    }

    @Test
    public void whenShoppingCartAcceptedEndSaga() {
        ShoppingCartAcceptedEvent acceptedEvent = new ShoppingCartAcceptedEvent(checkoutCompleted.getCartId());

        fixture
                .whenPublishingA(acceptedEvent)
                .expectActiveSagas(0);
    }
}
