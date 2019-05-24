package com.jdriven.workshop.axon.projection;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ShoppingCart {

    @Id
    private String id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "shoppingCart")
    private Set<ShoppingCartItem> items;

    private ShoppingCart() {
    }

    public ShoppingCart(final String id) {
        this.id = id;
        this.items = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public Set<ShoppingCartItem> getItems() {
        return items;
    }
}
