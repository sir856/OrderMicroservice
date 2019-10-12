package com.developing.shop.orders.controllers;

import com.developing.shop.orders.model.ChosenItem;
import com.developing.shop.orders.model.OrderEntity;
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
    public List<OrderEntity> getItems(@RequestBody @Nullable Map<String, String> params) {
        return orderService.getOrders(params);
    }

    @PostMapping(value = "/orders")
    public OrderEntity createItem(@RequestBody OrderEntity order) {
        return orderService.addOrder(order);
    }

    @PutMapping("/orders/{id}")
    public OrderEntity addItem(@PathVariable("id") long id, @RequestBody ChosenItem item) {
        return orderService.addItem(item, id);
    }

}
