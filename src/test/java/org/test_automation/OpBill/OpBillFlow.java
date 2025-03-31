package org.test_automation.OpBill;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.test_automation.SupplierDTO;
import org.test_automation.VO.Charges;
import org.test_automation.VO.OpBillData;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

public class OpBillFlow extends LoginAndLocationTest {

    public OpBillData testData;
    String insurenceProvider = null;
    String patientCode = null;

    String headerType = null;
    Boolean continueFlag = true;
    String doctorName = null;

    Integer chargeSize = 0;
    String chargesName;


    @DataProvider(name = "opBillData")
    public Object[][] getTestData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new File("src/test/resources/opFlowData.json"));
        return new Object[][]{
                {mapper.treeToValue(rootNode, OpBillData.class)} // Correct syntax
        };
    }

    @Test(priority = 3, description = "testLogin", dataProvider = "opBillData")
    public void setup(OpBillData testData) {
        this.testData = testData; // Assign cached data to global variable
    }


    @Test(priority = 4, description = "testLogin")
    public void createInsurenceProvider() {
        if (insurenceProvider == null) {
            menuPanelClick("Master", true, "Insurance", "");
            clickButtonElement(By.xpath("//button[contains(text(),'Add New')]"));

            insurenceProvider = testData.getInsuranceProvider().getNameBase() + generateSequence("INS");
            fillInputField("insuranceProvider", insurenceProvider);
            fillInputField("insuranceProviderCode", testData.getInsuranceProvider().getCodeBase() + generateSequence("TEST"));

            clickButtonElement(By.xpath("(//button[contains(text(),'Save & Close')])"));
        }

    }

    @Test(priority = 6, dependsOnMethods = "testLogin")
    public void patientRegisteration() {
        Random random = new Random();
        if (patientCode == null) {
            menuPanelClick("Patient Registration", false, "", "");
            threadTimer(3000);
            //fill the patient code,FirstName, lastName,Dob,phonenumber,Gender,State,City mantatory fields,and insurence details

            patientCode = testData.getPatient().getPatientCode();
            fillInputField("patientCode", patientCode);

            String firstName = testData.getPatient().getFirstNames().get(random.nextInt(testData.getPatient().getFirstNames().size()));
            String lastName = testData.getPatient().getLastNames().get(random.nextInt(testData.getPatient().getLastNames().size()));
            fillInputField("firstName", firstName);
            fillInputField("lastName", lastName);
            fillInputField("phoneNumber", testData.getPatient().getPhoneNumber());
            selectRadioButton("gender", testData.getPatient().getGender(), "");

            selectDatePicker(testData.getPatient().getDob(), "patRegDob12");
            threadTimer(500);


            selectFromMatSelectDropdown(driver, wait, "Select", testData.getPatient().getCity());

            selectRadioButton("insurance", testData.getPatient().getInsuranceEnable(), insurenceProvider);
            threadTimer(500);
            //expiryDate
            selectDatePicker(testData.getPatient().getInsuranceExpiry(), "patient-registration2");
            fillInputField("insuranceCode", testData.getPatient().getInsuranceCode());

            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Submit')]")));
            System.out.println("Submit button found and clickable.");

            // Click the submit button
            submitButton.click();
            System.out.println("Form submitted.");

            System.out.println("Verifying form submission success...");

            String messageText = handleRuntimeError("Patient", wait);
            System.out.println("Message text:-- patient Code confirmation" + messageText);
        }
    }

    @Test(priority = 7)
    public void createStaff() {


        if (doctorName == null) {
            menuPanelClick("Staff", true, "Staff Registration", "");
            threadTimer(2000);

            fillInputField("peopleCode", testData.getStaff().getStaffCode());
            selectField("salutation", testData.getStaff().getSalutation());

            Random random = new Random();
            String firstName = testData.getStaff().getDoctorFirstNames().get(random.nextInt(testData.getStaff().getDoctorFirstNames().size()));
            String lastName = testData.getStaff().getDoctorLastNames().get(random.nextInt(testData.getStaff().getDoctorLastNames().size()));

            fillInputField("firstName", firstName);
            fillInputField("lastName", lastName);
            selectRadioButton("gender", testData.getStaff().getGender(), "");

            selectDatePicker(testData.getStaff().getDob(), "staff-registration1");
            fillInputField("phoneNumber", testData.getStaff().getPhoneNumber());
            fillInputField("email", (firstName + lastName).toLowerCase() + testData.getStaff().getEmailSuffix());
            selectFromMatSelectDropdown(driver, wait, "Select", testData.getPatient().getCity());

            fillInputField("designation", testData.getStaff().getDesignation());
            fillInputField("userName", (firstName + lastName).toLowerCase());
            fillInputField("password", testData.getStaff().getPassword());
            selectField("role", testData.getStaff().getRole());

            WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Submit')]")));
            submitButton.click();

            doctorName = firstName + " " + lastName;
            menuPanelClick("Dashboard", false, "", "");


        }


    }

    @Test(priority = 8)
    public void createAppointment() {
        if (continueFlag) {
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


    @Test(priority = 9)
    public void checkInAppointment() {
        if (continueFlag) {
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


    @Test(priority = 10, description = "added on charges")
    public void addCharges() {
        if (continueFlag) {
            Charges chargesData = testData.getCharges();

            // Create Header Type
            menuPanelClick("Master", true, "Charges", "");
            threadTimer(2000);
            clickButtonElement(By.xpath("//a[@id='Header Type' and contains(@class, 'nav-link')]"));
            clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));

            headerType = chargesData.getHeaderTypeBase() + generateSequence("HT");
            fillInputField("headerTypeName", headerType);
            clickButtonElement(By.xpath("(//button[contains(text(),'Save & Close')])"));

            // Create Header Group
            clickButtonElement(By.xpath("//a[@id='Header Group' and contains(@class, 'nav-link')]"));
            clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));

            String headerGroup = chargesData.getHeaderGroupBase() + generateSequence("HG");
            fillInputField("headerGroupName", headerGroup);
            selectField("headerTypeId", headerType);
            clickButtonElement(By.xpath("(//button[contains(text(),'Save & Close')])"));

            // Create Header
            clickButtonElement(By.xpath("//a[@id='Header' and contains(@class, 'nav-link')]"));
            clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));

            String headerName = chargesData.getHeaderNameBase() + generateSequence("HEAD");
            fillInputField("headerName", headerName);
            selectField("headerGroupId", headerGroup);
            selectField("headerTypeId", headerType);
            clickButtonElement(By.xpath("(//button[contains(text(),'Save & Close')])"));

            // Create UOM
            clickButtonElement(By.xpath("//a[@id='UOM' and contains(@class, 'nav-link')]"));
            clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));

            String uomCode = chargesData.getUomCodeBase() + generateSequence("UOM-C");
            String uomName = chargesData.getUomNameBase() + generateSequence("UOM-N-");
            fillInputField("uomCode", uomCode);
            fillInputField("uomName", uomName);
            clickButtonElement(By.xpath("(//button[contains(text(),'Save & Close')])"));

            // Create Charges
            clickButtonElement(By.xpath("//a[@id='Charges' and contains(@class, 'nav-link')]"));
            clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));

            fillMatOptionField("headerName", headerName);
            selectField("itemType", chargesData.getItemType());

            chargesName = chargesData.getItemNameBase() + generateSequence("CR");
            fillInputField("itemName", chargesName);
            selectField("uomId", uomName);
            fillInputField("unitPrice", chargesData.getUnitPrice());
            selectRadioButton("usedStatus", "Active", "");

            WebElement checkbox = driver.findElement(
                    By.cssSelector("input[formcontrolname='excludeInsuranceClaim']")
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);

            clickButtonElement(By.xpath("(//button[contains(text(),'Save & Close')]"));
        }
    }

    @Test(priority = 11)
    public void createOpBill() {
        menuPanelClick("OP", false, "", "");

        WebElement row = wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//td[span[contains(text(),'" + patientCode + "')]]/parent::tr")
        )));

        row.findElement(By.xpath(".//button[@title='View Bill']")).click();


        List<String> optionTexts = List.of(chargesName);

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


            WebElement table = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//table[contains(@class, 'hm-p table-disable-hover')]")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", table); // Scroll to the table
            table.click();


            clickButtonElement(By.xpath("//label[contains(text(), 'Remarks')]")); // Click remarks
            clickButtonElement(By.xpath("//button[contains(text(), 'Pay Bill')]")); // Click pay bill
            threadTimer(3000); // Wait for the UI to update
            clickButtonElement(By.xpath("//div[contains(@class, 'sa-confirm-button-container')]//button[contains(text(), 'Yes')]")); // Confirm payment
            threadTimer(5000);

            closePrintScreen();

        }


    }


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

    String generateSequence(String code) {
        String randomPart = code + "-" + new Random().nextInt(900);
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
