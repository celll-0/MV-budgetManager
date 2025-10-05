import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {

        ArrayList<Map<String, Object>> incomes = new ArrayList<Map<String, Object>>();
        ArrayList<Map<String, Object>> expenses = new ArrayList<Map<String, Object>>();

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        boolean first_run = true;

        while (!exit) {
            String choice_main = runMainMenuForInput(scanner, first_run);
            if (first_run) {
                first_run = false;
            }
            switch (choice_main) {
                case "1" -> {
                    Map<String, Integer> summary = calculateSummary(incomes, expenses);
                    printSummary(summary);
                }
                case "2" -> runIncomesProc(scanner, incomes, exit);
                case "3" -> runExpensesProc(scanner, expenses, exit);
                case "4" -> exit = true;
                default -> System.out.println("Invalid choice. Please choose from the following options");
            }
        }
    }

    private static String runMainMenuForInput(Scanner scanner, boolean first_run) {
        if (first_run) {
            System.out.println("\n\nWelcome to Buggi!");
        }
        System.out.println("\n_________________________________\n");
        System.out.println("[1] Full budget summary");
        System.out.println("[2] Incomes ->");
        System.out.println("[3] Expenses ->");
        System.out.println("[4] Quit");
        return scanner.nextLine();
    }

    private static String runTransactionMenuForInput(Scanner scanner, String transactionType) {
        System.out.println("_________________________________\n");
        System.out.println(STR."[1] Add \{transactionType} transaction");
        System.out.println(STR."[2] See \{transactionType} report");
        System.out.println("[3] Main menu");
        System.out.println("[4] Exit");
//        System.out.println(STR."Left in buffer: ->|\{scanner.nextLine()}|<-");
        return scanner.nextLine();
    }

    private static void runIncomesProc(Scanner scanner, ArrayList<Map<String, Object>> incomes, boolean exit_main_proc){
        boolean exit_income = false;
        while (!exit_income) {
            String choice_incomes = runTransactionMenuForInput(scanner, "income");
            switch (choice_incomes) {
                case "1" -> runAddTransaction(scanner, incomes, "income");
                case "2" -> printTransactionReport(incomes, "income");
                case "3" -> exit_income = true;
                case "4" -> {
                    exit_main_proc = true;
                    break;
                }
                default -> System.out.println("Invalid choice. Please choose from the following options");
            }
        }
    }

    private static void runExpensesProc(Scanner scanner, ArrayList<Map<String, Object>> expenses, boolean exit_main_proc){
        boolean exit_expenses = false;
        while (!exit_expenses) {
            String choice_expenses = runTransactionMenuForInput(scanner, "expense");
            switch (choice_expenses) {
                case "1" -> runAddTransaction(scanner, expenses, "expense");
                case "2" -> printTransactionReport(expenses, "expense");
                case "3" -> exit_expenses = true;
                case "4" -> {
                    exit_main_proc = true;
                    break;
                }
                default -> System.out.println("Invalid choice. Please choose from the following options");
            }
        }
    }

    private static void printTransactionReport(ArrayList<Map<String, Object>> transactionList, String transactionType) {
//        Make transactionType title case.
        transactionType = transactionType.substring(0,1).toUpperCase() + transactionType.substring(1).toLowerCase();
        System.out.println("\n___________________________\n\n");
        System.out.println(STR."\{transactionType} Report______________\n");
//        Print out each transaction in amount-description format.
        for(Map<String, Object> transaction : transactionList) {
            System.out.println(STR."---> Amount: £\{transaction.get("amount")}");
            System.out.println(STR."---> Desc: \{transaction.get("description")}");
            System.out.println("\n\n");
        }
    }

    private static void printSummary(Map<String, Integer> summary) {
        System.out.println("\n___________________________\n");
        System.out.println("Summary: \n");
        System.out.println(STR."Income: £\{summary.get("incomes")}");
        System.out.println(STR."Expenses: £\{summary.get("expenses")}");
        System.out.println(STR."Net: £\{summary.get("total")}");
        System.out.println("\n___________________________\n");
    }

    private static Map<String, Integer> calculateSummary(ArrayList<Map<String, Object>> incomes, ArrayList<Map<String, Object>> expenses) {
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
     * @param transactionsList The list to add the transaction to (incomes or expenses)
     * @param transactionType The type of transaction ("income" or "expense") for display purposes
     */
    private static void runAddTransaction(Scanner scanner, ArrayList<Map<String, Object>> transactionsList, String transactionType) {
        runAddTransaction(scanner, transactionsList, transactionType, 0, "");
    }

    /**
     * Adds a new transaction to the specified list with user input validation and recursive retry logic.
     * Prompts the user for an amount and description. If the amount is invalid (zero or negative),
     * the user is given up to 2 more attempts before returning to the menu.
     * Use runAddTransaction parent function instead.
     *
     * @param scanner The Scanner object for reading user input
     * @param transactionsList The list to add the transaction to (incomes or expenses)
     * @param transactionType The type of transaction ("income" or "expense") for display purposes
     * @param tryRetryCount The current number of retry attempts (used for recursive validation)
     * @param description The user description of the transaction
     */
    private static void runAddTransaction(Scanner scanner, ArrayList<Map<String, Object>> transactionsList, String transactionType, int tryRetryCount, String description) {
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

        int amount;
        try {
            System.out.println(STR."Enter \{transactionType} amount: ");
            amount = scanner.nextInt();
        } catch(java.util.InputMismatchException e){
            // Attempt retry if the user enters a non-numeric value, clear the buffer and alert the user.
            tryRetryCount++;
            System.out.println("\nInvalid amount. Please enter a positive number");
            clearInputBuffer(scanner);
            runAddTransaction(scanner, transactionsList, transactionType, tryRetryCount, description);
            return;
        }

        if(amount <= 0) {
            // Attempt retry if the user enters a negative value, clear the buffer and alert the user.
            tryRetryCount++;
            if(tryRetryCount < 3) System.out.println("\nInvalid amount. Please enter a positive number");
            clearInputBuffer(scanner);
            runAddTransaction(scanner, transactionsList, transactionType, tryRetryCount, description);
            return;
        }

        clearInputBuffer(scanner);
        transactionsList.add(createTransaction(amount, description));
        System.out.println("\nTransaction added successfully!");
        System.out.println("__________________\n");
    }
    
    private static Map<String, Object> createTransaction(int amount, String description) {
        Map<String, Object> transaction = new HashMap<>();
            transaction.put("amount", amount);
            transaction.put("description", description);
        return transaction;
    }

    private static int getTransactionTotal(ArrayList<Map<String, Object>> transactions) {
        int total = 0;
        for(Map<String, Object> transaction : transactions) {
            total += (Integer) transaction.get("amount");
        }
        return total;
    }

    private static void clearInputBuffer(Scanner scanner) {
        // Skip any remaining characters in the buffer
        scanner.nextLine();
    }
}

