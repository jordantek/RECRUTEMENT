package com.tpc.tpcgestpaie.localapp.dto.numerisation;

import lombok.Data;

@Data
public class DocumentCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Integer displayOrder = 0;

    // ⭐ CHAMPS OBLIGATOIRES
    private Long companyId; // Entreprise à partir de laquelle on crée la catégorie

    // ⭐ NOUVEAUX CHAMPS POUR LA GESTION AVANCÉE
    private boolean shared = false; // true = catégorie partagée, false = spécifique
    private Long targetCompanyId; // Si spécifique, entreprise cible (optionnel)

    // ⭐ CHAMPS INFORMATIFS (en lecture seule - pour la réponse)
    private Long clientId;
    private String clientName;
    private String companyName;
    private boolean canManageCompanies;// Indique si la catégorie est partagée
    private Long effectiveCompanyId; // L'entreprise effective (client ou company)

    // Constructeurs
    public DocumentCategoryDTO() {
    }

    public DocumentCategoryDTO(Long companyId, String name, String description, Integer displayOrder) {
        this.companyId = companyId;
        this.name = name;
        this.description = description;
        this.displayOrder = displayOrder;
    }

    public DocumentCategoryDTO(Long companyId, String name, String description, Integer displayOrder, boolean shared, Long targetCompanyId) {
        this.companyId = companyId;
        this.name = name;
        this.description = description;
        this.displayOrder = displayOrder;
        this.shared = shared;
        this.targetCompanyId = targetCompanyId;
    }

    // Getters et Setters (Lombok @Data les génère automatiquement, mais voici les spécifiques si besoin)

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }


}