package org.test_automation.RoomManagement;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.test_automation.DBConnectivity.XPathUtil;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Test class for Room Management CRUD operations.
 * Handles creation of floors, wards, room types, rooms, and beds in a sequential manner.
 */
public class RoomManagement extends LoginAndLocationTest {

    private final XPathUtil xPathUtil = new XPathUtil();

    // Predefined lists for test data
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

    /**
     * Opens the Room Management menu.
     */
    @Test(priority = 3)
    public void menuOpen() {
        menuPanelClick("Master", true, "Room", "");
    }

    /**
     * Performs CRUD operations for Floor, Ward, Room Type, Room, and Bed entities.
     * Generates random test data and validates form submissions.
     */
    @Test(priority = 4)
    public void floorCrud() {
        // Floor Creation
        navigationTap("Floor");
        String floorName = floors.get(new Random().nextInt(floors.size()));
        xPathUtil.fillTextField("floorName", floorName, wait);
        xPathUtil.fillTextArea("floorDesc", "Automation test description", wait, driver);
        xPathUtil.formSubmitWithFormId("floorForm", driver, wait, false);

        // Ward Creation
        navigationTap("Ward");
        String wardName = wardNames.get(new Random().nextInt(wardNames.size()));
        xPathUtil.fillTextField("wardName", wardName, wait);
        xPathUtil.selectField("floorId", floorName, XPathUtil.DropdownType.STANDARD, "", driver, wait);
        xPathUtil.fillTextArea("wardDesc", "Automation test description", wait, driver);
        xPathUtil.formSubmitWithFormId("wardForm", driver, wait, false);

        // Room Type Creation
        navigationTap("Room Type");
        String roomType = roomTypes.get(new Random().nextInt(roomTypes.size()));
        xPathUtil.fillTextField("roomTypeName", roomType, wait);
        xPathUtil.selectField("uomId", "TEST-UOM", XPathUtil.DropdownType.STANDARD, "", driver, wait);
        xPathUtil.fillTextField("unitPrice", "1000", wait);
        xPathUtil.clickCheckBox(false, wait, driver, "Auto Post");
        xPathUtil.formSubmitWithFormId("roomTypeForm", driver, wait, false);

        // Room Creation
        navigationTap("Room");
        threadTimer(4000); // Wait for UI stabilization
        String roomNo = "RM-" + (100 + new Random().nextInt(900)); // Generates RM-100 to RM-999
        xPathUtil.fillTextField("roomNo", roomNo, wait);
        xPathUtil.selectField("roomTypeId", roomType, XPathUtil.DropdownType.STANDARD, "", driver, wait);
        xPathUtil.selectField("floorId", floorName, XPathUtil.DropdownType.DISPLAY_NONE, "", driver, wait);
        xPathUtil.selectField("wardId", wardName, XPathUtil.DropdownType.DISPLAY_NONE, "", driver, wait);
        xPathUtil.fillTextArea("roomDesc", "Automation test description", wait, driver);
        xPathUtil.formSubmitWithFormId("roomForm", driver, wait, false);

        // Refresh page to ensure new room is loaded
        ((JavascriptExecutor) driver).executeScript("location.reload()");

        // Bed Creation
        navigationTap("Bed");
        String bedNo = "BD-" + roomNo.split("-")[1]; // Matches room number
        xPathUtil.fillTextField("bedNo", bedNo, wait);
        xPathUtil.selectField("roomTypeId", roomType, XPathUtil.DropdownType.DISPLAY_NONE, "", driver, wait);
        xPathUtil.selectField("roomId", roomNo, XPathUtil.DropdownType.DISPLAY_NONE, "", driver, wait);
        xPathUtil.optionSelect("usedStatus", "Active", "", wait, driver);
        xPathUtil.formSubmitWithFormId("bedForm", driver, wait, false); // Fixed form ID
    }

    /**
     * Navigates to a section and clicks the 'Add' button.
     * @param id The ID of the navigation link to click.
     */
    private void navigationTap(String id) {
        xPathUtil.clickButtonElement(By.xpath("//a[@id='" + id + "' and contains(@class, 'nav-link')]"), driver, wait);
        xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Add')]"), driver, wait);
    }
}