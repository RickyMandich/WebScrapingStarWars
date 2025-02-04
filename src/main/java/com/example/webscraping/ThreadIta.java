package com.example.webscraping;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;

public class ThreadIta extends java.lang.Thread{
    private List<String> str;
    private List<Carta> collezione;
    public ThreadIta(String[] str, List<Carta> collezione) {
        this.str = new ArrayList<>(List.of(str));
        this.collezione = collezione;
    }
    @Override
    public void run() {
        while (!str.isEmpty()){
            String url = str.getFirst();
            WebDriver driver = new ChromeDriver();
            driver.get(url);
            try{
                Carta carta = new Carta(driver);
                System.out.println(carta);
                collezione.add(carta);
                str.remove(url);
            }catch (org.openqa.selenium.NoSuchElementException e){
                System.out.println("errore: " + url);
            }finally {
                driver.quit();
            }
        }
    }
}