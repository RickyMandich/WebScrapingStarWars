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

    public String removeSlash(WebElement element){
        return element.getText().replace("\"", "\\\"");
    }
}