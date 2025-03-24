package org.test_automation.FlowHelper;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.test_automation.BaseTest;
import org.test_automation.TapPharmacy.SalesReturn;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class ReturnHelper {
    public void createSalesReturnBill(BaseTest baseTest, WebDriver driver, WebDriverWait wait, String parentPanel, String childPanel, String billNumber) {


        System.out.println("pharmacy bills----");

        baseTest.menuPanelClick(parentPanel, true, childPanel, "");

        baseTest.clickButtonElement(By.xpath("//button[contains(text(),'Add Return')]"));


        baseTest.threadTimer(1500);

        selectBillNumber(wait, driver, billNumber);


        baseTest.threadTimer(2000);

        List<WebElement> drugNameElements = driver.findElements(
                By.cssSelector("tbody > tr:not([style*='border-bottom']) > td.tbody1-al.ng-star-inserted[title='']")
        );

        List<String> drugNames = drugNameElements.stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());

        System.out.println("Drug Names: " + drugNames);

        for (int i = 0; i < drugNames.size(); i++) {

            WebElement drugRow = driver.findElement(
                    By.xpath("//tr[.//td[contains(@class,'ng-star-inserted') and normalize-space()='" + drugNames.get(i) + "']]")
            );

// 2. Locate the checkbox inside that row
            WebElement checkbox = drugRow.findElement(
                    By.xpath(".//input[@type='checkbox']")
            );

// 3. Check the checkbox (if not already checked)
            if (!checkbox.isSelected()) {
                checkbox.click();
                System.out.println("Checkbox for PANTAGOLD 40MG is now checked.");
            } else {
                System.out.println("Checkbox was already checked.");
            }

            WebElement returnQtyInput = drugRow.findElement(
                    By.xpath(".//input[@title='Return Qty']")
            );

// 3. Remove the 'readonly' attribute (if present) using JavaScript
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].removeAttribute('readonly');", returnQtyInput
            );

// 4. Clear any existing value and enter the desired quantity (e.g., "5")
            returnQtyInput.clear();
            returnQtyInput.sendKeys("5");


        }
        baseTest.clickButtonElement(By.xpath("//button[contains(text(),'Return')]"));


        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }


        baseTest.threadTimer(2000); // Wait for print dialog

        // Press ESC to cancel print
        robot.keyPress(KeyEvent.VK_ESCAPE);
        robot.keyRelease(KeyEvent.VK_ESCAPE);
    }


    // Helper method to select a patient by code
    private void selectBillNumber(WebDriverWait wait, WebDriver driver, String billNumber) {
        WebElement dropdown1 = driver.findElement(By.xpath("//select[contains(@class, 'form-control')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='billNumber';", dropdown1);
        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change'));", dropdown1);

        WebElement number = wait.until(ExpectedConditions.elementToBeClickable(By.name("patientCode")));
        number.click();
        number.sendKeys(Keys.BACK_SPACE);
        number.sendKeys(billNumber);

        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//mat-option")));
        boolean found = false;
        for (WebElement option : options) {
            if (option.getText().contains(billNumber)) {
                option.click();
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("billNumber not found in dropdown.");
        }
    }
}
