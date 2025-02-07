package com.example.webscraping;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        Exception exception = null;
        do{
            try{
                driver.get("https://swudb.com/sets/");
                //css per ottenere il link fullset "a[href*=fullSet]"
            }catch (Exception ex){
                exception = ex;
            }
        }while(exception instanceof TimeoutException);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> completeSetRow = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("div.col-span-1.md\\:col-span-3")));
        System.out.println("size:\t" + completeSetRow.size());
        for(WebElement csr : completeSetRow){
            String set = csr.findElement(By.tagName("a")).getAttribute("href").split("/")[4];
            String uscita = csr.findElement(By.tagName("p")).getText().replace("Release Date: ", "");
            Carta.uscitaEspansioni.put(set, Scan.elaboraData(uscita));
        }
        for(String set:Carta.uscitaEspansioni.keySet()){
            System.out.println(set + ":\t" + Carta.uscitaEspansioni.get(set));
        }
        driver.quit();
    }
}