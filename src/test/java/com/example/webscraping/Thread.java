package com.example.webscraping;

import org.openqa.selenium.WebDriver;

public class Thread extends java.lang.Thread {
    Elenchi link;
    WebDriver driver;

    public Thread(Elenchi link, WebDriver driver) {
        this.link = link;
        this.driver = driver;
    }
    @Override
    public void run() {
        String line;
        while((line = link.getLink(this)) != null){
            try {
                driver.get(line);
                link.add(new Carta(driver));
                System.out.println(link.progresso());
                long tempoTrascorso = System.nanoTime() / 1000000;
                tempoTrascorso = tempoTrascorso - Scan.tempo;
                long secondi = tempoTrascorso / 1000;
                System.out.println("tempo trascorso:\t" + Scan.formattaSecondi(secondi) + link.tempoStimato(secondi));
            }catch (Exception e) {
                link.carte.add(line);
            }
        }
        System.out.println("------------------------------------------------------diminuisco thread di uno");
        link.hoFinito();
        driver.quit();
    }
}
