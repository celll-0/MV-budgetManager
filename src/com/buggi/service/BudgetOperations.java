package com.buggi.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

import com.buggi.model.TransactionList;
import com.buggi.model.Transaction;
import com.buggi.utils.Validation;


public class BudgetOperations {
    public static void printSummary(Map<String, Integer> summary) {
        System.out.println("\n___________________________\n");
        System.out.println("Summary: \n");
        System.out.println(STR."Income: £\{summary.get("incomes")}");
        System.out.println(STR."Expenses: £\{summary.get("expenses")}");
        System.out.println(STR."Net: £\{summary.get("total")}");
        System.out.println("\n___________________________\n");
    }

    public static void runFullBudgetReportProc(Scanner scanner, TransactionList incomes, TransactionList expenses) {
        runFullBudgetReportProc(scanner, incomes, expenses, 0);
    }

    private static void runFullBudgetReportProc(Scanner scanner, TransactionList incomes, TransactionList expenses, int tryRetryCount) {
        System.out.println("Would you like to see a full report? Answer yes(y)/no(n)");
        String report_choice = scanner.nextLine();
        switch (report_choice) {
            case "y" -> {
                printTransactionReport(incomes);
                printTransactionReport(expenses);
            }
            case "n" -> {
                System.out.println("Exiting to main menu!");
            }
            default -> {
                if(tryRetryCount < 3) {
                    tryRetryCount++;
                    System.out.println("Invalid choice. Please choose from the following options");
                    System.out.println("_________________________________");
                    runFullBudgetReportProc(scanner, incomes, expenses, tryRetryCount);
                } else {
                    System.out.println("Too many invalid attempts. Exiting to main menu...");
                }
            }
        }
    }

    public static Map<String, Integer> calculateSummary(TransactionList incomes, TransactionList expenses) {
        Integer incomeSum = getTransactionTotal(incomes);
        Integer expensesSum = getTransactionTotal(expenses);
        Map<String, Integer> summary =  new HashMap<>();
        summary.put("incomes", incomeSum);
        summary.put("expenses", expensesSum);
        summary.put("total", incomeSum - expensesSum);
        return summary;
    }

    /**
     * Adds a new transaction to the specified list with user input validation.
     * This is a convenience method that calls the overloaded version with a default retry count of 0.
     * Use as entry to point to add a new transaction over the overloaded, unless in special cases
     * where offsetting the retry count is desired.
     *
     * @param scanner The Scanner object for reading user input
     * @param transactionList The list to add the transaction to (incomes or expenses)
     */
    public static void runAddTransaction(Scanner scanner, TransactionList transactionList) {
        runAddTransaction(scanner, transactionList, 0, "", "");
    }

    /**
     * Adds a new transaction to the specified list with user input validation and recursive retry logic.
     * Prompts the user for an amount and description. If the amount is invalid (zero or negative),
     * the user is given up to 2 more attempts before returning to the menu.
     * Use runAddTransaction parent function instead.
     *
     * @param scanner The Scanner object for reading user input
     * @param transactionList The list to add the transaction to (incomes or expenses)
     * @param tryRetryCount The current number of retry attempts (used for recursive validation)
     * @param description The user description of the transaction
     */
    private static void runAddTransaction(Scanner scanner, TransactionList transactionList, int tryRetryCount, String description, String dateString) {
        System.out.println("_________________________________\n");
        // Return to the previous menu once the maximum retry count is reached
        if(tryRetryCount == 3) {
            System.out.println("Too many invalid attempts. Exiting To menu...");
            return;
        }
        // Preserve the origin user description in the case of a recursive call and do not request it again.

        description = description.isBlank() ? "" : description;
        if(tryRetryCount == 0) {
            System.out.println("What was this for: ");
            description = scanner.nextLine().trim();
        }


        dateString = dateString == null || dateString.trim().isBlank() ? "" : dateString;
        if(dateString.trim().isBlank()){
            try {
                System.out.println("Enter date of transaction (in format 'YYYY-MM-DD'): ");
                dateString = scanner.nextLine().trim();
                if(!Validation.isValidIsoDate(dateString)){
                    throw new InputMismatchException("Invalid date format: '" + dateString + "'. Expected format: YYYY-MM-DD");
                }
            } catch(InputMismatchException e){
                // Attempt retry if the user enter a non-numerical value, clear the buffer and alert the user.
                tryRetryCount++;
                System.out.println(e.getMessage());
                clearInputBuffer(scanner);
                runAddTransaction(scanner, transactionList, tryRetryCount, description, null);
            }
        }

        int amount;
        try {
            System.out.println(STR."Enter \{transactionList.type} amount: ");
            amount = scanner.nextInt();
        } catch(InputMismatchException e){
            // Attempt retry if the user enters a non-numeric value, clear the buffer and alert the user.
            tryRetryCount++;
            System.out.println("\nInvalid amount. Please enter a positive number");
            clearInputBuffer(scanner);
            runAddTransaction(scanner, transactionList, tryRetryCount, description, dateString);
            return;
        }

        if(amount <= 0) {
            // Attempt retry if the user enters a negative value, clear the buffer and alert the user.
            tryRetryCount++;
            if(tryRetryCount < 3) System.out.println("\nInvalid amount. Please enter a positive number");
            clearInputBuffer(scanner);
            runAddTransaction(scanner, transactionList, tryRetryCount, description, dateString);
            return;
        }

        clearInputBuffer(scanner);
        transactionList.add(createTransaction(amount, description, transactionList.type, dateString));
        System.out.println("\nTransaction added successfully!");
        System.out.println("\n_______________________________\n");
    }

    public static void printTransactionReport(TransactionList transactionList) {
//        Make transactionType title case.
        String transactionType = transactionList.type.substring(0,1).toUpperCase() + transactionList.type.substring(1).toLowerCase();
        System.out.println("\n___________________________\n\n");
        System.out.println(STR."\{transactionType} Report______________\n");
//        Print out each transaction in amount-description format.
        for(Transaction transaction : transactionList.getAll()) {
            System.out.println(transaction.toString());
            System.out.println("\n\n");
        }
    }

    private static Transaction createTransaction(int amount, String description, String type, String dateString) {
        Transaction transaction = new Transaction(amount, description, type);
        transaction.addAmount(amount);
        transaction.addDescription(description);
        transaction.addDate(dateString);
        return transaction;
    }

    private static int getTransactionTotal(TransactionList transactions) {
        int total = 0;
        for(Transaction transaction : transactions.getAll()) {
            total += (Integer) transaction.getAmount();
        }
        return total;
    }

    private static void clearInputBuffer(Scanner scanner) {
        // Skip any remaining characters in the buffer
        scanner.nextLine();
    }
}