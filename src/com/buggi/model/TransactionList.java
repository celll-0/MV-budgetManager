package com.buggi.model;

import java.util.ArrayList;

public class TransactionList {
    private final ArrayList<Transaction> transactions;
    public final String type;

    public TransactionList(String type){
        this.type = type;
        this.transactions = new ArrayList<Transaction>();
    }

    public ArrayList<Transaction> getAll() {
        return this.transactions;
    }

    public void add(Transaction transaction){
        if(transaction.type.equals(this.type)){
            this.transactions.add(transaction);
        } else {
            throw new IllegalArgumentException("Transaction type mismatch");
        }
    }

    public void removeLastTransaction(Transaction transaction){
        int lastIndex = this.transactions.size() - 1;
        this.transactions.remove(lastIndex);
    }

    public int size(){
        return this.transactions.size();
    }
}
