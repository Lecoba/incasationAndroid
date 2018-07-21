package com.example.user.incasation.domain;

public class Transaction {

    private String user;
    private String amount;
    private String currency;
    private String destinationBank;
    private String destinationBankBranch;
    private String transactionDate;

    private Transaction() {
        this.user = user;
        this.amount = amount;
        this.currency = currency;
        this.destinationBank = destinationBank;
        this.destinationBankBranch = destinationBankBranch;
        this.transactionDate = transactionDate;
    }

    public static Transaction createTransactionWithParams(String user, String amount, String currency, String destinationBank, String destinationBankBranch, String transactionDate){
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setDestinationBank(destinationBank);
        transaction.setDestinationBankBranch(destinationBankBranch);
        transaction.setTransactionDate(transactionDate);
        return transaction;
    }

    public static Transaction createTransactionWithoutDateParams(String user, String amount, String currency, String destinationBank, String destinationBankBranch){
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setDestinationBank(destinationBank);
        transaction.setDestinationBankBranch(destinationBankBranch);
        transaction.setTransactionDate(null);
        return transaction;
    }

    public String getUser() {
        return user;
    }

    private void setUser(String user) {
        this.user = user;
    }

    public String getAmount() {
        return amount;
    }

    private void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    private void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDestinationBank() {
        return destinationBank;
    }

    private void setDestinationBank(String destinationBank) {
        this.destinationBank = destinationBank;
    }

    public String getDestinationBankBranch() {
        return destinationBankBranch;
    }

    private void setDestinationBankBranch(String destinationBankBranch) {
        this.destinationBankBranch = destinationBankBranch;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    private void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }
}
