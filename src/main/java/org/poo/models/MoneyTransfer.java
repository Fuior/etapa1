package org.poo.models;

import lombok.Getter;

@Getter
public class MoneyTransfer extends Transaction {
    private final String senderIBAN;
    private final String receiverIBAN;
    private final String amount;
    private final String transferType;

    public MoneyTransfer(int timestamp, String description, String senderIBAN, String receiverIBAN,
                         String amount, String transferType) {

        super(timestamp, description);
        this.senderIBAN = senderIBAN;
        this.receiverIBAN = receiverIBAN;
        this.amount = amount;
        this.transferType = transferType;
    }
}
