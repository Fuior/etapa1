package org.poo.models;

import lombok.Getter;
import lombok.Setter;
import org.poo.utils.Utils;

import java.util.ArrayList;

@Getter
@Setter
public class AccountService {
    private String IBAN;
    private double balance;
    private double minBalance;
    private String currency;
    private String accountType;
    private int timestamp;
    private double interestRate;
    private ArrayList<CardDetails> cards;
    private ArrayList<CardPaymentFormat> cardPayments;

    public AccountService(String currency, String accountType, int timestamp) {
        this.IBAN = Utils.generateIBAN();
        this.balance = 0;
        this.minBalance = 0;
        this.currency = currency;
        this.accountType = accountType;
        this.timestamp = timestamp;
        this.cards = new ArrayList<>();
        this.cardPayments = new ArrayList<>();
    }
}
