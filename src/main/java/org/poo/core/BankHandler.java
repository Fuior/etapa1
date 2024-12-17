package org.poo.core;

import lombok.Data;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.PayOnlineOutput;
import org.poo.fileio.UserInput;
import org.poo.models.*;
import org.poo.utils.Utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Data
public class BankHandler {
    private ArrayList<UserDetails> users;

    public BankHandler(UserInput[] usersInputs) {
        this.users = new ArrayList<>();
        for (UserInput user : usersInputs) {
            users.add(new UserDetails(user));
        }
    }

    public UserDetails findUser(String email) {
        UserDetails user = null;

        for (UserDetails u : users) {
            if (u.getUserInput().getEmail().equals(email)) {
                user = u;
                break;
            }
        }

        return user;
    }

    private UserDetails findUser(AccountService account) {
        UserDetails user = null;

        for (UserDetails u : users) {
            for (AccountService a : u.getBankAccounts()) {
                if (a == account) {
                    user = u;
                    break;
                }
            }
        }

        return user;
    }

    private AccountService findBankAccount(String IBAN) {
        AccountService bankAccount = null;

        for (UserDetails user : users) {
            for (AccountService account : user.getBankAccounts()) {
                if (IBAN.startsWith("RO")) {
                    if (account.getIBAN().equals(IBAN)) {
                        bankAccount = account;
                        break;
                    }
                } else if (user.getAlias() != null &&
                        account.getIBAN().equals(user.getAlias().getAccount().getIBAN())) {

                    bankAccount = account;
                    break;
                }
            }
        }

        return bankAccount;
    }

    private int findBankAccount(String IBAN, UserDetails user) {

        for (int i = 0; i < user.getBankAccounts().size(); i++) {
            if (IBAN.startsWith("RO")) {
                if (user.getBankAccounts().get(i).getIBAN().equals(IBAN)) {
                    return i;
                }
            } else if (user.getAlias() != null &&
                        user.getBankAccounts().get(i).getIBAN()
                            .equals(user.getAlias().getAccount().getIBAN())) {

                return i;
            }
        }
        return -1;
    }

    public void addAccount(CommandInput accountDetails) {
        UserDetails user = findUser(accountDetails.getEmail());

        AccountService bankAccount = new AccountService(accountDetails.getCurrency(),
                accountDetails.getAccountType(), accountDetails.getTimestamp());

        if (accountDetails.getAccountType().equals("savings")) {
            bankAccount.setInterestRate(accountDetails.getInterestRate());
        }

        user.getBankAccounts().add(bankAccount);
        user.getTransactions().add(new Transaction(accountDetails.getTimestamp(),
                            "New account created"));
    }

    public String deleteAccount(CommandInput accountDetails) {
        AccountService bankAccount = findBankAccount(accountDetails.getAccount());

        String message = "Account couldn't be deleted - see org.poo.transactions for details";

        if (bankAccount == null)
            return message;

        for (UserDetails user : users) {
            if (user.getUserInput().getEmail().equals(accountDetails.getEmail())) {
                int pos = findBankAccount(accountDetails.getAccount(), user);

                if (bankAccount.getBalance() != 0) {
                    user.getTransactions().add(new Transaction(accountDetails.getTimestamp(),
                            "Account couldn't be deleted - there are funds remaining"));

                    return message;
                }

                bankAccount.getCards().clear();
                user.getBankAccounts().remove(pos);
                break;
            }
        }

        return null;
    }

    public void addFunds(CommandInput fundsDetails) {
        AccountService account = findBankAccount(fundsDetails.getAccount());
        if (account == null)
            return;

        account.setBalance(account.getBalance() + fundsDetails.getAmount());
        account.getFundsHistory().add(new FundsDetails(fundsDetails.getAmount(),
                fundsDetails.getTimestamp()));
    }

    public void addCard(CommandInput cardDetails) {
        AccountService account = findBankAccount(cardDetails.getAccount());
        if (account == null)
            return;

        String type;
        if (cardDetails.getCommand().equals("createCard")) {
            type = "basic card";
        } else {
            type = "one time card";
        }

        UserDetails user = findUser(cardDetails.getEmail());
        if (user == null)
            return;

        String cardNumber = Utils.generateCardNumber();
        account.getCards().add(new CardDetails(cardNumber ,type, cardDetails.getTimestamp()));

        if (account.getAccountType().equals("classic"))
            user.getTransactions().add(new CardActionsFormat(cardDetails.getTimestamp(),
                                    "New card created", cardNumber,
                                    user.getUserInput().getEmail(), account.getIBAN()));
    }

    public void deleteCard(CommandInput cardDetails) {
        for (UserDetails user : users) {
            for (AccountService account : user.getBankAccounts()) {
                for (int i = 0; i < account.getCards().size(); i++) {
                    if (account.getCards().get(i).getCardNumber().equals(cardDetails.getCardNumber())) {
                        user.getTransactions().add(new CardActionsFormat(cardDetails.getTimestamp(),
                                                "The card has been destroyed",
                                                account.getCards().get(i).getCardNumber(),
                                                user.getUserInput().getEmail(), account.getIBAN()));

                        account.getCards().remove(i);
                        break;
                    }
                }
            }
        }
    }

    private double getAmount(String receiverCurrency, double value,
                             String senderCurrency, ExchangeInput[] exchangeRates) {

        if (!senderCurrency.equals(receiverCurrency)) {
            CurrencyExchange currencyExchange = new CurrencyExchange(exchangeRates);
            double rate = currencyExchange.findRate(senderCurrency, receiverCurrency);

            double amount = (value * rate);
            double scale = Math.pow(10, 14);

            return  ((Math.round(amount * scale)) / scale);
        }

        return value;
    }

    public PayOnlineOutput payOnline(CommandInput cardDetails, ExchangeInput[] exchangeRates) {
        UserDetails user = findUser(cardDetails.getEmail());

        for (AccountService account : user.getBankAccounts()) {
            for (CardDetails c : account.getCards()) {
                if (c.getCardNumber().equals(cardDetails.getCardNumber())) {
                    double amount = getAmount(account.getCurrency(), cardDetails.getAmount(),
                                                cardDetails.getCurrency(), exchangeRates);

                    if (c.getCardStatus().equals("frozen")) {
                        user.getTransactions().add(new Transaction(cardDetails.getTimestamp(),
                                                    "The card is frozen"));
                        return null;
                    }

                    if (account.getBalance() >= amount) {
                        BigDecimal firstValue = BigDecimal.valueOf(account.getBalance());
                        BigDecimal secondValue = BigDecimal.valueOf(amount);

                        BigDecimal result = firstValue.subtract(secondValue);
                        account.setBalance(result.doubleValue());

                        CardPaymentFormat cardPayment = new CardPaymentFormat(cardDetails.getTimestamp(),
                                            "Card payment", amount, cardDetails.getCommerciant());

                        user.getTransactions().add(cardPayment);
                        account.getCardPayments().add(cardPayment);

                        if (c.getType().equals("one time card")) {
                            user.getTransactions().add(new CardActionsFormat(cardDetails.getTimestamp(),
                                                    "The card has been destroyed", c.getCardNumber(),
                                                    user.getUserInput().getEmail(), account.getIBAN()));

                            c.setCardNumber(Utils.generateCardNumber());

                            user.getTransactions().add(new CardActionsFormat(cardDetails.getTimestamp(),
                                                        "New card created", c.getCardNumber(),
                                                        user.getUserInput().getEmail(), account.getIBAN()));
                        }
                    } else {
                        user.getTransactions().add(new Transaction(cardDetails.getTimestamp(),
                                                    "Insufficient funds"));
                    }

                    return null;
                }
            }
        }

        return new PayOnlineOutput(cardDetails.getTimestamp());
    }

    public void sendMoney(CommandInput transferDetails, ExchangeInput[] exchangeRates) {
        if (!transferDetails.getAccount().startsWith("RO"))
            return;

        AccountService sender = findBankAccount(transferDetails.getAccount());
        AccountService receiver = findBankAccount(transferDetails.getReceiver());

        if (sender == null || receiver == null)
            return;

        UserDetails u1 = findUser(sender);
        UserDetails u2 = findUser(receiver);
        double amount = getAmount(receiver.getCurrency(), transferDetails.getAmount(),
                                    sender.getCurrency(), exchangeRates);

        if (sender.getBalance() >= transferDetails.getAmount()) {
            DecimalFormat df = new DecimalFormat("#.0");
            String money = df.format(transferDetails.getAmount()) + " " + sender.getCurrency();

            MoneyTransfer moneySent = new MoneyTransfer(transferDetails.getTimestamp(),
                                        transferDetails.getDescription(), sender.getIBAN(),
                                        receiver.getIBAN(), money, "sent");

            sender.setBalance(sender.getBalance() - transferDetails.getAmount());
            u1.getTransactions().add(moneySent);

            money = amount + " " + receiver.getCurrency();
            MoneyTransfer moneyReceived = new MoneyTransfer(transferDetails.getTimestamp(),
                                            transferDetails.getDescription(), sender.getIBAN(),
                                            receiver.getIBAN(), money, "received");

            receiver.setBalance(receiver.getBalance() + amount);
            u2.getTransactions().add(moneyReceived);
        } else {
            u1.getTransactions().add(new Transaction(transferDetails.getTimestamp(),
                                    "Insufficient funds"));
        }
    }

    public void setAlias(CommandInput aliasDetails) {
        UserDetails user = findUser(aliasDetails.getEmail());
        AccountService account = findBankAccount(aliasDetails.getAccount());

        user.setAlias(new Alias(aliasDetails.getAlias(), account));
    }

    public void setMinBalance(CommandInput balanceInput) {
        AccountService account = findBankAccount(balanceInput.getAccount());

        if (account == null)
            return;

        account.setMinBalance(balanceInput.getAmount());
    }

    private ArrayList<AccountService> getAccounts(List<String> IBANs) {
        ArrayList<AccountService> accounts = new ArrayList<>();

        for (String IBAN : IBANs) {
            accounts.add(findBankAccount(IBAN));
        }

        return accounts;
    }

    private String isPaymentValid(ArrayList<AccountService> accounts, double amount,
                                   String currency, ExchangeInput[] exchangeRates) {

        String IBAN = null;

        for (AccountService a : accounts) {
            double value = getAmount(a.getCurrency(), amount, currency, exchangeRates);
            if (a.getBalance() < value) {
                IBAN =  a.getIBAN();
            }
        }

        return IBAN;
    }

    private void setTransactionError(ArrayList<AccountService> accounts, int timestamp, String description,
                                     SplitPaymentFormat payment, String IBAN) {

        for (AccountService a : accounts) {
            UserDetails user = findUser(a);
            user.getTransactions().add(new SplitPaymentError(timestamp, description, payment, IBAN));
        }
    }

    public void splitPayment(CommandInput paymentDetails, ExchangeInput[] exchangeRates) {
        double valuePerPerson = paymentDetails.getAmount() / paymentDetails.getAccounts().size();
        ArrayList<AccountService> accounts = getAccounts(paymentDetails.getAccounts());

        DecimalFormat df = new DecimalFormat("#.00");
        String money = df.format(paymentDetails.getAmount()) + " " + paymentDetails.getCurrency();
        String description = "Split payment of " + money;

        SplitPaymentFormat payment = new SplitPaymentFormat(paymentDetails.getTimestamp(),
                                                description, paymentDetails.getCurrency(),
                                                valuePerPerson, paymentDetails.getAccounts());

        String isValid = isPaymentValid(accounts, valuePerPerson,
                                        paymentDetails.getCurrency(), exchangeRates);

        if (isValid != null) {
            setTransactionError(accounts, paymentDetails.getTimestamp(), description, payment, isValid);
            return;
        }

        for (AccountService a : accounts) {
            double amount = getAmount(a.getCurrency(), valuePerPerson,
                            paymentDetails.getCurrency(), exchangeRates);

            a.setBalance(a.getBalance() - amount);
            UserDetails user = findUser(a);
            user.getTransactions().add(payment);
        }
    }

    public int addInterest(CommandInput interestDetails) {
        AccountService accout = findBankAccount(interestDetails.getAccount());

        if (accout == null)
            return 0;

        if (accout.getAccountType().equals("classic"))
            return -1;

        double interest = accout.getBalance() * accout.getInterestRate();
        accout.setBalance(accout.getBalance() + interest);

        return 0;
    }

    public int changeInterestRate(CommandInput interestDetails) {
        AccountService accout = findBankAccount(interestDetails.getAccount());

        if (accout == null)
            return 0;

        if (accout.getAccountType().equals("classic"))
            return -1;

        accout.setInterestRate(interestDetails.getInterestRate());
        String description = "Interest rate of the account changed to " + accout.getInterestRate();

        UserDetails user = findUser(accout);
        user.getTransactions().add(new Transaction(interestDetails.getTimestamp(), description));

        return 0;
    }

    public Report getReport(CommandInput reportDetails) {
        AccountService account = findBankAccount(reportDetails.getAccount());

        if (account == null)
            return null;

        UserDetails user = findUser(account);

        Report report = new Report(reportDetails.getCommand(), reportDetails.getTimestamp());
        report.setOutput(reportDetails, user, account);

        return report;
    }
}
