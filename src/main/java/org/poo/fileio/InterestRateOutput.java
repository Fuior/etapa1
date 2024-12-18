package org.poo.fileio;

import lombok.Getter;
import org.poo.models.Transaction;

@Getter
public class InterestRateOutput {
    private final String command;
    private final Transaction output;
    private final int timestamp;

    public InterestRateOutput(String command, int timestamp) {
        this.command = command;
        this.output = new Transaction(timestamp, "This is not a savings account");
        this.timestamp = timestamp;
    }
}
