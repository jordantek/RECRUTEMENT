package com.tpc.tpcgestpaie.localapp.dto.util;

public class CompanyInitRequestDTO {
    private CompanyConfigDTO company;          // tes champs company
    private EmailConfigCreateDTO emailConfig;  // host, port, protocole, mailFrom, password, addedById

    public CompanyConfigDTO getCompany() { return company; }
    public void setCompany(CompanyConfigDTO company) { this.company = company; }

    public EmailConfigCreateDTO getEmailConfig() { return emailConfig; }
    public void setEmailConfig(EmailConfigCreateDTO emailConfig) { this.emailConfig = emailConfig; }
}
