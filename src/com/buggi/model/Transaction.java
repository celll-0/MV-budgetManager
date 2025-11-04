package com.buggi.model;

public class Transaction {
    private int amount;
    private String description;
    public String type;

    public Transaction(int amount, String description, String type) {
        this.amount = amount;
        this.description = description;
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public void addAmount(int amount){
        this.amount = amount;
    }

    public void addDescription(String value){
        this.description = value;
    }

    public String toString(){
        String amountLine = STR."---> Amount: £\{this.amount}";
        String descriptionLine = STR."---> Desc: \{this.description}";
        return amountLine + "\n" + descriptionLine;
    }
}