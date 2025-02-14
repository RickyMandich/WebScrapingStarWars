package com.example.webscraping;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Esegue Chrome in modalità headless
        WebDriver driver = new ChromeDriver(/*options*/);
        try{
            driver.get("https://starwarsunlimited.com/it/cards");
            Thread.sleep(5000);
            List<WebElement> carte = driver.findElements(By.cssSelector("img[alt*='Fronte']"));
            System.out.println("size di carte:\t" + carte.size());
            for (WebElement carta : carte){
                carta.click();
                System.out.println("ho cliccato");
                System.out.println(Scan.extractCid(driver.getCurrentUrl()));
                driver.findElement(By.cssSelector("div > div.mb-6.ml\\:mb-8 > div > div.flex.gap-4.items-start > button:nth-child(2) > svg")).click();
                System.out.println("ho cliccato close");
                Thread.sleep(500);
            }
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            driver.quit();
        }
    }
}
