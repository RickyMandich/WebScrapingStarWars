package com.example.webscraping;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Scan {

    public static String[] add(String[] array, String line){
        String[] newArray = new String[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = line;
        return newArray;
    }

    public static Carta[] add(Carta[] array, Carta line){
        Carta[] newArray = new Carta[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = line;
        return newArray;
    }

    public static void main(String[] args){
        WebDriver driver = new ChromeDriver();
        long tempo = System.nanoTime() / 1000000;
        Carta[] collezione = new Carta[0];
        String[] espansioni = new String[2];
        espansioni[0] = "SOR";
        espansioni[1] = "SHD";
        try {
            for(String set:espansioni){
                System.out.println("ora scansiono " + set);
                java.awt.Toolkit.getDefaultToolkit().beep();

                java.awt.Toolkit.getDefaultToolkit().beep();
                java.awt.Toolkit.getDefaultToolkit().beep();
                driver.get("https://swudb.com/sets/" + set);
                List<WebElement> elements = driver.findElements(By.className("col-6"));
                //String[] carte = new String[5];
                String[] carte = new String[elements.size()];
                System.out.println(elements.size());
                for (int i = 0; i < carte.length; i++) {
                    carte[i] = elements.get(i).findElement(By.tagName("a")).getAttribute("href");
                }
                for (String e : carte) {
                    System.out.println(e);
                }
                for (String link : carte) {
                    driver.get(link);
                    collezione = add(collezione, new Carta(driver));
                    System.out.println(collezione[collezione.length - 1]);
                }
            }
        } finally {
            driver.quit();
            java.awt.Toolkit.getDefaultToolkit().beep();
            java.awt.Toolkit.getDefaultToolkit().beep();
            java.awt.Toolkit.getDefaultToolkit().beep();
            java.awt.Toolkit.getDefaultToolkit().beep();
            java.awt.Toolkit.getDefaultToolkit().beep();
            java.awt.Toolkit.getDefaultToolkit().beep();
            long tempoTrascorso = System.nanoTime() / 1000000;
            tempoTrascorso = tempoTrascorso - tempo;
            System.out.println("ci ho impiegato:" + tempoTrascorso/1000);
            String json = json(collezione);
            System.out.println(json);
            try(FileWriter writer = new FileWriter("collezione.json")){
                writer.write(json);
            }catch (IOException e){
                System.out.println("non ho trovato \"collezione.json\", inserisci tu il nome del file");
                scrivi(json);
            }
        }
    }

    public static void scrivi(String json){
        try(FileWriter writer = new FileWriter(getString("inserisci il percorso del file in cui salvare il json"))){
            writer.write(json);
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("errore nella scrittura del file");
            scrivi(json);
        }
    }

    public static String json(Carta[] collezione) {
        return new Gson().toJson(collezione);
    }

    private static String getString(){
        try {
            return new Scanner(System.in).nextLine();
        } catch (NoSuchElementException e) {
            return getString();
        }
    }

    public static String getString(String message){
        System.out.println(message);
        java.awt.Toolkit.getDefaultToolkit().beep();
        return getString();
    }
}