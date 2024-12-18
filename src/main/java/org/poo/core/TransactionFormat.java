package org.poo.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.poo.models.AccountService;
import org.poo.models.Transaction;

import java.util.ArrayList;

@Data
public class TransactionFormat {
    @JsonProperty("IBAN")
    private String IBAN;
    private double balance;
    private String currency;
    protected ArrayList<Transaction> transactions;

    public TransactionFormat(AccountService account) {
       this.IBAN = account.getIBAN();
       this.balance = account.getBalance();
       this.currency = account.getCurrency();
       this.transactions = new ArrayList<>();
    }

    public void getTransactions(ArrayList<? extends Transaction> transactions,
                                int startTimestamp, int endTimestamp) {

        for (Transaction t : transactions) {
            if (t.getTimestamp() < startTimestamp)
                continue;

            if (t.getTimestamp() > endTimestamp)
                return;

            if (this.transactions.isEmpty()) {
                this.transactions.add(t);
            } else if (!(this.transactions.getLast().getDescription().equals(t.getDescription()) &&
                        (this.getTransactions().getLast().getTimestamp() == t.getTimestamp()))) {

                this.transactions.add(t);
            }
        }
    }
}
