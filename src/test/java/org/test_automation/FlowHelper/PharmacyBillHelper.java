package org.test_automation.FlowHelper;

import org.test_automation.BaseTest;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.test_automation.DBConnectivity.MenuUtils;
import org.test_automation.DBConnectivity.XPathUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PharmacyBillHelper {


    private final MenuUtils menuUtils=new MenuUtils();
    private final XPathUtil xPathUtil=new XPathUtil();


    public void addCustomBill(String patientCode, BaseTest baseTest, WebDriver driver) {

        WebDriverWait wait=new WebDriverWait(driver,Duration.ofMillis(50));
        xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Add Bill')]"),driver,wait);

        WebElement dropdown1 = driver.findElement(By.xpath("//select[contains(@class, 'form-control')]"));

        JavascriptExecutor js = (JavascriptExecutor) driver;

// Set the value directly
        js.executeScript("arguments[0].value='byCode';", dropdown1);

// Trigger the change event for Angular/React
        js.executeScript("arguments[0].dispatchEvent(new Event('change'));", dropdown1);

        System.out.println("Selected 'By Code' using JavaScript.");

        System.out.println("Custom dropdown option 'By Code' selected.");
        WebElement patientCodeInput = wait.until(
                ExpectedConditions.elementToBeClickable(By.name("patientCode"))
        );
        patientCodeInput.click();

        baseTest.threadTimer(1000);

        patientCodeInput.sendKeys(Keys.BACK_SPACE);
        baseTest.threadTimer(500);
        patientCodeInput.sendKeys(patientCode);

        patientCodeInput.click();

        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//mat-option")));

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


    }

    public String addPharmacyBill(BaseTest baseTest, String patientCode, WebDriver driver, WebDriverWait wait, String panel) {

        System.out.println("Patient Code:---" + patientCode);
        menuUtils.menuPanelClick(panel, false, "", "",driver,wait);


        wait = new WebDriverWait(driver, Duration.ofSeconds(20));


        // Wait for the row containing patientCode
        WebElement row = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[td/span[contains(text(),'" + patientCode + "')]]")
        ));

        WebElement billButton = row.findElement(By.xpath(".//button[@title='Bill']"));

        wait.until(ExpectedConditions.elementToBeClickable(billButton)).click();

        List<String> listItems = new ArrayList<>();
        baseTest.threadTimer(3000);
        listItems = findOutOfStockElement(listItems, driver);
        if (listItems.size() == 0) {
            // Wait for the "Generate Bill" button to be present
            WebElement generateBillButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[contains(text(),'Generate Bill')]")
            ));


            wait.until(ExpectedConditions.elementToBeClickable(generateBillButton)).click();


            System.out.println("Generate Bill button clicked successfully.");

            baseTest.threadTimer(1000);


            WebElement payButton = wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Pay') and not(contains(text(),'Print'))]")
            )));


            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", payButton);

            baseTest.threadTimer(3000);
            payButton.click();

            System.out.println("Pay button clicked successfully.");

            baseTest.threadTimer(5000);

            return "Success";

        } else {
            WebElement backButton = wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Back')]")
            )));


            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", backButton);

            baseTest.threadTimer(3000);
            backButton.click();
            return listItems.toString();
        }
    }

    private List<String> findOutOfStockElement(List<String> listItems, WebDriver driver) {
        List<WebElement> elementList = driver.findElements(By.xpath("//span[contains(text(), 'Out Of Stock')]"));
        // Check if any elements were found
        if (elementList.isEmpty()) {
            System.out.println("No out-of-stock items found.");
            return new ArrayList<>();
        } else {
            System.out.println("Out-of-stock items found: " + elementList.size());

            // Iterate through the list and print the text of each element
            for (WebElement elementChild : elementList) {
                System.out.println("Out Of Stock Item: " + elementChild.getText());
                listItems.add(elementChild.getText().replace("-(Out Of Stock)", "").trim());
            }
            return listItems;
        }
    }


    public WebElement warningMessageBilling(WebDriverWait wait) {
        try {
            WebElement resultElement = wait.until(driver -> {
                List<By> locators = List.of(
                        By.xpath("//div[contains(@class, 'container-2')]/p[contains(normalize-space(),'Quantity cannot be Zero!')]"),
                        By.xpath("//div[contains(@class, 'container-2')]/p[contains(text(),'Success')]"),
                        By.xpath("//div[contains(@class, 'container-2')]/p[contains(text(),'Hold')]")
                );

                for (By locator : locators) {
                    List<WebElement> elements = driver.findElements(locator);
                    if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                        return elements.get(0);
                    }
                }
                return null;
            });
            return resultElement;
        } catch (Exception e) {
            return null;
        }
    }

    public void addPrescriptionPharmacyBill(BaseTest baseTest, String patientCode, WebDriver driver, WebDriverWait wait, String panel, String screenCheck) {
       menuUtils.menuPanelClick(panel, false, "", screenCheck,driver,wait);
        try {
            if (screenCheck.isEmpty()) {
                WebElement patientRow = findAndClickDropdownAndPrescription(patientCode, wait, driver);
                if (patientRow != null) {
                    System.out.println("Dropdown clicked successfully.");
                    Thread.sleep(1000);
                    WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                            By.id("current-admission-prescribedAdd")
                    ));
                    addButton.click();
                } else {
                    System.out.println("Patient not found.");
                }
            }

            addBillItems(baseTest, wait,driver);
            WebElement quantityInput = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@type='number' and @title='Quantity']")
            ));

            quantityInput.clear();
            quantityInput.sendKeys("10");


            WebElement saveCloseButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(), 'Save & Close')]")
            ));

            saveCloseButton.click();

            System.out.println("Successfully created Prescription");


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addBillItems(BaseTest baseTest, WebDriverWait wait, WebDriver driver) {

        try {
           xPathUtil.clickButtonElement(By.xpath(".//button[@title='Add New']"),driver,wait);

            WebElement itemInput = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@class='mat-autocomplete-trigger form-control ng-pristine ng-valid ng-star-inserted ng-touched' and @role='combobox']")
            ));
            itemInput.click();

            // Loop through letters A to Z
            for (char c = 'A'; c <= 'Z'; c++) {
                // Clear the input field and enter the current letter
                itemInput.clear();
                itemInput.sendKeys(String.valueOf(c));

                // Wait for the dropdown options to load
                try {
                    List<WebElement> dynamicOptions = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.xpath("//mat-option//span[contains(text(),'" + c + "')]")
                    ));

                    // If options are found, click on the first one and exit the loop
                    if (!dynamicOptions.isEmpty()) {
                        dynamicOptions.get(0).click();
                        System.out.println("Clicked option for letter: " + c);
                        break; // Exit the loop after clicking the first option for the letter
                    }
                } catch (Exception e) {
                    // Handle the case where no options are found for the current letter
                    System.out.println("No options found for letter: " + c);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public WebElement findAndClickDropdownAndPrescription(String patientCode, WebDriverWait wait, WebDriver driver) {
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
                        By.xpath("//td[span[contains(text(),'" + patientCode + "')]]/following-sibling::td//span[contains(text(),'Prescription')]")
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


    public Boolean addPrescriptionCurrentAdmission(BaseTest baseTest, String patientCode, WebDriver driver, WebDriverWait wait, String panel, JSONObject patient) {
        menuUtils.menuPanelClick(panel, false, "", "",driver,wait);
        String prescriptionSearch = patient.getString("prescriptionSearch");
        String prescriptionSelect = patient.getString("prescriptionSelect");
        try {

            baseTest.filterSearchClick();
            baseTest.filterSearchElemenet(patientCode, "Patient Code", "Text");
            WebElement patientRow = findAndClickDropdownAndPrescription(patientCode, wait, driver);
            if (patientRow != null) {

                List<String> search = Arrays.asList(prescriptionSearch.split(","));
                List<String> select = Arrays.asList(prescriptionSelect.split(","));

                baseTest.threadTimer(1000);

                int count = 2;

                for (int i = 0; i < search.size(); i++) {
                    WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                            By.id("current-admission-prescribedAdd")
                    ));
                    addButton.click();

                    count = i;
                    count = count + 2;
                    addPrescriptionItems(wait, "auto", search.get(i), select.get(i), count, baseTest);
                    WebElement quantityInput = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//input[@type='number' and @title='Quantity']")

                    ));
                    quantityInput.clear();
                    quantityInput.sendKeys("10");
                }


                WebElement saveCloseButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(), 'Save & Close')]")
                ));

                saveCloseButton.click();

                System.out.println("Successfully created Prescription");
                return true;
            } else {
                return false;
            }


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private void addPrescriptionItems(WebDriverWait wait, String type, String search, String select, int selectitem, BaseTest baseTest) {

        WebElement medicineInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@placeholder='Enter Medicine']")
        ));
        if (type.equals("custom")) {
            medicineInput.sendKeys(search);

            try {
                Thread.sleep(1000); // Adjust if necessary
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            WebElement selectedOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//mat-option//span[contains(text(),'" + select + "')]")
            ));
            selectedOption.click();
            String fullText = selectedOption.getText();
            System.out.println("itemList:--"+fullText);

        } else {
            for (char c = 'P'; c <= 'Z'; c++) {
                // Clear the input field and enter the current letter
                medicineInput.clear();
                medicineInput.sendKeys(String.valueOf(c));

                // Wait for the options to load
                try {
                    Thread.sleep(1000); // Adjust the sleep time if necessary
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // Locate all the options in the dropdown that match the current letter
                List<WebElement> dynamicOptions = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.xpath("//mat-option//span[contains(text(),'" + c + "')]")
                ));

                // If options are found, click on the first one and exit the loop
                if (!dynamicOptions.isEmpty()) {
                    dynamicOptions.get(selectitem).click();

                    //baseTest.itemList.add(fullText);
                    break; // Exit the loop after clicking the first option for the letter
                }
            }
        }
    }

    private int getPaginationDetails(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String pageText = (String) js.executeScript("return document.querySelector('li.small-screen')?.textContent.trim();");

        if (pageText != null && !pageText.isEmpty()) {
            String[] pageParts = pageText.split("/");
            return Integer.parseInt(pageParts[1].trim());
        }
        return 1; // Default to 1 page if pagination is not found
    }

    public Boolean findRowCurrentAdmission(String patientCode, String title, String status, int totalPages, WebDriver driver, WebDriverWait wait) {
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


                    WebElement dropdownIcon = rows.get(i).findElement(By.xpath(".//span[contains(@class,'ti-angle-double-down')]"));
                    wait.until(ExpectedConditions.elementToBeClickable(dropdownIcon)).click();
                    System.out.println("Dropdown icon clicked successfully.");
                    System.out.println("ss");

                    // ✅ Step 3: Wait for Prescription option to appear and scroll into view
                    WebElement prescriptionOption = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//td[span[contains(text(),'" + patientCode + "')]]/following-sibling::td//span[contains(text(),'Prescription')]")
                    ));

                    // ✅ Scroll into view (in case it's hidden)
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", prescriptionOption);
                    try {
                        Thread.sleep(500); // Small delay for UI adjustment
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    // ✅ Attempt to click, fallback to JS click if necessary
                    try {
                        wait.until(ExpectedConditions.elementToBeClickable(prescriptionOption)).click();
                    } catch (ElementClickInterceptedException e) {
                        System.out.println("Click intercepted, using JS click.");
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", prescriptionOption);
                    }


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

    private void scrollToElement(WebElement element, WebDriver driver) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }


}
