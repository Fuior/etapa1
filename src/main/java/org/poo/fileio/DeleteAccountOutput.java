package org.poo.fileio;

import lombok.Data;

@Data
public class DeleteAccountOutput<T> {
    private final String command = "deleteAccount";
    private T output;
    private int timestamp;

    public DeleteAccountOutput(int timestamp) {
        this.timestamp = timestamp;
    }
}
