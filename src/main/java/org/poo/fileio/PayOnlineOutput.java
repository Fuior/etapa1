package org.poo.fileio;

import lombok.Data;

@Data
class Result {
    private final String description = "Card not found";
    private int timestamp;

    public Result(int timestamp) {
        this.timestamp = timestamp;
    }
}

@Data
public class PayOnlineOutput {
    private final String command = "payOnline";
    private Result output;
    private int timestamp;

    public PayOnlineOutput(int timestamp) {
        this.output = new Result(timestamp);
        this.timestamp = timestamp;
    }
}
