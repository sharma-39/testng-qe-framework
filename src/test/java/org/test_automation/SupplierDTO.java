package org.test_automation;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object (DTO) for Supplier information.
 */
public class SupplierDTO {

    // Basic Supplier Info
    private String supplierNamePrefix;
    private String emailDomain;
    private String supplierCodePrefix;
    private String drugLicensePrefix;
    private String fax;
    private String gstNumber;
    private String website;

    // Contact Information
    private Phone phone;
    private Address address;
    private PointOfContact pointOfContact;

    // Nested Classes ================================================

    /**
     * Phone number details.
     */

    public static class Phone {
        private String countryCode;
        private String phoneNumber;  // Add this field
        private String pointPhNumber; // Add this for point of contact

        // Getters and setters for all fields
        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getPointPhNumber() {
            return pointPhNumber;
        }

        public void setPointPhNumber(String pointPhNumber) {
            this.pointPhNumber = pointPhNumber;
        }
    }

    /**
     * Physical address details.
     */
    public static class Address {
        private String street;
        private String location;
        private String state;
        private String city;
        private String zipCode;

        // Getters & Setters
        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }
    }

    /**
     * Point of contact details.
     */
    public static class PointOfContact {
        private String name;
        private Phone phone;

        // Getters & Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Phone getPhone() {
            return phone;
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }
    }

    // Main Class Getters & Setters ==================================

    public String getSupplierNamePrefix() {
        return supplierNamePrefix;
    }

    public void setSupplierNamePrefix(String supplierNamePrefix) {
        this.supplierNamePrefix = supplierNamePrefix;
    }

    public String getEmailDomain() {
        return emailDomain;
    }

    public void setEmailDomain(String emailDomain) {
        this.emailDomain = emailDomain;
    }

    public String getSupplierCodePrefix() {
        return supplierCodePrefix;
    }

    public void setSupplierCodePrefix(String supplierCodePrefix) {
        this.supplierCodePrefix = supplierCodePrefix;
    }

    public String getDrugLicensePrefix() {
        return drugLicensePrefix;
    }

    public void setDrugLicensePrefix(String drugLicensePrefix) {
        this.drugLicensePrefix = drugLicensePrefix;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public PointOfContact getPointOfContact() {
        return pointOfContact;
    }

    public void setPointOfContact(PointOfContact pointOfContact) {
        this.pointOfContact = pointOfContact;
    }

    // Utility Method (Optional)
    @Override
    public String toString() {
        return "SupplierDTO{" +
                "supplierNamePrefix='" + supplierNamePrefix + '\'' +
                ", emailDomain='" + emailDomain + '\'' +
                ", supplierCodePrefix='" + supplierCodePrefix + '\'' +
                '}';
    }
}