package com.example.webscraping;

public class Test {
    public static void main(String[] args) {
        System.out.println("inserisci il nome del primo giocatore");
        String g1 = new java.util.Scanner(System.in).nextLine();
        System.out.println("inserisci il nome del secondo giocatore");
        String g2 = new java.util.Scanner(System.in).nextLine();
        java.util.List<String> mazzi = new java.util.ArrayList<>();
        String line;
        System.out.println("inserisci il nome del primo mazzo");
        while(!(line = new java.util.Scanner(System.in).nextLine()).isEmpty()) {
            mazzi.add(line);
            System.out.println("inserisci il nome del prossimo mazzo");
        }
        System.out.println(g1 + ":\t" + mazzi.remove(new java.util.Random().nextInt(mazzi.size())));
        System.out.println(g2 + ":\t" + mazzi.remove(new java.util.Random().nextInt(mazzi.size())));
    }
}