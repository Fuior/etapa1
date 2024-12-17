package org.poo.models;

import lombok.Data;

@Data
public class SuccessfulDelete {
    private final String success = "Account deleted";
    private int timestamp;

    public SuccessfulDelete(int timestamp) {
        this.timestamp = timestamp;
    }
}
