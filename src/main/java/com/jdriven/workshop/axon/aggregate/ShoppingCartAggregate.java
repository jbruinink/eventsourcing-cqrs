package com.jdriven.workshop.axon.aggregate;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import java.util.HashMap;
import java.util.Map;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;


import com.jdriven.workshop.axon.command.AddProductCommand;
import com.jdriven.workshop.axon.command.CompleteCheckoutCommand;
import com.jdriven.workshop.axon.command.CreateShoppingCartCommand;
import com.jdriven.workshop.axon.command.RemoveProductCommand;
import com.jdriven.workshop.axon.domain.CartValueTooLowException;
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

    @CommandHandler
    public ShoppingCartAggregate(CreateShoppingCartCommand cmd) {
        apply(new ShoppingCartCreatedEvent(cmd.getId()));
    }

    @CommandHandler
    public void on(AddProductCommand cmd) {
        apply(new ProductAddedEvent(cmd.getId(), cmd.getProduct(), cmd.getQuantity(), cmd.getPriceInCents()));
    }

    @CommandHandler
    public void on(RemoveProductCommand cmd) {
        apply(new ProductRemovedEvent(cmd.getCartId(), cmd.getProductId(), cmd.getQuantity()));
    }

    @CommandHandler
    public void on(CompleteCheckoutCommand cmd) throws CartValueTooLowException {
        if (products.values().stream()
                .mapToInt(scp -> scp.getQuanity() * scp.getPriceInCents()).sum() < 2500) {
            throw new CartValueTooLowException();
        }

        apply(new CheckoutCompletedEvent(cmd.getCartId()));
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