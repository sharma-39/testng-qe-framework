package org.example.TapPharmacy;

import org.example.FlowHelper.PharmacyBillHelper;
import org.example.LoginUtil.LoginAndLocationTest;
import org.testng.annotations.Test;

public class SalesReturn extends LoginAndLocationTest {


    private PharmacyBillHelper pharmacyBillHelper;

    public SalesReturn() {
        this.pharmacyBillHelper = new PharmacyBillHelper();
    }


    @Test(priority = 3)
    public void billGenerate()
    {
        if(isLoginSuccessful) {
            String patientCode="PD-2";
            String panel="Pharmacy";
            menuPanelClick("Pharmacy",false,"", "");
            threadTimer(2000);
            verifyPanelName("Pharmacy Bills");
            pharmacyBillHelper.addCustomBill(patientCode,this,driver);
            pharmacyBillHelper.addPrescriptionPharmacyBill(this,patientCode,driver,wait,"Pharmacy","Pharmacy Bill Details");



        }

    }


}
