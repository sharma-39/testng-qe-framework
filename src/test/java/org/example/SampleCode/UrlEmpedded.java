package org.example.SampleCode;

import org.example.LoginUtil.LoginAndLocationTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class UrlEmpedded extends LoginAndLocationTest {


    @Test(priority = 3, dependsOnMethods = {"testLogin"})
    public void OpenPharmacyClickPrint()
    {
        if(isLoginSuccessful) {
            menuPanelClick("Pharmacy", false, "", "");

            List<String> containTest = new ArrayList<>();
            containTest.add("Dr.David T");
            containTest.add("Dr.AMSICA B");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));


            for(int i=0;i<containTest.size();i++) {
                // Wait for the row containing "SharmaA"
                WebElement row = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//tr[td/span[contains(text(),'" + containTest.get(i) + "')]]")
                ));

                WebElement billButton = row.findElement(By.xpath(".//button[@title='View']"));

                wait.until(ExpectedConditions.elementToBeClickable(billButton)).click();

                threadTimer(5000);
                WebElement ageGenderElement = driver.findElement(By.xpath("//span[contains(text(), 'Age | Gender')]/following-sibling::span[4]"));

                // Highlight the element using JavaScript
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].style.border='3px solid red';", ageGenderElement);

                // Optional: Scroll the element into view
                js.executeScript("arguments[0].scrollIntoView(true);", ageGenderElement);

                System.out.println("check :--" + ageGenderElement.getText());
                String isConfig = null;
                if (ageGenderElement.getText().contains("Y")) {
                    System.out.println("Age In Month | Year");
                    isAgeInMonth=true;
                } else {
                    System.out.println("Age in Year");
                    isAgeInMonth=true;
                }

                WebElement closeButton = driver.findElement(By.xpath("//button[contains(text(), 'Close')]"));
                closeButton.click();

                // Wait for a few seconds to observe the result
               threadTimer(4000);
            }

        }
    }
}
