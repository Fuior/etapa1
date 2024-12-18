package org.poo.core;

public abstract class BankRepositoryEntity {
    protected final BankRepository bankRepository;

    public BankRepositoryEntity(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }
}
