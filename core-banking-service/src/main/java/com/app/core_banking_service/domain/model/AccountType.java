package com.app.core_banking_service.domain.model;

public enum AccountType {
    SAVINGS("Saving Account", false),

    CHECKING("Current / Cheacking Account", true),

    ESCROW("Escrow Holding Account", false),

    LOAN("Loan Ledger Account", false);

    private final String description;

    private final boolean overdraftPermitted;

    AccountType(String description, boolean overdraftPermitted){
        this.description = description;
        this.overdraftPermitted = overdraftPermitted;
    }


    public String getDescription() {
        return description;
    }

    public boolean isOverdraftPermitted(){
        return this.overdraftPermitted;
    }

}
