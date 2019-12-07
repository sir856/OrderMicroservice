package com.developing.shop.orders.repository;

import com.developing.shop.orders.model.ChosenItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ChosenItemRepository extends JpaRepository<ChosenItem, Long> {

    @Modifying
    @Transactional
    @Query(value = "delete from ChosenItem i where order_id=:order_id and item_id=:item_id")
    void deleteByCompositeKey(@Param("item_id") long itemId, @Param("order_id") long orderId);

    @Query(value = "select i from ChosenItem i where order_id=:order_id and item_id=:item_id")
    ChosenItem getByCompositeKey(@Param("item_id") long itemId, @Param("order_id") long orderId);
}
