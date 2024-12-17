package org.poo.models;

import lombok.Data;

@Data
public class Transaction {
    private int timestamp;
    private String description;

    public Transaction(int timestamp, String description) {
        this.timestamp = timestamp;
        this.description = description;
    }
}

