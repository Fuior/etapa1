package org.poo.fileio;

import lombok.Data;

@Data
public class CardOutput {
    private String cardNumber;
    private String status;

    public CardOutput(String cardNumber, String status) {
        this.cardNumber = cardNumber;
        this.status = status;
    }
}
