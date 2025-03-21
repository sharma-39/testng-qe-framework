package org.test_automation.PharmacyMaster;

import org.test_automation.Listener.AllTestListener;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Listeners(AllTestListener.class)
public class BrandTestScenario extends LoginAndLocationTest {

    static int incresescenario = 0;
    String editBrandCode;

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

    // DataProvider for Brand data
    @DataProvider(name = "BrandDataProvider")
    public Object[][] getBrandDataProvider() {
        return new Object[][]{
                // Scenario 1: ValidInput
                {
                        "ValidInput",
                        generateRandomNumber("BR-C"),
                        generateRandomNumber("BR-N"),
                        "Active",
                        "test scenario description",
                        "Success"
                },
                //  Scenario 2:Brand Code Already Exists
                {
                        "BrandCodeAlreadyExits",
                        "BR-20250313-902",
                        generateRandomNumber("BR-N"),
                        "Active",
                        "test scenario description",
                        "Already Exists"
                },
                // Scenario 4:Brand name Already Exists
                {
                        "BrandNameAlreadyExits",
                        generateRandomNumber("Brand-N"),
                        "BRAND373",
                        "Active",
                        "test scenario description",
                        "Already Exists"
                },

               //  Scenario 5: Empty Item Code
                {
                        "EmptyBrandCode",
                        null,
                        generateRandomNumber("BR-N"),
                        "Active",
                        "test description",
                        "Brand Code is required",
                },

                // Scenario 6: Empty Brand
                {
                        "EmptyBrand",
                        generateRandomNumber("BR-C"),
                        null,
                        "Active",
                        "test scenario description",
                        "Brand Name is required"
                },

                // Scenario 7: Special Characters in Brand
                {
                        "SpecialCharactersInBrand",
                        generateRandomNumber("BR-C"),
                        "BR-!@#$%^&*()" + 100 + new Random().nextInt(900),
                        "Active",
                        "test scenario description",
                        "Success"
                },

                // Scenario 8: Long Brand Name
                {
                        "LongBrandName",
                        generateRandomNumber("BR-C"),
                        "BR-" + new String(new char[255]).replace('\0', 'A'),
                        "Active",
                        "test scenario description",
                        "error",//  because long brand name required front end but backend throw 500 issue
                },
                // Scenario 9: Inactive Brand
                {
                        "InactiveBrand",
                        "Brand-20250313-185",
                        generateRandomNumber("Brand-N"),
                        "Inactive",
                        "test scenario description",
                        "Success"
                }
        };
    }

    @Test(priority = 3)
    public void openMenu() {
        if (isLoginSuccessful) {
            menuPanelClick("Master", true, "Pharmacy", "");
        }
    }

    // Test method for addBrand
    @Test(priority = 5, dataProvider = "BrandDataProvider")
    public void testAddBrand(String scenario, String BrandCode, String BrandName, String activeStatus, String description, String expectedResult) {

        System.out.println("Scenario: "+scenario+"brand Code:"+BrandCode+"BrandName:"+activeStatus+"Expect result:"+expectedResult);
        String response = new String();
        threadTimer(5000);
        response=addBrand(BrandCode, BrandName, activeStatus, description, expectedResult);
        System.out.println("return Message" + response);
        if (response != null && ((response.equals("error") || !response.contains(expectedResult)))) {
            Assert.fail("Expected the test to fail for scenario: " + scenario);
        } else {
            System.out.println("Test Scenario Success" + scenario);
        }
    }

    // Method to add an Brand
    private String addBrand(String brandCode, String brandName, String activeStatus, String description, String expectedResult) {

        clickElement(By.xpath("//a[@id='Brand' and contains(@class, 'nav-link')]"));
        if (activeStatus.equals("Inactive")) {
//            editBrandCode="BR-C-20250318-368";
            clickButtonInRow(editBrandCode, "Edit");
        } else {
            clickElement(By.xpath("//button[contains(text(),'Add New')]"));
            enterText(By.cssSelector("input[title='Brand Code']"), brandCode, true);
        }

        enterText(By.cssSelector("input[title='Brand Name']"), brandName, true);


        enterText(By.cssSelector("textarea[formcontrolname='description']"),
                description, true);

        selectRadioButton("active", activeStatus);

        clickElement(By.xpath("//button[contains(text(),'Save & Close')]"));

        String warningMessage=new String();
        for (WebElement item : getMantoraryFields()) {
           warningMessage= item.getText();
        }
        if (getMantoraryFields().size() == 0) {
            String backendResponse=warningMessagePurchase().getText();
            System.out.println("backend response"+backendResponse);
            if (backendResponse!= null && backendResponse.contains("Successfully")) {
                editBrandCode = brandCode;
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
        if(text!=null &&  !text.isEmpty())  {
            WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            inputField.clear();
            inputField.sendKeys(text);
            threadTimer(500);
        }
    }


    private void selectField(String title, String value) {
        if (value!=null)
        {
            WebElement titleDropdown = driver.findElement(By.cssSelector("select[formcontrolname='" + title + "']"));
            Select select = new Select(titleDropdown);
            select.selectByVisibleText(value);
            threadTimer(500);
        }
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

    private void clickButtonInRow(String searchText, String buttonTitle) {
        threadTimer(3000);
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

    private void highlightElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.backgroundColor = 'yellow'", element);
    }

    private void clickElement(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            System.out.println("Normal click failed, using JavaScript click...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }
}