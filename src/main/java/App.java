import java.util.*;

public class App {

    public static void main(String[] args) {

        TransactionList incomes = new TransactionList("income");
        TransactionList expenses = new TransactionList("expense");

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        boolean first_run = true;

        ArrayList<MenuOption> mainMenuOptions = new ArrayList<>();
            mainMenuOptions.add(new MenuOption("1", new FullBudgetSummaryHandler()));
            mainMenuOptions.add(new MenuOption("2", new TransactionProcHandler("incomes")));
            mainMenuOptions.add(new MenuOption("3", new TransactionProcHandler("expenses")));
            mainMenuOptions.add(new MenuOption("4", new ExitProcHandler()));
        MainMenu mainMenu = new MainMenu(mainMenuOptions, scanner);

        while (!exit) {
            String choice_main = mainMenu.runAndGetChoice();
            if (mainMenu.first_run) mainMenu.markFirstRun();
            switch (choice_main) {
                case "1" -> {
                    Map<String, Integer> summary = calculateSummary(incomes, expenses);
                    printSummary(summary);
                    runFullBudgetReportProc(scanner, incomes, expenses, 0);
                }
                case "2" -> {
                    boolean exit_req = runTransactionProc(scanner, incomes);
                    if(exit_req) exit = true;
                }
                case "3" -> {
                    boolean exit_req = runTransactionProc(scanner, expenses);
                    if(exit_req) exit = true;
                }
                case "4" -> exit = true;
                default -> System.out.println("Invalid choice. Please choose from the following options");
            }
        }
    }

    public interface IMenuOptionHandler {
        static void handle(Map<String, ?> args){
            //  menu option specific handler logic
        };
    }

    public static class FullBudgetSummaryHandler implements IMenuOptionHandler {
        public static void handle(Map<String, ?> args){
            TransactionList incomes = (TransactionList) args.get("incomes");
            TransactionList expenses = (TransactionList) args.get("expenses");
            Scanner scanner = (Scanner) args.get("scanner");

            Map<String, Integer> summary = calculateSummary(incomes, expenses);
            printSummary(summary);
            runFullBudgetReportProc(scanner, incomes, expenses, 0);
        }
    }

    public static class TransactionProcHandler implements IMenuOptionHandler {
        public final String transactionType;
        public TransactionProcHandler(String transactionType) {
            super();
            this.transactionType = transactionType;
        }

        public static void handle(Map<String, ?> args){
            TransactionList transactionList = (TransactionList) args.get("transactionList");
            Scanner scanner = (Scanner) args.get("scanner");
            runTransactionProc(scanner, transactionList);
        }
    }

    public static class ExitProcHandler implements IMenuOptionHandler {
        public static void handle(Map<String, ?> args){
            System.out.println("Exiting to main menu...");
        }
    }





    public interface IMenuOption {
        IMenuOptionHandler getHandler();
    }

    public static class MenuOption implements IMenuOption {
        public final String key;
        private final IMenuOptionHandler optionHandler;

        public MenuOption(String optionKey, IMenuOptionHandler optionHandler) {
            this.key = optionKey;
            this.optionHandler = optionHandler;
        }

        public IMenuOptionHandler getHandler() {
            return this.optionHandler;
        }
    }

    abstract private static class Menu {
        private final ArrayList<MenuOption> options;

        public Menu(ArrayList<MenuOption> options) {
            this.options = options;
        }

        public IMenuOptionHandler getOptionHandler(String optionKey){
            if(optionKey == null) return null;
            IMenuOptionHandler handler = null;
            for(MenuOption option : options) {
                if(option.key.equals(optionKey)){
                    handler = option.getHandler();
                }
            }
            return handler;
        }

        abstract public String runAndGetChoice();

        private String getMenuText(){
            return "";
        };
    }

    public static class MainMenu extends Menu {
        private boolean first_run;
        private final Scanner scanner;

        public MainMenu(ArrayList<MenuOption> options, Scanner scanner) {
            super(options);
            this.scanner = scanner;
            this.first_run = true;
        }

        public String runAndGetChoice() {
            String menuText = getMenuText();
            System.out.println(menuText);
            return scanner.nextLine();
        }


        private String getMenuText(){
            if (first_run) {
                System.out.println("\n\nWelcome to Buggi!");
            }
            ArrayList<String> lines = new ArrayList<String>();
            lines.add("_________________________________");
            lines.add("[1] Full budget summary");
            lines.add("[2] Incomes ->");
            lines.add("[3] Expenses ->");
            lines.add("[4] Quit");
            return String.join("\n", lines);
        }

        public void markFirstRun(){
            first_run = false;
        }
    }


    public static class TransactionList {
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
    }

    public static class Transaction {
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
            this.amount += amount;
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

    private static String printMainMenuForInput(Scanner scanner, boolean first_run) {
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

    private static String printTransactionMenuForInput(Scanner scanner, String transactionType) {
        System.out.println("_________________________________\n");
        System.out.println(STR."[1] Add \{transactionType} transaction");
        System.out.println(STR."[2] See \{transactionType} report");
        System.out.println("[3] Main menu");
        System.out.println("[4] Exit");
        return scanner.nextLine();
    }

    private static void printTransactionReport(TransactionList transactionList) {
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

    private static void printSummary(Map<String, Integer> summary) {
        System.out.println("\n___________________________\n");
        System.out.println("Summary: \n");
        System.out.println(STR."Income: £\{summary.get("incomes")}");
        System.out.println(STR."Expenses: £\{summary.get("expenses")}");
        System.out.println(STR."Net: £\{summary.get("total")}");
        System.out.println("\n___________________________\n");
    }

    private static boolean runTransactionProc(Scanner scanner, TransactionList transactionList){
        boolean exit = false;
        boolean exit_main_proc = false;
        while (!exit) {
            String choice_expenses = printTransactionMenuForInput(scanner, transactionList.type);
            switch (choice_expenses) {
                case "1" -> runAddTransaction(scanner, transactionList);
                case "2" -> printTransactionReport(transactionList);
                case "3" -> {
                    exit = true;
                }
                case "4" -> {
                    exit_main_proc = true;
                    exit = true;
                }
                default -> System.out.println("Invalid choice. Please choose from the following options");
            }
        }
        return exit_main_proc;
    }

    public static void runFullBudgetReportProc(Scanner scanner, TransactionList incomes, TransactionList expenses, int tryRetryCount) {
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

    private static Map<String, Integer> calculateSummary(TransactionList incomes, TransactionList expenses) {
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
    private static void runAddTransaction(Scanner scanner, TransactionList transactionList) {
        runAddTransaction(scanner, transactionList, 0, "");
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
    private static void runAddTransaction(Scanner scanner, TransactionList transactionList, int tryRetryCount, String description) {
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
            System.out.println(STR."Enter \{transactionList.type} amount: ");
            amount = scanner.nextInt();
        } catch(InputMismatchException e){
            // Attempt retry if the user enters a non-numeric value, clear the buffer and alert the user.
            tryRetryCount++;
            System.out.println("\nInvalid amount. Please enter a positive number");
            clearInputBuffer(scanner);
            runAddTransaction(scanner, transactionList, tryRetryCount, description);
            return;
        }

        if(amount <= 0) {
            // Attempt retry if the user enters a negative value, clear the buffer and alert the user.
            tryRetryCount++;
            if(tryRetryCount < 3) System.out.println("\nInvalid amount. Please enter a positive number");
            clearInputBuffer(scanner);
            runAddTransaction(scanner, transactionList, tryRetryCount, description);
            return;
        }

        clearInputBuffer(scanner);
        transactionList.add(createTransaction(amount, description, transactionList.type));
        System.out.println("\nTransaction added successfully!");
        System.out.println("__________________\n");
    }
    
    private static Transaction createTransaction(int amount, String description, String type) {
        Transaction transaction = new Transaction(amount, description, type);
            transaction.addAmount(amount);
            transaction.addDescription(description);
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

