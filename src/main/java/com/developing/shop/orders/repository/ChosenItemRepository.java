package com.developing.shop.orders.repository;

import com.developing.shop.orders.model.ChosenItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChosenItemRepository extends JpaRepository<ChosenItem, Long> {

    @Query(value = "select * from chosenitem i where i.order_id = :id",nativeQuery = true)
    List<ChosenItem> findByOrderId(@Param("id") long id);
}
