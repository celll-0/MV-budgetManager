package com.buggi.service;


import java.util.Arrays;
import java.util.Scanner;

import com.buggi.consoleUi.ConsoleScreen;
import com.buggi.model.Transaction;

public class Search {

    public static void lookUp(Transaction[] transactions, Scanner scanner){
        System.out.print("\n\n\nSearch:  ");
        String searchTerm = scanner.nextLine().toLowerCase();
        System.out.print("\n____________________\n");

        String[] searchTokens = searchTerm.split(" ");
        Transaction[] results = filterTransactionsByTokens(transactions, searchTokens);
        ConsoleScreen.printEachTransaction(Arrays.asList(results), true);
        ConsoleScreen.pressEnterToContinue();
    }

    private static Transaction[] filterTransactionsByTokens(Transaction[] transactions, String[] tokens) {
        return Arrays.stream(transactions)
                .filter(transaction -> {
                    String descriptionLower = transaction.getDescription().toLowerCase();
                    for (String token : tokens) {
                        if (descriptionLower.contains(token)) return true;
                    }
                    return false;
                })
                .toArray(Transaction[]::new);
    }
}
