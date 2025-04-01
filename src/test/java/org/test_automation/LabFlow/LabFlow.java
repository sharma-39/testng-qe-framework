package org.test_automation.LabFlow;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class LabFlow extends LoginAndLocationTest {

    String labGroupName;
    String specimenName;
    String uomName;

    String labTestName;

    @Test(priority = 4, description = "Lab Master", dependsOnMethods = "testLogin")
    public void labMasterCreate() {
        menuPanelClick("Master", true, "Lab", "");

        labGroupName = "Lab Group" + generateSequence();

        specimenName = "Spec" + generateSequence();

        String uomCode = "UOM-C" + generateSequence();
        uomName = "UOM-N" + generateSequence();

        labTestName = "Lab Test" + generateSequence();

        threadTimer(2000);
        clickButtonElement(By.xpath("//a[@id='Test Group' and contains(@class, 'nav-link')]"));
        clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));


        //create TestGroup
        //fill the labGroupName

        fillInputField("labGroupName", labGroupName);
        threadTimer(1000);

        multipleElementSaveAndClose("labGroupForm");

        threadTimer(2000);
        //create Specimen Details
        clickButtonElement(By.xpath("//a[@id='Specimen Details' and contains(@class, 'nav-link')]"));

        clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));


        threadTimer(1000);
        // Locate the specimenForm by its ID
        WebElement specimenForm = driver.findElement(By.xpath("//form[@id='specimenGroupForm']"));

// Locate all input elements inside the specimenForm
        List<WebElement> inputFields = specimenForm.findElements(By.xpath(".//input"));

// Iterate over the list of input fields and perform actions

        for (WebElement inputField : inputFields) {
            inputField.click();
            inputField.sendKeys(specimenName);
        }

        multipleElementSaveAndClose("specimenGroupForm");

        threadTimer(2000);


        clickButtonElement(By.xpath("//a[@id='UOM' and contains(@class, 'nav-link')]"));
        clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));


        fillInputField("uomCode", uomCode);
        fillInputField("uomName", uomName);

        multipleElementSaveAndClose("chargesForm");


        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("location.reload()");
        threadTimer(2000);
        //lab text nav form

        clickButtonElement(By.xpath("//a[@id='Lab Test' and contains(@class, 'nav-link')]"));


        clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));


        fillInputField("labTestName", labTestName);
        selectField("labGroupId", labGroupName);
        selectField("labTypeId", "Basic");

        selectRadioButton("formulary", "Yes", "");
        selectRadioButton("nrtForTheDay", "Yes", "");
        selectRadioButton("nrtForTheStay", "Yes", "");
        fillInputField("routineDuration", "10");
        fillInputField("statDuration", "50");

        selectRadioButton("cultureTest", "Yes", "");

        //   fillInputField("price","1000");
        selectRadioButton("confidentialReport", "Yes", "");

        selectRadioButton("active", "Active", "");

        fillTextareaField("testDescription", "description value");

        String packageOnly = "No";
        selectRadioButton("packageOnly", packageOnly, "");

        String performanceType = "Outsource";

        if (packageOnly.contains("Yes")) {
        } else {
            //performance type : =Inhouse and Outsource
            selectRadioButton("performanceType", performanceType, "");

            if (performanceType.equals("Outsource")) {
                //type :Billable  and Report only
                selectRadioButton("serviceType", "Billable", "");

            }
        }

        selectWithOutFormNameUsingTitle("Specimen Type", specimenName);
        selectWithOutFormNameUsingTitle("UOM", uomName);

        multipleElementSaveAndClose("labTestForm");

        if (packageOnly.equals("Yes")) {

            WebElement row = wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//td[span[contains(text(),'" + labTestName + "')]]/parent::tr")
            )));

            row.findElement(By.xpath(".//button[@title='Add Result']")).click();

            threadTimer(2000);
            selectField("sex", "Male");
            fillTheTextFieldInTitle("Age (From)", "5");
            selectField("ageFromUnit", "Years");

            fillTheTextFieldInTitle("Age (To)", "10");
            selectField("ageToUnit", "Years");

            selectRadioButton("resultHelpValue", "Yes", "");

            String labResultTypeId = "Drop-Down";

            selectField("labResultTypeId", labResultTypeId);

            if (labResultTypeId.equals("Range")) {
                System.out.println("selected " + labResultTypeId);
                fillInputField("low", "50");
                fillInputField("high", "100");
                selectField("uomId", uomName);
                fillInputField("criticallyLow", "10");
                fillInputField("criticallyHigh", "110");
                fillInputField("defaultValue", "10");
                //fillTextareaField("Description","test description");

            } else if (labResultTypeId.equals("Multi-Range")) {
                System.out.println("selected " + labResultTypeId);

                Boolean abnormalEnable = true;
                WebElement abnormalCheckbox = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//label[contains(., 'Abnormal')]/following-sibling::input[@type='checkbox']")
                ));


                if (abnormalEnable) {
                    if (!abnormalCheckbox.isSelected()) {
                        abnormalCheckbox.click();
                    }
                }
                selectWithOutFormNameUsingTitle("Range", "<=");
                fillTheTextFieldInTitle("Value", "50");

                fillTheTextFieldInTitle("Description", "Test desc");

                //selectWithOutFormNameUsingTitle("UOM", uomName);

                selectFieldUsingXPath("//*[@id=\"procedureForm\"]/div[2]/div[2]/div[4]/div/span/app-select/div/select", uomName);
            } else if (labResultTypeId.equals("Descriptive")) {

                System.out.println("selected " + labResultTypeId);

                fillTextareaField("valueStr", "test desc");

                selectFieldUsingXPath("//*[@id=\"procedureForm\"]/div[1]/div[7]/div[3]/app-select/div/select", uomName);
            } else if (labResultTypeId.equals("Drop-Down")) {
                System.out.println("selected " + labResultTypeId);
                WebElement abnormalCheckbox = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//label[contains(., 'Abnormal')]/following-sibling::input[@type='checkbox']")
                ));
                Boolean abnormalEnable = true;
                if (abnormalEnable) {
                    if (!abnormalCheckbox.isSelected()) {
                        abnormalCheckbox.click();
                    }
                }
                fillTheTextFieldInTitle("Options", "Test");

                selectFieldUsingXPath("//*[@id=\"procedureForm\"]/div[1]/div[7]/div[2]/div[2]/app-select/div/select", uomName);
            }


            multipleElementSaveAndClose("procedureForm");

            threadTimer(2000);

        }
        System.out.println("complete lab Test flow PO " + packageOnly + "| PT:" + performanceType);
        System.out.println("charges added to lab name" + labTestName);
        if (packageOnly.equals("No")) {
            if (performanceType.equals("Outsource")) {
                switchChargeTap(labTestName);
            }
        }
    }

    private void switchChargeTap(String labTestName) {


        menuPanelClick("Master", true, "Charges", "");
        clickButtonElement(By.xpath("//a[@id='Charges' and contains(@class, 'nav-link')]"));

        WebElement row = wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//td[span[contains(text(),'" + labTestName + "')]]/parent::tr")
        )));

        row.findElement(By.xpath(".//button[@title='Edit']")).click();

    }

    private void selectFieldUsingXPath(String xpath, String uomName) {


        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath(xpath)
        ));

        Select select = new Select(dropdown);

        select.selectByVisibleText(uomName);
    }


    private void fillTheTextFieldInTitle(String title, String value) {
        String cssSelector = "app-input-text[title='" + title + "'] input, input[title='" + title + "']";

        WebElement ageInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(cssSelector)
        ));

        // Clear existing value using JavaScript (works better with Angular forms)
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = '';", ageInput
        );

        ageInput.sendKeys(value);


        threadTimer(500);
    }

    private void multipleElementSaveAndClose(String id) {
        By saveCloseLocator = By.xpath("//form[@id='" + id + "']//button[contains(@class, 'saveNdClose') and normalize-space(.)='Save & Close']");

        clickButtonElement(saveCloseLocator);

        threadTimer(2000);
    }

    private void selectWithOutFormNameUsingTitle(String title, String value) {

        threadTimer(500);
        // Locate the Angular <app-select> component and its nested <select>
        String cssSelector = "app-select[title='" + title + "'] select";

        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(cssSelector)
        ));

        // Use Selenium's Select class for dropdown manipulation
        Select select = new Select(dropdown);

        List<WebElement> options = select.getOptions();

        System.out.println("Dropdown Options:");
        for (WebElement option : options) {
            System.out.println("Value: " + option.getAttribute("value") + " | Text: " + option.getText());
        }
        // Select by visible text (alternatively use value "973")
        select.selectByVisibleText(value);

        // Verify selection\
        threadTimer(500);  // Your existing delay
    }

    private void fillTextareaField(String formControlName, String value) {
        // Use CSS selector to target the Angular component structure
        String cssSelector = String.format("app-textarea[formcontrolname='%s'] textarea", formControlName);

        // Wait for the textarea to be interactable
        WebElement textarea = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(cssSelector)));

        // Clear existing text using JavaScript (more reliable for Angular fields)
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = '';", textarea
        );

        // Send keys and validate input
        textarea.sendKeys(value);

        // Verify the value was entered

        threadTimer(500);  // Your existing delay
    }

    //common functionality working

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

    private void fillMatOptionField(String fieldPathId, String value) {


        WebElement inputField = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@formcontrolname='" + fieldPathId + "'] | //app-input-text[@formcontrolname='" + fieldPathId + "']//input | //textarea[@formcontrolname='" + fieldPathId + "']")
        ));

        inputField.clear();
        inputField.sendKeys(value);

        inputField.sendKeys(Keys.BACK_SPACE);
        threadTimer(500);
// 3. Wait for options to appear in the autocomplete
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement desiredOption = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//mat-option[contains(., '" + value + "')]")
        ));

// 4. Click the matching option
        desiredOption.click();
    }


    private void selectField(String title, String value) {
        WebElement titleDropdown;
        try {
            titleDropdown = driver.findElement(By.cssSelector("select[formcontrolname='" + title + "']"));
        } catch (Exception e) {
            System.out.println("exception InvalidSelectorException ");
            titleDropdown = driver.findElement(By.cssSelector("app-select[formcontrolname='" + title + "'] select"));
        }
        Select select = new Select(titleDropdown);

        List<WebElement> option = select.getOptions();
        System.out.println("size" + option.size());
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
                By.xpath("//input[@formcontrolname='" + fieldPathId + "'] | //app-input-text[@formcontrolname='" + fieldPathId + "']//input | //textarea[@formcontrolname='" + fieldPathId + "'] | app-textarea[@formcontrolname='" + fieldPathId + "']//textarea")
        ));
        if (inputField.isEnabled()) {
            inputField.clear();
            inputField.sendKeys(value);
            inputField.sendKeys(Keys.TAB);
            threadTimer(500);
        }
    }


    private void selectRadioButton(String formControlName, String value, String childValue) {
        try {
            WebElement radioButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@formcontrolname='" + formControlName + "'][@value='" + value + "'] | //label[span[contains(text(), '" + value + "')]]/input[@formcontrolname='" + formControlName + "'] ")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", radioButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioButton);
            System.out.println("Selected radio button: " + value);
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

    String generateSequence() {
        String randomPart = "-" + new Random().nextInt(9999);
        return randomPart;
    }

    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    private void closePrintScreen() {
        try {
            Robot robot = new Robot();
            robot.delay(1000); // Wait before sending key
            robot.keyPress(KeyEvent.VK_ESCAPE); // Press escape key
            robot.keyRelease(KeyEvent.VK_ESCAPE); // Release escape key
            threadTimer(4000); // Wait for the screen to close
        } catch (AWTException ignored) {
            ignored.printStackTrace();
        }
    }
}
