package com.developing.shop.orders.service;

import com.developing.shop.orders.model.ChosenItem;
import com.developing.shop.orders.model.Order;
import com.developing.shop.orders.repository.ChosenItemRepository;
import com.developing.shop.orders.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    private final ChosenItemRepository itemRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ChosenItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Order addOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrders(Map<String, String> params) {
        List<Order> orders = orderRepository.findAll();

        return orders;
    }

    @Override
    public Order getOrderById(long id) {
        Order order = orderRepository.findById(id).orElse(null);
        return order;
    }

    @Override
    public Order addItem(ChosenItem item, long orderId) {
        Order order = getOrderById(orderId);
        item.setOrder(order);
        itemRepository.save(item);
        return order;
    }


    @Override
    public Order deleteItem(long itemId, long orderId) {
        itemRepository.deleteByCompositeKey(itemId, orderId);
        return orderRepository.findById(orderId).orElseGet(null);
    }
}
