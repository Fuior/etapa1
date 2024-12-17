package org.poo.models;

import lombok.Data;

@Data
public class CardDetails {
    private String cardNumber;
    private String type;
    private int timestamp;
    private String cardStatus;

    public CardDetails(String cardNumber, String type, int timestamp) {
        this.cardNumber = cardNumber;
        this.type = type;
        this.timestamp = timestamp;
        this.cardStatus = "active";
    }
}
