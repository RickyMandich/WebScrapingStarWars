package com.example.webscraping;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    String artista = "";

    static Map<String, String> uscitaEspansioni = new HashMap<>();

    public Carta(WebDriver driver){
        this(driver, false);
    }

    public Carta(WebDriver driver, boolean verbose){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        nome = removeSlash(wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1.text-center.text-2xl"))));
        if(verbose) System.out.println("nome:\t" + nome);
        unica = nome.startsWith("⟡ ");
        if(verbose) System.out.println("unica:\t" + unica);
        nome = nome.substring(unica ? 2 : 0);
        try{
            titolo = removeSlash(driver.findElement(By.cssSelector(".mt-1.text-center")));
        }catch (NoSuchElementException e){
            titolo = "";
        }
        if(verbose) System.out.println("titolo:\t" + titolo);
        espansione = StringUtils.substringBetween(removeSlash(driver.findElement(By.cssSelector("h1.text-2xl.text-gray-50.dark\\:text-gray-950"))), "(", ")");
        if(verbose) System.out.println("espansione:\t" + espansione);
        uscita = uscitaEspansioni.get(espansione);
        if(verbose) System.out.println("uscita:\t" + uscita);
        numero = Integer.parseInt(StringUtils.substringBetween(removeSlash(driver.findElement(By.cssSelector("div.text-md.font-light"))), "#", "•").replace(" ", ""));
        if(verbose) System.out.println("numero:\t" + numero);
        List<WebElement> aspetti = driver.findElements(By.cssSelector("img[alt*='Aspect']"));
        boolean primoAspetto = true;
        for(WebElement aspect : aspetti){
            String aspetto = aspect.getAttribute("alt").replace("Aspect ", "");
            switch (aspetto){
                case "4":
                    if(primoAspetto){
                        primoAspetto = false;
                        aspettoPrimario = "Blue";
                    }else aspettoSecondario = "Blue";
                    break;
                case "2":
                    if(primoAspetto){
                        primoAspetto = false;
                        aspettoPrimario = "Green";
                    }else aspettoSecondario = "Green";
                    break;
                case "1":
                    if(primoAspetto){
                        primoAspetto = false;
                        aspettoPrimario = "Red";
                    }else aspettoSecondario = "Red";
                    break;
                case "3":
                    if(primoAspetto){
                        primoAspetto = false;
                        aspettoPrimario = "Yellow";
                    }else aspettoSecondario = "Yellow";
                    break;
                case "5":
                    if(primoAspetto){
                        primoAspetto = false;
                        aspettoPrimario = "Light";
                    }else aspettoSecondario = "Light";
                    break;
                case "6":
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
        if(verbose) System.out.println("aspettoPrimario:\t" + aspettoPrimario);
        if(verbose) System.out.println("aspettoSecondario:\t" + aspettoSecondario);
        tipo = removeSlash(driver.findElement(By.cssSelector("#rules-box > div:nth-child(2) > div.items-center.text-center.font-normal > div:nth-child(1)")));
        if(verbose) System.out.println("tipo:\t" + tipo);
        List<WebElement> trait = driver.findElement(By.cssSelector("a[href*='/tr']")).findElements(By.tagName("a"));
        tratti = new String[0];
        for(WebElement t:trait){
            tratti = add(tratti, removeSlash(t));
        }
        if(verbose) System.out.println("tratti:\t" + Scan.join(tratti, " * "));
        List<WebElement> ability = driver.findElements(By.cssSelector("div[id*='ability-text-box']"));
        for(WebElement a:ability){
            if(ability.size() > 1){
                if(a.equals(ability.getFirst())){
                    descrizione = descrizione.concat("non schierato:\n");
                }else{
                    descrizione = descrizione.concat("schierato:\n");
                }
            }
            List<WebElement> abilita = a.findElements(By.tagName("p"));
            for(WebElement p:abilita) {
                String innerHTML = p.getAttribute("innerHTML").replace("<i class=\"fa-solid fa-turn-down fa-rotate-270 ps-1\"></i>", "->");
                innerHTML = innerHTML.replace("<img src=\"/images/Vigilance.png\" class=\"card-stats-aspect line-height\" alt=\"Aspect 4\">", "B");
                innerHTML = innerHTML.replace("<img src=\"/images/Command.png\" class=\"card-stats-aspect line-height\" alt=\"Aspect 2\">", "G");
                innerHTML = innerHTML.replace("<img src=\"/images/Aggression.png\" class=\"card-stats-aspect line-height\" alt=\"Aspect 1\">", "R");
                innerHTML = innerHTML.replace("<img src=\"/images/Cunning.png\" class=\"card-stats-aspect line-height\" alt=\"Aspect 3\">", "Y");
                innerHTML = innerHTML.replace("<img src=\"/images/Villainy.png\" class=\"card-stats-aspect line-height\" alt=\"Aspect 5\">", "D");
                innerHTML = innerHTML.replace("<img src=\"/images/Heroism.png\" class=\"card-stats-aspect line-height\" alt=\"Aspect 6\">", "W");
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
                descrizione = descrizione.concat((ability.size() > 1 ? "\t":"") + "ability:" + innerHTML + "\n");
            }
        }
        if(verbose) System.out.println("descrizione:\n" + descrizione);
        try{
            arena = removeSlash(driver.findElement(By.cssSelector("#rules-box > div:nth-child(2) > div.items-center.text-center.font-normal > div:nth-child(2) > div:nth-child(2)")));
        }catch (NoSuchElementException e){
            arena = "";
        }
        if(verbose) System.out.println("arena:\t" + arena);
        try{
            costo = Integer.parseInt(removeSlash(driver.findElement(By.cssSelector("span.text-3xl.text-yellow-400"))));
        }catch (NoSuchElementException|NumberFormatException e){
            costo = 0;
        }
        if(verbose) System.out.println("costo:\t" + costo);
        try{
            vita = Integer.parseInt(removeSlash(driver.findElement(By.cssSelector("span.text-3xl.text-blue-700"))));
        }catch (NoSuchElementException|NumberFormatException e){
            vita = 0;
        }
        if(verbose) System.out.println("vita:\t" + vita);
        try{
            potenza = Integer.parseInt(removeSlash(driver.findElement(By.cssSelector("span.text-3xl.text-red-700"))));
        }catch (NoSuchElementException|NumberFormatException ignored){
            potenza = 0;
        }
        if(verbose) System.out.println("potenza:\t" + potenza);
        rarita = StringUtils.substringBetween(removeSlash(driver.findElement(By.cssSelector("div.text-md.font-light"))), "• ", " •").replace(" ", "");
        if(verbose) System.out.println("rarita:\t" + rarita);
        artista = removeSlash(driver.findElement(By.cssSelector("a[href*='/artist']")));
        if(verbose) System.out.println("artista:\t" + artista);
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
        info += "artista:\t" + artista + "\n";
        return info;
    }



    public static void main(String[] args){
        WebDriver driver = new WebDriverWithoutImage();
        try {
            System.out.println("inserisci l'espansione");
            String espansione = /*new java.util.Scanner(System.in).nextLine()*/"TWI".toUpperCase();
            System.out.println("inserisci il numero");
            String numero = String.format("%03d", /*new java.util.Scanner(System.in).nextInt()*/1);
            String link = "https://swudb.com/card/" + espansione + "/" + numero;
            System.out.println("web scraping di " + link);
            driver.get(link);
            Carta c = new Carta(driver, false);
            System.out.println(c);
            System.out.println(new Gson().toJson(c));
        }catch (Exception e){
            System.out.println("c'è stato un errore, riprova");
            System.out.println(e.getMessage());
        }finally {
            driver.quit();
        }
    }

    public String removeSlash(WebElement element){
        return element.getText().replace("\"", "\\\"");
    }
}