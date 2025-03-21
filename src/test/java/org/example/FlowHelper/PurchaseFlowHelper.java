package org.example.FlowHelper;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.BaseTest;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class PurchaseFlowHelper {

    public void addStockPurchase(BaseTest baseTest, List<String> items, WebDriver driver, WebDriverWait wait, String pharmacy, JsonNode stockData) {

        baseTest.menuPanelClick("Stock", true, "Purchase", "");
        //add stock
        baseTest.clickButtonElement(By.xpath("//button[contains(text(),'Add Stock')]"));

        JsonNode supplier = stockData.get("stock").get("supplier");
        selectDropdownByVisibleText(By.xpath("//select[@formcontrolname='supplierId']"),
                supplier.get("name").asText(),
                supplier.get("id").asText(), wait);

        String invoiceNumber = generateRondamNumber("INV");
        enterText(By.cssSelector("input[formcontrolname='invoiceNumber']"), invoiceNumber,
                true, wait);

        invoiceDate(stockData.get("stock").get("invoiceDate").asText(), "purDate", driver, wait, baseTest);
        grnDate(stockData.get("stock").get("grnDate").asText(), "grnDate", driver, wait, baseTest);

        enterText(By.xpath("//input[@title='Cash Discount (%)']"),
                stockData.get("stock").get("cashDiscount").asText(), true, wait);
        enterText(By.xpath("//input[@title='TCS (%)']"),
                stockData.get("stock").get("tcs").asText(), true, wait);


        baseTest.clickButtonElement(By.xpath("//div[contains(@class, 'addIcon-button')]"));
        if (items.size() == 0) {
            for (int i = 0; i < stockData.get("stock").get("items").size(); i++) {

                addStockDetails(stockData.get("stock").get("items").get(i), wait, baseTest, driver, "auto", null);
                baseTest.clickButtonElement(By.xpath("//button[contains(text(),'Add New')]"));

            }
        } else {
            for (int i = 0; i < items.size(); i++) {
                addStockDetails(stockData.get("stock").get("items").get(i), wait, baseTest, driver, "custom", items.get(i).trim());
                baseTest.clickButtonElement(By.xpath("//button[contains(text(),'Add New')]"));
            }
        }

        WebElement modal = driver.findElement(By.cssSelector(".modal-content"));
        if (modal.isDisplayed()) {

            baseTest.clickButtonElement(By.xpath("//button[contains(text(), 'Close') and not(contains(text(), 'Save'))]"));
            System.out.println("Added items: ");
            baseTest.threadTimer(500);
            baseTest.clickButtonElement(By.xpath("(//button[contains(text(),'Save & Close')])"));
            System.out.println("Modal is open.");
        } else {
            System.out.println("Modal is closed.");
        }


    }


    private void addStockDetails(JsonNode itemData, WebDriverWait wait, BaseTest baseTest, WebDriver driver, String dataType, String items) {

        if (dataType.equals("auto")) {
            selectAutoCompleteItem(By.xpath("//input[@formcontrolname='itemName']"),
                    itemData.get("name").asText(),
                    itemData.get("description").asText(), true, baseTest, wait, driver, "auto");
        } else {
            selectAutoCompleteItem(By.xpath("//input[@formcontrolname='itemName']"),
                    items,
                    items, true, baseTest, wait, driver, "custom");
        }

        waitForSeconds(3);

        enterText(By.xpath("//input[@title='Batch Number']"), generateRondamNumber("BAT"), true, wait);
        selectDropdownByValue(By.xpath("//select[@title='Month']"), itemData.get("expiryMonth").asText(), true, wait);
        selectDropdownByValue(By.xpath("//select[@title='Year']"), itemData.get("expiryYear").asText(), true, wait);
        selectDropdownByIndex(By.xpath("//select[@formcontrolname='purchaseUomId']"),
                itemData.get("purchaseUomIndex").asInt(), true, wait);

        enterText(By.xpath("//input[@type='number' and @title='Qty Per Package']"), itemData.get("qtyPerPackage").asText(), true, wait);
        enterText(By.xpath("//input[@type='number' and @title='Purchase Quantity']"), itemData.get("purchaseQuantity").asText(), true, wait);
        enterText(By.xpath("//input[@type='number' and @title='MRP']"), itemData.get("mrp").asText(), true, wait);
        enterText(By.xpath("//input[@type='number' and @title='Purchase Rate']"), itemData.get("purchaseRate").asText(), true, wait);
        enterText(By.xpath("//input[@type='number' and @title='Purchase Discount (%)']"), itemData.get("purchaseDiscount").asText(), true, wait);
        enterText(By.xpath("//input[@type='number' and @title='GST']"), itemData.get("gst").asText(), true, wait);

    }


    private void enterText(By locator, String text, boolean editable, WebDriverWait wait) {
        WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        inputField.clear();
        inputField.sendKeys(text);
    }

    private void selectDropdownByVisibleText(By locator, String text, String fallbackValue, WebDriverWait wait) {


        WebElement dropdownElement = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        Select dropdown = new Select(dropdownElement);
        wait.until(d -> dropdown.getOptions().size() > 1);

        try {
            dropdown.selectByVisibleText(text);
        } catch (NoSuchElementException e) {
            dropdown.selectByIndex(2);
        }
    }

    private void selectDropdownByValue(By locator, String value, boolean editable, WebDriverWait wait) {
        WebElement dropdownElement = wait.until(ExpectedConditions.elementToBeClickable(locator));
        new Select(dropdownElement).selectByValue(value);
    }

    private void selectDropdownByIndex(By locator, int index, boolean editable, WebDriverWait wait) {
        WebElement dropdownElement = wait.until(ExpectedConditions.elementToBeClickable(locator));
        new Select(dropdownElement).selectByIndex(index);
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

    private void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String generateRondamNumber(String prefix) {

        String datePart = Instant.now()
                .atZone(ZoneId.systemDefault()) // Convert to system time zone
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")); // Format date

        int randomPart = 10000 + new Random().nextInt(90000); // Generate a 5-digit random number

        String generateNumber = prefix + "-" + datePart + "-" + randomPart; // Create invoice number

        System.out.println("Generated Number: " + generateNumber);
        return generateNumber;
    }


    public void invoiceDate(String date, String id, WebDriver driver, WebDriverWait wait, BaseTest baseTest) {
        try {
            String[] dateFormat = date.split("-");
            String dateText = dateFormat[0];
            String monthText = dateFormat[1];
            String yearText = dateFormat[2];

            // Step 1: Close any open datepicker
            //closeExistingDatePicker();

            // Step 2: Open the datepicker using JavaScript (to prevent multiple triggers)
            WebElement dateField = driver.findElement(By.id(id));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dateField);

            wait = new WebDriverWait(driver, Duration.ofSeconds(5));

            // Step 3: Select the year
            WebElement yearDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("select.yearselect")));
            new Select(yearDropdown).selectByValue(yearText);

            // Step 4: Select the month
            WebElement monthDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("select.monthselect")));
            new Select(monthDropdown).selectByVisibleText(convertMMToMonth(monthText));

            // Step 5: Select the day
            WebElement dayElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//td[text()='" + Integer.parseInt(dateText) + "']")));
            dayElement.click();

            // Step 6: Close the datepicker
            closeExistingDatePicker(driver);

        } catch (Exception e) {
            System.err.println("Error in datePicker method: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void closeExistingDatePicker(WebDriver driver) {
        try {
            List<WebElement> activeDatePickers = driver.findElements(By.cssSelector(".daterangepicker.show-calendar"));
            if (!activeDatePickers.isEmpty()) {
                Actions actions = new Actions(driver);
                actions.sendKeys(Keys.ESCAPE).perform();
                Thread.sleep(500);
            }
        } catch (InterruptedException ignored) {
        }
    }

    public String convertMMToMonth(String monthNumber) {
        int monthInt = Integer.parseInt(monthNumber);
        return Month.of(monthInt).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }


    private void grnDate(String date, String id, WebDriver driver, WebDriverWait wait, BaseTest baseTest) {
        String[] dateFormat = date.split("-");
        String dateText = dateFormat[0]; // Day
        String monthText = dateFormat[1]; // Month (MM)
        String yearText = dateFormat[2]; // Year (YYYY)

        WebElement expiryDateField = driver.findElement(By.id(id));
        expiryDateField.click();


        // Wait until the date range picker is visible

        // Locate the parent date range picker div
        WebElement dateRangePicker = driver.findElement(By.xpath("//div[@class='daterangepicker ltr auto-apply show-ranges single opensright']"));

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
        baseTest.threadTimer(2500);

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

        String desiredDate = String.valueOf(Integer.parseInt(dateText));

        List<WebElement> dateElements = dateRangePicker.findElements(By.cssSelector("td.available"));

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
}
