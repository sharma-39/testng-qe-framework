package org.test_automation.TapPharmacy;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.testng.annotations.Test;

public class PurchasePayments extends LoginAndLocationTest {


    @Test(priority = 3)
    public void menuClick() {
        if (isLoginSuccessful)
            menuPanelClick("Purchase Payments", false, "", "");
    }

    @Test(priority = 4)
    public void addPurchasePayments() {

        threadTimer(3000);
        filterSearchClick();

        String supplierName="Supplier 67";
        filterSearchElemenet(supplierName, "Supplier Name", "Text");


        // 1. First ensure the date picker is open (as per your previous code)
        WebElement dateRangeDiv = driver.findElement(By.cssSelector("div#purchase-payments1"));
        dateRangeDiv.click();

        filterRangeSelect("1 Month");

        threadTimer(3000);

        filterSearchClick();
        threadTimer(2000);
        filterSearchClick();
        filterSearchElemenet("05-05-2024","pharmacyinvoiceDate","Date");

        filterSearchElemenet("01-05-2025","pharmacygrnDate","Date");


//        WebElement row = wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(
//                By.xpath("//td[span[contains(text(),'" + supplierName + "')]]/parent::tr")
//        )));
//
//        row.findElement(By.xpath(".//button[@title='Payment']")).click();



    }




}
