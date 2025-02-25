package com.example.webscraping;

public class ThreadParse extends java.lang.Thread{
    private Elenchi elenco;

    public ThreadParse(Elenchi elenco) {
        this.elenco = elenco;
    }
    @Override
    public void run() {
        String url;
        while ((url = elenco.getLink(this)) != null){
            try{
                Scan.alert(url);
                Carta carta = new Carta(url);
                System.out.println(carta);

                elenco.add(carta);
            }catch (org.openqa.selenium.NoSuchElementException e){
                Scan.alert("errore: " + url, true);
            }
        }
        this.interrupt();
    }
}