package org.poo.models;

import lombok.Getter;

import java.util.List;

@Getter
public class SplitPaymentFormat extends Transaction {
    private String currency;
    private double amount;
    private List<String> involvedAccounts;

    public SplitPaymentFormat(int timestamp, String description, String currency,
                              double amount, List<String> involvedAccounts) {

        super(timestamp, description);
        this.currency = currency;
        this.amount = amount;
        this.involvedAccounts = involvedAccounts;
    }
}
