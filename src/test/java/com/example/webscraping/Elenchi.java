package com.example.webscraping;

public class Elenchi {
    String[] carte;
    Carta[] collezione;
    boolean semaforoCollezione = false;
    int i=0;
    boolean finito = false;

    public Elenchi(String[] carte){
        this.carte = carte;
        collezione = new Carta[0];
        i=0;
    }

    public void start(){
        i=0;
    }

    public String getLink(){
        while(semaforoCollezione);
        semaforoCollezione = true;
        String ret;
        try{
            ret = carte[i++];
        }catch(ArrayIndexOutOfBoundsException e){
            ret =  null;
            finito = true;
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
}