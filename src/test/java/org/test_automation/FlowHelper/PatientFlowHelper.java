package org.test_automation.FlowHelper;

import org.test_automation.BaseTest;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.test_automation.DBConnectivity.MenuUtils;
import org.test_automation.DBConnectivity.XPathUtil;
import org.test_automation.VO.BedAllocation;
import org.testng.Assert;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class PatientFlowHelper {

    private final XPathUtil xPathUtil = new XPathUtil();

    private final MenuUtils menuUtils=new MenuUtils();

    public String patientRegisterTest(BaseTest baseTest, JSONObject patient, WebDriver driver, WebDriverWait wait, String panel) {

        String name = patient.getString("patientName");
        String age = patient.getString("patientAge");
        String phone = patient.getString("patientPhone");
        String gender = patient.getString("gender");
        String patientCode;
        menuUtils.menuPanelClick(panel, false, "", "",driver,wait);
        try {

            patientFormSubmit(driver, wait);
            errorMessageHandle(driver, wait);
            fillInputField(driver, wait, "firstName", name);
            patientFormSubmit(driver, wait);
            errorMessageHandle(driver, wait);
            fillInputField(driver, wait, "age", age);
            patientFormSubmit(driver, wait);
            errorMessageHandle(driver, wait);
            fillInputField(driver, wait, "phoneNumber", phone);
            patientFormSubmit(driver, wait);
            errorMessageHandle(driver, wait);
            selectRadioButton(driver, wait, "gender", gender);
            patientFormSubmit(driver, wait);
            errorMessageHandle(driver, wait);

            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            selectFromMatSelectDropdown(driver, wait, "Select", "Chennai");


            patientFormSubmit(driver, wait);

            //500 error handle and failed backend connection and deployment scerio etc
            String messageText = handleRuntimeError("Patient Registeration", wait);

            if (messageText.contains("New Patient")) {
                patientCode = messageText.replace("New Patient", "")
                        .replace("Registered Successfully", "")
                        .trim();

                System.out.println("Extracted Code: " + patientCode);
            } else {
                patientCode = null;
            }
        } catch (Exception e) {
            patientCode = null;
        }
        return patientCode;
    }

    private void patientFormSubmit(WebDriver driver, WebDriverWait wait) {
        WebElement submitButton = wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Submit')]"))));
        submitButton.click();

    }

    private void errorMessageHandle(WebDriver driver, WebDriverWait wait) {
        WebElement errorMessage = wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'error-msg')]")
        )));

        highlightElement(driver, errorMessage);

        String errorText = errorMessage.getText().trim();
        //  System.out.println("Error!:" + errorText);
    }

    private void fillInputField(WebDriver driver, WebDriverWait wait, String formControlName, String value) {

        WebElement inputField = wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@formcontrolname='" + formControlName + "']")
        )));
        inputField.clear();
        inputField.sendKeys(value);
        //   System.out.println("Filled " + formControlName + " with value: " + value);
    }

    private void selectRadioButton(WebDriver driver, WebDriverWait wait, String formControlName, String value) {
        wait = new WebDriverWait(driver, Duration.ofSeconds(45));
        JavascriptExecutor js1 = (JavascriptExecutor) driver;
        WebElement element = driver.findElement(By.xpath("//input[@formcontrolname='" + formControlName + "' and @value='" + value + "']"));
        js1.executeScript("arguments[0].click();", element);

    }

    private void highlightElement(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.border='3px solid red';", element);
        //  System.out.println("Highlighted the error message element.");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


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

    public Boolean createAppointment(BaseTest baseTest, JSONObject patient, WebDriver driver, WebDriverWait wait, String panel, String patientCode) {

        String name = patient.getString("patientName");
        String admissionType = patient.getString("admissionType");
        String doctorName = patient.getString("doctorName");
        String scanType = patient.getString("scanType");
        menuUtils.menuPanelClick(panel, false, "", "",driver,wait);

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

            baseTest.threadTimer(1000);

            patientCodeInput.sendKeys(Keys.BACK_SPACE);
            baseTest.threadTimer(500);
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

            WebElement purposeDropdown = wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("select[formcontrolname='purpose']")
            )));

            Select selectPurpse = new Select(purposeDropdown);
            selectPurpse.selectByVisibleText(admissionType);


            if (admissionType.equals("Scan")) {
                WebElement dropdown = wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(By.xpath("//select[@formcontrolname='scanType']"))));
                Select selectScan = new Select(dropdown);
                selectScan.selectByVisibleText(scanType);
            }


            WebElement selectDoctorId = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("select[formcontrolname='doctorId']")
            ));

            Select selectDr = new Select(selectDoctorId);
            try {
                selectDr.selectByVisibleText(doctorName);
            } catch (Exception e) {
                selectDr.selectByIndex(1);
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            WebElement saveButton = driver.findElement(By.id("saveNdCloseAp"));
            saveButton.click();

            // Get all <p> elements inside the container
            // Locate the <p> element containing the message
            wait = new WebDriverWait(driver, Duration.ofSeconds(5));


            WebElement resultElement = wait.until(new Function<WebDriver, WebElement>() {
                @Override
                public WebElement apply(WebDriver driver) {
                    List<By> locators = Arrays.asList(
                            By.xpath("//div[contains(@class, 'toast-right-top')]//p[contains(text(), 'Appointment Already Created')]"),
                            By.xpath("//div[contains(@class, 'toast-right-top')]//p[contains(text(), 'Appointment Saved Successfully')]")
                    );

                    for (By locator : locators) {
                        List<WebElement> elements = driver.findElements(locator);
                        if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                            return elements.get(0);
                        }
                    }
                    return null;
                }
            });

            if (resultElement != null) {
                String resultText = resultElement.getText().trim();
                if (resultText.contains("Saved Successfully")) {
                    return true;
                } else {

                    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    WebElement closeButton = driver.findElement(By.id("CloseAp"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", closeButton);
                    try {
                        Thread.sleep(500); // Small delay after scrolling
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    closeButton.click();
                    return false;
                }

            }

            baseTest.threadTimer(3000);
        }

        return false;
    }


    public boolean checkingAppointmentTest(BaseTest baseTest, WebDriver driver, WebDriverWait wait, String panel, String patientCode) {

        try {
            menuUtils.menuPanelClick(panel, false, "", "",driver,wait);
            WebElement row = wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//td[span[contains(text(),'" + patientCode + "')]]/parent::tr")
            )));

            row.findElement(By.xpath(".//button[@title='Check In']")).click();
            return true;
        } catch (TimeoutException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean createAdmission(BaseTest baseTest, JSONObject patient, WebDriver driver, WebDriverWait wait, String panel, String patientCode, Boolean bedAllocationFlag, BedAllocation bedAllocation) {
        menuUtils.menuPanelClick(panel, false, "", "",driver,wait);

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

            baseTest.threadTimer(1000);

            patientCodeInput.sendKeys(Keys.BACK_SPACE);
            baseTest.threadTimer(500);
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

            xPathUtil.fillTextField("poa", "new registeration ", wait);


            if (bedAllocationFlag) {

                xPathUtil.optionSelect("bedAllocate", "Yes", "", wait, driver);


                baseTest.threadTimer(2000);
                xPathUtil.selectField("roomTypeId", bedAllocation.getRoomType(), XPathUtil.DropdownType.STANDARD, "", driver, wait);

                baseTest.threadTimer(2000);
                xPathUtil.selectField("roomId", bedAllocation.getRoomNo(), XPathUtil.DropdownType.STANDARD, "", driver, wait);
                baseTest.threadTimer(2000);


                xPathUtil.selectField("bedId", bedAllocation.getBedNo(), XPathUtil.DropdownType.STANDARD, "", driver, wait);

            }

            WebElement selectDoctorId = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("select[formcontrolname='doctorId']")
            ));

            String doctorName = null;
            Select selectDr = new Select(selectDoctorId);
            try {
                if (doctorName != null) {
                    selectDr.selectByVisibleText(doctorName);
                } else {
                    selectDr.selectByIndex(1);
                }
            } catch (Exception e) {
                selectDr.selectByIndex(1);
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            xPathUtil.clickButtonElement(By.xpath("(//button[contains(text(),'Admit')])"),driver,wait);
        }


        return true;
    }
}
