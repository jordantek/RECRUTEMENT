package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.SubordonneResponseDTO;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeSuperieurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeSuperieurService {

    private final EmployeSuperieurRepository repository;
    private final EmployeSuperieurRepository employeSuperieurRepository;
    private final EmployeRepository employeRepository;

    /**
     * Créer ou mettre à jour la hiérarchie d'un employé
     */
    @Transactional
    public EmployeSuperieur saveOrUpdateHierarchie(
            Company company,
            Employe employe,
            ContratEmploye contratEmploye,
            HierarchieJson hierarchie,
            User currentUser
    ) {
        // Vérifier s'il existe déjà une hiérarchie active
        Optional<EmployeSuperieur> existingOpt = repository
                .findByContratEmployeIdAndIsActiveTrue(contratEmploye.getId());

        EmployeSuperieur employeSuperieur;

        if (existingOpt.isPresent()) {
            // Mise à jour
            employeSuperieur = existingOpt.get();
            employeSuperieur.setHierarchie(hierarchie);
            employeSuperieur.setUpdatedBy(currentUser);
        } else {
            // Création
            employeSuperieur = new EmployeSuperieur();
            employeSuperieur.setCompany(company);
            employeSuperieur.setEmploye(employe);
            employeSuperieur.setContratEmploye(contratEmploye);
            employeSuperieur.setHierarchie(hierarchie);
            employeSuperieur.setIsActive(true);
            employeSuperieur.setCreatedBy(currentUser);
            employeSuperieur.setUpdatedBy(currentUser);
        }

        return repository.save(employeSuperieur);
    }

    /**
     * Récupérer la hiérarchie d'un employé
     */
    public Optional<EmployeSuperieur> getHierarchieByContrat(Long contratEmployeId) {
        return repository.findByContratEmployeIdAndIsActiveTrue(contratEmployeId);
    }

    /**
     * Récupérer tous les subordonnés d'un supérieur
     */


    /**
     * Désactiver la hiérarchie (en cas de changement de contrat)
     */
    @Transactional
    public void desactiverHierarchie(Long contratEmployeId, User currentUser) {
        repository.findByContratEmployeIdAndIsActiveTrue(contratEmployeId)
                .ifPresent(es -> {
                    es.setIsActive(false);
                    es.setUpdatedBy(currentUser);
                    repository.save(es);
                });
    }

    /**
     * Valider qu'il n'y a pas de boucle hiérarchique
     */
    public boolean validateNoCircularHierarchy(Long employeId, HierarchieJson hierarchie) {
        // Vérifier que l'employé ne se retrouve pas dans sa propre hiérarchie
        return hierarchie.getSuperieurs().stream()
                .noneMatch(sup -> sup.getEmployeSuperieurId().equals(employeId));
    }

    /**
     * Récupère tous les employés subordonnés à un employé donné
     * VERSION CORRIGÉE - Utilise directement la native query
     */
    public List<SubordonneResponseDTO> getSubordonnes(Long superieurId) {

        // Vérifier que le supérieur existe
        if (!employeRepository.existsById(superieurId)) {
            throw new RuntimeException("Employé non trouvé avec ID: " + superieurId);
        }

        // Utiliser directement la native query
        List<Object[]> results = employeSuperieurRepository.findSubordonnesBySuperieurId(superieurId);


        return convertNativeQueryResultsToDTO(results);
    }

    /**
     * VERSION ALTERNATIVE avec JSON_CONTAINS (si JSON_SEARCH ne fonctionne pas)
     */
    public List<SubordonneResponseDTO> getSubordonnesV2(Long superieurId) {

        if (!employeRepository.existsById(superieurId)) {
            throw new RuntimeException("Employé non trouvé avec ID: " + superieurId);
        }

        List<Object[]> results = employeSuperieurRepository.findSubordonnesBySuperieurIdV2(superieurId);
        return convertNativeQueryResultsToDTO(results);
    }

    /**
     * Récupère uniquement les subordonnés directs (N+1)
     */
    public List<SubordonneResponseDTO> getSubordonnesDirects(Long superieurId) {

        if (!employeRepository.existsById(superieurId)) {
            throw new RuntimeException("Employé non trouvé avec ID: " + superieurId);
        }

        List<Object[]> results = employeSuperieurRepository.findSubordonnesDirectsBySuperieurId(superieurId);
        return convertNativeQueryResultsToDTO(results);
    }

    /**
     * Compte le nombre de subordonnés
     */
    public Long compterSubordonnes(Long superieurId) {
        Long count = employeSuperieurRepository.countSubordonnesBySuperieurId(superieurId);
        return count != null ? count : 0L;
    }

    /**
     * Vérifie si un employé a des subordonnés
     */
    public boolean hasSubordonnes(Long superieurId) {
        Long count = compterSubordonnes(superieurId);
        return count != null && count > 0;
    }

    /**
     * DEBUG : Récupère toutes les hiérarchies pour voir le contenu JSON
     */
    public List<String> debugHierarchies() {

        List<Object[]> results = employeSuperieurRepository.findAllHierarchiesForDebug();
        List<String> debug = new ArrayList<>();

        for (Object[] row : results) {
            String info = String.format("ID=%s, Employé=%s %s (ID=%s), Hierarchie=%s",
                    row[0], row[2], row[3], row[1], row[4]);
            debug.add(info);

        }

        return debug;
    }

    // ========================================
    // MÉTHODES UTILITAIRES PRIVÉES
    // ========================================

    /**
     * Convertit les résultats de la native query en DTOs
     * VERSION CORRIGÉE avec meilleure gestion des types
     */
    private List<SubordonneResponseDTO> convertNativeQueryResultsToDTO(List<Object[]> results) {
        List<SubordonneResponseDTO> dtos = new ArrayList<>();

        for (Object[] row : results) {
            try {
                SubordonneResponseDTO dto = new SubordonneResponseDTO(
                        getLong(row[0]),           // employeId
                        getString(row[1]),         // matricule
                        getString(row[2]),         // nom
                        getString(row[3]),         // prenom
                        getString(row[4]),         // titre
                        getString(row[5]),         // sexe
                        getString(row[6]),         // telephone
                        getString(row[7]),         // email
                        getLocalDate(row[8]),      // dateNaissance
                        getLong(row[9]),           // contratEmployeId
                        getString(row[10]),        // typeContrat
                        getString(row[11]),        // statusContrat
                        getLocalDate(row[12]),     // dateEmbauche
                        getLong(row[15]),          // posteId
                        getString(row[16]),        // posteLibelle
                        getLong(row[17]),          // departementId
                        getString(row[18]),        // departementLibelle
                        getString(row[19]),        // categorieEmploye
                        getLong(row[20]),          // companyId
                        getString(row[21]),        // companyName
                        getBoolean(row[22]),       // isActive
                        getLocalDateTime(row[23]), // createdAt
                        getLocalDateTime(row[24])  // updatedAt
                );
                dtos.add(dto);
            } catch (Exception e) {
                // Continue avec les autres enregistrements
            }
        }

        return dtos;
    }

    // Méthodes helper pour conversion sécurisée
    private Long getLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getString(Object value) {
        return value != null ? value.toString() : null;
    }

    private Double getDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Double) return (Double) value;
        if (value instanceof Number) return ((Number) value).doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {

            return null;
        }
    }

    private Boolean getBoolean(Object value) {
        if (value == null) return null;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).intValue() == 1;
        String str = value.toString().toLowerCase();
        return str.equals("true") || str.equals("1");
    }

    private LocalDate getLocalDate(Object value) {
        if (value == null) return null;
        if (value instanceof LocalDate) return (LocalDate) value;
        if (value instanceof java.sql.Date) return ((java.sql.Date) value).toLocalDate();
        try {
            return LocalDate.parse(value.toString());
        } catch (Exception e) {

            return null;
        }
    }

    private LocalDateTime getLocalDateTime(Object value) {
        if (value == null) return null;
        if (value instanceof LocalDateTime) return (LocalDateTime) value;
        if (value instanceof java.sql.Timestamp) return ((java.sql.Timestamp) value).toLocalDateTime();
        try {
            return LocalDateTime.parse(value.toString());
        } catch (Exception e) {

            return null;
        }
    }
}