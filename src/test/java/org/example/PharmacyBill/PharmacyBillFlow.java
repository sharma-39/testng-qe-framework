package org.example.PharmacyBill;

import org.example.FlowHelper.PatientFlowHelper;
import org.example.FlowHelper.PharmacyBillHelper;
import org.example.LoginUtil.LoginAndLocationTest;
import org.json.JSONObject;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.*;

import java.io.IOException;

// patient register to pharmacy bill scanerio
public class PharmacyBillFlow extends LoginAndLocationTest {

    private static final long THREAD_SECONDS = 3000; // Constant for thread sleep time
    private static int patientIncrement = 0; // Counter for patient increment
    private final PatientFlowHelper patientFlowHelper; // Helper class for patient flow
    private final PharmacyBillHelper pharmacyBillHelper;
    private String patientCode; // Stores the patient code
    private boolean isAppointmentCreated = false; // Flag to check if appointment is created
    private boolean isAppointmentCheckedIn = false; // Flag to check if appointment is checked in

    private Boolean isPrescriptionAdd = false;

    public PharmacyBillFlow() {
        this.patientFlowHelper = new PatientFlowHelper();
        this.pharmacyBillHelper = new PharmacyBillHelper();
    }

    @Test(priority = 3, dependsOnMethods = {"testLogin"})
    public void pharmacyBillFlowData() throws IOException, InterruptedException {

        if (isLoginSuccessful) {
            for (int i = 5; i <= 5; i++) {
                JSONObject patient = tempPatientData.getJSONObject(patientIncrement);
                patientIncrement = i;
                // Register the patient and get the patient code
                patientCode = patientFlowHelper.patientRegisterTest(this, patient, driver, wait, "Patient Registration");
                System.out.println("OP Bill flow started for Patient Code: " + patientCode);

                // Navigate to the dashboard
                menuPanelClick("Dashboard", false, "", "");

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
                                JavascriptExecutor js = (JavascriptExecutor) driver;
                                js.executeScript("location.reload()");

                                String message = pharmacyBillHelper.addPharmacyBill(this, patientCode, driver, wait, "Pharmacy");

                                System.out.println("Text:--" + message);
                            }
                            else {
                                System.out.println("Added Prescriptions Failed");
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
