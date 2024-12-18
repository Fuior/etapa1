package org.poo.core;

import org.poo.fileio.CommandInput;
import org.poo.models.AccountService;
import org.poo.models.CardActionsFormat;
import org.poo.models.CardDetails;
import org.poo.models.UserDetails;
import org.poo.utils.Utils;

public class CardServiceManager extends BankRepositoryEntity implements ResourceManager {

    public CardServiceManager(BankRepository bankRepository) {
        super(bankRepository);
    }

    public void add(CommandInput cardDetails) {
        AccountService account = bankRepository.findAccountByIBAN(cardDetails.getAccount());
        UserDetails user = bankRepository.findUser(cardDetails.getEmail());

        if (account == null || user == null)
            return;

        String type = cardDetails.getCommand().equals("createCard") ? "basic card" : "one time card";
        String cardNumber = Utils.generateCardNumber();

        account.getCards().add(new CardDetails(cardNumber ,type, cardDetails.getTimestamp()));

        bankRepository.addCard(account.getCards().getLast());
        bankRepository.addAccountByCard(account, account.getCards().getLast());

        if (account.getAccountType().equals("classic"))
            user.getTransactions().add(new CardActionsFormat(cardDetails.getTimestamp(),
                    "New card created", cardNumber,
                    user.getUserInput().getEmail(), account.getIBAN()));
    }

    public void delete(CommandInput cardDetails) {
        CardDetails card = bankRepository.findCardByNumber(cardDetails.getCardNumber());

        if (card == null)
            return;

        AccountService account = bankRepository.findAccountByCard(cardDetails.getCardNumber());
        UserDetails user = bankRepository.findUserByAccount(account);

        user.getTransactions().add(new CardActionsFormat(cardDetails.getTimestamp(),
                "The card has been destroyed", card.getCardNumber(),
                user.getUserInput().getEmail(), account.getIBAN()));

        bankRepository.deleteCard(card.getCardNumber());
        bankRepository.deleteAccountByCard(card);

        account.getCards().removeIf(c -> c.getCardNumber().equals(card.getCardNumber()));
    }

    public void replaceCard(UserDetails user, AccountService account,
                             CardDetails card, int timestamp) {

        user.getTransactions().add(new CardActionsFormat(timestamp,
                "The card has been destroyed", card.getCardNumber(),
                user.getUserInput().getEmail(), account.getIBAN()));

        bankRepository.deleteCard(card.getCardNumber());
        bankRepository.deleteAccountByCard(card);

        card.setCardNumber(Utils.generateCardNumber());

        bankRepository.addCard(card);
        bankRepository.addAccountByCard(account, card);

        user.getTransactions().add(new CardActionsFormat(timestamp,
                "New card created", card.getCardNumber(),
                user.getUserInput().getEmail(), account.getIBAN()));
    }
}
