package org.poo.core;

import org.poo.fileio.CommandInput;
import org.poo.models.AccountService;
import org.poo.models.UserDetails;

public class ReportingService extends BankRepositoryEntity {

    public ReportingService(BankRepository bankRepository) {
        super(bankRepository);
    }

    public Report generateReport(CommandInput reportDetails) {
        AccountService account = bankRepository.findAccountByIBAN(reportDetails.getAccount());

        if (account == null)
            return null;

        UserDetails user = bankRepository.findUserByAccount(account);

        Report report = new Report(reportDetails.getCommand(), reportDetails.getTimestamp());
        report.setOutput(reportDetails, user, account);

        return report;
    }
}
