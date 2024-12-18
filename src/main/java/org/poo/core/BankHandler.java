package org.poo.core;

import lombok.Data;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.PayOnlineOutput;
import org.poo.models.UserDetails;

import java.util.ArrayList;

@Data
public class BankHandler implements IBankHandler {
    protected ArrayList<UserDetails> users;
    protected BankRepository bankRepository;
    private AccountServiceManager accountServiceManager;
    private CardServiceManager cardServiceManager;
    private TransactionService transactionService;
    private ReportingService reportingService;

    public BankHandler(ArrayList<UserDetails> users, BankRepository bankRepository) {
        this.users = users;
        this.bankRepository = bankRepository;
        this.accountServiceManager = new AccountServiceManager(bankRepository);
        this.cardServiceManager = new CardServiceManager(bankRepository);
        this.transactionService = new TransactionService(bankRepository, cardServiceManager);
        this.reportingService = new ReportingService(bankRepository);
    }

    @Override
    public void addAccount(CommandInput accountDetails) {
        accountServiceManager.add(accountDetails);
    }

    @Override
    public String deleteAccount(CommandInput accountDetails) {
        accountServiceManager.delete(accountDetails);
        return accountServiceManager.getError();
    }

    @Override
    public void addFunds(CommandInput fundsDetails) {
        accountServiceManager.addFunds(fundsDetails);
    }

    @Override
    public void setMinBalance(CommandInput balanceInput) {
        accountServiceManager.setMinBalance(balanceInput);
    }

    @Override
    public void setAlias(CommandInput aliasDetails) {
        accountServiceManager.setAlias(aliasDetails);
    }

    @Override
    public int addInterest(CommandInput interestDetails) {
        return accountServiceManager.addInterest(interestDetails);
    }

    @Override
    public int changeInterestRate(CommandInput interestDetails) {
        return accountServiceManager.changeInterestRate(interestDetails);
    }

    @Override
    public void addCard(CommandInput cardDetails) {
        cardServiceManager.add(cardDetails);
    }

    @Override
    public void deleteCard(CommandInput cardDetails) {
        cardServiceManager.delete(cardDetails);
    }

    @Override
    public PayOnlineOutput payOnline(CommandInput cardDetails, ExchangeInput[] exchangeRates) {
        return transactionService.payOnline(cardDetails, exchangeRates);
    }

    @Override
    public void sendMoney(CommandInput transferDetails, ExchangeInput[] exchangeRates) {
        transactionService.sendMoney(transferDetails, exchangeRates);
    }

    @Override
    public void splitPayment(CommandInput paymentDetails, ExchangeInput[] exchangeRates) {
        transactionService.splitPayment(paymentDetails, exchangeRates);
    }

    @Override
    public Report generateReport(CommandInput reportDetails) {
        return reportingService.generateReport(reportDetails);
    }
}
