package com.developing.shop.orders.model;



import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "chosenItem")
//@org.springframework.data.relational.core.mapping.Table("chosenitem")
public class ChosenItem {
    @Id
//    @org.springframework.data.annotation.Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
//    @org.springframework.data.relational.core.mapping.Column("itemId")
    private long itemId;

    @Column
    private long amount;

    @Column
    private long price;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "orderId", nullable = false)
    @JsonIgnore
    private OrderEntity order;

    ChosenItem(long itemId, OrderEntity order, long amount) {
        this.itemId = itemId;
        this.order = order;
        this.amount = amount;
    }

    ChosenItem() {}

    public long getId() {
        return id;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
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
