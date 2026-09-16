package com.tpc.tpcgestpaie.localapp.dto;

public class AssignRoleRequestDTO {
    private Long employeId;
    private Long roleId;

    // Getters & setters
    public Long getEmployeId() {
        return employeId;
    }
    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }
    public Long getRoleId() {
        return roleId;
    }
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
