package org.test_automation.RoomManagement;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.test_automation.DBConnectivity.XPathUtil;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.testng.annotations.Test;

import java.util.Random;

public class RoomManagement extends LoginAndLocationTest {


    private final XPathUtil xPathUtil = new XPathUtil();
    String floorName;
    String wardName;
    String roomType;

    String roomNo;

    @Test(priority = 3)
    public void menuOpen() {
        menuPanelClick("Master", true, "Room", "");
    }

    @Test(priority = 4)
    public void floorCrud() {
        clickButtonElement(By.xpath("//a[@id='Floor' and contains(@class, 'nav-link')]"));
        clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));


        floorName = generateRondamNumber("FLOOR");
        xPathUtil.fillTextField("floorName", floorName, wait);

        String description = "desc";
        xPathUtil.fillTextArea("floorDesc", description, wait, driver);

        xPathUtil.formSubmitWithFormId("floorForm", driver, wait, false);

        clickButtonElement(By.xpath("//a[@id='Ward' and contains(@class, 'nav-link')]"));
        clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));


        wardName = generateRondamNumber("WARD");

        xPathUtil.fillTextField("wardName", wardName, wait);

        xPathUtil.selectField("floorId", floorName, XPathUtil.DropdownType.STANDARD, "", driver, wait);


        xPathUtil.fillTextArea("wardDesc", "description", wait, driver);

        xPathUtil.formSubmitWithFormId("wardForm", driver, wait, false);


        clickButtonElement(By.xpath("//a[@id='Room Type' and contains(@class, 'nav-link')]"));
        clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));

        roomType = generateRondamNumber("RT");

        xPathUtil.fillTextField("roomTypeName", roomType, wait);

        String uomId = "TEST-UOM";
        String unitPrice = "1000";
        xPathUtil.selectField("uomId", uomId, XPathUtil.DropdownType.STANDARD, "", driver, wait);
        xPathUtil.fillTextField("unitPrice", unitPrice, wait);

        Boolean autoPost = false;
        xPathUtil.clickCheckBox(autoPost, wait, driver, "Auto Post");

        xPathUtil.formSubmitWithFormId("roomTypeForm", driver, wait, false);

        clickButtonElement(By.xpath("//a[@id='Room' and contains(@class, 'nav-link')]"));
        clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));

        threadTimer(4000);

        Random random = new Random();
        int randomThreeDigit = 100 + random.nextInt(900); // generates number between 100 and 999
        roomNo = "RM-" + randomThreeDigit;

        xPathUtil.fillTextField("roomNo", roomNo, wait);


        xPathUtil.selectField("roomTypeId", roomType, XPathUtil.DropdownType.STANDARD, "", driver, wait);

        threadTimer(2000);
        System.out.println("floor name:---" + floorName);
        xPathUtil.selectField("floorId", floorName, XPathUtil.DropdownType.DISPLAY_NONE, "", driver, wait);

        threadTimer(2000);

        xPathUtil.selectField("wardId", wardName, XPathUtil.DropdownType.DISPLAY_NONE, "", driver, wait);

        xPathUtil.fillTextArea("roomDesc", "desc", wait, driver);

        xPathUtil.formSubmitWithFormId("roomForm", driver, wait, false);


        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("location.reload()");

        clickButtonElement(By.xpath("//a[@id='Bed' and contains(@class, 'nav-link')]"));
        clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));


        String bedNo = generateRondamNumber("BED-NO");

        xPathUtil.fillTextField("bedNo", bedNo, wait);

        xPathUtil.selectField("roomTypeId", roomType, XPathUtil.DropdownType.DISPLAY_NONE, "", driver, wait);

        threadTimer(2000);
        xPathUtil.selectField("roomId", roomNo, XPathUtil.DropdownType.DISPLAY_NONE, "", driver, wait);


        xPathUtil.optionSelect("usedStatus", "Active", "", wait, driver);

        xPathUtil.formSubmitWithFormId("roomForm", driver, wait, false);


    }
}
