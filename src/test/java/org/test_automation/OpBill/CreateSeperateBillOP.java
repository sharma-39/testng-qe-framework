package org.test_automation.OpBill;

import org.test_automation.DBConnectivity.MenuUtils;
import org.test_automation.DBConnectivity.XPathUtil;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class CreateSeperateBillOP extends LoginAndLocationTest {

    private final MenuUtils menuUtils=new MenuUtils();
    private final XPathUtil xPathUtil=new XPathUtil();
    @Test(priority = 3, description = "Test to create a new bill for a patient")
    public void createBill() {
        // Check if login was successful
        if (isLoginSuccessful) {

            // Navigate to the OP menu
            menuUtils.menuPanelClick("OP", false, "", "",driver,wait);
            By opBillsLocator = By.xpath("//li[contains(@class, 'breadcrumb-item') and contains(text(), 'OP Bills')]");

            while (driver.findElements(opBillsLocator).isEmpty()) {
                System.out.println("Waiting for OP Bills element...");
                try {
                    Thread.sleep(500); // Small delay to prevent CPU overload
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("OP Bills element found!");
            System.out.println("Create Seprate bill configure");
            wait = new WebDriverWait(driver, Duration.ofSeconds(55));

            // Wait for the page to load and click the "Create New Bill" button
            threadTimer(2000);
            clickCreateNewBillButton(wait);

            // Verify if the Patient Search label is visible
            if (isPatientSearchLabelVisible(wait)) {
                // Select patient by code
                selectPatientByCode(wait, "SWI-2-910-P-2025");

                // Select the doctor from the dropdown
                selectDoctor(wait, "Dr.Vishnu V");

                // Add billing details and enter amounts
                addBillingDetails("Partially Paid");
                amountTabClick();
                enterAmounts("Partially Paid", 0);

                // Submit billing and close the print screen
                submitBilling();
                xPathUtil.closePrintScreen();

                // Get pagination details and search for the patient code and status
                int totalPages = getPaginationDetails();
                if (findRowPatientCodeAndStatus("SWI-2-910-P-2025", "View Bill", "Partially Paid".toUpperCase(), totalPages)) {
                    threadTimer(3000);
                    submitBilling();
                    xPathUtil.closePrintScreen();
                }
            } else {
                System.out.println("Patient Search label not found.");
            }
        } else {
            System.out.println("Login was not successful. Skipping test.");
        }
    }

    // Helper method to click the "Create New Bill" button
    private void clickCreateNewBillButton(WebDriverWait wait) {

        // Wait for button to be clickable
        WebElement createNewBillButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("opBIllCrBtn")));

// Scroll to button
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", createNewBillButton);

// Small delay before clicking
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

// Click using JavaScript to avoid interception
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", createNewBillButton);


    }

    // Helper method to check if the Patient Search label is visible
    private boolean isPatientSearchLabelVisible(WebDriverWait wait) {
        try {
            WebElement patientSearchLabel = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//label[contains(text(), 'Patient Search')]")
            ));
            return patientSearchLabel.getText().contains("Patient Search");
        } catch (Exception e) {
            return false;
        }
    }

    // Helper method to select a patient by code
    private void selectPatientByCode(WebDriverWait wait, String patientCode) {
        WebElement dropdown1 = driver.findElement(By.xpath("//select[contains(@class, 'form-control')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='byCode';", dropdown1);
        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change'));", dropdown1);

        WebElement patientCodeInput = wait.until(ExpectedConditions.elementToBeClickable(By.name("patientCode")));
        patientCodeInput.click();
        patientCodeInput.sendKeys(Keys.BACK_SPACE);
        patientCodeInput.sendKeys(patientCode);

        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//mat-option")));
        boolean found = false;
        for (WebElement option : options) {
            if (option.getText().contains(patientCode)) {
                option.click();
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Patient name not found in dropdown.");
        }
    }

    // Helper method to select a doctor from the dropdown
    private void selectDoctor(WebDriverWait wait, String doctorName) {
        WebElement doctorDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.className("form-control")));
        doctorDropdown.click();

        WebElement doctorOption = driver.findElement(By.xpath("//option[contains(text(), '" + doctorName + "')]"));
        doctorOption.click();
    }

    // Helper method to add billing details
    private void addBillingDetails(String statusText) {
        List<String> optionTexts = Arrays.asList("Consultation Charge", "Doctor Fees 5");

        // Click "Add New" to enable the dropdown if needed
        WebElement addNewButton = driver.findElement(By.xpath("//div[contains(@class, 'addIcon-button')]/span[text()='Add New']"));
        addNewButton.click();
        threadTimer(3000); // Wait for UI update

        for (String optionText : optionTexts) {
            WebElement dropdown = driver.findElement(By.xpath("//mat-select//span[contains(text(), 'Select')]"));
            if (!dropdown.isEnabled()) {
                System.out.println("Dropdown is disabled, clicking 'Add New' to enable it...");
                addNewButton.click(); // Click Add New again to enable
                threadTimer(2000); // Wait for UI update
            }

            dropdown.click();
            threadTimer(2000); // Wait for dropdown options to appear
            selectDropdownOption(optionText);
            addNewButton.click();
            threadTimer(3000); // Wait for UI update before selecting the next option
        }
    }

    // Helper method to select an option from the dropdown
    private void selectDropdownOption(String optionText) {
        List<WebElement> options = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//mat-option//span")));

        for (WebElement option : options) {
            if (option.getText().trim().equals(optionText)) {
                scrollToElement(option);
                threadTimer(500);
                option.click();
                threadTimer(1000);
                break;
            }
        }
    }

    // Helper method to click the amount tab
    private void amountTabClick() {
        WebElement table = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//table[contains(@class, 'hm-p table-disable-hover')]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", table);
        table.click();
    }

    // Helper method to enter amounts in the billing form
    private void enterAmounts(String statusText, int discount) {
        fillDiscountAmount("Overall Discount Percentage", discount);

        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//tbody[@id='tbodyIn2']/tr")));
        WebElement finalAmountElement = driver.findElement(By.xpath("//tr[th[contains(text(), 'Final Amount')]]/td"));
        String finalAmount = finalAmountElement.getText().replaceAll("[^0-9]", "");

        for (int i = 0; i < rows.size(); i++) {
            try {
                WebElement row = rows.get(i);
                WebElement amountInput = row.findElement(By.xpath(".//td[contains(@class, 'text-right')]//input[@type='number']"));
                if (amountInput.isDisplayed()) {
                    scrollToElement(amountInput);
                    if (statusText.equals("Partially Paid")) {
                        amountInput.clear();
                        amountInput.sendKeys(String.valueOf(Math.round(Integer.parseInt(finalAmount) / 2)));
                    } else {
                        amountInput.clear();
                        amountInput.sendKeys(finalAmount);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error in row " + (i + 1) + ": " + e.getMessage());
            }
        }
    }

    // Helper method to fill the discount amount
    private void fillDiscountAmount(String label, int discount) {
        WebElement discountAmountField = driver.findElement(By.xpath("//tr[th[contains(text(),'" + label + "')]]//input"));
        discountAmountField.clear();
        discountAmountField.sendKeys(String.valueOf(discount));
    }

    // Helper method to submit billing
    private void submitBilling() {
        clickElement(By.xpath("//label[contains(text(), 'Remarks')]"));
        clickElement(By.xpath("//button[contains(text(), 'Pay Bill')]"));
        threadTimer(3000);
        clickElement(By.xpath("//div[contains(@class, 'sa-confirm-button-container')]//button[contains(text(), 'Yes')]"));
        threadTimer(5000);
        xPathUtil.closePrintScreen();
    }



    // Helper method to click an element
    private void clickElement(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        scrollToElement(element);
        element.click();
    }

    // Helper method to scroll to an element
    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    // Helper method to get pagination details
    private int getPaginationDetails() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String pageText = (String) js.executeScript("return document.querySelector('li.small-screen')?.textContent.trim();");
        if (pageText != null && !pageText.isEmpty()) {
            String[] pageParts = pageText.split("/");
            return Integer.parseInt(pageParts[1].trim());
        }
        return 1;
    }

    // Helper method to find a row by patient code and status
    public Boolean findRowPatientCodeAndStatus(String patientCode, String title, String status, int totalPages) {
        boolean isFound = false;
        int currentPage = 1;

        while (!isFound && currentPage <= totalPages) {
            List<WebElement> rows = driver.findElements(By.xpath("//table//tr"));
            for (int i = 0; i < rows.size(); i++) {
                String rowText = rows.get(i).getText();
                if (rowText.contains(patientCode) && rowText.contains(status)) {
                    System.out.println("Row Found at Index: " + (i + 1));

                    // Highlight the row
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("arguments[0].style.backgroundColor = 'yellow'", rows.get(i));

                    System.out.println("Row Highlighted!");
                    isFound = true;
                    WebElement viewButton = rows.get(i).findElement(By.xpath(".//button[@title='" + title + "']"));
                    scrollToElement(viewButton);
                    viewButton.click();
                    break;
                }
            }

            if (!isFound && currentPage < totalPages) {
                try {
                    currentPage++; // Increment before clicking
                    WebElement pageNo = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//ul[contains(@class, 'ngx-pagination')]//li/a/span[text()='" + currentPage + "']")
                    ));

                    // Scroll into view and click the next page button
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", pageNo);
                    Thread.sleep(500); // Small delay for UI adjustment
                    pageNo.click();
                    Thread.sleep(3000); // Allow time for the new page to load
                } catch (Exception e) {
                    System.out.println("Pagination button not found or not clickable.");
                    break;
                }
            }
        }

        if (!isFound) {
            System.out.println("Patient Code not found in any pages.");
            return false;
        } else {
            return true;
        }
    }
}