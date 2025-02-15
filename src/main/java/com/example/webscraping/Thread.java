package com.example.webscraping;

public class Thread extends java.lang.Thread{
    private Elenchi elenco;
    public Thread(Elenchi elenco) {
        this.elenco = elenco;
    }
    @Override
    public void run() {
        String url;
        while ((url = elenco.getLink(this)) != null){
            try{
                Carta carta = new Carta(url);
                System.out.println(carta);
                elenco.add(carta);
                elenco.removeLink(url);
            }catch (org.openqa.selenium.NoSuchElementException e){
                System.out.println("errore: " + url);
            }
        }
    }
}