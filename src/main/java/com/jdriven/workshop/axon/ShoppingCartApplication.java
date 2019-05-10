package com.jdriven.workshop.axon;

import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


import com.jdriven.workshop.axon.aggregate.ShoppingCartAggregate;

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

    @Override
    public void run(String... args) {
    }
}
