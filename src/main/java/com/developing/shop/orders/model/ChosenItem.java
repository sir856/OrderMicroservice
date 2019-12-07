package com.developing.shop.orders.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "chosenItem")
@IdClass(ChosenItemIdClass.class)
public class ChosenItem implements Serializable {

    @Id
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "itemId", nullable = false)
    @JsonIgnoreProperties({"items", "price", "amount"})
    private Item item;

    @Column
    private int amount;

    @Column
    private long price;

    @Id
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "orderId", nullable = false)
    @JsonIgnore
    private Order order;

    ChosenItem(Item item, Order order, int amount) {
        this.item = item;
        this.order = order;
        this.amount = amount;
    }

    ChosenItem() {
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
