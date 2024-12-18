package org.poo.fileio;

import lombok.Getter;
import org.poo.core.BankRepository;
import org.poo.models.Transaction;
import org.poo.models.UserDetails;

import java.util.ArrayList;

@Getter
public class TransactionsOutput {
    private final String command = "printTransactions";
    private ArrayList<Transaction> output;
    private final int timestamp;

    public TransactionsOutput(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setOutput(BankRepository bankRepository, String email) {
        UserDetails user = bankRepository.findUser(email);
        output = user.getTransactions();
    }
}
