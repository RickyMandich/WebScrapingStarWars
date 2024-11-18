package com.example.webscraping;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

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
    boolean imboscata;
    boolean tenacia;
    boolean sopraffazione;
    boolean sabotatore;
    boolean sentinella;
    boolean schermata;
    boolean incursione;
    int valoreIncursione;
    boolean recupero;
    int valoreRecupero;
    boolean contrabbando;
    String valoreContrabbando = "";
    boolean quandoGiocata;
    String valoreQuandoGiocata = "";
    boolean taglia;
    String valoreTaglia = "";
    boolean quandoSconfitta;
    String valoreQuandoSconfitta = "";
    boolean quandoAttacca;
    String valoreQuandoAttacca = "";
    boolean descrizioneEvento;
    String valoreDescrizioneEvento = "";
    boolean azione;
    String valoreAzione = "";
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
        List<WebElement> trait = driver.findElement(By.className("card-trait-text")).findElements(By.tagName("a"));
        tratti = new String[0];
        for(WebElement t:trait){
            tratti = add(tratti, removeSlash(t));
        }
        List<WebElement> ability = driver.findElements(By.className("card-ability-text"));
        for(WebElement a:ability){
            List<WebElement> abilita = a.findElements(By.tagName("p"));
            for(WebElement p:abilita) {
                if(removeSlash(p).matches("^Action.*?")){
                    azione = true;
                    valoreAzione = String.valueOf(removeSlash(p).charAt(11));
                    List<WebElement> aspettiAzione = p.findElements(By.tagName("img"));
                    for(WebElement aspetto:aspettiAzione){
                        switch (aspetto.getAttribute("alt").toLowerCase()){
                            case "vigilance aspect":
                                valoreAzione = valoreAzione.concat("B");
                            break;
                            case "command aspect":
                                valoreAzione = valoreAzione.concat("G");
                            break;
                            case "aggression aspect":
                                valoreAzione = valoreAzione.concat("R");
                            break;
                            case "cunning aspect":
                                valoreAzione = valoreAzione.concat("Y");
                            break;
                            case "heroism aspect":
                                valoreAzione = valoreAzione.concat("H");
                            break;
                            case "villainy aspect":
                                valoreAzione = valoreAzione.concat("D");
                            break;
                        }
                    }
                    valoreAzione = valoreAzione.concat(removeSlash(p).substring(removeSlash(p).indexOf(":")+1));
                } else if(removeSlash(p).matches("^On Attack.*?")){
                    quandoAttacca = true;
                    valoreQuandoAttacca = removeSlash(p).substring(removeSlash(p).indexOf(":") + 1);
                } else if(removeSlash(p).matches("^When Played.*?")){
                    quandoGiocata = true;
                    valoreQuandoGiocata = removeSlash(p).substring(removeSlash(p).indexOf(":"));
                } else if(removeSlash(p).matches("^When Defeated.*?")){
                    quandoSconfitta = true;
                    valoreQuandoSconfitta = removeSlash(p).substring(removeSlash(p).indexOf(":"));
                } else if(removeSlash(p).matches("^When you play an [A-z]* card:.*")){
                    if(quandoGiocata){
                        valoreQuandoGiocata = valoreQuandoGiocata.concat(" * " + removeSlash(p));
                    } else{
                        quandoGiocata = true;
                        valoreQuandoGiocata = removeSlash(p);
                    }
                } else if(removeSlash(p).toUpperCase().matches("^grit.*?".toUpperCase())){
                    tenacia = true;
                } else if(removeSlash(p).toUpperCase().matches("^ambush.*?".toUpperCase())){
                    imboscata = true;
                } else if(removeSlash(p).toUpperCase().matches("^overwhelm.*?".toUpperCase())){
                    sopraffazione = true;
                } else if(removeSlash(p).toUpperCase().matches("^saboteur.*?".toUpperCase())){
                    sabotatore = true;
                } else if(removeSlash(p).toUpperCase().matches("^sentinel.*?".toUpperCase())){
                    sentinella = true;
                } else if(removeSlash(p).toUpperCase().matches("^shielded.*?".toUpperCase())){
                    schermata = true;
                } else if(removeSlash(p).toUpperCase().matches("^raid [0-9].*?".toUpperCase())){
                    incursione = true;
                    valoreIncursione = Integer.parseInt(String.valueOf(removeSlash(p).charAt(5)));
                } else if(removeSlash(p).toUpperCase().matches("^restore [0-9].*?".toUpperCase())){
                    recupero = true;
                    valoreRecupero = Integer.parseInt(String.valueOf(removeSlash(p).charAt(8)));
                } else if(removeSlash(p).toUpperCase().matches("^bounty.*?".toUpperCase())){
                    taglia = true;
                    valoreTaglia = removeSlash(p).substring(removeSlash(p).indexOf(" - ") + 1).replace("- ", "");
                } else if(removeSlash(p).toUpperCase().matches("^smuggle.*?".toUpperCase())){
                    contrabbando = true;
                    Matcher m = Pattern.compile("R([0-9])}").matcher(removeSlash(p));
                    m.find();
                    valoreContrabbando = m.group(1);
                    List<WebElement> aspettiContrabbando = p.findElements(By.tagName("img"));
                    for(WebElement aspetto:aspettiContrabbando){
                        switch (aspetto.getAttribute("alt").toLowerCase()){
                            case "vigilance aspect":
                                valoreContrabbando = valoreContrabbando.concat("B");
                            break;
                            case "command aspect":
                                valoreContrabbando = valoreContrabbando.concat("G");
                            break;
                            case "aggression aspect":
                                valoreContrabbando = valoreContrabbando.concat("R");
                            break;
                            case "cunning aspect":
                                valoreContrabbando = valoreContrabbando.concat("Y");
                            break;
                            case "heroism aspect":
                                valoreContrabbando = valoreContrabbando.concat("H");
                            break;
                            case "villainy aspect":
                                valoreContrabbando = valoreContrabbando.concat("D");
                            break;
                        }
                    }
                } else{
                    if(!removeSlash(p).toLowerCase().contains("epic")){
                        if(descrizioneEvento) valoreDescrizioneEvento = valoreDescrizioneEvento.concat(" * ");
                        descrizioneEvento = true;
                        valoreDescrizioneEvento = valoreDescrizioneEvento.concat(removeSlash(p));
                    }
                }/**/
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
        this.imboscata = jsonObject.get("imboscata").getAsBoolean();
        this.tenacia = jsonObject.get("tenacia").getAsBoolean();
        this.sopraffazione = jsonObject.get("sopraffazione").getAsBoolean();
        this.sabotatore = jsonObject.get("sabotatore").getAsBoolean();
        this.sentinella = jsonObject.get("sentinella").getAsBoolean();
        this.schermata = jsonObject.get("schermata").getAsBoolean();
        this.incursione = jsonObject.get("incursione").getAsBoolean();
        this.valoreIncursione = jsonObject.get("valoreIncursione").getAsInt();
        this.recupero = jsonObject.get("recupero").getAsBoolean();
        this.valoreRecupero = jsonObject.get("valoreRecupero").getAsInt();
        this.contrabbando = jsonObject.get("contrabbando").getAsBoolean();
        this.valoreContrabbando = jsonObject.get("valoreContrabbando").getAsString();
        this.quandoGiocata = jsonObject.get("quandoGiocata").getAsBoolean();
        this.valoreQuandoGiocata = jsonObject.get("valoreQuandoGiocata").getAsString();
        this.taglia = jsonObject.get("taglia").getAsBoolean();
        this.valoreTaglia = jsonObject.get("valoreTaglia").getAsString();
        this.quandoSconfitta = jsonObject.get("quandoSconfitta").getAsBoolean();
        this.valoreQuandoSconfitta = jsonObject.get("valoreQuandoSconfitta").getAsString();
        this.quandoAttacca = jsonObject.get("quandoAttacca").getAsBoolean();
        this.valoreQuandoAttacca = jsonObject.get("valoreQuandoAttacca").getAsString();
        this.descrizioneEvento = jsonObject.get("descrizioneEvento").getAsBoolean();
        this.valoreDescrizioneEvento = jsonObject.get("valoreDescrizioneEvento").getAsString();
        this.azione = jsonObject.get("azione").getAsBoolean();
        this.valoreAzione = jsonObject.get("valoreAzione").getAsString();
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
            System.out.println("inserisci l'espansione");
            String espansione = new java.util.Scanner(System.in).nextLine();
            System.out.println("inserisci il numero");
            String numero = String.format("%03d", new java.util.Scanner(System.in).nextInt());
            String link = "https://swudb.com/card/" + espansione + "/" + numero;
            System.out.println("web scraping di " + link);
            driver.get(link);
            Carta c = new Carta(driver);
            System.out.println(c);
            System.out.println(new Gson().toJson(c));
            System.out.println(c.insertSql());
        }catch (Exception e){
            System.out.println("c'è stato un errore, riprova");
        }finally {
            driver.quit();
        }
    }

    public String insertSql(){
        String info = "insert into carte values(\"";
        info = info.concat(espansione);
        info = info.concat("\"," + numero);
        info = info.concat(",\"" + nome);
        info = info.concat("\",'" + uscita);
        info = info.concat("'," + unica);
        info = info.concat(",\"" + titolo);
        info = info.concat("\",\"" + aspettoPrimario);
        info = info.concat("\",\"" + aspettoSecondario);
        info = info.concat("\",\"" + tipo);
        info = info.concat("\",\"" + join(tratti, " * "));
        info = info.concat("\"," + (imboscata ? 1 : 0));
        info = info.concat("," + (tenacia ? 1 : 0));
        info = info.concat("," + (sopraffazione ? 1 : 0));
        info = info.concat("," + (sabotatore ? 1 : 0));
        info = info.concat("," + (sentinella ? 1 : 0));
        info = info.concat("," + (schermata ? 1 : 0));
        info = info.concat("," + (incursione ? 1 : 0));
        info = info.concat("," + (recupero ? 1 : 0));
        info = info.concat("," + (contrabbando ? 1 : 0));
        info = info.concat("," + (quandoGiocata ? 1 : 0));
        info = info.concat("," + (taglia ? 1 : 0));
        info = info.concat("," + (quandoSconfitta ? 1 : 0));
        info = info.concat("," + (quandoAttacca ? 1 : 0));
        info = info.concat("," + (descrizioneEvento ? 1 : 0));
        info = info.concat(",\"" + rarita);
        info = info.concat("\"," + costo);
        info = info.concat("," + vita);
        info = info.concat("," + potenza);
        info = info.concat("," + prezzo);
        info = info.concat(",\"" + artista);
        info = info.concat("\"," + valoreIncursione);
        info = info.concat("," + valoreRecupero);
        info = info.concat(",\"" + valoreContrabbando);
        info = info.concat("\",\"" + valoreQuandoGiocata);
        info = info.concat("\",\"" + valoreTaglia);
        info = info.concat("\",\"" + valoreQuandoSconfitta);
        info = info.concat("\",\"" + valoreQuandoAttacca);
        info = info.concat("\",\"" + valoreDescrizioneEvento);
        info = info.concat("\",\"" + arena);
        info = info.concat("\"," + (azione ? 1 : 0));
        info = info.concat(",\"" + valoreAzione);
        info = info.concat("\");");
        return info;
    }

    public String removeSlash(WebElement element){
        return element.getText().replace("\"", "\\\"");
    }
}