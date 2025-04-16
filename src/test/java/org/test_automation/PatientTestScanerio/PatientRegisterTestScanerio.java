package org.test_automation.PatientTestScanerio;

import org.asynchttpclient.util.DateUtils;
import org.test_automation.DBConnectivity.DatePickerUtil;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

public class PatientRegisterTestScanerio extends LoginAndLocationTest {

    static int scenario = 1;
    private final DatePickerUtil dateUtils = new DatePickerUtil();
    Map<String, Boolean> mandatoryFieldsMap = new LinkedHashMap<>();
    private JSONObject patientData;

    public static String generateRandomFirstName(int length) {
        Random random = new Random();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder firstName = new StringBuilder();

        for (int i = 0; i < length; i++) {
            firstName.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }

        return firstName.toString();
    }

    @DataProvider(name = "patientDataProvider")
    public Object[][] getPatientData() {
        return new Object[][]{
                // Scenario 1: DOB < 18 → Fill Parent Details
                {createPatientData(
                        "Mr.", "Test Name" + generateRandomFirstName(5) + generateRandomFirstName(5), "M", "D/O", "A +ve", "Intulogic", "05-05-2010", "9791310502",
                        "Murugaiyan", "9883834874", "Male", "Married", "77 west street srinivasonnalur kumbakonam",
                        "sharmamurugaiyan@gmail.com", "Tamil Nadu", "Chennai", "F CVT", "fill any value", "612204",
                        "Test", "Brother", "9477477478", "test@gmail.com", "Indian", "267323633773", "application", "good",
                        null, null, null, "testing purpose"
                ), true},
                // Scenario 2: DOB ≥ 18 → Fill Phone Number
                {createPatientData(
                        "Mr.", "Test Name" + generateRandomFirstName(5), "M", "D/O", "A +ve", "Intulogic", "05-05-1994", "9791310502",
                        null, null, "Male", "Married", "77 west street srinivasonnalur kumbakonam",
                        "sharmamurugaiyan@gmail.com", "Tamil Nadu", "Chennai", "F CVT", "fill any diagnonsis", "612204",
                        null, null, null, null, "Indian", "267323633773", "application", "good",
                        null, null, null, "testing purpose"
                ), true},
                // Scenario 3: Title is NULL → Fill all fields
                {createPatientData(
                        "Mr.", "Test Name" + generateRandomFirstName(5), "M", "Cancel", "A +ve", null, "05-05-1994", "9791310502",
                        null, null, "Male", "Married", "77 west street srinivasonnalur kumbakonam",
                        "sharmamurugaiyan@gmail.com", "Tamil Nadu", "Chennai", "F CVT", "fill any diagnonsis", "612204",
                        null, null, null, null, "Indian", "267323633773", "application", "good",
                        null, null, null, "testing purpose"
                ), true},
                // Scenario 4: Title = "D/O" → Fill Guardian Name
                {createPatientData(
                        "Mr.", "Test Name" + generateRandomFirstName(5), "M", "D/O", "A +ve", "Guardian Test", "05-05-1994", "9791310502",
                        null, null, "Male", "Married", "77 west street srinivasonnalur kumbakonam",
                        "sharmamurugaiyan@gmail.com", "Tamil Nadu", "Chennai", "F CVT", "fill any diagnonsis", "612204",
                        null, null, null, null, "Indian", "267323633773", "application", "good",
                        null, null, null, "testing purpose"
                ), true},
                // Scenario 5: Fill Only Mandatory Fields
                {createPatientData(
                        null, "Test Name" + generateRandomFirstName(5), null, "Cancel", null, null, "05-05-1994", "9791310502",
                        null, null, "Male", null, null,
                        null, "Tamil Nadu", "Chennai", null, null, null,
                        null, null, null, null, null, null, null, null,
                        null, null, null, null
                ), true},
                // Scenario 6: Fill Without Mandatory Fields (Error Highlighting)
                {createPatientData(
                        null, null,
                        "M", "Cancel", null, null, null, null,
                        null, null, null, null, null, "emailid@gmail.com", null, null,
                        null, null, null,
                        null, null, null, null, null, null, null, null, null, null, null, "Testing errors"
                ), false},
                // Scenario 7: Select "Insurance = Yes" → Fill Insurance Fields
                {createPatientData(
                        null, "Test Name" + generateRandomFirstName(5), "M", "D/O", "A +ve", "Intulogic", "05-05-1994", "9791310502",
                        null, null, "Male", "Married", "77 west street srinivasonnalur kumbakonam",
                        "sharmamurugaiyan@gmail.com", "Tamil Nadu", "Chennai", "F CVT", "fill any diagnonsis", "612204",
                        null, null, null, null, "Indian", "267323633773", "application", "good",
                        "Yes", "8273827383", "10-10-2025", "testing purpose"
                ), true},
                // Scenario 8: Select "Insurance = No"
                {createPatientData(
                        null, "Test Name" + generateRandomFirstName(5), "M", "D/O", "A +ve", "Intulogic", "05-05-1994", "9791310502",
                        null, null, "Male", "Married", "77 west street srinivasonnalur kumbakonam",
                        "sharmamurugaiyan@gmail.com", "Tamil Nadu", "Chennai", "F CVT", "fill any diagnonsis", "612204",
                        null, null, null, null, "Indian", "267323633773", "application", "good",
                        "No", null, null, "testing purpose"
                ), true}
                // Scenario 9: 500 error throw patient registeration
                , {createPatientData(
                null, "Sharma", "M", "D/O", "A +ve", "Intulogic", "05-05-1994", "9791310502",
                null, null, "Male", "Married", "77 west street srinivasonnalur kumbakonam",
                "sharmamurugaiyan@gmail.com", "Tamil Nadu", "Chennai", "F CVT", "fill any diagnonsis", "612204",
                null, null, null, null, "Indian", "267323633773", "application", "good",
                "No", null, null, "testing purpose"
        ), false}

                // Scenario 10: email and phone length error validator throw patient registeration
                , {createPatientData(
                null, "Sharma", "M", "D/O", "A +ve", "Intulogic", "05-05-1994", "9791310",
                null, null, "Male", "Married", "77 west street srinivasonnalur kumbakonam",
                "sharmamurugaiyan", "Tamil Nadu", "Chennai", "F CVT", "fill any diagnonsis", "612204",
                null, null, null, null, "Indian", "267323633773", "application", "good",
                "No", null, null, "testing purpose"
        ), false}
        };
    }

    private JSONObject createPatientData(
            String salutation, String firstName, String lastName, String title, String bloodGroup, String guardianName,
            String dob, String phoneNumber, String parentName, String parentNumber, String gender, String maritalStatus,
            String address, String email, String state, String city, String caseType, String diagnosis, String postalCode,
            String inchargeName, String inchargeRelationship, String inchargePhone, String inchargeEmail, String citizian,
            String aadharNumber, String knownAllergies, String previousMedicalIssue, String insurance,
            String insuranceCode, String expiryDateInsurance, String notes) {

        Map<String, Object> orderedMap = new LinkedHashMap<>();
        orderedMap.put("salutation", salutation);
        orderedMap.put("firstName", firstName);
        orderedMap.put("lastName", lastName);
        orderedMap.put("title", title);
        orderedMap.put("bloodGroup", bloodGroup);
        orderedMap.put("guardianName", guardianName);
        orderedMap.put("dob", dob);
        orderedMap.put("phoneNumber", phoneNumber);
        orderedMap.put("parentName", parentName);
        orderedMap.put("parentNumber", parentNumber);
        orderedMap.put("gender", gender);
        orderedMap.put("maritalStatus", maritalStatus);
        orderedMap.put("address", address);
        orderedMap.put("email", email);
        orderedMap.put("state", state);
        orderedMap.put("city", city);
        orderedMap.put("caseType", caseType);
        orderedMap.put("diagnosis", diagnosis);
        orderedMap.put("postalCode", postalCode);
        orderedMap.put("inchargeName", inchargeName);
        orderedMap.put("inchargeRelationship", inchargeRelationship);
        orderedMap.put("inchargePhone", inchargePhone);
        orderedMap.put("inchargeEmail", inchargeEmail);
        orderedMap.put("citizian", citizian);
        orderedMap.put("aadharNumber", aadharNumber);
        orderedMap.put("knownAllergies", knownAllergies);
        orderedMap.put("previousMedicalIssue", previousMedicalIssue);
        orderedMap.put("insurance", insurance);
        orderedMap.put("insuranceCode", insuranceCode);
        orderedMap.put("expiryDateInsurance", expiryDateInsurance);
        orderedMap.put("notes", notes);

        patientData = new JSONObject(orderedMap);
        return new JSONObject(orderedMap);
    }

    @Test(priority = 3, dependsOnMethods = {"testLogin"})
    public void runOpenMenu() {
        if (isLoginSuccessful) {
            menuPanelClick("Patient Registration", false, "", "");
        }
    }

    @Test(priority = 4, dataProvider = "patientDataProvider", description = "Patient Registration Test")
    public void patientRegisteration(JSONObject patientData, boolean expectedResult) throws IOException, InterruptedException {
        if (isLoginSuccessful) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("location.reload()");

            // Print the scenario description, data, and expected result
            System.out.println("\n=========================================");
            System.out.println("Scenario Description: " + getScenarioDescription(patientData));
            System.out.println("Testing Scenario with Data: " + scenario);
            System.out.println("Expected Result: " + (expectedResult ? "Success" : "Failure"));
            System.out.println("=========================================");


            //findMantatoryFields();
            //validateMantatoryFields();

            // Fill all fields in the form using the patientData JSON object
            fillAllFieldsFromJSON(patientData);

            // Submit the form
            try {
                WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Submit')]")));
                System.out.println("Submit button found and clickable.");

                // Click the submit button
                submitButton.click();
                System.out.println("Form submitted.");

                // Handle success or failure
                if (expectedResult) {
                    // Verify successful submission
                    System.out.println("Verifying form submission success...");

                    String messageText = handleRuntimeError("Patient", wait);
                    if (messageText.contains("New Patient")) {

                        System.out.println("Successfully register" + messageText);
                    } else {

                        // Handle validation errors
                        errorMessageHandle(driver, wait);
                    }
                }

            } catch (Exception e) {
                System.out.println("Error during form submission: " + e.getMessage());
                e.printStackTrace();
            }
            if (scenario == 1 || scenario == 2 || scenario == 3 || scenario == 4 || scenario == 5 || scenario == 7 || scenario == 8) {
                scenario++;
            } else if (scenario == 6 || scenario == 10) {
                scenario++;
                findMantatoryFields();

                threadTimer(2000);

                WebElement reset = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Reset')]")));
                System.out.println("Reset clicked");
                reset.click();
                Assert.fail(getScenarioDescription(patientData));

            } else {
                WebElement reset = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Reset')]")));
                System.out.println("Reset clicked");
                reset.click();
            }


            threadTimer(3000);


        }
    }

    private String getScenarioDescription(JSONObject patientData) {


        if (scenario == 1) {
            return "Scenario 1: If the patient's age is less than 18, parent details must be filled.";
        } else if (scenario == 2) {
            return "Scenario 2: If the patient's age is 18 or older, the phone number must be provided.";
        } else if (scenario == 3) {
            return "Scenario 3: If the title is NULL, all required fields must be filled.";
        } else if (scenario == 4) {
            return "Scenario 4: If the title is 'D/O', the guardian's name must be provided.";
        } else if (scenario == 5) {
            return "Scenario 5: Only mandatory fields are filled.";
        } else if (scenario == 6) {
            return "Scenario 6: If mandatory fields are missing, the form highlights errors.";
        } else if (scenario == 7) {
            return "Scenario 7: If insurance is selected as 'Yes', insurance details must be provided.";
        } else if (scenario == 8) {
            return "Scenario 8: If insurance is selected as 'No', insurance fields remain empty.";
        } else if (scenario == 9) {
            return "Scenario 9: A 500 error is thrown during patient registration.";
        } else if (scenario == 10) {
            return "Scenario 10: Email and phone number validation is enforced.";
        } else {
            return "Unknown Scenario: Please provide a valid scenario number.";
        }

    }

    private void fillAllFieldsFromJSON(JSONObject patientData) throws InterruptedException {
        String[] allFields = {
                "salutation", "firstName", "lastName", "title", "bloodGroup", "guardianName",
                "dob", "phoneNumber", "parentName", "parentNumber", "gender", "maritalStatus",
                "address", "email", "state", "city", "caseType", "diagnosis", "postalCode",
                "inchargeName", "inchargeRelationship", "inchargePhone", "inchargeEmail",
                "citizian", "aadharNumber", "knownAllergies", "previousMedicalIssue",
                "insurance", "insuranceCode", "expiryDateInsurance", "notes"
        };
        System.out.println("Filling all fields from JSON data...");


        for (String fieldName : allFields) {
            if (patientData.has(fieldName)) {
                Object fieldValue = patientData.get(fieldName);

                // Skip NULL values
                if (fieldValue != JSONObject.NULL) {
                    System.out.println("Filling field: " + fieldName + " with value: " + fieldValue);
                    fillField(fieldName, patientData);
                }
            }
        }
    }

    private void fillField(String fieldName, JSONObject patientData) throws InterruptedException {
        switch (fieldName) {
            case "firstName":
            case "lastName":
            case "phoneNumber":
            case "address":
            case "email":
            case "diagnosis":
            case "postalCode":
            case "parentName":
            case "parentNumber":
            case "parentEmail":
            case "aadharNumber":
            case "knownAllergies":
            case "previousMedicalIssue":
            case "insuranceCode":
            case "notes":
            case "guardianName":
                fillInputField(fieldName, patientData.getString(fieldName), fieldMantatoryValidator(fieldName));
                break;
            case "title":
            case "bloodGroup":
            case "incharge1Relationship":
                selectField(fieldName, patientData.getString(fieldName));
                break;
            case "citizian":
                selectRadioButtonCitizen(fieldName, patientData.getString(fieldName));
                break;
            case "gender":
            case "maritalStatus":
            case "insurance":
                selectRadioButton(fieldName, patientData.getString(fieldName));
                break;
            case "dob":
                dateUtils.selectDatePicker(patientData.getString(fieldName), "patRegDob12", wait, driver);
                break;
            case "state":
                selectSelectDropdown(fieldName, patientData.getString("state"));
                break;
            case "city":
            case "caseType":
                matSelectDropDown(fieldName, patientData.getString(fieldName));
                break;
            case "expiryDateInsurance":
                dateUtils.selectDatePicker(patientData.getString(fieldName), "patient-registration2", wait, driver);
                break;
            default:
                System.out.println("Unknown field: " + fieldName);
                break;
        }
    }

    private void selectRadioButtonCitizen(String fieldName, String value) {

        try {

            WebElement radioButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@formcontrolname='nri'][@value='" + value + "'] | //label[span[contains(text(), '" + value + "')]]/input[@formcontrolname='nri'] ")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", radioButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioButton);
            System.out.println("Selected radio button: " + value);
        } catch (TimeoutException e) {
            System.out.println("Radio button with value '" + value + "' not found!");
        }
    }

    private boolean fieldMantatoryValidator(String keyName) {
        return mandatoryFieldsMap.containsKey(keyName) ? mandatoryFieldsMap.get(keyName) : false;
    }

    private void findMantatoryFields() {
        List<WebElement> asteriskElements = driver.findElements(By.xpath(
                "//span[contains(@style,'color: red') and text()='*']"
        ));

        System.out.println("Total Fields Marked with Red Asterisk: " + asteriskElements.size());

        JavascriptExecutor js = (JavascriptExecutor) driver;

        for (WebElement asterisk : asteriskElements) {
            WebElement field = null;

            try {
                field = asterisk.findElement(By.xpath(
                        "./ancestor::label/following-sibling::input | " +
                                "./ancestor::label/following-sibling::select | " +
                                "./parent::div//input | " +
                                "./parent::div//select | " +
                                "./parent::div//input[@type='radio']"
                ));
            } catch (Exception e) {
                System.out.println("No direct field found for this asterisk.");
            }

            if (field != null) {
                String tagName = field.getTagName();
                String fieldType = field.getAttribute("type");

                // Highlight fields with different colors based on type
                if ("select".equals(tagName)) {
                    js.executeScript("arguments[0].style.border='3px solid blue'", field); // Dropdown
                    System.out.println("Highlighted SELECT field.");
                } else if ("radio".equals(fieldType)) {
                    js.executeScript("arguments[0].style.outline='3px solid green'", field); // Radio button
                    System.out.println("Highlighted RADIO button.");
                } else {
                    js.executeScript("arguments[0].style.border='3px solid red'", field); // Text Input
                    System.out.println("Highlighted INPUT field.");
                }

                // Print field details
                String title = field.getAttribute("title");
                String formControlName = field.getAttribute("formcontrolname");
                String placeholder = field.getAttribute("placeholder");

                System.out.println("Mandatory Field -> Title: " + title +
                        ", FormControlName: " + formControlName +
                        ", Placeholder: " + placeholder);
                if (formControlName != null && !formControlName.isEmpty()) {
                    mandatoryFieldsMap.put(formControlName, true);
                }
            }

        }
    }

    private void fillInputField(String formControlName, String value, boolean mandatory) {
        WebElement inputField = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@formcontrolname='" + formControlName + "'] | //app-input-text[@formcontrolname='" + formControlName + "']//input | //textarea[@formcontrolname='" + formControlName + "']")
        ));
        inputField.sendKeys(value);
        System.out.println("Filled " + formControlName + " with value: " + value);
    }

    private void selectField(String title, String value) {
        WebElement titleDropdown = driver.findElement(By.cssSelector("select[formcontrolname='" + title + "']"));
        Select select = new Select(titleDropdown);
        select.selectByVisibleText(value);
        threadTimer(500);
    }

    private void selectRadioButton(String formControlName, String value) {
        try {
            WebElement radioButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@formcontrolname='" + formControlName + "'][@value='" + value + "'] | //label[span[contains(text(), '" + value + "')]]/input[@formcontrolname='" + formControlName + "'] ")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", radioButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioButton);
            System.out.println("Selected radio button: " + value);
            if (value.equals("Yes")) {
                selectInsuranceId();
            }
        } catch (TimeoutException e) {
            System.out.println("Radio button with value '" + value + "' not found!");
        }
    }

    private void selectSelectDropdown(String id, String value) {
        System.out.println(id + value);
        if (id.equals("state")) {
            id = "cityChange";
        }
        WebElement dropDownField = driver.findElement(By.id(id));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value='" + value + "'; arguments[0].dispatchEvent(new Event('change'));", dropDownField);
        System.out.println(value + " selected using JavaScript");
    }

    private void matSelectDropDown(String formControlName, String optionText) {

//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(
//                By.xpath("//mat-select[@formcontrolname='" + formControlName + "']")
//        ));
//        js.executeScript("arguments[0].scrollIntoView(true);", matSelect);
//        try {
//            Thread.sleep(500); // Allow time for scrolling (try to avoid, use waits if possible)
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        matSelect.click();
//
//
//        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
//                By.xpath("//mat-option//span[contains(text(), '" + optionText + "')]")
//        ));
//        option.click();
//        System.out.println("Selected option: " + optionText);


        try {
            System.out.println("Selecting from mat-select: " + formControlName + " -> " + optionText);

            // Locate mat-select element and wait for it to be clickable
            WebElement matSelect = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//mat-select[@formcontrolname='" + formControlName + "']")
            ));

            // Scroll into view to avoid interception issues
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matSelect);
            Thread.sleep(500); // Allow UI to adjust (can be replaced with better wait)

            // Click using JavaScript to avoid interception issues
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", matSelect);

            // Wait for options to appear
            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//mat-option//span[contains(text(), '" + optionText + "')]")
            ));

            // Scroll into view and click the option
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", option);
            Thread.sleep(300); // Allow UI to adjust
            option.click();

            System.out.println("Selected option: " + optionText);
        } catch (ElementClickInterceptedException e) {
            System.out.println("ElementClickInterceptedException: Trying alternative click...");
            try {
                WebElement matSelect = driver.findElement(By.xpath("//mat-select[@formcontrolname='" + formControlName + "']"));
                Actions actions = new Actions(driver);
                actions.moveToElement(matSelect).click().perform(); // Try clicking via Actions
            } catch (Exception ex) {
                System.out.println("Alternative click also failed: " + ex.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error selecting mat-select dropdown: " + e.getMessage());
        }
    }

    public void datatePickerDob(String date, String id) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Actions actions = new Actions(driver);

        String[] dateFormat = date.split("-");
        String dateText = dateFormat[0]; // Day
        String monthText = dateFormat[1]; // Month (MM)
        String yearText = dateFormat[2]; // Year (YYYY)

        // Step 1: Ensure the date picker is closed
        actions.sendKeys(Keys.ESCAPE).perform();

        // Step 2: Open the date picker
        WebElement datePickerContainer = driver.findElement(By.id(id));
        WebElement calendarIcon = datePickerContainer.findElement(By.cssSelector("i.fa-calendar"));
        calendarIcon.click();

        // Step 3: Select the year
        WebElement yearDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("select.yearselect")));
        yearDropdown.click();
        Select yearSelect = new Select(yearDropdown);
        yearSelect.selectByValue(yearText);

        // Step 4: Select the month
        WebElement monthDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("select.monthselect")));
        monthDropdown.click();
        String monthConvertText = convertMMToMonth(monthText);
        WebElement monthOption = monthDropdown.findElement(By.xpath(".//option[text()='" + monthConvertText + "']"));
        monthOption.click();

        // Step 5: Select the day
        WebElement dayElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td[text()='" + Integer.parseInt(dateText) + "']")));
        dayElement.click();

        // Step 6: Close the date picker
        actions.sendKeys(Keys.ESCAPE).perform();

        // Step 7: Reset focus
        actions.moveByOffset(0, 0).click().perform();
    }

    public String convertMMToMonth(String monthNumber) {
        int monthInt = Integer.parseInt(monthNumber);
        return Month.of(monthInt).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }

    private int getAgeFromAgeField() {
        WebElement ageInput = driver.findElement(By.cssSelector("input[formcontrolname='age']"));
        String ageValue = ageInput.getAttribute("value");
        return Integer.parseInt(ageValue);
    }

    private void selectInsuranceId() {
        WebElement insuranceDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("select[formcontrolname='insuranceProviderId']")));
        Select select = new Select(insuranceDropdown);
        List<WebElement> options = select.getOptions();
        if (options.size() > 1) {
            Random rand = new Random();
            int randomIndex = rand.nextInt(options.size() - 1) + 1;
            select.selectByIndex(randomIndex);
            System.out.println("Selected Insurance Provider: " + options.get(randomIndex).getText());
        } else {
            System.out.println("No available options to select.");
        }
    }

    private void fillExpiryDate(String date) {
        String[] dateFormat = date.split("-");
        String dateText = dateFormat[0]; // Day
        String monthText = dateFormat[1]; // Month (MM)
        String yearText = dateFormat[2]; // Year (YYYY)

        WebElement expiryDateField = driver.findElement(By.id("patient-registration2"));
        expiryDateField.click();

        WebElement dateRangePicker = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.daterangepicker.ltr.auto-apply.single.opensright.show-calendar")
        ));


        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.border='2px solid purple'", dateRangePicker);

        WebElement leftCalendar = dateRangePicker.findElement(By.cssSelector("div.drp-calendar.left.single"));
        js.executeScript("arguments[0].style.border='2px solid red'", leftCalendar);

        WebElement monthSelectElement = leftCalendar.findElement(By.cssSelector("select.monthselect"));

        try {
            monthSelectElement.click();
            System.out.println("Clicked month dropdown using normal click.");
            Select monthSelect = new Select(monthSelectElement);
            monthSelect.selectByVisibleText(convertMMToMonth(monthText));
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", monthSelectElement);
            System.out.println("Clicked month dropdown using JavaScript.");
            js.executeScript("arguments[0].value='" + convertMMToMonth(monthText) + "'; arguments[0].dispatchEvent(new Event('change'));", monthSelectElement);
            System.out.println("Month 'Apr' selected using JavaScript.");
        }

        System.out.println("click month");
        threadTimer(2500);

        WebElement yearSelectElement = leftCalendar.findElement(By.cssSelector("select.yearselect"));
        js.executeScript("arguments[0].style.display='block';", yearSelectElement);

        try {
            yearSelectElement.click();
            System.out.println("Clicked year dropdown using normal click.");
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", yearSelectElement);
            System.out.println("Clicked year dropdown using JavaScript.");
        }

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("select.yearselect option")));

        yearSelectElement = leftCalendar.findElement(By.cssSelector("select.yearselect"));
        Select yearSelect = new Select(yearSelectElement);

        try {
            yearSelect.selectByValue(yearText);
            System.out.println("Year '" + yearText + "' selected.");
        } catch (Exception e) {
            js.executeScript("arguments[0].value='2026'; arguments[0].dispatchEvent(new Event('change'));", yearSelectElement);
            System.out.println("Year '" + yearText + "' selected using JavaScript.");
        }

        String desiredDate = dateText;

        WebElement datePicker = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.daterangepicker.ltr.auto-apply.single.opensright.show-calendar")
        ));

        List<WebElement> dateElements = datePicker.findElements(By.cssSelector("td.available"));

        boolean dateFound = false;
        for (WebElement dateElement : dateElements) {
            if (dateElement.getText().trim().equals(desiredDate)) {
                dateElement.click();
                System.out.println("Clicked date: " + desiredDate);
                dateFound = true;
                break;
            }
        }

        if (!dateFound) {
            System.out.println("Date not found: " + desiredDate);
        }
    }

    private void resetDatePicker() {
        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.ESCAPE).perform();
        actions.moveByOffset(0, 0).click().perform();
    }

    private void saluationInputJquery(String salutation) {
        WebElement salutationDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("select[formcontrolname='salutation']")));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value='" + salutation + "'; arguments[0].dispatchEvent(new Event('change'));", salutationDropdown);
        System.out.println("Salutation 'Mr.' selected using JavaScript.");
    }

    private void errorMessageHandle(WebDriver driver, WebDriverWait wait) {
        WebElement errorMessage = wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'error-msg')]")
        )));

        highlightElement(driver, errorMessage);

        String errorText = errorMessage.getText().trim();
        System.out.println("Error!:" + errorText);
        threadTimer(3000);
    }

    private void highlightElement(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.border='3px solid red';", element);
        System.out.println("Highlighted the error message element.");

        threadTimer(2000);
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
}