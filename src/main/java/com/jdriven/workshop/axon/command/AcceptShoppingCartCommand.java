package com.jdriven.workshop.axon.command;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
public class AcceptShoppingCartCommand {
    @TargetAggregateIdentifier
    private final String cartId;
}
