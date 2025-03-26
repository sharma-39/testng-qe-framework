package org.test_automation.TapPharmacy;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.test_automation.FlowHelper.ReturnHelper;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.testng.annotations.Test;

public class SalesReturn extends LoginAndLocationTest {

    private final ReturnHelper returnHelper;

    public SalesReturn() {
        this.returnHelper = new ReturnHelper();
    }


    @Test(priority = 3, dependsOnMethods = {"testLogin"})
    public void billGenerate() {

        if (isLoginSuccessful) {
            System.out.println("login successfully :-- itemList" + getPatientSearchCode());

            String patientCode = getPatientSearchCode();


            menuPanelClick("Pharmacy", false, "", "");
            String billNo = findBillNumber(patientCode);
            if (billNo != null) {
                returnHelper.createSalesReturnBill(this, driver, wait, "Returns", "Sales Returns", billNo);
            }
        }

    }

    private String findBillNumber(String patientCode) {

        filterSearchClick();
        filterSearchElemenet(patientCode, "patientCode");
        String billNumber = findAndClickDropdownAndPrescription(patientCode, wait, driver);

        return billNumber;

    }


    public String findAndClickDropdownAndPrescription(String patientCode, WebDriverWait wait, WebDriver driver) {

        try {
            // ✅ Step 1: Find the row containing the patient name
            WebElement row = wait.until(ExpectedConditions.refreshed(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//td[span[contains(text(),'" + patientCode + "')]]/parent::tr"))
            ));
            System.out.println("Patient row found.");

            WebElement refreshedCell = wait.until(ExpectedConditions.refreshed(
                    ExpectedConditions.presenceOfNestedElementLocatedBy(
                            row,
                            By.xpath(".//td[span[@title='Bill Number']]")
                    )
            ));

            System.out.println("=============== code" + refreshedCell.getText());
            // ✅ Step 2: Find and click the dropdown inside this row

            return refreshedCell.getText().trim();
        } catch (StaleElementReferenceException e) {
            System.out.println("StaleElementReferenceException caught. Retrying...");
            return null;
        } catch (TimeoutException e) {
            System.out.println("TimeoutException caught. Retrying...");
            return null;
        }

    }


}
