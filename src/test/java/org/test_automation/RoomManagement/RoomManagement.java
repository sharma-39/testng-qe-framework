package org.test_automation.RoomManagement;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.test_automation.DBConnectivity.XPathUtil;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RoomManagement extends LoginAndLocationTest {

    private final XPathUtil xPathUtil = new XPathUtil();
    List<String> roomTypes = Arrays.asList(
            "General Ward", "Private Room", "Semi-Private Room", "ICU (Intensive Care Unit)", "NICU (Neonatal Intensive Care Unit)",
            "PICU (Pediatric Intensive Care Unit)", "Emergency Room", "Operation Theater", "Post-Operative Room", "Recovery Room (PACU)",
            "Isolation Room", "Quarantine Room", "Labor Room", "Delivery Room", "Maternity Ward", "Consultation Room", "Examination Room",
            "Procedure Room", "Radiology Room", "Dialysis Room", "Chemotherapy Room", "Burn Unit", "Physiotherapy Room", "Pediatric Ward"
    );

    List<String> wardNames = Arrays.asList(
            "General Ward", "Male Ward", "Female Ward", "Pediatric Ward", "Surgical Ward", "Medical Ward", "ICU Ward", "NICU Ward",
            "PICU Ward", "Maternity Ward", "Postnatal Ward", "Antenatal Ward", "Orthopedic Ward", "Cardiology Ward", "Neurology Ward",
            "Oncology Ward", "Burn Ward", "Isolation Ward", "Infectious Disease Ward", "Psychiatric Ward", "Rehabilitation Ward",
            "ENT Ward", "Urology Ward", "Gastroenterology Ward", "High Dependency Unit (HDU)", "Emergency Ward", "Dialysis Ward"
    );

    List<String> floors = Arrays.asList(
            "Ground Floor", "First Floor", "Second Floor", "Third Floor", "Basement", "Mezzanine Floor", "Lower Ground Floor",
            "Service Floor", "Emergency Floor", "Operation Theater Floor", "ICU Floor", "Diagnostic Floor", "Maternity Floor",
            "Outpatient Floor (OPD)", "Inpatient Floor (IPD)", "Administrative Floor", "Pharmacy Floor", "Lab Floor", "Radiology Floor",
            "Rehabilitation Floor", "Specialty Floor", "Consultation Floor", "Executive Floor", "Cafeteria Floor", "Parking Basement"
    );

    String floorName;
    String wardName;
    String roomType;


    Random random = new Random();
    String randomRoomType = roomTypes.get(random.nextInt(roomTypes.size()));

    String roomNo;

    @Test(priority = 3)
    public void menuOpen() {
        menuPanelClick("Master", true, "Room", "");
    }

    @Test(priority = 4)
    public void floorCrud() {
        clickButtonElement(By.xpath("//a[@id='Floor' and contains(@class, 'nav-link')]"));
        clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));


        floorName = floors.get((int) (Math.random() * roomTypes.size()));
        xPathUtil.fillTextField("floorName", floorName, wait);

        String description = "desc";
        xPathUtil.fillTextArea("floorDesc", description, wait, driver);

        xPathUtil.formSubmitWithFormId("floorForm", driver, wait, false);

        clickButtonElement(By.xpath("//a[@id='Ward' and contains(@class, 'nav-link')]"));
        clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));


        wardName = wardNames.get((int) (Math.random() * roomTypes.size()));

        xPathUtil.fillTextField("wardName", wardName, wait);

        xPathUtil.selectField("floorId", floorName, XPathUtil.DropdownType.STANDARD, "", driver, wait);


        xPathUtil.fillTextArea("wardDesc", "description", wait, driver);

        xPathUtil.formSubmitWithFormId("wardForm", driver, wait, false);


        clickButtonElement(By.xpath("//a[@id='Room Type' and contains(@class, 'nav-link')]"));
        clickButtonElement(By.xpath("//button[contains(text(),'Add')]"));

        roomType = roomTypes.get((int) (Math.random() * roomTypes.size()));
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
