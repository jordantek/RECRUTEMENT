package com.tpc.tpcgestpaie.localapp.dto.users;

import lombok.Data;
import java.util.List;

@Data
public class EmployesSansCompteParEntrepriseDTO {
    private Long companyId;
    private String companyName;
    private List<EmployeSansCompteDTO> employes;
    private int totalEmployes;

    public EmployesSansCompteParEntrepriseDTO(Long companyId, String companyName, List<EmployeSansCompteDTO> employes) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.employes = employes;
        this.totalEmployes = employes.size();
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

    public List<EmployeSansCompteDTO> getEmployes() {
        return employes;
    }

    public void setEmployes(List<EmployeSansCompteDTO> employes) {
        this.employes = employes;
    }

    public int getTotalEmployes() {
        return totalEmployes;
    }

    public void setTotalEmployes(int totalEmployes) {
        this.totalEmployes = totalEmployes;
    }
}