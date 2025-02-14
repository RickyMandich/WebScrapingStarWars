package com.example.webscraping;

import com.google.gson.*;

import org.openqa.selenium.*;

import java.net.*;
import java.net.http.*;

public class Carta {
    String cid;
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
    String artista;

    public Carta(String cid) {
        String apiResult = apiCall(cid);
        JsonObject jsonObject = new Gson().fromJson(apiResult, JsonObject.class);

        // Accedi ai dati annidati
        JsonObject data = jsonObject.getAsJsonObject("data");
        JsonObject attributes = data.getAsJsonObject("attributes");

        // Estrai i dati base
        this.cid = cid;
        this.nome = attributes.get("title").getAsString();
        this.titolo = attributes.get("subtitle").getAsString();
        this.unica = attributes.get("unique").getAsBoolean();
        this.numero = attributes.get("cardNumber").getAsInt();
        this.descrizione = attributes.get("text").getAsString();
        this.costo = attributes.get("cost").getAsInt();
        this.vita = attributes.get("hp").getAsInt();
        this.potenza = attributes.get("power").getAsInt();
        this.artista = attributes.get("artist").getAsString();

        // Estrai espansione (solo il codice)
        this.espansione = attributes.getAsJsonObject("expansion")
                                  .getAsJsonObject("data")
                                  .getAsJsonObject("attributes")
                                  .get("code").getAsString();

        // Estrai arena
        JsonArray arenas = attributes.getAsJsonObject("arenas")
                                   .getAsJsonArray("data");
        if (arenas.size() > 0) {
            this.arena = arenas.get(0)
                              .getAsJsonObject()
                              .getAsJsonObject("attributes")
                              .get("name").getAsString();
        }

        // Estrai aspetti
        JsonArray aspects = attributes.getAsJsonObject("aspects")
                                    .getAsJsonArray("data");
        if (aspects.size() > 0) {
            this.aspettoPrimario = traduciAspetto(aspects.get(0)
                                        .getAsJsonObject()
                                        .getAsJsonObject("attributes")
                                        .get("name").getAsString());
        }
        if (aspects.size() > 1) {
            this.aspettoSecondario = traduciAspetto(aspects.get(1)
                                          .getAsJsonObject()
                                          .getAsJsonObject("attributes")
                                          .get("name").getAsString());
        }

        // Estrai tipo
        this.tipo = attributes.getAsJsonObject("type")
                             .getAsJsonObject("data")
                             .getAsJsonObject("attributes")
                             .get("name").getAsString();

        // Estrai tratti
        JsonArray traits = attributes.getAsJsonObject("traits")
                                   .getAsJsonArray("data");
        this.tratti = new String[traits.size()];
        for (int i = 0; i < traits.size(); i++) {
            this.tratti[i] = traits.get(i)
                                 .getAsJsonObject()
                                 .getAsJsonObject("attributes")
                                 .get("name").getAsString();
        }

        // Estrai rarità
        this.rarita = attributes.getAsJsonObject("rarity")
                               .getAsJsonObject("data")
                               .getAsJsonObject("attributes")
                               .get("name").getAsString();
    }

    public Carta(WebDriver driver) {
        System.out.println("----------------------inizio costruttore----------------------");
        cid = Scan.extractCid(driver.getCurrentUrl());
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
            case "Malvagità" -> "Nero";
            case "Eroismo" -> "Bianco";
            case "Autorità" -> "Verde";
            case "Offensiva" -> "Rosso";
            case "Astuzia" -> "Giallo";
            default -> aspetto;
        };
    }

    private String apiCall(String cid) {
        HttpResponse<String> response;
        try {
            try(HttpClient client = HttpClient.newHttpClient()) {
                String url = "https://admin.starwarsunlimited.com/api/card/" + cid + "?locale=it";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Accept", "application/json")
                        .GET()
                        .build();

                response = client.send(request, HttpResponse.BodyHandlers.ofString());


                if (response.statusCode() != 200) {
                    throw new RuntimeException("Errore API: " + response.statusCode());
                }
            }
            return response.body();

        } catch (Exception e) {
            System.out.println("current card id: " + cid);
            throw new RuntimeException("Errore nella chiamata API", e);
        }
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
        if(artista != null) {
            info += "artista:\t" + artista + "\n";
        }
        return info;
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
        //insert another `*` on the first one to switch from the static to the dinamic input
        String cid = /*/String.valueOf(getLong("inserisci il cid (Carta ID) della carta che vuoi cercare"));/*/"4179470615";/**/
        try {
            Carta carta = new Carta(cid);
            System.out.println(carta);
        }catch (Error e){
            e.printStackTrace();
        }
        System.out.println("fine");
    }
}