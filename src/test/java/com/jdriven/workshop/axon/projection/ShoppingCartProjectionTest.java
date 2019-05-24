package com.jdriven.workshop.axon.projection;

import com.jdriven.workshop.axon.domain.Product;
import com.jdriven.workshop.axon.domain.ShoppingCartStatus;
import com.jdriven.workshop.axon.event.CheckoutCompletedEvent;
import com.jdriven.workshop.axon.event.ProductAddedEvent;
import com.jdriven.workshop.axon.event.ProductRemovedEvent;
import com.jdriven.workshop.axon.event.ShoppingCartCreatedEvent;


import org.axonframework.eventhandling.AnnotationEventListenerAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.axonframework.eventhandling.GenericEventMessage.asEventMessage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingCartProjectionTest {

    @Captor
    ArgumentCaptor<ShoppingCart> shoppingCartCaptor;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    private AnnotationEventListenerAdapter shoppingCartProjection;

    @Before
    public void setup() {
        ShoppingCart shoppingCart = new ShoppingCart("cartId", ShoppingCartStatus.SHOPPING);
        shoppingCart.getItems().add(new ShoppingCartItem(shoppingCart, "productId", "productName", 3, BigDecimal.valueOf(25.00)));

        when(shoppingCartRepository.findById("cartId")).thenReturn(Optional.of(shoppingCart));
        shoppingCartProjection = new AnnotationEventListenerAdapter(new ShoppingCartProjection(shoppingCartRepository));
    }

    @Test
    public void testSavesCartOnCartCreatedEvent() throws Exception {
        shoppingCartProjection.handle(asEventMessage(new ShoppingCartCreatedEvent("cartId")));

        Mockito.verify(shoppingCartRepository).save(shoppingCartCaptor.capture());
        ShoppingCart cart = shoppingCartCaptor.getValue();
        assertEquals("cartId", cart.getId());
    }

    @Test
    public void testAddsProducts() throws Exception {
        shoppingCartProjection.handle(asEventMessage(new ProductAddedEvent("cartId", new Product("productId", "productName"), 3, 2500)));

        Mockito.verify(shoppingCartRepository).save(shoppingCartCaptor.capture());
        ShoppingCart cart = shoppingCartCaptor.getValue();
        assertEquals("cartId", cart.getId());
        ShoppingCartItem item = cart.getItems().iterator().next();
        assertEquals("productId", item.getProductId());
        assertEquals(6, item.getQuantity());
    }

    @Test
    public void testRemoveProducts() throws Exception {
        shoppingCartProjection.handle(asEventMessage(new ProductRemovedEvent("cartId", "productId", 3)));

        Mockito.verify(shoppingCartRepository).save(shoppingCartCaptor.capture());
        ShoppingCart cart = shoppingCartCaptor.getValue();
        assertEquals("cartId", cart.getId());
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    public void statusIsCompletedOnCheckoutCompleted() throws Exception {
        shoppingCartProjection.handle(asEventMessage(new CheckoutCompletedEvent("cartId")));

        Mockito.verify(shoppingCartRepository).save(shoppingCartCaptor.capture());
        ShoppingCart cart = shoppingCartCaptor.getValue();
        assertEquals("cartId", cart.getId());
        assertEquals(ShoppingCartStatus.CHECKOUT_COMPLETED, cart.getStatus());
    }
}
