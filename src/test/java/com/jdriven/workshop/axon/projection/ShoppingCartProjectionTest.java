package com.jdriven.workshop.axon.projection;

import com.jdriven.workshop.axon.domain.Product;
import com.jdriven.workshop.axon.event.CheckoutCompletedEvent;
import com.jdriven.workshop.axon.event.ProductAddedEvent;
import com.jdriven.workshop.axon.event.ProductRemovedEvent;
import com.jdriven.workshop.axon.event.ShoppingCartCreatedEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingCartProjectionTest {

    @Captor
    ArgumentCaptor<ShoppingCart> shoppingCartCaptor;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @InjectMocks
    private ShoppingCartProjection shoppingCartProjection;

    @Before
    public void setup() {
        ShoppingCart shoppingCart = new ShoppingCart("cartId");
        shoppingCart.getItems().add(new ShoppingCartItem(shoppingCart, "productId", "productName", 3, BigDecimal.valueOf(25.00)));

        when(shoppingCartRepository.findById("cartId")).thenReturn(Optional.of(shoppingCart));
    }

    @Test
    public void testSavesCartOnCartCreatedEvent() {
        shoppingCartProjection.on(new ShoppingCartCreatedEvent("cartId"));

        Mockito.verify(shoppingCartRepository).save(shoppingCartCaptor.capture());
        ShoppingCart cart = shoppingCartCaptor.getValue();
        assertEquals("cartId", cart.getId());
    }

    @Test
    public void testAddsProducts() {
        shoppingCartProjection.on(new ProductAddedEvent("cartId", new Product("productId", "productName"), 3, 2500));

        Mockito.verify(shoppingCartRepository).save(shoppingCartCaptor.capture());
        ShoppingCart cart = shoppingCartCaptor.getValue();
        assertEquals("cartId", cart.getId());
        ShoppingCartItem item = cart.getItems().iterator().next();
        assertEquals("productId", item.getProductId());
        assertEquals(6, item.getQuantity());
    }

    @Test
    public void testRemoveProducts() {
        shoppingCartProjection.on(new ProductRemovedEvent("cartId", "productId", 3));

        Mockito.verify(shoppingCartRepository).save(shoppingCartCaptor.capture());
        ShoppingCart cart = shoppingCartCaptor.getValue();
        assertEquals("cartId", cart.getId());
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    public void removesCartOnCheckoutCompleted() {
        shoppingCartProjection.on(new CheckoutCompletedEvent("cartId"));

        Mockito.verify(shoppingCartRepository).deleteById("cartId");
    }
}