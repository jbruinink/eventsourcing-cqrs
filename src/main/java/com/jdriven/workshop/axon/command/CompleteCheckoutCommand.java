package com.jdriven.workshop.axon.command;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
public class CompleteCheckoutCommand {

    @TargetAggregateIdentifier
    String cartId;
}
