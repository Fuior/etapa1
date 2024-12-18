package org.poo.models;

import lombok.Getter;

import java.util.List;

@Getter
public class SplitPaymentFormat extends Transaction {
    private final String currency;
    private final double amount;
    private final List<String> involvedAccounts;

    public SplitPaymentFormat(int timestamp, String description, String currency,
                              double amount, List<String> involvedAccounts) {

        super(timestamp, description);
        this.currency = currency;
        this.amount = amount;
        this.involvedAccounts = involvedAccounts;
    }
}
