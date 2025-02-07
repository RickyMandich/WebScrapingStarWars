package com.example.webscraping;


import java.time.Duration;

public class Test {
    public static void main(String[] args) {
        System.out.println("inserisci un numero entro 5 secondi");
        int x = Scan.readIntWithTimeout(Duration.ofSeconds(5), 4);
        System.out.println("hai inserito: ");
        try{Thread.sleep(5000);}catch(Exception ignore){}
        System.out.println(x);
    }
}