package com.jdriven.workshop.axon.projection;

import com.jdriven.workshop.axon.domain.ShoppingCartStatus;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class ShoppingCart {

    @Id
    private String id;

    private ShoppingCartStatus status;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "shoppingCart")
    private Set<ShoppingCartItem> items;

    private ShoppingCart() {
    }

    public ShoppingCart(final String id, ShoppingCartStatus status) {
        this.id = id;
        this.status = status;
        this.items = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public Set<ShoppingCartItem> getItems() {
        return items;
    }

    public ShoppingCartStatus getStatus() {
        return status;
    }

    public void setStatus(ShoppingCartStatus status) {
        this.status = status;
    }
}
