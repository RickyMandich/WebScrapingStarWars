package com.example.webscraping;

import com.google.gson.JsonSyntaxException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.*;

import com.google.gson.Gson;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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

    public static boolean contains(Carta[] collezione, String espansione, String numero){
        for(Carta c:collezione){
            if(c.espansione.equals(espansione) && c.numero == Integer.parseInt(numero)) return true;
        }
        return false;
    }

    public static String[] add(String[] array, String line, Carta[] collezione){
        if(contains(collezione, line.split("/")[4], line.split("/")[5])) return array;
        return add(array, line);
    }

    public static Carta[] add(Carta[] array, Carta line){
        Carta[] newArray = new Carta[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = line;
        return newArray;
    }

    public static int readIntWithTimeout(Duration timeout, int defaultValue) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(() -> {
            try {
                Scanner scanner = new Scanner(System.in);
                return scanner.nextInt();
            } catch (Exception e) {
                return defaultValue;
            }
        });

        try {
            return future.get(timeout.getSeconds(), TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        } finally {
            executor.shutdownNow();
            System.out.println("chiusura scanner");
        }
    }

    public static void main(String[] args){
        if(args.length == 0) alert("programma lanciato");
        boolean finito = false;
        WebDriver driver = new WebDriverWithoutImage();
        tempo  = System.nanoTime() / 1000000;
        Carta[] collezione = getJsonCollezione();
        String[] espansioni = new String[0];
        String line;
        long secondi = 0;
        if(args.length == 1){
            secondi = Long.parseLong(args[0]);
        }else{
            args = new String[1];
        }
        String datiEsecuzione = "\t" + new SimpleDateFormat("yyyy MM dd HH:mm:ss").format(new Date());
        int numeroThread = 0;
        try {
            Exception exception = null;
            do {
                try {
                    driver.get("https://swudb.com/sets/");
                    //css per ottenere il link fullset "a[href*=fullSet]"
                } catch (Exception ex) {
                    exception = ex;
                }
            } while (exception instanceof TimeoutException);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> completeSetRow = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("div.col-span-1.md\\:col-span-3")));
            for (WebElement csr : completeSetRow) {
                String set = csr.findElement(By.tagName("a")).getAttribute("href").split("/")[4];
                String uscita = csr.findElement(By.tagName("p")).getText().replace("Release Date: ", "");
                Carta.uscitaEspansioni.put(set, elaboraData(uscita));
            }
            driver.quit();
            boolean inCorso = true;
            for (String set : Carta.uscitaEspansioni.keySet()) {
                espansioni = add(espansioni, set);
            }
            System.out.println("-----------------------\narray espansioni = ");
            espansioni = orderAndCompact(espansioni);
            for (String s : espansioni) {
                System.out.println(s);
            }
            driver = new WebDriverWithoutImage();
            String[] carte = new String[0];
            for (String set : espansioni) {
                System.out.println("ora scansiono " + set);
                Toolkit.getDefaultToolkit().beep();
                try {
                    driver.get("https://swudb.com/sets/" + set + "/fullSet");
                } catch (Exception e) {
                    espansioni = add(espansioni, set);
                }
                System.out.println("ora tocca al wait");
                wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.flex.justify-center a")));
                System.out.println("trovate " + elements.size() + " carte");
                for (WebElement e : elements) {
                    String url = e.getAttribute("href");
                    System.out.println("analizzo la carta " + url);
                    String regex = "[A-Z]{2,6}([0-9]{2})?\\/[0-9]{1,3}";
                    System.out.println(regex);
                    Pattern pat = Pattern.compile(regex);
                    Matcher mat = pat.matcher(notNull(url));
                    if(mat.find()) {
                        url = mat.group();
                        System.out.println("trovato " + url);
                        url = "https://swudb.com/card/" + url;
                        carte = add(carte, url, collezione);
                    }else{
                        System.out.println("non trovato");
                    }
                }
                System.out.println("analisi carte della pagina finita");
            }
            for (String c : carte) {
                System.out.println(c);
            }
            if (carte.length == 0) finito = true;
            driver.quit();
            if (!finito) {
                System.out.println("quanti thread vuoi lanciare?\t\t(se le carte sono più dei thread, verranno lanciati tanti thread quanti sono le carte)");
                numeroThread = Math.min(readIntWithTimeout(Duration.ofSeconds(60), 4), carte.length);
                tempo = System.nanoTime() / 1000000;
                carte = orderAndCompact(carte);
                Elenchi elenco = new Elenchi(carte, numeroThread, collezione);
                elenco.carte.ready();
                Thread[] processi = new Thread[numeroThread];
                for (int i = 0; i < processi.length; i++) {
                    processi[i] = new Thread(elenco, new WebDriverWithoutImage());
                    processi[i].start();
                }
                boolean fine = false;
                while (!fine) {
                    fine = true;
                    for (Thread t : processi) {
                        if (t.isAlive()) {
                            fine = false;
                            break;
                        }
                    }
                }
                collezione = elenco.getResult();
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            System.out.println("sono nel finally");
            driver.quit();
            long tempoTrascorso = System.nanoTime() / 1000000;
            tempoTrascorso = tempoTrascorso - tempo;
            secondi += tempoTrascorso / 1000;
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
                datiEsecuzione += "\tthread: " + numeroThread + "\ttempo trascorso:\t" + formattaSecondi(secondi) + "\tset:\t" + join(espansioni, " - ");
                writer.write(fileTempo + datiEsecuzione + "\n");
            }catch (IOException ignore){}
            Toolkit.getDefaultToolkit().beep();
            String json = json(collezione);
            try(FileWriter writer = new FileWriter("collezione.json")){
                writer.write(json);
                /*
                * todo:
                *  - sistemare l'upload*/
                //uploadWithFtp("collezione.json");
            }catch (IOException e){
                System.out.println("non ho trovato \"collezione.json\", inserisci tu il nome del file");
                scrivi(json);
            }
        }
        args[0] = secondi+"";
        if(!finito) Scan.main(args);
        else{
            alert("webscraping finito" + datiEsecuzione);
        }
    }

    public static String notNull(String s){
        return s == null ? "" : s;
    }

    private static Carta[] getJsonCollezione(){
        try{
            BufferedReader reader = new BufferedReader(new FileReader("collezione.json"));
            String line;
            if((line=reader.readLine()) != null){
                return new Gson().fromJson(line, Carta[].class);
            }else{
                throw new IOException("il file 'collezione.json' è vuoto");
            }
        }catch (IOException |JsonSyntaxException e){
            return new Carta[0];
        }
    }

    public static String join(String[] array, String concatenatore){
        if(array.length == 0) return "";
        else if (array.length == 1) return array[0];
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

    public static String elaboraData(String uscita){
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

    public static void alert(String message){
        final String BOT_TOKEN = "7717265706:AAH5chf4Ae3vsFSt7158K-RFWdh9BudnnQc";
        final String CHAT_ID = "5533337157";
        try {
            String urlEncodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
            String url = String.format(
                "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                BOT_TOKEN, CHAT_ID, urlEncodedMessage
            );

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(url);
                client.execute(request);
            }

            System.out.println("Telegram notification sent successfully!");
        } catch (IOException e) {
            System.err.println("Error sending Telegram notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
}