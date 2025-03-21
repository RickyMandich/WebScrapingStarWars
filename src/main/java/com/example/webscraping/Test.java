package com.example.webscraping;

public class Test {
    public static void main(String[] args) {
        ThreadMessage tm = new ThreadMessage();
        for(int i=0;i<100;i++){
            tm.addMessage(i+"", i%5==0);
            try{
                Thread.sleep(100);
            }catch (InterruptedException ignore){}
        }
        tm.stats();
        tm.start();
        tm.finish();
    }
}