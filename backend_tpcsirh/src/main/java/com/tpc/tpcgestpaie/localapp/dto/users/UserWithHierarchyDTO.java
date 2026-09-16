package com.tpc.tpcgestpaie.localapp.dto.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithHierarchyDTO {

    @NotNull(message = "L'employé est obligatoire")
    private Long employeId;

    @NotNull(message = "L'entreprise est obligatoire")
    private Long companyId;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    private String username;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    private List<Long> roleIds;

    private String status = "active";

    // 🔹 Hiérarchie des supérieurs
    private List<SuperieurDTO> superieurs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuperieurDTO {
        @NotNull(message = "Le contrat supérieur est obligatoire")
        private Long contratSuperieurId;
        @NotNull(message = "L'ordre est obligatoire")
        private Integer ordre; // ordre unique
    }
}
