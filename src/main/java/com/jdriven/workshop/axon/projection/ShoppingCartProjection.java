package com.jdriven.workshop.axon.projection;

import com.jdriven.workshop.axon.event.CheckoutCompletedEvent;
import com.jdriven.workshop.axon.event.ProductAddedEvent;
import com.jdriven.workshop.axon.event.ProductRemovedEvent;
import com.jdriven.workshop.axon.query.ShoppingCartQuery;
import com.jdriven.workshop.axon.query.ShoppingCartResponse;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Iterator;

import com.jdriven.workshop.axon.event.ShoppingCartCreatedEvent;

@Component
public class ShoppingCartProjection {

    private final ShoppingCartRepository repository;

    public ShoppingCartProjection(final ShoppingCartRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    public void on(ShoppingCartCreatedEvent event) {
        repository.save(new ShoppingCart(event.getId()));
    }

    @EventHandler
    public void on(ProductAddedEvent event) {
        repository.findById(event.getShoppingCartId()).ifPresent(cart -> {
            updateCart(cart, event);
            repository.save(cart);

        });
    }

    private void updateCart(final ShoppingCart shoppingCart, ProductAddedEvent event) {
        BigDecimal price = BigDecimal.valueOf(event.getPriceInCents()).movePointLeft(2);

        for (ShoppingCartItem item : shoppingCart.getItems()) {
            if (item.getProductId().equals(event.getProduct().getId())) {
                item.setQuantity(item.getQuantity() + event.getQuantity());
                item.setDescription(event.getProduct().getName());
                item.setPrice(price);

                return;
            }
        }

        shoppingCart.getItems().add(new ShoppingCartItem(shoppingCart, event.getProduct().getId(),
                event.getProduct().getName(), event.getQuantity(), price));
    }

    @EventHandler
    public void on(ProductRemovedEvent event) {
        repository.findById(event.getCartId()).ifPresent(cart -> updateCart(cart, event));
    }

    private void updateCart(final ShoppingCart shoppingCart, ProductRemovedEvent event) {
        Iterator<ShoppingCartItem> iterator = shoppingCart.getItems().iterator();
        while (iterator.hasNext()) {
            ShoppingCartItem item = iterator.next();
            if (item.getProductId().equals(event.getProductId())) {
                int quantity = item.getQuantity() - event.getQuantity();
                if (quantity <= 0) {
                    iterator.remove();
                } else {
                    item.setQuantity(quantity);
                }
                break;
            }
        }
        repository.save(shoppingCart);
    }

    @EventHandler
    public void on(CheckoutCompletedEvent event) {
        repository.deleteById(event.getCartId());
    }

    @QueryHandler
    public ShoppingCartResponse getShoppingCarts(ShoppingCartQuery query) {
        return new ShoppingCartResponse(repository.findByProductId(query.getProductId()));
    }
}
