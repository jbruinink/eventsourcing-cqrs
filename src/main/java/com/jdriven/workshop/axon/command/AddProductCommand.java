package com.jdriven.workshop.axon.command;

import com.jdriven.workshop.axon.domain.Product;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import javax.validation.constraints.Positive;

@Value
public class AddProductCommand {

    @TargetAggregateIdentifier
    String id;

    Product product;

    @Positive
    int quantity;
    int priceInCents;
}
