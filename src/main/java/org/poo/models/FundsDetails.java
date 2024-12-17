package org.poo.models;

import lombok.Data;

@Data
public class FundsDetails {
    private double amount;
    private int timestamp;

    public FundsDetails(double amount, int timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }
}
