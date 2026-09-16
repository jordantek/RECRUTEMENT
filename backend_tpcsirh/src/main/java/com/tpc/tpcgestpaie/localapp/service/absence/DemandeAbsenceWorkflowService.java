package com.tpc.tpcgestpaie.localapp.service.absence;

import com.tpc.tpcgestpaie.localapp.dto.absence.*;
import com.tpc.tpcgestpaie.localapp.dto.conge.consommation.ConfirmationResponseDTO;
import com.tpc.tpcgestpaie.localapp.dto.conge.consommation.ResultatConsommationDTO;
import com.tpc.tpcgestpaie.localapp.enums.StatutDemandeAbsence;
import com.tpc.tpcgestpaie.localapp.enums.UniteAbsence;
import com.tpc.tpcgestpaie.localapp.exception.conge.SoldeInsuffisantException;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.model.absence.*;
import com.tpc.tpcgestpaie.localapp.model.jourFerie.JourFerie;
import com.tpc.tpcgestpaie.localapp.repository.*;
import com.tpc.tpcgestpaie.localapp.repository.absence.*;
import com.tpc.tpcgestpaie.localapp.repository.administration.*;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.conge.ConsommationCongeService;
import com.tpc.tpcgestpaie.localapp.service.conge.SoldeCongeService;
import com.tpc.tpcgestpaie.localapp.service.jourFerie.JourFerieEntrepriseService;
import com.tpc.tpcgestpaie.localapp.util.GlobalEnums;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.tpc.tpcgestpaie.localapp.util.GlobalEnums.ConditionAcceptationConge.A_DEDUIRE_DES_CONGES;

@Slf4j
@Service
@RequiredArgsConstructor
public class DemandeAbsenceWorkflowService {

    private final DemandeAbsenceRepository demandeAbsenceRepository;
    private final SuiviAbsenceRepository suiviAbsenceRepository;
    private final ValidationNiveauRepository validationNiveauRepository;
    private final EmployeSuperieurRepository employeSuperieurRepository;
    private final EmployeRepository employeRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final TypeAbsenceRepository typeAbsenceRepository;
    private final MotifAbsenceRepository motifAbsenceRepository;
    private final NotificationDemandeAbsenceService notificationService;
    private final UserService userService;
    private final JourFerieEntrepriseService jourFerieEntrepriseService;

    private final CalculJoursAbsenceService calculJoursAbsenceService;
    private final CompanyRepository companyRepository;
    private final SoldeCongeService soldeCongeService;
    private final ConsommationCongeService consommationCongeService;

    // ========================================
    // 1. CRÉATION DE DEMANDE
    // ========================================

    @Transactional
    public DemandeAbsence creerDemande(DemandeAbsenceCreateDTO dto) {
        // 1. Récupérer le contrat et l'employé
        Employe employe = employeRepository.findById(dto.getEmployeId())
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

        ContratEmploye contrat = contratEmployeRepository.findActifByEmployeId(dto.getEmployeId())
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé"));

        // 2. Vérifier doublons et chevauchements
        if (demandeAbsenceRepository.existsByEmployeDemandeurAndDateDebutAndDateFin(
                employe, dto.getDateDebut(), dto.getDateFin())) {
            throw new RuntimeException("Une demande existe déjà pour cette période");
        }
        if (demandeAbsenceRepository.existsByEmployeDemandeurAndDateDebutBetweenOrDateFinBetween(
                employe, dto.getDateDebut(), dto.getDateFin())) {
            throw new RuntimeException("Une demande chevauche avec une période existante");
        }
        if (dto.getDateFin().isBefore(dto.getDateDebut())) {
            throw new RuntimeException("La date de fin ne peut pas être antérieure à la date de début");
        }

        // 3. Hiérarchie de validation
        EmployeSuperieur hierarchie = employeSuperieurRepository
                .findByContratEmployeIdAndIsActiveTrue(contrat.getId())
                .orElseThrow(() -> new RuntimeException("Aucune hiérarchie définie pour cet employé"));

        // Validation de la date de fin selon l'unité
        if (dto.getUniteAbsence() != UniteAbsence.DEMI_JOURNEE &&
                dto.getUniteAbsence() != UniteAbsence.HEURE) {
            if (dto.getDateFin() == null) {
                throw new RuntimeException("La date de fin est obligatoire pour une absence d'une journée entière ou plus");
            }
        } else {
            if (dto.getDateFin() == null) {
                dto.setDateFin(dto.getDateDebut());
            }
        }

        // 4. Créer la demande
        DemandeAbsence demande = new DemandeAbsence();
        demande.setEmployeDemandeur(employe);
        demande.setContratEmploye(contrat);
        demande.setCompanyId(employe.getCompany() != null ? employe.getCompany().getId() : null);
        demande.setTypeAbsence(typeAbsenceRepository.findById(dto.getTypeAbsenceId())
                .orElseThrow(() -> new RuntimeException("Type d'absence non trouvé")));
        demande.setMotifDemande(dto.getMotifDemande());
        demande.setDateDebut(dto.getDateDebut());
        demande.setDateFin(dto.getDateFin());
        demande.setUniteAbsence(dto.getUniteAbsence());

        // 🆕 CALCUL PRÉVISIONNEL SELON LE TYPE D'ABSENCE
        Long companyId = employe.getCompany().getId();
        long nbJoursCalendaires = ChronoUnit.DAYS.between(dto.getDateDebut(), dto.getDateFin()) + 1;

        // Récupérer les jours fériés pour la période
        Set<LocalDate> datesFeries = getJoursFeriesPeriode(companyId, dto.getDateDebut(), dto.getDateFin());
        long nbJoursFeries = datesFeries.size();
        long nbJoursTravailles = nbJoursCalendaires - nbJoursFeries;

        switch (dto.getUniteAbsence()) {
            case HEURE -> {
                if (dto.getHeureDepart() == null || dto.getHeureArrivee() == null) {
                    throw new RuntimeException("Les heures de début et de fin sont obligatoires pour une absence horaire");
                }

                // Heures par jour × nombre de jours travaillés
                int heuresParJour = (int) ChronoUnit.HOURS.between(dto.getHeureDepart(), dto.getHeureArrivee());
                int totalHeures = (int) (nbJoursTravailles * heuresParJour);
                double joursOuvres = totalHeures / 8.0;

                demande.setHeureDepart(dto.getHeureDepart());
                demande.setHeureArrivee(dto.getHeureArrivee());
                demande.setNombreHeures(heuresParJour); // Heures par jour (pour info)
                demande.setNombreJours((int) Math.round(arrondir(joursOuvres, 2))); // Équivalent en jours

                log.info("Création absence HEURE: {}h/jour × {} jours = {}h total = {} jours ouvrés",
                        heuresParJour, nbJoursTravailles, totalHeures, joursOuvres);
            }

            case DEMI_JOURNEE -> {
                // 0.5 jour ouvré par jour travaillé
                double totalJoursOuvres = nbJoursTravailles * 0.5;
                int totalHeures = (int) (totalJoursOuvres * 8);


                demande.setNombreJours((int) Math.round(arrondir(totalJoursOuvres, 2)));
                demande.setNombreHeures(4); // 4h par demi-journée (pour info)

                log.info("Création absence DEMI_JOURNEE: {} demi-journées = {} jours ouvrés ({} heures)",
                        nbJoursTravailles, totalJoursOuvres, totalHeures);
            }

            case JOURNEE_ENTIERE -> {
                // Calcul standard avec jours fériés et weekends exclus
                CalculJoursAbsenceService.CalculJoursAbsenceResultat calcul =
                        calculJoursAbsenceService.calculerJoursAbsenceReels(companyId, dto.getDateDebut(), dto.getDateFin());

                demande.setNombreJours(calcul.getJoursAbsenceReels());
                demande.setNombreHeures(null);
            }
        }

        // 5. Gestion justificatif
        if (dto.getJustificatif() != null && !dto.getJustificatif().isEmpty()) {
            try {
                String fileName = UUID.randomUUID() + "_" +
                        dto.getJustificatif().getOriginalFilename();
                Path uploadPath = Paths.get("uploads/absences");
                Files.createDirectories(uploadPath);
                Files.copy(dto.getJustificatif().getInputStream(),
                        uploadPath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING);
                demande.setPieceJustificatif(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de l'enregistrement du justificatif");
            }
        }

        // 6. Workflow et validation
        HierarchieValidation hierarchieValidation = creerHierarchieValidation(hierarchie);
        demande.setHierarchieValidation(hierarchieValidation);
        demande.setNiveauValidationEnCours(1);
        demande.setStatut(StatutDemandeAbsence.EN_ATTENTE_VALIDATION);

        // 7. Sauvegarde
        demande = demandeAbsenceRepository.save(demande);

        // 8. Création validations, suivi et notifications
        creerValidationsNiveaux(demande, hierarchieValidation);
        creerSuiviAbsence(demande, nbJoursTravailles, datesFeries.size());
        notifierTousLesValidateurs(demande, hierarchieValidation);

        return demande;
    }

    /**
     * 🆕 Récupère les jours fériés effectifs pour une période
     */
    private Set<LocalDate> getJoursFeriesPeriode(Long companyId, LocalDate dateDebut, LocalDate dateFin) {
        Set<LocalDate> datesFeries = new HashSet<>();
        int anneeDebut = dateDebut.getYear();
        int anneeFin = dateFin.getYear();

        for (int annee = anneeDebut; annee <= anneeFin; annee++) {
            List<JourFerie> joursFeriesAnnee = jourFerieEntrepriseService.getJoursFeriesEffectifs(companyId, annee);
            datesFeries.addAll(
                    joursFeriesAnnee.stream()
                            .map(JourFerie::getDateFerie)
                            .filter(date -> !date.isBefore(dateDebut) && !date.isAfter(dateFin))
                            .collect(Collectors.toSet())
            );
        }
        return datesFeries;
    }

    private HierarchieValidation creerHierarchieValidation(EmployeSuperieur hierarchie) {
        HierarchieValidation hierarchieValidation = new HierarchieValidation();
        List<HierarchieValidation.NiveauValidateur> niveaux = new ArrayList<>();

        for (HierarchieJson.SuperieurHierarchique sup : hierarchie.getHierarchie().getSuperieurs()) {
            HierarchieValidation.NiveauValidateur niveau = new HierarchieValidation.NiveauValidateur();
            niveau.setOrdre(sup.getOrdre());
            niveau.setContratSuperieurId(sup.getContratSuperieurId());
            niveau.setEmployeSuperieurId(sup.getEmployeSuperieurId());
            niveau.setFonction(sup.getFonction());
            niveau.setNomComplet(sup.getNomComplet());
            niveau.setStatut("EN_ATTENTE");
            niveaux.add(niveau);
        }

        hierarchieValidation.setNiveaux(niveaux);
        return hierarchieValidation;
    }

    private void creerValidationsNiveaux(DemandeAbsence demande, HierarchieValidation hierarchieValidation) {
        for (HierarchieValidation.NiveauValidateur niv : hierarchieValidation.getNiveaux()) {
            ValidationNiveau validation = new ValidationNiveau();
            validation.setDemandeAbsence(demande);
            validation.setOrdreNiveau(niv.getOrdre());
            validation.setValidateur(employeRepository.findById(niv.getEmployeSuperieurId()).orElse(null));
            validation.setFonctionValidateur(niv.getFonction());
            validation.setStatut("EN_ATTENTE");
            validationNiveauRepository.save(validation);
        }
    }

    /**
     * 🆕 Création du suivi avec les infos de calcul
     */
    private void creerSuiviAbsence(DemandeAbsence demande, long nbJoursTravailles, long nbJoursFeries) {
        SuiviAbsence suivi = new SuiviAbsence();
        suivi.setDemandeAbsence(demande);
        suivi.setDateDepartPrevue(demande.getDateDebut());
        suivi.setDateRetourPrevue(demande.getDateFin());
        suivi.setJoursPrevu(demande.getNombreJours());
        suivi.setHeuresPrevu(calculerHeuresPrevues(demande, nbJoursTravailles));
        suivi.setDepartConfirme(false);
        suivi.setRetourConfirme(false);

        // Stockage pour traçabilité
//        suivi.setJoursCalendairesPrevu((int) (nbJoursTravailles + nbJoursFeries));
//        suivi.setjo((int) nbJoursFeries);

        suiviAbsenceRepository.save(suivi);
    }

    /**
     * 🆕 Calcule les heures prévues totales
     */
    private Integer calculerHeuresPrevues(DemandeAbsence demande, long nbJoursTravailles) {
        return switch (demande.getUniteAbsence()) {
            case HEURE -> {
                int heuresParJour = (int) ChronoUnit.HOURS.between(
                        demande.getHeureDepart(),
                        demande.getHeureArrivee()
                );
                yield (int) (nbJoursTravailles * heuresParJour);
            }
            case DEMI_JOURNEE -> (int) (nbJoursTravailles * 4);
            case JOURNEE_ENTIERE -> (int) (nbJoursTravailles * 8);
        };
    }

    private void notifierTousLesValidateurs(DemandeAbsence demande, HierarchieValidation hierarchieValidation) {
        for (int i = 0; i < hierarchieValidation.getNiveaux().size(); i++) {
            HierarchieValidation.NiveauValidateur niveau = hierarchieValidation.getNiveaux().get(i);
            Employe validateur = employeRepository.findById(niveau.getEmployeSuperieurId()).orElse(null);

            if (validateur != null) {
                String message = (i == 0)
                        ? "Nouvelle demande d'absence à valider de " + demande.getEmployeDemandeur().getNom()
                        : "Nouvelle demande d'absence en attente (niveau " + niveau.getOrdre() + ") de " +
                        demande.getEmployeDemandeur().getNom();

                notificationService.envoyerNotificationDemande(
                        validateur.getEmail(),
                        "Demande d'absence - " + demande.getEmployeDemandeur().getNom(),
                        message
                );
            }
        }
    }

    // ========================================
    // 2. VALIDATION PAR NIVEAU
    // ========================================

    @Transactional
    public void validerParNiveau(ValidationNiveauDTO dto, Long validateurId) {
        // ... (inchangé)
        DemandeAbsence demande = demandeAbsenceRepository.findById(dto.getDemandeId())
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        Integer niveauActuelOrdre = demande.getNiveauValidationEnCours();
        if (niveauActuelOrdre == null) {
            throw new RuntimeException("Cette demande n'attend plus de validation");
        }

        HierarchieValidation.NiveauValidateur niveauActuel = demande.getHierarchieValidation()
                .getNiveaux()
                .stream()
                .filter(n -> n.getOrdre().equals(niveauActuelOrdre))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Niveau de validation non trouvé"));

        if (!niveauActuel.getEmployeSuperieurId().equals(validateurId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à valider à ce niveau");
        }

        ValidationNiveau validation = validationNiveauRepository
                .findByDemandeAbsenceIdAndOrdreNiveau(demande.getId(), niveauActuelOrdre)
                .orElseThrow(() -> new RuntimeException("Validation non trouvée"));

        int prochainNiveau = niveauActuelOrdre + 1;
        boolean estDernierNiveau = prochainNiveau > demande.getHierarchieValidation().getNiveaux().size();

        if (dto.getAccepter()) {
            if (estDernierNiveau && dto.getConditionAcceptation() == null) {
                throw new RuntimeException("La condition d'acceptation est obligatoire pour la validation finale");
            }

            validation.setStatut("APPROUVE");
            validation.setCommentaire(dto.getCommentaire());
            validation.setDateAction(LocalDateTime.now());

            if (estDernierNiveau && dto.getConditionAcceptation() != null) {
                validation.setConditionAcceptationConge(dto.getConditionAcceptation().name());
            } else {
                validation.setConditionAcceptationConge(null);
            }

            niveauActuel.setStatut("APPROUVE");
            niveauActuel.setCommentaire(dto.getCommentaire());
            niveauActuel.setDateAction(LocalDateTime.now().toString());

            if (estDernierNiveau) {
                demande.setStatut(StatutDemandeAbsence.APPROUVE_FINAL);
                demande.setNiveauValidationEnCours(null);
                notifierEmployeValidationFinale(demande);
            } else {
                demande.setNiveauValidationEnCours(prochainNiveau);
                demande.setStatut(StatutDemandeAbsence.EN_COURS_VALIDATION);
                notifierProchainValidateur(demande, prochainNiveau);
            }

        } else {
            if (dto.getRaisonRejet() == null || dto.getRaisonRejet().trim().isEmpty()) {
                throw new RuntimeException("La raison du rejet est obligatoire");
            }

            validation.setStatut("REJETE");
            validation.setRaisonRejet(dto.getRaisonRejet());
            validation.setDateAction(LocalDateTime.now());
            validation.setConditionAcceptationConge(null);

            niveauActuel.setStatut("REJETE");
            niveauActuel.setRaisonRejet(dto.getRaisonRejet());
            niveauActuel.setDateAction(LocalDateTime.now().toString());

            demande.setStatut(StatutDemandeAbsence.REJETE);
            demande.setNiveauValidationEnCours(null);

            notifierEmployeRejet(demande, dto.getRaisonRejet());
            notifierAutresValidateursRejet(demande);
        }

        validationNiveauRepository.save(validation);
        demandeAbsenceRepository.save(demande);
    }

    private void notifierProchainValidateur(DemandeAbsence demande, int prochainNiveau) {
        HierarchieValidation.NiveauValidateur niveau = demande.getHierarchieValidation()
                .getNiveaux()
                .get(prochainNiveau - 1);

        Employe validateur = employeRepository.findById(niveau.getEmployeSuperieurId()).orElse(null);
        if (validateur != null) {
            notificationService.envoyerNotificationDemande(
                    validateur.getEmail(),
                    "À valider : Demande d'absence",
                    "La demande d'absence de " + demande.getEmployeDemandeur().getNom() + " " + demande.getEmployeDemandeur().getPrenom() +
                            " vous est transmise pour validation (Niveau " + prochainNiveau + ")"
            );
        }
    }

    private void notifierEmployeValidationFinale(DemandeAbsence demande) {
        notificationService.envoyerNotificationDemande(
                demande.getEmployeDemandeur().getEmail(),
                "Demande d'absence approuvée",
                "Votre demande d'absence du " + demande.getDateDebut() + " au " + demande.getDateFin() +
                        " a été approuvée définitivement."
        );
    }

    private void notifierEmployeRejet(DemandeAbsence demande, String raisonRejet) {
        notificationService.envoyerNotificationDemande(
                demande.getEmployeDemandeur().getEmail(),
                "Demande d'absence rejetée",
                "Votre demande d'absence a été rejetée. Raison : " + raisonRejet
        );
    }

    private void notifierAutresValidateursRejet(DemandeAbsence demande) {
        for (HierarchieValidation.NiveauValidateur niveau : demande.getHierarchieValidation().getNiveaux()) {
            Employe validateur = employeRepository.findById(niveau.getEmployeSuperieurId()).orElse(null);
            if (validateur != null) {
                notificationService.envoyerNotificationDemande(
                        validateur.getEmail(),
                        "Demande d'absence rejetée",
                        "La demande d'absence de " + demande.getEmployeDemandeur().getNom()  + " " + demande.getEmployeDemandeur().getPrenom() + " a été rejetée."
                );
            }
        }
    }

    // ========================================
    // 3. CONFIRMATION DE DÉPART
    // ========================================

@Transactional(rollbackFor = {SoldeInsuffisantException.class, RuntimeException.class})
public ConfirmationResponseDTO confirmerDepart(ConfirmationDepartDTO dto, Long employeId) {

    // 1. Récupération et validations
    DemandeAbsence demande = demandeAbsenceRepository.findById(dto.getDemandeId())
            .orElseThrow(() -> new RuntimeException("Demande non trouvée: " + dto.getDemandeId()));

    if (!demande.getEmployeDemandeur().getId().equals(employeId)) {
        throw new RuntimeException("Vous n'êtes pas autorisé à confirmer ce départ");
    }

    if (demande.getStatut() != StatutDemandeAbsence.APPROUVE_FINAL) {
        throw new RuntimeException("La demande doit être approuvée avant confirmation. Statut actuel: " + demande.getStatut());
    }

    // 2. Récupération SuiviAbsence
    SuiviAbsence suivi = suiviAbsenceRepository.findByDemandeAbsenceId(demande.getId())
            .orElseThrow(() -> new RuntimeException("Suivi non trouvé pour la demande: " + demande.getId()));

    // 3. Déterminer la condition d'acceptation du dernier validateur
    GlobalEnums.ConditionAcceptationConge conditionAcceptation = recupererConditionAcceptation(demande);

    // 4. Mise à jour SuiviAbsence selon le type (toujours fait)
    LocalDateTime now = LocalDateTime.now();
    LocalDate today = LocalDate.now();
    mettreAJourSuiviAbsence(suivi, demande, today, now, dto.getCommentaire());

    // 5. 🆕 CONSOMMATION CONDITIONNELLE
    ResultatConsommationDTO resultatConsommation = null;
    BigDecimal joursADeduire = BigDecimal.ZERO;
    BigDecimal montantAllocation = BigDecimal.ZERO;
    boolean soldeDeduit = false;

    if (conditionAcceptation == GlobalEnums.ConditionAcceptationConge.A_DEDUIRE_DES_CONGES) {
        // 🆕 Vérification: pas déjà consommée
        if (Boolean.TRUE.equals(demande.getSoldeDeduit())) {
            throw new RuntimeException("Le solde a déjà été déduit pour cette demande le " + demande.getDateDeductionSolde());
        }

        // Calcul des jours à déduire
        joursADeduire = calculerJoursADeduire(demande);

        try {
            resultatConsommation = consommationCongeService.consommerProvisions(
                    employeId,
                    joursADeduire,
                    demande.getId()
            );

            montantAllocation = resultatConsommation.getMontantTotal();
            soldeDeduit = true;

        } catch (SoldeInsuffisantException e) {
            throw e; // Rollback automatique
        }

    } else if (conditionAcceptation == GlobalEnums.ConditionAcceptationConge.A_DEDUIRE_DU_SALAIRE_DE_PRESENCE) {
        // Pas de consommation de provisions
        // TODO: Logique de déduction salaire si nécessaire

    } else if (conditionAcceptation == GlobalEnums.ConditionAcceptationConge.SANS_CONDITION) {


    } else {

        // Par défaut: pas de consommation pour éviter les erreurs
    }

    // 6. Mise à jour DemandeAbsence
    demande.setDateDebutEffective(today);
    demande.setDateConfirmationDepartEffective(now);
    demande.setStatut(StatutDemandeAbsence.EN_COURS);

    // 🆕 Champs provision (uniquement si déduction)
    demande.setSoldeDeduit(soldeDeduit);
    if (soldeDeduit) {
        demande.setDateDeductionSolde(today);
        demande.setJoursEffectifsDeduits(joursADeduire);
        demande.setMontantAllocation(montantAllocation);
    }

    // Stocker la condition appliquée
    demande.setModeJouissanceEffectif(mapperConditionToEnum(conditionAcceptation));

    // 7. Sauvegarde
    suiviAbsenceRepository.save(suivi);
    demandeAbsenceRepository.save(demande);

    // 8. Notification
    notifierSuperieursDepartConfirme(demande);

    // 9. Construction réponse adaptée
    return construireReponse(demande, suivi, resultatConsommation, joursADeduire,
            montantAllocation, soldeDeduit, employeId, now);
}

// ============================================
// MÉTHODES EXTRAITES
// ============================================

    /**
     * Récupère la condition d'acceptation du dernier validateur (niveau le plus élevé)
     */
    private GlobalEnums.ConditionAcceptationConge recupererConditionAcceptation(DemandeAbsence demande) {

        // Option 1: Depuis la hiérarchie de validation JSON
        if (demande.getHierarchieValidation() != null
                && demande.getHierarchieValidation().getNiveaux() != null
                && !demande.getHierarchieValidation().getNiveaux().isEmpty()) {

            // Dernier niveau = niveau le plus élevé (dernier à avoir validé)
            List<HierarchieValidation.NiveauValidateur> niveaux = demande.getHierarchieValidation().getNiveaux();

            return niveaux.stream()
                    .filter(n -> "APPROUVE".equals(n.getStatut())) // Niveaux approuvés uniquement
                    .max(Comparator.comparing(HierarchieValidation.NiveauValidateur::getOrdre))
                    .map(HierarchieValidation.NiveauValidateur::getConditionAcceptationConge)
                    .orElse(GlobalEnums.ConditionAcceptationConge.SANS_CONDITION); // Défaut si non trouvé
        }

        // Option 2: Depuis les ValidationNiveau en base
        List<ValidationNiveau> validations = validationNiveauRepository
                .findByDemandeAbsenceIdAndStatutOrderByOrdreNiveauDesc(demande.getId(), "APPROUVE");

        if (!validations.isEmpty()) {
            ValidationNiveau derniereValidation = validations.get(0); // Premier = ordre le plus élevé

            // Convertir String vers Enum
            String conditionStr = derniereValidation.getConditionAcceptationConge();
            if (conditionStr != null) {
                try {
                    return GlobalEnums.ConditionAcceptationConge.valueOf(conditionStr);
                } catch (IllegalArgumentException e) {
                    log.warn("Condition invalide en base: {}", conditionStr);
                }
            }
        }

        // Option 3: Depuis le champ condition stocké dans DemandeAbsence (si tu l'as ajouté)
        // return demande.getConditionAcceptationFinale();

        // Défaut: sans condition (pas de déduction)
        return GlobalEnums.ConditionAcceptationConge.SANS_CONDITION;
    }

    /**
     * Met à jour le suivi d'absence selon l'unité
     */
    private void mettreAJourSuiviAbsence(SuiviAbsence suivi, DemandeAbsence demande,
                                         LocalDate today, LocalDateTime now, String commentaire) {

        switch (demande.getUniteAbsence()) {
            case HEURE -> {
                suivi.setHeureDepartEffective(LocalTime.now());
                suivi.setDateDepartEffective(today);
                if (suivi.getHeuresEffectifs() == null && demande.getNombreHeures() != null) {
                    suivi.setHeuresEffectifs(demande.getNombreHeures());
                }
            }
            case DEMI_JOURNEE -> {
                suivi.setHeureDepartEffective(null);
                suivi.setDateDepartEffective(today);
                suivi.setJoursEffectifs(demande.getNombreJours());
            }
            case JOURNEE_ENTIERE -> {
                suivi.setHeureDepartEffective(null);
                suivi.setDateDepartEffective(today);
                suivi.setJoursEffectifs(demande.getNombreJours());
            }
        }

        suivi.setDepartConfirme(true);
        suivi.setDateConfirmationDepart(now);
        suivi.setCommentaireDepart(commentaire);
    }

    /**
     * Mappe ConditionAcceptationConge vers ModeJouissanceConge
     */
    private GlobalEnums.ModeJouissanceConge mapperConditionToEnum(GlobalEnums.ConditionAcceptationConge condition) {
        return switch (condition) {
            case A_DEDUIRE_DES_CONGES -> GlobalEnums.ModeJouissanceConge.NUMERAIRE;
            case A_DEDUIRE_DU_SALAIRE_DE_PRESENCE -> GlobalEnums.ModeJouissanceConge.NUMERAIRE;
            case SANS_CONDITION -> GlobalEnums.ModeJouissanceConge.REEL;
        };
    }

    /**
     * Construit la réponse adaptée selon le type de déduction
     */
    private ConfirmationResponseDTO construireReponse(DemandeAbsence demande, SuiviAbsence suivi,
                                                      ResultatConsommationDTO resultatConsommation,
                                                      BigDecimal joursADeduire, BigDecimal montantAllocation,
                                                      boolean soldeDeduit, Long employeId, LocalDateTime now) {

        ConfirmationResponseDTO.ConfirmationResponseDTOBuilder builder = ConfirmationResponseDTO.builder()
                .demandeId(demande.getId())
                .nouveauStatut(demande.getStatut().name())
                .dateDebutEffective(demande.getDateDebutEffective())
                .heureDepartEffective(
                        demande.getUniteAbsence() == UniteAbsence.HEURE
                                ? suivi.getHeureDepartEffective().toString()
                                : null
                )
//                .conditionAcceptation(demande.getModeJouissanceEffectif().name())
                .timestamp(now);

        if (soldeDeduit && resultatConsommation != null) {
            // Cas avec déduction de congés
            builder
                    .joursDeduits(joursADeduire)
                    .montantAllocation(montantAllocation)
                    .repartition(resultatConsommation.getDetailsParMois())
                    .soldeJoursRestant(
                            soldeCongeService.getSoldeActuel(employeId).getSoldeJoursDisponibles()
                    )
                    .soldeMontantEstime(
                            soldeCongeService.getSoldeActuel(employeId).getValeurEstimeeSolde()
                    )
                    .messageConfirmation("Départ confirmé. Votre congé a été déduit de vos provisions.")
                    .messageDetailleRH(resultatConsommation.getMessage());

        } else {
            // Cas sans déduction
            builder
                    .joursDeduits(BigDecimal.ZERO)
                    .montantAllocation(BigDecimal.ZERO)
                    .repartition(List.of())
                    .soldeJoursRestant(null) //  Ou récupérer quand même si tu veux l'info
                    .soldeMontantEstime(null)
                    .messageConfirmation("Départ confirmé. Absence sans déduction de congés.")
                    .messageDetailleRH("Condition: " + demande.getModeJouissanceEffectif());
        }

        return builder.build();
    }

    /**
     * Calcule les jours à déduire selon l'unité d'absence
     */
    private BigDecimal calculerJoursADeduire(DemandeAbsence demande) {
        return switch (demande.getUniteAbsence()) {
            case HEURE -> {
                // Conversion heures → jours selon config entreprise
                Integer heures = demande.getNombreHeures();
                if (heures == null) {
                    throw new RuntimeException("Nombre d'heures non renseigné pour absence horaire");
                }

                Double heuresParJour = demande.getContratEmploye().getCompany().getHeuresParJour();
                 if (heuresParJour == null || heuresParJour == 0) {
                    heuresParJour = 8.0; // défaut
                }

                yield BigDecimal.valueOf(heures)
                        .divide(BigDecimal.valueOf(heuresParJour), 2, RoundingMode.HALF_UP);
            }

            case DEMI_JOURNEE -> {
                // 1 demi-journée = 0.5 jour (à adapter selon ta convention)
                Integer jours = demande.getNombreJours();
                if (jours == null) {
                    throw new RuntimeException("Nombre de jours non renseigné");
                }
                yield BigDecimal.valueOf(jours).multiply(new BigDecimal("0.5"));
            }

            case JOURNEE_ENTIERE -> {
                Integer jours = demande.getNombreJours();
                if (jours == null) {
                    throw new RuntimeException("Nombre de jours non renseigné");
                }
                yield BigDecimal.valueOf(jours);
            }
        };
    }
    private void notifierSuperieursDepartConfirme(DemandeAbsence demande) {
        for (HierarchieValidation.NiveauValidateur niveau : demande.getHierarchieValidation().getNiveaux()) {
            Employe validateur = employeRepository.findById(niveau.getEmployeSuperieurId()).orElse(null);
            if (validateur != null) {
                notificationService.envoyerNotificationDemande(
                        validateur.getEmail(),
                        "Départ confirmé",
                        demande.getEmployeDemandeur().getNom() + " a confirmé son départ en congé le " +
                                demande.getDateDebutEffective()
                );
            }
        }
    }

    // ========================================
    // 4. CONFIRMATION DE RETOUR
    // ========================================

    @Transactional
    public void confirmerRetour(ConfirmationRetourDTO dto, Long employeId) {
        DemandeAbsence demande = demandeAbsenceRepository.findById(dto.getDemandeId())
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        if (!demande.getEmployeDemandeur().getId().equals(employeId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à confirmer ce retour");
        }

        SuiviAbsence suivi = suiviAbsenceRepository.findByDemandeAbsenceId(demande.getId())
                .orElseThrow(() -> new RuntimeException("Suivi non trouvé"));

        if (!suivi.getDepartConfirme()) {
            throw new RuntimeException("Le départ doit d'abord être confirmé");
        }

        if (suivi.getRetourConfirme()) {
            throw new RuntimeException("Le retour a déjà été confirmé");
        }

        Long companyId = demande.getEmployeDemandeur().getCompany().getId();

        // 🆕 CALCUL EFFECTIF SELON LE TYPE
        switch (demande.getUniteAbsence()) {
            case HEURE -> {
                suivi.setHeureArriveeEffective(LocalTime.now());
                suivi.setDateRetourEffective(LocalDate.now());

                // 🆕 Calcul heures effectives : début premier jour → fin dernier jour
                int heuresEffectives = calculerHeuresEffectives(
                        suivi.getDateDepartEffective(),
                        suivi.getHeureDepartEffective(),
                        LocalDate.now(),
                        LocalTime.now(),
                        demande.getHeureDepart(),
                        demande.getHeureArrivee()
                );

                suivi.setHeuresEffectifs(heuresEffectives);
                suivi.setEcartHeures(suivi.getHeuresPrevu() - heuresEffectives);

                // Conversion en jours pour cohérence
                double joursOuvresEquivalent = heuresEffectives / 8.0;
                suivi.setJoursEffectifs((int) Math.round(arrondir(joursOuvresEquivalent, 2)));

                // Calcul des jours calendaires effectifs
                long joursCalendairesEffectifs = ChronoUnit.DAYS.between(
                        suivi.getDateDepartEffective(),
                        LocalDate.now()
                ) + 1;
                suivi.setJoursCalendairesEffectifs((int) joursCalendairesEffectifs);

                log.info("Retour HEURE confirmé: {} heures effectives sur {} jours calendaires = {} jours ouvrés",
                        heuresEffectives, joursCalendairesEffectifs, joursOuvresEquivalent);
            }

            case DEMI_JOURNEE -> {
                suivi.setDateRetourEffective(LocalDate.now());
                suivi.setHeureArriveeEffective(null);

                // Calcul des demi-journées effectives
                long joursCalendairesEffectifs = ChronoUnit.DAYS.between(
                        suivi.getDateDepartEffective(),
                        LocalDate.now()
                ) + 1;

                // Vérifier les fériés dans la période effective
                Set<LocalDate> feriesEffectifs = getJoursFeriesPeriode(
                        companyId,
                        suivi.getDateDepartEffective(),
                        LocalDate.now()
                );
                long joursTravaillesEffectifs = joursCalendairesEffectifs - feriesEffectifs.size();

                double joursOuvresEffectifs = joursTravaillesEffectifs * 0.5;


                suivi.setJoursCalendairesEffectifs((int) joursCalendairesEffectifs);
                suivi.setJoursFeriesEffectifs((int) feriesEffectifs.size());
                suivi.setEcartJours((int) Math.round(arrondir(suivi.getJoursPrevu() - joursOuvresEffectifs, 2)));
                suivi.setJoursEffectifs((int) Math.round(arrondir(joursOuvresEffectifs, 2)));


                log.info("Retour DEMI_JOURNEE confirmé: {} demi-journées = {} jours ouvrés, {} fériés exclus",
                        joursTravaillesEffectifs, joursOuvresEffectifs, feriesEffectifs.size());
            }

            case JOURNEE_ENTIERE -> {
                suivi.setDateRetourEffective(LocalDate.now());
                suivi.setHeureArriveeEffective(null);

                // Calcul précis avec jours fériés
                CalculJoursAbsenceService.CalculJoursAbsenceResultat calcul =
                        calculJoursAbsenceService.calculerJoursAbsenceEffectifs(
                                companyId,
                                suivi.getDateDepartEffective(),
                                LocalDate.now()
                        );

                suivi.setJoursEffectifs(calcul.getJoursAbsenceReels());
                suivi.setJoursCalendairesEffectifs(calcul.getJoursCalendaires());
                suivi.setJoursFeriesEffectifs(calcul.getJoursFeriesTravailles());
                suivi.setEcartJours(suivi.getJoursPrevu() - calcul.getJoursAbsenceReels());

                demande.setNombreJoursEffectifs(calcul.getJoursAbsenceReels());

                log.info("Retour JOURNEE_ENTIERE confirmé: {} jours ouvrés ({} calendaires, {} fériés)",
                        calcul.getJoursAbsenceReels(),
                        calcul.getJoursCalendaires(),
                        calcul.getJoursFeriesTravailles());
            }
        }

        suivi.setRetourConfirme(true);
        suivi.setDateConfirmationRetour(LocalDateTime.now());
        suivi.setCommentaireRetour(dto.getCommentaire());

        demande.setDateFinEffective(LocalDate.now());
        demande.setStatut(StatutDemandeAbsence.TERMINEE);

        suiviAbsenceRepository.save(suivi);
        demandeAbsenceRepository.save(demande);
        // 🆕 APPEL AU SERVICE SOLDE CONGÉ SI CONDITIONS RÉUNIES
//        traiterSoldeCongeSiApplicable(demande, suivi);
        notifierSupérieursRetourConfirme(demande, suivi);
    }

    /**
     * 🆕 Calcule les heures effectives totales pour une absence horaire sur plusieurs jours
     * Logique : début premier jour → fin dernier jour
     */
    private int calculerHeuresEffectives(
            LocalDate dateDepart,
            LocalTime heureDepart,
            LocalDate dateRetour,
            LocalTime heureRetour,
            LocalTime heureDebutPrevu,
            LocalTime heureFinPrevu) {

        // Même jour
        if (dateDepart.equals(dateRetour)) {
            return (int) ChronoUnit.HOURS.between(heureDepart, heureRetour);
        }

        // Calcul par jour
        long nbJoursTotal = ChronoUnit.DAYS.between(dateDepart, dateRetour) + 1;
        int heuresParJour = (int) ChronoUnit.HOURS.between(heureDebutPrevu, heureFinPrevu);

        // Premier jour (partiel : heure départ effective → fin prévue)
        int heuresPremierJour = (int) ChronoUnit.HOURS.between(heureDepart, heureFinPrevu);

        // Dernier jour (partiel : début prévu → heure retour effective)
        int heuresDernierJour = (int) ChronoUnit.HOURS.between(heureDebutPrevu, heureRetour);

        // Jours intermédiaires (pleins)
        long nbJoursIntermediaires = nbJoursTotal - 2;
        int heuresIntermediaires = (int) (nbJoursIntermediaires * heuresParJour);

        return heuresPremierJour + heuresIntermediaires + heuresDernierJour;
    }

    private void notifierSupérieursRetourConfirme(DemandeAbsence demande, SuiviAbsence suivi) {
        for (HierarchieValidation.NiveauValidateur niveau : demande.getHierarchieValidation().getNiveaux()) {
            Employe validateur = employeRepository.findById(niveau.getEmployeSuperieurId()).orElse(null);
            if (validateur != null) {
                String message = demande.getEmployeDemandeur().getNom() + " a confirmé son retour le " +
                        demande.getDateFinEffective();

                if (suivi.getHeuresEffectifs() != null) {
                    message += ". Heures effectives : " + suivi.getHeuresEffectifs() +
                            " (Prévu : " + suivi.getHeuresPrevu() + ")";
                } else {
                    message += ". Jours effectifs : " + suivi.getJoursEffectifs() +
                            " (Prévu : " + suivi.getJoursPrevu() + ")";
                }

                notificationService.envoyerNotificationDemande(
                        validateur.getEmail(),
                        "Retour confirmé",
                        message
                );
            }
        }
    }

    // ========================================
    // 5. REPORTER UNE DEMANDE
    // ========================================

    @Transactional
    public void reporterDemande(ReportAbsenceDTO dto, Long employeId) {
        DemandeAbsence demande = demandeAbsenceRepository.findById(dto.getDemandeId())
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        if (!demande.getEmployeDemandeur().getId().equals(employeId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à reporter cette demande");
        }

        if (demande.getStatut() != StatutDemandeAbsence.APPROUVE_FINAL) {
            throw new RuntimeException("Seules les demandes approuvées peuvent être reportées");
        }

        SuiviAbsence suivi = suiviAbsenceRepository.findByDemandeAbsenceId(demande.getId())
                .orElseThrow(() -> new RuntimeException("Suivi non trouvé"));

        if (suivi.getDepartConfirme()) {
            throw new RuntimeException("Impossible de reporter une absence déjà commencée");
        }

        // 🆕 Recalcul avec les nouvelles dates
        Long companyId = demande.getEmployeDemandeur().getCompany().getId();
        long nbJoursCalendaires = ChronoUnit.DAYS.between(dto.getNouvelleDateDebut(), dto.getNouvelleDateFin()) + 1;
        Set<LocalDate> datesFeries = getJoursFeriesPeriode(companyId, dto.getNouvelleDateDebut(), dto.getNouvelleDateFin());
        long nbJoursTravailles = nbJoursCalendaires - datesFeries.size();

        switch (dto.getUniteAbsence()) {
            case HEURE -> {
                if (dto.getNouvelleHeureDepart() == null || dto.getNouvelleHeureArrivee() == null) {
                    throw new RuntimeException("Les heures de début et de fin sont obligatoires pour une absence horaire");
                }
                if (dto.getNouvelleHeureArrivee().isBefore(dto.getNouvelleHeureDepart())) {
                    throw new RuntimeException("L'heure de fin ne peut pas être avant l'heure de début");
                }

                int heuresParJour = (int) ChronoUnit.HOURS.between(dto.getNouvelleHeureDepart(), dto.getNouvelleHeureArrivee());
                int totalHeures = (int) (nbJoursTravailles * heuresParJour);
                double joursOuvres = totalHeures / 8.0;

                demande.setHeureDepart(dto.getNouvelleHeureDepart());
                demande.setHeureArrivee(dto.getNouvelleHeureArrivee());
                demande.setNombreHeures(heuresParJour);
                demande.setNombreJours((int) Math.round(arrondir(joursOuvres, 2)));
            }

            case DEMI_JOURNEE -> {
                double totalJoursOuvres = nbJoursTravailles * 0.5;
                demande.setNombreJours((int) Math.round(arrondir(totalJoursOuvres, 2)));
                demande.setNombreHeures(4);
            }

            case JOURNEE_ENTIERE -> {
                CalculJoursAbsenceService.CalculJoursAbsenceResultat calcul =
                        calculJoursAbsenceService.calculerJoursAbsenceReels(
                                companyId,
                                dto.getNouvelleDateDebut(),
                                dto.getNouvelleDateFin()
                        );
                demande.setNombreJours(calcul.getJoursAbsenceReels());
                demande.setNombreHeures(null);
            }
        }

        demande.setDateDebut(dto.getNouvelleDateDebut());
        demande.setDateFin(dto.getNouvelleDateFin());
        demande.setEstReporte(true);
        demande.setMotifReport(dto.getMotifReport());
        demande.setDateReport(LocalDateTime.now());
        demande.setStatut(StatutDemandeAbsence.REPORTEE);

        suivi.setDateDepartPrevue(dto.getNouvelleDateDebut());
        suivi.setDateRetourPrevue(dto.getNouvelleDateFin());
        suivi.setJoursPrevu(demande.getNombreJours());
        suivi.setHeuresPrevu(calculerHeuresPrevues(demande, nbJoursTravailles));
        suivi.setAEteReporte(true);
        suivi.setMotifReport(dto.getMotifReport());
        suivi.setDateDemandeReport(LocalDateTime.now());

        demandeAbsenceRepository.save(demande);
        suiviAbsenceRepository.save(suivi);

        notifierSupérieursReport(demande);
    }

    private void notifierSupérieursReport(DemandeAbsence demande) {
        for (HierarchieValidation.NiveauValidateur niveau : demande.getHierarchieValidation().getNiveaux()) {
            Employe validateur = employeRepository.findById(niveau.getEmployeSuperieurId()).orElse(null);
            if (validateur != null) {
                String message = demande.getEmployeDemandeur().getNom() + " a reporté son congé. " +
                        "Nouvelles dates : du " + demande.getDateDebut() + " au " + demande.getDateFin() +
                        ". Motif : " + demande.getMotifReport();

                notificationService.envoyerNotificationDemande(
                        validateur.getEmail(),
                        "Congé reporté",
                        message
                );
            }
        }
    }

    // ========================================
    // 6. RÉCUPÉRATION
    // ========================================

    @Transactional(readOnly = true)
    public DemandeAbsence getDemandeById(Long id) {
        return demandeAbsenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));
    }

    @Transactional(readOnly = true)
    public SuiviAbsence getSuiviByDemandeId(Long demandeId) {
        return suiviAbsenceRepository.findByDemandeAbsenceId(demandeId)
                .orElseThrow(() -> new RuntimeException("Suivi non trouvé"));
    }

    @Transactional(readOnly = true)
    public List<DemandeAbsence> getDemandesEnAttenteValidationPourEmploye(Long employeId) {
        return demandeAbsenceRepository.findAll()
                .stream()
                .filter(d -> {
                    if (d.getNiveauValidationEnCours() == null) return false;

                    HierarchieValidation.NiveauValidateur niveauActuel = d.getHierarchieValidation()
                            .getNiveaux()
                            .stream()
                            .filter(n -> n.getOrdre().equals(d.getNiveauValidationEnCours()))
                            .findFirst()
                            .orElse(null);

                    return niveauActuel != null &&
                            niveauActuel.getEmployeSuperieurId().equals(employeId);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DemandeAbsence> getMesDemandes(Long employeId) {
        return demandeAbsenceRepository.findByEmployeDemandeurIdOrderByDateCreationDesc(employeId);
    }

    @Transactional(readOnly = true)
    public List<DemandeAbsence> getDemandesByCompany(Long companyId) {
        return demandeAbsenceRepository.findByCompanyIdOrderByDateCreationDesc(companyId);
    }

    @Transactional
    public DemandeAbsence annulerDemande(Long demandeId) {
        User currentUser = userService.getCurrentUser();

        DemandeAbsence demande = demandeAbsenceRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        if (!demande.getEmployeDemandeur().getId().equals(currentUser.getEmploye().getId())) {
            throw new RuntimeException("Vous n'êtes pas autorisé à annuler cette demande");
        }

        if (demande.getStatut() == StatutDemandeAbsence.REJETE ||
                demande.getStatut() == StatutDemandeAbsence.ANNULE ||
                demande.getStatut() == StatutDemandeAbsence.TERMINEE) {
            throw new RuntimeException("Cette demande ne peut pas être annulée");
        }

        demande.setStatut(StatutDemandeAbsence.ANNULE);
        demande.setDateAnnulation(LocalDateTime.now());

        return demandeAbsenceRepository.save(demande);
    }

    // ========================================
    // 7. HISTORIQUE VALIDATIONS
    // ========================================

    @Transactional(readOnly = true)
    public List<DemandeValidationEnAttenteDTO> getHistoriqueValidations(Long employeIdConnecte) {
        if (!employeRepository.existsById(employeIdConnecte)) {
            throw new RuntimeException("Employé non trouvé");
        }

        List<ValidationNiveau> validations = validationNiveauRepository
                .findHistoriqueByValidateur(employeIdConnecte);

        return validations.stream()
                .map(validation -> {
                    try {
                        return construireDTOValidation(validation, employeIdConnecte);
                    } catch (Exception e) {
                        log.error("Erreur traitement validation ID {}: {}", validation.getId(), e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator
                        .comparing(DemandeValidationEnAttenteDTO::isEstEnAttenteDeMoi, Comparator.reverseOrder())
                        .thenComparing(DemandeValidationEnAttenteDTO::getDateCreationValidation, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }
    private DemandeValidationEnAttenteDTO construireDTOValidation(
            ValidationNiveau validation,
            Long employeIdConnecte) {

        DemandeAbsence demande = validation.getDemandeAbsence();
        HierarchieValidation hierarchie = demande.getHierarchieValidation();

        if (hierarchie == null) return null;

        List<HierarchieValidation.NiveauValidateur> niveaux = hierarchie.getNiveaux();

        // 🔥 Récupération des validations réelles en base
        List<ValidationNiveau> validationsReelles =
                validationNiveauRepository
                        .findByDemandeAbsenceIdOrderByOrdreNiveauAsc(demande.getId());

        // 🔥 Dernière validation effectuée (APPROUVE ou REJETE)
        ValidationNiveau derniereValidation = validationsReelles.stream()
                .filter(v -> !"EN_ATTENTE".equals(v.getStatut()))
                .reduce((first, second) -> second)
                .orElse(null);

        // 🔥 Déterminer validateur actuel réel
        String nomValidateurActuel = derniereValidation != null
                ? derniereValidation.getValidateur().getNom() + " " + derniereValidation.getValidateur().getPrenom()
                : "N/A";

        String fonctionValidateurActuel = derniereValidation != null
                ? derniereValidation.getFonctionValidateur()
                : "N/A";

        Long idValidateurActuel = derniereValidation != null
                ? derniereValidation.getValidateur().getId()
                : null;

        boolean aDejaValide = derniereValidation != null &&
                derniereValidation.getValidateur().getId().equals(employeIdConnecte);

        String monStatut = aDejaValide
                ? derniereValidation.getStatut()
                : null;

        boolean estMonTour = validation.getValidateur().getId().equals(employeIdConnecte)
                && "EN_ATTENTE".equals(validation.getStatut());

        boolean estEnAttenteDeMoi = estMonTour;
        boolean estTermineePourMoi = aDejaValide;

        boolean estRecent = validation.getCreatedAt() != null &&
                validation.getCreatedAt().isAfter(LocalDateTime.now().minusDays(7));

        String categorie = determinerCategorie(
                estMonTour,
                aDejaValide,
                demande.getStatut(),
                monStatut
        );

        String messageAction = construireMessageAction(
                estMonTour,
                aDejaValide,
                null,
                demande.getStatut(),
                monStatut
        );

        return DemandeValidationEnAttenteDTO.builder()
                .demandeId(demande.getId())
                .nomDemandeur(demande.getEmployeDemandeur().getNom())
                .prenomDemandeur(demande.getEmployeDemandeur().getPrenom())
                .matriculeDemandeur(demande.getEmployeDemandeur().getMatricule())
                .typeAbsence(demande.getTypeAbsence() != null
                        ? demande.getTypeAbsence().getLibelle()
                        : "N/A")
                .motifDemande(demande.getMotifDemande())
                .dateDebut(demande.getDateDebut())
                .dateFin(demande.getDateFin())
                .uniteAbsence(demande.getUniteAbsence())
                .nombreJours(demande.getNombreJours())
                .nombreHeures(demande.getNombreHeures())
                .statutDemande(demande.getStatut())
                .dateCreationDemande(demande.getCreatedAt())
                .totalNiveaux(niveaux.size())

                // 🔥 validateur réel
                .nomValidateurActuel(nomValidateurActuel)
                .fonctionValidateurActuel(fonctionValidateurActuel)
                .idValidateurActuel(idValidateurActuel)

                .statutNiveauActuel(validation.getStatut())
                .dateCreationValidation(validation.getCreatedAt())

                .estMonTourDeValider(estMonTour)
                .aDejaValide(aDejaValide)
                .monStatutValidation(monStatut)
                .maDateValidation(aDejaValide ? derniereValidation.getDateAction() : null)
                .monCommentaire(aDejaValide ? derniereValidation.getCommentaire() : null)

                .estEnAttenteDeMoi(estEnAttenteDeMoi)
                .estTermineePourMoi(estTermineePourMoi)
                .categorie(categorie)
                .messageAction(messageAction)
                .estRecent(estRecent)

                // 🔥 historique reconstruit proprement
                .historiqueValidations(construireHistorique(validationsReelles))

                // 🔥 plus aucun prochain validateur si rejet
                .prochainsValidateurs(
                        demande.getStatut() == StatutDemandeAbsence.REJETE
                                ? Collections.emptyList()
                                : construireProchainsValidateurs(niveaux, demande.getNiveauValidationEnCours())
                )

                .build();
    }


//    private DemandeValidationEnAttenteDTO construireDTOValidation(ValidationNiveau validation, Long employeIdConnecte) {
//        DemandeAbsence demande = validation.getDemandeAbsence();
//        HierarchieValidation hierarchie = demande.getHierarchieValidation();
//
//        if (hierarchie == null) return null;
//
//        Integer niveauActuel = demande.getNiveauValidationEnCours();
//        List<HierarchieValidation.NiveauValidateur> niveaux = hierarchie.getNiveaux();
//
//        HierarchieValidation.NiveauValidateur niveauEnCours = niveaux.stream()
//                .filter(n -> n.getOrdre().equals(niveauActuel))
//                .findFirst()
//                .orElse(null);
//
//        boolean estMonTour = niveauEnCours != null &&
//                niveauEnCours.getEmployeSuperieurId().equals(employeIdConnecte);
//
//        boolean aDejaValide = !"EN_ATTENTE".equals(validation.getStatut());
//        String monStatut = aDejaValide ? validation.getStatut() : null;
//
//        String categorie = determinerCategorie(estMonTour, aDejaValide, demande.getStatut(), monStatut);
//
//        boolean estEnAttenteDeMoi = estMonTour && !aDejaValide;
//        boolean estTermineePourMoi = aDejaValide;
//
//        boolean estRecent = validation.getCreatedAt() != null &&
//                validation.getCreatedAt().isAfter(LocalDateTime.now().minusDays(7));
//
//        String messageAction = construireMessageAction(estMonTour, aDejaValide, niveauEnCours, demande.getStatut(), monStatut);
//
//        String conditionAcceptation = null;
//        String conditionLibelle = null;
//        boolean estValidationFinale = false;
//
//        if (demande.getStatut() == StatutDemandeAbsence.APPROUVE_FINAL) {
//            Optional<ValidationNiveau> derniereValidation = validationNiveauRepository
//                    .findByDemandeAbsenceIdAndOrdreNiveau(demande.getId(), hierarchie.getNiveaux().size());
//
//            if (derniereValidation.isPresent()) {
//                ValidationNiveau dernier = derniereValidation.get();
//                if (dernier.getConditionAcceptationConge() != null && !dernier.getConditionAcceptationConge().isEmpty()) {
//                    conditionAcceptation = dernier.getConditionAcceptationConge();
//                    try {
//                        GlobalEnums.ConditionAcceptationConge enumValue =
//                                GlobalEnums.ConditionAcceptationConge.valueOf(dernier.getConditionAcceptationConge());
//                        conditionLibelle = getLibelleCondition(enumValue);
//                    } catch (IllegalArgumentException e) {
//                        conditionLibelle = null;
//                    }
//                }
//                estValidationFinale = validation.getOrdreNiveau().equals(hierarchie.getNiveaux().size());
//            }
//        }
//
//        ValidationNiveau validationRejet = null;
//
//        if (demande.getStatut() == StatutDemandeAbsence.REJETE) {
//            validationRejet = validationNiveauRepository
//                    .findByDemandeAbsenceId(demande.getId())
//                    .stream()
//                    .filter(v -> "REJETE".equals(v.getStatut()))
//                    .findFirst()
//                    .orElse(null);
//        }
//
//
//        return DemandeValidationEnAttenteDTO.builder()
//                .demandeId(demande.getId())
//                .nomDemandeur(demande.getEmployeDemandeur().getNom())
//                .prenomDemandeur(demande.getEmployeDemandeur().getPrenom())
//                .matriculeDemandeur(demande.getEmployeDemandeur().getMatricule())
//                .typeAbsence(demande.getTypeAbsence() != null ? demande.getTypeAbsence().getLibelle() : "N/A")
//                .motifDemande(demande.getMotifDemande())
//                .dateDebut(demande.getDateDebut())
//                .dateFin(demande.getDateFin())
//                .uniteAbsence(demande.getUniteAbsence())
//                .nombreJours(demande.getNombreJours())
//                .nombreHeures(demande.getNombreHeures())
//                .statutDemande(demande.getStatut())
//
//                .dateCreationDemande(demande.getCreatedAt())
//                .niveauValidationActuel(niveauActuel)
//                .totalNiveaux(niveaux.size())
//                .statutNiveauActuel(validation.getStatut())
//                .dateCreationValidation(validation.getCreatedAt())
//                .nomValidateurActuel(niveauEnCours != null ? niveauEnCours.getNomComplet() : "N/A")
//                .fonctionValidateurActuel(niveauEnCours != null ? niveauEnCours.getFonction() : "N/A")
//                .idValidateurActuel(niveauEnCours != null ? niveauEnCours.getEmployeSuperieurId() : null)
//                .estMonTourDeValider(estMonTour)
//                .aDejaValide(aDejaValide)
//                .monStatutValidation(monStatut)
//                .maDateValidation(aDejaValide ? validation.getDateAction() : null)
//                .monCommentaire(aDejaValide ? validation.getCommentaire() : null)
//                .estEnAttenteDeMoi(estEnAttenteDeMoi)
//                .estTermineePourMoi(estTermineePourMoi)
//                .categorie(categorie)
//                .messageAction(messageAction)
//                .estRecent(estRecent)
////                .historiqueValidations(construireHistorique(niveaux, niveauActuel))
//                .historiqueValidations(construireHistorique(niveaux, niveauActuel, demande))
//                .prochainsValidateurs(construireProchainsValidateurs(niveaux, niveauActuel))
//                .conditionAcceptation(conditionAcceptation)
//                .conditionAcceptationLibelle(conditionLibelle)
//                .estValidationFinale(estValidationFinale)
//                .build();
//    }

    private String getLibelleCondition(GlobalEnums.ConditionAcceptationConge condition) {
        if (condition == null) return null;
        return switch (condition) {
            case A_DEDUIRE_DES_CONGES -> "À déduire des congés";
            case A_DEDUIRE_DU_SALAIRE_DE_PRESENCE -> "À déduire du salaire de présence";
            case SANS_CONDITION -> "Sans condition";
            default -> condition.name();
        };
    }

    private String determinerCategorie(boolean estMonTour, boolean aDejaValide,
                                       StatutDemandeAbsence statutDemande, String monStatut) {
        if (statutDemande == StatutDemandeAbsence.REJETE) {
            return aDejaValide ? "REJETEE_PAR_MOI" : "REJETEE_AUTRE";
        }
        if (statutDemande == StatutDemandeAbsence.APPROUVE_FINAL) {
            return aDejaValide ? "APPROUVEE_PAR_MOI" : "APPROUVEE_AUTRE";
        }
        if (estMonTour && !aDejaValide) {
            return "A_VALIDER";
        }
        if (aDejaValide && "APPROUVE".equals(monStatut)) {
            return "VALIDE_PAR_MOI";
        }
        if (aDejaValide && "REJETE".equals(monStatut)) {
            return "REJETE_PAR_MOI";
        }
        return "EN_ATTENTE_AUTRE";
    }

    private String construireMessageAction(boolean estMonTour, boolean aDejaValide,
                                           HierarchieValidation.NiveauValidateur niveauEnCours,
                                           StatutDemandeAbsence statutDemande, String monStatut) {
        if (aDejaValide) {
            if ("APPROUVE".equals(monStatut)) return "✅ Vous avez approuvé cette demande";
            if ("REJETE".equals(monStatut)) return "❌ Vous avez rejeté cette demande";
        }
        if (estMonTour && !aDejaValide) return "🔴 C'EST VOTRE TOUR - À valider";
        if (statutDemande == StatutDemandeAbsence.REJETE) return "❌ Demande rejetée";
        if (statutDemande == StatutDemandeAbsence.APPROUVE_FINAL) return "✅ Demande approuvée définitivement";
        if (statutDemande == StatutDemandeAbsence.EN_COURS) return "🟢 Congé en cours";
        if (statutDemande == StatutDemandeAbsence.TERMINEE) return "🏁 Congé terminé";
        if (niveauEnCours != null) return "⏳ En attente de " + niveauEnCours.getNomComplet();
        return "Statut inconnu";
    }

//    private List<DemandeValidationEnAttenteDTO.HistoriqueValidationDTO> construireHistorique(
//            List<HierarchieValidation.NiveauValidateur> niveaux, Integer niveauActuel) {
//        if (niveauActuel == null) return Collections.emptyList();
//        return niveaux.stream()
//                .filter(n -> n.getOrdre() < niveauActuel)
//                .map(n -> DemandeValidationEnAttenteDTO.HistoriqueValidationDTO.builder()
//                        .ordre(n.getOrdre())
//                        .nomValidateur(n.getNomComplet())
//                        .fonctionValidateur(n.getFonction())
//                        .statut(n.getStatut())
//                        .dateValidation(parseDate(n.getDateAction()))
//                        .commentaire(n.getCommentaire())
//                        .build())
//                .collect(Collectors.toList());
//    }

//        private List<DemandeValidationEnAttenteDTO.ProchainValidateurDTO> construireProchainsValidateurs(
//                List<HierarchieValidation.NiveauValidateur> niveaux, Integer niveauActuel) {
//            if (niveauActuel == null) return Collections.emptyList();
//            return niveaux.stream()
//                    .filter(n -> n.getOrdre() > niveauActuel)
//                    .map(n -> DemandeValidationEnAttenteDTO.ProchainValidateurDTO.builder()
//                            .ordre(n.getOrdre())
//                            .nomValidateur(n.getNomComplet())
//                            .fonctionValidateur(n.getFonction())
//                            .statut(n.getStatut())
//                            .build())
//                    .collect(Collectors.toList());
//        }
//
//
//private List<DemandeValidationEnAttenteDTO.HistoriqueValidationDTO> construireHistorique(
//        List<HierarchieValidation.NiveauValidateur> niveaux,
//        Integer niveauActuel,
//        DemandeAbsence demande) {
//
//    if (niveauActuel == null) return Collections.emptyList();
//
//    boolean estRejet = demande.getStatut() == StatutDemandeAbsence.REJETE;
//
//    return niveaux.stream()
//            .filter(n -> {
//                if (estRejet) {
//                    // ✅ inclure aussi le niveau actuel si rejet
//                    return n.getOrdre() <= niveauActuel;
//                }
//                return n.getOrdre() < niveauActuel;
//            })
//            .map(n -> DemandeValidationEnAttenteDTO.HistoriqueValidationDTO.builder()
//                    .ordre(n.getOrdre())
//                    .nomValidateur(n.getNomComplet())
//                    .fonctionValidateur(n.getFonction())
//                    .statut(n.getStatut())
//                    .dateValidation(parseDate(n.getDateAction()))
//                    .commentaire(n.getCommentaire())
//                    .raisonRejet(
//                            "REJETE".equals(n.getStatut()) ? n.getRaisonRejet() : null
//                    )
//                    .build())
//            .collect(Collectors.toList());
//}

    private List<DemandeValidationEnAttenteDTO.HistoriqueValidationDTO>
    construireHistorique(List<ValidationNiveau> validations) {

        return validations.stream()
                .filter(v -> !"EN_ATTENTE".equals(v.getStatut()))
                .map(v -> DemandeValidationEnAttenteDTO.HistoriqueValidationDTO.builder()
                        .ordre(v.getOrdreNiveau())
                        .nomValidateur(v.getValidateur().getNom() + " " + v.getValidateur().getPrenom())
                        .fonctionValidateur(v.getFonctionValidateur())
                        .statut(v.getStatut())
                        .dateValidation(v.getDateAction())
                        .commentaire(v.getCommentaire())
                        .raisonRejet(
                                "REJETE".equals(v.getStatut())
                                        ? v.getRaisonRejet()
                                        : null
                        )
                        .build())
                .collect(Collectors.toList());
    }

    private List<DemandeValidationEnAttenteDTO.ProchainValidateurDTO> construireProchainsValidateurs(
            List<HierarchieValidation.NiveauValidateur> niveaux,
            Integer niveauActuel) {

        if (niveauActuel == null) return Collections.emptyList();

        return niveaux.stream()
                .filter(n -> n.getOrdre() >= niveauActuel) // ✅ inclut le niveau actuel
                .map(n -> DemandeValidationEnAttenteDTO.ProchainValidateurDTO.builder()
                        .ordre(n.getOrdre())
                        .nomValidateur(n.getNomComplet())
                        .fonctionValidateur(n.getFonction())
                        .statut(n.getStatut())
                        .build())
                .collect(Collectors.toList());
    }

    private LocalDateTime parseDate(String dateStr) {
        if (dateStr == null) return null;
        try {
            return LocalDateTime.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    private double arrondir(double valeur, int decimals) {
        double factor = Math.pow(10, decimals);
        return Math.round(valeur * factor) / factor;
    }
}