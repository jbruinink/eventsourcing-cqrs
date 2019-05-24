package com.jdriven.workshop.axon.command;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
public class RejectShoppingCartCommand {
    @TargetAggregateIdentifier
    private final String cartId;
    private final String reason;
}
