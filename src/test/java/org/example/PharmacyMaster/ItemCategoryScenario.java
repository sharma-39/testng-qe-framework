package org.example.PharmacyMaster;

import org.example.LoginUtil.LoginAndLocationTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ItemCategoryScenario extends LoginAndLocationTest {

    private Map<String, String> treeMap = new HashMap<>();

    // Helper method to generate a random number with a prefix
    private String generateRandomNumber(String prefix) {
        String datePart = Instant.now()
                .atZone(ZoneId.systemDefault()) // Convert to system time zone
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")); // Format date

        int randomPart = 100 + new Random().nextInt(900); // Generate a 3-digit random number

        String generateNumber = prefix + "-" + datePart + "-" + randomPart; // Create invoice number

        //System.out.println("Generated Number: " + generateNumber);
        return generateNumber;
    }

    // DataProvider for item category data
    @DataProvider(name = "itemCategoryDataProvider")
    public Object[][] getItemCategoryDataProvider() {
        return new Object[][]{
                // Scenario 1: ValidInput
                {
                        "ValidInput",
                        generateRandomNumber("IC"),
                        generateRandomNumber("ICN"),
                        "Active",
                        "item category field",
                        false,
                        "Successfully",
                        true
                },
                // Scenario 2:Item Category Code Already Exists
                {
                        "ItemCategoryCodeAlreadyExits",
                        "ITEM-123",
                        generateRandomNumber("IC"),
                        "Active",
                        "item category field",
                        true,
                        "Code Already Exists",
                        false
                },
                // Scenario 2:Item category name Already Exists
                {
                        "ItemCategoryNameAlreadyExits",
                        generateRandomNumber("ICN"),
                        "ITEM-Category-123",
                        "Active",
                        "item category field",
                        true,
                        "Name Already Exists",
                        false
                },

                // Scenario 2: Empty Item Code
                {
                        "EmptyItemCode",
                        null,
                        generateRandomNumber("ICN"),
                        "Active",
                        "item category field",
                        true,
                        "Item Code is required",
                        false
                },

               //  Scenario 3: Empty Item Category
                {
                        "EmptyItemCategory",
                        generateRandomNumber("IC"),
                        null,
                        "Active",
                        "item category field",
                        false,
                        "Item Category is required",
                        false
                },

                // Scenario 4: Special Characters in Item Category
                {
                        "SpecialCharactersInCategory",
                        generateRandomNumber("IC"),
                        "ITEM-!@#$%^&*()"+ 100 + new Random().nextInt(900),
                        "Active",
                        "item category field",
                        false,
                        "Successfully",
                        true
                },

                // Scenario 5: Long Item Category Name
                {
                        "LongItemCategoryName",
                        generateRandomNumber("IC"),
                        "ITEM-" + new String(new char[255]).replace('\0', 'A'),
                        "Active",
                        "item category field",
                        false,
                        "error",
                        false,
                },
                // Scenario 7: Inactive Item Category
                {
                        "InactiveItemCategory",
                        generateRandomNumber("IC"),
                        generateRandomNumber("ICN"),
                        "Inactive",
                        "item category field",
                        false,
                        "Successfully",
                        true
                }
        };
    }
    @Test(priority = 3)
    public void openMenu() {
        if (isLoginSuccessful) {
            menuPanelClick("Master", true, "Pharmacy", "");
        }
    }

    // Test method for addItemCategory
    @Test(priority = 4, dataProvider = "itemCategoryDataProvider")
    public void testAddItemCategory(String scenario, String itemCode, String itemCategory, String activeStatus,
                                    String description,Boolean hideOfSummary,String result, boolean expectedResult) {

            threadTimer(3000);
            System.out.println("result "+result);

            String responseCode = addItemCategory(itemCode, itemCategory, activeStatus, description,hideOfSummary,result);
            System.out.println("backend response test:--"+responseCode);
          //  System.out.println("returnable code"+responseCode);
            if (responseCode!=null && ((responseCode.equals("error")  ||  !responseCode.contains(result)))) {
                Assert.fail("Expected the test to fail for scenario: " + scenario);
            } else {
                System.out.println("Test Scenario Success"+scenario );
            }
    }

    // Method to add an item category
    private String addItemCategory(String itemCode, String itemCategory, String activeStatus, String description, boolean hideOfSummary, String result) {
        clickElement(By.xpath("//a[@id='Item Category' and contains(@class, 'nav-link')]"));
        clickElement(By.xpath("//button[contains(text(),'Add New')]"));

        enterText(By.cssSelector("input[title='Item Code']"), !nullValidation(itemCode) ? "" : itemCode, true);

        enterText(By.cssSelector("input[title='Item Category']"), !nullValidation(itemCategory) ? "" : itemCategory, true);

        if (nullValidation(activeStatus)) {
            selectRadioButton("active", activeStatus);
        }
        enterText(By.cssSelector("textarea[formcontrolname='description']"), description, true);
        if (hideOfSummary) {
            clickElement(By.cssSelector("input[formcontrolname='hideIpSummary']"));
        }
        clickElement(By.xpath("//button[contains(text(),'Save & Close')]"));

        String warningMessage=new String();
        for (WebElement item : getMantoraryFields()) {
            warningMessage= item.getText();
        }
        if (getMantoraryFields().size() == 0) {
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
            return warningMessage;
        }


    }




    private void clickElement(By locator) {

        threadTimer(500);
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
        } catch (Exception e) {
      //      System.out.println("Normal click failed, using JavaScript click...");

        }

    }

    private void enterText(By locator, String text, boolean editable) {
        WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        inputField.clear();
        inputField.sendKeys(text);
        threadTimer(500);
    }


    private void selectField(String title, String value) {
        if (nullValidation(value)) {
            WebElement titleDropdown = driver.findElement(By.cssSelector("select[formcontrolname='" + title + "']"));
            Select select = new Select(titleDropdown);
            select.selectByVisibleText(value);
            threadTimer(500);
        }
    }

    private boolean nullValidation(String value) {
        return value != null && !value.isEmpty() && !value.equals("null") && !value.equals("Null");
    }

    private void selectRadioButton(String formControlName, String value) {
        try {
            WebElement radioButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@formcontrolname='" + formControlName + "'][@value='" + value + "'] | //label[span[contains(text(), '" + value + "')]]/input[@formcontrolname='" + formControlName + "'] ")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", radioButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioButton);
          //  System.out.println("Selected radio button: " + value);
            threadTimer(500);
        } catch (TimeoutException e) {
          //  System.out.println("Radio button with value '" + value + "' not found!");
        }
    }

    private void closePanel() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            By closeButtonLocator = By.cssSelector("button[aria-label='Close']");
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(closeButtonLocator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", closeButton);
            closeButton.click();
        //    System.out.println("Close button clicked successfully.");
        } catch (Exception e) {
     //       System.out.println("Failed to close the panel: " + e.getMessage());
        }
    }

    

}