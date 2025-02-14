package com.example.webscraping;

public class Test {
    public static void main(String[] args) {
        String cidLink = Scan.getString("inserisci il link del cid da estrarre");
        System.out.println("cid:\t" + Scan.extractCid(cidLink));
    }
}
