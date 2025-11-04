package com.buggi.consoleUi;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;
import java.util.stream.Collectors;


import com.buggi.menus.Menu;
import com.buggi.menus.MenuOption.*;
import com.buggi.menus.GenericMenu;
import com.buggi.service.MenuBuilder;
import com.buggi.model.TransactionList;


public class ConsoleProcess {
    private final Map<String, Menu> menus;
    private Menu currentMenu = null;
    private boolean exited = false;
    private MenuBuilder MenuBuilder;

    public ConsoleProcess(Menu[] menus){
        this.menus = new HashMap<>();
        this.registerMenus(menus);
        this.switchMenu("main");
    }

    public void run(Map<String, Object> resources){
        while(!exited){
            try {
                IMenuOptionHandler optionHandler = currentMenu.runProc();
                Map<String, Object> handlerParams = getRequiredResources(resources, optionHandler.getHandlerDependencyNames());

                if (optionHandler instanceof NextMenuOption nextMenuHandler) {
                    String nextMenuName = nextMenuHandler.menuName;
                    switchMenu(nextMenuName);

                    if (currentMenu.name.equals("transaction")){
                        setActiveTransactionList(resources, nextMenuHandler);
                        GenericMenu transactionMenu = (GenericMenu) menus.get("transaction");
                        TransactionList activeTransactionList = (TransactionList) resources.get("transactionList");
                        transactionMenu.setType(activeTransactionList.type);
                    }
                    continue;
                }

                if (optionHandler instanceof ExitProcOption) {
                    if (((ExitProcOption) optionHandler).exitToMain) {
                        switchMenu("main");
                    } else {
                        exit();
                    }
                }

                handlerParams = fulfillHandlerDependencyRequirements(resources, optionHandler);
                optionHandler.handle(handlerParams);
            } catch(IllegalArgumentException e){
                continue;
            }
        }
    }

    private static void setActiveTransactionList(Map<String, Object> resources, NextMenuOption nextMenuHandler) {
        String transactionType = nextMenuHandler.getTransactionType();
        TransactionList transactionList = (TransactionList) resources.get(transactionType);
        if (transactionList != null) resources.remove("transactionList");
        resources.put("transactionList", transactionList);
    }

    private void registerMenus(Menu[] menuList){
        for(Menu menu : menuList){
            menus.put(menu.name, menu);
        }
    }

    private static Map<String, Object> getRequiredResources(Map<String, Object> menuResources, String[] requiredResourceNames){
        return menuResources.entrySet().stream()
                .filter(resource -> {
                    for(String param : requiredResourceNames) {
                        if(param.equals(resource.getKey())) return true;
                    }
                    return false;
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    private void switchMenu(String menuName){
        Menu nextMenu = menus.get(menuName);
        if(currentMenu == null || !nextMenu.name.equals(currentMenu.name)){
            currentMenu = nextMenu;
        }
    }

    private Map<String, Object> fulfillHandlerDependencyRequirements(Map<String, Object> handlerResources, IMenuOptionHandler handler){
        List<String> handlerDependencyNames = Arrays.asList(handler.getHandlerDependencyNames());
        if (handlerDependencyNames.contains("scanner")) handlerResources.put("scanner", currentMenu.getScanner());
        return handlerResources;
    }

    private void exit(){ exited = true; }
}
