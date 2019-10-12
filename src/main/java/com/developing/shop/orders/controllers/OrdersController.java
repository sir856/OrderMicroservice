package com.developing.shop.orders.controllers;

import com.developing.shop.orders.model.ChosenItem;
import com.developing.shop.orders.model.Order;
import com.developing.shop.orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/warehouse")
public class OrdersController {
    private final OrderService orderService;

    @Autowired
    public OrdersController(OrderService orderService) {
        this.orderService = orderService;
    }


    @GetMapping("/orders")
    public List<Order> getItems(@RequestBody @Nullable Map<String, String> params) {
        return orderService.getOrders(params);
    }

    @PostMapping(value = "/orders")
    public Order createItem(@RequestBody Order order) {
        return orderService.addOrder(order);
    }

    @PutMapping("/orders/{id}")
    public Order addItem(@PathVariable("id") long id, @RequestBody ChosenItem item) {
        return orderService.addItem(item, id);
    }

    @DeleteMapping("/orders/{order_id}/{item_id}")
    public Order deleteItem(@PathVariable("order_id") long orderId, @PathVariable("item_id") long itemId) {
        return orderService.deleteItem(itemId,orderId);
    }

}
