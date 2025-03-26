package org.test_automation.TapPharmacy;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

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
        filterSearchElemenet(supplierName, "Supplier Name");


        // 1. First ensure the date picker is open (as per your previous code)
        WebElement dateRangeDiv = driver.findElement(By.cssSelector("div#purchase-payments1"));
        dateRangeDiv.click();

        filterDate("1 Month");

        WebElement row = wait.until(ExpectedConditions.refreshed(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//td[span[contains(text(),'" + supplierName + "')]]/parent::tr")
        )));

        row.findElement(By.xpath(".//button[@title='Payment']")).click();


    }

    private void filterDate(String range) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        By visiblePicker = By.cssSelector("div.daterangepicker.ltr.show-ranges.opensright[style*='display: block']");
        wait.until(ExpectedConditions.visibilityOfElementLocated(visiblePicker)); // Ensure the picker is visible

        By oneMonthOption = By.cssSelector("div.daterangepicker.ltr.show-ranges.opensright[style*='display: block'] div.ranges li[data-range-key='" + range + "']");
        WebElement oneMonthElement = wait.until(ExpectedConditions.elementToBeClickable(oneMonthOption));

        oneMonthElement.click();
    }
}
