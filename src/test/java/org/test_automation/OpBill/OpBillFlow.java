package org.test_automation.OpBill;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class OpBillFlow extends LoginAndLocationTest {

    String insurenceProvider = "Info tech solution";
    String patientCode = "TEST-ORG-01";

    Boolean continueFlag=false;
    String doctorName = "Sri Dharan";


    @Test(priority = 3, description = "testLogin")
    public void createInsurenceProvider() {
        if (insurenceProvider == null) {
            menuPanelClick("Master", true, "Insurance", "");
            clickButtonElement(By.xpath("//button[contains(text(),'Add New')]"));

            insurenceProvider = "Info tech solution";
            fillInputField("insuranceProvider", insurenceProvider);
            fillInputField("insuranceProviderCode", "TEST-INSURENCE-IN");

            clickButtonElement(By.xpath("(//button[contains(text(),'Save & Close')])"));
        }

    }

    @Test(priority = 4, dependsOnMethods = "testLogin")
    public void patientRegisteration() {
        if (patientCode == null) {
            menuPanelClick("Patient Registration", false, "", "");
            threadTimer(3000);
            //fill the patient code,FirstName, lastName,Dob,phonenumber,Gender,State,City mantatory fields,and insurence details

            patientCode = "TEST-ORG-01";
            fillInputField("patientCode", patientCode);
            fillInputField("firstName", "Asta");
            fillInputField("lastName", "M");
            fillInputField("phoneNumber", "9791310502");
            selectRadioButton("gender", "Male", "");

            selectDatePicker("05-05-1994", "patRegDob12");
            threadTimer(500);


            selectFromMatSelectDropdown(driver, wait, "Select", "Chennai");

            selectRadioButton("insurance", "Yes", insurenceProvider);
            threadTimer(500);
            //expiryDate
            selectDatePicker("05-05-2025", "patient-registration2");
            fillInputField("insuranceCode", "INSURENCE");

            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Submit')]")));
            System.out.println("Submit button found and clickable.");

            // Click the submit button
            submitButton.click();
            System.out.println("Form submitted.");

            System.out.println("Verifying form submission success...");

            String messageText = handleRuntimeError("Patient", wait);

            if (messageText.contains("New Patient")) {
                String responsePatientCode = messageText.replace("New Patient", "")
                        .replace("Registered Successfully", "")
                        .trim();

                System.out.println("Extracted Code: " + responsePatientCode);
                if (!patientCode.equals(responsePatientCode)) {
                    Assert.fail("Error in Patient Registeration");
                }
            } else {
                patientCode = null;
            }
        }
    }

    @Test(priority = 5)
    public void createStaff() {


        if (doctorName == null) {
            menuPanelClick("Staff", true, "Staff Registration", "");
            threadTimer(2000);


            fillInputField("peopleCode", "DOCTOR-05");
            selectField("salutation", "Dr.");


            String firstName = "Sri";
            String lastName = "Dharan";


            fillInputField("firstName", "Sri");

            fillInputField("lastName", "Dharan");

            selectRadioButton("gender", "Male", "");


            selectDatePicker("05-05-1990", "staff-registration1");

            fillInputField("phoneNumber", "9377777323");
            fillInputField("email", "test@gmail.com");
            selectFromMatSelectDropdown(driver, wait, "Select", "Chennai");


            fillInputField("designation", "Doctor");


            fillInputField("userName", "Sridharan");
            fillInputField("password", "Admin@123");


            selectField("role", "Doctor");


            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Submit')]")));
            System.out.println("Submit button found and clickable.");

            // Click the submit button
            submitButton.click();

            doctorName = firstName + " " + lastName;
        }


    }

    @Test(priority = 6)
    public void createAppointment() {
        if(continueFlag) {
            menuPanelClick("Create Appointment", false, "", "");

            threadTimer(2000);


            WebElement patientSearchLabel = wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//label[contains(text(), 'Patient Search')]")
            )));
            if (patientSearchLabel.getText().contains("Patient Search")) {
                // System.out.println("Patient Search label found and loaded.");
                wait = new WebDriverWait(driver, Duration.ofSeconds(50));

                WebElement dropdown1 = driver.findElement(By.xpath("//select[contains(@class, 'form-control')]"));

                JavascriptExecutor js = (JavascriptExecutor) driver;

// Set the value directly
                js.executeScript("arguments[0].value='byCode';", dropdown1);

// Trigger the change event for Angular/React
                js.executeScript("arguments[0].dispatchEvent(new Event('change'));", dropdown1);


                // System.out.println("Custom dropdown option 'By Code' selected.");
                WebElement patientCodeInput = wait.until(ExpectedConditions.refreshed(
                        ExpectedConditions.elementToBeClickable(By.name("patientCode"))
                ));


                patientCodeInput.click();

                threadTimer(1000);

                patientCodeInput.sendKeys(Keys.BACK_SPACE);
                threadTimer(500);
                patientCodeInput.sendKeys(patientCode);


                List<WebElement> options = wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//mat-option"))));

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

                WebElement selectElement = driver.findElement(By.cssSelector("select[formcontrolname='minute']"));

                // Create a Select object
                Select select = new Select(selectElement);

                // Get the currently selected value
                String currentValue = select.getFirstSelectedOption().getAttribute("value");

                if (!currentValue.equals("null")) {
                    // Get all available options
                    List<WebElement> optionsMinits = select.getOptions();

                    // Find the next available minute value
                    for (int i = 0; i < optionsMinits.size(); i++) {
                        String optionValue = optionsMinits.get(i).getAttribute("value");

                        if (optionValue.equals(currentValue) && i + 1 < options.size()) {
                            // Select the next available value
                            select.selectByValue(options.get(i + 1).getAttribute("value"));
                            break;
                        }
                    }
                }

                selectDropDown("Out Patient", "purpose");


                selectDropDown(doctorName, "doctorId");

                clickButtonElement(By.xpath("(//button[contains(text(),'Save & Close')])"));
            }
        }

    }


    @Test(priority = 7)
    public void checkInAppointment() {
        if(continueFlag) {
            try {
                menuPanelClick("View Appointments", false, "", "");
                WebElement row = wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//td[span[contains(text(),'" + patientCode + "')]]/parent::tr")
                )));

                row.findElement(By.xpath(".//button[@title='Check In']")).click();

            } catch (TimeoutException ex) {
                ex.printStackTrace();

            }
        }
    }

    private void selectField(String title, String value) {
        WebElement titleDropdown = driver.findElement(By.cssSelector("select[formcontrolname='" + title + "']"));
        Select select = new Select(titleDropdown);
        select.selectByVisibleText(value);
        threadTimer(500);
    }


    private String handleRuntimeError(String type, WebDriverWait wait) {
        return wait.until(driver -> {
            // Error messages (only these should cause test failure)
            List<String> errorMessages = Arrays.asList(
                    "Error 403",
                    "Error 0",
                    "Error 2",
                    "Something Went Wrong",
                    ""
            );

            // XPath locators for detecting messages
            List<By> messageLocators = Arrays.asList(
                    By.xpath("//div[contains(@class, 'container-2')]/p"),
                    By.xpath("//p"),
                    By.xpath("//div[contains(@class, 'toast-right-top')]//p")
            );

            // Iterate through locators to find a message
            for (By locator : messageLocators) {
                List<WebElement> elements = driver.findElements(locator);
                if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                    String messageText = elements.get(0).getText().trim();
                    System.out.println("🔍 Message Found: " + messageText);

                    // Fail only if message is in errorMessages list
                    if (errorMessages.contains(messageText)) {
                        System.out.println("⚠️ Error Message Detected: " + messageText);
                        Assert.fail("Test failed due to error message: " + messageText);
                    }

                    // ✅ Return the found message text
                    return messageText;
                }
            }

            return null;
        });
    }

    public void selectDatePicker(String dateValue, String formFieldId) {

        WebElement datePickerContainer = driver.findElement(By.id(formFieldId));
        datePickerContainer.click();
        System.out.println("date value: " + dateValue);// "05-05-1994"

        String[] dateFormat = dateValue.split("-");
        String dateText = String.valueOf(Integer.parseInt(dateFormat[0])); // Day
        String monthText = convertMMToMonth(dateFormat[1]); // Convert MM to Month name
        String yearText = dateFormat[2]; // Year

        System.out.println("date: " + dateText + " month: " + monthText + " year: " + yearText);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Select Year
        WebElement yearDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.daterangepicker[style*='display: block'] select.yearselect")));
        Select selectYear = new Select(yearDropdown);
        selectYear.selectByVisibleText(yearText);
        System.out.println("Selected Year: " + yearText);

        // Wait for the month dropdown to be ready
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("div.daterangepicker[style*='display: block'] select.yearselect"), yearText));

        // Select Month
        WebElement monthDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.daterangepicker[style*='display: block'] select.monthselect")));
        Select selectMonth = new Select(monthDropdown);
        selectMonth.selectByVisibleText(monthText);
        System.out.println("Selected Month: " + monthText);

        // Wait for the day picker to refresh
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.daterangepicker[style*='display: block'] td.available")));

        // Select Day
        List<WebElement> days = driver.findElements(By.cssSelector("div.daterangepicker[style*='display: block'] td.available"));
        for (WebElement day : days) {
            System.out.println("get Days" + day.getText());
            if (day.getText().trim().equals(dateText)) { // Fixing Integer.parseInt issue
                day.click();
                System.out.println("Selected Day: " + dateText);
                break;
            }
        }
        System.out.println("Date selection complete.");
        threadTimer(2000);
    }


    public String convertMMToMonth(String monthNumber) {
        int monthInt = Integer.parseInt(monthNumber);
        return Month.of(monthInt).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }

    private void selectFromMatSelectDropdown(WebDriver driver, WebDriverWait wait, String matSelectId, String optionText) {

        try {
            // Step 1: Click the dropdown to open it
            WebElement matDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(text(), '" + matSelectId + "')]/ancestor::div[contains(@class, 'mat-select-trigger')]")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matDropdown);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", matDropdown);

            // Step 2: Wait for dropdown options to appear (Angular uses overlay for options)
            WebElement option = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//mat-option//span[contains(text(), '" + optionText + "')]")
            ));

            // Step 3: Click the desired option
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", option);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", option);

            //  System.out.println("✅ Selected option: " + optionText);

        } catch (TimeoutException e) {
            System.out.println("❌ Failed to select option: " + optionText);
            throw new RuntimeException("Dropdown selection failed", e);
        }
    }

    private void fillInputField(String fieldPathId, String value) {

        WebElement inputField = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@formcontrolname='" + fieldPathId + "'] | //app-input-text[@formcontrolname='" + fieldPathId + "']//input | //textarea[@formcontrolname='" + fieldPathId + "']")
        ));

        inputField.clear();
        inputField.sendKeys(value);
        inputField.sendKeys(Keys.TAB);
        threadTimer(500);
    }


    private void selectRadioButton(String formControlName, String value, String insurenceProvider) {
        try {
            WebElement radioButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@formcontrolname='" + formControlName + "'][@value='" + value + "'] | //label[span[contains(text(), '" + value + "')]]/input[@formcontrolname='" + formControlName + "'] ")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", radioButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioButton);
            System.out.println("Selected radio button: " + value);
            if (value.equals("Yes")) {
                selectDropDown(insurenceProvider, "insuranceProviderId");
            }
        } catch (TimeoutException e) {
            System.out.println("Radio button with value '" + value + "' not found!");
        }
    }

    private void selectDropDown(String value, String fieldId) {
        WebElement insuranceDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("select[formcontrolname='" + fieldId + "']")));
        Select select = new Select(insuranceDropdown);

// Option 1: Directly select by visible text (simplest if you know the exact text)
        try {
            select.selectByVisibleText(value); // corrected variable name spelling
        } catch (NoSuchElementException e) {
            System.out.println("find this value '" + value + "' not found in dropdown");
        }

// OR Option 2: Iterate through options (if you need additional logic)
        List<WebElement> options = select.getOptions();
        boolean found = false;

        for (WebElement option : options) {
            if (option.getText().contains(value)) {
                select.selectByVisibleText(option.getText());
                found = true;
                break; // exit loop once found
            }
        }

        if (!found) {
            System.out.println("Insurance provider '" + value + "' not found in dropdown");
        }
    }
}
