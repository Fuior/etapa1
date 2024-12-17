package org.poo.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Data;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ObjectInput;
import org.poo.utils.Utils;

@Data
public class CommandProcessor {

    public void execute(ObjectInput inputData, ObjectMapper objectMapper, ArrayNode output) {
        BankHandler bank = new BankHandler(inputData.getUsers());
        Utils.resetRandom();

        for (CommandInput commandInput : inputData.getCommands()) {
            OutputHandler outputHandler = new OutputHandler(commandInput, bank, objectMapper, output);

            switch (commandInput.getCommand()) {
                case "printUsers" -> outputHandler.printUsers();

                case "addAccount" -> bank.addAccount(commandInput);

                case "createCard", "createOneTimeCard" -> bank.addCard(commandInput);

                case "addFunds" -> bank.addFunds(commandInput);

                case "deleteAccount" -> outputHandler.deleteAccount();

                case "deleteCard" -> bank.deleteCard(commandInput);

                case "payOnline" -> outputHandler.payOnline(inputData.getExchangeRates());

                case "sendMoney" -> bank.sendMoney(commandInput, inputData.getExchangeRates());

                case "setAlias" -> bank.setAlias(commandInput);

                case "printTransactions" -> outputHandler.printTransactions();

                case "checkCardStatus" -> outputHandler.checkCardStatus();

                case "setMinBalance" -> bank.setMinBalance(commandInput);

                case "splitPayment" -> bank.splitPayment(commandInput, inputData.getExchangeRates());

                case "addInterest", "changeInterestRate" -> outputHandler.interestRate();

                case "report" -> outputHandler.getReport(commandInput);

                case "spendingsReport" -> outputHandler.getSpendingReport(commandInput);
            }
        }
    }
}
