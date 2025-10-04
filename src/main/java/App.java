import java.security.Key;
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
                String choice_incomes = runIncomesMenuForInput(scanner);
                if(choice_incomes.equals("1")) {

                }
            }
            exit = true;
        }
    }

    private static String runMainMenuForInput(Scanner scanner) {
        System.out.println("Welcome to Buggi!\n\n");
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


}
	
