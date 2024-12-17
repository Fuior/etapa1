package org.poo.fileio;

import lombok.Data;

@Data
class OutputFormat {
    private int timestamp;
    private final String description = "This is not a savings account";

    public OutputFormat(int timestamp) {
        this.timestamp = timestamp;
    }
}

@Data
public class InterestRateOutput {
    private String command;
    private OutputFormat output;
    private int timestamp;

    public InterestRateOutput(String command, int timestamp) {
        this.command = command;
        this.output = new OutputFormat(timestamp);
        this.timestamp = timestamp;
    }
}
