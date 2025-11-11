package com.buggi.consoleUi;

import java.io.EOFException;
import java.io.IOException;

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
}
