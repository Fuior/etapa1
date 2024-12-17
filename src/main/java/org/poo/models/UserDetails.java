package org.poo.models;

import lombok.Data;
import org.poo.fileio.UserInput;

import java.util.ArrayList;

@Data
public class UserDetails {
    private UserInput userInput;
    private ArrayList<AccountService> bankAccounts;
    private Alias alias;
    private ArrayList<Transaction> transactions;

    public UserDetails(UserInput userInput) {
        this.userInput = userInput;
        this.bankAccounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }
}
