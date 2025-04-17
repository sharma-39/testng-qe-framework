package org.test_automation.FlowHelper;

import com.fasterxml.jackson.databind.JsonNode;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.test_automation.BaseTest;
import org.test_automation.DBConnectivity.MenuUtils;
import org.test_automation.DBConnectivity.XPathUtil;
import org.test_automation.SupplierDTO;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ReturnHelper {
    private final MenuUtils menuUtils = new MenuUtils();
    private final XPathUtil xPathUtil=new XPathUtil();

    public void createSalesReturnBill(BaseTest baseTest, WebDriver driver, WebDriverWait wait, String parentPanel, String childPanel, String billNumber) {


        System.out.println("pharmacy bills----");

        menuUtils.menuPanelClick(parentPanel, true, childPanel, "", driver, wait);

        xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Add Return')]"),driver,wait);


        baseTest.threadTimer(1500);

        selectBillNumber(wait, driver, billNumber);


        baseTest.threadTimer(2000);

        List<WebElement> drugNameElements = driver.findElements(
                By.cssSelector("tbody > tr:not([style*='border-bottom']) > td.tbody1-al.ng-star-inserted[title='']")
        );

        List<String> drugNames = drugNameElements.stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());

        System.out.println("Drug Names: " + drugNames);

        for (int i = 0; i < drugNames.size(); i++) {

            WebElement drugRow = driver.findElement(
                    By.xpath("//tr[.//td[contains(@class,'ng-star-inserted') and normalize-space()='" + drugNames.get(i) + "']]")
            );

// 2. Locate the checkbox inside that row
            WebElement checkbox = drugRow.findElement(
                    By.xpath(".//input[@type='checkbox']")
            );

// 3. Check the checkbox (if not already checked)
            if (!checkbox.isSelected()) {
                checkbox.click();
                System.out.println("Checkbox for PANTAGOLD 40MG is now checked.");
            } else {
                System.out.println("Checkbox was already checked.");
            }

            WebElement returnQtyInput = drugRow.findElement(
                    By.xpath(".//input[@title='Return Qty']")
            );

// 3. Remove the 'readonly' attribute (if present) using JavaScript
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].removeAttribute('readonly');", returnQtyInput
            );

// 4. Clear any existing value and enter the desired quantity (e.g., "5")
            returnQtyInput.clear();
            returnQtyInput.sendKeys("5");


        }
        xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Return')]"),driver,wait);


        xPathUtil.closePrintScreen();
    }


    // Helper method to select a bill by code
    private void selectBillNumber(WebDriverWait wait, WebDriver driver, String billNumber) {
        WebElement dropdown1 = driver.findElement(By.xpath("//select[contains(@class, 'form-control')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='billNumber';", dropdown1);
        ((JavascriptExecutor) driver).executeScript("arguments[0].dispatchEvent(new Event('change'));", dropdown1);

        WebElement number = wait.until(ExpectedConditions.elementToBeClickable(By.name("patientCode")));
        number.click();
        number.sendKeys(Keys.BACK_SPACE);
        number.sendKeys(billNumber);

        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//mat-option")));
        boolean found = false;
        for (WebElement option : options) {
            if (option.getText().contains(billNumber)) {
                option.click();
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("billNumber not found in dropdown.");
        }
    }

    //create supplier
    public String addSupplier(BaseTest baseTest, SupplierDTO data, WebDriverWait wait, WebDriver driver) {
        menuUtils.verifyPanelName("Pharmacy Master", wait);
        System.out.println("Successfully loaded Pharmacy Master");

        clickElement(By.xpath("//a[@id='Supplier' and contains(@class, 'nav-link')]"), wait);
        clickElement(By.xpath("//button[contains(text(),'Add New')]"), wait);

        // Generate dynamic data
        String supplierName = data.getSupplierNamePrefix() + " " + new Random().nextInt(100);
        String phoneNumber = generatePhoneNumber();
        String email = generateEmail(supplierName, data.getEmailDomain());
        String supplierCode = generateRandomNumber(data.getSupplierCodePrefix());

        // Fill the form
        enterText(By.cssSelector("input[formcontrolname='code']"), supplierCode, true, wait);
        enterText(By.cssSelector("input[title='Supplier Name']"), supplierName, true, wait);
        enterText(By.cssSelector("input[formcontrolname='drugLiscense']"),
                generateRandomNumber(data.getDrugLicensePrefix()), true, wait);
        enterText(By.cssSelector("input[formcontrolname='phoneCode']"),
                data.getPhone().getCountryCode(), true, wait);
        enterText(By.cssSelector("input[formcontrolname='phoneNumber']"), phoneNumber, true, wait);
        enterText(By.cssSelector("input[formcontrolname='email']"), email, true, wait);
        enterText(By.cssSelector("input[formcontrolname='fax']"), data.getFax(), true, wait);
        enterText(By.cssSelector("input[formcontrolname='gst']"), data.getGstNumber(), true, wait);
        enterText(By.cssSelector("input[formcontrolname='address']"), data.getAddress().getStreet(), true, wait);
        enterText(By.cssSelector("input[formcontrolname='location']"), data.getAddress().getLocation(), true, wait);
        selectField("state", data.getAddress().getState(), driver);
        enterText(By.cssSelector("input[formcontrolname='city']"), data.getAddress().getCity(), true, wait);
        enterText(By.cssSelector("input[formcontrolname='zipCode']"), data.getAddress().getZipCode(), true, wait);
        enterText(By.cssSelector("input[formcontrolname='website']"), data.getWebsite(), true, wait);
        enterText(By.cssSelector("input[formcontrolname='pointOfContactName']"),
                data.getPointOfContact().getName(), true, wait);
        enterText(By.cssSelector("input[formcontrolname='pointOfContactPhoneCode']"),
                data.getPointOfContact().getPhone().getCountryCode(), true, wait);
        enterText(By.cssSelector("input[formcontrolname='pointOfContactNumber']"), generatePhoneNumber(), true, wait);

        clickElement(By.xpath("//button[contains(text(),'Save & Close')]"), wait);
        return supplierName;
    }

    private void clickElement(By locator, WebDriverWait wait) {

        threadTimer(500);
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            if (element.isEnabled()) {
                element.click();
            }
        } catch (Exception e) {
            System.out.println("Normal click failed, using JavaScript click...");

        }

    }

    private void threadTimer(int i) {
        try {
            Thread.sleep(i);
        } catch (Exception e) {

        }
    }

    // Helper method to validate if a value is not null and not empty
    private boolean nullValidation(String value) {
        return value != null && !value.isEmpty() && !value.equals("null") && !value.equals("Null");
    }

    // Helper method to enter text into a field
    private void enterText(By locator, String text, boolean editable, WebDriverWait wait) {
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
    private void selectField(String title, String value, WebDriver driver) {
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


    public void createPurchaseReturnBill(BaseTest baseTest, WebDriver driver, WebDriverWait wait, String parentPanel, String childPanel, String supplierName, JsonNode jsonNode) {

        menuUtils.menuPanelClick(parentPanel, true, childPanel, "", driver, wait);

        xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Add Return')]"),driver,wait);


        baseTest.threadTimer(1500);

        WebElement number = wait.until(ExpectedConditions.elementToBeClickable(By.name("patientCode")));
        number.click();
        number.sendKeys(Keys.BACK_SPACE);
        number.sendKeys(supplierName);

        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//mat-option")));
        boolean found = false;
        for (WebElement option : options) {
            if (option.getText().contains(supplierName)) {
                option.click();
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("billNumber not found in dropdown.");
        }

        System.out.println("inside function" + jsonNode.toPrettyString());
        for (JsonNode item : jsonNode) {
            // Add null checks for all accessed fields
            String name = item.path("name").asText(); // Safe access
            String desc = item.path("description").asText();
            System.out.println("itemname" + name);
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Item name is required");
            }
            xPathUtil.clickButtonElement(By.xpath(".//button[@title='Add New']"),driver,wait);
            selectAutoCompleteItem(By.xpath("//input[@formcontrolname='itemName']"),
                    name,
                    desc, true, baseTest, wait, driver, "auto");

            threadTimer(3000);
            enterQtyText(By.xpath("//input[@type='number' and @title='Return Qty']"), "50", false, wait, driver);

            threadTimer(1000);
            enterQtyText(By.xpath("//input[@type='number' and @title='Return Net Amount']"), "100", false, wait, driver);

            threadTimer(3000);
            if (baseTest.getMandatoryFields().size() == 0) {
                clickElement(By.xpath("//button[contains(text(),'Save & Close')]"), wait);

            } else {
                System.out.println("Already paid this item and return ");
                xPathUtil.clickButtonElement(By.xpath("//button[contains(text(), 'Close') and not(contains(text(), 'Save'))]"),driver,wait);
            }

        }

        clickElement(By.xpath("//button[contains(text(),'Return')]"), wait);


    }

    private void enterQtyText(By xpath, String number, boolean b, WebDriverWait wait, WebDriver driver) {

        WebElement qtyInput = driver.findElement(xpath);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        qtyInput.clear();
        qtyInput.sendKeys(number);
        qtyInput.sendKeys(Keys.TAB);
    }

    private void selectAutoCompleteItem(By inputLocator, String inputText, String value, boolean editable, BaseTest baseTest, WebDriverWait wait, WebDriver driver, String custom) {
        boolean optionFound = false;


        while (!optionFound) {
            WebElement inputField = wait.until(ExpectedConditions.elementToBeClickable(inputLocator));
            inputField.clear();
            inputField.click();
            inputField.sendKeys(inputText);
            System.out.println("input text" + inputText);
            try {


                baseTest.threadTimer(1000);

                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//mat-option")));

                // Re-fetch options to avoid StaleElementReferenceException
                List<WebElement> options = driver.findElements(By.xpath("//mat-option"));


                for (WebElement option : options) {
                    System.out.println("get Text" + option.getText());

                    if (custom.equals("auto") && option.getText().trim().equalsIgnoreCase(value)) {
                        wait.until(ExpectedConditions.elementToBeClickable(option)).click(); // Ensure element is clickable
                        System.out.println("Selected: " + option.getText());
                        break;
                    }

                    if (custom.equals("custom")) {
                        wait.until(ExpectedConditions.elementToBeClickable(option)).click();// Ensure element is clickable
                        System.out.println("Selected: " + option.getText());
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Entered Item Name: " + inputField.getAttribute("value"));
                if (!inputField.getAttribute("value").equals("")) {
                    optionFound = true;
                }
            }
        }
    }
}
