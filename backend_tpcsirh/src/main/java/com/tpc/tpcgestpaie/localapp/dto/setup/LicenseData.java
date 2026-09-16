package com.tpc.tpcgestpaie.localapp.dto.setup;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class LicenseData {
    private CompanyInfo companyInfo;
    private ContactPersons contactPersons;
    private LicenseConfig licenseConfig;
    private List<String> modules;

    public CompanyInfo getCompanyInfo() {
        return companyInfo;
    }

    public void setCompanyInfo(CompanyInfo companyInfo) {
        this.companyInfo = companyInfo;
    }

    public ContactPersons getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(ContactPersons contactPersons) {
        this.contactPersons = contactPersons;
    }

    public LicenseConfig getLicenseConfig() {
        return licenseConfig;
    }

    public void setLicenseConfig(LicenseConfig licenseConfig) {
        this.licenseConfig = licenseConfig;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }


}



