package org.poo.fileio;

import lombok.Data;
import org.poo.models.AccountService;
import org.poo.models.CardDetails;
import org.poo.models.UserDetails;

import java.util.ArrayList;
import java.util.List;

@Data
public class OutputGenerator {
    private String command;
    private List<UserOutput> output;
    private int timestamp;

    public OutputGenerator(String command, int timestamp) {
        this.command = command;
        this.output = new ArrayList<>();
        this.timestamp = timestamp;
    }

    public ArrayList<CardOutput> createCardsOutput(AccountService account) {
        ArrayList<CardOutput> cards = new ArrayList<>();

        for (CardDetails card : account.getCards()) {
            cards.add(new CardOutput(card.getCardNumber(), card.getCardStatus()));
        }

        return cards;
    }

    public ArrayList<AccountOutput> createAccountsOutput(UserDetails user) {
        ArrayList<AccountOutput> accounts = new ArrayList<>();

        for (AccountService account : user.getBankAccounts()) {
            accounts.add(new AccountOutput(account.getIBAN(),
                    account.getBalance(),
                    account.getCurrency(),
                    account.getAccountType(),
                    createCardsOutput(account)));
        }

        return accounts;
    }

    public void createUsersOutput(ArrayList<UserDetails> users) {
        for (UserDetails user : users) {
            output.add(new UserOutput(user.getUserInput().getFirstName(),
                    user.getUserInput().getLastName(),
                    user.getUserInput().getEmail(),
                    createAccountsOutput(user)));
        }
    }
}
