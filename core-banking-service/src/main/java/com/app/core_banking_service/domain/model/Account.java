package com.app.core_banking_service.domain.model;

import java.util.UUID;

import com.app.core_banking_service.domain.exception.InsufficientFundsException;
import com.app.core_banking_service.domain.exception.InvalidTransactionAmountException;
import com.app.core_banking_service.domain.exception.AccountFrozenException;
import java.time.Instant;

public class Account {
    
    private final Long id;

    private final UUID accountNumber;

    private final UUID customerId;

    private final AccountType accountType;

    private Money balance;

    private Money reservedBalance;

    private AccountStatus status;

    private final Long version;

    private final Instant createdAt;

    private Instant updatedAt;

    private Account(Long id, UUID accountNumber, UUID customerId, AccountType accountType,
                    Money balance, Money reservedBalance, AccountStatus status, Long version,
                    Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = balance;
        this.reservedBalance = reservedBalance;
        this.status = status;
        this.version = version;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Account open(UUID customerId, AccountType accountType, String currencyCode){

        return new Account(
            null,
            UUID.randomUUID(),
            customerId,
            accountType,
            Money.zero(currencyCode),
            Money.zero(currencyCode),
            AccountStatus.ACTIVE,
            0L,
            Instant.now(),
            Instant.now()
        );
    }

    public void debit(Money amount){
        if(amount.isZeroOrNegative()){
            throw new InvalidTransactionAmountException("Debit amount must be strictly greater than zero");
        }
        if(!this.status.canDebit()){
            throw new AccountFrozenException("Account " + this.accountNumber + " is " + this.status + " and cannot be debited");
        }

        Money availableBalance = getAvailableBalance();
        if (!availableBalance.isGreaterThanOrEqualTo(amount)
                && !this.accountType.isOverdraftPermitted()) {
            throw new InsufficientFundsException("Insufficient funds for account " + this.accountNumber
                    + ". Available: " + availableBalance + ", Requested: " + amount);
           } 

        this.balance = this.balance.subtract(amount);

        this.updatedAt  = Instant.now();
    }

    public Money getAvailableBalance(){
        return this.balance.subtract(this.reservedBalance);
    }

    public void freeze(){
        this.status = AccountStatus.FROZEN;
        this.updatedAt = Instant.now();
    }

    public void unfreeze(){
        this.status = AccountStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public Long getId(){
        return id;
    }
    public UUID getAccountNumber() { return accountNumber;}
    public UUID getCustomerId() { return customerId;}
    public AccountType getAccountType(){ return accountType;}
    public Money getBalance() { return balance;}
    public Money getReservedBalance() { return reservedBalance;}
    public AccountStatus getStatus() { return status;}
    public Long getVersion(){ return version;}
    public Instant getCreatedAt() { return createdAt;}
    public Instant getUpdatedAt() { return updatedAt;}
}
