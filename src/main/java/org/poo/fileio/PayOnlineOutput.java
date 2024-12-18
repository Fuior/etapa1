package org.poo.fileio;

import lombok.Getter;

@Getter
public class PayOnlineOutput {
    private final String command = "payOnline";
    private final ErrorOutput output;
    private final int timestamp;

    public PayOnlineOutput(int timestamp) {
        this.output = new ErrorOutput("Card not found", timestamp);
        this.timestamp = timestamp;
    }
}
