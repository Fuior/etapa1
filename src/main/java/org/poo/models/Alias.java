package org.poo.models;

import lombok.Data;

@Data
public class Alias {
    private String name;
    private AccountService account;

    public Alias(String name, AccountService account) {
        this.name = name;
        this.account = account;
    }
}
