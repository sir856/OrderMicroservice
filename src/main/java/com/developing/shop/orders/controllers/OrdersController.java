package com.developing.shop.orders.controllers;

import com.developing.shop.orders.model.ChosenItem;
import com.developing.shop.orders.model.Item;
import com.developing.shop.orders.model.Order;
import com.developing.shop.orders.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/warehouse")
public class OrdersController {
    private Logger logger = LoggerFactory.getLogger(OrdersController.class);

    private final OrderService orderService;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public OrdersController(OrderService orderService, RabbitTemplate template) {
        this.orderService = orderService;
        this.rabbitTemplate = template;
    }


    @GetMapping("/orders")
    public List<Order> getItems(@RequestBody @Nullable Map<String, String> params) {
        return orderService.getOrders(params);
    }

    @PostMapping(value = "/orders")
    public Order createOrder(@RequestBody Order order) {
        return orderService.addOrder(order);
    }

    @PutMapping("/orders/{id}")
    public Order addItemToOrder(@PathVariable("id") long id, @RequestBody ChosenItem item) {
        Order order = orderService.addItemToOrder(item, id);
        rabbitTemplate.setExchange("itemExchange");
        rabbitTemplate.convertAndSend("add", item);
        return order;
    }

    @DeleteMapping("/orders/{order_id}/{item_id}")
    public ChosenItem deleteItemFromOrder(@PathVariable("order_id") long orderId, @PathVariable("item_id") long itemId) {
        ChosenItem item = orderService.deleteItemFromOrder(itemId, orderId);
        rabbitTemplate.setExchange("itemExchange");
        rabbitTemplate.convertAndSend("delete", item);
        return item;
    }

    @PostMapping(value = "/items")
    public Item createItem(@RequestBody Item item) {
        return orderService.addItem(item);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(Exception ex) {
        logger.error("Illegal argument exception", ex);

        Map<String, String> body = new HashMap<>();
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
