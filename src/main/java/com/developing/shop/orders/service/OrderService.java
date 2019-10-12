package com.developing.shop.orders.service;

import com.developing.shop.orders.model.ChosenItem;
import com.developing.shop.orders.model.OrderEntity;

import java.util.List;
import java.util.Map;

public interface OrderService {
    OrderEntity addOrder(OrderEntity order);
    List<OrderEntity> getOrders(Map<String, String> params);
    OrderEntity getOrderById(long id);
    OrderEntity addItem(ChosenItem item, long orderId);
    OrderEntity deleteItem(long itemId, long orderId);
}
