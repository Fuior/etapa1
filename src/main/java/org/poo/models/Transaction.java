package org.poo.models;

import lombok.Getter;

@Getter
public class Transaction {
    private final int timestamp;
    private final String description;

    public Transaction(int timestamp, String description) {
        this.timestamp = timestamp;
        this.description = description;
    }
}

