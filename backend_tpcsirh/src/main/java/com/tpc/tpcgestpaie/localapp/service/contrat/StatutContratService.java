package com.tpc.tpcgestpaie.localapp.service.contrat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tpc.tpcgestpaie.localapp.dto.contrat.ContratUpdateDTO;
import com.tpc.tpcgestpaie.localapp.dto.contrat.StatutContratDTO;
import com.tpc.tpcgestpaie.localapp.dto.contrat.StatutContratMapper;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class StatutContratService {

    private final StatutContratRepository statutContratRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final ObjectMapper objectMapper;
    private final CategorieEmployeRepository categorieEmployeRepository;
    private final DepartementRepository departementRepository;
    private final PosteRepository posteRepository;

    public StatutContratService(
            StatutContratRepository statutContratRepository,
            ContratEmployeRepository contratEmployeRepository,
            ObjectMapper objectMapper,
            CategorieEmployeRepository categorieEmployeRepository,
            DepartementRepository departementRepository,
            PosteRepository posteRepository) {
        this.statutContratRepository = statutContratRepository;
        this.contratEmployeRepository = contratEmployeRepository;
        this.objectMapper = objectMapper;
        this.categorieEmployeRepository = categorieEmployeRepository;
        this.departementRepository = departementRepository;
        this.posteRepository = posteRepository;
    }

    @Transactional
    public StatutContratDTO archiverEtModifierContrat(ContratUpdateDTO dto) throws Exception {
        try {
            // 1️⃣ Charger le contrat existant
            ContratEmploye contrat = contratEmployeRepository.findById(dto.getContratEmployeId())
                    .orElseThrow(() -> new IllegalArgumentException("Contrat employé introuvable"));

            // 2️⃣ Désactiver les anciens statuts actifs
            List<StatutContrat> anciensStatuts = statutContratRepository.findByContratEmployeIdAndActifTrue(contrat.getId());
            anciensStatuts.forEach(statut -> {
                statut.setActif(false);
                statutContratRepository.save(statut);
            });

            // 3️⃣ Upload de la preuve
            String nomFichierPreuve = null;
            if (dto.getPreuve() != null && !dto.getPreuve().isEmpty()) {
                nomFichierPreuve = sauvegarderFichier(dto.getPreuve());
            }

            // 4️⃣ Créer le nouveau statut
            StatutContrat nouveauStatut = new StatutContrat();
            nouveauStatut.setContratEmploye(contrat);
            nouveauStatut.setDateEffet(dto.getDateEffet() != null ? dto.getDateEffet() : LocalDate.now());
            nouveauStatut.setActif(true);
            nouveauStatut.setMotif(dto.getMotif());
            nouveauStatut.setPreuve(nomFichierPreuve);
            nouveauStatut.setInitialisation(anciensStatuts.isEmpty());

            // ============================================
            // NOUVEAUX CHAMPS AVENANTS
            // ============================================
            nouveauStatut.setLieuContrat(dto.getLieuContrat());
            nouveauStatut.setQualificationProfessionnelle(dto.getQualificationProfessionnelle());
            nouveauStatut.setTravailAFaire(dto.getTravailAFaire());
            nouveauStatut.setHoraireTravail(dto.getHoraireTravail());
            nouveauStatut.setNatureJuridiqueEmployeur(dto.getNatureJuridiqueEmployeur());
            nouveauStatut.setAncienneSituation(dto.getAncienneSituation());
            nouveauStatut.setNouvelleSituation(dto.getNouvelleSituation());
            nouveauStatut.setCommentaire(dto.getCommentaire());

            if (dto.getTypeModification() != null) {
                try {
                    nouveauStatut.setTypeModification(
                            StatutContrat.TypeModification.valueOf(dto.getTypeModification())
                    );
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Type de modification invalide : " + dto.getTypeModification());
                }
            }

            // 5️⃣ Snapshot sécurisé - Désactiver FAIL_ON_EMPTY_BEANS
            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            String snapshot = objectMapper.writeValueAsString(contrat);
            nouveauStatut.setSnapshotJson(snapshot);

            statutContratRepository.save(nouveauStatut);

            // 6️⃣ Mise à jour du contrat
            if (dto.getCategorieEmployeId() != null) {
                CategorieEmploye cat = categorieEmployeRepository.findById(dto.getCategorieEmployeId())
                        .orElseThrow(() -> new IllegalArgumentException("Catégorie employé introuvable"));
                contrat.setCategorieEmploye(cat);
            }
            if (dto.getDepartementId() != null) {
                Departement dep = departementRepository.findById(dto.getDepartementId())
                        .orElseThrow(() -> new IllegalArgumentException("Département introuvable"));
                contrat.setDepartement(dep);
            }
            if (dto.getPosteId() != null) {
                Poste poste = posteRepository.findById(dto.getPosteId())
                        .orElseThrow(() -> new IllegalArgumentException("Poste introuvable"));
                contrat.setPoste(poste);
            }

            if (dto.getTypeContrat() != null) {
                contrat.setType_contrat(dto.getTypeContrat());
            }

            // Salaires
            if (dto.getSalaireBase() != null) {
                contrat.setSalaire_base(dto.getSalaireBase());
            }
            if (dto.getSalaireBrut() != null) {
                contrat.setSalaire_brut(dto.getSalaireBrut());
            }

            if (dto.getDateFinContrat() != null) {
                contrat.setDate_fin(dto.getDateFinContrat());
            }

            // ============================================
            // MISE À JOUR NOUVEAUX CHAMPS SUR CONTRAT
            // ============================================
            if (dto.getLieuContrat() != null) {
                contrat.setLieu_execution(dto.getLieuContrat());
            }
            if (dto.getQualificationProfessionnelle() != null) {
                contrat.setDiplome_requis(dto.getQualificationProfessionnelle());
            }
            if (dto.getTravailAFaire() != null) {
                contrat.setMissions(dto.getTravailAFaire());
            }
            if (dto.getHoraireTravail() != null) {
                contrat.setHoraireTravail(dto.getHoraireTravail());
            }
            if (dto.getNatureJuridiqueEmployeur() != null) {
                contrat.setNatureJuridiqueEmployeur(dto.getNatureJuridiqueEmployeur());
            }
            if (dto.getAncienneSituation() != null) {
                contrat.setAncienneSituation(dto.getAncienneSituation());
            }
            if (dto.getNouvelleSituation() != null) {
                contrat.setNouvelleSituation(dto.getNouvelleSituation());
            }
            if (dto.getCommentaire() != null) {
                contrat.setCommentaire(dto.getCommentaire());
            }

            // Sauvegarder le contrat mis à jour
            ContratEmploye contratMisAJour = contratEmployeRepository.save(contrat);

            // 7️⃣ Retourner le DTO
            return mapToStatutContratDTO(nouveauStatut, contratMisAJour);

        } catch (Exception e) {
            throw new Exception("Erreur lors de la modification du contrat: " + e.getMessage(), e);
        }
    }

    // Méthode de mapping séparée pour plus de clarté
    private StatutContratDTO mapToStatutContratDTO(StatutContrat statut, ContratEmploye contrat) {
        StatutContratDTO response = new StatutContratDTO();

        // Mapper les champs du statut
        response.setId(statut.getId());
        response.setTypeModification(statut.getTypeModification() != null ? statut.getTypeModification().name() : null);
        response.setMotif(statut.getMotif());
        response.setPreuve(statut.getPreuve());
        response.setDateEffet(statut.getDateEffet());
        response.setActif(statut.isActif());
        response.setInitialisation(statut.isInitialisation());
        response.setSnapshotJson(statut.getSnapshotJson());
        response.setCreatedAt(statut.getCreatedAt());
        response.setUpdatedAt(statut.getUpdatedAt());

        // ============================================
        // MAPPER NOUVEAUX CHAMPS DU STATUT
        // ============================================
        response.setLieuContrat(statut.getLieuContrat());
        response.setQualificationProfessionnelle(statut.getQualificationProfessionnelle());
        response.setTravailAFaire(statut.getTravailAFaire());
        response.setHoraireTravail(statut.getHoraireTravail());
        response.setNatureJuridiqueEmployeur(statut.getNatureJuridiqueEmployeur());
        response.setAncienneSituation(statut.getAncienneSituation());
        response.setNouvelleSituation(statut.getNouvelleSituation());
        response.setCommentaire(statut.getCommentaire());

        // Mapper les champs du contrat mis à jour
        response.setContratEmployeId(contrat.getId());
        response.setCategorieEmployeId(contrat.getCategorieEmploye() != null ? contrat.getCategorieEmploye().getId() : null);
        response.setDepartementId(contrat.getDepartement() != null ? contrat.getDepartement().getId() : null);
        response.setPosteId(contrat.getPoste() != null ? contrat.getPoste().getId() : null);
        response.setTypeContrat(contrat.getType_contrat());
        response.setDateFinContrat(contrat.getDate_fin());
        response.setSalaireBase(contrat.getSalaire_base());
        response.setSalaireBrut(contrat.getSalaire_brut());

        // ============================================
        // MAPPER NOUVEAUX CHAMPS DU CONTRAT
        // ============================================
        response.setLieuContrat(contrat.getLieu_execution());
        response.setQualificationProfessionnelle(contrat.getDiplome_requis());
        response.setTravailAFaire(contrat.getMissions());
        response.setHoraireTravail(contrat.getHoraireTravail());
        response.setNatureJuridiqueEmployeur(contrat.getNatureJuridiqueEmployeur());
        response.setAncienneSituation(contrat.getAncienneSituation());
        response.setNouvelleSituation(contrat.getNouvelleSituation());
        response.setCommentaire(contrat.getCommentaire());

        return response;
    }

    private String sauvegarderFichier(MultipartFile fichier) throws IOException {
        // Créer un nom de fichier unique
        String nomOriginal = fichier.getOriginalFilename();
        String extension = nomOriginal.substring(nomOriginal.lastIndexOf("."));
        String nomFichier = UUID.randomUUID().toString() + extension;

        // Chemin de stockage (à configurer selon votre besoin)
        Path cheminStockage = Paths.get("uploads/preuves/" + nomFichier);

        // Créer les dossiers si nécessaire
        Files.createDirectories(cheminStockage.getParent());

        // Sauvegarder le fichier
        Files.copy(fichier.getInputStream(), cheminStockage, StandardCopyOption.REPLACE_EXISTING);

        return nomFichier;
    }

    public List<StatutContratDTO> getStatutsByEmploye(Long employeId) {
        List<StatutContrat> statuts = statutContratRepository.findByContratEmploye_Employe_Id(employeId);

        return statuts.stream()
                .map(StatutContratMapper::toDTO)
                .toList();
    }

    // Méthode utilitaire pour fusionner avec repository
    private <T, R> R resolve(Long dtoId, R fallback, JpaRepository<R, Long> repository) {
        if (dtoId != null) return repository.findById(dtoId).orElse(fallback);
        return fallback;
    }
}