package org.poo.models;

import lombok.Data;

@Data
public class FailedDelete {
    private String error;
    private int timestamp;

    public FailedDelete(String error, int timestamp) {
        this.error = error;
        this.timestamp = timestamp;
    }
}
