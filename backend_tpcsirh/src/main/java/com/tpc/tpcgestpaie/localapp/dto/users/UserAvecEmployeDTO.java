package com.tpc.tpcgestpaie.localapp.dto.users;

import com.tpc.tpcgestpaie.localapp.model.HierarchieJson;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserAvecEmployeDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private List<String> roles;
    private String status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;

    // Informations de l'employé associé
    private EmployeInfoDTO employe;

    // Informations de l'entreprise
    private CompanyInfoDTO company;

    private List<HierarchieJson.SuperieurHierarchique> hierarchie;

    @Data
    @Builder
    public static class EmployeInfoDTO {
        private Long id;
        private String matricule;
        private String nom;
        private String prenom;
        private String telephone;
        private String email;
        private String posteActuel;
        private String departementActuel;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getMatricule() {
            return matricule;
        }

        public void setMatricule(String matricule) {
            this.matricule = matricule;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPosteActuel() {
            return posteActuel;
        }

        public void setPosteActuel(String posteActuel) {
            this.posteActuel = posteActuel;
        }

        public String getDepartementActuel() {
            return departementActuel;
        }

        public void setDepartementActuel(String departementActuel) {
            this.departementActuel = departementActuel;
        }
    }

    @Data
    @Builder
    public static class CompanyInfoDTO {
        private Long id;
        private String name;
        private String code;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public EmployeInfoDTO getEmploye() {
        return employe;
    }

    public void setEmploye(EmployeInfoDTO employe) {
        this.employe = employe;
    }

    public CompanyInfoDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyInfoDTO company) {
        this.company = company;
    }
}