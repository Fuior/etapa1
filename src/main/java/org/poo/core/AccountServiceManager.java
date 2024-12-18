package org.poo.core;

import lombok.Getter;
import org.poo.fileio.CommandInput;
import org.poo.models.AccountService;
import org.poo.models.Transaction;
import org.poo.models.UserDetails;

@Getter
public class AccountServiceManager extends BankRepositoryEntity implements ResourceManager {
    private String error;

    public AccountServiceManager(BankRepository bankRepository) {
        super(bankRepository);
        error = null;
    }

    public void add(CommandInput accountDetails) {
        UserDetails user = bankRepository.findUser(accountDetails.getEmail());

        AccountService bankAccount = new AccountService(accountDetails.getCurrency(),
                accountDetails.getAccountType(), accountDetails.getTimestamp());

        if (accountDetails.getAccountType().equals("savings"))
            bankAccount.setInterestRate(accountDetails.getInterestRate());

        user.getBankAccounts().add(bankAccount);
        user.getTransactions().add(new Transaction(accountDetails.getTimestamp(),
                "New account created"));

        bankRepository.addAccount(bankAccount);
        bankRepository.addUserByAccount(user, bankAccount.getIBAN());
    }

    public void delete(CommandInput accountDetails) {
        AccountService bankAccount = bankRepository.findAccountByIBAN(accountDetails.getAccount());

        String message = "Account couldn't be deleted - see org.poo.transactions for details";

        if (bankAccount == null) {
            error = message;
            return;
        }

        UserDetails user = bankRepository.findUserByAccount(bankAccount);

        if (bankAccount.getBalance() != 0) {
            user.getTransactions().add(new Transaction(accountDetails.getTimestamp(),
                    "Account couldn't be deleted - there are funds remaining"));

            error = message;
            return;
        }

        bankRepository.deleteUser(bankAccount);
        bankRepository.deleteAccount(bankAccount.getIBAN());

        if (user.getAlias() != null && user.getAlias().account() == bankAccount)
            bankRepository.deleteAccount(user.getAlias().name());

        bankAccount.getCards().clear();
        user.getBankAccounts().removeIf(a -> a.getIBAN().equals(bankAccount.getIBAN()));
    }

    public void addFunds(CommandInput fundsDetails) {
        AccountService account = bankRepository.findAccountByIBAN(fundsDetails.getAccount());

        if (account != null)
            account.setBalance(account.getBalance() + fundsDetails.getAmount());
    }

    public void setMinBalance(CommandInput balanceInput) {
        AccountService account = bankRepository.findAccountByIBAN(balanceInput.getAccount());

        if (account == null)
            return;

        account.setMinBalance(balanceInput.getAmount());
    }

    public void setAlias(CommandInput aliasDetails) {
        UserDetails user = bankRepository.findUser(aliasDetails.getEmail());
        AccountService account = bankRepository.findAccountByIBAN(aliasDetails.getAccount());

        user.setAlias(new UserDetails.Alias(aliasDetails.getAlias(), account));
        bankRepository.addAccountByAlias(account, aliasDetails.getAlias());
    }

    public int addInterest(CommandInput interestDetails) {
        AccountService accout = bankRepository.findAccountByIBAN(interestDetails.getAccount());

        if (accout == null)
            return 0;

        if (accout.getAccountType().equals("classic"))
            return -1;

        double interest = accout.getBalance() * accout.getInterestRate();
        accout.setBalance(accout.getBalance() + interest);

        return 0;
    }

    public int changeInterestRate(CommandInput interestDetails) {
        AccountService accout = bankRepository.findAccountByIBAN(interestDetails.getAccount());

        if (accout == null)
            return 0;

        if (accout.getAccountType().equals("classic"))
            return -1;

        accout.setInterestRate(interestDetails.getInterestRate());
        String description = "Interest rate of the account changed to " + accout.getInterestRate();

        UserDetails user = bankRepository.findUserByAccount(accout);
        user.getTransactions().add(new Transaction(interestDetails.getTimestamp(), description));

        return 0;
    }
}
