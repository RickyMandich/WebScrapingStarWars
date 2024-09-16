package com.example.webscraping;

import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class Carta{
    boolean unica;                          // fatto
    String nome;                            // fatto
    String titolo;                          // fatto
    String espansione;                      // fatto
    int numero;                             // fatto
    String aspettoPrimario;                 // fatto
    String aspettoSecondario;               // fatto
    String tipo;                            // fatto
    String[] tratti;                        // fatto
    boolean imboscata;                      // todo
    boolean tenacia;                        // todo
    boolean sopraffazione;                  // todo
    boolean sabotatore;                     // todo
    boolean sentinella;                     // todo
    boolean schermata;                      // todo
    boolean incursione;                     // todo
    int valoreIncursione;                   // todo
    boolean recupero;                       // todo
    int valoreRecupero;                     // todo
    boolean contrabbando;                   // todo
    String valoreContrabbando;              // todo
    boolean quandoGiocata;                  // fatto
    String valoreQuandoGiocata;             // fatto
    boolean taglia;                         // todo
    String valoreTaglia;                    // todo
    boolean quandoSconfitta;                // todo
    String valoreQuandoSconfitta;           // todo
    boolean quandoAttacca;                  // fatto
    String valoreQuandoAttacca;             // fatto
    boolean descrizioneEvento;              // fatto
    String valoreDescrizioneEvento;         // fatto
    boolean azione;                         // fatto
    String valoreAzione;                    // fatto
    String arena;                           // fatto
    int costo;                              // fatto
    int vita;                               // fatto
    int potenza;                            // fatto
    String rarita;                          // fatto
    double prezzo;                          // fatto
    String artista;                         // fatto

    Carta(WebDriver driver){
        nome = driver.findElement(By.className("col")).findElement(By.tagName("h4")).getText();
        unica = nome.startsWith("⟡ ");
        nome = nome.substring(unica ? 2 : 0);
        try{
            titolo = driver.findElement(By.className("card-title")).getText();
        }catch (NoSuchElementException e){
            titolo = "";
        }
        espansione = StringUtils.substringBetween(driver.findElement(By.className("card-expansion-name")).getText(), "(", ")");
        numero = Integer.parseInt(StringUtils.substringBetween(driver.findElement(By.className("card-expansion-header")).findElement(By.tagName("span")).getText(), "#", "•").replace(" ", ""));
        List<WebElement> aspetti = driver.findElements(By.className("card-stats-aspect"));
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
        tipo = driver.findElement(By.cssSelector(".col-3.d-flex.align-items-center.justify-content-center span")).getText();
        List<WebElement> trait = driver.findElement(By.className("card-trait-text")).findElements(By.tagName("a"));
        tratti = new String[0];
        for(WebElement t:trait){
            tratti = add(tratti, t.getText());
        }
        List<WebElement> ability = driver.findElements(By.className("card-ability-text"));
        for(WebElement a:ability){
            System.out.println("sono dentro il for each di a");
            List<WebElement> abilita = a.findElements(By.tagName("p"));
            for(WebElement p:abilita){
                System.out.println("sono dentro il for each di p");
                String stringCondizioneAbilita = "";
                try{
                    System.out.println("sono nel try di strong");
                    WebElement condizioneAbilita = p.findElement(By.tagName("strong"));
                    stringCondizioneAbilita = condizioneAbilita.getText();
                    try{
                        System.out.println("sono nel try di i");
                        stringCondizioneAbilita = stringCondizioneAbilita.replace(" [" + condizioneAbilita.findElement(By.tagName("i")).getText() + "]", "");
                    }catch (NoSuchElementException e){
                        System.out.println("i non trovato");
                    }
                }catch (NoSuchElementException e){
                    System.out.println("strong non trovato");
                }
                System.out.println(stringCondizioneAbilita);
                String[] condizioni = stringCondizioneAbilita.replace(":", "").split("/");
                for(String condition:condizioni) {
                    switch (condition) {
                        case "Action":
                            azione = true;
                            valoreAzione = p.getText().replace(p.findElement(By.tagName("strong")).getText(), "");
                            break;
                        case "On Attack":
                            quandoAttacca = true;
                            valoreQuandoAttacca = p.getText().replace(p.findElement(By.tagName("strong")).getText(), "");
                            break;
                        case "When Played":
                            quandoGiocata = true;
                            valoreQuandoAttacca = p.getText().replace(p.findElement(By.tagName("strong")).getText(), "");
                            break;
                        default:
                            descrizioneEvento = true;
                            valoreDescrizioneEvento = p.getText();
                            break;
                    }
                }
            }
        }
        arena = driver.findElement(By.cssSelector(".card-stats-arena-box>span")).getText();
        costo = Integer.parseInt(driver.findElement(By.cssSelector("div.card-info-box>div.row.card-stats-row>div.col-3.card-resources.d-flex.align-items-center.justify-content-center")).getText());
        vita = Integer.parseInt(driver.findElement(By.className("card-hp")).getText());
        potenza = Integer.parseInt(driver.findElement(By.className("card-power")).getText());
        rarita = driver.findElement(By.cssSelector(".card-expansion-header span")).getText().split(" • ")[1];
        prezzo = Double.parseDouble(driver.findElement(By.cssSelector(".container>.row.mt-1>.col.px-0>a[href*=\"tcgplayer.com\"]")).getText().split("\\$")[1]);
        artista = driver.findElement(By.cssSelector(".card-stats-artist>a")).getText();
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
        info += "\nimboscata:\t" + imboscata + "\n";
        info += "tenacia:\t" + tenacia + "\n";
        info += "sopraffazione:\t" + sopraffazione + "\n";
        info += "sabotatore:\t" + sabotatore + "\n";
        info += "sentinella:\t" + sentinella + "\n";
        info += "schermata:\t" + schermata + "\n";
        info += "incursione:\t" + incursione + "\n";
        info += "valoreIncursione:\t" + valoreIncursione + "\n";
        info += "recupero:\t" + recupero + "\n";
        info += "valoreRecupero:\t" + valoreRecupero + "\n";
        info += "contrabbando:\t" + contrabbando + "\n";
        info += "valoreContrabbando:\t" + valoreContrabbando + "\n";
        info += "quandoGiocata:\t" + quandoGiocata + "\n";
        info += "valoreQuandoGiocata:\t" + valoreQuandoGiocata + "\n";
        info += "taglia:\t" + taglia + "\n";
        info += "valoreTaglia:\t" + valoreTaglia + "\n";
        info += "quandoSconfitta:\t" + quandoSconfitta + "\n";
        info += "valoreQuandoSconfitta:\t" + valoreQuandoSconfitta + "\n";
        info += "quandoAttacca:\t" + quandoAttacca + "\n";
        info += "valoreQuandoAttacca:\t" + valoreQuandoAttacca + "\n";
        info += "descrizioneEvento:\t" + descrizioneEvento + "\n";
        info += "valoreDescrizioneEvento:\t" + valoreDescrizioneEvento + "\n";
        info += "azione:\t" + azione + "\n";
        info += "valoreAzione:\t" + valoreAzione + "\n";
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
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://swudb.com/card/sor/014/sabine-wren");
            System.out.println(new Carta(driver));
        }finally {
            driver.quit();
        }
    }
}