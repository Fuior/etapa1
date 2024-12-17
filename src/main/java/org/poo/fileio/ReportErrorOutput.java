package org.poo.fileio;

import lombok.Data;

@Data
class ErrorOutput {
    private final String description = "Account not found";
    private int timestamp;

    public ErrorOutput(int timestamp) {
        this.timestamp = timestamp;
    }
}

@Data
public class ReportErrorOutput {
    private String command;
    private ErrorOutput output;
    private int timestamp;

    public ReportErrorOutput(String command, int timestamp) {
        this.command = command;
        this.output = new ErrorOutput(timestamp);
        this.timestamp = timestamp;
    }
}
