package com.tpc.tpcgestpaie.localapp.dto.jourFerie;

import com.tpc.tpcgestpaie.localapp.model.jourFerie.JourFerie;
import com.tpc.tpcgestpaie.localapp.model.jourFerie.JourFerieEntreprise;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Mapper pour convertir entre entités et DTOs
 */
@Component
public class JourFerieMapper {

    /**
     * Convertit un CreateDTO en entité
     */
    public JourFerie toEntity(JourFerieCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        JourFerie entity = JourFerie.builder()
                .slug(dto.getSlug())
                .libelle(dto.getLibelle())
                .dateFerie(dto.getDateFerie())
                .pays(dto.getPays())
                .estFixe(dto.getEstFixe() != null ? dto.getEstFixe() : false)
                .estRecurrent(dto.getEstRecurrent() != null ? dto.getEstRecurrent() : false)
                .build();


        // Générer un code automatiquement si non fourni
        if (entity.getSlug() == null || entity.getSlug().trim().isEmpty()) {
            entity.setSlug(genererCode(dto.getLibelle(), dto.getDateFerie()));
        }

        return entity;
    }

    /**
     * Convertit une entité en ResponseDTO
     */
    public JourFerieResponseDTO toResponseDTO(JourFerie entity) {
        if (entity == null) {
            return null;
        }

        return JourFerieResponseDTO.builder()
                .id(entity.getId())
                .slug(entity.getSlug())
                .libelle(entity.getLibelle())
                .dateFerie(entity.getDateFerie())
                .jourSemaine(entity.getJourSemaine())
                .tombeEnWeekend(entity.tombeEnWeekend())
                .pays(entity.getPays())
                .estFixe(entity.getEstFixe())
                .estRecurrent(entity.getEstRecurrent())
                .build();
    }

    /**
     * Met à jour une entité à partir d'un UpdateDTO
     */
    public void updateEntityFromDto(JourFerie entity, JourFerieUpdateDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getLibelle() != null) {
            entity.setLibelle(dto.getLibelle());
        }


        if (dto.getDateFerie() != null) {
            entity.setDateFerie(dto.getDateFerie());
        }

        if (dto.getEstRecurrent() != null) {
            entity.setEstRecurrent(dto.getEstRecurrent());
        }
    }


    /**
     * Génère un code automatiquement à partir du libellé
     */
    private String genererCode(String libelle, LocalDate date) {
        if (libelle == null || libelle.trim().isEmpty()) {
            return "JOUR_FERIE_" + date;
        }

        // Nettoyer et formater le libellé
        String code = libelle.trim()
                .toUpperCase()
                .replaceAll("[àâäáã]", "A")
                .replaceAll("[éèêë]", "E")
                .replaceAll("[îïí]", "I")
                .replaceAll("[ôöó]", "O")
                .replaceAll("[ûüú]", "U")
                .replaceAll("[ç]", "C")
                .replaceAll("[^A-Z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");

        // Limiter la longueur
        if (code.length() > 30) {
            code = code.substring(0, 30);
        }

        return code + "_" + date;
    }
}