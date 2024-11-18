package com.example.webscraping;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Elenchi{
    Stringhe carte;
    Carta[] collezione;
    boolean semaforoCollezione = false;
    int i;
    boolean finito = false;
    //thread che sono in esecuzione
    int thread;

    public Elenchi(){
        carte = new Stringhe(new String[0]);
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
        this.collezione = new Carta[0];
        this.semaforoCollezione = false;
        this.i = 0;
        this.finito = false;
        this.thread = 0;
    }

    public Elenchi(String[] carte, int thread, Carta[] collezione){
        this.carte = new Stringhe(carte);
        this.collezione = collezione;
        i=0;
        this.thread = thread;
    }

    public String getLink(Thread t){
        while(semaforoCollezione){
            System.out.printf(t.getName());
        }
        semaforoCollezione = true;
        String ret;
        try{
            ret = carte.next();
            System.out.println("link emesso " + ret);
        }catch(ArrayIndexOutOfBoundsException e){
            ret =  null;
        }
        semaforoCollezione = false;
        return ret;
    }

    public void add(Carta c){
        collezione = Scan.add(collezione, c);
    }

    public String progresso(){
        double puntoPercentuale = (double) carte.size() /100;
        double percentuale = collezione.length / puntoPercentuale;
        return collezione.length + "/" + carte.size() + "(" + String.format("%.2f", percentuale) + "%)";
    }

    public String tempoStimato(long secondi){
        double carta = (double) secondi/collezione.length;
        long tempoStimatoTotale = (long) (carta * carte.size());
        return "\ttempo stimato:\t" + Scan.formattaSecondi(tempoStimatoTotale - secondi);
    }

    public static void main(String[] args){
        Elenchi e = new Elenchi();
        WebDriver driver = new WebDriverWithoutImage();
        e.carte.ready();
        driver.get(e.carte.getString(0));
        e.add(new Carta(driver));
        System.out.println(e.progresso());
        try{
            Thread.sleep(60000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        driver.quit();
    }

    public Carta[] getResult(){
        return collezione;
    }

    public Boolean getFinito(){
        return finito;
    }

    public void hoFinito(){
        thread--;
        if(thread == 0) finito = true;
    }
}