package com.buggi.model;

import com.buggi.utils.Validation;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Transaction {
    private int amount;
    private String description;
    private Instant createAt;
    private ZonedDateTime date;
    public String type;

    public Transaction(int amount, String description, String type) {
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.createAt = Instant.now();
    }

    public int getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public ZonedDateTime getDate(){ return date; }

    public void addAmount(int amount){
        this.amount = amount;
    }

    public void addDescription(String value){
        this.description = value;
    }

    public void addDate(String dateString){
        try {
            date = LocalDate.parse(dateString, Validation.ISO_LOCAL_DATE)
                    .atStartOfDay(ZoneId.systemDefault());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: '" + dateString + "'. Expected format: yyyy-MM-dd", e);
        }
    }

    public String toString(){
        String[] lines = new String[]{
            STR."---> Date: \{this.date.toLocalDate()}",
            STR."---> Desc: \{this.description}",
            STR."---> Amount: £\{this.amount}"
        };
        return String.join("\n", lines);
    }
}
