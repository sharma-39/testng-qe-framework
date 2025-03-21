package org.test_automation.PharmacyMaster;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.test_automation.Listener.AllTestListener;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Listeners(AllTestListener.class)
public class ItemTestScenario extends LoginAndLocationTest {

    String itemTestCode;
    List<String> randomNumberGeneration = new ArrayList<>();

    // Helper method to generate a random number with a prefix
    private String generateRandomNumber(String prefix) {

        Random random = new Random();
        String numbergeneration;
        // Keep generating until a unique number is found
        do {
            numbergeneration = "TEST-" + prefix + "-" + random.nextInt(999999);
        } while (randomNumberGeneration.contains(numbergeneration));
        // Add the new unique number to the set
        randomNumberGeneration.add(numbergeneration);
        return numbergeneration;
    }

    // Helper method to create item data as JsonNode
    private JsonNode createItemData(String itemCode, String itemName, String itemComposition, String strength,
                                    String categoryId, String brandId, String itemType, String drugType,
                                    String scheduleId, String sellingUomId, String hsnCode, Object reorderLevel,
                                    Object minQtyLevel, String rackNumber, String version, String gstType,
                                    int maxDiscount, String itemDescription, String returnable, boolean hideIpSummary) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode itemData = mapper.createObjectNode();
        itemData.put("itemCode", itemCode);
        itemData.put("itemName", itemName);
        itemData.put("itemComposition", itemComposition);
        itemData.put("strength", strength);
        itemData.put("categoryId", categoryId);
        itemData.put("brandId", brandId);
        itemData.put("itemType", itemType);
        itemData.put("drugType", drugType);
        itemData.put("scheduleId", scheduleId);
        itemData.put("sellingUomId", sellingUomId);
        itemData.put("hsnCode", hsnCode);
        itemData.put("reorderLevel", reorderLevel instanceof Integer ? (int) reorderLevel : 0);
        itemData.put("minQtyLevel", minQtyLevel instanceof Integer ? (int) minQtyLevel : 0);
        itemData.put("rackNumber", rackNumber);
        itemData.put("version", version);
        itemData.put("gstType", gstType);
        itemData.put("maxDiscount", maxDiscount);
        itemData.put("itemDescription", itemDescription);
        itemData.put("returnable", returnable);
        itemData.put("hideIpSummary", hideIpSummary);
        return itemData;
    }

    // DataProvider for item data
    @DataProvider(name = "itemDataProvider")
    public Object[][] getItemDataProvider() {
        return new Object[][]{
                //positive testing
                //Scenario 1: Non-Medicinal Item with all fields filled (postive)
                {
                        "nonMedicialRadio",
                        createItemData(
                                generateRandomNumber("FAC"), generateRandomNumber("ITEM"), "500", "500", "GLOVES", "BRAND102",
                                "Non-Medicinal", "Others", null, "Other", "AZ21232", 32, 10, "AZ21232",
                                "2.0", "Goods", 10, "item field", "No", true
                        ),
                        "Success"
                },

                // Scenario 2: Medicinal Item with all fields filled(postive)
                {
                        "MedicinalRadio",
                        createItemData(
                                generateRandomNumber("FAC"), generateRandomNumber("ITEM"), "Z21233", "Y21234", "GEL", "BRAND102",
                                "Medicine", "Others", "Others", "Other", "AZ21232", 32, 10, "1221232",
                                "2.0", "Goods", 10, "item field", "Yes", false
                        ),
                        "Success"
                },
                //  Scenario 3: GST type included good
                {
                        "GSTTypeGood",
                        createItemData(
                                generateRandomNumber("FAC"), generateRandomNumber("ITEM"), "Z21233", "Y21234", "GEL", "BRAND102",
                                "Medicine", "Others", "Others", "Other", "AZ21232", 32, 10, "1221232",
                                "2.0", "Goods", 10, "item field", "Yes", false
                        ),
                        "Success"
                },
                // Scenario 4: GST type included Services
                {
                        "GstTypeService",
                        createItemData(
                                generateRandomNumber("FAC"), generateRandomNumber("ITEM"), "Z21233", "Y21234", "GEL", "BRAND102",
                                "Medicine", "Others", "Others", "Other", "AZ21232", 32, 10, "1221232",
                                "2.0", "Services", 10, "item field", "Yes", false
                        ),
                        "Success"
                },
                // Scenario 5: Hide Item in IP Summary enable
                {
                        "HideIteminIPSummary",
                        createItemData(
                                generateRandomNumber("FAC"), generateRandomNumber("ITEM"), "Z21233", "Y21234", "GEL", "BRAND102",
                                "Medicine", "Others", "Others", "Other", "AZ21232", 32, 10, "1221232",
                                "2.0", "Services", 10, "item field", "Yes", true
                        ),
                        "Success"
                },
                // Scenario 6: Hide Item in IP Summary disable
                {
                        "HideIteminIPSummary",
                        createItemData(
                                generateRandomNumber("FAC"), generateRandomNumber("ITEM"), "Z21233", "Y21234", "GEL", "BRAND102",
                                "Medicine", "Others", "Others", "Other", "AZ21232", 32, 10, "1221232",
                                "2.0", "Services", 10, "item field", "Yes", false
                        ),
                        "Success"
                },

                // Scenario 7: Ruturnable
                {
                        "Ruturnable",
                        createItemData(
                                generateRandomNumber("FAC"), generateRandomNumber("ITEM"), "Z21233", "Y21234", "GEL", "BRAND102",
                                "Medicine", "Others", "Others", "Other", "AZ21232", 32, 10, "1221232",
                                "2.0", "Services", 10, "item field", "Yes", false
                        ),
                        "Success"
                },

                // Scenario 8: nonReturnable
                {
                        "nonReturnable",
                        createItemData(
                                generateRandomNumber("FAC"), generateRandomNumber("ITEM"), "Z21233", "Y21234", "GEL", "BRAND102",
                                "Medicine", "Others", "Others", "Other", "AZ21232", 32, 10, "1221232",
                                "2.0", "Services", 10, "item field", "No", false
                        ),
                        "Success"
                },
                //Scenario 9:Add an item with zero "Reorder Quantity"
                {
                        "ZeroQty",
                        createItemData(
                                generateRandomNumber("FAC"), generateRandomNumber("ITEM"), "UOM-!@#$%^&*()" + 100 + new Random().nextInt(900), "500", "GEL", "BRAND102",
                                "Medicine", "Others", null, "Other", "AZ21232", 0, 0, "AZ21232",
                                "2.0", "Services", 0, "item field", "No", true
                        ),
                        "Success"
                },

                //Scenario 10:Long item Name
                {
                        "LongNameItemName",
                        createItemData(
                                generateRandomNumber("FAC"), generateRandomNumber("ITEM") + new String(new char[255]).replace('\0', 'A'), "UOM-!@#$%^&*()" + 100 + new Random().nextInt(900), "500", "GEL", "BRAND102",
                                "Medicine", "Others", null, "Other", "AZ21232", 0, 0, "AZ21232",
                                "2.0", "Services", 0, "item field", "No", true
                        ),
                        "Success"
                },

                //Scenario 11: minus qty auto replace plus:
                {
                        "MinusEnterQty",
                        createItemData(
                                generateRandomNumber("FAC"), generateRandomNumber("ITEM"), "UOM", "500", "AMP", "BRAND102",
                                "Medicine", "Others", null, "Other", "AZ21232", -10, -5, "AZ21232",
                                "2.0", "Services", 0, "item field", "No", true
                        ),
                        "Success"
                },
                //Scenario 12: greater then 100% discount
                {
                        "discountGreaterThen100",
                        createItemData(
                                generateRandomNumber("FAC"), generateRandomNumber("ITEM"), "UOM-!@#$%^&*()" + 100 + new Random().nextInt(900), "500", "AMP", "BRAND102",
                                "Medicine", "Others", null, "Other", "AZ21232", 10, 5, "AZ21232",
                                "2.0", "Services", 180, "item field", "No", true
                        ),
                        "Success"
                },

                //negative scenario:==
                // Scenario 13: Already exits
                {
                        "AlreadyExits",
                        createItemData(
                                "alreadyExits", "FAC-472", "Z21233", "Y21234", "GEL", "BRAND102",
                                "Medicine", "Others", "Others", "Other", "AZ21232", 32, 10, "1221232",
                                "2.0", "Services", 10, "item field", "Yes", false
                        ),
                        "Already Exists"
                },

                // Scenario 14: Missing Required Fields (e.g., itemCode, itemName)
                {
                        "MissingRequiredFields",
                        createItemData(
                                null, null, "500", "500", null, "BRAND102",
                                "Medicine", null, null, null, "AZ21232", 32, 10, "AZ21232",
                                "2.0", "Goods", 0, "item field", "No", true
                        ),
                        "Item Code is required"
                },

                // Scenario 15: Missing Required Fields (item Code)
                {
                        "MissingItemCode",
                        createItemData(
                                null, "FAC-472", "500", "500", "GEL", "BRAND102",
                                "Medicine", "Others", null, "Other", "AZ21232", 32, 10, "AZ21232",
                                "2.0", "Services", 10, "item field", "No", true
                        ),
                        "Item Code is required"
                },
                // Scenario 16: Missing Required Fields (itemName)
                {
                        "MissingItemName",
                        createItemData(
                                generateRandomNumber("FAC"), null, "500", "500", "GEL", "BRAND102",
                                "Medicine", "Others", null, "Other", "AZ21232", 32, 10, "AZ21232",
                                "2.0", "Services", 10, "item field", "No", true
                        ),
                        "Item Name is required"
                },
                // Scenario 17: Missing Required Fields (Category field)
                {
                        "MissingItemCategory",
                        createItemData(
                                generateRandomNumber("FAC"), generateRandomNumber("ITEM"), "500", "500", null, "BRAND102",
                                "Medicine", "Others", null, "Other", "AZ21232", 32, 10, "AZ21232",
                                "2.0", "Services", 10, "item field", "No", true
                        ),
                        "Category is required"
                },
                //  Scenario 18: Missing Required Fields (Category field)
                {
                        "MissingItemDragType",
                        createItemData(
                                generateRandomNumber("FAC"), generateRandomNumber("ITEM"), "500", "500", "GEL", "BRAND102",
                                "Medicine", null, null, "Other", "AZ21232", 32, 10, "AZ21232",
                                "2.0", "Services", 10, "item field", "No", true
                        ),
                        "Drug Type is required"
                },
                // Scenario 19: Missing Required Fields (Category field)
                {
                        "MissingItemUOMType",
                        createItemData(
                                generateRandomNumber("FAC"), generateRandomNumber("ITEM"), "500", "500", "GEL", "BRAND102",
                                "Medicine", "Others", null, null, "AZ21232", 32, 10, "AZ21232",
                                "2.0", "Services", 10, "item field", "No", true
                        ),
                        "Selling UOM is required"
                },


        };
    }

    // Test method for fillItemForm


    @Test(priority = 3)
    public void openMenu() {
        if (isLoginSuccessful) {
            menuPanelClick("Master", true, "Pharmacy", "");
        }
    }

    @Test(priority = 4, dataProvider = "itemDataProvider")
    public void testFillItemForm(String scenario, JsonNode itemData, String expectedResult) {
        driver.navigate().refresh();
        try {
            threadTimer(3000);
            String response = addItem(itemData, scenario);
            System.out.println("backend and front end warning:-" + response);
            threadTimer(3000);
            if (response != null && ((response.equals("error") || !response.contains(expectedResult)))) {
                Assert.fail("❌ Expected the test to fail for scenario or error backend : " + scenario);
            } else {
                System.out.println("✅Test Success:-" + scenario);
            }
            threadTimer(3000);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Expected the test to fail for scenario: " + scenario);

        }

    }

    // Method to fill the item form
    private void fillItemForm(JsonNode itemData, String itemCode, boolean editable, String scenario) {
        String itemType = itemData.get("itemType").asText();

        System.out.println("itemType " + itemType + "scanerio" + scenario + "validation:=" + itemData.get("drugType").isNull());
        threadTimer(1000);
        if (itemType.equals("Medicine")) {
            if (!itemData.get("drugType").isNull()) {
                selectField("drugType", itemData.get("drugType").asText());
            }
            selectField("scheduleId", itemData.get("scheduleId").asText());
        } else if (itemType.equals("Non-Medicinal")) {
            System.out.println("enable non medicinal");
            selectRadioButton("itemType", itemType);
        } else {
        }

        threadTimer(2000);
        if (itemCode.equals("alreadyExits")) {
            itemCode = itemTestCode;
        }
        if (editable) {
            enterText(By.cssSelector("input[formcontrolname='itemCode']"), nullValidation(itemCode) ? itemCode : "", true);
            enterText(By.cssSelector("input[formcontrolname='itemName']"), nullValidation(itemData.get("itemName").asText()) ? itemData.get("itemName").asText() : "", true);
        }
        enterText(By.cssSelector("input[formcontrolname='itemComposition']"), itemData.get("itemComposition").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='strength']"), itemData.get("strength").asText(), true);


        selectField("categoryId", itemData.get("categoryId").asText());
        selectField("brandId", itemData.get("brandId").asText());

        selectField("sellingUomId", itemData.get("sellingUomId").asText());

        enterText(By.cssSelector("input[formcontrolname='hsnCode']"), itemData.get("hsnCode").asText(), true);
        enterText(By.cssSelector("input[formcontrolname='reorderLevel']"), String.valueOf(itemData.get("reorderLevel").asInt()), true);
        enterText(By.cssSelector("input[formcontrolname='minQtyLevel']"), String.valueOf(itemData.get("minQtyLevel").asInt()), true);

        if (itemData.get("reorderLevel").asInt() < 0) {
            checkInputValue(By.cssSelector("input[formcontrolname='reorderLevel']"), itemData.get("reorderLevel").asInt());
        }
        if (itemData.get("minQtyLevel").asInt() < 0) {
            checkInputValue(By.cssSelector("input[formcontrolname='minQtyLevel']"), itemData.get("minQtyLevel").asInt());
        }
        enterText(By.cssSelector("input[formcontrolname='rackNumber']"), itemData.get("rackNumber").asText(), true);
        enterText(By.cssSelector("input[formcontrolname='version']"), itemData.get("version").asText(), true);
        if (!itemData.get("gstType").asText().equals("Goods")) {
            selectRadioButton("gstType", itemData.get("gstType").asText());
        }

        enterText(By.cssSelector("input[title='Maximum Discount(%)']"), String.valueOf(itemData.get("maxDiscount").asInt()), true);

        if (itemData.get("maxDiscount").asInt() > 100) {
            disCountAmountValid(By.cssSelector("input[title='Maximum Discount(%)']"), itemData.get("maxDiscount").asInt());
        }

        enterText(By.cssSelector("textarea[formcontrolname='itemDescription']"), itemData.get("itemDescription").asText(), true);
        selectRadioButton("returnable", itemData.get("returnable").asText());

        if (itemData.get("hideIpSummary").asBoolean()) {
            clickElement(By.cssSelector("input[formcontrolname='hideIpSummary']"));
        }
    }

    private void disCountAmountValid(By locator, int maxDiscount) {
        WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        String value = inputField.getAttribute("value");

        try {
            int discount = (int) Double.parseDouble(value); // Convert to int safely
            if (discount != maxDiscount) {
                System.out.println("✅Replace less than 100% discount");
            }
        } catch (NumberFormatException e) {
            //     System.out.println("Invalid discount value: " + value);
        }

    }

    private void checkInputValue(By locator, int minusValue) {

        WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        String value = inputField.getAttribute("value");
        if (Integer.parseInt(value) != minusValue) {
            System.out.println("✅negative value not accepted");
        }
    }

    // Method to add a new item
    private String addItem(JsonNode itemData, String scenario) {
        clickElement(By.xpath("//a[@id='Item' and contains(@class, 'nav-link')]"));
        clickElement(By.xpath("//button[contains(text(),'Add New')]"));
        String itemCode = itemData.get("itemCode").asText();
        fillItemForm(itemData, itemCode, true, scenario);
        clickElement(By.xpath("//button[normalize-space(text())='Save & Close']"));


        StringBuilder warningMessage = new StringBuilder();
        for (WebElement item : getMantoraryFields()) {
            warningMessage.append(item.getText() + ",");
        }
        if (getMantoraryFields().size() == 0) {
            String backendResponse = warningMessagePurchase().getText();
            //   System.out.println("backend response" + backendResponse);
            if (backendResponse != null && backendResponse.contains("Successfully")) {

                itemTestCode = itemCode;
                return backendResponse;
            } else if (backendResponse != null && backendResponse.contains("Already Exists")) {
                closePanel();
                return backendResponse;
            } else if (backendResponse.contains("Error")) {
                closePanel();
                return "error";
            } else {
                closePanel();
                return "error";
            }
        } else {
            closePanel();
            return warningMessage.toString();
        }
    }


    private void clickElement(By locator) {

        threadTimer(500);
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
        } catch (Exception e) {
            //System.out.println("Normal click failed, using JavaScript click...");

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
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement titleDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("select[formcontrolname='" + title + "']")));
                wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("select[formcontrolname='" + title + "'] option"), 1));
                Select select = new Select(titleDropdown);
                select.selectByVisibleText(value); // Selects the second option
            } catch (Exception e) {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement titleDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("select[formcontrolname='" + title + "']")));
                wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("select[formcontrolname='" + title + "'] option"), 1));
                Select select = new Select(titleDropdown);
                select.selectByIndex(1);
            }
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
            //System.out.println("Selected radio button: " + value);
            threadTimer(500);

        } catch (TimeoutException e) {
            //   System.out.println("Radio button with value '" + value + "' not found!");
        }
    }

    private void closePanel() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            By closeButtonLocator = By.cssSelector("button[aria-label='Close']");
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(closeButtonLocator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", closeButton);
            closeButton.click();
            // System.out.println("Close button clicked successfully.");
        } catch (Exception e) {
            //  System.out.println("Failed to close the panel: " + e.getMessage());
        }
    }
}