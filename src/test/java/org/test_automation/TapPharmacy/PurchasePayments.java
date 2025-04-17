package org.test_automation.TapPharmacy;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.test_automation.DBConnectivity.MenuUtils;
import org.test_automation.DBConnectivity.XPathUtil;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.testng.annotations.Test;

public class PurchasePayments extends LoginAndLocationTest {

    private final MenuUtils menuUtils=new MenuUtils();
    private final XPathUtil xPathUtil=new XPathUtil();

    @Test(priority = 3)
    public void menuClick() {
        if (isLoginSuccessful) {
            menuUtils.menuPanelClick("Purchase Payments", false, "", "",driver,wait);
        }
    }

    @Test(priority = 4, dependsOnMethods = "menuClick")
    public void addPurchasePayments() {

        threadTimer(3000);
        filterSearchClick();

        String supplierName = "Supplier 67";
        filterSearchElemenet(supplierName, "Supplier Name", "Text");


        // 1. First ensure the date picker uis open (as per your previous code)
        WebElement dateRangeDiv = driver.findElement(By.cssSelector("div#purchase-payments1"));
        dateRangeDiv.click();

        filterRangeSelect("1 Month");

        threadTimer(3000);

//        filterSearchElemenet("05-05-2024","pharmacyinvoiceDate","Date");
//
//        filterSearchElemenet("05-01-2025","pharmacygrnDate","Date");


        WebElement row = wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//td[span[contains(text(),'" + supplierName + "')]]/parent::tr")
        )));

        row.findElement(By.xpath(".//button[@title='Payment']")).click();


        WebElement balanceInput = driver.findElement(By.xpath("//label[text()='Balance Amount']/following-sibling::input"));
        String balanceValue = balanceInput.getAttribute("value");
        System.out.println("Balance Amount: " + balanceValue);

        WebElement amountInput = driver.findElement(
                By.xpath("//label[contains(.,'Amount')]/following::app-input-number//input[@type='number']")
        );

// Clear and enter value
        amountInput.clear();
        amountInput.sendKeys(balanceValue); // Enter your desired amount (0-8862)

        xPathUtil.clickButtonElement(By.xpath("//button[contains(text(), 'Pay')]"),driver,wait);


    }


}
