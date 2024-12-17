package org.poo.models;

import lombok.Getter;

@Getter
public class MoneyTransfer extends Transaction {
    private String senderIBAN;
    private String receiverIBAN;
    private String amount;
    private String transferType;

    public MoneyTransfer(int timestamp, String description, String senderIBAN, String receiverIBAN,
                         String amount, String transferType) {

        super(timestamp, description);
        this.senderIBAN = senderIBAN;
        this.receiverIBAN = receiverIBAN;
        this.amount = amount;
        this.transferType = transferType;
    }
}
