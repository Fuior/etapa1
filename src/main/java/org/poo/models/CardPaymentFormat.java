package org.poo.models;

import lombok.Getter;

@Getter
public class CardPaymentFormat extends Transaction {
    private double amount;
    private String commerciant;

    public CardPaymentFormat(int timestamp, String description,
                             double amount, String commerciant) {

        super(timestamp, description);
        this.amount = amount;
        this.commerciant = commerciant;
    }
}
