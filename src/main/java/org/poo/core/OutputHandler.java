package org.poo.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.*;
import org.poo.models.FailedDelete;
import org.poo.models.SuccessfulDelete;

public class OutputHandler {
    private final CommandInput commandInput;
    private final BankHandler bank;
    private final ObjectMapper objectMapper;
    private final ArrayNode output;

    public OutputHandler(CommandInput commandInput, BankHandler bank,
                         ObjectMapper objectMapper, ArrayNode output) {

        this.commandInput = commandInput;
        this.bank = bank;
        this.objectMapper = objectMapper;
        this.output = output;
    }

    public void printUsers() {
        OutputGenerator myOutput;
        myOutput = new OutputGenerator("printUsers", commandInput.getTimestamp());

        myOutput.createUsersOutput(bank.getUsers());
        output.add(objectMapper.valueToTree(myOutput));
    }

    public void deleteAccount() {
        DeleteAccountOutput<Object> myOutput;
        myOutput = new DeleteAccountOutput<>(commandInput.getTimestamp());

        String error = bank.deleteAccount(commandInput);

        if (error == null) {
            myOutput.setOutput(new SuccessfulDelete(commandInput.getTimestamp()));
        } else {
            myOutput.setOutput(new FailedDelete(error, commandInput.getTimestamp()));
        }

        output.add(objectMapper.valueToTree(myOutput));
    }

    public void payOnline(ExchangeInput[] exchangeRates) {
        PayOnlineOutput myOutput = bank.payOnline(commandInput, exchangeRates);

        if (myOutput != null)
            output.add(objectMapper.valueToTree(myOutput));
    }

    public void checkCardStatus() {
        CardStatus myOutput = new CardStatus(commandInput.getTimestamp());

        int result = myOutput.checkStatus(commandInput.getTimestamp(),
                commandInput.getCardNumber(), bank.getUsers());

        if (result == 0)
            output.add(objectMapper.valueToTree(myOutput));
    }

    public void printTransactions() {
        TransactionsOutput myOutput = new TransactionsOutput(commandInput.getTimestamp());
        myOutput.setOutput(bank.getBankRepository(), commandInput.getEmail());
        output.add(objectMapper.valueToTree(myOutput));
    }

    public void interestRate() {
        int result;

        if (commandInput.getCommand().equals("addInterest")) {
            result = bank.addInterest(commandInput);
        } else {
            result = bank.changeInterestRate(commandInput);
        }

        if (result == 0)
            return;

        InterestRateOutput myOutput = new InterestRateOutput(commandInput.getCommand(),
                                                            commandInput.getTimestamp());
        output.add(objectMapper.valueToTree(myOutput));
    }

    public void getReport(CommandInput reportDetails) {
        Report report = bank.generateReport(commandInput);

        if (report == null) {
            ReportErrorOutput myOutput = new ReportErrorOutput(reportDetails.getCommand(),
                                                                reportDetails.getTimestamp());
            output.add(objectMapper.valueToTree(myOutput));
            return;
        }

        if (report.getOutput() != null)
            output.add(objectMapper.valueToTree(report));
    }

    public void getSpendingReport(CommandInput reportDetails) {
        Report report = bank.generateReport(commandInput);

        if (report == null) {
            ReportErrorOutput myOutput = new ReportErrorOutput(reportDetails.getCommand(),
                    reportDetails.getTimestamp());
            output.add(objectMapper.valueToTree(myOutput));
            return;
        }

        if (report.getOutput() != null) {
            output.add(objectMapper.valueToTree(report));
        } else {
            SpendingsErrorOutput myOutput = new SpendingsErrorOutput(commandInput.getTimestamp());
            output.add(objectMapper.valueToTree(myOutput));
        }
    }
}
