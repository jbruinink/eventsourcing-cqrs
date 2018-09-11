package com.jdriven.workshop.axon;

import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


import com.jdriven.workshop.axon.aggregate.ShoppingCartAggregate;
import com.jdriven.workshop.axon.event.ProductAddedEvent;
import com.jdriven.workshop.axon.event.ShoppingCartCreatedEvent;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class EventsourcingExersizeTest {

    @Autowired
    private EventSourcingRepository<ShoppingCartAggregate> repo;

    @Before
    public void setup() {
        //This is just to stop Axon from complaining about having an active unit of work
        DefaultUnitOfWork.startAndGet(new GenericCommandMessage<>(new Object()));
    }

    /**
     * For this exercise, finish the {@link org.axonframework.eventsourcing.EventSourcingHandler} for the
     * {@link ShoppingCartCreatedEvent} and add one for the {@link ProductAddedEvent} in the {@link ShoppingCartAggregate}
     */
    @Test
    public void exercise_1() {
        //Load an existing aggregate from the repo
        ShoppingCartAggregate shoppingCartAggregate = repo.load("cart-1")
                .getWrappedAggregate()
                .getAggregateRoot();

        Assert.assertEquals("There should be 3 products", 3, shoppingCartAggregate.getProducts().size());
        Assert.assertEquals("There should be 12 beers", 12, shoppingCartAggregate.getProducts()
                .get("beer").getQuanity());
    }

    /**
     * For this exercise, create an {@link org.axonframework.eventsourcing.EventSourcingHandler} for the
     * {@link com.jdriven.workshop.axon.event.ProductRemovedEvent} in the {@link ShoppingCartAggregate}
     */
    @Test
    public void exercise_2() {
        //Load an existing aggregate from the repo
        ShoppingCartAggregate shoppingCartAggregate = repo.load("cart-2")
                .getWrappedAggregate()
                .getAggregateRoot();

        Assert.assertEquals("There should be 3 products", 3, shoppingCartAggregate.getProducts().size());
        Assert.assertEquals("There should be 6 beers", 6, shoppingCartAggregate.getProducts().get("beer").getQuanity());
    }

    /**
     * For this exercise, create an {@link org.axonframework.eventsourcing.EventSourcingHandler} for the
     * {@link com.jdriven.workshop.axon.event.CheckoutCompletedEvent} in the {@link ShoppingCartAggregate}
     */
    @Test
    public void exercise_3() {
        //Load an existing aggregate from the repo
        ShoppingCartAggregate shoppingCartAggregate = repo.load("cart-3")
                .getWrappedAggregate()
                .getAggregateRoot();

        Assert.assertEquals("The cart should be empty", 0, shoppingCartAggregate.getProducts().size());
    }
}
