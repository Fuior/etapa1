package org.poo.core;

import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.PayOnlineOutput;
import org.poo.models.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TransactionService extends BankRepositoryEntity {
    private final CardServiceManager cardServiceManager;

    public TransactionService(BankRepository bankRepository, CardServiceManager cardServiceManager) {
        super(bankRepository);
        this.cardServiceManager = cardServiceManager;
    }

    private double getAmount(String receiverCurrency, double value,
                             String senderCurrency, ExchangeInput[] exchangeRates) {

        if (!senderCurrency.equals(receiverCurrency)) {
            CurrencyExchange currencyExchange = new CurrencyExchange(exchangeRates);

            double rate = currencyExchange.findRate(senderCurrency, receiverCurrency);
            double scale = Math.pow(10, 14);

            return  ((Math.round((value * rate) * scale)) / scale);
        }

        return value;
    }

    private void pay(UserDetails user, AccountService account, CardDetails card,
                     CommandInput cardDetails, double amount) {

        BigDecimal firstValue = BigDecimal.valueOf(account.getBalance());
        BigDecimal secondValue = BigDecimal.valueOf(amount);

        BigDecimal result = firstValue.subtract(secondValue);
        account.setBalance(result.doubleValue());

        CardPaymentFormat cardPayment = new CardPaymentFormat(cardDetails.getTimestamp(),
                "Card payment", amount, cardDetails.getCommerciant());

        user.getTransactions().add(cardPayment);
        account.getCardPayments().add(cardPayment);

        if (card.getType().equals("one time card")) {
            cardServiceManager.replaceCard(user, account, card, cardDetails.getTimestamp());
        }
    }

    public PayOnlineOutput payOnline(CommandInput cardDetails, ExchangeInput[] exchangeRates) {
        CardDetails card = bankRepository.findCardByNumber(cardDetails.getCardNumber());

        if (card == null)
            return new PayOnlineOutput(cardDetails.getTimestamp());

        UserDetails user = bankRepository.findUser(cardDetails.getEmail());

        if (card.getCardStatus().equals("frozen")) {
            user.getTransactions().add(new Transaction(cardDetails.getTimestamp(),
                    "The card is frozen"));
            return null;
        }

        AccountService account = bankRepository.findAccountByCard(card.getCardNumber());
        double amount = getAmount(account.getCurrency(), cardDetails.getAmount(),
                cardDetails.getCurrency(), exchangeRates);

        if (account.getBalance() >= amount) {
            pay(user, account, card, cardDetails, amount);
        } else {
            user.getTransactions().add(new Transaction(cardDetails.getTimestamp(),
                    "Insufficient funds"));
        }

        return null;
    }

    private void send(CommandInput transferDetails, AccountService sender,
                      AccountService receiver, UserDetails user) {

        DecimalFormat df = new DecimalFormat("#.0");
        String money = df.format(transferDetails.getAmount()) + " " + sender.getCurrency();

        MoneyTransfer moneySent = new MoneyTransfer(transferDetails.getTimestamp(),
                transferDetails.getDescription(), sender.getIBAN(),
                receiver.getIBAN(), money, "sent");

        sender.setBalance(sender.getBalance() - transferDetails.getAmount());
        user.getTransactions().add(moneySent);
    }

    private void receive(CommandInput transferDetails, AccountService sender,
                         AccountService receiver, UserDetails user, double amount) {

        String money = amount + " " + receiver.getCurrency();

        MoneyTransfer moneyReceived = new MoneyTransfer(transferDetails.getTimestamp(),
                transferDetails.getDescription(), sender.getIBAN(),
                receiver.getIBAN(), money, "received");

        receiver.setBalance(receiver.getBalance() + amount);
        user.getTransactions().add(moneyReceived);
    }

    public void sendMoney(CommandInput transferDetails, ExchangeInput[] exchangeRates) {
        if (!transferDetails.getAccount().startsWith("RO"))
            return;

        AccountService sender = bankRepository.findAccountByIBAN(transferDetails.getAccount());
        AccountService receiver = bankRepository.findAccountByIBAN(transferDetails.getReceiver());

        if (sender == null || receiver == null)
            return;

        UserDetails u1 = bankRepository.findUserByAccount(sender);
        UserDetails u2 = bankRepository.findUserByAccount(receiver);

        double amount = getAmount(receiver.getCurrency(), transferDetails.getAmount(),
                sender.getCurrency(), exchangeRates);

        if (sender.getBalance() >= transferDetails.getAmount()) {
            send(transferDetails, sender, receiver, u1);
            receive(transferDetails, sender, receiver, u2, amount);
        } else {
            u1.getTransactions().add(new Transaction(transferDetails.getTimestamp(),
                    "Insufficient funds"));
        }
    }

    private ArrayList<AccountService> getAccounts(List<String> IBANs) {
        ArrayList<AccountService> accounts = new ArrayList<>();

        for (String IBAN : IBANs) {
            accounts.add(bankRepository.findAccountByIBAN(IBAN));
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
            UserDetails user = bankRepository.findUserByAccount(a);
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
            UserDetails user = bankRepository.findUserByAccount(a);
            user.getTransactions().add(payment);
        }
    }
}
