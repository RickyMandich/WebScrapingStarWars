package com.example.webscraping;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import com.google.gson.Gson;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scan {
    static long tempo;

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
        WebDriver driver = new ChromeDriver();
        tempo  = System.nanoTime() / 1000000;
        Carta[] collezione = new Carta[0];
        String[] espansioni = new String[0];
        String line;
        int numeroThread = 0;
        try {
            Exception exception = null;
            do{
                try{
                    driver.get("https://swudb.com/sets/");
                }catch (Exception ex){
                    exception = ex;
                }
            }while(exception instanceof TimeoutException);
            List<WebElement> tabelle = driver.findElements(By.tagName("tbody"));
            List<WebElement> row = new ArrayList<>();
            for(WebElement table:tabelle){
                row.addAll(table.findElements(By.tagName("tr")));
            }
            for(WebElement r:row){
                String set = r.findElement(By.cssSelector(".ms-3.small.text-muted")).getText();
                String uscita = r.findElement(By.cssSelector("td:last-child span")).getText();
                Carta.uscitaEspansioni.put(set, elaboraData(uscita));
            }
            List<WebElement> completeSetRow = driver.findElements(By.cssSelector(".col-12.col-md-8.mt-2.mt-md-4"));
            for(WebElement csr : completeSetRow){
                String href = csr.findElement(By.cssSelector(".core-set-title>a")).getAttribute("href");
                String set = href.split("/")[href.split("/").length - 1];
                String uscita = csr.findElement(By.cssSelector("span[title=\"Release Date\"]>em")).getText();
                if(uscita.split(" ")[0].startsWith("Q")) uscita = uscita.split(" ")[1];
                Carta.uscitaEspansioni.put(set, elaboraData(uscita));
            }
            System.out.println("espansioni e date");
            for(String set:Carta.uscitaEspansioni.keySet()){
                System.out.println(set + "\t" + Carta.uscitaEspansioni.get(set));
            }
            System.out.println("inserisci l'espansione da scansionare, se hai finito lascia vuoto");
            boolean inCorso = true;
            driver.quit();
            while(inCorso && !(line = new Scanner(System.in).nextLine()).isEmpty()){
                inCorso = false;
                if(line.equalsIgnoreCase("all")){
                    for(String set:Carta.uscitaEspansioni.keySet()){
                        espansioni = add(espansioni, set);
                    }
                    System.out.println("-----------------------\narray espansioni = ");
                    espansioni = orderAndCompact(espansioni);
                    for(String s: espansioni){
                        System.out.println(s);
                    }
                }else if(line.equalsIgnoreCase("singolo 1")){
                    espansioni = add(espansioni, "sorop");
                }else if(line.equalsIgnoreCase("singolo 2")){
                    espansioni = add(espansioni, "shdop");
                }else if(line.equalsIgnoreCase("doppio")){
                    espansioni = add(add(espansioni, "shdop"), "sorop");
                }else {
                    inCorso = true;
                    espansioni = add(espansioni, line);
                    System.out.println("inserisci l'espansione da scansionare, se hai finito lascia vuoto");
                }
            }
            driver = new ChromeDriver();
            String[] carte = new String[0];
            for(String set:espansioni){
                System.out.println("ora scansiono " + set);
                Toolkit.getDefaultToolkit().beep();
                try{
                    driver.get("https://swudb.com/sets/" + set);
                } catch (Exception e) {
                    espansioni = add(espansioni, set);
                }
                List<WebElement> elements = driver.findElements(By.className("col-6"));
                //String[] carte = new String[5];
                for (WebElement e : elements) {
                    String href = e.findElement(By.tagName("a")).getAttribute("href");
                    if(href.split("/")[href.split("/").length-2].charAt(0) != 'T'){
                        carte = add(carte, href);
                    }
                }
                System.out.println(elements.size());
            }
            for (String e : carte) {
                System.out.println(e);
            }
            driver.quit();
            numeroThread = 2;                                                //new Scanner(System.in).nextInt();
            tempo  = System.nanoTime() / 1000000;
            carte = orderAndCompact(carte);
            Elenchi elenco = new Elenchi(carte, numeroThread);
            elenco.carte.ready();
            Thread[] processi = new Thread[numeroThread];
            for (int i = 0; i < processi.length; i++) {
                processi[i] = new Thread(elenco, new ChromeDriver());
                processi[i].start();
            }
            boolean fine = false;
            while(!fine){
                fine = true;
                for(Thread t : processi){
                    if (t.isAlive()) {
                        fine = false;
                        break;
                    }
                }
            }
            System.out.println("-----------------fine thread-----------------");
            collezione = elenco.getResult();
        } finally {
            System.out.println("-------------------finally--------------------------");
            long tempoTrascorso = System.nanoTime() / 1000000;
            tempoTrascorso = tempoTrascorso - tempo;
            long secondi = tempoTrascorso / 1000;
            System.out.println("tempo trascorso:\t" + formattaSecondi(secondi));
            BufferedReader reader;
            String fileTempo = "";
            try {
                reader = new BufferedReader(new FileReader("tempo.txt"));
                while( (line = reader.readLine()) != null){
                    fileTempo = fileTempo.concat(line + "\n");
                }
            } catch (IOException ignore) {}
            try(FileWriter writer = new FileWriter("tempo.txt")){
                writer.write(fileTempo + new SimpleDateFormat("yyyy MM dd HH:mm:ss").format(new Date()) + "\tthread: " + numeroThread + "\t" + formattaSecondi(secondi) + "\tset:\t" + join(espansioni, " - ") + "\n");
            }catch (IOException ignore){}
            Toolkit.getDefaultToolkit().beep();
            String json = json(collezione);
            try(FileWriter writer = new FileWriter("collezione.json")){
                writer.write(json);
            }catch (IOException e){
                System.out.println("non ho trovato \"collezione.json\", inserisci tu il nome del file");
                scrivi(json);
            }
        }
        //Upload.main(new String[0]);
    }

    public static String join(String[] array, String concatenatore){
        String ret = array[0];
        for(int i=1;i<array.length;i++){
            ret = ret.concat(concatenatore);
            ret = ret.concat(array[i]);
        }
        return ret;
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

    private static String elaboraData(String uscita){
        Matcher release = Pattern.compile("([A-Z][a-z]*)? ?([0-9]{1,2})?,? ?([0-9]{4})").matcher(uscita);
        String data = "";
        if(release.find()){
            String giorno = release.group(2) != null ? release.group(2) : "";
            String mese = release.group(1) != null ? release.group(1) : "";
            String anno = release.group(3) != null ? release.group(3) : "";
            switch (mese) {
                case "January" -> mese = "01";
                case "February" -> mese = "02";
                case "March" -> mese = "03";
                case "April" -> mese = "04";
                case "May" -> mese = "05";
                case "June" -> mese = "06";
                case "July" -> mese = "07";
                case "August" -> mese = "08";
                case "September" -> mese = "09";
                case "October" -> mese = "10";
                case "November" -> mese = "11";
                case "December" -> mese = "12";
            }
            data = anno + " " + mese + " " + giorno;
        }
        return (data.isEmpty() ? uscita : data);
    }

    private static String[] orderAndCompact(String[] stringa){
        mergeSort(stringa);
        boolean rimosso;
        do{
            rimosso = false;
            for(int i = 0; i < stringa.length - 1; i++){
                if(stringa[i].equals(stringa[i+1])){
                    rimosso = true;
                    stringa = rimuovi(stringa, i);
                }
            }
        }while(rimosso);
        return stringa;
    }

    public static String[] rimuovi(String[] oldStringa, int h){
        String[] newStringa = new String[oldStringa.length - 1];
        for(int i=0, j=0;j<oldStringa.length; ){
            if(j==h){
                j++;
            }else{
                newStringa[i++] = oldStringa[j++];
            }
        }
        return newStringa;
    }

    public static void mergeSort(String[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }

        int mid = arr.length / 2;
        String[] left = new String[mid];
        String[] right = new String[arr.length - mid];

        // Popola gli array left e right
        System.arraycopy(arr, 0, left, 0, mid);
        if(arr.length - mid >= 0) System.arraycopy(arr, mid, right, 0, arr.length - mid);

        mergeSort(left);
        mergeSort(right);

        merge(arr, left, right);
    }

    private static void merge(String[] arr, String[] left, String[] right) {
        int i = 0, j = 0, k = 0;

        while (i < left.length && j < right.length) {
            if (left[i].compareTo(right[j]) <= 0) {
                arr[k++] = left[i++];
            } else {
                arr[k++] = right[j++];
            }
        }

        while (i < left.length) {
            arr[k++] = left[i++];
        }

        while (j < right.length) {
            arr[k++] = right[j++];
        }
    }

    public static void scrivi(String json){
        try(FileWriter writer = new FileWriter(getString("inserisci il percorso del file in cui salvare il json"))){
            writer.write(json);
        }catch (IOException e){
            System.out.println("errore nella scrittura del file");
            scrivi(json);
        }
    }

    public static String json(Carta[] collezione) {
        return new Gson().toJson(collezione);
    }

    private static String getString(){
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
}