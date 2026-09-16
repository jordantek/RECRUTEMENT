package com.tpc.tpcgestpaie.localapp.service.jourFerie;

import com.tpc.tpcgestpaie.localapp.dto.jourFerie.*;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.jourFerie.JourFerie;
import com.tpc.tpcgestpaie.localapp.model.jourFerie.JourFerieEntreprise;
import com.tpc.tpcgestpaie.localapp.model.jourFerie.JourFerieHistorique;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.jourFerie.JourFerieEntrepriseRepository;
import com.tpc.tpcgestpaie.localapp.repository.jourFerie.JourFerieHistoriqueRepository;
import com.tpc.tpcgestpaie.localapp.repository.jourFerie.JourFerieRepository;
import com.tpc.tpcgestpaie.localapp.repository.jourtravail.JoursTravaillesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service de gestion des jours fériés
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JourFerieService {

    private final JourFerieRepository jourFerieRepository;
    private final JourFerieEntrepriseRepository jourFerieEntrepriseRepository;
    private final JourFerieHistoriqueRepository historiqueRepository;
    private final JourFerieMapper mapper;
    private final JourFerieMobileCalculator mobileCalculator;
    private final CompanyRepository companyRepository;
    private final JoursTravaillesRepository joursTravaillesRepository;

    /**
     * Crée un nouveau jour férié
     */
    public JourFerieResponseDTO creerJourFerie(JourFerieCreateDTO dto, Long userId) {
        log.info("Création d'un nouveau jour férié: {}", dto.getLibelle());

        // Validation
        validerJourFerie(dto);

        // Conversion et sauvegarde
        JourFerie jourFerie = mapper.toEntity(dto);
        jourFerie.setCreatedBy(userId);
        jourFerie.setUpdatedBy(userId);

        JourFerie saved = jourFerieRepository.save(jourFerie);

        log.info("Jour férié créé avec succès: ID={}", saved.getId());
        return mapper.toResponseDTO(saved);
    }

    /**
     * Met à jour un jour férié existant
     */
    public JourFerieResponseDTO mettreAJourJourFerie(Long id, JourFerieUpdateDTO dto, Long userId) {
        log.info("Mise à jour du jour férié ID={}", id);

        JourFerie jourFerie = jourFerieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jour férié non trouvé"));

        mapper.updateEntityFromDto(jourFerie, dto);
        jourFerie.setUpdatedBy(userId);

        JourFerie updated = jourFerieRepository.save(jourFerie);

        log.info("Jour férié mis à jour avec succès: ID={}", id);
        return mapper.toResponseDTO(updated);
    }

    /**
     * Supprime un jour férié (soft delete)
     */
    public void supprimerJourFerie(Long id, Long userId) {
        log.info("Suppression du jour férié ID={}", id);

        JourFerie jourFerie = jourFerieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jour férié non trouvé"));

        jourFerie.setDeletedAt(LocalDateTime.now());
        jourFerie.setUpdatedBy(userId);
        jourFerieRepository.save(jourFerie);

        log.info("Jour férié supprimé avec succès: ID={}", id);
    }

    /**
     * Récupère tous les jours fériés d'une année
     */
    @Transactional(readOnly = true)
    public List<JourFerieResponseDTO> getJoursFeriesParAnnee(Integer annee) {
        return jourFerieRepository.findByYears(annee)
                .stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un jour férié par ID
     */
    @Transactional(readOnly = true)
    public JourFerieResponseDTO getJourFerieById(Long id) {
        JourFerie jourFerie = jourFerieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jour férié non trouvé"));
        return mapper.toResponseDTO(jourFerie);
    }

    /**
     * Génère automatiquement tous les jours fériés pour une année
     * Basé sur les jours fériés officiels du Bénin
     */
    public List<JourFerieResponseDTO> genererJoursFeriesAnnee(Integer annee, Long userId) {
        log.info("Génération des jours fériés pour l'année {}", annee);

        List<JourFerie> joursFeries = new ArrayList<>();
        // ===== JOURS FÉRIÉS FIXES =====

        // 1er janvier - Jour de l'An
        joursFeries.add(creerJourFerieFixe("JOUR_AN", "Jour de l'An",
                LocalDate.of(annee, 1, 1), 1, 1, annee, userId, true,"BJ"));

        // 10 janvier - Fête du Vodoun
        joursFeries.add(creerJourFerieFixe("FETE_VODOUN", "Fête du Vodoun",
                LocalDate.of(annee, 1, 10), 10, 1, annee, userId, true,"BJ"));

        // 1er mai - Fête du Travail
        joursFeries.add(creerJourFerieFixe("FETE_TRAVAIL", "Fête du Travail",
                LocalDate.of(annee, 5, 1), 1, 5, annee, userId, true,"BJ"));

        // 1er août - Fête Nationale
        joursFeries.add(creerJourFerieFixe("FETE_NATIONALE", "Fête Nationale du Bénin",
                LocalDate.of(annee, 8, 1), 1, 8, annee, userId, true,"BJ"));

        // 15 août - Assomption
        joursFeries.add(creerJourFerieFixe("ASSOMPTION", "Assomption",
                LocalDate.of(annee, 8, 15), 15, 8, annee, userId, true,"BJ"));

        // 26 octobre - Fête des Forces Armées
        joursFeries.add(creerJourFerieFixe("FORCES_ARMEES", "Fête des Forces Armées",
                LocalDate.of(annee, 10, 26), 26, 10, annee, userId, true,"BJ"));

        // 1er novembre - Toussaint
        joursFeries.add(creerJourFerieFixe("TOUSSAINT", "Toussaint",
                LocalDate.of(annee, 11, 1), 1, 11, annee, userId, true,"BJ"));

        // 30 novembre - Fête Nationale (St André)
        joursFeries.add(creerJourFerieFixe("SAINT_ANDRE", "Saint André",
                LocalDate.of(annee, 11, 30), 30, 11, annee, userId, true,"BJ"));

        // 25 décembre - Noël
        joursFeries.add(creerJourFerieFixe("NOEL", "Noël",
                LocalDate.of(annee, 12, 25), 25, 12, annee, userId, true,"BJ"));

        // ===== JOURS FÉRIÉS MOBILES (Chrétiens) =====

        // Lundi de Pâques
        LocalDate lundiPaques = mobileCalculator.calculerLundiPaques(annee);
        joursFeries.add(creerJourFerieMobile("LUNDI_PAQUES", "Lundi de Pâques",
                lundiPaques, annee, userId, true,"BJ"));

        // Ascension
        LocalDate ascension = mobileCalculator.calculerAscension(annee);
        joursFeries.add(creerJourFerieMobile("ASCENSION", "Ascension",
                ascension, annee, userId, true,"BJ"));

        // Lundi de Pentecôte
        LocalDate lundiPentecote = mobileCalculator.calculerLundiPentecote(annee);
        joursFeries.add(creerJourFerieMobile("LUNDI_PENTECOTE", "Lundi de Pentecôte",
                lundiPentecote, annee, userId, true,"BJ"));

        // ===== JOURS FÉRIÉS MOBILES (Musulmans) =====
        // Note: Ces dates sont approximatives car basées sur le calendrier lunaire

        // Korité (Aïd el-Fitr)
        LocalDate korite = mobileCalculator.calculerKorite(annee);
        joursFeries.add(creerJourFerieMobile("KORITE", "Korité (Aïd el-Fitr)",
                korite, annee, userId, true,"BJ"));

        // Tabaski (Aïd el-Kebir)
        LocalDate tabaski = mobileCalculator.calculerTabaski(annee);
        joursFeries.add(creerJourFerieMobile("TABASKI", "Tabaski (Aïd el-Kébir)",
                tabaski, annee, userId, true,"BJ"));

        // Maouloud
        LocalDate maouloud = mobileCalculator.calculerMaouloud(annee);
        joursFeries.add(creerJourFerieMobile("MAOULOUD", "Maouloud (Anniversaire du Prophète)",
                maouloud, annee, userId, true,"BJ"));

        // Sauvegarde de tous les jours fériés
        List<JourFerie> saved = jourFerieRepository.saveAll(joursFeries);

        log.info("{} jours fériés générés pour l'année {}", saved.size(), annee);

        return saved.stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }



    // ===== MÉTHODES PRIVÉES =====

    private JourFerie creerJourFerieFixe(String code, String libelle, LocalDate date,
                                         Integer jour, Integer mois, Integer annee,
                                         Long userId, boolean estNational, String pays) {
        return JourFerie.builder()
                .slug(code + "_" + annee)
                .libelle(libelle)
                .dateFerie(date)
                .pays(pays)
                .estFixe(true)
                .estRecurrent(true)
                .createdBy(userId)
                .updatedBy(userId)
                .build();
    }

    private JourFerie creerJourFerieMobile(String code, String libelle, LocalDate date,
                                           Integer annee, Long userId, boolean estNational, String pays) {
        return JourFerie.builder()
                .slug(code + "_" + annee)
                .libelle(libelle)
                .dateFerie(date)
                .pays(pays)
                .estFixe(false)
                .estRecurrent(false)
                .createdBy(userId)
                .updatedBy(userId)
                .build();
    }

    private void validerJourFerie(JourFerieCreateDTO dto) {
        if (dto.getLibelle() == null || dto.getLibelle().trim().isEmpty()) {
            throw new IllegalArgumentException("Le libellé est obligatoire");
        }
        if (dto.getDateFerie() == null) {
            throw new IllegalArgumentException("La date est obligatoire");
        }

    }

    private void enregistrerHistorique(Company company, JourFerieEntreprise association,
                                       JourFerieHistorique.TypeActionJourFerie typeAction,
                                       String description, Long userId) {
        enregistrerHistorique(company, association, typeAction, description, null, userId);
    }

    private void enregistrerHistorique(Company company, JourFerieEntreprise association,
                                       JourFerieHistorique.TypeActionJourFerie typeAction,
                                       String description, String valeurApres, Long userId) {
        JourFerieHistorique historique = JourFerieHistorique.builder()
                .company(company)
                .jourFerieEntreprise(association)
                .typeAction(typeAction)
                .description(description)
                .valeurApres(valeurApres)
                .dateModification(LocalDateTime.now())
                .modifiedBy(userId)
                .build();

        historiqueRepository.save(historique);
    }






}