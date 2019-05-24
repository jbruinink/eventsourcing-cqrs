package com.jdriven.workshop.axon.projection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class ShoppingCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @ManyToOne
    @JoinColumn(name = "shoppingCartId")
    private ShoppingCart shoppingCart;

    private String productId;
    private String description;
    private int quantity;
    private BigDecimal price;

    private ShoppingCartItem() {
    }

    public ShoppingCartItem(final ShoppingCart shoppingCart, final String productId, final String description,
                            final int quantity, final BigDecimal price) {
        this.shoppingCart = shoppingCart;
        this.productId = productId;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(final BigDecimal price) {
        this.price = price;
    }
}
