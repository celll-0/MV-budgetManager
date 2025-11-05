package com.buggi.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class Validation {
    public static final DateTimeFormatter ISO_LOCAL_DATE = DateTimeFormatter.ofPattern("yyyy-M-d");

    public static boolean isValidIsoDate(String dateString) {
        if (dateString == null) return false;
        try {
            System.out.println(STR."----\{dateString}----");
            LocalDate.parse(dateString, ISO_LOCAL_DATE);
            System.out.println("should return true");
            return true;
        } catch (DateTimeParseException ex) {
            System.out.println("returning false");
            return false;
        }
    }
}
