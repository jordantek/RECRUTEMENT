package com.tpc.tpcgestpaie.localapp.dto.numerisation;

import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentCategory;
import org.springframework.stereotype.Component;

@Component
public class DocumentCategoryMapper {

    public DocumentCategoryDTO toDTO(DocumentCategory category) {
        DocumentCategoryDTO dto = new DocumentCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setDisplayOrder(category.getDisplayOrder());

        // ⭐ INFORMATIONS DE LIEN
        dto.setClientId(category.getClient().getId());
        dto.setClientName(category.getClient().getName());
        dto.setShared(category.isShared());

        if (category.isCompanySpecific()) {
            dto.setCompanyId(category.getCompany().getId());
            dto.setCompanyName(category.getCompany().getName());
            dto.setEffectiveCompanyId(category.getCompany().getId());
        } else {
            dto.setEffectiveCompanyId(category.getClient().getId());
        }

        dto.setCanManageCompanies(category.getClient().canManageCompanies());

        return dto;
    }

    public DocumentCategory toEntity(DocumentCategoryDTO dto, Company currentCompany, Company clientCompany) {
        DocumentCategory category = new DocumentCategory();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setDisplayOrder(dto.getDisplayOrder());
        category.setClient(clientCompany);

        // ⭐ DÉTERMINER LE LIEN COMPANY
        if (dto.isShared() && clientCompany.canManageCompanies()) {
            category.setCompany(null); // Catégorie partagée
        } else {
            // Catégorie spécifique
            Company targetCompany = dto.getTargetCompanyId() != null ?
                    // TODO: Récupérer l'entreprise cible depuis le repository
                    currentCompany : // Pour l'exemple, utiliser currentCompany
                    currentCompany;
            category.setCompany(targetCompany);
        }

        return category;
    }
}