package com.tpc.tpcgestpaie.localapp.dto.users;

import com.tpc.tpcgestpaie.localapp.model.EmployeSuperieur;
import com.tpc.tpcgestpaie.localapp.model.Role;
import com.tpc.tpcgestpaie.localapp.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseWithHierarchyDTO {

    private Long userId;
    private Long employeId;
    private String fullName;
    private String username;
    private String email;
    private String status;
    private Long companyId;
    private String companyName;
    private List<String> roles;

    // 🔹 Hiérarchie des supérieurs
    private List<SuperieurDTO> superieurs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuperieurDTO {
        private Long contratSuperieurId;
        private Long employeSuperieurId;
        private String nomComplet;
        private String fonction;
        private String departement;
        private String companyName;
        private Integer ordre; // ordre unique
    }

    public UserResponseWithHierarchyDTO mapToResponse(User user, EmployeSuperieur employeSuperieur) {

        List<UserResponseWithHierarchyDTO.SuperieurDTO> superieursList = employeSuperieur != null && employeSuperieur.getHierarchie() != null
                ? employeSuperieur.getHierarchie().getSuperieurs().stream()
                .map(sup -> new UserResponseWithHierarchyDTO.SuperieurDTO(
                        sup.getContratSuperieurId(),
                        sup.getEmployeSuperieurId(),
                        sup.getNomComplet(),
                        sup.getFonction(),
                        sup.getDepartement(),
                        sup.getCompanyName(),
                        sup.getOrdre()
                ))
                .toList()
                : List.of();

        return new UserResponseWithHierarchyDTO(
                user.getId(),
                user.getEmploye() != null ? user.getEmploye().getId() : null,
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getStatus() != null ? user.getStatus().getName() : null,
                user.getCompany() != null ? user.getCompany().getId() : null,
                user.getCompany() != null ? user.getCompany().getName() : null,
                user.getRoles().stream().map(Role::getName).toList(),
                superieursList
        );
    }

}
