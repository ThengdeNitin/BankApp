package com.app.core_banking_service.domain.model;

public enum AccountStatus {
    ACTIVE(true, true),
    FROZON(false, true),
    DORMANT(false, false),
    CLOSED(false, false);

    private final boolean debitAllowed;
    private final boolean creditAllowed;

    AccountStatus(boolean debitAllowed, boolean creditAllowed){
        this.debitAllowed = debitAllowed;
        this.creditAllowed = creditAllowed;
    }

    public boolean canDebit(){
        return this.debitAllowed;
    }

    public boolean canCredit(){
        return this.creditAllowed;
    }
}