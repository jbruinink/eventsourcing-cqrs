package com.jdriven.workshop.axon.command;

import lombok.Value;

@Value
public class RejectShoppingCartCommand {
    private final String cartId;
    private final String reason;
}
