package com.example.webscraping;

public class Test {
    public static void main(String[] args) {
        long tempo1  = System.nanoTime() / 1000000;
        System.out.println(tempo1);
        try{Thread.sleep(60000);}catch(InterruptedException ignore){}
        long tempo2 = System.nanoTime() / 1000000;
        System.out.println(Scan.formattaSecondi(tempo2-tempo1));
    }
}
