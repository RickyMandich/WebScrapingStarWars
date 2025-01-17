package com.example.webscraping;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static String extractCid(String url) {
        String cid = "";
        Pattern pattern = Pattern.compile("cid=([0-9]+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            cid = matcher.group(1);
        }
        return cid;
    }

    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://starwarsunlimited.com/it/cards");
            ((JavascriptExecutor) driver).executeScript("document.body.style.zoom='25%'");
            int i = 0;
            do{
                ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,1000);");
                i++;
            }while(driver.findElement(By.cssSelector("body")).getText().toLowerCase().contains("carica"));
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));

            i=0;
            List<WebElement> cardImages = driver.findElements(By.cssSelector("img[alt='Fronte Della Carta']"));
            for (WebElement cardImage : cardImages) {
                System.out.println("ho iniziato il ciclo");
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", cardImage);
                if (i==0) ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,-1000);");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
                cardImage.click();
                String url = driver.getCurrentUrl();
                i++;
                System.out.println(i++ + ")\t" + extractCid(url));
                wait = new WebDriverWait(driver, Duration.ofSeconds(100));
                wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.flex.gap-4.items-start button:last-child"))).click();
                System.out.println("ho cliccato close");
            }
        }finally {
            driver.quit();
        }
    }
}
