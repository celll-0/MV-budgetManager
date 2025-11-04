package com.buggi.menus;

import java.util.Map;
import java.util.Scanner;

import com.buggi.menus.MenuOption.IMenuOptionHandler;

// Need to be split into individual concise subclasses for each menu
public class GenericMenu extends Menu {
    private final String[] menuLines;
    private final String welcomeMessage;
    private boolean firstRun;
    private String transactionType;

    public GenericMenu(String name, Map<String, IMenuOptionHandler> options, Scanner scanner,
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

    public void setType(String type){
        transactionType = type;
    }
}