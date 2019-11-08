package com.developing.shop.orders.repository;

import com.developing.shop.orders.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ItemRepository extends JpaRepository<Item, Long> {
}
