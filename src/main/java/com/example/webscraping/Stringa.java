package com.example.webscraping;

public class Stringa{
    String value;
    Stringa next;

    public Stringa(String value){
        this.value = value;
    }

    public Stringa(String[] elenco){
        if(elenco.length>0){
            value=elenco[0];
            if(elenco.length>1){
                next = new Stringa(elenco);
            }
        }
    }

    public String getString(int i){
        if(i==0){
            return value;
        }else{
            return next.getString(i-1);
        }
    }

    public void add(String line){
        if(next != null && next.equals(line)){
            next = next.next;
        }
        if(next == null){
            next = new Stringa(line);
        }else{
            next.add(line);
        }
    }

    public boolean equals(String s){
        return value.equals(s);
    }
}