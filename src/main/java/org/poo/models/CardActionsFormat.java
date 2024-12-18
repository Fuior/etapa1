package org.poo.models;

import lombok.Getter;

@Getter
public class CardActionsFormat extends Transaction {
    private final String card;
    private final String cardHolder;
    private final String account;

    public CardActionsFormat(int timestamp, String description,
                             String card, String cardHolder, String account) {

        super(timestamp, description);
        this.card = card;
        this.cardHolder = cardHolder;
        this.account = account;
    }
}
