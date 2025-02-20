package com.example.webscraping;

public class Test {
    public static void main(String[] args) {
        long a = System.nanoTime()/1000000000;
        for(int i=0;i<10000;i++) {
            System.out.println(i + ":\t" + Scan.formattaSecondi(i));
        }
    }
}
