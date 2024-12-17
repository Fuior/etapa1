package org.poo.fileio;

import lombok.Data;
import org.poo.core.BankHandler;
import org.poo.models.Transaction;
import org.poo.models.UserDetails;

import java.util.ArrayList;

@Data
public class TransactionsOutput {
    private final String command = "printTransactions";
    private ArrayList<Transaction> output;
    private int timestamp;

    public TransactionsOutput(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setOutput(BankHandler bank, String email) {
        UserDetails user = bank.findUser(email);
        output = user.getTransactions();
    }
}
