package com.example.webscraping;

import java.util.ArrayList;
import java.util.List;

public class ThreadMessage extends java.lang.Thread{
    String lastMessage;
    List<String> messageBuffer;
    boolean messageBufferBusy;
    boolean running;

    public ThreadMessage() {
        this.messageBuffer = new ArrayList<String>();
        this.running = true;
        this.messageBufferBusy = false;
    }

    @Override
    public void run() {
        while (running || !messageBuffer.isEmpty()){
            if(!messageBuffer.isEmpty()){
                if(lastMessage != null) Scan.deleteMessage(lastMessage);
                lastMessage = Scan.alert(messageBuffer.getFirst(), true);
                messageBuffer.removeFirst();
            }else{
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    Scan.alert("Errore: " + e.getMessage(), true);
                }
            }
        }
    }

    public void finish(){
        running = false;
    }

    public void addMessage(String message){
        while(messageBufferBusy){
            try{
                Thread.sleep(10);
            }catch (InterruptedException ignore){}
        }
        messageBufferBusy = true;
        messageBuffer.add(message);
        messageBufferBusy = false;
    }
}
