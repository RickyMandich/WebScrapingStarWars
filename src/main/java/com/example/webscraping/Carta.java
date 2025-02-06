package com.example.webscraping;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Carta {
    long cid;
    String nome;
    String titolo;
    boolean unica;
    String espansione;
    int numero;
    String aspettoPrimario;
    String aspettoSecondario;
    String tipo;
    String[] tratti;
    String descrizione;
    String arena;
    int costo;
    int vita;
    int potenza;
    String rarita;
    double prezzo;
    String artista;

    static Map<String, String> uscitaEspansioni = new HashMap<>();

    public Carta(WebDriver driver) {
        System.out.println("----------------------inizio costruttore----------------------");
        cid = Test.extractCid(driver.getCurrentUrl());
        System.out.println("cid:\t" + cid);
        nome = driver.findElements(By.cssSelector("h3.text-2xl.font-extrabold")).getFirst().getText();
        System.out.println("nome fatto");
        try {
            titolo = driver.findElements(By.cssSelector("p.text-neutral-400.italic")).getFirst().getText();
            System.out.println("titolo fatto");
            unica = true;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            unica = false;
        }
        try{
            this.numero = Integer.parseInt(driver.findElements(By.xpath("/html/body/div[17]/div/div/div/div[2]/div/div[3]/div[1]/div[6]/div[2]/div[7]/span[2]")).getFirst().getText().split("/")[0]);
            System.out.println("numero fatto");
        }catch (java.util.NoSuchElementException e){
            System.out.println("numero:\t" + driver.findElements(By.xpath("/html/body/div[17]/div/div/div/div[2]/div/div[3]/div[1]/div[6]/div[2]/div[7]/span[2]")).size());
        }
        try{
            String[] aspetti = driver.findElements(By.xpath("/html/body/div[17]/div/div/div/div[2]/div/div[3]/div[1]/div[6]/div[1]/div[1]/span[2]")).getFirst().getText().split(", ");
            if(aspetti.length > 0) aspettoPrimario = traduciAspetto(aspetti[0]);
            if(aspetti.length > 1) aspettoSecondario = traduciAspetto(aspetti[1]);
            System.out.println("aspetti fatto");
        }catch (java.util.NoSuchElementException e){
            System.out.println("aspetto:\t" + driver.findElements(By.xpath("/html/body/div[17]/div/div/div/div[2]/div/div[3]/div[1]/div[6]/div[1]/div[1]/span[2]")).size());
        }
        try{
            tipo = driver.findElements(By.xpath("/html/body/div[17]/div/div/div/div[2]/div/div[3]/div[1]/div[6]/div[2]/div[1]/span[2]")).getFirst().getText();
            System.out.println("tipo fatto");
        }catch (java.util.NoSuchElementException e){
            System.out.println("tipo:\t" + driver.findElements(By.xpath("/html/body/div[17]/div/div/div/div[2]/div/div[3]/div[1]/div[6]/div[2]/div[1]/span[2]")).size());
        }
        try{
            tratti = driver.findElements(By.xpath("/html/body/div[17]/div/div/div/div[2]/div/div[3]/div[1]/div[6]/div[2]/div[3]/span[2]")).getFirst().getText().split(", ");
            System.out.println("tratti fatto");
        }catch (java.util.NoSuchElementException e){
            System.out.println("tratti:\t" + driver.findElements(By.xpath("/html/body/div[17]/div/div/div/div[2]/div/div[3]/div[1]/div[6]/div[2]/div[3]/span[2]")).size());
        }
        try{
            descrizione = driver.findElements(By.xpath("/html/body/div[17]/div/div/div/div[2]/div/div[3]/div[1]")).getFirst().getText();
            System.out.println("descrizione fatto");
        }catch (java.util.NoSuchElementException e){
            System.out.println("descrizione:\t" + driver.findElements(By.xpath("/html/body/div[17]/div/div/div/div[2]/div/div[3]/div[1]")).size());
        }
        try{
            arena = driver.findElements(By.cssSelector("html > body > div:nth-of-type(21) > div > div > div > div:nth-of-type(2) > div > div:nth-of-type(3) > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(2) > span:nth-of-type(2)")).getFirst().getText();
            System.out.println("arena fatto");
        }catch (java.util.NoSuchElementException e){
            System.out.println("arena:\t" + driver.findElements(By.cssSelector("html > body > div:nth-of-type(21) > div > div > div > div:nth-of-type(2) > div > div:nth-of-type(3) > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(2) > span:nth-of-type(2)")).size());
        }
        try{
            costo = Integer.parseInt(driver.findElements(By.cssSelector("html > body > div:nth-of-type(21) > div > div > div > div:nth-of-type(2) > div > div:nth-of-type(3) > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(3) > span:nth-of-type(2)")).getFirst().getText());
            System.out.println("costo fatto");
        }catch (java.util.NoSuchElementException e){
            System.out.println("costo:\t" + driver.findElements(By.cssSelector("html > body > div:nth-of-type(21) > div > div > div > div:nth-of-type(2) > div > div:nth-of-type(3) > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(3) > span:nth-of-type(2)")).size());
        }
        try{
            vita = Integer.parseInt(driver.findElements(By.cssSelector("html > body > div:nth-of-type(21) > div > div > div > div:nth-of-type(2) > div > div:nth-of-type(3) > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(5) > span:nth-of-type(2)")).getFirst().getText());
            System.out.println("vita fatto");
        }catch (java.util.NoSuchElementException e){
            System.out.println("vita:\t" +  driver.findElements(By.cssSelector("html > body > div:nth-of-type(21) > div > div > div > div:nth-of-type(2) > div > div:nth-of-type(3) > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(5) > span:nth-of-type(2)")).size());
        }
        try{
            potenza = Integer.parseInt(driver.findElements(By.cssSelector("html > body > div:nth-of-type(21) > div > div > div > div:nth-of-type(2) > div > div:nth-of-type(3) > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(4) > span:nth-of-type(2)")).getFirst().getText());
            System.out.println("potenza fatto");
        }catch (java.util.NoSuchElementException e){
            System.out.println("potenza:\t" + driver.findElements(By.cssSelector("html > body > div:nth-of-type(21) > div > div > div > div:nth-of-type(2) > div > div:nth-of-type(3) > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(4) > span:nth-of-type(2)")).size());
        }
        try{
            rarita = driver.findElements(By.cssSelector("html > body > div:nth-of-type(21) > div > div > div > div:nth-of-type(2) > div > div:nth-of-type(3) > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(2) > div:nth-of-type(4) > span:nth-of-type(2)")).getFirst().getText();
            System.out.println("rarita fatto");
        }catch (java.util.NoSuchElementException e){
            System.out.println("rarita:\t" + driver.findElements(By.cssSelector("html > body > div:nth-of-type(21) > div > div > div > div:nth-of-type(2) > div > div:nth-of-type(3) > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(2) > div:nth-of-type(4) > span:nth-of-type(2)")).size());
        }
        try{
            artista = driver.findElements(By.cssSelector("html > body > div:nth-of-type(21) > div > div > div > div:nth-of-type(2) > div > div:nth-of-type(3) > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(2) > div:nth-of-type(6) > span:nth-of-type(2)")).getFirst().getText();
            System.out.println("artista fatto");
        }catch (java.util.NoSuchElementException e){
            System.out.println("artista:\t" + driver.findElements(By.cssSelector("html > body > div:nth-of-type(21) > div > div > div > div:nth-of-type(2) > div > div:nth-of-type(3) > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(2) > div:nth-of-type(6) > span:nth-of-type(2)")).size());
        }
        driver.get("https://starwarsunlimited.com/it/cards?cid="+ cid +"#collection");
        try{
            espansione = driver.findElements(By.cssSelector("html > body > div:nth-of-type(21) > div > div > div > div:nth-of-type(2) > div > div:nth-of-type(3) > div:nth-of-type(2) > div > table > tbody > tr:nth-of-type(1) > th > span:nth-of-type(2) > span")).getFirst().getText();
            System.out.println("espansione fatto");
        }catch (java.util.NoSuchElementException e){
            System.out.println("espansione:\t" + driver.findElements(By.cssSelector("html > body > div:nth-of-type(21) > div > div > div > div:nth-of-type(2) > div > div:nth-of-type(3) > div:nth-of-type(2) > div > table > tbody > tr:nth-of-type(1) > th > span:nth-of-type(2) > span")).size());
        }
        System.out.println("----------------------fine costruttore----------------------");
    }

    public static String traduciAspetto(String aspetto){
        return switch (aspetto) {
            case "Vigilanza" -> "Blu";
            case "Malvagità" -> "nero";
            case "Eroismo" -> "bianco";
            case "Autorità" -> "verde";
            case "Offensiva" -> "rosso";
            case "Astuzia" -> "giallo";
            default -> aspetto;
        };
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

    private String[] add(String[] oldArray, String newElement) {
        String[] newArray = new String[oldArray.length + 1];
        System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
        newArray[oldArray.length] = newElement;
        return newArray;
    }

    public String toString() {
        String info = "";
        info += "unica:\t" + unica + "\n";
        if(nome != null){
            info +="nome:\t"+nome +"\n";
        }
        if(titolo != null){
            info += "titolo:\t" + titolo + "\n";
        }
        if(espansione != null){
            info += "espansione:\t" + espansione + "\n";
        }
        info += "numero:\t" + numero + "\n";
        if(aspettoPrimario != null){
            info += "aspettoPrimario:\t" + aspettoPrimario + "\n";
        }
        if(aspettoSecondario != null){
            info += "aspettoSecondario:\t" + aspettoSecondario + "\n";
        }
        if(tipo != null){
            info += "tipo:\t" + tipo + "\n";
        }
        if(tratti != null){
            info += "tratti:\n";
            boolean primo = true;
            for(String t:tratti) {
                if (primo) info = info.concat(t);
                else info = info.concat(" * " + t);
                primo = false;
            }
        }
        if(descrizione != null){
            info += "\ndescrizione:\n" + descrizione + "\n";
        }
        if(arena != null){
            info += "arena:\t" + arena + "\n";
        }
        info += "costo:\t" + costo + "\n";
        info += "vita:\t" + vita + "\n";
        info += "potenza:\t" + potenza + "\n";
        if(rarita != null){
            info += "rarita:\t" + rarita + "\n";
        }
        info += "prezzo:\t" + prezzo + "\n";
        if(artista != null) {
            info += "artista:\t" + artista + "\n";
        }
        return info;
    }

    public static long getLong(String message){
        System.out.println(message);
        return getLong();
    }

    public static long getLong(){
        try{
            return new java.util.Scanner(System.in).nextLong();
        }catch (java.util.InputMismatchException e){
            System.out.println("Errore: inserisci un long");
            return getLong();
        }
    }

    public static void main(String[] args) {
        boolean close = true;//Scan.getBoolean("vuoi chiudere il browser alla fine?");
        //insert another `*` on the first one to switch from the static to the dinamic input
        String cid = /*/String.valueOf(getLong("inserisci il cid (Carta ID) della carta che vuoi cercare"));/*/"4179470615";/**/
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Esegue Chrome in modalità headless
        WebDriver driver = new ChromeDriver(options);
        //String htmlContent = HtmlPageFetcher.getHtmlUsingSelenium("https://starwarsunlimited.com/it/cards?cid=" + cid);
        driver.get("https://starwarsunlimited.com/it/cards?cid=" + cid/*"data:text/html;charset=utf-8," + htmlContent*/);
        System.out.println("get eseguito, ora inizio a salvare i dati della carta");
        try {
            Carta carta = new Carta(driver);
            System.out.println(carta);
        }catch (Error e){
            System.out.println("errore: " + e.getMessage());
        }finally {
            if(close) driver.quit();
        }
        System.out.println("fine");
    }
}