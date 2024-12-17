package org.poo.core;

import lombok.Data;
import org.poo.models.AccountService;
import org.poo.models.CardDetails;
import org.poo.models.Transaction;
import org.poo.models.UserDetails;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Data
class Status {
    private int timestamp;
    private String description;

    public Status(int timestamp, String description) {
        this.timestamp = timestamp;
        this.description = description;
    }
}

@Data
public class CardStatus {
    private final String command = "checkCardStatus";
    private Status output;
    private int timestamp;

    public CardStatus(int timestamp) {
        this.timestamp = timestamp;
    }

    private CardDetails findCard(String cardNumber, AccountService account) {
        for (CardDetails card : account.getCards()) {
            if (card.getCardNumber().equals(cardNumber)) {
                return card;
            }
        }

        return null;
    }

    private void setError(AtomicInteger error, AccountService account, CardDetails card) {

        if ((account.getBalance() <= account.getMinBalance()) ||
                (account.getBalance() - account.getMinBalance() <= 30) ||
                (card.getCardStatus().equals("frozen")) ||
                (account.getMinBalance() == 0)) {

            error.set(-1);
        }
    }

    private CardDetails findCard(String cardNumber, ArrayList<UserDetails> users,
                                 AtomicInteger error, int timestamp) {

        for (UserDetails user : users) {
            for (AccountService account : user.getBankAccounts()) {
                CardDetails card = findCard(cardNumber, account);

                if (card == null)
                    continue;

                if (account.getBalance() <= account.getMinBalance()) {
                    card.setCardStatus("frozen");

                    user.getTransactions().add(new Transaction(timestamp,
                            "You have reached the minimum amount of funds," +
                                    " the card will be frozen"));
                }

                setError(error, account, card);

                return card;

            }
        }

        return null;
    }

    public int checkStatus(int timestamp, String cardNumber, ArrayList<UserDetails> users) {
        AtomicInteger error = new AtomicInteger(0);
        CardDetails card = findCard(cardNumber, users, error, timestamp);

        if (card == null) {
            output = new Status(timestamp, "Card not found");
        } else {
            output = new Status(timestamp, card.getCardStatus());
        }

        return error.get();
    }
}
