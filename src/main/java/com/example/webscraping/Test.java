package com.example.webscraping;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static Long extractCid(String url) {
        String cid = "";
        Pattern pattern = Pattern.compile("cid=([0-9]+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            cid = matcher.group(1);
        }
        try{
            return Long.parseLong(cid);
        } catch (NumberFormatException e) {
            System.out.println("url:\t" + url);
            System.out.println("cid:\t" + cid);
            throw e;
        }
    }

    public static void main(String[] args) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Esegue Chrome in modalità headless
        WebDriver driver = new ChromeDriver(options);
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
            Long[] cid = new Long[0];
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
                            cid = Scan.add(cid, extractCid(url));
                            cardImages.remove(cardImage);
                        }catch (NumberFormatException ignore){}
                    }
                }catch (org.openqa.selenium.ElementClickInterceptedException | org.openqa.selenium.JavascriptException ignore){}
            }
            System.out.println("ho usato " + j + " subList");
            String[] carte = new String[cid.length];
            try(FileWriter writer = new FileWriter("log.txt")){
                writer.write("ho usato " + j + " subList\n");
                i=0;
                for(Long c : cid){
                    carte[i] = "https://starwarsunlimited.com/it/cards?cid=" + c;
                    writer.write(i++ + ")\t" + (i<100?"\t":"") + c + "\n");
                }
            }catch (IOException ignore){}
            long tempoTrascorso = System.nanoTime() / 1000000;
            tempoTrascorso = tempoTrascorso - tempo;
            System.out.println("tempo trascorso:\t" + Scan.formattaSecondi(tempoTrascorso));
            driver.quit();
            List<Carta> collezione = new ArrayList<>();
            ThreadIta t = new ThreadIta(carte, collezione);
            t.start();
            tempoTrascorso = System.nanoTime() / 1000000;
            tempoTrascorso = tempoTrascorso - tempo;
            System.out.println("tempo trascorso:\t" + Scan.formattaSecondi(tempoTrascorso));
            Toolkit.getDefaultToolkit().beep();
            String json = Scan.json(collezione);
            try(FileWriter writer = new FileWriter("collezione.json")){
                writer.write(json);
                Scan.uploadWithFtp("collezione.json");
            }catch (IOException e){
                System.out.println("non ho trovato \"collezione.json\", inserisci tu il nome del file");
                Scan.scrivi(json);
            }
        }finally {
            try{Thread.sleep(5000);}catch (Exception e){}
            driver.quit();
        }
    }

    public static void closeWindow(WebDriver driver) {
        while ((long) ((JavascriptExecutor) driver).executeScript("return document.querySelectorAll('div.flex.gap-4.items-start button:last-child').length")>0) {
            ((JavascriptExecutor) driver).executeScript("document.querySelector(\"div.flex.gap-4.items-start button:last-child\").click()");
        }
    }
}
