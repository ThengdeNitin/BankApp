package com.app.core_banking_service.domain.model;

import java.util.UUID;

import com.app.core_banking_service.domain.exception.InsufficientFundsException;
import com.app.core_banking_service.domain.exception.InvalidTransactionAmountException;
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

        Money avaliableBalance = getAvailableBalance();
        if(!avaliableBalance.isGreaterThanOrEqualTo(amount)){
           if(!this.accuntType.isOverdraftPermitted()){
            throw new InsufficientFundsException("Insufficient funds for account " + this.accountNumber + ". Available: " + avaliableBalance + ", Requested: " + amount);
           } 
        }

        this.balance = this.balance.subtract(amount);

        this.updatedAt  = Instant.now();
    }

    public Money getAvailbleBalance(){
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
