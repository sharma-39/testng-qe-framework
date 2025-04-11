package org.test_automation.LabFlow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.test_automation.DBConnectivity.DatePickerUtil;
import org.test_automation.DBConnectivity.XPathUtil;
import org.test_automation.FlowHelper.PatientFlowHelper;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.test_automation.VO.LabTestData;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

public class LabFlow extends LoginAndLocationTest {

    private static final long THREAD_SECONDS = 3000; // Constant for thread sleep time
    private static final int patientIncrement = 0; // Counter for patient increment
    private final PatientFlowHelper patientFlowHelper; // Helper class for patient flow
    private final Boolean basicLabFlow = true;
    private final Boolean basicPatientToCheckin = true;
    private final Boolean chargesFlow = true;
    private final XPathUtil xPathUtil;
    private final DatePickerUtil datePickerUtil=new DatePickerUtil();
    Boolean addCharges = false;
    String labGroupName;
    String specimenName;
    String uomName;
    String labTestName;
    List<String> labTestNameBundle = new ArrayList<>();
    private String patientCode; // Stores the patient code
    private boolean isAppointmentCreated = false; // Flag to check if appointment is created
    private boolean isAppointmentCheckedIn = false; // Flag to check if appointment is checked in

    public LabFlow() {
        this.patientFlowHelper = new PatientFlowHelper();
        this.xPathUtil = new XPathUtil();
    }

    @DataProvider(name = "labTestData")
    public Iterator<Object[]> provideLabTestData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("lab_test_data.json");
        List<LabTestData> testDataList = mapper.readValue(inputStream, new TypeReference<List<LabTestData>>() {
        });
        return testDataList.stream()
                .map(data -> new Object[]{data})
                .iterator();
    }

    @Test(priority = 4, description = "Lab Master", dependsOnMethods = "testLogin", dataProvider = "labTestData")
    public void labMasterCreate(LabTestData data) {

        if (basicLabFlow) {
            menuPanelClick("Master", true, "Lab", "");

            labGroupName = data.getLabGroupName() + generateSequence(); // object

            specimenName = data.getSpecimenName() + generateSequence();

            String uomCode = data.getUomCode() + generateSequence();
            uomName = data.getUomName() + generateSequence();

            labTestName = data.getLabTestName() + generateSequence();

            threadTimer(2000);
            clickButtonElement(By.xpath("//a[@id='Test Group' and contains(@class, 'nav-link')]"));
            clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));


            //create TestGroup
            //fill the labGroupName

            xPathUtil.fillTextField("labGroupName", labGroupName, wait);
            threadTimer(1000);

            formSubmitWithFormId("labGroupForm");

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

            formSubmitWithFormId("specimenGroupForm");



            clickButtonElement(By.xpath("//a[@id='UOM' and contains(@class, 'nav-link')]"));
            clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));


            xPathUtil.fillTextField("uomCode", uomCode, wait);
            xPathUtil.fillTextField("uomName", uomName, wait);

            formSubmitWithFormId("chargesForm");


            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("location.reload()");
            threadTimer(2000);
            //lab text nav form

            clickButtonElement(By.xpath("//a[@id='Lab Test' and contains(@class, 'nav-link')]"));


            clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));


            xPathUtil.fillTextField("labTestName", labTestName, wait);
            xPathUtil.selectField("labGroupId", labGroupName, DropdownType.STANDARD, "", driver, wait);
            xPathUtil.selectField("labTypeId", data.getLabTypeId(), DropdownType.STANDARD, "", driver, wait);

            xPathUtil.optionSelect("formulary", data.getFormulary(), "", wait, driver);
            xPathUtil.optionSelect("nrtForTheDay", data.getNrtForTheDay(), "", wait, driver);
            xPathUtil.optionSelect("nrtForTheStay", data.getNrtForTheStay(), "", wait, driver);
            xPathUtil.fillTextField("routineDuration", data.getRoutineDuration(), wait);
            xPathUtil.fillTextField("statDuration", data.getStatDuration(), wait);

            xPathUtil.optionSelect("cultureTest", data.getCultureTest(), "", wait, driver);

            //   fillInputField("price","1000");
            xPathUtil.optionSelect("confidentialReport", data.getConfidentialReport(), "", wait, driver);

            xPathUtil.optionSelect("active", data.getActive(), "", wait, driver);

            xPathUtil.fillTextArea("testDescription", data.getTestDescription(), wait, driver);

            String packageOnly = data.getPackageOnly();
            xPathUtil.optionSelect("packageOnly", packageOnly, "", wait, driver);

            String performanceType = data.getPerformanceType();

            if (packageOnly.contains("Yes")) {
            } else {
                //performance type : =Inhouse and Outsource
                xPathUtil.optionSelect("performanceType", performanceType, "", wait, driver);

                if (performanceType.equals("Outsource")) {
                    //type :Billable  and Report only
                    xPathUtil.optionSelect("serviceType", data.getServiceType(), "", wait, driver);

                }
            }

            xPathUtil.selectField("Specimen Type", specimenName, DropdownType.ANGULAR_TITLE, "", driver, wait);
            xPathUtil.selectField("UOM", uomName, DropdownType.ANGULAR_TITLE, "", driver, wait);


            formSubmitWithFormId("labTestForm");
            labTestNameBundle.add(labTestName);

            if (performanceType.equals("Inhouse")) {

                WebElement row = wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//td[span[contains(text(),'" + labTestName + "')]]/parent::tr")
                )));

                row.findElement(By.xpath(".//button[@title='Add Result']")).click();

                threadTimer(2000);
                xPathUtil.selectField("sex", data.getSex(), DropdownType.STANDARD, "", driver, wait);
                fillTheTextFieldInTitle("Age (From)", data.getAgeFrom());
                xPathUtil.selectField("ageFromUnit", data.getAgeFromUnit(), DropdownType.STANDARD, "", driver, wait);

                fillTheTextFieldInTitle("Age (To)", data.getAgeTo());
                xPathUtil.selectField("ageToUnit", data.getAgeToUnit(), DropdownType.STANDARD, "", driver, wait);

                xPathUtil.optionSelect("resultHelpValue", data.getResultHelpValue(), "", wait, driver);

                String labResultTypeId = data.getLabResultTypeId();

                xPathUtil.selectField("labResultTypeId", labResultTypeId, DropdownType.STANDARD, "", driver, wait);

                if (labResultTypeId.equals("Range")) {
                    System.out.println("selected " + labResultTypeId);
                    xPathUtil.fillTextField("low", data.getLabResult().getLow(), wait);
                    xPathUtil.fillTextField("high", data.getLabResult().getHigh(), wait);
                    xPathUtil.selectField("uomId", uomName, DropdownType.STANDARD, "", driver, wait);
                    xPathUtil.fillTextField("criticallyLow", data.getLabResult().getCriticallyLow(), wait);
                    xPathUtil.fillTextField("criticallyHigh", data.getLabResult().getCriticallyHigh(), wait);
                    xPathUtil.fillTextField("defaultValue", data.getLabResult().getDefaultValue(), wait);
                    //xPathUtil.fillTextAreaField("Description","test description");

                } else if (labResultTypeId.equals("Multi-Range")) {
                    System.out.println("selected " + labResultTypeId);

                    Boolean abnormalEnable = data.getLabResult().getAbnormal();
                    WebElement abnormalCheckbox = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//label[contains(., 'Abnormal')]/following-sibling::input[@type='checkbox']")
                    ));


                    if (abnormalEnable) {
                        if (!abnormalCheckbox.isSelected()) {
                            abnormalCheckbox.click();
                        }
                    }
                    // Modern type-safe selector
                    xPathUtil.selectField("Range", data.getLabResult().getRange(), LabFlow.DropdownType.ANGULAR_TITLE, "", driver, wait);
                    fillTheTextFieldInTitle("Value", data.getLabResult().getResultValue());

                    fillTheTextFieldInTitle("Description", data.getLabResult().getDescription());

                    //selectWithOutFormNameUsingTitle("UOM", uomName);

                    xPathUtil.selectField("//*[@id='procedureForm']/div[2]/div[2]/div[4]/div/span/app-select/div/select", uomName, DropdownType.XPATH, "", driver, wait);
                } else if (labResultTypeId.equals("Descriptive")) {

                    System.out.println("selected " + labResultTypeId);

                    xPathUtil.fillTextArea("valueStr", data.getLabResult().getDescription(), wait, driver);

                    xPathUtil.selectField("//*[@id='procedureForm']/div[1]/div[7]/div[3]/app-select/div/select", uomName, DropdownType.XPATH, "", driver, wait);
                } else if (labResultTypeId.equals("Drop-Down")) {
                    System.out.println("selected " + labResultTypeId);
                    WebElement abnormalCheckbox = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//label[contains(., 'Abnormal')]/following-sibling::input[@type='checkbox']")
                    ));
                    Boolean abnormalEnable = data.getLabResult().getAbnormal();
                    if (abnormalEnable) {
                        if (!abnormalCheckbox.isSelected()) {
                            abnormalCheckbox.click();
                        }
                    }
                    fillTheTextFieldInTitle("Options", data.getLabResult().getOptions());

                    xPathUtil.selectField("UOM", uomName, DropdownType.FORM_ID, "procedureForm", driver, wait);
                }


                formSubmitWithFormId("procedureForm");

                threadTimer(2000);

            }
            addCharges = true;

        }
    }


    @Test(priority = 5, dataProvider = "labTestData")
    public void switchChargeTap(LabTestData data) {
        if (chargesFlow) {
            System.out.println("size:-- and data:--" + labTestNameBundle.toString());
            if (data.getIndex() == 0) {
                menuPanelClick("Master", true, "Charges", "");
            }
            clickButtonElement(By.xpath("//a[@id='Charges' and contains(@class, 'nav-link')]"));
            threadTimer(2000);

            WebElement row = wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//td[span[contains(text(),'" + labTestNameBundle.get(data.getIndex()) + "')]]/parent::tr")
            )));

            row.findElement(By.xpath(".//button[@title='Edit']")).click();

            String headerName = data.getHeaderName();
            fillMatOptionField("headerName", headerName);


            xPathUtil.selectField("uomId", data.getUomId(), DropdownType.STANDARD, "", driver, wait);
            xPathUtil.fillTextField("unitPrice", data.getUnitPrice(), wait);
            threadTimer(2000);
            clickButtonElement(By.xpath("//button[contains(text(),'Save & Close')]"));
            threadTimer(3000);

        }

    }

    @Test(priority = 6, description = "Enable auto patient code ")
    public void enableAutoGeneratedPatientCode() {
        threadTimer(2000);
        menuPanelClick("General", false, "", "");
        threadTimer(2000);
        // Wait for checkbox to be clickable (10-second timeout)
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement checkbox = driver.findElement(By.xpath("//li[span[text()='Patient Code Auto Generation']]//input[@type='checkbox']"));

        if (checkbox.isEnabled()) {
            boolean click = checkbox.isSelected();  // check state before click

            System.out.println("clicked " + click);
            // Perform click using JavaScript
            if (click) {
                System.out.println("Checkbox click was successful. State changed.");
            } else {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
                System.out.println("Checkbox click had no effect. State did not change.");
            }
        } else {
            System.out.println("Checkbox is disabled. Cannot click.");
        }

        WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(), 'Save') and contains(@class, 'saveNdClose')]"));
        saveButton.click();
        System.out.println("Save button clicked successfully.");

    }

    @Test(priority = 7)
    public void labFlowPatientRegisterToLabAdded() {
        if (basicPatientToCheckin) {
            JSONObject patient = tempPatientData.getJSONObject(5);

            // Register the patient and get the patient code
            patientCode = patientFlowHelper.patientRegisterTest(this, patient, driver, wait, "Patient Registration");
            System.out.println("OP Bill flow started for Patient Code: " + patientCode);

            // Navigate to the dashboard
            menuPanelClick("Dashboard", false, "", "");

            if (patientCode != null) {
                // Create an appointment for the patient
                isAppointmentCreated = patientFlowHelper.createAppointment(this, patient, driver, wait, "Create Appointment", patientCode);

                if (isAppointmentCreated) {
                    threadTimer(3000); // Wait for the appointment to be created

                    // Check in the appointment
                    isAppointmentCheckedIn = patientFlowHelper.checkingAppointmentTest(this, driver, wait, "View Appointments", patientCode);

                    if (isAppointmentCheckedIn) {
                        // Navigate to the Lab Flow
                        labFlow();
                        labTestResult();
                        paidFlow();
                    }


                }
            }
        }
    }

    private void labTestResult() {
        menuPanelClick("View Lab", false, "", "");

        filterSearchClick();
        filterSearchElemenet(patientCode, "Patient Code", "Text");
        threadTimer(2000);
        WebElement row = wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//td[span[contains(text(),'" + patientCode + "')]]/parent::tr")
        )));

        row.findElement(By.xpath(".//button[@title='View Test']")).click();

        try {
            // XPath to locate all result inputs in relevant tables
            String xpath =
                    "//table[.//th[contains(translate(., 'RESULT', 'result'), 'result')]]" +
                            "//tr[td]/td[3]//input[@type='text']  | " +  // Text input
                            "//tr[td]/td[3]//select";


            // Wait for all result inputs to be visible
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> resultInputs = wait.until(
                    ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpath))
            );

// Iterate through inputs and interact
            int i = 1;
            for (WebElement input : resultInputs) {
                // Get the associated test name (from the second column)
                System.out.println("Test:   Tag name" + input.getTagName());
                if (input.getTagName().equalsIgnoreCase("input")) {
                    if (input.isEnabled() && input.isDisplayed()) {
                        input.sendKeys("55" + i++);
                    }
                } else if (input.getTagName().equalsIgnoreCase("select")) {
                    // Handle dropdown (select index 1)
                    Select dropdown = new Select(input);
                    dropdown.selectByIndex(1); // Select second option (index 1)
                    System.out.println("Dropdown selected: " + dropdown.getFirstSelectedOption().getText());
                }
                WebElement resultInput = input.findElement(By.xpath(
                        "./ancestor::tr/td[1]//input[@type='checkbox' and not(@disabled)]"
                ));
                resultInput.click();
                // Enter a value into the input fieldS

            }
        } catch (Exception e) {

        }
        WebElement saveCloseButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Save')]")
        ));

        saveCloseButton.click();
    }

    private void paidFlow() {
        menuPanelClick("Lab", false, "", "");

        threadTimer(3000); // Wait for the page to load
        int discount = 50;
        // Get pagination details
        int totalPages = getPaginationDetails();
        System.out.println("Total Pages: " + totalPages);
        System.out.println("Patient Code: " + patientCode);

        filterSearchClick();
        filterSearchElemenet(patientCode, "Patient Code", "Text");

        System.out.println("Successfully selected");
        // Find the row with the patient code and process billing
        if (findRow(patientCode, "View Bill", "Success", totalPages)) {
            amountTabClick(); // Click the amount tab
            enterAmounts(discount); // Enter amounts
            threadTimer(3000); // Wait for the UI to update
            submitBilling(); // Submit the billing
        }

    }

    // Helper method to submit billing
    private void submitBilling() {
        clickButtonElement(By.xpath("//label[contains(text(), 'Remarks')]")); // Click remarks
        clickButtonElement(By.xpath("//button[contains(text(), 'Pay Bill')]")); // Click pay bill
        threadTimer(3000); // Wait for the UI to update
        clickButtonElement(By.xpath("//div[contains(@class, 'sa-confirm-button-container')]//button[contains(text(), 'Yes')]")); // Confirm payment
        threadTimer(5000); // Wait for the payment to process
        closePrintScreen(); // Close the print screen
    }


    private void enterAmounts(int discount) {
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
                        amountInput.sendKeys(String.valueOf(Math.round(Integer.parseInt(finalAmount))));
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

    private int getPaginationDetails() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String pageText = (String) js.executeScript("return document.querySelector('li.small-screen')?.textContent.trim();");

        if (pageText != null && !pageText.isEmpty()) {
            String[] pageParts = pageText.split("/");
            return Integer.parseInt(pageParts[1].trim());
        }
        return 1; // Default to 1 page if pagination is not found
    }

    private void labFlow() {
        menuPanelClick("Current Admissions", false, "", "");

        WebElement patientRow = findAndClickDropdownAndPrescription(patientCode, wait, driver, "Lab");

        if (patientRow != null) {
            System.out.println("Dropdown clicked successfully.");

            for (int i = 0; i < labTestNameBundle.size(); i++) {
                threadTimer(1000);
                WebElement addNewButton = driver.findElement(By.xpath("//div[contains(@class, 'addIcon-button')]/span[text()='Add New']"));
                addNewButton.click();
                // 1. Locate the input field inside the <td>

                WebElement td = driver.findElement(By.xpath("//td[@class='tbody1-al']"));

                WebElement inputField;
                System.out.println("find tag name list" + td.getTagName());

                try {
                    // Try to find <input> first
                    inputField = driver.findElement(By.xpath("//td[@class='tbody1-al']//input[@type='text']"));
                    inputField.sendKeys(labTestName);


// 3. Wait for autocomplete suggestions to appear and select the one you want
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(40));
                    WebElement suggestion = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//mat-option//span[contains(text(), '" + labTestNameBundle.get(i) + "')]") // match your suggestion
                    ));

// 4. Click the suggestion
                    suggestion.click();

                    System.out.println("Input field found and text entered.");
                } catch (InvalidArgumentException | NoSuchElementException e1) {
                    try {

                        WebElement matSelect = driver.findElement(By.xpath("//mat-select[@placeholder='Select']"));

                        matSelect.click();

                        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                                By.xpath("//mat-option//span[contains(text(), '" + labTestNameBundle.get(i) + "')]")
                        ));
                        option.click();

                        System.out.println("mat-select option selected successfully.");
                    } catch (NoSuchElementException e2) {
                        System.out.println();
                    }
                }

                System.out.println("Autocomplete suggestion selected.");

            }
            WebElement saveCloseButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector(".btn.btn-info.btn-sm.mr-2.saveNdClose.padng.ng-star-inserted")
            ));

            saveCloseButton.click();

        } else {
            System.out.println("Patient not found.");
        }
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


    private void formSubmitWithFormId(String id) {
        By saveCloseLocator = By.xpath("//form[@id='" + id + "']//button[contains(@class, 'saveNdClose') and normalize-space(.)='Save & Close']");

        clickButtonElement(saveCloseLocator);

        threadTimer(2000);
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


    


    String generateSequence() {
        String randomPart = "-" + new Random().nextInt(9999);
        return randomPart;
    }

    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public WebElement findAndClickDropdownAndPrescription(String patientCode, WebDriverWait wait, WebDriver driver, String dropDownName) {
        WebElement row = null;

        while (true) {
            try {
                // ✅ Step 1: Find the row containing the patient name
                row = wait.until(ExpectedConditions.refreshed(
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//td[span[contains(text(),'" + patientCode + "')]]/parent::tr"))
                ));
                System.out.println("Patient row found.");

                // ✅ Step 2: Find and click the dropdown inside this row
                WebElement dropdownIcon = row.findElement(By.xpath(".//span[contains(@class,'ti-angle-double-down')]"));
                wait.until(ExpectedConditions.elementToBeClickable(dropdownIcon)).click();
                System.out.println("Dropdown icon clicked successfully.");

                // ✅ Step 3: Wait for Prescription option to appear and scroll into view
                WebElement prescriptionOption = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//td[span[contains(text(),'" + patientCode + "')]]/following-sibling::td//span[contains(text(),'" + dropDownName + "')]")
                ));

                // ✅ Scroll into view (in case it's hidden)
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", prescriptionOption);
                Thread.sleep(500); // Small delay for UI adjustment

                // ✅ Attempt to click, fallback to JS click if necessary
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(prescriptionOption)).click();
                } catch (ElementClickInterceptedException e) {
                    System.out.println("Click intercepted, using JS click.");
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", prescriptionOption);
                }

                System.out.println("Clicked on Prescription option.");
                return row; // ✅ Return the row after successful action

            } catch (StaleElementReferenceException e) {
                System.out.println("StaleElementReferenceException caught. Retrying...");
            } catch (TimeoutException e) {
                // ✅ Step 4: Handle pagination (if patient not found on current page)
                List<WebElement> nextPageButton = driver.findElements(By.xpath("//li[@class='ng-star-inserted']/a/span[text()='2']"));
                if (!nextPageButton.isEmpty()) {
                    System.out.println("Patient not found, navigating to the next page...");
                    nextPageButton.get(0).click();
                    wait.until(ExpectedConditions.stalenessOf(nextPageButton.get(0))); // Wait for page reload
                } else {
                    System.out.println("Patient not found on any page.");
                    return null; // Exit if no more pages
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public enum DropdownType {
        STANDARD,       // HTML <select> with formControlName
        ANGULAR_TITLE,  // Angular component with title attribute
        MATERIAL,       // Material-UI <mat-select>
        XPATH,
        NORMAL_SELECT,
        NG_SELECT,
        FORM_ID,// Custom XPath locator
    }
}
