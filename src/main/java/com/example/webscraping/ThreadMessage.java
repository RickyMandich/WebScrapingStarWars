package com.example.webscraping;

import java.util.ArrayList;
import java.util.List;

public class ThreadMessage extends java.lang.Thread{
    static String lastMessage;
    static List<String> messageBuffer = new ArrayList<String>();

    @Override
    public void run() {
        while (true){
            if(messageBuffer.size() > 0){
                lastMessage = Scan.editMessage(lastMessage, messageBuffer.getFirst());
                messageBuffer.remove(lastMessage);
            }else{
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    Scan.alert("Errore: " + e.getMessage(), true);
                }
            }
        }
    }
}
