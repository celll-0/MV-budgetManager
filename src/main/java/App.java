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

        while (!exit) {
            String choice_main = runMainMenuForInput(scanner);
            if(choice_main.equals("1")) {
                Map<String, Integer> summary = calculateSummary(incomes, expenses);
                printSummary(summary);
            }
            else if(choice_main.equals("2")) {
                boolean exit_income = false;
                while (!exit_income) {
                    String choice_incomes = runIncomesMenuForInput(scanner);
                    switch (choice_incomes) {
                        case "1" -> addIncomeTransaction(scanner, incomes);
                        case "2" -> exit_income = true;
                        case "3" -> {
                            exit = true;
                            break;
                        }
                    }
                }
            }
            else if(choice_main.equals("3")) {
//              run expenses process
            }
            else if(choice_main.equals("4")) {
                exit = true;
            }
        }
    }

    private static String runMainMenuForInput(Scanner scanner) {
        System.out.println("\n\nWelcome to Buggi!\n");
        System.out.println("_________________________________\n");
        System.out.println("1. Full budget summary");
        System.out.println("2. Incomes ->");
        System.out.println("3. Expenses ->");
        System.out.println("4. Quit");
        return scanner.nextLine();
    }

    private static String runIncomesMenuForInput(Scanner scanner) {
        System.out.println("_________________________________\n");
        System.out.println("1. Add income transaction");
        System.out.println("2. Main menu");
        System.out.println("3. Exit");
        return scanner.nextLine();
    }

    private static void printSummary(Map<String, Integer> summary) {
        System.out.println("\n___________________________\n");
        System.out.println("Summary: \n");
        System.out.println(STR."Income: \{summary.get("incomes")}");
        System.out.println(STR."Expenses: \{summary.get("expenses")}");
        System.out.println(STR."Net: \{summary.get("total")}");
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

    private static void addIncomeTransaction(Scanner scanner, ArrayList<Map<String, Object>> incomes) {
        System.out.println("_________________________________\n");
        System.out.println("Enter income amount: ");
        Integer incomeAmount = scanner.nextInt();
        System.out.println("What was this for: ");
        String description = scanner.next();
        //  consume leftover newline
        scanner.nextLine();
        incomes.add(createTransaction(incomeAmount, description));
    }

    private static void addExpenseTransaction(Scanner scanner, ArrayList<Map<String, Object>> expenses) {
        System.out.println("_________________________________\n");
        System.out.println("Enter expense amount: ");
        Integer expenseAmount = scanner.nextInt();
        System.out.println("What was this for: ");
        String description = scanner.next();
        //  consume leftover newline
        scanner.nextLine();
        expenses.add(createTransaction(expenseAmount, description));
    }

    private static Map<String, Object> createTransaction(Integer amount, String description) {
        Map<String, Object> transaction = new HashMap<>();
            transaction.put("Amount", amount);
            transaction.put("Desc", description);
        return transaction;
    }

    private static int getTransactionTotal(ArrayList<Map<String, Object>> transactions) {
        int total = 0;
        for(Map<String, Object> transaction : transactions) {
            total += (Integer) transaction.get("Amount");
        }
        return total;
    }
}
	
