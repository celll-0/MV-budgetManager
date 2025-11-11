package com.buggi.service;

import java.awt.color.ICC_ColorSpace;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.buggi.model.TransactionList;
import com.buggi.model.Transaction;
import com.buggi.utils.Validation;


public class BudgetOperations {
    public static void printSummary(Map<String, Integer> summary) {
        System.out.println("\n___________________________\n");
        System.out.println("Summary: \n");
        System.out.println("Income: £" + summary.get("incomes"));
        System.out.println("Expenses: £" + summary.get("expenses"));
        System.out.println("Net: £" + summary.get("total"));
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
                printTransactionReport(incomes, true);
                printTransactionReport(expenses, true);
                // Pauses execution to allow user to review report
                System.out.println("\n__________________________");
                System.out.println("\nPress enter to continue...");
                clearInputBuffer(scanner);
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
     * Creates new Transaction from prompted user input and add it to the given
     * transaction list. Input methods facilitate retries up to the specified limit
     * and will exit to the previous menu if this is exceeded. Those retry capabilities are
     * supported by internal input validation.
     *
     * @param scanner The Scanner object for reading user input
     * @param transactionList The list to add the transaction to (incomes or expenses)
     */
    public static void runAddTransaction(Scanner scanner, TransactionList transactionList) {
        int tryRetryCount = 0;
        // Prompt user for transaction details input for each field, all 'promptFor' method use recursive retry.
        String description = promptForTransactionDescription(scanner);
        // Prompt method for transaction amount returns 0 if input process fails or retry limit is exceeded.
        int amount = promptForTransactionAmount(scanner, transactionList.type, tryRetryCount);
        if (amount == 0) return;
        String dateString = promptForTransactionDate(scanner, tryRetryCount);
        if (dateString.isBlank()) return;
        // Construct the Transaction Object and add it to transaction list,. Then print success message
        transactionList.add(createTransaction(amount, description, transactionList.type, dateString));
        System.out.println("\nTransaction added successfully!");
        System.out.println("\n_______________________________\n");
        // Clear the input buffer to prevent any residual character corrupting next input.
        clearInputBuffer(scanner);
    }

    private static String promptForTransactionDescription(Scanner scanner){
        System.out.println("What was this for: ");
        String answer = scanner.nextLine().trim();
        System.out.println("\n");
        return answer;
    }

    private static int promptForTransactionAmount(Scanner scanner, String transactionType, int tryRetryCount){
        if(tryRetryCount == 3) {
            // Exit input process if the maximum retry count has been reached.
            System.out.println("Too many invalid attempts. Exiting To menu...");
            return 0;
        }

        int amount;
        try {
            System.out.println("Enter " + transactionType + " amount: ");
            amount = scanner.nextInt();
        } catch(InputMismatchException e){
            // Attempt retry if the user enters a non-numeric value, clear the buffer and alert the user.
            tryRetryCount++;
            System.out.println("\nInvalid amount. Please enter a positive number");
            clearInputBuffer(scanner);
            return promptForTransactionAmount(scanner, transactionType, tryRetryCount);
        }

        if(amount <= 0) {
            // Attempt retry if the user enters a negative value, clear the buffer and alert the user.
            tryRetryCount++;
            if(tryRetryCount < 3) System.out.println("\nInvalid amount. Please enter a positive number");
            clearInputBuffer(scanner);
            return promptForTransactionAmount(scanner, transactionType, tryRetryCount);
        }
        clearInputBuffer(scanner);
        System.out.println("\n");
        return amount;
    }

    private static String promptForTransactionDate(Scanner scanner, int tryRetryCount){
        if(tryRetryCount == 3) {
            // Exit input process if the maximum retry count has been reached.
            System.out.println("Too many invalid attempts. Exiting To menu...");
            return "";
        }

        try {
            System.out.println("Enter date of transaction (in format 'YYYY-MM-DD'): ");
            String dateString = scanner.nextLine().trim();
            if(!Validation.isValidIsoDate(dateString)){
                throw new InputMismatchException("Invalid date format: '" + dateString + "'. Expected format: YYYY-MM-DD");
            }
            System.out.println("\n");
            return dateString;
        } catch(InputMismatchException e){
            // Attempt retry if the user enter a non-numerical value, clear the buffer and alert the user.
            tryRetryCount++;
            System.out.println(e.getMessage());
            clearInputBuffer(scanner);
            return promptForTransactionDate(scanner, tryRetryCount);
        }
    }

    public static void printTransactionReport(TransactionList transactionList, boolean relativeDateSort) {
        // Make transactionType title case.
        String transactionType = transactionList.type.substring(0,1).toUpperCase() + transactionList.type.substring(1).toLowerCase();
        System.out.println("\n___________________________\n\n");
        System.out.println(transactionType + "Report______________\n");
        if(relativeDateSort){
            // Split transaction list into upcoming and outstanding transactions
            // and sort each list using the custom transaction comparator
            ArrayList<Transaction> upcoming = transactionList.getUpcoming().stream().sorted(TransactionList.ascendingOrder_byDate)
                    .collect(Collectors.toCollection(ArrayList::new));
            ArrayList<Transaction> outstanding = transactionList.getOutstanding().stream().sorted(TransactionList.ascendingOrder_byDate)
                    .collect(Collectors.toCollection(ArrayList::new));
            // Print all items in each transaction list to the console.
            System.out.println("\n__________Upcoming:\n");
            printEachTransaction(upcoming);
            System.out.println("\n___________Outstanding:\n");
            printEachTransaction(outstanding);
        } else {
            // Print all transaction in defined string format to the console.
            printEachTransaction(transactionList.getAll());
        }
    }

    private static void printEachTransaction(ArrayList<Transaction> transactionList){
        if(transactionList.isEmpty()){
            System.out.println("\n  NONE FOUND!\n");
        } else {
            for (Transaction transaction : transactionList) {
                System.out.println(transaction.toString());
                System.out.println("\n\n");
            }
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

    public static void clearInputBuffer(Scanner scanner) {
        // Skip any remaining characters in the buffer
        scanner.nextLine();
    }
}