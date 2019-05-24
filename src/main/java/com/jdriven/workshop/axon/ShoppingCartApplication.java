package com.jdriven.workshop.axon;

import com.jdriven.workshop.axon.aggregate.ShoppingCartAggregate;
import com.jdriven.workshop.axon.command.AddProductCommand;
import com.jdriven.workshop.axon.command.CompleteCheckoutCommand;
import com.jdriven.workshop.axon.command.CreateShoppingCartCommand;
import com.jdriven.workshop.axon.command.RemoveProductCommand;
import com.jdriven.workshop.axon.domain.Product;
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
    }
}
