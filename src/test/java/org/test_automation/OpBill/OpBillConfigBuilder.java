package org.test_automation.OpBill;

import org.test_automation.FlowHelper.PatientFlowHelper;

public class OpBillConfigBuilder {
    private PatientFlowHelper patientFlowHelper;

    public OpBillConfigBuilder setPatientFlowHelper(PatientFlowHelper patientFlowHelper) {
        this.patientFlowHelper = patientFlowHelper;
        return this;
    }

}