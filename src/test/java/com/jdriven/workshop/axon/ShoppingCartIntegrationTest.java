package com.jdriven.workshop.axon;

import com.jdriven.workshop.axon.command.AddProductCommand;
import com.jdriven.workshop.axon.command.CompleteCheckoutCommand;
import com.jdriven.workshop.axon.command.CreateShoppingCartCommand;
import com.jdriven.workshop.axon.command.RemoveProductCommand;
import com.jdriven.workshop.axon.domain.Product;
import com.jdriven.workshop.axon.projection.ShoppingCart;
import com.jdriven.workshop.axon.projection.ShoppingCartProjection;
import com.jdriven.workshop.axon.query.ShoppingCartQuery;
import com.jdriven.workshop.axon.query.ShoppingCartResponse;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ShoppingCartIntegrationTest {

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private QueryGateway queryGateway;

    @Autowired
    private ShoppingCartProjection projection;

    /**
     * Before running this test, make sure you completed the exercises
     */
    @Test
    @Transactional
    public void test() throws ExecutionException, InterruptedException {
        commandGateway.sendAndWait(new CreateShoppingCartCommand("cart-5"));
        commandGateway.sendAndWait(new AddProductCommand("cart-5", new Product("pizza", "large pizza"), 3, 400));

        commandGateway.sendAndWait(new CreateShoppingCartCommand("cart-6"));
        commandGateway.sendAndWait(new AddProductCommand("cart-6", new Product("pizza", "large pizza"), 3, 400));
        commandGateway.sendAndWait(new RemoveProductCommand("cart-6", "pizza", 3));

        commandGateway.sendAndWait(new CreateShoppingCartCommand("cart-7"));
        commandGateway.sendAndWait(new AddProductCommand("cart-7", new Product("pizza", "large pizza"), 30, 400));
        commandGateway.sendAndWait(new CompleteCheckoutCommand("cart-7"));

        CompletableFuture<ShoppingCartResponse> response = queryGateway.query(new ShoppingCartQuery("pizza"), ShoppingCartResponse.class);
        ShoppingCartResponse shoppingCartResponse = response.get();
        List<ShoppingCart> shoppingCarts = shoppingCartResponse.getShoppingCarts();

        assertEquals(1, shoppingCarts.size());
        assertEquals("cart-5", shoppingCarts.iterator().next().getId());
    }
}