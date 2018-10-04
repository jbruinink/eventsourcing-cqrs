package com.jdriven.workshop.axon;

import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.queryhandling.DefaultQueryGateway;
import org.axonframework.queryhandling.QueryBus;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.jdriven.workshop.axon.aggregate.ShoppingCartAggregate;
import com.jdriven.workshop.axon.command.AddProductCommand;
import com.jdriven.workshop.axon.command.CompleteCheckoutCommand;
import com.jdriven.workshop.axon.command.CreateShoppingCartCommand;
import com.jdriven.workshop.axon.command.RemoveProductCommand;
import com.jdriven.workshop.axon.domain.Product;

@SpringBootApplication
public class ShoppingCartApplication implements CommandLineRunner {

    private final CommandGateway commandGateway;

    public static void main (String[] args) {
        SpringApplication.run(ShoppingCartApplication.class, args);
    }

    public ShoppingCartApplication(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Bean(name = "shoppingCartAggregateRepository")
    public EventSourcingRepository<ShoppingCartAggregate> repository(EventStore eventStore) {
        return new EventSourcingRepository<>(ShoppingCartAggregate.class, eventStore);
    }

    @Bean
    public QueryGateway queryGateway(QueryBus queryBus) {
        return new DefaultQueryGateway(queryBus);
    }

    @Override
    public void run(String... args) {
        String cartId = "cart-1";
        Product lamzac = new Product("lamzac", "JDriven Lamzac");
        Product beer = new Product("beer", "Beer");
        Product umbrella = new Product("umbrella", "Umbrella");

        commandGateway.send(new CreateShoppingCartCommand(cartId), errorLoggingCallback());
        commandGateway.send(new AddProductCommand(cartId, lamzac, 1, 1500), errorLoggingCallback());
        commandGateway.send(new AddProductCommand(cartId, beer, 12, 80), errorLoggingCallback());
        commandGateway.send(new AddProductCommand(cartId, umbrella, 1, 1000), errorLoggingCallback());

        cartId = "cart-2";
        commandGateway.send(new CreateShoppingCartCommand(cartId), errorLoggingCallback());
        commandGateway.send(new AddProductCommand(cartId, lamzac, 1, 1500), errorLoggingCallback());
        commandGateway.send(new AddProductCommand(cartId, beer, 12, 80), errorLoggingCallback());
        commandGateway.send(new AddProductCommand(cartId, umbrella, 1, 1000), errorLoggingCallback());
        commandGateway.send(new RemoveProductCommand(cartId, beer.getId(), 6), errorLoggingCallback());

        cartId = "cart-3";
        commandGateway.send(new CreateShoppingCartCommand(cartId));
        commandGateway.send(new AddProductCommand(cartId, lamzac, 1, 1500));
        commandGateway.send(new AddProductCommand(cartId, beer, 12, 80));
        commandGateway.send(new AddProductCommand(cartId, umbrella, 1, 1000));
        commandGateway.send(new RemoveProductCommand(cartId, beer.getId(), 6));
        commandGateway.send(new CompleteCheckoutCommand(cartId));
    }

    private <C, R> CommandCallback<C, R> errorLoggingCallback() {
        return new CommandCallback<C, R>() {
            @Override
            public void onSuccess(CommandMessage<? extends C> commandMessage, R r) {

            }

            @Override
            public void onFailure(CommandMessage<? extends C> commandMessage, Throwable throwable) {
                System.out.printf("Command %s failed%n", commandMessage.getCommandName());
                throwable.printStackTrace();
            }
        };
    }
}
