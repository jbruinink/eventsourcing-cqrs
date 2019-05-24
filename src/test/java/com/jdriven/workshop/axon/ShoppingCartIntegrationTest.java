package com.jdriven.workshop.axon;

import com.jdriven.workshop.axon.command.AddProductCommand;
import com.jdriven.workshop.axon.command.CompleteCheckoutCommand;
import com.jdriven.workshop.axon.command.CreateShoppingCartCommand;
import com.jdriven.workshop.axon.command.RemoveProductCommand;
import com.jdriven.workshop.axon.domain.Product;
import com.jdriven.workshop.axon.domain.ShoppingCartStatus;
import com.jdriven.workshop.axon.projection.ShoppingCart;
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

import static com.jdriven.workshop.axon.query.ShoppingCartQuery.byCart;
import static com.jdriven.workshop.axon.query.ShoppingCartQuery.byProduct;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ShoppingCartIntegrationTest {

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private QueryGateway queryGateway;

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

        CompletableFuture<ShoppingCartResponse> response = queryGateway.query(byProduct("pizza"), ShoppingCartResponse.class);
        ShoppingCartResponse shoppingCartResponse = response.get();
        List<ShoppingCart> shoppingCarts = shoppingCartResponse.getShoppingCarts();

        assertThat(shoppingCarts).hasSize(2);
        assertThat(shoppingCarts).extracting("id").contains("cart-5", "cart-7");

        CompletableFuture<ShoppingCartResponse> cartQuery = queryGateway.query(byCart("cart-7"), ShoppingCartResponse.class);
        ShoppingCartResponse cart7Response = cartQuery.get();

        assertThat(cart7Response.getShoppingCarts()).hasSize(1);
        assertThat(cart7Response.getShoppingCarts()).allSatisfy(cart -> {
            assertThat(cart.getId()).isEqualTo("cart-7");
            assertThat(cart.getStatus()).isIn(ShoppingCartStatus.REJECTED, ShoppingCartStatus.ACCEPTED);
            assertThat(cart.getItems()).extracting("productId").contains("pizza");
        });
    }
}
