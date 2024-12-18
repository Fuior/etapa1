package org.poo.core;

import org.poo.models.AccountService;
import org.poo.models.CardDetails;
import org.poo.models.UserDetails;

import java.util.HashMap;
import java.util.Map;

public class BankRepository {
    private static BankRepository instance;
    private final Map<String, UserDetails> userMapByEmail = new HashMap<>();
    private final Map<String, UserDetails> userMapByAccount = new HashMap<>();
    private final Map<String, AccountService> accountMap = new HashMap<>();
    private final Map<String, AccountService> accountMapByCard = new HashMap<>();
    private final Map<String, CardDetails> cardMap = new HashMap<>();

    public static synchronized BankRepository getInstance() {
        if (instance == null)
            instance = new BankRepository();

        return instance;
    }

    public void addUserByEmail(UserDetails user) {
        userMapByEmail.put(user.getUserInput().getEmail(), user);
    }

    public void addUserByAccount(UserDetails user, String account) {
        userMapByAccount.put(account, user);
    }

    public void deleteUser(AccountService account) {
        userMapByAccount.remove(account.getIBAN());
    }

    public void addAccount(AccountService account) {
        accountMap.put(account.getIBAN(), account);
    }

    public void addAccountByAlias(AccountService account, String alias) {
        accountMap.put(alias, account);
    }

    public void addAccountByCard(AccountService account, CardDetails card) {
        accountMapByCard.put(card.getCardNumber(), account);
    }

    public void deleteAccount(String account) {
        accountMap.remove(account);
    }

    public void deleteAccountByCard(CardDetails card) {
        accountMapByCard.remove(card.getCardNumber());
    }

    public void addCard(CardDetails card) {
        cardMap.put(card.getCardNumber(), card);
    }

    public void deleteCard(String cardNumber) {
        cardMap.remove(cardNumber);
    }

    public UserDetails findUser(String email) {
        return userMapByEmail.get(email);
    }

    public UserDetails findUserByAccount(AccountService account) {
        return userMapByAccount.get(account.getIBAN());
    }

    public AccountService findAccountByIBAN(String iban) {
        return accountMap.get(iban);
    }

    public AccountService findAccountByCard(String cardNumber) {
        return accountMapByCard.get(cardNumber);
    }

    public CardDetails findCardByNumber(String cardNumber) {
        return cardMap.get(cardNumber);
    }
}
