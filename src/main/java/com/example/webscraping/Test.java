package com.example.webscraping;

public class Test {
    public static void main(String[] args) {
        ThreadMessage tm = new ThreadMessage();
        tm.start();
        tm.addMessage("Hello");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tm.addMessage("World");
        tm.finish();
        try {
            tm.join();
        }catch (InterruptedException ignore){}
    }
}