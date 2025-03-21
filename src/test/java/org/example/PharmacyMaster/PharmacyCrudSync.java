package org.example.PharmacyMaster;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.LoginUtil.LoginAndLocationTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PharmacyCrudSync extends LoginAndLocationTest {


    private final static HashMap<String, String> treeMap = new HashMap<>();

    @DataProvider(name = "supplierData")
    public Object[][] getSupplierData() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodes = objectMapper.readTree(new File("src/test/resources/supplier_data.json"));

        Object[][] data = new Object[jsonNodes.size()][1];
        for (int i = 0; i < jsonNodes.size(); i++) {
            data[i][0] = jsonNodes.get(i);
        }
        return data;
    }

    @DataProvider(name = "brandData")
    public Object[][] getBrandData() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodes = objectMapper.readTree(new File("src/test/resources/brand_data.json"));

        Object[][] data = new Object[jsonNodes.size()][1];
        for (int i = 0; i < jsonNodes.size(); i++) {
            data[i][0] = jsonNodes.get(i);
        }
        return data;
    }

    @DataProvider(name = "itemData")
    public Object[][] getItemData() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNodes = objectMapper.readTree(new File("src/test/resources/items.json"));

        return new Object[][]{{jsonNodes}};
    }

    @Test(priority = 3)
    public void menuClick() {
        if (isLoginSuccessful) {
            menuPanelClick("Master", true, "Pharmacy", "");
        }
    }


    @Test(priority = 4, dependsOnMethods = "menuClick", dataProvider = "supplierData")
    public void supplierFlow(JsonNode supplierData) {
        System.out.println("Process Create Pharmacy Supplier flow---");
        String supplierCode = addSupplier(supplierData);
        System.out.println("Completed Create Pharmacy Supplier flow---");
//        System.out.println("Process Edit Pharmacy Supplier flow---" + supplierCode);
//        editSupplier(supplierData, supplierCode);
//        System.out.println("Completed Edit Pharmacy Supplier flow---" + supplierCode);

    }

    @Test(priority = 5, dependsOnMethods = "menuClick", dataProvider = "brandData")
    public void brandFlow(JsonNode brandData) {
        String brandCode = addBrand(brandData);

//        editBrand(brandCode, brandData);

    }

    @Test(priority = 6, dependsOnMethods = "menuClick")
    public void uomCrud() {
        threadTimer(3000);
        String uomCode = addUOM();

        //String uomName = editUOM(uomCode);

        //    deleteUOM(uomCode);
    }


    @Test(priority = 7, dependsOnMethods = "menuClick")
    public void itemCategoryCrud() {
        threadTimer(3000);
        String itemCode = addItemCategory();
      //  String itemCategory = editItemCategory(itemCode);


        //deleteItemCategory(itemCode);


    }


    @Test(priority = 8, dependsOnMethods = "menuClick", dataProvider = "itemData")
    public void itemCrud(JsonNode itemData) {
        threadTimer(3000);
        System.out.println("itemData" + itemData.size());
        JsonNode data = itemData.get(0);
        String itemCode = addItem(data);

//        data = itemData.get(1);
//        editItem(data, itemCode);

    }


    private void editItem(JsonNode itemData, String itemCode) {
        threadTimer(3000);
        clickButtonInRow(itemCode, "Edit");
        fillItemForm(itemData, itemCode, false);
        clickElement(By.xpath("//button[contains(text(),'Save & Close')]"));
        System.out.println("Item edited successfully.");
    }

    private void fillItemForm(JsonNode itemData, String itemCode, boolean editable) {
        if (editable) {
            enterText(By.cssSelector("input[formcontrolname='itemCode']"), itemCode, true);
            enterText(By.cssSelector("input[formcontrolname='itemName']"), "ITEM-" + 100 + new Random().nextInt(900), true);
        }
        enterText(By.cssSelector("input[formcontrolname='itemComposition']"), itemData.get("itemComposition").asText(), true);
        enterText(By.cssSelector("input[formcontrolname='strength']"), itemData.get("strength").asText(), true);

        selectField("categoryId", treeMap.get("itemCategoryName"));
        selectField("brandId",  treeMap.get("brandName"));

        selectField("sellingUomId", treeMap.get("uomName"));
        String itemType = itemData.get("itemType").asText();


        selectRadioButton("itemType", itemType);


        if (!"Non-Medicinal".equals(itemType)) {
            selectField("drugType", itemData.get("drugType").asText());
            selectField("scheduleId", itemData.get("scheduleId").asText());
        }


        enterText(By.cssSelector("input[formcontrolname='hsnCode']"), itemData.get("hsnCode").asText(), true);
        enterText(By.cssSelector("input[formcontrolname='reorderLevel']"), String.valueOf(itemData.get("reorderLevel").asInt()), true);
        enterText(By.cssSelector("input[formcontrolname='minQtyLevel']"), String.valueOf(itemData.get("minQtyLevel").asInt()), true);
        enterText(By.cssSelector("input[formcontrolname='rackNumber']"), itemData.get("rackNumber").asText(), true);
        enterText(By.cssSelector("input[formcontrolname='version']"), itemData.get("version").asText(), true);
        selectRadioButton("gstType", itemData.get("gstType").asText());
        enterText(By.cssSelector("input[title='Maximum Discount(%)']"), String.valueOf(itemData.get("maxDiscount").asInt()), true);
        enterText(By.cssSelector("textarea[formcontrolname='itemDescription']"), itemData.get("itemDescription").asText(), true);
        selectRadioButton("returnable", itemData.get("returnable").asText());

        if (itemData.get("hideIpSummary").asBoolean()) {
            clickElement(By.cssSelector("input[formcontrolname='hideIpSummary']"));
        }
    }

    private String addItem(JsonNode itemData) {

        clickElement(By.xpath("//a[@id='Item' and contains(@class, 'nav-link')]"));
        clickElement(By.xpath("//button[contains(text(),'Add New')]"));
        String itemCode = generateRondamNumber("FAC");
        fillItemForm(itemData, itemCode, true);
        clickElement(By.xpath("//button[normalize-space(text())='Save & Close']"));

        return itemCode;
    }

    private void deleteItemCategory(String itemCode) {
        threadTimer(2000);
        clickButtonInRow(itemCode, "Edit");
        threadTimer(1000);
        selectRadioButton("active", "Inactive");
        clickElement(By.xpath("//button[contains(text(),'Save & Close')]"));

    }

    private String editItemCategory(String itemCode) {
        threadTimer(2000);
        clickButtonInRow(itemCode, "Edit");
        threadTimer(1000);

        String itemCategory = "ITEM" + "-" + new Random().nextInt(1000);
        enterText(By.cssSelector("input[title='Item Category']"), itemCategory, true);

        selectRadioButton("active", "Active");

        enterText(By.cssSelector("textarea[formcontrolname='description']"),
                "item category field ", true);

        clickElement(By.cssSelector("input[formcontrolname='hideIpSummary']"));

        clickElement(By.xpath("//button[contains(text(),'Save & Close')]"));

        return itemCategory;
    }

    private String addItemCategory() {
        clickElement(By.xpath("//a[@id='Item Category' and contains(@class, 'nav-link')]"));
        clickElement(By.xpath("//button[contains(text(),'Add New')]"));


        String itemCode = generateRandomNumber("ITEM");
        enterText(By.cssSelector("input[title='Item Code']"), itemCode, true);
        String itemCategory="ITEM" + "-" + new Random().nextInt(1000);
        enterText(By.cssSelector("input[title='Item Category']"), itemCategory, true);

        selectRadioButton("active", "Active");

        enterText(By.cssSelector("textarea[formcontrolname='description']"),
                "item category field ", true);

        clickElement(By.cssSelector("input[formcontrolname='hideIpSummary']"));

        clickElement(By.xpath("//button[contains(text(),'Save & Close')]"));
        treeMap.put("itemCategoryName", itemCategory);
        return itemCode;
    }


    private void deleteUOM(String uomCode) {

        threadTimer(1000);
        clickButtonInRow(uomCode, "Edit");


        selectRadioButton("status", "Inactive");


        clickElement(By.xpath("//button[contains(text(),'Save & Close')]"));

    }

    private String editUOM(String uomCode) {
        threadTimer(2000);
        clickButtonInRow(uomCode, "Edit");
        threadTimer(1000);
        String uomName = "UOM" + "-" + new Random().nextInt(1000);
        enterText(By.cssSelector("input[title='UOM']"), uomName, true);


        clickElement(By.xpath("//button[contains(text(),'Save & Close')]"));

        return uomName;

    }

    private String addUOM() {
        clickElement(By.xpath("//a[@id='UOM' and contains(@class, 'nav-link')]"));
        clickElement(By.xpath("//button[contains(text(),'Add New')]"));

        String uomCode = generateRandomNumber("UOM");
        String uomName = "UOM" + "-" + new Random().nextInt(1000);
        enterText(By.cssSelector("input[title='UOM Code']"), uomCode, true);
        enterText(By.cssSelector("input[title='UOM']"), uomName, true);


        clickElement(By.xpath("//button[contains(text(),'Save & Close')]"));

        treeMap.put("uomName", uomName);
        return uomCode;
    }

    private void editBrand(String brandCode, JsonNode brandData) {

        threadTimer(2000);
        clickButtonInRow(brandCode, "Edit");
        threadTimer(2000);

        enterText(By.cssSelector("input[title='Brand Name']"), brandData.get("brand_name").asText() + +new Random().nextInt(1000), true);


        enterText(By.cssSelector("textarea[formcontrolname='description']"),
                brandData.get("description").asText(), true);

        selectRadioButton("active", brandData.get("status").asText());

        clickElement(By.xpath("//button[contains(text(),'Save & Close')]"));

    }

    private String addBrand(JsonNode brandData) {

        threadTimer(1000);
        clickElement(By.xpath("//a[@id='Brand' and contains(@class, 'nav-link')]"));

        threadTimer(3000);
        clickElement(By.xpath("//button[contains(text(),'Add New')]"));

        String brandCode = generateRandomNumber("BR");
        String brandName = brandData.get("brand_name").asText() + +new Random().nextInt(1000);
        enterText(By.cssSelector("input[title='Brand Code']"), brandCode, true);
        enterText(By.cssSelector("input[title='Brand Name']"), brandName, true);


        enterText(By.cssSelector("textarea[formcontrolname='description']"),
                brandData.get("description").asText(), true);

        selectRadioButton("active", brandData.get("status").asText());

        clickElement(By.xpath("//button[contains(text(),'Save & Close')]"));

        treeMap.put("brandName", brandName);
        return brandCode;
    }


    public String addSupplier(JsonNode supplierData) {
        verifyPanelName("Pharmacy Master");
        System.out.println("Successfully loaded Pharmacy Master");

        clickElement(By.xpath("//a[@id='Supplier' and contains(@class, 'nav-link')]"));
        clickElement(By.xpath("//button[contains(text(),'Add New')]"));

        String supplierName = supplierData.get("supplierNamePrefix").asText() + " " + new Random().nextInt(100);
        String phoneNumber = generatePhoneNumber();
        String email = generateEmail(supplierName, supplierData.get("emailDomain").asText());

        String supplierCode = generateRandomNumber(supplierData.get("supplierCodePrefix").asText());
        enterText(By.cssSelector("input[formcontrolname='code']"),
                supplierCode, true);

        enterText(By.cssSelector("input[title='Supplier Name']"), supplierName, true);

        enterText(By.cssSelector("input[formcontrolname='drugLiscense']"),
                generateRandomNumber(supplierData.get("drugLicensePrefix").asText()), true);

        enterText(By.cssSelector("input[formcontrolname='phoneCode']"),
                supplierData.get("phone").get("countryCode").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='phoneNumber']"), phoneNumber, true);

        enterText(By.cssSelector("input[formcontrolname='email']"), email, true);

        enterText(By.cssSelector("input[formcontrolname='fax']"),
                supplierData.get("fax").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='gst']"),
                supplierData.get("gstNumber").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='address']"),
                supplierData.get("address").get("street").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='location']"),
                supplierData.get("address").get("location").asText(), true);

        selectField("state", supplierData.get("address").get("state").asText());

        enterText(By.cssSelector("input[formcontrolname='city']"),
                supplierData.get("address").get("city").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='zipCode']"),
                supplierData.get("address").get("zipCode").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='website']"),
                supplierData.get("website").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='pointOfContactName']"),
                supplierData.get("pointOfContact").get("name").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='pointOfContactPhoneCode']"),
                supplierData.get("pointOfContact").get("phone").get("countryCode").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='pointOfContactNumber']"), generatePhoneNumber(), true);

        clickElement(By.xpath("//button[contains(text(),'Save & Close')]"));

        return supplierCode;
    }

    public void editSupplier(JsonNode supplierData, String SupplierCode) {


        threadTimer(2000);
        clickButtonInRow(SupplierCode, "Edit");

        threadTimer(2000);


        String supplierName = supplierData.get("supplierNamePrefix").asText() + " " + new Random().nextInt(100);
        String phoneNumber = generatePhoneNumber();
        String email = generateEmail(supplierName, supplierData.get("emailDomain").asText());

//        enterText(By.cssSelector("input[formcontrolname='code']"),
//                generateRandomNumber(supplierData.get("supplierCodePrefix").asText()), true);

        enterText(By.cssSelector("input[title='Supplier Name']"), supplierName, true);

        enterText(By.cssSelector("input[formcontrolname='drugLiscense']"),
                generateRandomNumber(supplierData.get("drugLicensePrefix").asText()), true);

        enterText(By.cssSelector("input[formcontrolname='phoneCode']"),
                supplierData.get("phone").get("countryCode").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='phoneNumber']"), phoneNumber, true);

        enterText(By.cssSelector("input[formcontrolname='email']"), email, true);

        enterText(By.cssSelector("input[formcontrolname='fax']"),
                supplierData.get("fax").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='gst']"),
                supplierData.get("gstNumber").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='address']"),
                supplierData.get("address").get("street").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='location']"),
                supplierData.get("address").get("location").asText(), true);

        selectField("state", supplierData.get("address").get("state").asText());

        enterText(By.cssSelector("input[formcontrolname='city']"),
                supplierData.get("address").get("city").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='zipCode']"),
                supplierData.get("address").get("zipCode").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='website']"),
                supplierData.get("website").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='pointOfContactName']"),
                supplierData.get("pointOfContact").get("name").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='pointOfContactPhoneCode']"),
                supplierData.get("pointOfContact").get("phone").get("countryCode").asText(), true);

        enterText(By.cssSelector("input[formcontrolname='pointOfContactNumber']"), generatePhoneNumber(), true);

        clickElement(By.xpath("//button[contains(text(),'Save & Close')]"));
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


    private void enterText(By locator, String text, boolean editable) {
        WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        inputField.clear();
        inputField.sendKeys(text);
        threadTimer(500);
    }

    public String generateRondamNumber(String prefix) {

        String datePart = Instant.now()
                .atZone(ZoneId.systemDefault()) // Convert to system time zone
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")); // Format date

        int randomPart = 100 + new Random().nextInt(900); // Generate a 3-digit random number

        String generateNumber = prefix + "-" + datePart + "-" + randomPart; // Create invoice number

        System.out.println("Generated Number: " + generateNumber);
        return generateNumber;
    }


    private void selectField(String title, String value) {
        WebElement titleDropdown = driver.findElement(By.cssSelector("select[formcontrolname='" + title + "']"));
        Select select = new Select(titleDropdown);
        select.selectByVisibleText(value);
        threadTimer(500);
    }


    public String generateRandomNumber(String prefix) {


        String datePart = Instant.now()
                .atZone(ZoneId.systemDefault()) // Convert to system time zone
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")); // Format date

        int randomPart = 100 + new Random().nextInt(900); // Generate a 3-digit random number

        String generateNumber = prefix + "-" + datePart + "-" + randomPart; // Create invoice number

        System.out.println("Generated Number: " + generateNumber);
        return generateNumber;
    }

    public String generatePhoneNumber() {
        Random random = new Random();
        int firstDigit = 7 + random.nextInt(3); // Ensures first digit is 7, 8, or 9
        StringBuilder phoneNumber = new StringBuilder();
        phoneNumber.append(firstDigit);
        for (int i = 0; i < 9; i++) {
            phoneNumber.append(random.nextInt(10));
        }
        return phoneNumber.toString();
    }

    public String generateEmail(String supplierName, String emailDomain) {
        Random random = new Random();
        String formattedName = supplierName.toLowerCase().replace(" ", "").replaceAll("[^a-zA-Z0-9]", "");
        String[] domains = {"supplier.com", "traders.net", "business.org"};
        return formattedName + random.nextInt(1000) + "@" + domains[random.nextInt(domains.length)];
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

    private void selectRadioButton(String formControlName, String value) {
        try {
            WebElement radioButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//input[@formcontrolname='" + formControlName + "'][@value='" + value + "'] | //label[span[contains(text(), '" + value + "')]]/input[@formcontrolname='" + formControlName + "'] ")
            ));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", radioButton);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", radioButton);
            System.out.println("Selected radio button: " + value);
            threadTimer(500);
        } catch (TimeoutException e) {
            System.out.println("Radio button with value '" + value + "' not found!");
        }
    }

}
