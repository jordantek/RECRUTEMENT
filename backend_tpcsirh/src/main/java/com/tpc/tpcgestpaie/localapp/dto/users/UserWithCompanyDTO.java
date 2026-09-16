package com.tpc.tpcgestpaie.localapp.dto.users;

import com.tpc.tpcgestpaie.localapp.dto.UserDTO;
import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.model.User;

// Ajoutez cette classe si besoin
public class UserWithCompanyDTO extends UserDTO {
    private CompanyDTO company;

    public UserWithCompanyDTO(User user) {
        super(user);
        if (user.getCompany() != null) {
            this.company = CompanyDTO.fromEntity(user.getCompany());
        }
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }
}