package org.poo.core;

import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.PayOnlineOutput;

public interface IBankHandler {

    // Account Operations
    void addAccount(CommandInput accountDetails);
    String deleteAccount(CommandInput accountDetails);
    void addFunds(CommandInput fundsDetails);
    void setMinBalance(CommandInput balanceInput);
    void setAlias(CommandInput aliasDetails);
    int addInterest(CommandInput interestDetails);
    int changeInterestRate(CommandInput interestDetails);

    // Card Operations
    void addCard(CommandInput cardDetails);
    void deleteCard(CommandInput cardDetails);

    // Transaction Operations
    PayOnlineOutput payOnline(CommandInput cardDetails, ExchangeInput[] exchangeRates);
    void sendMoney(CommandInput transferDetails, ExchangeInput[] exchangeRates);
    void splitPayment(CommandInput paymentDetails, ExchangeInput[] exchangeRates);

    // Reporting Operations
    Report generateReport(CommandInput reportDetails);
}
