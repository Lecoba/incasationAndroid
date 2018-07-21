package com.example.user.incasation.domain;

public class Bank {

    private String name;
    private BankBranch [] bankBranches;

    public void setName(String name) {
        this.name = name;
    }

    public void setBankBranches(BankBranch[] bankBranches) {
        this.bankBranches = bankBranches;
    }

    public String getName() {

        return name;
    }

    public BankBranch[] getBankBranches() {
        return bankBranches;
    }
}
