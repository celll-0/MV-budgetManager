package com.buggi.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.buggi.menus.MenuOption.IMenuOptionHandler;
import com.buggi.menus.GenericMenu;
import com.buggi.menus.Menu;

public class MenuBuilder {
    private final String name;
    private final Map<String, IMenuOptionHandler> options = new HashMap<String, IMenuOptionHandler>();
    private final Scanner scanner;
    private String[] menuLines;
    private String welcomeMessage;
    private boolean hasWelcome = false;

    public MenuBuilder(String name, Scanner scanner) {
        this.name = name;
        this.scanner = scanner;
    }

    public MenuBuilder addOption(String key, IMenuOptionHandler handler) {
        options.put(key, handler);
        return this;
    }

    public MenuBuilder setMenuLines(String... lines) {
        menuLines = lines;
        return this;
    }

    public MenuBuilder setWelcomeMessage(String message) {
        welcomeMessage = message;
        hasWelcome = true;
        return this;
    }

    public Menu build() {
        GenericMenu menu = new GenericMenu(name, options, scanner, menuLines, welcomeMessage, hasWelcome);
        if(name.equals("transaction")){ menu.setType(name);}
        return menu;
    }
}