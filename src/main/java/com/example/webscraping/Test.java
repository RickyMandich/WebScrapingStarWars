package com.example.webscraping;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Test {
    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://starwarsunlimited.com/it/cards");
            int i = 0;
            do{
                System.out.println("attento il caricamento della pagina...");
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,10000);");
                i++;
            }while(driver.findElement(By.cssSelector("body")).getText().toLowerCase().contains("carica"));
        }finally {
            driver.quit();
        }
    }
}
