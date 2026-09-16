package com.tpc.tpcgestpaie.localapp.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFullRequestDTO {

    private Long userId; // null si création

    @NotNull(message = "L'employé est obligatoire")
    private Long employeId;

    @NotNull(message = "L'entreprise est obligatoire")
    private Long companyId;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    private String username;

    private String password; // optionnel pour mise à jour

    @Email(message = "Email invalide")
    @NotBlank(message = "Email obligatoire")
    private String email;

    private List<Long> roleIds;

    private String status = "active";

    private List<SuperieurDTO> superieurs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuperieurDTO {
        private Long contratSuperieurId;
        private Integer ordre;
    }
}
