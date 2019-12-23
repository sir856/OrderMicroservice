package com.developing.shop.orders.messageListeners;

import com.developing.shop.orders.messageListeners.data.MessageItem;
import com.developing.shop.orders.messageListeners.data.StatusChange;
import com.developing.shop.orders.model.ChosenItem;
import com.developing.shop.orders.model.Item;
import com.developing.shop.orders.model.Order;
import com.developing.shop.orders.model.Status;
import com.developing.shop.orders.repository.ItemRepository;
import com.developing.shop.orders.repository.OrderRepository;
import com.developing.shop.orders.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RabbitMqListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public RabbitMqListener(ItemRepository itemRepository, OrderRepository orderRepository, OrderService orderService) {
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
    }

    @RabbitListener(queues = "addItem")
    public void addItem(MessageItem newItem) {
        Item item = new Item(newItem.getId(), newItem.getName(), newItem.getAmount(), newItem.getPrice());

        itemRepository.save(item);
    }

    @RabbitListener(queues = "deleteItem")
    public void deleteItem(long id) {

        itemRepository.deleteById(id);
    }

    @RabbitListener(queues = "cancelToOrder")
    public void cancelOrder(long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No order with id : " + id));
        if (order.getStatus() == Status.CANCELLED) {
            return;
        }

        for (ChosenItem chosenItem : order.getItems()) {
            Item item = chosenItem.getItem();
            item.changeAmount(-chosenItem.getAmount());
            itemRepository.save(item);
        }
        orderRepository.save(order);
    }

    @RabbitListener(queues = "orderStatus")
    public void changeStatus(StatusChange statusChange) {
        Order order = orderRepository.findById(statusChange.getId()).orElseThrow(()
                -> new IllegalArgumentException("No order with id : " + statusChange.getId()));
        order.setStatus(statusChange.getStatus());
        orderRepository.save(order);
    }
}
