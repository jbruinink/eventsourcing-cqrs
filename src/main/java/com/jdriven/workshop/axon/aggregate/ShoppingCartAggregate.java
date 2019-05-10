package com.jdriven.workshop.axon.aggregate;

import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.HashMap;
import java.util.Map;

import com.jdriven.workshop.axon.domain.ShoppingCartProduct;
import com.jdriven.workshop.axon.event.CheckoutCompletedEvent;
import com.jdriven.workshop.axon.event.ProductAddedEvent;
import com.jdriven.workshop.axon.event.ProductRemovedEvent;
import com.jdriven.workshop.axon.event.ShoppingCartCreatedEvent;

@Aggregate
public class ShoppingCartAggregate {
    @AggregateIdentifier
    private String id;

    private Map<String, ShoppingCartProduct> products;

    public ShoppingCartAggregate() {
    }

    public Map<String, ShoppingCartProduct> getProducts() {
        return products;
    }

    @EventSourcingHandler
    public void on(ShoppingCartCreatedEvent event) {
        this.id = event.getId();
        this.products = new HashMap<>();
    }

    @EventSourcingHandler
    public void on(ProductAddedEvent event) {
        products.compute(event.getProduct().getId(), (key, existing) ->
                new ShoppingCartProduct(event.getProduct(),
                        (existing != null ? existing.getQuanity() : 0) + event.getQuantity(),
                        event.getPriceInCents()));
    }

    @EventSourcingHandler
    public void on(ProductRemovedEvent event) {
        products.compute(event.getProductId(), (key, existing) -> {
            if (existing == null) {
                return null;
            }
            return new ShoppingCartProduct(existing.getProduct(),
                    Math.max(0, existing.getQuanity() - event.getQuantity()), existing.getPriceInCents());
        });
    }

    @EventSourcingHandler
    public void on(CheckoutCompletedEvent event) {
        products.clear();
    }
}