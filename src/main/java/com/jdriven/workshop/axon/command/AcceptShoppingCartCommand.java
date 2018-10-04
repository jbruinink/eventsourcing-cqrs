package com.jdriven.workshop.axon.command;

import lombok.Value;

@Value
public class AcceptShoppingCartCommand {
    private final String cartId;
}
