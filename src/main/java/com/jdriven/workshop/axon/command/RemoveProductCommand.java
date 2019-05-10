package com.jdriven.workshop.axon.command;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
public class RemoveProductCommand {
    @TargetAggregateIdentifier
    String cartId;
    String productId;
    int quantity;
}
