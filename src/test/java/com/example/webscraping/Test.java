package com.example.webscraping;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Test {

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
        String set;
        WebDriver driver = new ChromeDriver();
        do{
            set = getString("inserisci l'acronimo del set da caricare (non inserire nulla se hai finito");
            long tempo = System.nanoTime() / 1000000;
            try {
                driver.get("https://swudb.com/sets/" + set);
                List<WebElement> elements = driver.findElements(By.className("col-6"));
                String[] carte = new String[elements.size()];
                System.out.println(elements.size());
                for (int i = 0; i < carte.length; i++) {
                    carte[i] = elements.get(i).findElement(By.tagName("a")).getAttribute("href");
                }
                for (String e : carte) {
                    System.out.println(e);
                }
                Carta[] collezione = new Carta[0];
                for (String link : carte) {
                    driver.get(link);
                    collezione = add(collezione, new Carta(driver));
                    System.out.println(collezione[collezione.length - 1]);
                }
            } finally {
                driver.quit();
                long tempoTrascorso = System.nanoTime() / 1000000;
                tempoTrascorso = tempoTrascorso - tempo;
                System.out.println("ci ho impiegato:" + tempoTrascorso);
            }
        }while(!set.isEmpty());
    }

    private static String getString(){
        try(Scanner scan = new Scanner(System.in)) {
            return scan.nextLine();
        }catch (InputMismatchException e){
            return getString();
        }
    }

    public static String getString(String message){
        System.out.println(message);
        return getString();
    }
}