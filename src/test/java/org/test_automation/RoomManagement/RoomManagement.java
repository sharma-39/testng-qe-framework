package org.test_automation.RoomManagement;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.test_automation.DBConnectivity.XPathUtil;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.testng.annotations.Test;

import java.util.*;

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
    List<String> floorDescriptions = Arrays.asList(
            "The main entry level of a building, often used for lobbies, reception, and primary access points.",                         // Ground Floor
            "The level immediately above the ground floor; typically used for office spaces, patient rooms, or staff areas.",           // First Floor
            "Located above the first floor, often used for additional offices, patient wards, or specialized areas.",                   // Second Floor
            "Above the second floor, used for administrative offices, treatment areas, or wards.",                                      // Third Floor
            "A level below the ground floor, typically used for storage, parking, or utility services.",                                // Basement
            "An intermediate floor between two main floors, often used for waiting areas or auxiliary services.",                       // Mezzanine Floor
            "A partially below-ground floor, often used for parking, utilities, or services.",                                          // Lower Ground Floor
            "Dedicated floor for building operations and infrastructure such as HVAC, plumbing, etc.",                                  // Service Floor
            "Contains emergency treatment rooms like the ER or trauma bays.",                                                           // Emergency Floor
            "Floor housing operating rooms and surgical suites for procedures.",                                                        // Operation Theater Floor
            "Contains ICUs for critically ill patients needing constant monitoring and care.",                                          // ICU Floor
            "Used for diagnostic services like X-ray, MRI, CT scans, and other imaging.",                                               // Diagnostic Floor
            "Provides maternity care with labor, delivery, and recovery rooms.",                                                        // Maternity Floor
            "Handles outpatient services including consultations, check-ups, and minor treatments.",                                    // Outpatient Floor (OPD)
            "Accommodates patients admitted for longer stays and medical care.",                                                        // Inpatient Floor (IPD)
            "Office space for hospital administration, records, HR, and executive management.",                                         // Administrative Floor
            "Floor where medicines are stored, dispensed, and managed by pharmacists.",                                                 // Pharmacy Floor
            "Equipped for pathology and clinical testing including blood, urine, and tissue samples.",                                  // Lab Floor
            "Specialized for imaging tests such as MRI, CT, X-ray, and ultrasound.",                                                    // Radiology Floor
            "Supports physical therapy, occupational therapy, and post-treatment rehabilitation.",                                      // Rehabilitation Floor
            "Dedicated to specific departments like cardiology, neurology, orthopedics, etc.",                                          // Specialty Floor
            "Houses rooms for medical consultations, diagnostics, and evaluations.",                                                    // Consultation Floor
            "Premium facility with high-end rooms and services for executive or VIP patients.",                                         // Executive Floor
            "Where food and beverages are served to patients, visitors, and hospital staff.",                                           // Cafeteria Floor
            "Sub-ground floor dedicated to parking for visitors, patients, and staff."                                                  // Parking Basement
    );
    Map<String, String> roomTypeDescriptions = new HashMap<String, String>() {{
        put("General Ward", "A shared room for patients requiring basic medical or surgical care.");
        put("Private Room", "A single-occupancy room providing privacy and comfort.");
        put("Semi-Private Room", "A room shared by two patients, offering moderate privacy.");
        put("ICU (Intensive Care Unit)", "Provides intensive treatment and monitoring for critically ill patients.");
        put("NICU (Neonatal Intensive Care Unit)", "Specialized care unit for premature and critically ill newborns.");
        put("PICU (Pediatric Intensive Care Unit)", "Intensive care unit for critically ill children.");
        put("Emergency Room", "Handles urgent and emergency medical situations.");
        put("Operation Theater", "A sterile room where surgical operations are performed.");
        put("Post-Operative Room", "Room for monitoring patients immediately after surgery.");
        put("Recovery Room (PACU)", "Post-Anesthesia Care Unit for patients recovering from anesthesia.");
        put("Isolation Room", "For patients with contagious diseases requiring isolation.");
        put("Quarantine Room", "For separating and monitoring individuals exposed to infectious diseases.");
        put("Labor Room", "Room equipped for labor and delivery preparation.");
        put("Delivery Room", "Where childbirth occurs under medical supervision.");
        put("Maternity Ward", "Dedicated to care of mothers before and after childbirth.");
        put("Consultation Room", "Used for medical consultations between doctors and patients.");
        put("Examination Room", "For physical examinations and minor procedures.");
        put("Procedure Room", "Equipped for non-surgical medical procedures.");
        put("Radiology Room", "Contains imaging equipment like X-ray, CT, or MRI machines.");
        put("Dialysis Room", "Equipped for renal dialysis treatment for kidney patients.");
        put("Chemotherapy Room", "Used for administering chemotherapy to cancer patients.");
        put("Burn Unit", "Specialized care area for burn injury treatment and recovery.");
        put("Physiotherapy Room", "Used for physical therapy sessions and rehabilitation.");
        put("Pediatric Ward", "Dedicated to the treatment and care of children.");
    }};


    Map<String, String> wardDescriptions = new HashMap<>();


    String floorName;
    String wardName;
    String roomType;


    String roomNo;

    @Test(priority = 3)
    public void menuOpen() {
        menuPanelClick("Master", true, "Room", "");
        wardDescriptions.put("General Ward", "A shared room for general medical or surgical care with multiple beds.");
        wardDescriptions.put("Male Ward", "A general ward specifically designated for male patients.");
        wardDescriptions.put("Female Ward", "A general ward specifically designated for female patients.");
        wardDescriptions.put("Pediatric Ward", "Provides medical care for infants, children, and adolescents.");
        wardDescriptions.put("Surgical Ward", "Dedicated to patients recovering from surgical procedures.");
        wardDescriptions.put("Medical Ward", "For patients requiring general medical treatment and monitoring.");
        wardDescriptions.put("ICU Ward", "Intensive Care Unit for critically ill patients needing constant monitoring.");
        wardDescriptions.put("NICU Ward", "Neonatal ICU for premature and seriously ill newborns.");
        wardDescriptions.put("PICU Ward", "Pediatric ICU for critically ill children.");
        wardDescriptions.put("Maternity Ward", "Caters to women during childbirth and postnatal recovery.");
        wardDescriptions.put("Postnatal Ward", "For mothers and newborns after delivery, providing postpartum care.");
        wardDescriptions.put("Antenatal Ward", "For pregnant women requiring hospital care before childbirth.");
        wardDescriptions.put("Orthopedic Ward", "Treats patients with bone, joint, and musculoskeletal conditions.");
        wardDescriptions.put("Cardiology Ward", "Specializes in care for heart-related medical conditions.");
        wardDescriptions.put("Neurology Ward", "Dedicated to patients with neurological disorders and injuries.");
        wardDescriptions.put("Oncology Ward", "Provides care for cancer patients, including chemotherapy.");
        wardDescriptions.put("Burn Ward", "Specialized in the treatment and recovery of burn injuries.");
        wardDescriptions.put("Isolation Ward", "For patients with contagious diseases requiring strict isolation.");
        wardDescriptions.put("Infectious Disease Ward", "Manages patients with infectious or communicable diseases.");
        wardDescriptions.put("Psychiatric Ward", "Provides mental health care and psychiatric treatment.");
        wardDescriptions.put("Rehabilitation Ward", "Helps patients recover physical functions through therapy.");
        wardDescriptions.put("ENT Ward", "Dedicated to treatment of ear, nose, and throat conditions.");
        wardDescriptions.put("Urology Ward", "Treats conditions related to the urinary tract and male reproductive system.");
        wardDescriptions.put("Gastroenterology Ward", "Specializes in digestive system disorders.");
        wardDescriptions.put("High Dependency Unit (HDU)", "For patients who need close observation but not full ICU care.");
        wardDescriptions.put("Emergency Ward", "Handles patients with acute illnesses or trauma needing urgent attention.");
        wardDescriptions.put("Dialysis Ward", "Provides dialysis treatment for patients with kidney failure.");

    }

    @Test(priority = 4)
    public void floorCrud() {

        navigationTap("Floor");

        int index=  (int) (Math.random() * roomTypes.size());
        floorName = floors.get(index);
        xPathUtil.fillTextField("floorName", floorName, wait);
        String description = floorDescriptions.get(index);
        xPathUtil.fillTextArea("floorDesc", description, wait, driver);

        xPathUtil.formSubmitWithFormId("floorForm", driver, wait, false);


        navigationTap("Ward");


        wardName = wardNames.get((int) (Math.random() * roomTypes.size()));

        xPathUtil.fillTextField("wardName", wardName, wait);

        xPathUtil.selectField("floorId", floorName, XPathUtil.DropdownType.STANDARD, "", driver, wait);


        xPathUtil.fillTextArea("wardDesc", wardDescriptions.get(wardName), wait, driver);

        xPathUtil.formSubmitWithFormId("wardForm", driver, wait, false);


        navigationTap("Room Type");

        roomType = roomTypes.get((int) (Math.random() * roomTypes.size()));
        xPathUtil.fillTextField("roomTypeName", roomType, wait);

        String uomId = "TEST-UOM";
        String unitPrice = "1000";
        xPathUtil.selectField("uomId", uomId, XPathUtil.DropdownType.STANDARD, "", driver, wait);
        xPathUtil.fillTextField("unitPrice", unitPrice, wait);

        Boolean autoPost = false;
        xPathUtil.clickCheckBox(autoPost, wait, driver, "Auto Post");

        xPathUtil.formSubmitWithFormId("roomTypeForm", driver, wait, false);

        navigationTap("Room");

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

        xPathUtil.fillTextArea("roomDesc", roomTypeDescriptions.get(roomType), wait, driver);

        xPathUtil.formSubmitWithFormId("roomForm", driver, wait, false);


        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("location.reload()");


        navigationTap("Bed");


        String bedNo = "BD-" + randomThreeDigit;

        xPathUtil.fillTextField("bedNo", bedNo, wait);

        xPathUtil.selectField("roomTypeId", roomType, XPathUtil.DropdownType.DISPLAY_NONE, "", driver, wait);

        threadTimer(2000);
        xPathUtil.selectField("roomId", roomNo, XPathUtil.DropdownType.DISPLAY_NONE, "", driver, wait);


        xPathUtil.optionSelect("usedStatus", "Active", "", wait, driver);

        xPathUtil.formSubmitWithFormId("roomForm", driver, wait, false);


    }

    private void navigationTap(String id) {
        xPathUtil.clickButtonElement(By.xpath("//a[@id='" + id + "' and contains(@class, 'nav-link')]"), driver, wait);
        xPathUtil.clickButtonElement(By.xpath("//button[contains(text(),'Add')]"), driver, wait);
    }
}
