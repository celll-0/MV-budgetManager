package com.buggi.menus;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

import com.buggi.menus.MenuOption.IMenuOptionHandler;

public abstract class Menu {
    private final Map<String, IMenuOptionHandler> options;
    protected final Scanner scanner;
    protected boolean exit = false;
    public final String name;
    public String errorMessage = null;

    public Menu(Map<String, IMenuOptionHandler> options, Scanner scanner, String name) {
        this.options = options;
        this.scanner = scanner;
        this.name = name;
    }

    public abstract String getMenuText();

    protected abstract void updateMenuState();

    public IMenuOptionHandler getOptionHandler(String optionKey){
        if(optionKey == null) return null;
        return options.get(optionKey);
    }

    public IMenuOptionHandler runProc(){
        String userSelection = runAndGetChoice();
        if(!isNumeric(userSelection) || !isInOptionRange(userSelection, options)){
            // Flag invalid input and warn the user.
            throw new IllegalArgumentException("Invalid choice. Please choose from the given options"); // Throw an exception to catch in consoleProc and continue the loop there.
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

    private static boolean isInOptionRange(String userSelection, Map<String, IMenuOptionHandler> options){
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
        System.out.println("\n\n\n");

        if(errorMessage != null){
            System.out.println(errorMessage);
            errorMessage = null;
        }

        System.out.println(menuText);
        return scanner.nextLine();
    }

    public Scanner getScanner(){
        return scanner;
    }
}
