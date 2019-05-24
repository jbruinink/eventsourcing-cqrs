package com.jdriven.workshop.axon.projection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, String> {

    @Query("select s from ShoppingCart s join s.items i where (i.productId = :productId)")
    List<ShoppingCart> findByProductId(@Param("productId") String productId);
}
