package com.example.webscraping;

import com.google.gson.Gson;
import org.apache.commons.net.ftp.FTPClient;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scan {
    public static long tempo;

    public static boolean hasCid(String url) {
        String cid = "";
        Pattern pattern = Pattern.compile("cid=([0-9]+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            cid = matcher.group(1);
        }
        try{
            return !cid.isEmpty();
        } catch (NumberFormatException e) {
            System.out.println("url:\t" + url);
            System.out.println("cid:\t" + cid);
            throw e;
        }
    }

    public static String extractCid(String url) {
        String cid = "";
        Pattern pattern = Pattern.compile("cid=([0-9]+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            cid = matcher.group(1);
        }
        try{
            return cid;
        } catch (NumberFormatException e) {
            System.out.println("url:\t" + url);
            System.out.println("cid:\t" + cid);
            throw e;
        }
    }

    public static String[] add(String[] oldArray, String newElement) {
        String[] newArray = new String[oldArray.length + 1];
        System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
        newArray[oldArray.length] = newElement;
        return newArray;
    }

    public static Carta[] add(Carta[] array, Carta line){
        Carta[] newArray = new Carta[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = line;
        return newArray;
    }

    public static void main(String[] args) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Esegue Chrome in modalità headless
        WebDriver driver = new ChromeDriver(/*options*/);
        long tempo  = System.nanoTime() / 1000000;
        try {
            driver.get("https://starwarsunlimited.com/it/cards");
            ((JavascriptExecutor) driver).executeScript("document.body.style.zoom='25%'");
            int i = 0;
            do{
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,1000);");
                i++;
            }while(driver.findElement(By.cssSelector("body")).getText().toLowerCase().contains("carica"));

            i=1;
            int j=0;
            String[] cid = new String[0];
            List<WebElement> cardImages = driver.findElements(By.cssSelector("img[alt='Fronte Della Carta']"));
            while (cardImages.size() != 0) {
                try{
                    j++;
                    System.out.println("--------------------------------------------------------------------------------------------------------");
                    System.out.println("tentativo " + j);
                    List<WebElement> subCardImages = new ArrayList<>(cardImages);
                    for (WebElement cardImage : subCardImages) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", cardImage);
                        boolean continueLoop = false;
                        while (!continueLoop) {
                            if(!((long) ((JavascriptExecutor) driver).executeScript("return document.querySelectorAll('div.flex.gap-4.items-start button:last-child').length")>0)){
                                cardImage.click();
                                continueLoop = true;
                            }else{
                                closeWindow(driver);
                                continueLoop = false;
                            }
                        }
                        String url = driver.getCurrentUrl();
                        try{
                            System.out.println(i + ")\t" + (i++<100?"\t":"") + extractCid(url));
                            if(hasCid(url)) {
                                cid = add(cid, extractCid(url));
                                cardImages.remove(cardImage);
                            }
                        }catch (NumberFormatException ignore){}
                    }
                }catch (org.openqa.selenium.ElementClickInterceptedException | org.openqa.selenium.JavascriptException ignore){}
            }
            System.out.println("ho usato " + j + " subList");
            String[] carte = new String[cid.length];
            try(FileWriter writer = new FileWriter("log.txt")){
                writer.write("ho usato " + j + " subList\n");
                i=0;
                for(String c : cid){
                    carte[i] = String.valueOf(c);
                    writer.write(i++ + ")\t" + (i<100?"\t":"") + c + "\n");
                }
            }catch (IOException ignore){}
            long tempoTrascorso = System.nanoTime() / 1000000;
            tempoTrascorso = tempoTrascorso - tempo;
            System.out.println("tempo trascorso:\t" + formattaSecondi(tempoTrascorso));
            driver.quit();
            List<Carta> collezione = new ArrayList<>();
            ThreadIta t = new ThreadIta(carte, collezione);
            t.start();
            while (t.isAlive()) {
            }
            tempoTrascorso = System.nanoTime() / 1000000;
            tempoTrascorso = tempoTrascorso - tempo;
            System.out.println("tempo trascorso:\t" + formattaSecondi(tempoTrascorso));
            Toolkit.getDefaultToolkit().beep();
            String json = json(collezione);
            try(FileWriter writer = new FileWriter("collezione.json")){
                writer.write(json);
                uploadWithFtp("collezione.json");
            }catch (IOException e){
                System.out.println("non ho trovato \"collezione.json\", inserisci tu il nome del file");
                scrivi(json);
            }
        }finally {
            try{Thread.sleep(5000);}catch (Exception e){}
            driver.quit();
        }
    }

    public static String formattaSecondi(long secondi){
        long minuti = 0;
        long ore = 0;
        while(secondi>=60){
            secondi -= 60;
            minuti++;
        }
        while(minuti>=60){
            minuti -= 60;
            ore++;
        }
        return String.format("%02d", ore) + ":" + String.format("%02d", minuti) + ":" + String.format("%02d", secondi);
    }

    public static void uploadWithFtp(String filePath){
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect("ftp.swudb.altervista.org");
            ftp.login("swudb", "Minecraft35?");
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.storeFile(filePath, new FileInputStream(filePath));
            ftp.logout();
            ftp.disconnect();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String json(List<Carta> collezione) {
        return new Gson().toJson(collezione);
    }

    public static String json(Carta[] collezione) {
        return new Gson().toJson(collezione);
    }

    public static String getString(){
        try {
            return new Scanner(System.in).nextLine();
        } catch (NoSuchElementException e) {
            return getString();
        }
    }

    public static String getString(String message){
        System.out.println(message);
        Toolkit.getDefaultToolkit().beep();
        return getString();
    }

    public static void scrivi(String json){
        try(FileWriter writer = new FileWriter(getString("inserisci il percorso del file in cui salvare il json"))){
            writer.write(json);
        }catch (IOException e){
            System.out.println("errore nella scrittura del file");
            scrivi(json);
        }
    }

    public static void closeWindow(WebDriver driver) {
        while ((long) ((JavascriptExecutor) driver).executeScript("return document.querySelectorAll('div.flex.gap-4.items-start button:last-child').length")>0) {
            ((JavascriptExecutor) driver).executeScript("document.querySelector(\"div.flex.gap-4.items-start button:last-child\").click()");
        }
    }
}
