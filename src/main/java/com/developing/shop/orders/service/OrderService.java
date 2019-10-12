package com.developing.shop.orders.service;

import com.developing.shop.orders.model.ChosenItem;
import com.developing.shop.orders.model.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Order addOrder(Order order);
    List<Order> getOrders(Map<String, String> params);
    Order getOrderById(long id);
    Order addItem(ChosenItem item, long orderId);
    Order deleteItem(long itemId, long orderId);
}
