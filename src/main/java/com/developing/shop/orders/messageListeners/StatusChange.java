package com.developing.shop.orders.messageListeners;

import com.developing.shop.orders.model.Status;

import java.io.Serializable;

public class StatusChange implements Serializable {
    private long id;
    private Status status;

    public StatusChange(long id, String statusStr) {
        this.id = id;
        this.status = Status.valueOf(statusStr);
    }

    public long getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }
}
