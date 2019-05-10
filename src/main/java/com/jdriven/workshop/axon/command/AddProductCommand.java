package com.jdriven.workshop.axon.command;

import javax.validation.constraints.Positive;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;


import com.jdriven.workshop.axon.domain.Product;

@Value
public class AddProductCommand {

    @TargetAggregateIdentifier
    String id;

    Product product;

    @Positive
    int quantity;
    int priceInCents;
}
