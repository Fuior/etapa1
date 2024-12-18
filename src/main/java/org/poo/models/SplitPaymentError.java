package org.poo.models;

import lombok.Getter;

import java.util.List;

@Getter
public class SplitPaymentError extends Transaction {
    private final String currency;
    private final double amount;
    private final List<String> involvedAccounts;
    private final String error;


    public SplitPaymentError(int timestamp, String description,
                             SplitPaymentFormat paymentInput, String IBAN) {

        super(timestamp, description);
        this.currency = paymentInput.getCurrency();
        this.amount = paymentInput.getAmount();
        this.involvedAccounts = paymentInput.getInvolvedAccounts();
        this.error = "Account " + IBAN + " has insufficient funds for a split payment.";
    }
}
