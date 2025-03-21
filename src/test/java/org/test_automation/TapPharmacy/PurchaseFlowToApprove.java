package org.test_automation.TapPharmacy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class PurchaseFlowToApprove extends LoginAndLocationTest {

    @DataProvider(name = "stockData")
    public Object[][] getStockData() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File("src/test/resources/stockData.json"));
        return new Object[][]{{jsonNode}};
    }

    @Test(priority = 3, dataProvider = "stockData")
    public void testPharmacyFlow(JsonNode stockData) {

        boolean backdrop=false;
        while(!backdrop) {
            boolean afterApprovelEdit = false;
            menuPanelClick("Stock", true, "Purchase", "");
            waitForSeconds(3);
            verifyPanelName("Purchase Management");
            String invoiceNumber = addStock(stockData);
            System.out.println("STOCK Created Successfully :-Invoice number " + invoiceNumber);

            backdropOccur();
            editStock(invoiceNumber, stockData);
            System.out.println("STOCK Edit Successfully :-Invoice number " + invoiceNumber);
            backdropOccur();

            menuPanelClick("Approval", true, "Purchase Edit Approval", "");

            verifyPanelName("Purchase Edit Approval");

            if (afterApprovelEdit) {
                editStock(invoiceNumber, stockData);
            }
            System.out.println("Processing approvel  :-Invoice number " + invoiceNumber);
            approvelPurchase(invoiceNumber);
        }
    }

    private void backdropOccur() {
        if (!driver.findElements(By.className("modal-backdrop")).isEmpty()) {
            System.out.println("Modal backdrop detected! Reloading the page...");
            // Wait for the page to reload and ensure the modal disappears
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("modal-backdrop")));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("location.reload()");
            threadTimer(3000);
            menuPanelClick("Dashboard", false, "", "");
            verifyPanelName("Dashboard");

        } else {
            System.out.println("No modal backdrop detected, proceeding with test...");
        }
    }

    private void approvelPurchase(String invoiceNumber) {
        clickButtonInRow(invoiceNumber, "Approve");
        threadTimer(3000);
        clickElement(By.xpath("//button[contains(text(),'Approve & Close')]"));
    }

    private String addStock(JsonNode stockData) {
        clickElement(By.xpath("//button[contains(text(),'Add Stock')]"));

        JsonNode supplier = stockData.get("stock").get("supplier");
        selectDropdownByVisibleText(By.xpath("//select[@formcontrolname='supplierId']"),
                supplier.get("name").asText(),
                supplier.get("id").asText());

        String invoiceNumber = generateRondamNumber("INV");
        enterText(By.cssSelector("input[formcontrolname='invoiceNumber']"), invoiceNumber,
                true);

        invoiceDate(stockData.get("stock").get("invoiceDate").asText(), "purDate");
        grnDate(stockData.get("stock").get("grnDate").asText(), "grnDate");

        enterText(By.xpath("//input[@title='Cash Discount (%)']"),
                stockData.get("stock").get("cashDiscount").asText(), true);
        enterText(By.xpath("//input[@title='TCS (%)']"),
                stockData.get("stock").get("tcs").asText(), true);


        clickElement(By.xpath("//div[contains(@class, 'addIcon-button')]"));
        for (int i = 0; i < stockData.get("stock").get("items").size(); i++) {
            addStockDetails(stockData.get("stock").get("items").get(i));
            clickElement(By.xpath("//button[contains(text(),'Add New')]"));

        }

        threadTimer(3000);

        clickElement(By.cssSelector("button.saveNdClose.ng-star-inserted"));
        threadTimer(3000);


        clickElement(By.cssSelector("button.saveNdClose.ng-star-inserted"));


        return invoiceNumber;


    }

    private void editStock(String invoiceNumber, JsonNode itemData) {

        //find this row and click
        clickButtonInRow(invoiceNumber, "Edit");

        JsonNode itemsArray = itemData.path("stock").path("items");
        for (int i = 0; i < itemsArray.size(); i++) {
            if (itemsArray.get(i).get("edit").asBoolean()) {
                threadTimer(1000);

                String targetItem = itemsArray.get(i).get("description").asText();
                List<WebElement> rowsItems = driver.findElements(By.xpath("//table//tr"));
                for (int j = 0; j < rowsItems.size(); j++) {
                    if (rowsItems.get(j).getText().contains(targetItem)) {
                        System.out.println("Row Found at Index: " + (j + 1));

                        // Highlight the row
                        JavascriptExecutor js = (JavascriptExecutor) driver;
                        js.executeScript("arguments[0].style.backgroundColor = 'yellow'", rowsItems.get(j));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", rowsItems.get(j));
                        System.out.println("Row Highlighted!");
                        WebElement viewButton = rowsItems.get(j).findElement(By.xpath(".//button[@title='Edit']"));
                        // Scroll to the button
                        viewButton.click(); // Click the button

                        threadTimer(2000);
                        enterText(By.xpath("//input[@title='Batch Number']"), itemsArray.get(i).get("batchNumber").asText(), true);
                        selectDropdownByValue(By.xpath("//select[@title='Month']"), itemsArray.get(i).get("expiryMonth").asText(), true);
                        selectDropdownByValue(By.xpath("//select[@title='Year']"), itemsArray.get(i).get("expiryYear").asText(), true);
                        selectDropdownByIndex(By.xpath("//select[@formcontrolname='purchaseUomId']"),
                                itemsArray.get(i).get("purchaseUomIndex").asInt(), true);

                        enterText(By.xpath("//input[@type='number' and @title='Qty Per Package']"), itemsArray.get(i).get("qtyPerPackage").asText(), true);
                        enterText(By.xpath("//input[@type='number' and @title='Purchase Quantity']"), itemsArray.get(i).get("purchaseQuantity").asText(), true);
                        enterText(By.xpath("//input[@type='number' and @title='MRP']"), itemsArray.get(i).get("mrp").asText(), true);
                        enterText(By.xpath("//input[@type='number' and @title='Purchase Rate']"), itemsArray.get(i).get("purchaseRate").asText(), true);
                        enterText(By.xpath("//input[@type='number' and @title='Purchase Discount (%)']"), itemsArray.get(i).get("purchaseDiscount").asText(), true);
                        enterText(By.xpath("//input[@type='number' and @title='GST']"), itemsArray.get(i).get("gst").asText(), true);


                        System.out.println("fill all record edit");
                        threadTimer(2000);
                        clickElement(By.xpath("(//button[contains(text(),'Save & Close')])[1]"));

                    }
                }

                threadTimer(500);
                clickElement(By.xpath("(//button[contains(text(),'Save & Close')])"));

            }
        }
    }


    private void addStockDetails(JsonNode itemData) {


        waitForSeconds(3);

        selectAutoCompleteItem(By.xpath("//input[@formcontrolname='itemName']"),
                itemData.get("name").asText(),
                itemData.get("description").asText(), true);

        enterText(By.xpath("//input[@title='Batch Number']"), generateRondamNumber("BAT"), true);
        selectDropdownByValue(By.xpath("//select[@title='Month']"), itemData.get("expiryMonth").asText(), true);
        selectDropdownByValue(By.xpath("//select[@title='Year']"), itemData.get("expiryYear").asText(), true);
        selectDropdownByIndex(By.xpath("//select[@formcontrolname='purchaseUomId']"),
                itemData.get("purchaseUomIndex").asInt(), true);

        enterText(By.xpath("//input[@type='number' and @title='Qty Per Package']"), itemData.get("qtyPerPackage").asText(), true);
        enterText(By.xpath("//input[@type='number' and @title='Purchase Quantity']"), itemData.get("purchaseQuantity").asText(), true);
        enterText(By.xpath("//input[@type='number' and @title='MRP']"), itemData.get("mrp").asText(), true);
        enterText(By.xpath("//input[@type='number' and @title='Purchase Rate']"), itemData.get("purchaseRate").asText(), true);
        enterText(By.xpath("//input[@type='number' and @title='Purchase Discount (%)']"), itemData.get("purchaseDiscount").asText(), true);
        enterText(By.xpath("//input[@type='number' and @title='GST']"), itemData.get("gst").asText(), true);
    }

    private void clickElement(By locator) {

        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
        } catch (Exception e) {
            System.out.println("Normal click failed, using JavaScript click...");

        }

    }


    private void enterText(By locator, String text, boolean editable) {
        WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        inputField.clear();
        inputField.sendKeys(text);
    }

    private void selectDropdownByVisibleText(By locator, String text, String fallbackValue) {


        WebElement dropdownElement = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        Select dropdown = new Select(dropdownElement);
        wait.until(d -> dropdown.getOptions().size() > 1);

        try {
            dropdown.selectByVisibleText(text);
        } catch (NoSuchElementException e) {
            dropdown.selectByIndex(2);
        }
    }

    private void selectDropdownByValue(By locator, String value, boolean editable) {
        WebElement dropdownElement = wait.until(ExpectedConditions.elementToBeClickable(locator));
        new Select(dropdownElement).selectByValue(value);
    }

    private void selectDropdownByIndex(By locator, int index, boolean editable) {
        WebElement dropdownElement = wait.until(ExpectedConditions.elementToBeClickable(locator));
        new Select(dropdownElement).selectByIndex(index);
    }


    private void selectAutoCompleteItem(By inputLocator, String inputText, String value, boolean editable) {
        boolean optionFound = false;


        while (!optionFound) {
            WebElement inputField = wait.until(ExpectedConditions.elementToBeClickable(inputLocator));
            inputField.clear();
            inputField.click();
            inputField.sendKeys(inputText);
            System.out.println("input text" + inputText);
            try {


                threadTimer(1000);

                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//mat-option")));

                // Re-fetch options to avoid StaleElementReferenceException
                List<WebElement> options = driver.findElements(By.xpath("//mat-option"));


                for (WebElement option : options) {
                    System.out.println("get Text" + option.getText());
                    if (option.getText().trim().equalsIgnoreCase(value)) {
                        wait.until(ExpectedConditions.elementToBeClickable(option)).click(); // Ensure element is clickable
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


    private void findMandatoryFields() {
        List<WebElement> asteriskElements = driver.findElements(By.xpath("//span[contains(@style,'color: red') and text()='*']"));
        System.out.println("Total Fields Marked with Red Asterisk: " + asteriskElements.size());

        for (WebElement asterisk : asteriskElements) {
            WebElement parentElement = asterisk.findElement(By.xpath("./ancestor::*[1]"));
            List<WebElement> inputFields = parentElement.findElements(By.xpath(".//input | .//select"));
            for (WebElement inputField : inputFields) {
                System.out.println("Field: " + inputField.getTagName() +
                        ", Name: " + inputField.getAttribute("name") +
                        ", Placeholder: " + inputField.getAttribute("placeholder"));
            }
        }
    }


    private void grnDate(String date, String id) {
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


    public void invoiceDate(String date, String id) {
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

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

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
            closeExistingDatePicker();

        } catch (Exception e) {
            System.err.println("Error in datePicker method: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void closeExistingDatePicker() {
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

    public static String invoiceNumberGenerate(int length) {
        Random random = new Random();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder firstName = new StringBuilder();

        for (int i = 0; i < length; i++) {
            firstName.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }

        return firstName.toString();
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

    private void clickButtonInRow(String searchText, String buttonTitle) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));

        // Wait for the table to be visible
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table")));

        // Get all rows in the table
        List<WebElement> rows = driver.findElements(By.xpath("//table//tr"));

        System.out.println("Total rows found: " + rows.size());

        for (WebElement row : rows) {
            if (row.getText().contains(searchText)) {
                System.out.println("Row Found containing: " + searchText);

                // Highlight the row
                highlightElement(row);

                // Scroll into view
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", row);
                System.out.println("Row Highlighted!");

                // Find the button in the row and click it
                WebElement button = row.findElement(By.xpath(".//button[@title='" + buttonTitle + "']"));
                clickElement(button);
                break;
            }
        }
    }

    /**
     * Highlights a web element using JavaScript.
     */
    private void highlightElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.backgroundColor = 'yellow'", element);
    }

    /**
     * Clicks an element with proper handling.
     */
    private void clickElement(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            System.out.println("Normal click failed, using JavaScript click...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

}
