package org.test_automation.VO;

public class LabResult {
    private String low;
    private String high;
    private String criticallyLow;
    private String criticallyHigh;
    private String defaultValue;
    private String description;
    private Boolean abnormal;
    private String range;
    private String resultValue;
    private String options;

    // Getters and setters for all fields

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getCriticallyLow() {
        return criticallyLow;
    }

    public void setCriticallyLow(String criticallyLow) {
        this.criticallyLow = criticallyLow;
    }

    public String getCriticallyHigh() {
        return criticallyHigh;
    }

    public void setCriticallyHigh(String criticallyHigh) {
        this.criticallyHigh = criticallyHigh;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAbnormal() {
        return abnormal;
    }

    public void setAbnormal(Boolean abnormal) {
        this.abnormal = abnormal;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getResultValue() {
        return resultValue;
    }

    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}
