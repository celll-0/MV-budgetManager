package com.buggi.consoleUi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.buggi.menus.Menu;
import com.buggi.menus.MenuOption.*;
import com.buggi.menus.GenericMenu;
import com.buggi.service.MenuBuilder;
import com.buggi.model.TransactionList;


public class ConsoleMenu {
    private final Map<String, Menu> menus;
    private Menu currentMenu = null;
    public boolean exited = false;
    private MenuBuilder MenuBuilder;

    public ConsoleMenu(Menu[] menus){
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

                if (Arrays.asList(optionHandler.getHandlerDependencyNames()).contains("scanner")){
                    handlerParams.put("scanner", currentMenu.getScanner());
                }
                optionHandler.handle(handlerParams);
            } catch(IllegalArgumentException e){
                continue;
            }
        }
    }

    private void setActiveTransactionList(Map<String, Object> resources, NextMenuOption nextMenuHandler) {
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

    private Map<String, Object> getRequiredResources(Map<String, Object> menuResources, String[] requiredResourceNames){
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

    private void exit(){
        exited = true;
    }
}
