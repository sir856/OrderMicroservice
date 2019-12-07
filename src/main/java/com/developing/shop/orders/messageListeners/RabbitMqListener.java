package com.developing.shop.orders.messageListeners;

import com.developing.shop.orders.model.Item;
import com.developing.shop.orders.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ItemRepository itemRepository;

    @Autowired
    public RabbitMqListener(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @RabbitListener(queues = "addItem")
    public void addItem(com.developing.shop.items.model.Item newItem) {
        Item item = new Item(newItem.getId(), newItem.getName(), newItem.getAmount(), newItem.getPrice());

        itemRepository.save(item);
    }

    @RabbitListener(queues = "deleteItem")
    public void deleteItem(com.developing.shop.items.model.Item deletingItem) {

        itemRepository.deleteById(deletingItem.getId());
    }
}
