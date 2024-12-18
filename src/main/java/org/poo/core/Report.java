package org.poo.core;

import lombok.Getter;
import org.poo.fileio.CommandInput;
import org.poo.models.AccountService;
import org.poo.models.Transaction;
import org.poo.models.UserDetails;

import java.util.ArrayList;

@Getter
public class Report {
    private final String command;
    private TransactionFormat output;
    private final int timestamp;

    public Report(String command, int timestamp) {
        this.command = command;
        this.timestamp = timestamp;
    }

    public void setOutput(CommandInput commandInput, UserDetails user, AccountService account) {
        if (account.getAccountType().equals("savings")) {
            this.output = null;
            return;
        }

        ArrayList<? extends Transaction> transactions;

        if (commandInput.getCommand().equals("report")) {
            this.output = new TransactionFormat(account);
            transactions = user.getTransactions();
        } else {
            this.output = new SpendingTransactions(account);
            transactions = account.getCardPayments();
        }

        output.getTransactions(transactions, commandInput.getStartTimestamp(),
                                commandInput.getEndTimestamp());

        if (commandInput.getCommand().equals("spendingsReport"))
            ((SpendingTransactions) output).getCommerciants();
    }
}
