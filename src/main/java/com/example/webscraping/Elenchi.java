package com.example.webscraping;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Elenchi{
    List<String> carte;
    List <Carta> collezione;
    boolean semaforoCollezione = false;
    boolean finito = false;
    //thread che sono in esecuzione
    int thread;
    int carteGiaFatte;

    public Elenchi(){
        carte = new ArrayList<String>(List.of(new String[0]));
        String linkBase = "https://swudb.com/card/";
        System.out.println("inserisci il set delle carte che vuoi inserire nel test");
        String set = new java.util.Scanner(System.in).nextLine();
        System.out.println("inserisci il numero della prima carta che vuoi inserire nel test");
        int start = new java.util.Scanner(System.in).nextInt();
        System.out.println("inserisci il numero dell'ultima carta che vuoi inserire nel test");
        int end = new java.util.Scanner(System.in).nextInt();
        for(int i=start;i<=end;i++){
            carte.add(linkBase + set + "/" + String.format("%03d", i));
        }
        this.collezione = new ArrayList<Carta>();
        this.semaforoCollezione = false;
        this.finito = false;
        this.thread = 0;
    }

    public Elenchi(List<String> carte, List<Carta> collezione){
        if(carte.isEmpty()) {
            this.carte = new ArrayList<String>();
        }else{
            System.out.println(carte.size());
            System.out.println("---------------------");
            for(String c:carte){
                Scan.alert(c);
            }
            this.carte = new ArrayList<String>(carte);
        }
        this.collezione = collezione;
        this.carteGiaFatte = collezione.size();
    }

    public String getLink(Thread t){
        while (semaforoCollezione){
            try{
                Thread.sleep(100);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        semaforoCollezione = true;
        String ret;
        try{
            ret = carte.removeFirst();
            System.out.println("link emesso " + ret);
        }catch(IndexOutOfBoundsException | NoSuchElementException e){
            ret =  null;
        }
        semaforoCollezione = false;
        return ret;
    }

    public void add(Carta c){
        collezione.add(c);
    }

    public String progresso(){
        double puntoPercentuale = (double) carte.size() /100;
        double percentuale = (collezione.size()-carteGiaFatte) / puntoPercentuale;
        return (collezione.size()-carteGiaFatte) + "/" + carte.size() + "(" + String.format("%.2f", percentuale) + "%)";
    }

    public String tempoStimato(long secondi){
        double carta = (double) secondi/(collezione.size()-carteGiaFatte);
        long tempoStimatoTotale = (long) (carta * carte.size());
        return "\ttempo stimato:\t" + Scan.formattaSecondi(tempoStimatoTotale - secondi);
    }

    public List<Carta> getResult(){
        return collezione;
    }

    public void hoFinito(){
        thread--;
        if(thread == 0) finito = true;
    }
}