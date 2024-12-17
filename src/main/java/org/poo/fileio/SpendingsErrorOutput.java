package org.poo.fileio;

import lombok.Data;

@Data
class Error {
    private final String error = "This kind of report is not supported for a saving account";
}

@Data
public class SpendingsErrorOutput {
    private final String command = "spendingsReport";
    private final Error output = new Error();
    private int timestamp;

    public SpendingsErrorOutput(int timestamp) {
        this.timestamp = timestamp;
    }
}
