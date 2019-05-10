package com.jdriven.workshop.axon;

import org.axonframework.messaging.interceptors.JSR303ViolationException;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Before;
import org.junit.Test;


import com.jdriven.workshop.axon.aggregate.ShoppingCartAggregate;
import com.jdriven.workshop.axon.command.AddProductCommand;
import com.jdriven.workshop.axon.command.CompleteCheckoutCommand;
import com.jdriven.workshop.axon.command.CreateShoppingCartCommand;
import com.jdriven.workshop.axon.command.RemoveProductCommand;
import com.jdriven.workshop.axon.domain.CartValueTooLowException;
import com.jdriven.workshop.axon.domain.Product;
import com.jdriven.workshop.axon.event.CheckoutCompletedEvent;
import com.jdriven.workshop.axon.event.ProductAddedEvent;
import com.jdriven.workshop.axon.event.ProductRemovedEvent;
import com.jdriven.workshop.axon.event.ShoppingCartCreatedEvent;

public class CommandHandlingExerciseTest {

    private AggregateTestFixture<ShoppingCartAggregate> fixture;

    @Before
    public void setUp() {
        fixture = new AggregateTestFixture<>(ShoppingCartAggregate.class);
    }

    /**
     * In this exercise, implement a {@link org.axonframework.commandhandling.CommandHandler} for the
     * {@link CreateShoppingCartCommand} in the {@link ShoppingCartAggregate} that applies a {@link ShoppinCartCreatedEvent}
     */
    @Test
    public void testCreatesShoppingCart() {
        final String cartId = "cart-4";
        fixture
                .givenNoPriorActivity()
                .when(new CreateShoppingCartCommand(cartId))
                .expectEvents(new ShoppingCartCreatedEvent(cartId));
    }

    /**
     * In this exercise, implement a {@link org.axonframework.commandhandling.CommandHandler} for the
     * {@link AddProductCommand} in the {@link ShoppingCartAggregate}
     */
    @Test
    public void testAddsProductsToShoppingCart() {
        final String cartId = "cart-5";
        final Product testProduct = new Product("product-1", "Test Product");

        fixture
                .given(new ShoppingCartCreatedEvent(cartId))
                .when(new AddProductCommand(cartId, testProduct, 3, 300))
                .expectEvents(new ProductAddedEvent(cartId, testProduct, 3, 300));
    }

    /**
     * In this exercise, implement a {@link org.axonframework.commandhandling.CommandHandler} for the
     * {@link RemoveProductCommand} in the {@link ShoppingCartAggregate}
     */
    @Test
    public void testRemovesProductsFromShoppingCart() {
        final String cartId = "cart-5";
        final String productId = "product-1";
        final Product testProduct = new Product(productId, "Test Product");

        fixture
                .given(
                        new ShoppingCartCreatedEvent(cartId),
                        new AddProductCommand(cartId, testProduct, 3, 300))
                .when(new RemoveProductCommand(cartId, productId, 2))
                .expectEvents(new ProductRemovedEvent(cartId, productId, 2));
    }

    /**
     * In this exercise, implement a {@link org.axonframework.commandhandling.CommandHandler} for the
     * {@link CompleteCheckoutCommand} in the {@link ShoppingCartAggregate}
     */
    @Test
    public void testCompletesCheckout() {
        final String cartId = "cart-5";
        final String productId = "product-1";
        final Product testProduct = new Product(productId, "Test Product");

        fixture
                .given(
                        new ShoppingCartCreatedEvent(cartId),
                        new ProductAddedEvent(cartId, testProduct, 3, 300),
                        new ProductRemovedEvent(cartId, productId, 2))
                .when(new CompleteCheckoutCommand(cartId))
                .expectEvents(new CheckoutCompletedEvent(cartId));
    }

    /**
     * In this exercise, fix this unit test
     */
    @Test
    public void testChecksImpossibleToAddNegativeAmountOfProducts() {
        final String cartId = "cart-5";
        final Product testProduct = new Product("product-1", "Test Product");

        fixture
                .given(new ShoppingCartCreatedEvent(cartId))
                .when(new AddProductCommand(cartId, testProduct, -3, 300))
                .expectEvents(new ProductAddedEvent(cartId, testProduct, -3, 300));
    }

    /**
     * In this exercise add validation to the {@link org.axonframework.commandhandling.CommandHandler} for the
     * {@link CompleteCheckoutCommand} to only accept checking out a minimum of 25 euros
     */
    @Test
    public void testRequiresMinimumCheckoutAmount() {
        final String cartId = "cart-5";
        final Product testProduct = new Product("product-1", "Test Product");

        fixture
                .given(new ShoppingCartCreatedEvent(cartId),
                        new AddProductCommand(cartId, testProduct, 1, 2499))
                .when(new CompleteCheckoutCommand(cartId))
                .expectException(CartValueTooLowException.class);
    }
}
