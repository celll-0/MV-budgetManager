package com.buggi.consoleUi;

import com.buggi.model.Transaction;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConsoleScreen {
    public static void clear(){
        final String OS_NAME = System.getProperty("os.name");
        try{
            if(OS_NAME.toLowerCase().contains("win")){
                Runtime.getRuntime().exec(new String[]{"cls"});
            } else {
                System.out.print("\u001b[H \u001b[2J");
                System.out.flush();
            }
        } catch (Exception e){
            System.out.println("Couldn't clear the console due to an error\n" + e.getMessage());
        }
    }

    public static void printEachTransaction(Iterable<? extends Transaction> transactionList, boolean includeType){
        if(transactionList == null || ((List<?>) transactionList).isEmpty()){
            System.out.println("\n  NONE FOUND!\n");
        } else {
            for (Transaction transaction : transactionList) {
                System.out.println(transaction.toString());
                if(includeType){
                    System.out.println("---> Type: " + transaction.type);
                }
                System.out.println("\n\n");
            }
        }
    }

    public static void pressEnterToContinue() {
        System.out.println("Press Enter to continue...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
