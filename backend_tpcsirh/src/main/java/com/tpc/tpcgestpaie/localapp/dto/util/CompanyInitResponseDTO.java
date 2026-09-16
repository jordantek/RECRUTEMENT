package com.tpc.tpcgestpaie.localapp.dto.util;

public class CompanyInitResponseDTO {
    private boolean success;
    private String message;
    private Long companyId;
    private Long emailConfigId;
    private Long initialConfigId;

    public CompanyInitResponseDTO(boolean success, String message,
                                  Long companyId, Long emailConfigId, Long initialConfigId) {
        this.success = success;
        this.message = message;
        this.companyId = companyId;
        this.emailConfigId = emailConfigId;
        this.initialConfigId = initialConfigId;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Long getCompanyId() { return companyId; }
    public Long getEmailConfigId() { return emailConfigId; }
    public Long getInitialConfigId() { return initialConfigId; }
}
