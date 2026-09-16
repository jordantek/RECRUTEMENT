package com.tpc.tpcgestpaie.localapp.dto.paie;


import lombok.Data;

@Data
public class PayrollParametersCheckResponse {
    private boolean allParametersSet;
    private String message;
    private Long companyId;
    private String companyName;

    public PayrollParametersCheckResponse(boolean allParametersSet, String message, Long companyId, String companyName) {
        this.allParametersSet = allParametersSet;
        this.message = message;
        this.companyId = companyId;
        this.companyName = companyName;
    }

    public boolean isAllParametersSet() {
        return allParametersSet;
    }

    public void setAllParametersSet(boolean allParametersSet) {
        this.allParametersSet = allParametersSet;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}