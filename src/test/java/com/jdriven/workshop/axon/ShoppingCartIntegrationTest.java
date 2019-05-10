package com.jdriven.workshop.axon;

import java.util.concurrent.ExecutionException;

import javax.transaction.Transactional;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


import com.jdriven.workshop.axon.command.AddProductCommand;
import com.jdriven.workshop.axon.command.CompleteCheckoutCommand;
import com.jdriven.workshop.axon.command.CreateShoppingCartCommand;
import com.jdriven.workshop.axon.command.RemoveProductCommand;
import com.jdriven.workshop.axon.domain.Product;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ShoppingCartIntegrationTest {

    @Autowired
    private CommandGateway commandGateway;


    /**
     * Before running this test, make sure you completed the exercises
     */
    @Test
    @Transactional
    public void test() {
        commandGateway.sendAndWait(new CreateShoppingCartCommand("cart-5"));
        commandGateway.sendAndWait(new AddProductCommand("cart-5", new Product("pizza", "large pizza"), 3, 400));

        commandGateway.sendAndWait(new CreateShoppingCartCommand("cart-6"));
        commandGateway.sendAndWait(new AddProductCommand("cart-6", new Product("pizza", "large pizza"), 3, 400));
        commandGateway.sendAndWait(new RemoveProductCommand("cart-6", "pizza", 3));

        commandGateway.sendAndWait(new CreateShoppingCartCommand("cart-7"));
        commandGateway.sendAndWait(new AddProductCommand("cart-7", new Product("pizza", "large pizza"), 30, 400));
        commandGateway.sendAndWait(new CompleteCheckoutCommand("cart-7"));
    }
}