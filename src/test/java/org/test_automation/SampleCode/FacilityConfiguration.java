package org.test_automation.SampleCode;

import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.annotations.Test;

import java.util.List;

public class FacilityConfiguration extends LoginAndLocationTest {



    @Test(priority = 3, dependsOnMethods = {"testLogin"})
    public void FacilityConfigurateAgeInMonthEnable()
    {
        if(isLoginSuccessful) {
            menuPanelClick("Facility Configurations", false, "", "");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            WebElement ageFormatElement = driver.findElement(By.xpath("//h2[contains(text(), 'Age Format In Bill')]"));

// Scroll the element into view
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", ageFormatElement);

            // Locate all radio buttons
            List<WebElement> radioButtons = driver.findElements(By.xpath("//label[contains(@class, 'fancy-radio')]/input[@type='radio']"));

// Iterate over radio buttons and select the desired one
            for (WebElement radioButton : radioButtons) {
                // Find the label text next to the radio button
                WebElement label = radioButton.findElement(By.xpath("./following-sibling::span"));
                String labelText = label.getText().trim();
                System.out.println("Found Radio Button: " + labelText);

                // Choose based on label text
                if (labelText.contains("Age In Years And Months")) { // Change text if needed

                    // Click if not already selected
                    if (!radioButton.isSelected()) {
                        radioButton.click();
                        System.out.println("Selected Radio Button: " + labelText);
                    } else {
                        System.out.println("Radio Button already selected.");
                    }// Exit after selecting desired radio
                } else if (labelText.contains("Age In Years")) {
                    try {
                        Thread.sleep(500); // Wait after scrolling
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    // Click if not already selected
                    if (!radioButton.isSelected()) {
                        wait.until(ExpectedConditions.elementToBeClickable(radioButton));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioButton);
                        System.out.println("Selected Radio Button: " + labelText);
                    } else {
                        System.out.println("Radio Button already selected.");
                    }
                }
            }

        }
    }

//    @Test(priority = 4, dependsOnMethods = {"testLogin"})
//    public void FacilityConfigurateAgeInYearEnable()
//    {
//        if(isLoginSuccessful) {
//        }
//    }


}
