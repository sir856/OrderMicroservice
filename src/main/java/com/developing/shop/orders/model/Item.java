package com.developing.shop.orders.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "item")
public class Item implements Serializable {
    @Column
    @Id
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "price")
    private Long price;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "item")
    @JsonProperty
    private List<ChosenItem> items;

    Item() {
    }

    public Item(long id, String name, Integer amount, Long price) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.price = price;
        this.items = new ArrayList<>();
    }

    Item(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void changeAmount(int amount) {
        if (this.amount >= amount) {
            this.amount -= amount;
        }
        else {
            throw new IllegalArgumentException("Lacks " + (amount - this.amount) + " items with id : " + this.id);
        }
    }
}
