package org.test_automation.PharmacyMaster;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.test_automation.DBConnectivity.MenuUtils;
import org.test_automation.Listener.AllTestListener;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Listeners(AllTestListener.class)
public class SuppilerTestScenario extends LoginAndLocationTest {

    private final MenuUtils menuUtils=new MenuUtils();
    private final static HashMap<String, String> treeMap = new HashMap<>();

    @DataProvider(name = "supplierDataProvider")
    public Object[][] getSupplierDataProvider() {
        return new Object[][]{
                // Scenario 1: All fields filled
                {"AllFieldsFilled", createSupplierData(
                        generateRandomNumber("Supplier"), "example.com",
                        generateRandomNumber("SUP"), generateRondamNumber("DLL"),
                        "+91", "9791310502", "1234567890", "GST123456",
                        "123 Main St", "77 west street", "Tamil Nadu", "Kumbakonam", "900012",
                        "www.example.com", "John Doe", "+91", "9791310502"
                ), "Success"},

                // Scenario 2: Non-Mandatory Fields
                {"NonMandatoryFields", createSupplierData(
                        null, "example.com", null, null, null, null, "1234567890", "GST123456",
                        "123 Main St", "77 west street", null, null, "900012",
                        "www.example.com", "John Doe", "+91", "9791310502"
                ), "Supplier Code is required"},

                // Scenario 3: Supplier Code null
                {"SupplierCodeNull", createSupplierData(
                        generateRandomNumber("Medicine"), "example.com", null, generateRandomNumber("DL"),
                        "+91", "9791310502", "1234567890", "GST123456",
                        "123 Main St", "77 west street", "Tamil Nadu", "Kumbakonam", "900012",
                        "www.example.com", "John Doe", "+91", "9791310502"
                ), "Supplier Code is required"},

                // Scenario 4: Supplier Name Null
                {"SupplierNameNull", createSupplierData(
                        null, "example.com", generateRandomNumber("SUP"), generateRandomNumber("DL"), "+91", "9791310502", "1234567890", "GST123456",
                        "123 Main St", "77 west street", "Tamil Nadu", "Kumbakonam", "900012",
                        "www.example.com", "John Doe", "+91", "9791310502"
                ), "Supplier Name is required"},

                // Scenario 5: Drug License Null
                {"DrugLicenseNull", createSupplierData(
                        generateRandomNumber("Medicine Supplier"), "example.com", generateRandomNumber("CD"), null, "+91", "9791310502", "1234567890", "GST123456",
                        "123 Main St", "77 west street", "Tamil Nadu", "Kumbakonam", "900012",
                        "www.example.com", "John Doe", "+91", "9791310502"
                ), "Drug License is required"},

                // Scenario 6: Phone Number Null
                {"PhoneNumberNull", createSupplierData(
                        generateRandomNumber("Supplier"), "example.com",
                        generateRandomNumber("SUP"), generateRondamNumber("DL"), null, null, "1234567890", "GST123456",
                        "123 Main St", "77 west street", "Tamil Nadu", "Kumbakonam", "900012",
                        "www.example.com", "John Doe", "+91", "9791310502"
                ), "Supplier Phone No. is required"},

                // Scenario 7: State Null
                {"StateNull", createSupplierData(
                        generateRandomNumber("Supplier"), "example.com",
                        generateRandomNumber("SUP"), generateRondamNumber("DL"), "+91", "9791310502", "1234567890", "GST123456",
                        "123 Main St", "77 west street", null, "Kumbakonam", "900012",
                        "www.example.com", "John Doe", "+91", "9791310502"
                ), "State is required"},

                // Scenario 8: City Null
                {"CityNull", createSupplierData(
                        generateRandomNumber("Supplier"), "example.com",
                        generateRandomNumber("SUP"), generateRondamNumber("DL"),
                        "+91", "9791310502", "1234567890", "GST123456",
                        "123 Main St", "77 west street", "Tamil Nadu", null, "900012",
                        "www.example.com", "John Doe", "+91", "9791310502"
                ), "City is required"},

                // Scenario 9: Supplier Code Already Exists
                {"SupplierCodeExists", createSupplierData(
                        generateRandomNumber("SUP"), "example.com",
                        "SUP-20250314-188", generateRondamNumber("DL"),
                        "+91", "9791310502", "1234567890", "GST123456",
                        "123 Main St", "77 west street", "Tamil Nadu", "kumbaknam", "900012",
                        "www.example.com", "John Doe", "+91", "9791310502"
                ), "Already Exists"}
        };
    }

    // Helper method to create supplier data
    private JsonNode createSupplierData(
            String supplierNamePrefix, String emailDomain, String supplierCodePrefix, String drugLicensePrefix,
            String phoneCountryCode, String phoneNumber, String fax, String gstNumber, String street, String location, String state,
            String city, String zipCode, String website, String pointOfContactName, String pointOfContactCountryCode,
            String pointOfContactNumber
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode supplierData = objectMapper.createObjectNode();

        supplierData.put("supplierNamePrefix", supplierNamePrefix);
        supplierData.put("emailDomain", emailDomain);
        supplierData.put("supplierCodePrefix", supplierCodePrefix);
        supplierData.put("drugLicensePrefix", drugLicensePrefix);

        ObjectNode phone = objectMapper.createObjectNode();
        phone.put("countryCode", phoneCountryCode);
        phone.put("phoneNumber", phoneNumber);
        supplierData.set("phone", phone);

        supplierData.put("fax", fax);
        supplierData.put("gstNumber", gstNumber);

        ObjectNode address = objectMapper.createObjectNode();
        address.put("street", street);
        address.put("location", location);
        address.put("state", state);
        address.put("city", city);
        address.put("zipCode", zipCode);
        supplierData.set("address", address);

        supplierData.put("website", website);

        ObjectNode pointOfContact = objectMapper.createObjectNode();
        pointOfContact.put("name", pointOfContactName);

        ObjectNode pointOfContactPhone = objectMapper.createObjectNode();
        pointOfContactPhone.put("countryCode", pointOfContactCountryCode);
        pointOfContactPhone.put("pointPhNumber", pointOfContactNumber);
        pointOfContact.set("phone", pointOfContactPhone);
        supplierData.set("pointOfContact", pointOfContact);

        System.out.println("Supplier Data: " + supplierData);
        return supplierData;
    }

    @Test(priority = 3)
    public void testMenuOpen() {
        if (isLoginSuccessful) {
            menuUtils.menuPanelClick("Master", true, "Pharmacy", "",driver,wait);
        }
    }

    @BeforeClass
    public void printTestScenarios() {
        Object[][] testData = getSupplierDataProvider();
        System.out.println("=== Supplier Creation Test Scenarios ===");
        for (Object[] data : testData) {
            System.out.println("Scenario: " + data[0] + " | Data: " + data[1] + " | Expected Result: " + data[2]);
        }
    }

    @Test(priority = 3, dataProvider = "supplierDataProvider")
    public void testSupplierCreation(String scenarioName, JsonNode supplierData, String expectedResult) {
        System.out.println("Running scenario: " + scenarioName);

        // Attempt to create the supplier
        String response = addSupplier(supplierData);

        // Assert the result based on the expected outcome
        if (response != null && ((response.equals("error") || !response.contains(expectedResult)))) {
            Assert.fail("Supplier creation failed when it was expected to pass."+scenarioName);
        } else {
            System.out.println("Test Passed " + scenarioName);
        }
    }

    private void closePanel() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            By closeButtonLocator = By.cssSelector("button[aria-label='Close']");
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(closeButtonLocator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", closeButton);
            closeButton.click();
            System.out.println("Close button clicked successfully.");
        } catch (Exception e) {
            System.out.println("Failed to close the panel: " + e.getMessage());
        }
    }

    // Helper method to add a supplier
    public String addSupplier(JsonNode supplierData) {
        menuUtils.verifyPanelName("Pharmacy Master",wait);
        clickElement(By.xpath("//a[@id='Supplier' and contains(@class, 'nav-link')]"));
        clickElement(By.xpath("//button[contains(text(),'Add New')]"));

        // Print the supplier name prefix for debugging purposes
        System.out.println("data:- supplier name prefix:--" + supplierData.get("supplierNamePrefix").asText());

        // Check if the supplier name prefix is not null and not empty
        String supplierNamePrefix = supplierData.get("supplierNamePrefix").asText();
        if (nullValidation(supplierNamePrefix)) {
            enterText(By.cssSelector("input[title='Supplier Name']"), supplierNamePrefix, true);
            String email = generateEmail(supplierNamePrefix, supplierData.get("emailDomain").asText());
            enterText(By.cssSelector("input[formcontrolname='email']"), email, true);
        } else {
            System.out.println("Supplier Name Prefix is null or empty. Skipping supplier name and email generation.");
        }

        // Initialize a variable to store the supplier code
        String supplierCode = null;
        String supplierCodePrefix = supplierData.get("supplierCodePrefix").asText();
        if (nullValidation(supplierCodePrefix)) {
            supplierCode = supplierData.get("supplierCodePrefix").asText();
            enterText(By.cssSelector("input[formcontrolname='code']"), supplierCode, true);
        }

        // Extract the drug license prefix
        String drugPref = supplierData.get("drugLicensePrefix").asText();
        if (nullValidation(drugPref)) {
            enterText(By.cssSelector("input[formcontrolname='drugLiscense']"), supplierData.get("drugLicensePrefix").asText(), true);
        }

        // Enter the phone country code into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='phoneCode']"), supplierData.get("phone").get("countryCode").asText(), true);

        // Enter the phone number into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='phoneNumber']"), supplierData.get("phone").get("phoneNumber").asText(), true);

        // Enter the fax number into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='fax']"), supplierData.get("fax").asText(), true);

        // Enter the GST number into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='gst']"), supplierData.get("gstNumber").asText(), true);

        // Enter the street address into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='address']"), supplierData.get("address").get("street").asText(), true);

        // Enter the location into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='location']"), supplierData.get("address").get("location").asText(), true);

        // Select the state from a dropdown or similar field
        selectField("state", supplierData.get("address").get("state").asText());

        // Enter the city into the corresponding input field
        if (nullValidation(supplierData.get("address").get("city").asText())) {
            enterText(By.cssSelector("input[formcontrolname='city']"), supplierData.get("address").get("city").asText(), true);
        }

        // Enter the zip code into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='zipCode']"), supplierData.get("address").get("zipCode").asText(), true);

        // Enter the website URL into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='website']"), supplierData.get("website").asText(), true);

        // Enter the point of contact name into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='pointOfContactName']"), supplierData.get("pointOfContact").get("name").asText(), true);

        // Enter the point of contact phone country code into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='pointOfContactPhoneCode']"), supplierData.get("pointOfContact").get("phone").get("countryCode").asText(), true);

        // Enter the point of contact phone number into the corresponding input field
        enterText(By.cssSelector("input[formcontrolname='pointOfContactNumber']"), supplierData.get("pointOfContact").get("phone").get("pointPhNumber").asText(), true);

        // Click the "Save & Close" button to submit the form
        clickElement(By.xpath("//button[contains(text(),'Save & Close')]"));
        threadTimer(500);


        StringBuilder warningMessage=new StringBuilder();
        for (WebElement item : getMandatoryFields()) {
            warningMessage.append(item.getText()+",");
        }
        if (getMandatoryFields().size() == 0) {
            String backendResponse=warningMessagePurchase().getText();
            System.out.println("backend response"+backendResponse);
            if (backendResponse!= null && backendResponse.contains("Successfully")) {
                return backendResponse;
            } else if (backendResponse != null && backendResponse.contains("Already Exists")) {
                closePanel();
                return backendResponse;
            } else if(backendResponse.contains("Error")) {
                closePanel();
                return "error";
            }
            else {
                closePanel();
                return "error";
            }
        } else {
            closePanel();
            return warningMessage.toString();
        }
    }

    // Helper method to validate if a value is not null and not empty
    private boolean nullValidation(String value) {
        return value != null && !value.isEmpty() && !value.equals("null") && !value.equals("Null");
    }

    // Helper method to enter text into a field
    private void enterText(By locator, String text, boolean editable) {
        WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        inputField.clear();
        inputField.sendKeys(text);
        threadTimer(500);
    }

    // Helper method to generate a random number with a prefix
    public String generateRondamNumber(String prefix) {
        String datePart = Instant.now().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomPart = 100 + new Random().nextInt(900);
        String generateNumber = prefix + "-" + datePart + "-" + randomPart;
        System.out.println("Generated Number: " + generateNumber);
        return generateNumber;
    }

    // Helper method to select a value from a dropdown
    private void selectField(String title, String value) {
        if (nullValidation(value)) {
            WebElement titleDropdown = driver.findElement(By.cssSelector("select[formcontrolname='" + title + "']"));
            Select select = new Select(titleDropdown);
            select.selectByVisibleText(value);
            threadTimer(500);
        }
    }

    // Helper method to generate a random number with a prefix
    public String generateRandomNumber(String prefix) {
        String datePart = Instant.now().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomPart = 100 + new Random().nextInt(900);
        String generateNumber = prefix + "-" + datePart + "-" + randomPart;
        System.out.println("Generated Number: " + generateNumber);
        return generateNumber;
    }

    // Helper method to generate a phone number
    public String generatePhoneNumber() {
        Random random = new Random();
        int firstDigit = 7 + random.nextInt(3);
        StringBuilder phoneNumber = new StringBuilder();
        phoneNumber.append(firstDigit);
        for (int i = 0; i < 9; i++) {
            phoneNumber.append(random.nextInt(10));
        }
        return phoneNumber.toString();
    }

    // Helper method to generate an email
    public String generateEmail(String supplierName, String emailDomain) {
        Random random = new Random();
        String formattedName = supplierName.toLowerCase().replace(" ", "").replaceAll("[^a-zA-Z0-9]", "");
        String[] domains = {"supplier.com", "traders.net", "business.org"};
        return formattedName + random.nextInt(1000) + "@" + domains[random.nextInt(domains.length)];
    }
    
    // Helper method to print error messages
    private String printErrorMessage(List<WebElement> errorMessages) {
        StringBuilder allErrors = new StringBuilder();
        for (WebElement error : errorMessages) {
            if (!error.getText().trim().isEmpty()) {
                allErrors.append(error.getText()).append(" | ");
            }
        }
        String errorString = allErrors.toString();
        if (errorString.endsWith(" | ")) {
            errorString = errorString.substring(0, errorString.length() - 3);
        }
        System.out.println("All Error Messages: " + errorString);
        return errorString;
    }

    // Helper method to clear all fields
    public void clearAllFields() {
        List<By> fields = List.of(
                By.cssSelector("input[formcontrolname='email']"),
                By.cssSelector("input[formcontrolname='code']"),
                By.cssSelector("input[formcontrolname='drugLiscense']"),
                By.cssSelector("input[formcontrolname='phoneCode']"),
                By.cssSelector("input[formcontrolname='phoneNumber']"),
                By.cssSelector("input[formcontrolname='fax']"),
                By.cssSelector("input[formcontrolname='gst']"),
                By.cssSelector("input[formcontrolname='address']"),
                By.cssSelector("input[formcontrolname='location']"),
                By.cssSelector("input[formcontrolname='city']"),
                By.cssSelector("input[formcontrolname='zipCode']"),
                By.cssSelector("input[formcontrolname='website']"),
                By.cssSelector("input[formcontrolname='pointOfContactName']"),
                By.cssSelector("input[formcontrolname='pointOfContactPhoneCode']"),
                By.cssSelector("input[formcontrolname='pointOfContactNumber']"),
                By.cssSelector("input[title='Supplier Name']")
        );

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        for (By field : fields) {
            try {
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(field));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
                if (element.isEnabled()) {
                    element.clear();
                } else {
                    System.out.println("Element is disabled: " + field);
                }
            } catch (Exception e) {
                System.out.println("Failed to interact with element: " + field);
                e.printStackTrace();
            }
        }

        try {
            WebElement stateDropdown = driver.findElement(By.cssSelector("select[formcontrolname='state']"));
            Select select = new Select(stateDropdown);
            select.selectByValue("null");
        } catch (Exception e) {
            System.out.println("Failed to interact with state dropdown");
            e.printStackTrace();
        }
    }



    private void clickElement(By locator) {

        threadTimer(500);
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
        } catch (Exception e) {
            System.out.println("Normal click failed, using JavaScript click...");

        }

    }
}