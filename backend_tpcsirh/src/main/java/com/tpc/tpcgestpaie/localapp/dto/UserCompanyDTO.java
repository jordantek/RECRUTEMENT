package com.tpc.tpcgestpaie.localapp.dto;

import java.util.List;

public class UserCompanyDTO {
    private String username;
    private String role;
    private List<String> companies;

    public UserCompanyDTO(String username, String role, List<String> companies) {
        this.username = username;
        this.role = role;
        this.companies = companies;
    }

    // Getters & Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public List<String> getCompanies() { return companies; }
    public void setCompanies(List<String> companies) { this.companies = companies; }
}
