package org.poo.fileio;

import lombok.Getter;

@Getter
public class ReportErrorOutput {
    private final String command;
    private final ErrorOutput output;
    private final int timestamp;

    public ReportErrorOutput(String command, int timestamp) {
        this.command = command;
        this.output = new ErrorOutput("Account not found", timestamp);
        this.timestamp = timestamp;
    }
}
