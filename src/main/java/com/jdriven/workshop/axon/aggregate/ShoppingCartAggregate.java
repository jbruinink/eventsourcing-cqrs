package com.jdriven.workshop.axon.aggregate;

import java.util.Map;

import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;


import com.jdriven.workshop.axon.domain.ShoppingCartProduct;
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
    }
}
