package com.developing.shop.orders.messageListeners.data;

import java.io.Serializable;

public class MessageItem implements Serializable {
    private long id;
    private String name;
    private int amount;
    private long price;

    public MessageItem (long id, String name, int amount, long price) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public long getPrice() {
        return price;
    }
}
