package org.test_automation.TapPharmacy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.test_automation.FlowHelper.PurchaseFlowHelper;
import org.test_automation.FlowHelper.ReturnHelper;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.test_automation.SupplierDTO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class PurchaseReturn extends LoginAndLocationTest {

    String purchaseSupplierName = null;

    private final ReturnHelper returnHelper;

    private final PurchaseFlowHelper purchaseFlowHelper;

    public PurchaseReturn() {
        this.purchaseFlowHelper = new PurchaseFlowHelper();
        this.returnHelper = new ReturnHelper();
    }

    @Test(priority = 3)
    public void menuClick() {

        if (isLoginSuccessful) {
            menuPanelClick("Master", true, "Pharmacy", "");
        }
    }


    @DataProvider(name = "supplierData")
    public Object[][] getSupplierData(Method method) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new File("src/test/resources/purchase_return_data.json"));

        return new Object[][]{
                {mapper.treeToValue(rootNode, SupplierDTO.class)}
        };
    }

//    @Test(priority = 4, dataProvider = "supplierData")
//    public void addSupplierData(SupplierDTO supplierData) {
//        System.out.println("Process Create Pharmacy Supplier flow---");
//        String supplierName = returnHelper.addSupplier(this, supplierData, wait, driver);
//        System.out.println("Supplier created with code: " + supplierName);
//        System.out.println("Completed Create Pharmacy Supplier flow---");
//        purchaseSupplierName = supplierName;
//    }
//
//    @Test(priority = 5)
//    public void addPurchase() {
//
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        try {
//            // First, log the input to verify what we're actually receiving
//            System.out.println("Raw input data: " + tempStockData);
//
//            // Parse JSON string to JsonNode
//            JsonNode rootNode = objectMapper.readTree(String.valueOf(tempStockData));
//            // Create a deep copy first if you need to keep the original
//
//            // Debug: print the entire parsed structure
//            System.out.println("Parsed JSON structure: " + rootNode.toPrettyString());
//
//            // Check if root is an object (sometimes JSON might be just an array)
//            if (!rootNode.isObject()) {
//                throw new RuntimeException("Root node is not an object");
//            }
//
//            // Get the stock node with better error reporting
//            JsonNode stockNode = rootNode.get("stock");
//            if (stockNode == null) {
//                throw new RuntimeException("'stock' node is missing. Available fields: " +
//                        rootNode.fieldNames());
//            }
//            if (!stockNode.isObject()) {
//                throw new RuntimeException("'stock' node is not an object");
//            }
//
//            ObjectNode stockObjectNode = (ObjectNode) stockNode;
//
//            // 4. Process supplier with null-safe approach
//            JsonNode supplierNode = stockObjectNode.path("supplier");
//            if (supplierNode.isMissingNode()) {
//                throw new RuntimeException("'supplier' node is missing in stock");
//            }
//            if (!supplierNode.isObject()) {
//                throw new RuntimeException("'supplier' should be an object");
//            }
//
//            // 5. Update supplier name while preserving other fields
//            ObjectNode supplierObjectNode = (ObjectNode) supplierNode;
//            supplierObjectNode.put("name", purchaseSupplierName);
//
//            // 6. Process items array
//            JsonNode itemsNode = stockObjectNode.path("items");
//            if (itemsNode.isMissingNode()) {
//                throw new RuntimeException("'items' array is missing in stock");
//            }
//            if (!itemsNode.isArray()) {
//                throw new RuntimeException("'items' should be an array");
//            }
//
//
//
//            System.out.println("Parsed Modified JSON structure: " + rootNode.toPrettyString());
//            // Call your helper method
//            purchaseFlowHelper.addStockPurchase(
//                    this,
//                    new ArrayList<>(),
//                    driver,
//                    wait,
//                    "Pharmacy",
//                   rootNode,
//                    "custom");
//
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("JSON processing error: " + e.getMessage(), e);
//        } catch (IOException e) {
//            throw new RuntimeException("IO error: " + e.getMessage(), e);
//        }
//
//    }


//    @Test(priority = 5)
//    public void addPurchaseReturn() {
//
//        System.out.println("Raw input data: " + tempStockData);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        // Parse JSON string to JsonNode
//        JsonNode rootNode = null;
//        try {
//            rootNode = objectMapper.readTree(String.valueOf(tempStockData));
//        } catch (JsonProcessingException e) {
//
//        }
//
//        System.out.println("Raw input data: " + rootNode.toPrettyString());
//        System.out.println("items"+rootNode.get("stock").get("items").toPrettyString());
//        returnHelper.createPurchaseReturnBill(this, driver, wait, "Returns", "Purchase Returns", purchaseSupplierName,rootNode.get("stock").get("items"));
//
//        System.out.println("Successfully completed purchase return");
//    }

    @Test(priority = 6)
    public void purchaseReturnPaid() {
        menuPanelClick("Returns", true, "Purchase Returns", "");
        threadTimer(3000);
        purchaseSupplierName = "Supplier 67";
        WebElement row = wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//td[span[contains(text(),'" + purchaseSupplierName + "')]]/parent::tr")
        )));

        row.findElement(By.xpath(".//button[@title='Payment']")).click();

        threadTimer(2000);
        WebElement balanceInput = driver.findElement(
                By.xpath("//input[@type='number' and @title='Balance Amount']")
        );
        String balanceValue = balanceInput.getAttribute("value");
        System.out.println("Balance Amount: " + balanceValue);
        enterQtyText(By.xpath("//input[@type='number' and @title='Purchase Return Amount']"), balanceValue, false, wait, driver);

        WebElement paymentDropdown = driver.findElement(
                By.xpath("//select[@title='Payment Type']")
        );

// Create a Select object
        Select paymentType = new Select(paymentDropdown);

// Select by visible text (e.g., "Cash")
        paymentType.selectByVisibleText("Cash");

        clickButtonElement(By.xpath("//button[contains(text(), 'Pay')]"));

    }


    private void enterQtyText(By xpath, String number, boolean b, WebDriverWait wait, WebDriver driver) {

        WebElement qtyInput = driver.findElement(xpath);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        qtyInput.clear();
        qtyInput.sendKeys(number);
        qtyInput.sendKeys(Keys.TAB);
    }


}
