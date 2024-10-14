package com.example.webscraping;

public class Elenchi extends java.lang.Thread {
    String[] carte;
    Carta[] collezione;
    boolean semaforoCollezione = false;
    int i=0;
    boolean finito = false;
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

    public String progresso(){
        double puntoPercentuale = (double) carte.length /100;
        double percentuale = collezione.length / puntoPercentuale;
        return collezione.length + "/" + carte.length + "(" + percentuale + "%)";
    }

    public Carta[] getResult(){
        return collezione;
    }

    public void hoFinito(){
        System.out.println("hoFinito prima di diminuire:" + thread);
        thread--;
        System.out.println("hoFinito dopo aver diminuito:" + thread);
    }

    @Override
    public void run() {
        while (!finito){
            System.out.println(thread);
            if(thread == 0) finito = true;
        }
    }
}