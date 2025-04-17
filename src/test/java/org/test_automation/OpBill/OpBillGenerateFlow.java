package org.test_automation.OpBill;

import org.test_automation.DBConnectivity.MenuUtils;
import org.test_automation.DBConnectivity.XPathUtil;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.test_automation.FlowHelper.PatientFlowHelper;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class OpBillGenerateFlow extends LoginAndLocationTest {

    private static final long THREAD_SECONDS = 3000; // Constant for thread sleep time
    private static int patientIncrement = 0; // Counter for patient increment
    private final XPathUtil xPathUtil = new XPathUtil();
    private final PatientFlowHelper patientFlowHelper; // Helper class for patient flow
    private String patientCode; // Stores the patient code
    private boolean isAppointmentCreated = false; // Flag to check if appointment is created
    private boolean isAppointmentCheckedIn = false; // Flag to check if appointment is checked in

    private  final MenuUtils menuUtils=new MenuUtils();
    // Constructor to initialize the PatientFlowHelper
    public OpBillGenerateFlow() {
        this.patientFlowHelper = new PatientFlowHelper();
    }

    @Test(priority = 3, description = "Test to generate OP bill flow")
    public void opBillFlow() {
        // Check if login was successful
        if (!isLoginSuccessful) {
            Assert.fail("Login failed");
        }

        // Get patient data from JSON
        JSONObject patient = tempPatientData.getJSONObject(patientIncrement);
        for (int i = 50; i <= 50; i++) {
            patientIncrement = i;
            // Register the patient and get the patient code
            patientCode = patientFlowHelper.patientRegisterTest(this, patient, driver, wait, "Patient Registration");
            System.out.println("OP Bill flow started for Patient Code: " + patientCode);

            // Navigate to the dashboard
            menuUtils.menuPanelClick("Dashboard", false, "", "",driver,wait);

            if (patientCode != null) {
                // Create an appointment for the patient
                isAppointmentCreated = patientFlowHelper.createAppointment(this, patient, driver, wait, "Create Appointment", patientCode);
                if (isAppointmentCreated) {
                    threadTimer(3000); // Wait for the appointment to be created

                    // Check in the appointment
                    isAppointmentCheckedIn = patientFlowHelper.checkingAppointmentTest(this, driver, wait, "View Appointments", patientCode);

                    if (isAppointmentCheckedIn) {
                        // Navigate to the OP menu
                        menuUtils.menuPanelClick("OP", false, "", "",driver,wait);

                        // Define billing statuses
                        List<String> status = Arrays.asList("Partially Paid", "Paid");
                        int discount = 50; // Discount amount

                        // Process billing for each status
                        for (int loop = 0; loop < status.size(); loop++) {
                            System.out.println("STATUS:---" + status.get(loop));
                            opbillPayPartialStatusToPaid(patientCode, status.get(loop), loop, discount);
                        }

                        // Cancel the bill after processing
                        cancelBill(patientCode);
                    }
                }
            }
        }
    }

    // Helper method to handle partial payment to paid status
    private void opbillPayPartialStatusToPaid(String patientCode, String statusText, int loop, int discount) {
        threadTimer(3000); // Wait for the page to load

        // Get pagination details
        int totalPages = getPaginationDetails();
        System.out.println("Total Pages: " + totalPages);
        System.out.println("Patient Code: " + patientCode);

        filterSearchClick();
        filterSearchElemenet(patientCode, "patientCode", "Text");

        System.out.println("Successfully selected");
        // Find the row with the patient code and process billing
        if (findRow(patientCode, "View Bill", "Success", totalPages)) {
            if (statusText.equals("Partially Paid")) {
                addBillingDetails(statusText); // Add billing details
                amountTabClick(); // Click the amount tab
                enterAmounts(statusText, discount); // Enter amounts
            }
            threadTimer(3000); // Wait for the UI to update
            submitBilling(); // Submit the billing
        }
    }

    // Helper method to get pagination details
    private int getPaginationDetails() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String pageText = (String) js.executeScript("return document.querySelector('li.small-screen')?.textContent.trim();");

        if (pageText != null && !pageText.isEmpty()) {
            String[] pageParts = pageText.split("/");
            return Integer.parseInt(pageParts[1].trim());
        }
        return 1; // Default to 1 page if pagination is not found
    }

    // Helper method to add billing details
    private void addBillingDetails(String statusText) {
        List<String> optionTexts = Arrays.asList("Consultation Charge", "Doctor Fees 5");

        // Click "Add New" to enable the dropdown if needed
        WebElement addNewButton = driver.findElement(By.xpath("//div[contains(@class, 'addIcon-button')]/span[text()='Add New']"));
        addNewButton.click();
        threadTimer(3000); // Wait for UI update

        for (String optionText : optionTexts) {
            // Find the dropdown
            WebElement dropdown = driver.findElement(By.xpath("//mat-select//span[contains(text(), 'Select')]"));

            // Check if the dropdown is disabled
            if (!dropdown.isEnabled()) {
                System.out.println("Dropdown is disabled, clicking 'Add New' to enable it...");
                addNewButton.click(); // Click Add New again to enable
                threadTimer(2000); // Wait for UI update
            }

            // Click the dropdown
            dropdown.click();
            threadTimer(2000); // Wait for dropdown options to appear

            // Select the option from the dropdown
            selectDropdownOption(optionText);

            // Click "Add New" again to add the selected option
            addNewButton.click();
            threadTimer(3000); // Wait for UI update before selecting the next option
        }
    }

    // Helper method to select an option from the dropdown
    private void selectDropdownOption(String optionText) {
        List<WebElement> options = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//mat-option//span")));
        for (WebElement option : options) {
            if (option.getText().trim().equals(optionText)) {
                scrollToElement(option); // Scroll to the option
                threadTimer(500); // Small wait
                option.click(); // Click the option
                threadTimer(1000); // Wait for the UI to update
                break;
            }
        }
    }

    // Helper method to enter amounts in the billing form
    private void enterAmounts(String statusText, int discount) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            fillDiscountAmount("Overall Discount Percentage", discount); // Fill discount amount

            // Get all table rows inside tbodyIn2
            List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.xpath("//tbody[@id='tbodyIn2']/tr")
            ));

            // Get the final amount and total paid amount
            WebElement finalAmountElement = driver.findElement(By.xpath("//tr[th[contains(text(), 'Final Amount')]]/td"));
            String finalAmount = finalAmountElement.getText().replaceAll("[^0-9]", "");
            WebElement finalTotalPaidElement = driver.findElement(By.xpath("//tr[th[contains(text(), 'Total Paid Amount')]]/td"));
            String totalPaidAmount = finalTotalPaidElement.getText().replaceAll("[^0-9]", "");

            System.out.println("Total rows found: " + rows.size());

            // Iterate through rows and fill amounts
            for (int i = 0; i < rows.size(); i++) {
                try {
                    // Re-fetch rows each iteration to prevent stale elements
                    rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//tbody[@id='tbodyIn2']/tr")));
                    WebElement row = rows.get(i);

                    // Locate the "Amount" input field inside the refreshed row
                    WebElement amountInput = row.findElement(By.xpath(".//td[contains(@class, 'text-right')]//input[@type='number']"));

                    if (amountInput.isDisplayed()) {
                        scrollToElement(amountInput); // Scroll to the input field
                        if (statusText.equals("Partially Paid")) {
                            amountInput.clear();
                            amountInput.sendKeys(String.valueOf(Math.round(Integer.parseInt(finalAmount) / 2))); // Enter half amount for partial payment
                        } else {
                            amountInput.clear();
                            amountInput.sendKeys(String.valueOf(Math.round(Float.parseFloat(finalAmount) - Float.parseFloat(totalPaidAmount)))); // Enter remaining amount
                        }
                        System.out.println("✅ Filled Amount Field in Row " + (i + 1));
                    }
                } catch (NoSuchElementException e) {
                    System.out.println("❌ No Amount Field Found in Row " + (i + 1));
                } catch (StaleElementReferenceException e) {
                    System.out.println("🔄 Stale element detected. Retrying Row " + (i + 1));
                    i--; // Retry the same row
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // Helper method to submit billing
    private void submitBilling() {
        clickElement(By.xpath("//label[contains(text(), 'Remarks')]")); // Click remarks
        clickElement(By.xpath("//button[contains(text(), 'Pay Bill')]")); // Click pay bill
        threadTimer(3000); // Wait for the UI to update
        clickElement(By.xpath("//div[contains(@class, 'sa-confirm-button-container')]//button[contains(text(), 'Yes')]")); // Confirm payment
        threadTimer(5000); // Wait for the payment to process
        xPathUtil.closePrintScreen(); // Close the print screen
    }

    // Helper method to click an element
    private void clickElement(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        scrollToElement(element); // Scroll to the element
        element.click(); // Click the element
    }

    // Helper method to scroll to an element
    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    // Helper method to find a row by patient code and status
    public Boolean findRow(String patientCode, String title, String status, int totalPages) {
        boolean isFound = false;
        int currentPage = 1;

        while (!isFound && currentPage <= totalPages) {
            // Re-fetch the table rows after each page change
            List<WebElement> rows = driver.findElements(By.xpath("//table//tr"));
            for (int i = 0; i < rows.size(); i++) {
                if (rows.get(i).getText().contains(patientCode)) {
                    System.out.println("Row Found at Index: " + (i + 1));

                    // Highlight the row
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("arguments[0].style.backgroundColor = 'yellow'", rows.get(i));

                    System.out.println("Row Highlighted!");
                    isFound = true;
                    WebElement viewButton = rows.get(i).findElement(By.xpath(".//button[@title='" + title + "']"));
                    scrollToElement(viewButton); // Scroll to the button
                    viewButton.click(); // Click the button
                    break;
                }
            }

            if (!isFound) {
                try {
                    currentPage++; // Increment before clicking
                    System.out.println("click index of page number" + currentPage);
                    WebElement pageNo = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//ul[contains(@class, 'ngx-pagination')]//li/a/span[text()='" + currentPage + "']")
                    ));

                    // Scroll into view and click the next page button
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", pageNo);
                    Thread.sleep(500); // Small delay for UI adjustment
                    pageNo.click(); // Click the next page button
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

    // Helper method to click the amount tab
    private void amountTabClick() {
        WebElement table = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//table[contains(@class, 'hm-p table-disable-hover')]")
        ));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", table); // Scroll to the table
        table.click(); // Click the table
    }

    // Helper method to fill the discount amount
    private void fillDiscountAmount(String label, int discount) {
        WebElement discountAmountField = driver.findElement(By.xpath("//tr[th[contains(text(),'" + label + "')]]//input"));
        discountAmountField.clear(); // Clear the field
        discountAmountField.sendKeys(String.valueOf(discount)); // Enter the discount amount
    }

    // Helper method to cancel the bill
    private void cancelBill(String patientCode) {
        int totalPages = getPaginationDetails();

        if (findRow(patientCode, "Cancel", "Success", totalPages)) {
            // Locate the readonly input field
            WebElement inputElement = driver.findElement(By.xpath("//input[@class='form-control' and @readonly]"));
            String inputValue = inputElement.getAttribute("value"); // Get the value from the input field
            System.out.println("Extracted Value: " + inputValue);

            // Locate the textarea field using its placeholder
            WebElement textareaElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//textarea[@placeholder='Please enter a reason for bill cancellation']")
            ));
            textareaElement.sendKeys("Incorrect patient details."); // Enter cancellation reason

            clickElement(By.xpath("//button[contains(text(), 'Cancel Bill')]")); // Click cancel bill
            System.out.println("Cancellation reason entered successfully!");
        }
    }


    private void searchButtonClick() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

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


    private void searchFieldPatientCode(String patientCode) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

// Wait for the input field inside the "Patient Code" column
        WebElement patientCodeInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//th[@title='Patient Code']//input[contains(@class, 'form-control')]")
        ));

// Scroll into view (if needed)
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", patientCodeInput);
        try {
            Thread.sleep(500); // Small delay for UI adjustment
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

// Clear any existing text and enter new value
        patientCodeInput.clear();
        patientCodeInput.sendKeys(patientCode); // Replace with actual Patient Code

        patientCodeInput.sendKeys(Keys.ENTER);

        threadTimer(3000);
    }
}