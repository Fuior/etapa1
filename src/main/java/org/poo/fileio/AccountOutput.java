package org.poo.fileio;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

@Data
public class AccountOutput {
    @JsonProperty("IBAN")
    private String IBAN;
    private double balance;
    private String currency;
    private String type;
    private ArrayList<CardOutput> cards;

    public AccountOutput(String IBAN, double balance, String currency, String type,
                         ArrayList<CardOutput> cards) {
        this.IBAN = IBAN;
        this.balance = balance;
        this.currency = currency;
        this.type = type;
        this.cards = cards;
    }
}
