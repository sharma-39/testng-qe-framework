package org.test_automation.DBConnectivity;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MenuUtils {


    public void menuPanelClick(String panel, Boolean subPanel, String subPanelName, String screenCheck, WebDriver driver, WebDriverWait wait) {
        if (screenCheck.isEmpty()) {
            wait = new WebDriverWait(driver, Duration.ofSeconds(50));
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            WebElement menuButton = driver.findElement(By.id("mega-menu-nav-btn"));
            if (menuButton.isDisplayed()) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].click();", menuButton);
            } else {
                System.out.println(" ⚠ Menu Button is not visible, skipping click action.");
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("page-loader-wrapper")));

            System.out.println("Parent panel Name: " + panel);

            if (subPanel) {
                // Step 1: Click "Master" to Expand the Dropdown
                WebElement masterMenu = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[normalize-space()='" + panel + "']")));
                masterMenu.click();
                System.out.println("✅ Clicked on " + panel);

                // Step 2: Click "Pharmacy" Inside the Dropdown
                WebElement pharmacyLink = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//ul[contains(@class, 'show-submenu')]//a[normalize-space()='" + subPanelName + "']")));

                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].scrollIntoView(true);", pharmacyLink);
                js.executeScript("arguments[0].style.border='3px solid blue';", pharmacyLink); // Highlight

                try {
                    pharmacyLink.click();
                } catch (Exception e) {
                    System.out.println("Using JavaScript click as fallback.");
                    js.executeScript("arguments[0].click();", pharmacyLink);
                }

                System.out.println("✅ Clicked on " + subPanelName);
            } else {
                WebElement panelClick = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'" + panel + "')]")));

                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].style.border='3px solid red';", panelClick); // Highlight
                js.executeScript("arguments[0].scrollIntoView(true);", panelClick);
                panelClick.click();
                System.out.println("✅ Panel Click: " + panel);
            }
        } else {
            verifyPanelName(screenCheck, wait);
        }
    }

    public Boolean verifyPanelName(String expectedText, WebDriverWait wait) {
        try {
            WebElement breadcrumb = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//li[@class='breadcrumb-item active breadcrums-data' and normalize-space()='" + expectedText + "']")
            ));
            System.out.println("Breadcrumb found: " + breadcrumb.getText());
            if (breadcrumb.getText().contains(expectedText)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
