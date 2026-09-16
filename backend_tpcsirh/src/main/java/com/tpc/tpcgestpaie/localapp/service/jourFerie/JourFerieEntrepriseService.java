package com.tpc.tpcgestpaie.localapp.service.jourFerie;

import com.tpc.tpcgestpaie.localapp.dto.jourFerie.JourFerieEntrepriseRequestDTO;
import com.tpc.tpcgestpaie.localapp.enums.StatutJourFerieEntreprise;
import com.tpc.tpcgestpaie.localapp.model.jourFerie.JourFerie;
import com.tpc.tpcgestpaie.localapp.model.jourFerie.JourFerieEntreprise;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.repository.jourFerie.JourFerieEntrepriseRepository;
import com.tpc.tpcgestpaie.localapp.repository.jourFerie.JourFerieRepository;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JourFerieEntrepriseService {

    private final JourFerieRepository jourFerieRepository;
    private final JourFerieEntrepriseRepository jourFerieEntrepriseRepository;
    private final CompanyRepository companyRepository;

    // ========================================
    // 🎯 RÉCUPÉRATION DES JOURS FÉRIÉS EFFECTIFS
    // ========================================

    /**
     * Récupère les jours fériés effectifs d'une entreprise pour une année
     * Logique : (Jours nationaux) - (Jours retirés) + (Jours ajoutés)
     */
    public List<JourFerie> getJoursFeriesEffectifs(Long companyId, int annee) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));

        String codePays = Optional.ofNullable(company.getCountryCode())
                .map(String::trim)
                .map(String::toUpperCase)
                .orElse("");

        // 1. Récupérer les jours fériés nationaux du pays
        List<JourFerie> joursFeriesNationaux = jourFerieRepository
                .findByPaysAndAnnee(codePays, annee);

        // 2. Récupérer les modifications de l'entreprise
        List<JourFerieEntreprise> modificationsEntreprise =
                jourFerieEntrepriseRepository.findByCompanyIdAndAnnee(companyId, annee);

        // 3. Séparer les ADD et les REMOVE
        Set<String> slugsRetires = modificationsEntreprise.stream()
                .filter(j -> j.getStatut() == StatutJourFerieEntreprise.REMOVE)
                .map(JourFerieEntreprise::getSlug)
                .collect(Collectors.toSet());

        List<JourFerieEntreprise> joursAjoutes = modificationsEntreprise.stream()
                .filter(j -> j.getStatut() == StatutJourFerieEntreprise.ADD)
                .toList();

        // 4. Filtrer les jours nationaux (enlever les REMOVE)
        List<JourFerie> joursFeriesEffectifs = joursFeriesNationaux.stream()
                .filter(j -> !slugsRetires.contains(j.getSlug()))
                .collect(Collectors.toList());

        // 5. Ajouter les jours personnalisés (ADD)
        List<JourFerie> joursPersonnalises = joursAjoutes.stream()
                .map(this::convertToJourFerie)
                .toList();

        joursFeriesEffectifs.addAll(joursPersonnalises);

        // 6. Trier par date
        joursFeriesEffectifs.sort(Comparator.comparing(JourFerie::getDateFerie));

        log.info("Jours fériés effectifs pour entreprise {} en {}: {} jours",
                companyId, annee, joursFeriesEffectifs.size());

        return joursFeriesEffectifs;
    }

    /**
     * Vérifie si une date est un jour férié pour une entreprise
     */
    public boolean isJourFerie(Long companyId, LocalDate date) {
        int annee = date.getYear();
        List<JourFerie> joursFeries = getJoursFeriesEffectifs(companyId, annee);

        return joursFeries.stream()
                .anyMatch(j -> j.getDateFerie().equals(date));
    }

    // ========================================
    // ➕ AJOUTER UN JOUR FÉRIÉ PERSONNALISÉ
    // ========================================

    /**
     * Ajoute un jour férié personnalisé pour une entreprise
     */
    public JourFerieEntreprise ajouterJourFeriePersonnalise(
            Long companyId, JourFerieEntrepriseRequestDTO request, Long userId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));

        // Vérifier si déjà existant
        String slug = generateSlug(request.getLibelle());
        int annee = request.getDateFerie().getYear();

        if (jourFerieEntrepriseRepository.existsByCompanyIdAndSlugAndAnnee(companyId, slug, annee)) {
            throw new RuntimeException("Ce jour férié existe déjà pour cette année");
        }

        JourFerieEntreprise jourFerie = JourFerieEntreprise.builder()
                .company(company)
                .statut(StatutJourFerieEntreprise.ADD)
                .slug(slug)
                .libelle(request.getLibelle())
                .dateFerie(request.getDateFerie())
                .pays(company.getCountry())
                .estFixe(request.getEstFixe())
                .estRecurrent(request.getEstRecurrent())
                .createdBy(userId)
                .build();

        log.info("Ajout du jour férié personnalisé '{}' pour l'entreprise {}", request.getLibelle(), companyId);

        return jourFerieEntrepriseRepository.save(jourFerie);
    }

    // ========================================
    // ➖ RETIRER UN JOUR FÉRIÉ NATIONAL
    // ========================================

    /**
     * Retire un jour férié national pour une entreprise
     */
    public JourFerieEntreprise retirerJourFerieNational(
            Long companyId,
            String slug,
            int annee,
            Long userId) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));

        // Vérifier si déjà retiré
        if (jourFerieEntrepriseRepository.hasRemovedJourFerie(companyId, slug, annee)) {
            throw new RuntimeException("Ce jour férié a déjà été retiré");
        }

        // Récupérer le jour férié national
        JourFerie jourFerieNational = jourFerieRepository
                .findBySlugAndPaysAndAnnee(slug, company.getCountry(), annee)
                .orElseThrow(() -> new RuntimeException("Jour férié national non trouvé"));

        JourFerieEntreprise jourFerieRetire = JourFerieEntreprise.builder()
                .company(company)
                .jourFerie(jourFerieNational)
                .statut(StatutJourFerieEntreprise.REMOVE)
                .slug(jourFerieNational.getSlug())
                .libelle(jourFerieNational.getLibelle())
                .dateFerie(jourFerieNational.getDateFerie())
                .pays(jourFerieNational.getPays())
                .estFixe(jourFerieNational.getEstFixe())
                .estRecurrent(jourFerieNational.getEstRecurrent())
                .createdBy(userId)
                .build();

        log.info("Retrait du jour férié '{}' pour l'entreprise {}", slug, companyId);

        return jourFerieEntrepriseRepository.save(jourFerieRetire);
    }

    // ========================================
    // 📋 GESTION DES MODIFICATIONS
    // ========================================

    /**
     * Récupère toutes les modifications (ADD et REMOVE) pour une année
     */
    public Map<String, List<JourFerieEntreprise>> getModifications(Long companyId, int annee) {
        List<JourFerieEntreprise> toutesModifications =
                jourFerieEntrepriseRepository.findByCompanyIdAndAnnee(companyId, annee);

        Map<String, List<JourFerieEntreprise>> result = new HashMap<>();

        result.put("ajoutes", toutesModifications.stream()
                .filter(j -> j.getStatut() == StatutJourFerieEntreprise.ADD)
                .toList());

        result.put("retires", toutesModifications.stream()
                .filter(j -> j.getStatut() == StatutJourFerieEntreprise.REMOVE)
                .toList());

        return result;
    }

    /**
     * Récupère uniquement les jours fériés ajoutés
     */
    public List<JourFerieEntreprise> getJoursFeriesAjoutes(Long companyId, int annee) {
        return jourFerieEntrepriseRepository.findByCompanyIdAndStatutAndDeletedAtIsNullOrderByDateFerieAsc(
                        companyId, StatutJourFerieEntreprise.ADD)
                .stream()
                .filter(j -> j.getDateFerie().getYear() == annee)
                .toList();
    }

    /**
     * Récupère uniquement les jours fériés retirés
     */
    public List<JourFerieEntreprise> getJoursFeriesRetires(Long companyId, int annee) {
        return jourFerieEntrepriseRepository.findByCompanyIdAndStatutAndDeletedAtIsNullOrderByDateFerieAsc(
                        companyId, StatutJourFerieEntreprise.REMOVE)
                .stream()
                .filter(j -> j.getDateFerie().getYear() == annee)
                .toList();
    }

    // ========================================
    // 🗑️ SUPPRESSION
    // ========================================

    /**
     * Supprime une modification spécifique
     */
    public void supprimerModification(Long jourFerieId) {
        JourFerieEntreprise jourFerie = jourFerieEntrepriseRepository.findById(jourFerieId)
                .orElseThrow(() -> new RuntimeException("Modification non trouvée"));

        jourFerie.setDeletedAt(java.time.LocalDateTime.now());
        jourFerieEntrepriseRepository.save(jourFerie);

        log.info("Suppression de la modification {}", jourFerieId);
    }

    /**
     * Supprime toutes les modifications d'une année
     */
    @Transactional
    public void supprimerToutesModifications(Long companyId, int annee) {
        jourFerieEntrepriseRepository.deleteAllByCompanyIdAndAnnee(companyId, annee);

        log.info("Suppression de toutes les modifications pour l'entreprise {} en {}",
                companyId, annee);
    }

    // ========================================
    // 📊 STATISTIQUES
    // ========================================

    /**
     * Génère des statistiques sur les jours fériés d'une entreprise
     */
    public Map<String, Object> getStatistiques(Long companyId, int annee) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));

        String pays = company.getCountry();

        // Jours nationaux
        List<JourFerie> joursNationaux = jourFerieRepository.findByPaysAndAnnee(pays, annee);

        // Jours ajoutés
        long nbAjoutes = jourFerieEntrepriseRepository
                .countJoursFeriesPersonnalises(companyId, annee);

        // Jours retirés
        long nbRetires = jourFerieEntrepriseRepository
                .countJoursFeriesRetires(companyId, annee);

        // Jours effectifs
        List<JourFerie> joursEffectifs = getJoursFeriesEffectifs(companyId, annee);

        // Jours tombant un weekend
        long nbWeekend = joursEffectifs.stream()
                .filter(JourFerie::tombeEnWeekend)
                .count();

        // Répartition par mois
        Map<Integer, Long> parMois = joursEffectifs.stream()
                .collect(Collectors.groupingBy(
                        j -> j.getDateFerie().getMonthValue(),
                        Collectors.counting()
                ));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalJoursFeries", joursEffectifs.size());
        stats.put("joursNationaux", joursNationaux.size());
        stats.put("joursAjoutes", nbAjoutes);
        stats.put("joursRetires", nbRetires);
        stats.put("joursEffectifs", joursEffectifs.size());
        stats.put("joursWeekend", nbWeekend);
        stats.put("parMois", parMois);

        return stats;
    }

    // ========================================
    // 🔧 UTILITAIRES
    // ========================================

    /**
     * Convertit un JourFerieEntreprise en JourFerie
     */
    private JourFerie convertToJourFerie(JourFerieEntreprise jfe) {
        return JourFerie.builder()
                .slug(jfe.getSlug())
                .libelle(jfe.getLibelle())
                .dateFerie(jfe.getDateFerie())
                .pays(jfe.getPays())
                .estFixe(jfe.getEstFixe())
                .estRecurrent(jfe.getEstRecurrent())
                .createdAt(jfe.getCreatedAt())
                .updatedAt(jfe.getUpdatedAt())
                .createdBy(jfe.getCreatedBy())
                .updatedBy(jfe.getUpdatedBy())
                .build();
    }

    /**
     * Génère un slug à partir d'un libellé
     */
    private String generateSlug(String libelle) {
        return libelle.toUpperCase()
                .replaceAll("[ÀÁÂÃÄÅ]", "A")
                .replaceAll("[ÈÉÊË]", "E")
                .replaceAll("[ÌÍÎÏ]", "I")
                .replaceAll("[ÒÓÔÕÖ]", "O")
                .replaceAll("[ÙÚÛÜ]", "U")
                .replaceAll("[Ç]", "C")
                .replaceAll("[^A-Z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }
}