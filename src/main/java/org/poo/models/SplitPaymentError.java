package org.poo.models;

import lombok.Getter;

import java.util.List;

@Getter
public class SplitPaymentError extends Transaction {
    private String currency;
    private double amount;
    private List<String> involvedAccounts;
    private String error;


    public SplitPaymentError(int timestamp, String description,
                             SplitPaymentFormat paymentInput, String IBAN) {

        super(timestamp, description);
        this.currency = paymentInput.getCurrency();
        this.amount = paymentInput.getAmount();
        this.involvedAccounts = paymentInput.getInvolvedAccounts();
        this.error = "Account " + IBAN + " has insufficient funds for a split payment.";
    }
}
