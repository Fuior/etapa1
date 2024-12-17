package org.poo.fileio;

import lombok.Data;

import java.util.ArrayList;

@Data
public class UserOutput {
    private String firstName;
    private String lastName;
    private String email;
    private ArrayList<AccountOutput> accounts;

    public UserOutput(String firstName, String lastName, String email,
                      ArrayList<AccountOutput> accounts) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.accounts = accounts;
    }
}
