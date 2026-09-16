package com.tpc.tpcgestpaie.localapp.dto;
import java.util.List;

public class AssignCompaniesRequestDTO {
    private Long userId;
    private List<Long> companyIds;

    // Getters & Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public List<Long> getCompanyIds() { return companyIds; }
    public void setCompanyIds(List<Long> companyIds) { this.companyIds = companyIds; }
}
