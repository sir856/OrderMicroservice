package com.developing.shop.orders.model;



import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "chosenItem")
@IdClass(ChosenItemIdClass.class)
public class ChosenItem {

    @Id
    @Column
    private long itemId;

    @Column
    private long amount;

    @Column
    private long price;

    @Id
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "orderId", nullable = false)
    @JsonIgnore
    private Order order;

    ChosenItem(long itemId, Order order, long amount) {
        this.itemId = itemId;
        this.order = order;
        this.amount = amount;
    }

    ChosenItem() {}

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
