package com.example.webscraping;

public class Stringhe{
    private Stringa start;
    private int size;

    Stringa iterator;

    public Stringhe(String[] elenco){
        for(String str : elenco){
            add(str);
        }
    }

    public void ready(){
        iterator = start;
    }

    public String getString(int i){
        return start.getString(i);
    }

    public int size(){
        return size;
    }

    public void add(String line){
        size++;
        if(start == null){
            start = new Stringa(line);
        }else{
            start.add(line);
        }
    }

    public String next(){
        String ret = iterator!=null ? iterator.value : null;
        if(ret != null) iterator = iterator.next;
        return ret;
    }
}