package org.example.OpBill;

import org.example.FlowHelper.PatientFlowHelper;

public class OpBillConfigBuilder {
    private PatientFlowHelper patientFlowHelper;

    public OpBillConfigBuilder setPatientFlowHelper(PatientFlowHelper patientFlowHelper) {
        this.patientFlowHelper = patientFlowHelper;
        return this;
    }

}