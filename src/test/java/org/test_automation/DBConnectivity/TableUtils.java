package org.test_automation.DBConnectivity;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TableUtils {
    
    private final DatePickerUtil datePickerUtil=new DatePickerUtil();


    public void filterSearchClick(WebDriverWait wait, WebDriver driver) {

        // Wait until the button is clickable
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@title='Search']")
        ));

        // Scroll into view
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", searchButton);
        try {
            Thread.sleep(500); // Small wait for smooth UI
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

// Click the button using JavaScript to avoid interception issues
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", searchButton);
    }


    public void filterSearchElement(String searchValue, String searchFiler, String type,WebDriverWait wait, WebDriver driver) {


        if (type.equals("Text")) {

// Wait for the input field inside the "Patient Code" column
            WebElement fieldsElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//th[@title='" + searchFiler + "']//input[contains(@class, 'form-control')]")
            ));

// Scroll into view (if needed)
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", fieldsElement);
            try {
                Thread.sleep(500); // Small delay for UI adjustment
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

// Clear any existing text and enter new value
            fieldsElement.clear();
            fieldsElement.sendKeys(searchValue); // Replace with actual Patient Code

            fieldsElement.sendKeys(Keys.ENTER);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            WebElement expiryDateField = driver.findElement(By.id(searchFiler));
            expiryDateField.click();
            datePickerUtil.selectFilterSearchDatePicker(searchValue,driver,wait);
        }
    }

}
