package com.buggi.menus;

import com.buggi.model.Transaction;
import com.buggi.service.BudgetOperations;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.buggi.model.TransactionList;
import com.buggi.consoleUi.ConsoleScreen;
import com.buggi.service.Search;


public class MenuOption {
    public interface IMenuOptionHandler {
        String[] getHandlerDependencyNames();
        void handle(Map<String, ?> args);
    }

    public static class TransactionReportOption implements IMenuOptionHandler{
        private final String[] requiredParams = new String[]{"transactionList", "scanner"};

        @Override
        public String[] getHandlerDependencyNames() { return requiredParams; }

        @Override
        public void handle(Map<String, ?> args){
            TransactionList transactionList = (TransactionList) args.get("transactionList");
            Scanner scanner = (Scanner) args.get("scanner");

            ConsoleScreen.clear();
            BudgetOperations.printTransactionReport(transactionList, false);
            // Pauses execution to allow user to review report
            System.out.println("\n__________________________");
            System.out.println("\nPress enter to continue...");
            BudgetOperations.clearInputBuffer(scanner);
        }
    }

    public static class SearchAllTransactionsOption implements IMenuOptionHandler {
        private final String[] requiredParams = new String[]{"incomes", "expenses", "scanner"};

        @Override
        public String[] getHandlerDependencyNames() { return requiredParams; }

        @Override
        public void handle(Map<String, ?> args){
            TransactionList incomes = (TransactionList) args.get("incomes");
            TransactionList expenses = (TransactionList) args.get("expenses");
            Scanner scanner = (Scanner) args.get("scanner");

            Transaction[] transactions = (Transaction[]) Stream.concat(incomes.getAll().stream(), expenses.getAll().stream())
                            .toArray(Transaction[]::new);
            ConsoleScreen.clear();
            Search.lookUp(transactions, scanner);
        }
    }

    public static class AddTransactionOption implements IMenuOptionHandler {
        private final String[] requiredParams = new String[]{"transactionList", "scanner", "formatter"};

        @Override
        public String[] getHandlerDependencyNames() { return requiredParams; }

        @Override
        public void handle(Map<String, ?> args){
            TransactionList transactionList = (TransactionList) args.get("transactionList");
            Scanner scanner = (Scanner) args.get("scanner");

            ConsoleScreen.clear();
            System.out.println("\n\n___ADD TRANSACTION_______________\n");
            BudgetOperations.runAddTransaction(scanner, transactionList);
        }
    }

    public static class FullBudgetSummaryOption implements IMenuOptionHandler {
        private final String[] requiredParams = new String[]{"incomes", "expenses", "scanner"};

        @Override
        public String[] getHandlerDependencyNames() { return requiredParams; }

        @Override
        public void handle(Map<String, ?> args){
            TransactionList incomes = (TransactionList) args.get("incomes");
            TransactionList expenses = (TransactionList) args.get("expenses");
            Scanner scanner = (Scanner) args.get("scanner");

            ConsoleScreen.clear();
            Map<String, Integer> summary = BudgetOperations.calculateSummary(incomes, expenses);
            BudgetOperations.printSummary(summary);
            BudgetOperations.runFullBudgetReportProc(scanner, incomes, expenses);
        }
    }

    public static class NextMenuOption implements IMenuOptionHandler {
        private final String[] requiredParams = new String[]{"transactionList", "scanner"};
        public final String menuName;
        private String transactionType;

        public NextMenuOption(String menuName){ this.menuName = menuName; }

        public IMenuOptionHandler setTransactionType(String type){
            transactionType = type;
            return this;
        }
        public String getTransactionType(){ return transactionType; }

        @Override
        public String[] getHandlerDependencyNames() { return requiredParams; }

        @Override
        public void handle(Map<String, ?> args){
            System.out.println("\n\n");
        }

    }

    public static class ExitProcOption implements IMenuOptionHandler {
        private final String[] requiredParams = new String[0];
        public final boolean exitToMain;

        public ExitProcOption(boolean exitToMain){ this.exitToMain = exitToMain; }

        @Override
        public String[] getHandlerDependencyNames() { return requiredParams; }

        @Override
        public void handle(Map<String, ?> args){
            System.out.println(exitToMain ? "Returning to main menu..." : "Exiting...");
        }
    }
}