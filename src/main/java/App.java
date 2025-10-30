import java.util.*;
import java.util.stream.Collectors;

public class App {

    void main(String[] args) {

        TransactionList incomes = new TransactionList("income");
        TransactionList expenses = new TransactionList("expense");
        Map<String, Object> resources = new HashMap<>();
            resources.put("incomes", incomes);
            resources.put("expenses", expenses);

        Scanner scanner = new Scanner(System.in);
        ConsoleMenuProcess consoleMenuProcess = new ConsoleMenuProcess(new Menu[]{
                new MenuBuilder("main", scanner)
                        .addOption("1", new FullBudgetSummaryOptionHandler())
                        .addOption("2", new NextMenuOptionHandler("transaction").setTransactionType("incomes"))
                        .addOption("3", new NextMenuOptionHandler("transaction").setTransactionType("expenses"))
                        .addOption("4", new ExitProcOptionHandler(false))
                        .setWelcomeMessage("\n\nWelcome to Buggi!")
                        .setMenuLines(
                                "_________________________________\n",
                                "[1] Full budget summary",
                                "[2] Incomes ->",
                                "[3] Expenses ->",
                                "[4] Quit"
                        ).build(),
                new MenuBuilder("transaction", scanner)
                        .addOption("1", new AddTransactionOptionHandler())
                        .addOption("2", new TransactionReportOptionHandler())
                        .addOption("3", new ExitProcOptionHandler(true))
                        .addOption("4", new ExitProcOptionHandler(false))
                        .setMenuLines(
                                "_{T_TYPE}S______________________\n",
                                "[1] Add transaction",
                                "[2] See report",
                                "[3] Main menu",
                                "[4] Exit"
                        ).build()
        });
        consoleMenuProcess.run(resources);
    }

    /*
    MAKE BUDGET OPERATIONS CLASS FOR MAIN FUNCTIONALITY (EVERYTHING CAN CONNECT ONCE YOU HAVE IT)
    **SHOULD HAVE DONE THAT FIRST ---------------------------------------------------------------
     */

    public static class ConsoleMenuProcess {
        private final Map<String, Menu> menus;
        private Menu currentMenu = null;
        public boolean exited = false;
        private MenuBuilder MenuBuilder;

        public ConsoleMenuProcess(Menu[] menus){
            this.menus = new HashMap<>();
            this.registerMenus(menus);
            this.switchMenu("main");
        }

        public void run(Map<String, Object> resources){
            while(!exited){
                try {
                    IMenuOptionHandler optionHandler = currentMenu.runProc(resources);
                    Map<String, Object> handlerParams = optionHandler.getRequiredResources(resources);

                    if (optionHandler instanceof NextMenuOptionHandler nextMenuHandler) {
                        String nextMenuName = nextMenuHandler.menuName;
                        switchMenu(nextMenuName);

                        if (currentMenu.name.equals("transaction")) setActiveTransactionList(resources, nextMenuHandler);
                        continue;
                    }

                    if (optionHandler instanceof ExitProcOptionHandler){
                        if(((ExitProcOptionHandler) optionHandler).exitToMain){
                            switchMenu("main");
                        } else {
                            exit();
                        }
                    }

                    if(optionHandler.requiresScanner) handlerParams.put("scanner", currentMenu.getScanner());
                    optionHandler.handle(handlerParams);
                } catch(IllegalArgumentException e){
                    continue;
                }
            }
        }

        private void setActiveTransactionList(Map<String, Object> resources, NextMenuOptionHandler nextMenuHandler) {
            String transactionType = nextMenuHandler.getTransactionType();
            TransactionList transactionList = (TransactionList) resources.get(transactionType);
            if (transactionList != null) resources.remove("transactionList");
            resources.put("transactionList", transactionList);
        }

        public void registerMenus(Menu[] menuList){
            for(Menu menu : menuList){
                menus.put(menu.name, menu);
            }
        }

        private void switchMenu(String menuName){
            Menu nextMenu = menus.get(menuName);
            if(currentMenu == null || !nextMenu.name.equals(currentMenu.name)){
                currentMenu = nextMenu;
            }
        }

        private void exit(){
            exited = true;
        }
    }

    public static class BudgetOperations {

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
    }

    public static abstract class IMenuOptionHandler {
        protected String[] requiredParams;
        public boolean requiresScanner = false;

        public abstract void handle(Map<String, ?> args);

        public Map<String, Object> getRequiredResources(Map<String, Object> menuResources){
            return menuResources.entrySet().stream()
                    .filter(resource -> {
                        for(String param : requiredParams) {
                            if(param.equals(resource.getKey())) return true;
                        }
                        return false;
                    })
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                    ));
        }
    }

    public static class TransactionReportOptionHandler extends IMenuOptionHandler{
        public TransactionReportOptionHandler(){
            this.requiredParams = new String[]{"transactionList"};
        }

        public void handle(Map<String, ?> args){
            TransactionList transactionList = (TransactionList) args.get("transactionList");
            BudgetOperations.printTransactionReport(transactionList);
        }
    }

    public static class AddTransactionOptionHandler extends IMenuOptionHandler {
        public AddTransactionOptionHandler(){
            this.requiredParams = new String[]{"transactionList", "scanner"};
            this.requiresScanner = true;
        }

        public void handle(Map<String, ?> args){
            TransactionList transactionList = (TransactionList) args.get("transactionList");
            Scanner scanner = (Scanner) args.get("scanner");
            BudgetOperations.runAddTransaction(scanner, transactionList);
        }
    }

    public static class FullBudgetSummaryOptionHandler extends IMenuOptionHandler {
        public FullBudgetSummaryOptionHandler(){
            this.requiredParams = new String[]{"incomes", "expenses", "scanner"};
            this.requiresScanner = true;
        }

        public void handle(Map<String, ?> args){
            TransactionList incomes = (TransactionList) args.get("incomes");
            TransactionList expenses = (TransactionList) args.get("expenses");
            Scanner scanner = (Scanner) args.get("scanner");

            Map<String, Integer> summary = BudgetOperations.calculateSummary(incomes, expenses);
            BudgetOperations.printSummary(summary);
            BudgetOperations.runFullBudgetReportProc(scanner, incomes, expenses, 0);
        }
    }

    public static class NextMenuOptionHandler extends IMenuOptionHandler {
        public final String menuName;
        private String transactionType;
        private boolean requiresTransactionList;

        public NextMenuOptionHandler(String menuName){
            this.requiredParams = new String[]{"transactionList", "scanner"};
            this.menuName = menuName;
        }

        public void handle(Map<String, ?> args){
            System.out.println("\n\n");
        }

        public IMenuOptionHandler setTransactionType(String type){
            transactionType = type;
            requiresTransactionList = true;
            return this;
        }

        public String getTransactionType(){
            return transactionType;
        }
    }

    public static class ExitProcOptionHandler extends IMenuOptionHandler {
        public final boolean exitToMain;

        public ExitProcOptionHandler(boolean exitToMain){
            this.requiredParams = new String[0];
            this.exitToMain = exitToMain;
        }

        public void handle(Map<String, ?> args){
            System.out.println(exitToMain ? "Returning to main menu..." : "Exiting...");
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


    public static abstract class Menu {
        private final ArrayList<MenuOption> options;
        protected final Scanner scanner;
        protected boolean exit = false;
        public final String name;

        public Menu(ArrayList<MenuOption> options, Scanner scanner, String name) {
            this.options = options;
            this.scanner = scanner;
            this.name = name;
        }

        public abstract String getMenuText();

        protected abstract void updateMenuState();

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

        public IMenuOptionHandler runProc(Map<String, Object> menuParams){
            String userSelection = runAndGetChoice();
            System.out.println("\n\n");
            if(!isNumeric(userSelection) || !isInOptionRange(userSelection, options)){
//                  Flag invalid input and warn the user.
                System.out.println("Invalid choice. Please choose from the following options");
                throw new IllegalArgumentException(("Invalid selection made menu " + name)); // Throw an exception to catch in consoleProc and continue the loop there.
            }
            IMenuOptionHandler optionHandler = getOptionHandler(userSelection);
            updateMenuState();
            return optionHandler;
        };

        public String[] getMenuOptionTextList(){
            String menuText = getMenuText();
            return Arrays.stream(menuText.split("\n"))
                    .dropWhile(line -> !line.trim().matches("_+"))  // Drop until we find the divider
                    .skip(1)
                    .filter(line -> !line.trim().matches("_+"))
                    .toArray(String[]::new);
        }

        private static boolean isInOptionRange(String userSelection, ArrayList<MenuOption> options){
            int selectionAsNumeric = Integer.parseInt(userSelection);
            return selectionAsNumeric <= options.size();
        }

        private static boolean isNumeric(String userSelection) {
            try {
                Double.parseDouble(userSelection);
                return true;
            } catch(NumberFormatException e){
                return false;
            }
        }

        public String runAndGetChoice() {
            String menuText = getMenuText();
            System.out.println(menuText);
            return scanner.nextLine();
        }

        protected void exitProc(){
            exit = true;
        };

        public Scanner getScanner(){
            return scanner;
        }
    }

    public static class MenuBuilder {
        private final String name;
        private final ArrayList<MenuOption> options = new ArrayList<>();
        private final Scanner scanner;
        private String[] menuLines;
        private String welcomeMessage;
        private boolean hasWelcome = false;

        public MenuBuilder(String name, Scanner scanner) {
            this.name = name;
            this.scanner = scanner;
        }

        public MenuBuilder addOption(String key, IMenuOptionHandler handler) {
            this.options.add(new MenuOption(key, handler));
            return this;
        }

        public MenuBuilder setMenuLines(String... lines) {
            this.menuLines = lines;
            return this;
        }

        public MenuBuilder setWelcomeMessage(String message) {
            this.welcomeMessage = message;
            this.hasWelcome = true;
            return this;
        }

        public Menu build() {
            return new GenericMenu(name, options, scanner, menuLines, welcomeMessage, hasWelcome);
        }
    }

    // Generic Menu class that can represent any menu
    public static class GenericMenu extends Menu {
        private final String[] menuLines;
        private final String welcomeMessage;
        private boolean firstRun;
        private String transactionType;

        public GenericMenu(String name, ArrayList<MenuOption> options, Scanner scanner,
                           String[] menuLines, String welcomeMessage, boolean hasWelcome) {
            super(options, scanner, name);
            this.menuLines = menuLines;
            this.welcomeMessage = welcomeMessage;
            this.firstRun = hasWelcome;
            this.transactionType = null;
        }

        @Override
        public String getMenuText() {
            String menuText = String.join("\n", menuLines);
            if (firstRun && welcomeMessage != null) {
                return welcomeMessage + "\n" + menuText;
            }

            if(name.equals("transaction") && transactionType != null) {
                menuText = menuText.replace("{T_TYPE}", transactionType.toUpperCase());
            }
            return menuText;
        }

        @Override
        protected void updateMenuState() {
            if (firstRun) firstRun = false;
        }

        @Override
        public IMenuOptionHandler runProc(Map<String, Object> args){
            if(name.equals("transaction")){
                TransactionList transactionList = (TransactionList) args.get("transactionList");
                setType(transactionList.type);
            }
            return super.runProc(args);
        }

        public void setType(String type){
            transactionType = type;
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

        public int size(){
            return this.transactions.size();
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
            this.amount = amount;
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

    private static void clearInputBuffer(Scanner scanner) {
        // Skip any remaining characters in the buffer
        scanner.nextLine();
    }
}

