package com.example.webscraping;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Elenchi{
    String[] carte;
    Carta[] collezione;
    boolean semaforoCollezione = false;
    int i=0;
    Boolean finito = Boolean.FALSE;
    //thread che sono in esecuzione
    int thread;

    public Elenchi(String[] carte, int thread){
        this.carte = carte;
        collezione = new Carta[0];
        i=0;
        this.thread = thread;
    }

    public void first(){
        i=0;
    }

    public String getLink(Thread t){
        while(semaforoCollezione){
            System.out.printf(t.getName());
        }
        semaforoCollezione = true;
        String ret;
        try{
            ret = carte[i++];
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

    public void add(String link){
        carte = Scan.add(carte, link);
    }

    public String progresso(){
        double puntoPercentuale = (double) carte.length /100;
        double percentuale = collezione.length / puntoPercentuale;
        return collezione.length + "/" + carte.length + "(" + percentuale + "%)";
    }

    public Carta[] getResult(){
        return collezione;
    }

    public Boolean getFinito(){
        return finito;
    }

    public void hoFinito(){
        System.out.println("hoFinito prima di diminuire:" + thread);
        thread--;
        System.out.println("hoFinito dopo aver diminuito:" + thread);
        if(thread == 0) finito = Boolean.TRUE;
        System.out.println("hoFinito finito:" + finito);
    }
}