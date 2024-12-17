package org.poo.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import org.poo.fileio.CommandInput;
import org.poo.models.AccountService;
import org.poo.models.CardPaymentFormat;
import org.poo.models.Transaction;
import org.poo.models.UserDetails;

import java.util.ArrayList;
import java.util.Comparator;

@Data
public class Report {
    private String command;
    private TransactionFormat output;
    private int timestamp;

    public Report(String command, int timestamp) {
        this.command = command;
        this.timestamp = timestamp;
    }

    public void setOutput(CommandInput commandInput, UserDetails user, AccountService account) {
        if (commandInput.getCommand().equals("report")) {
            this.output = new TransactionFormat(account);
        } else {
            this.output = new SpendingTransactions(account);
        }

        if (account.getAccountType().equals("classic")) {
            if (commandInput.getCommand().equals("spendingsReport")) {
                output.getTransactions(account.getCardPayments(), commandInput.getStartTimestamp(),
                                        commandInput.getEndTimestamp());

                ((SpendingTransactions) output).getCommerciants();
            } else {
                output.getTransactions(user.getTransactions(), commandInput.getStartTimestamp(),
                        commandInput.getEndTimestamp());
            }
        } else {
            this.output = null;
        }
    }
}

@Data
class TransactionFormat {
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

    public void getTransactions(ArrayList<? extends Transaction> transactions, int startTimestamp, int endTimestamp) {

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

@Getter
class SpendingTransactions extends TransactionFormat {
    @JsonProperty("commerciants")
    private ArrayList<Commerciant> commerciants;

    public SpendingTransactions(AccountService account) {
        super(account);
        this.commerciants = new ArrayList<>();
    }

    @Override
    public void getTransactions(ArrayList<? extends Transaction> transactions, int startTimestamp, int endTimestamp) {

        for (Transaction t : transactions) {
            if (t.getTimestamp() < startTimestamp)
                continue;

            if (t.getTimestamp() > endTimestamp)
                return;

            if (t.getDescription().equals("Card payment"))
                this.transactions.add(t);
        }
    }

    private int findCommerciant(String name) {

        for (int i = 0; i < commerciants.size(); i++) {
            if (commerciants.get(i).getCommerciant().equals(name)) {
                return i;
            }
        }

        return -1;
    }

    public void getCommerciants() {

        for (Transaction t : transactions) {
            int pos = findCommerciant(((CardPaymentFormat) t).getCommerciant());

            if (pos == -1) {
                commerciants.add(new Commerciant(((CardPaymentFormat) t).getCommerciant(),
                                ((CardPaymentFormat) t).getAmount()));
            } else {
                commerciants.get(pos).setTotal(commerciants.get(pos).getTotal() +
                                                ((CardPaymentFormat) t).getAmount());
            }
        }

        commerciants.sort(Comparator.comparing(Commerciant::getCommerciant));
    }
}

@Data
class Commerciant {
    private String commerciant;
    private double total;

    public Commerciant(String commerciant, double total) {
        this.commerciant = commerciant;
        this.total = total;
    }
}
