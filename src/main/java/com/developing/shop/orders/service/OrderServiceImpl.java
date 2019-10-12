package com.developing.shop.orders.service;

import com.developing.shop.orders.model.ChosenItem;
import com.developing.shop.orders.model.OrderEntity;
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
    public OrderEntity addOrder(OrderEntity order) {
        return orderRepository.save(order);
    }

    @Override
    public List<OrderEntity> getOrders(Map<String, String> params) {
        List<OrderEntity> orders = orderRepository.findAll();

        return orders;
    }

    @Override
    public OrderEntity getOrderById(long id) {
        OrderEntity order = orderRepository.findById(id).orElse(null);
        return order;
    }

    @Override
    public OrderEntity addItem(ChosenItem item, long orderId) {
        validateItem(item);
        OrderEntity order = getOrderById(orderId);
        item.setOrder(order);
        itemRepository.save(item);
        return order;
    }

    private void validateItem(ChosenItem item) {
        item.setPrice(100);
    }

    @Override
    public OrderEntity deleteItem(long itemId, long orderId) {
        OrderEntity order = getOrderById(orderId);
        order.deleteItem(itemId);
        itemRepository.deleteById(itemId);
        orderRepository.save(order);
        return order;
    }
}
