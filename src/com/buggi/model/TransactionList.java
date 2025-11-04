package com.buggi.model;

import java.lang.reflect.Array;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    public ArrayList<Transaction> getUpcoming(){
        ArrayList<Transaction> upcoming = transactions.stream().filter(transactionDateUpcomingFilter)
                .collect(Collectors.toCollection(ArrayList::new));
        return upcoming;
    }
    public ArrayList<Transaction> getOutstanding(){
        ArrayList<Transaction> outstanding = transactions.stream().filter(transactionDateOutstandingFilter)
                .collect(Collectors.toCollection(ArrayList::new));
        return outstanding;
    }

    private final Supplier<ZonedDateTime> today = () -> ZonedDateTime.from(LocalDate.now(ZoneId.systemDefault()));
    private final Predicate<Transaction> transactionDateOutstandingFilter = transaction -> transaction.getDate().isBefore(today.get());
    private final Predicate<Transaction> transactionDateUpcomingFilter = transaction -> transaction.getDate().isAfter(today.get());
}

