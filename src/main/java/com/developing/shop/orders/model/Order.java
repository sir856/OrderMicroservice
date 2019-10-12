package com.developing.shop.orders.model;



import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orderEntity")
public class Order {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Formula("(select coalesce(sum(i.amount), 0) from chosen_item i where i.order_id = id)")
    private long totalAmount;

    @Formula("(select coalesce(sum(i.price * i.amount), 0) from chosen_item i where i.order_id = id)")
    private long totalCost;

    @Column
    private String userName;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order")
    @JsonProperty
    private List<ChosenItem> items;

    Order() {

    }
    @JsonCreator
    Order(String userName) {
        this.userName = userName;
        this.status = Status.COLLECTING;
        this.items = new ArrayList<>();
    }

    List<ChosenItem> getItems() {
        return items;
    }

    public void addItem(ChosenItem item) {
        items.add(item);
    }

    public void setItems(List<ChosenItem> items) {
        this.items = items;
    }

    public long getId() {
        return id;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(long totalCost) {
        this.totalCost = totalCost;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
