package com.example.webscraping;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.join;

public class Carta{
    boolean unica;
    String nome = "";
    String titolo = "";
    String espansione = "";
    String uscita = "";
    int numero;
    String aspettoPrimario = "";
    String aspettoSecondario = "";
    String tipo = "";
    String[] tratti;
    String descrizione = "";
    String arena = "";
    int costo;
    int vita;
    int potenza;
    String rarita = "";
    double prezzo;
    String artista = "";

    static Map<String, String> uscitaEspansioni = new HashMap<>();

    public Carta(WebDriver driver){
        nome = removeSlash(driver.findElement(By.className("col")).findElement(By.tagName("h4")));
        unica = nome.startsWith("⟡ ");
        nome = nome.substring(unica ? 2 : 0);
        try{
            titolo = removeSlash(driver.findElement(By.className("card-title")));
        }catch (NoSuchElementException e){
            titolo = "";
        }
        espansione = StringUtils.substringBetween(removeSlash(driver.findElement(By.className("card-expansion-name"))), "(", ")");
        uscita = uscitaEspansioni.get(espansione);
        numero = Integer.parseInt(StringUtils.substringBetween(removeSlash(driver.findElement(By.className("card-expansion-header")).findElement(By.tagName("span"))), "#", "•").replace(" ", ""));
        List<WebElement> aspetti = driver.findElements(By.cssSelector("div>.card-stats-aspect"));
        boolean primoAspetto = true;
        for(WebElement aspect : aspetti){
            String aspetto = aspect.getAttribute("alt").replace(" Aspect", "");
            switch (aspetto){
                case "Vigilance":
                    if(primoAspetto){
                        primoAspetto = false;
                        aspettoPrimario = "Blue";
                    }else aspettoSecondario = "Blue";
                    break;
                case "Command":
                    if(primoAspetto){
                        primoAspetto = false;
                        aspettoPrimario = "Green";
                    }else aspettoSecondario = "Green";
                    break;
                case "Aggression":
                    if(primoAspetto){
                        primoAspetto = false;
                        aspettoPrimario = "Red";
                    }else aspettoSecondario = "Red";
                    break;
                case "Cunning":
                    if(primoAspetto){
                        primoAspetto = false;
                        aspettoPrimario = "Yellow";
                    }else aspettoSecondario = "Yellow";
                    break;
                case "Heroism":
                    if(primoAspetto){
                        primoAspetto = false;
                        aspettoPrimario = "Light";
                    }else aspettoSecondario = "Light";
                    break;
                case "Villainy":
                    if(primoAspetto){
                        primoAspetto = false;
                        aspettoPrimario = "Dark";
                    }else aspettoSecondario = "Dark";
                    break;
                default:
                    if(primoAspetto){
                        primoAspetto = false;
                        aspettoPrimario = aspetto;
                    }else aspettoSecondario = aspetto;
            }
        }
        tipo = removeSlash(driver.findElement(By.cssSelector(".col-3.d-flex.align-items-center.justify-content-center span")));
        List<WebElement> trait = driver.findElements(By.className("card-trait-text")).getLast().findElements(By.tagName("a"));
        tratti = new String[0];
        for(WebElement t:trait){
            tratti = add(tratti, removeSlash(t));
        }
        List<WebElement> ability = driver.findElements(By.className("card-ability-text"));
        for(WebElement a:ability){
            List<WebElement> abilita = a.findElements(By.tagName("p"));
            for(WebElement p:abilita) {
                String innerHTML = p.getAttribute("innerHTML").replace("<i class=\"fa-solid fa-turn-down fa-rotate-270 ps-1\"></i>", "->");
                innerHTML = innerHTML.replace("<img src=\"/images/Vigilance.png\" class=\"card-stats-aspect line-height\" alt=\"Vigilance Aspect\">", "B");
                innerHTML = innerHTML.replace("<img src=\"/images/Command.png\" class=\"card-stats-aspect line-height\" alt=\"Command Aspect\">", "G");
                innerHTML = innerHTML.replace("<img src=\"/images/Aggression.png\" class=\"card-stats-aspect line-height\" alt=\"Aggression Aspect\">", "R");
                innerHTML = innerHTML.replace("<img src=\"/images/Cunning.png\" class=\"card-stats-aspect line-height\" alt=\"Cunning Aspect\">", "Y");
                innerHTML = innerHTML.replace("<img src=\"/images/Villainy.png\" class=\"card-stats-aspect line-height\" alt=\"Villainy Aspect\">", "D");
                innerHTML = innerHTML.replace("<img src=\"/images/Heroism.png\" class=\"card-stats-aspect line-height\" alt=\"Heroism Aspect\">", "W");
                innerHTML = innerHTML.replace("</span>", "");
                Pattern pspan = Pattern.compile("<span.*?>");
                Matcher mspan = pspan.matcher(innerHTML);
                try {
                    while(mspan.find()){
                        innerHTML = innerHTML.replace(mspan.group(), "");
                    }
                } catch (Exception ignore) {}
                innerHTML = innerHTML.replace("<em>", "");
                innerHTML = innerHTML.replace("</em>", "");
                innerHTML = innerHTML.replace("<strong>", "");
                innerHTML = innerHTML.replace("</strong>", "");
                descrizione = descrizione.concat("ability:"+ innerHTML);
            }
        }
        try{
            arena = removeSlash(driver.findElement(By.cssSelector(".card-stats-arena-box>span")));
        }catch (NoSuchElementException e){
            arena = "";
        }
        try{
            costo = Integer.parseInt(removeSlash(driver.findElement(By.cssSelector("div.card-info-box>div.row.card-stats-row>div.col-3.card-resources.d-flex.align-items-center.justify-content-center"))));
        }catch (NoSuchElementException|NumberFormatException e){
            costo = 0;
        }
        try{
            vita = Integer.parseInt(removeSlash(driver.findElement(By.className("card-hp"))));
        }catch (NoSuchElementException|NumberFormatException e){
            vita = 0;
        }
        try{
            potenza = Integer.parseInt(removeSlash(driver.findElement(By.className("card-power"))));
        }catch (NoSuchElementException|NumberFormatException ignored){}
        rarita = removeSlash(driver.findElement(By.cssSelector(".card-expansion-header span"))).split(" • ")[1];
        List<WebElement> prezzi = driver.findElements(By.cssSelector(".container>.row.mt-1>.col.px-0>a"));
        for(WebElement p:prezzi){
            try{
                prezzo = Double.parseDouble(removeSlash(p).split("\\$")[1]);
            }catch (ArrayIndexOutOfBoundsException ignore){}
        }
        artista = removeSlash(driver.findElement(By.cssSelector(".card-stats-artist>a")));
    }

    public Carta(String jsonString) {
        JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
        this.unica = jsonObject.get("unica").getAsBoolean();
        this.nome = jsonObject.get("nome").getAsString();
        this.titolo = jsonObject.get("titolo").getAsString();
        this.espansione = jsonObject.get("espansione").getAsString();
        this.numero = jsonObject.get("numero").getAsInt();
        this.aspettoPrimario = jsonObject.get("aspettoPrimario").getAsString();
        this.aspettoSecondario = jsonObject.get("aspettoSecondario").getAsString();
        this.tipo = jsonObject.get("tipo").getAsString();
        this.tratti = new Gson().fromJson(jsonObject.get("tratti"), String[].class);
        this.descrizione = jsonObject.get("descrizione").getAsString();
        this.arena = jsonObject.get("arena").getAsString();
        this.costo = jsonObject.get("costo").getAsInt();
        this.vita = jsonObject.get("vita").getAsInt();
        this.potenza = jsonObject.get("potenza").getAsInt();
        this.rarita = jsonObject.get("rarita").getAsString();
        this.prezzo = jsonObject.get("prezzo").getAsDouble();
        this.artista = jsonObject.get("artista").getAsString();
    }

    private String[] add(String[] oldArray, String newElement){
        String[] newArray = new String[oldArray.length + 1];
        System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
        newArray[oldArray.length] = newElement;
        return newArray;
    }

    public String toString() {
        String info = "";
        info += "unica:\t" + unica + "\n";
        info += "nome:\t" + nome + "\n";
        info += "titolo:\t" + titolo + "\n";
        info += "espansione:\t" + espansione + "\n";
        info += "numero:\t" + numero + "\n";
        info += "aspettoPrimario:\t" + aspettoPrimario + "\n";
        info += "aspettoSecondario:\t" + aspettoSecondario + "\n";
        info += "tipo:\t" + tipo + "\n";
        info += "tratti:\n";
        boolean primo = true;
        for(String t:tratti) {
            if (primo) info = info.concat(t);
            else info = info.concat(" * " + t);
            primo = false;
        }
        info += "\ndescrizione:\n" + descrizione + "\n";
        info += "arena:\t" + arena + "\n";
        info += "costo:\t" + costo + "\n";
        info += "vita:\t" + vita + "\n";
        info += "potenza:\t" + potenza + "\n";
        info += "rarita:\t" + rarita + "\n";
        info += "prezzo:\t" + prezzo + "\n";
        info += "artista:\t" + artista + "\n";
        return info;
    }



    public static void main(String[] args){
        WebDriver driver = null;
        try {
            System.out.println("inserisci l'espansione");
            String espansione = /*new java.util.Scanner(System.in).nextLine()*/"SHD".toUpperCase();
            System.out.println("inserisci il numero");
            String numero = String.format("%03d", /*new java.util.Scanner(System.in).nextInt()*/38);
            String link = "https://swudb.com/card/" + espansione + "/" + numero;
            System.out.println("web scraping di " + link);
            driver = new WebDriverWithoutImage();
            driver.get(link);
            Carta c = new Carta(driver);
            System.out.println(c);
            System.out.println(new Gson().toJson(c));
        }catch (Exception e){
            System.out.println("c'è stato un errore, riprova");
            e.printStackTrace();
        }finally {
            driver.quit();
        }
    }

    public String removeSlash(WebElement element){
        return element.getText().replace("\"", "\\\"");
    }
}