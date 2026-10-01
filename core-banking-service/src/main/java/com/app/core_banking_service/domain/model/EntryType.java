package com.app.core_banking_service.domain.model;

public enum EntryType {
    
    DEBIT(-1);

    CREDIT(1);

    private final int balanceMultiplier;

    EntryType(int balanceMultiplier){
        this.balanceMultiplier = balanceMultiplier;
    }

    public int getBalanceMultiplier(){
        return this.balanceMultiplier;
    }
}
