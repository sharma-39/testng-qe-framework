package org.test_automation.OpBill;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.test_automation.DBConnectivity.MenuUtils;
import org.test_automation.DBConnectivity.XPathUtil;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.test_automation.VO.Charges;
import org.test_automation.VO.OpBillData;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.List;

public class OpBillFlow extends LoginAndLocationTest {

    public OpBillData testData;
    String insurenceProvider = null;
    String patientCode = null;

    String headerType = null;
    Boolean continueFlag = true;
    String doctorName = null;

    String chargesName;

    private final XPathUtil xPathUtil=new XPathUtil();

    private final MenuUtils menuUtils=new MenuUtils();

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
            menuUtils.menuPanelClick("Master", true, "Insurance", "",driver,wait);
            xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Add New')]"),driver,wait);

            insurenceProvider = testData.getInsuranceProvider().getNameBase() + generateSequence();
            fillInputField("insuranceProvider", insurenceProvider);
            fillInputField("insuranceProviderCode", testData.getInsuranceProvider().getCodeBase() + generateSequence());

            xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Save & Close')])"),driver,wait);
        }

    }

    @Test(priority = 6, dependsOnMethods = "testLogin")
    public void patientRegisteration() {
        Random random = new Random();
        if (patientCode == null) {
            menuUtils.menuPanelClick("Patient Registration", false, "", "",driver,wait);
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
            menuUtils.menuPanelClick("Staff", true, "Staff Registration", "",driver,wait);
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
            menuUtils.menuPanelClick("Dashboard", false, "", "",driver,wait);


        }


    }

    @Test(priority = 8)
    public void createAppointment() {
        if (continueFlag) {
            menuUtils.menuPanelClick("Create Appointment", false, "", "",driver,wait);

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

                xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Save & Close')])"),driver,wait);
            }
        }

    }


    @Test(priority = 9)
    public void checkInAppointment() {
        if (continueFlag) {
            try {
                menuUtils.menuPanelClick("View Appointments", false, "", "",driver,wait);
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
            menuUtils.menuPanelClick("Master", true, "Charges", "",driver,wait);
            threadTimer(2000);
            xPathUtil.clickButtonElement(By.xpath("//a[@id='Header Type' and contains(@class, 'nav-link')]"),driver,wait);
            xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Add')]"),driver,wait);

            headerType = chargesData.getHeaderTypeBase() + generateSequence();
            fillInputField("headerTypeName", headerType);
            xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Save & Close')])"),driver,wait);

            // Create Header Group
            xPathUtil.clickButtonElement(By.xpath("//a[@id='Header Group' and contains(@class, 'nav-link')]"),driver,wait);
            xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Add')]"),driver,wait);

            String headerGroup = chargesData.getHeaderGroupBase() + generateSequence();
            fillInputField("headerGroupName", headerGroup);
            selectField("headerTypeId", headerType);
            xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Save & Close')])"),driver,wait);

            // Create Header
            xPathUtil.clickButtonElement(By.xpath("//a[@id='Header' and contains(@class, 'nav-link')]"),driver,wait);
            xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Add')]"),driver,wait);

            String headerName = chargesData.getHeaderNameBase() + generateSequence();
            fillInputField("headerName", headerName);
            selectField("headerGroupId", headerGroup);
            selectField("headerTypeId", headerType);
            xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Save & Close')])"),driver,wait);

            // Create UOM
            xPathUtil.clickButtonElement(By.xpath("//a[@id='UOM' and contains(@class, 'nav-link')]"),driver,wait);
            xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Add')]"),driver,wait);

            String uomCode = chargesData.getUomCodeBase() + generateSequence();
            String uomName = chargesData.getUomNameBase() + generateSequence();
            fillInputField("uomCode", uomCode);
            fillInputField("uomName", uomName);
            xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Save & Close')])"),driver,wait);

            // Create Charges
            xPathUtil.clickButtonElement(By.xpath("//a[@id='Charges' and contains(@class, 'nav-link')]"),driver,wait);
            xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Add')]"),driver,wait);

            fillMatOptionField("headerName", headerName);
            selectField("itemType", chargesData.getItemType());

            chargesName = chargesData.getItemNameBase() + generateSequence();
            fillInputField("itemName", chargesName);
            selectField("uomId", uomName);
            fillInputField("unitPrice", chargesData.getUnitPrice());
            selectRadioButton("usedStatus", "Active", "");

            WebElement checkbox = driver.findElement(
                    By.cssSelector("input[formcontrolname='excludeInsuranceClaim']")
            );
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);

            xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Save & Close')]"),driver,wait);
        }
    }

    @Test(priority = 11)
    public void createOpBill() {
        menuUtils.menuPanelClick("OP", false, "", "",driver,wait);

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


            xPathUtil.clickButtonElement(By.xpath("//label[contains(text(), 'Remarks')]"),driver,wait); // Click remarks
            xPathUtil.clickButtonElement(By.xpath("//button[contains(text(), 'Pay Bill')]"),driver,wait); // Click pay bill
            threadTimer(3000); // Wait for the UI to update
            xPathUtil.clickButtonElement(By.xpath("//div[contains(@class, 'sa-confirm-button-container')]//button[contains(text(), 'Yes')]"),driver,wait); // Confirm payment
            threadTimer(5000);

            xPathUtil.closePrintScreen();

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

    String generateSequence() {
        String randomPart = ""+ new Random().nextInt(9999);
        return randomPart;
    }

    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }


}
