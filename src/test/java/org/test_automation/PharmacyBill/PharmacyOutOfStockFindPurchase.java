package org.test_automation.PharmacyBill;

import org.test_automation.DBConnectivity.MenuUtils;
import org.test_automation.FlowHelper.PatientFlowHelper;
import org.test_automation.FlowHelper.PharmacyBillHelper;
import org.test_automation.FlowHelper.PurchaseFlowHelper;
import org.test_automation.LoginUtil.LoginAndLocationTest;
import org.json.JSONObject;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PharmacyOutOfStockFindPurchase extends LoginAndLocationTest {

    private static final long THREAD_SECONDS = 3000; // Constant for thread sleep time
    private static int patientIncrement = 0; // Counter for patient increment
    private final PatientFlowHelper patientFlowHelper; // Helper class for patient flow
    private final PharmacyBillHelper pharmacyBillHelper;
    private final PurchaseFlowHelper purchaseFlowHelper;
    private String patientCode; // Stores the patient code
    private boolean isAppointmentCreated = false; // Flag to check if appointment is created
    private boolean isAppointmentCheckedIn = false; // Flag to check if appointment is checked in
    private boolean isPrescriptionAdd = false;
    private final MenuUtils menuUtils=new MenuUtils();

    public PharmacyOutOfStockFindPurchase() {
        this.patientFlowHelper = new PatientFlowHelper();
        this.pharmacyBillHelper = new PharmacyBillHelper();
        this.purchaseFlowHelper = new PurchaseFlowHelper();
    }


    @Test(priority = 3, dependsOnMethods = {"testLogin"})
    public void pharmacyBillFlowData() throws IOException, InterruptedException {

        if (isLoginSuccessful) {
            for (int i = 7; i <= 7; i++) {
                JSONObject patient = tempPatientData.getJSONObject(patientIncrement);
                patientIncrement = i;
                // Register the patient and get the patient code
                patientCode = patientFlowHelper.patientRegisterTest(this, patient, driver, wait, "Patient Registration");
                System.out.println("OP Bill flow started for Patient Code: " + patientCode);

                // Navigate to the dashboard
                menuUtils.menuPanelClick("Dashboard", false, "", "",driver,wait);

                setPatientSearchCode(patientCode);
                if (patientCode != null) {
                    // Create an appointment for the patient
                    isAppointmentCreated = patientFlowHelper.createAppointment(this, patient, driver, wait, "Create Appointment", patientCode);
                    if (isAppointmentCreated) {
                        threadTimer(3000); // Wait for the appointment to be created

                        // Check in the appointment
                        isAppointmentCheckedIn = patientFlowHelper.checkingAppointmentTest(this, driver, wait, "View Appointments", patientCode);
                        if (isAppointmentCheckedIn) {
                            isPrescriptionAdd = pharmacyBillHelper.addPrescriptionCurrentAdmission(this, patientCode, driver, wait, "Current Admissions", patient);

                            if (isPrescriptionAdd) {
                                threadTimer(3000);
                                String message = pharmacyBillHelper.addPharmacyBill(this, patientCode, driver, wait, "Pharmacy");
                                System.out.println("add Pharmacy bill message" + message);
                                if (!message.equals("Success")) {
                                    message = message.replace("[", "").replace("]", "");
                                    List<String> items = Arrays.stream(message.split(",")).collect(Collectors.toUnmodifiableList());
                                    System.out.println("Text:--" + items);

                                    threadTimer(3000);
                                    driver.navigate().refresh();

                                    purchaseFlowHelper.addStockPurchase(this, items, driver, wait, "Pharmacy", tempStockData, "custom");


                                    System.out.println("Successfully added Completed Purchase flow");
                                    pharmacyBillHelper.addPharmacyBill(this, patientCode, driver, wait, "Pharmacy");

                                    System.out.println("set this patient code"+patientCode);



                                }
                            } else {
                                System.out.println("Failed");
                            }

                        } else {
                            System.out.println("Appoinment Created failed. Retrying..");
                        }
                    }
                }
            }
        }
    }


    public void threadTimer(long seconds) {
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
