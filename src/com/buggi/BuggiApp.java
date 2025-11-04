package com.buggi;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.buggi.menus.MenuOption.*;
import com.buggi.model.TransactionList;
import com.buggi.consoleUi.ConsoleMenu;
import com.buggi.service.MenuBuilder;
import com.buggi.menus.Menu;


public class BuggiApp {
    void main(String[] args) {
        TransactionList incomes = new TransactionList("income");
        TransactionList expenses = new TransactionList("expense");
        Map<String, Object> resources = new HashMap<>();
        resources.put("incomes", incomes);
        resources.put("expenses", expenses);

        Scanner scanner = new Scanner(System.in);
        ConsoleMenu consoleMenuProcess = new ConsoleMenu(new Menu[]{
                new MenuBuilder("main", scanner)
                        .addOption("1", new FullBudgetSummaryOption())
                        .addOption("2", new NextMenuOption("transaction").setTransactionType("incomes"))
                        .addOption("3", new NextMenuOption("transaction").setTransactionType("expenses"))
                        .addOption("4", new ExitProcOption(false))
                        .setWelcomeMessage("\n\nWelcome to Buggi!")
                        .setMenuLines(
                                "_________________________________\n",
                                "[1] Full budget summary",
                                "[2] Incomes ->",
                                "[3] Expenses ->",
                                "[4] Quit"
                        ).build(),
                new MenuBuilder("transaction", scanner)
                        .addOption("1", new AddTransactionOption())
                        .addOption("2", new TransactionReportOption())
                        .addOption("3", new ExitProcOption(true))
                        .addOption("4", new ExitProcOption(false))
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
}
