package com.tpc.tpcgestpaie.localapp.dto;

import com.tpc.tpcgestpaie.localapp.model.Role;
import com.tpc.tpcgestpaie.localapp.model.User;

public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String username;
    private String phone;
    private String role;       // rôle principal (pour compatibilité)
    private String status;
    private String[] roles;    // tous les rôles sous forme de tableau

    // ⭐ NOUVEAU : Informations de l'entreprise principale
    private Long companyId;
    private String companyName;

    // Champs employés simples
    private String employeNom;
    private String employePrenom;
    private String employeMatricule;
    private Long employeId;

    // Champs techniques
    private String lastLoginIp;
    private String lastLoginAt;
    private String createdAt;
    private String updatedAt;

    public UserDTO() {
    }

    // Constructeur complet
    public UserDTO(Long id, String fullName, String email, String username, String phone,
                   String role, String status, String[] roles, Long companyId, String companyName,
                   String employeNom, String employePrenom, String employeMatricule, Long employeId,
                   String lastLoginIp, String lastLoginAt, String createdAt, String updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.roles = roles;
        this.companyId = companyId;
        this.companyName = companyName;
        this.employeNom = employeNom;
        this.employePrenom = employePrenom;
        this.employeMatricule = employeMatricule;
        this.employeId = employeId;
        this.lastLoginIp = lastLoginIp;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructeur à partir de l'entité User
    public UserDTO(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.phone = user.getPhone();
        this.status = user.getStatus() != null ? user.getStatus().getName() : null;

        // Rôles
        this.roles = user.getRoles().stream()
                .map(Role::getName)
                .toArray(String[]::new);

        // Prendre le premier rôle comme rôle principal (pour compatibilité)
        this.role = this.roles.length > 0 ? this.roles[0] : null;

        // ⭐ NOUVEAU : Informations de l'entreprise
        if (user.getCompany() != null) {
            this.companyId = user.getCompany().getId();
            this.companyName = user.getCompany().getName();
        }

        // Informations de l'employé
        if (user.getEmploye() != null) {
            this.employeId = user.getEmploye().getId();
            this.employeNom = user.getEmploye().getNom();
            this.employePrenom = user.getEmploye().getPrenom();
            this.employeMatricule = user.getEmploye().getMatricule();
        }

        // Champs techniques formatés
        this.lastLoginIp = user.getLastLoginIp();
        this.lastLoginAt = user.getLastLoginAt() != null ? user.getLastLoginAt().toString() : null;
        this.createdAt = user.getCreatedAt() != null ? user.getCreatedAt().toString() : null;
        this.updatedAt = user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : null;
    }

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    // ⭐ NOUVEAUX GETTERS/SETTERS pour l'entreprise
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

    public String getEmployeNom() {
        return employeNom;
    }

    public void setEmployeNom(String employeNom) {
        this.employeNom = employeNom;
    }

    public String getEmployePrenom() {
        return employePrenom;
    }

    public void setEmployePrenom(String employePrenom) {
        this.employePrenom = employePrenom;
    }

    public String getEmployeMatricule() {
        return employeMatricule;
    }

    public void setEmployeMatricule(String employeMatricule) {
        this.employeMatricule = employeMatricule;
    }

    public Long getEmployeId() {
        return employeId;
    }

    public void setEmployeId(Long employeId) {
        this.employeId = employeId;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(String lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ⭐ MÉTHODES UTILITAIRES

    /**
     * Vérifie si l'utilisateur a un rôle spécifique
     */
    public boolean hasRole(String roleName) {
        if (roles == null) return false;
        for (String role : roles) {
            if (roleName.equals(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si l'utilisateur a une entreprise principale
     */
    public boolean hasCompany() {
        return companyId != null;
    }

    /**
     * Vérifie si l'utilisateur a un employé associé
     */
    public boolean hasEmploye() {
        return employeId != null;
    }

    /**
     * Retourne le nom complet de l'employé (nom + prénom)
     */
    public String getEmployeFullName() {
        if (employeNom != null && employePrenom != null) {
            return employeNom + " " + employePrenom;
        }
        return null;
    }
}