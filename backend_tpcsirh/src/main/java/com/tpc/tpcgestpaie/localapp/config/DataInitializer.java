package com.tpc.tpcgestpaie.localapp.config;

import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.model.numerisation.parcours.EventCategory;
import com.tpc.tpcgestpaie.localapp.repository.*;
import com.tpc.tpcgestpaie.localapp.repository.administration.MotifAbsenceRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.TypeAbsenceRepository;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.DocumentCategoryRepository;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.parcours.EventCategoryRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.InstitutionRepository;
import com.tpc.tpcgestpaie.localapp.service.ColonneAffichageService;
import com.tpc.tpcgestpaie.localapp.service.LienParenteService;
import com.tpc.tpcgestpaie.localapp.service.MotifArretContratService;
import com.tpc.tpcgestpaie.localapp.service.NiveauAffichageService;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.tpc.tpcgestpaie.localapp.repository.jourFerie.JourFerieRepository;
import com.tpc.tpcgestpaie.localapp.model.jourFerie.JourFerie;
import com.tpc.tpcgestpaie.localapp.enums.TypeJourFerie;
@Component
public class DataInitializer implements CommandLineRunner {
    private final ActivityAreaRepository activityAreaRepository;
    private final RoleRepository roleRepository;
    private final StatusRepository statusRepository;
    private final TvaRepository tvaRepository;
    private final NatureContratRepository natureContratRepository;
    private final CategorieEmployeRepository categorieEmployeRepository;
    private final NiveauAffichageRepository niveauAffichageRepository;
    private final ColonneAffichageRepository colonneAffichageRepository;
    private final RubriqueRepository rubriqueRepository;
    private final NiveauAffichageService niveauAffichageService;
    private final ColonneAffichageService colonneAffichageService;
    private final LienParenteRepository lienParenteRepository;
    private final ModeDePaiementRepository modeDePaiementRepository;
    private final MotifAbsenceRepository motifAbsenceRepository;
    private final TypeAbsenceRepository typeAbsenceRepository;
    private final  BanqueRepository banqueRepository;
    private final InstitutionRepository institutionRepository;
    private final DiplomeRepository diplomeRepository;
    private final MotifArretContratService motifArretContratService;
    private final MotifArretContratRepository motifArretContratRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MinistereTravailRepository ministereTravailRepository;
    private final DocumentCategoryRepository documentCategoryRepository;
    private final ParametrePaieRepository parametrePaieRepository;
    private final ItsTrancheRepository itsTrancheRepository;
    private final JourFerieRepository jourFerieRepository;
    private final EventCategoryRepository eventCategoryRepository;

    public DataInitializer(ActivityAreaRepository activityAreaRepository, RoleRepository roleRepository, StatusRepository statusRepository, TvaRepository tvaRepository, NatureContratRepository natureContratRepository, CategorieEmployeRepository categorieEmployeRepository, NiveauAffichageRepository niveauAffichageRepository, ColonneAffichageRepository colonneAffichageRepository, RubriqueRepository rubriqueRepository, NiveauAffichageService niveauAffichageService, ColonneAffichageService colonneAffichageService, LienParenteService lienParenteService, LienParenteRepository lienParenteRepository, ModeDePaiementRepository modeDePaiementRepository, MotifAbsenceRepository motifAbsenceRepository, TypeAbsenceRepository typeAbsenceRepository, BanqueRepository banqueRepository, InstitutionRepository institutionRepository, DiplomeRepository diplomeRepository, MotifArretContratService motifArretContratService, MotifArretContratRepository motifArretContratRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, MinistereTravailRepository ministereTravailRepository, DocumentCategoryRepository documentCategoryRepository, ParametrePaieRepository parametrePaieRepository, ItsTrancheRepository itsTrancheRepository, JourFerieRepository jourFerieRepository, EventCategoryRepository eventCategoryRepository) {
            this.activityAreaRepository = activityAreaRepository;
            this.roleRepository = roleRepository;
            this.statusRepository = statusRepository;
            this.tvaRepository = tvaRepository;
            this.natureContratRepository = natureContratRepository;
            this.categorieEmployeRepository = categorieEmployeRepository;
            this.niveauAffichageRepository = niveauAffichageRepository;
            this.colonneAffichageRepository = colonneAffichageRepository;
            this.rubriqueRepository = rubriqueRepository;
            this.niveauAffichageService = niveauAffichageService;
            this.colonneAffichageService = colonneAffichageService;
            this.lienParenteRepository = lienParenteRepository;
            this.modeDePaiementRepository = modeDePaiementRepository;
            this.motifAbsenceRepository = motifAbsenceRepository;
            this.typeAbsenceRepository = typeAbsenceRepository;
            this.banqueRepository = banqueRepository;
            this.institutionRepository = institutionRepository;
            this.diplomeRepository = diplomeRepository;
        this.motifArretContratService = motifArretContratService;
        this.motifArretContratRepository = motifArretContratRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.ministereTravailRepository = ministereTravailRepository;
        this.documentCategoryRepository = documentCategoryRepository;
        this.parametrePaieRepository = parametrePaieRepository;
        this.itsTrancheRepository = itsTrancheRepository;
        this.jourFerieRepository = jourFerieRepository;
        this.eventCategoryRepository = eventCategoryRepository;
    }

        @Override
        @Transactional
        public void run(String... args) {
            initRoles();
            initStatuses();
            initActivityAreas();
            initTvas();
            initCategoryEmployes();
            initNatureContrats();
            initNiveauAffichages();
            initColonneAffichages();
            initRubriques();
            initLienParente();
            initModeDePaiements();
            initTypeAbsences();
            initBanques();
            initInstitutions();
            initDiplome();
            initMotifAbsences();
            initMotifsArretContrat();
            initMinistereTravail();
            initParametresPaie();
            initItsTranches();
            initJoursFeriesNationaux();
            initEventCategories();
        }

    public void initEventCategories() {

        addIfNotExists(
                "CONTRAT",
                "Historique des contrats",
                "contrat,cdi,cdd,engagement,embauche,recrutement,confirmation,\n" +
                        "renouvellement,avenant,clause contractuelle,prise de service,\n" +
                        "date d'effet,date effet,contract,employment contract\n",
                1
        );

        addIfNotExists(
                "PROMOTION",
                "Évolution de carrière",
                "promotion,nomination,affectation,poste,fonction,designation,\n" +
                        "désignation,elevation,élévation,mutation,changement de poste,\n" +
                        "responsable,chef,manager,directeur,nomme,nommé,promu,promotion interne\n",
                2
        );

        addIfNotExists(
                "SALAIRE",
                "Évolution salariale",
                "salaire,augmentation,prime,brut,net,remuneration",
                3
        );

        addIfNotExists(
                "FORMATION",
                "Formations professionnelles",
                "formation,seminaire,certification,stage",
                4
        );

        addIfNotExists(
                "SANCTION",
                "Sanctions disciplinaires",
                "sanction,avertissement,blame,blâme,mise a pied,mise à pied,\n" +
                        "discipline,faute,faute grave,suspension,mesure disciplinaire,\n" +
                        "disciplinary warning,penalty\n",
                5
        );

        addIfNotExists(
                "DIPLOME",
                "Diplômes et certifications",
                "diplome,diplôme,certificat,attestation,licence,master,\n" +
                        "bts,doctorat,qualification,titre professionnel,\n" +
                        "academic certificate,degree\n",
                6
        );

        addIfNotExists(
                "ABSENCE",
                "Absences et congés",
                "absence,absent,conge,conges,congé,sabbatique,maladie,malade,permission,\n" +
                        "autorisation,absence autorisee,absence autorisée,conge annuel,congé annuel,\n" +
                        "conge maladie,congé maladie,conge maternite,congé maternité,\n" +
                        "conge paternite,congé paternité,conge sans solde,congé sans solde,\n" +
                        "conge exceptionnel,congé exceptionnel,leave,vacation,sick leave\n",
                7
        );

        addIfNotExists(
                "DOCUMENT",
                "Autres documents",
                "attestation,certificat,lettre,decision,décision,note de service,\n" +
                        "correspondance,document officiel,official document\n",
                99
        );

        System.out.println("✅ EventCategory initialized");
    }

    private void addIfNotExists(String code,
                                String label,
                                String keywords,
                                int order) {

        if (!eventCategoryRepository.existsByCode(code)) {

            EventCategory category = new EventCategory();

            category.setCode(code);
            category.setLabel(label);
            category.setSearchKeywords(keywords);
            category.setDisplayOrder(order);
            category.setDescription("Catégorie système");

            eventCategoryRepository.save(category);

            System.out.println("✔ Created category: " + code);
        }
    }

        private void initRoles() {
            List<String> roles = List.of("ROLE_COMPANY_ADMIN", "ROLE_SUPER_ADMIN", "ROLE_MANAGER", "ROLE_HR","ROLE_EMPLOYEE","COMPTABLE");
            roles.forEach(roleName -> {
                if (!roleRepository.existsByName(roleName)) {
                    Role role = new Role();
                    role.setName(roleName);
                    role.setDescription("Rôle " + roleName.toLowerCase());
                    role.setCreatedAt(LocalDateTime.now());
                    role.setUpdatedAt(LocalDateTime.now());
                    roleRepository.save(role);
                }
            });
        }
        private void initStatuses() {
            addStatusIfNotExists("Active", "Utilisateur actif");
            addStatusIfNotExists("Inactive", "Utilisateur inactif");
        }

        private void addStatusIfNotExists(String name, String description) {
            if (!statusRepository.existsByName(name)) {
                Status status = new Status();
                status.setName(name);
                status.setDescription(description);
                status.setCreatedAt(LocalDateTime.now());
                status.setUpdatedAt(LocalDateTime.now());
                statusRepository.save(status);
            }
        }

        private void initDiplome() {
            addDiplomeIfNotExists(new String("CEP"), "Certificat d'Études Primaires");
            addDiplomeIfNotExists(new String("BEPC"), "Brevet d'Études du Premier Cycle");
            addDiplomeIfNotExists(new String("BAC"), "Baccalauréat");

            addDiplomeIfNotExists(new String("CAP"), "Certificat d’Aptitude Professionnelle");
            addDiplomeIfNotExists(new String("BEP"), "Brevet d'Études Professionnelles");
            addDiplomeIfNotExists(new String("DUT"), "Diplôme Universitaire de Technologie");
            addDiplomeIfNotExists(new String("BTS"), "Brevet de Technicien Supérieur");
            addDiplomeIfNotExists(new String("LICENCE GENERALE"), "BAC+3");
            addDiplomeIfNotExists(new String("LICENCE PROFESSIONNELLE"), "BAC+3");
            addDiplomeIfNotExists(new String("BBA"), "BAC+3");

            addDiplomeIfNotExists(new String("MAITRISE"), "");
            addDiplomeIfNotExists(new String("MASTER"), "");
            addDiplomeIfNotExists(new String("DIPLOME D'INGENIEUR DE CONCEPTION"), "");
            addDiplomeIfNotExists(new String("DIPLOME D'INGENIEUR DES TRAVAUX"), "");
            addDiplomeIfNotExists(new String("MBA"), "Master of Business Administration");
            addDiplomeIfNotExists(new String("DOCTORAT (PhD)"), "");
        }

        private void addDiplomeIfNotExists(String libelle, String description) {
            if (!diplomeRepository.existsByName(libelle)) {
                Diplome diplome = new Diplome();
                diplome.setName(libelle);
                diplome.setDescription(description);
                diplome.setCreated_at(LocalDateTime.now());
                diplome.setUpdated_at(LocalDateTime.now());
                diplomeRepository.save(diplome);
            }
        }

    private void initActivityAreas() {
        addActivityArea("Informatique", "Technologies de l’information, développement logiciel, cybersécurité, cloud, intelligence artificielle");
        addActivityArea("Santé", "Hôpitaux, cliniques, laboratoires médicaux, industries pharmaceutiques");
        addActivityArea("Éducation", "Écoles, universités, centres de formation, e-learning");
        addActivityArea("Finance", "Banques, assurances, microfinance, fintech, institutions financières");
        addActivityArea("Construction", "BTP, génie civil, architecture, urbanisme, travaux publics");
        addActivityArea("Agroalimentaire", "Agriculture, élevage, pêche, transformation alimentaire, distribution");
        addActivityArea("Énergie", "Production et distribution d’électricité, pétrole, gaz, énergies renouvelables, eau");
        addActivityArea("Transport et logistique", "Transports routiers, maritimes, aériens, ferroviaires, supply chain");
        addActivityArea("Commerce et distribution", "Grande distribution, e-commerce, import-export, marchés de gros");
        addActivityArea("Tourisme et hôtellerie", "Agences de voyage, hôtels, restaurants, loisirs, culture");
        addActivityArea("Industrie", "Manufacture, production industrielle, usines, textile, mécanique");
        addActivityArea("Télécommunications", "Opérateurs mobiles, fournisseurs internet, data centers");
        addActivityArea("Immobilier", "Promotion immobilière, gestion locative, foncier, habitat social");
        addActivityArea("Services publics", "Administrations, collectivités locales, ONG, associations");
        addActivityArea("Médias et communication", "Presse, télévision, radio, agences marketing et communication digitale");
        addActivityArea("Droit et conseil", "Cabinets d’avocats, notaires, expertise comptable, conseil en gestion");
        addActivityArea("Sécurité", "Sociétés de gardiennage, sécurité privée, cybersécurité");
        addActivityArea("Art et culture", "Artisans, créateurs, musique, cinéma, événements");
        addActivityArea("Environnement", "Gestion des déchets, recyclage, protection de la nature, climat");
        addActivityArea("Sports et loisirs", "Clubs sportifs, fitness, événements sportifs, divertissement");
        addActivityArea("Ressources Humaines", "Cabinets RH, gestion du personnel, recrutement, formation, externalisation");
        addActivityArea("Services aux entreprises", "Audit, conseil, centres d’appels, outsourcing, BPO");
        addActivityArea("Recherche et développement", "Innovation, laboratoires de recherche, start-ups technologiques");
    }

        private void addActivityArea(String name, String description) {
            if (!activityAreaRepository.existsByName(name)) {
                ActivityArea area = new ActivityArea();
                area.setName(name);
                area.setDescription(description);
                area.setCreatedAt(LocalDateTime.now());
                area.setUpdatedAt(LocalDateTime.now());
                activityAreaRepository.save(area);
            }
        }

        private void initTvas() {

            addTvaIfNotExists(new BigDecimal("0.18"), "ASSUJETTIE");
            addTvaIfNotExists(new BigDecimal("0.0"), "NON ASSUJETTIE");
        }
        private void addTvaIfNotExists(BigDecimal rate, String description) {
            if (!tvaRepository.existsByRate(rate)) {
                Tva tva = new Tva();
                tva.setRate(rate);
                tva.setDescription(description);
                tva.setCreatedAt(LocalDateTime.now());
                tva.setUpdatedAt(LocalDateTime.now());
                tvaRepository.save(tva);
            }
        }

    private void initMinistereTravail() {
        addMinistereTravailIfNotExists(
                "Ministère du Travail et des Affaires Sociales",
                "Caisse Nationale de Sécurité Sociale",
                "Direction de l’Enregistrement des Contrats",
                "Jean Dupont"
        );
    }

    private void addMinistereTravailIfNotExists(String ministere, String serviceSecuriteSociale,
                                                String directionEnregistrementContrat, String directeurDepartementalTravail) {
        if (ministereTravailRepository.count() == 0) {
            MinistereTravail ministereTravail = new MinistereTravail();
            ministereTravail.setMinistere(ministere);
            ministereTravail.setServiceSecuriteSociale(serviceSecuriteSociale);
            ministereTravail.setDirectionEnregistrementContrat(directionEnregistrementContrat);
            ministereTravail.setDirecteurDepartementalTravail(directeurDepartementalTravail);
            ministereTravail.setCreated_at(LocalDateTime.now());
            ministereTravail.setUpdated_at(LocalDateTime.now());
            ministereTravailRepository.save(ministereTravail);
        }
    }

        //Nature contrat
        private void initNatureContrats() {
            addNatureContratIfNotExists(new String("TRAVAIL"), "");
            addNatureContratIfNotExists(new String("STAGE"), "");
            addNatureContratIfNotExists(new String("PRESTATION"), "");
            addNatureContratIfNotExists(new String("EXPATRIE"), "");
        }

        private void addNatureContratIfNotExists(String libelle, String description) {
            if (!natureContratRepository.existsByLibelle(libelle)) {
                NatureContrat natureContrat = new NatureContrat();
                natureContrat.setLibelle(libelle);
                natureContrat.setDescription(description);
                natureContrat.setCreated_at(LocalDateTime.now());
                natureContrat.setUpdated_at(LocalDateTime.now());
                natureContratRepository.save(natureContrat);
            }
        }

    private void initItsTranches() {
        addItsTrancheIfNotExists(
                new BigDecimal("60000"),   // limiteTranche1
                new BigDecimal("150000"),  // limiteTranche2
                new BigDecimal("250000"),  // limiteTranche3
                new BigDecimal("500000"),  // limiteTranche4
                0.0,      // rateTranche1 (0%)
                0.10,    // rateTranche2 (3,6%)
                0.15,    // rateTranche3 (6,4%)
                0.19,     // rateTranche4 (10%)
                0.25      // rateAbattement (20%)
        );
    }

    private void addItsTrancheIfNotExists(BigDecimal limiteTranche1,
                                          BigDecimal limiteTranche2,
                                          BigDecimal limiteTranche3,
                                          BigDecimal limiteTranche4,
                                          Double rateTranche1,
                                          Double rateTranche2,
                                          Double rateTranche3,
                                          Double rateTranche4,
                                          Double rateAbattement) {

        String uniqueKey = "PARAM_UNIQUE";

        if (!itsTrancheRepository.existsByUniqueKey(uniqueKey)) {
            ItsTranche itsTranche = new ItsTranche();

            // Set des limites de tranches
            itsTranche.setLimiteTranche1(limiteTranche1);
            itsTranche.setLimiteTranche2(limiteTranche2);
            itsTranche.setLimiteTranche3(limiteTranche3);
            itsTranche.setLimiteTranche4(limiteTranche4);

            // Set des taux de tranches (en décimal)
            itsTranche.setRateTranche1(rateTranche1);     // 0%
            itsTranche.setRateTranche2(rateTranche2);     // 3,6%
            itsTranche.setRateTranche3(rateTranche3);     // 6,4%
            itsTranche.setRateTranche4(rateTranche4);     // 10%

            // Set du taux d'abattement
            itsTranche.setRateAbattement(rateAbattement); // 20%

            itsTranche.setUniqueKey(uniqueKey);
            itsTranche.setAdded_by(null); // À adapter avec l'utilisateur connecté

            // Les dates created_at et updated_at seront gérées automatiquement
            // par les annotations @PrePersist et @PreUpdate

            itsTrancheRepository.save(itsTranche);
        }
    }

    // ParametrePaie initialisation
    private void initParametresPaie() {
        addParametrePaieIfNotExists(0.09,  0.064,  0.036, 2.0, 0.0); // Valeurs par défaut
    }
    private void addParametrePaieIfNotExists(Double prestationsFamiliales,
                                             Double pensionsEntreprise,
                                             Double pensionsEmploye,
                                             Double nbrAnneVpsEntreprise,
                                             Double nbrAnneVpsEmploye) {

        String uniqueKey = "PARAM_UNIQUE";

        if (!parametrePaieRepository.existsByUniqueKey(uniqueKey)) {
            ParametrePaie parametrePaie = new ParametrePaie();
            parametrePaie.setPrestationsfamiliales(prestationsFamiliales);
            parametrePaie.setPensionsEntreprise(pensionsEntreprise);
            parametrePaie.setPensionsEmploye(pensionsEmploye);
            parametrePaie.setNbrAnneVpsEntreprise(nbrAnneVpsEntreprise);
            parametrePaie.setNbrAnneVpsEmploye(nbrAnneVpsEmploye);
            parametrePaie.setUniqueKey(uniqueKey);
            // Note: added_by pourrait être défini avec l'utilisateur connecté ou null
            parametrePaie.setAdded_by(null); // ou récupérer l'utilisateur courant
            parametrePaie.setCreated_at(LocalDateTime.now());
            parametrePaie.setUpdated_at(LocalDateTime.now());

            parametrePaieRepository.save(parametrePaie);
        }
    }

        private void initModeDePaiements() {
            addModeDePaiementIfNotExists(new String("HORAIRE"), "");
            addModeDePaiementIfNotExists(new String("JOURNALIER"), "");
            addModeDePaiementIfNotExists(new String("MENSUEL"), "");
        }

        private void addModeDePaiementIfNotExists(String libelle, String description) {
            if (!modeDePaiementRepository.existsByLibelle(libelle)) {
                ModeDePaiement modeDePaiement = new ModeDePaiement();
                modeDePaiement.setLibelle(libelle);
                modeDePaiement.setDescription(description);
                modeDePaiement.setCreated_at(LocalDateTime.now());
                modeDePaiement.setUpdated_at(LocalDateTime.now());
                modeDePaiementRepository.save(modeDePaiement);
            }
        }

    private void initMotifsArretContrat() {
        addMotifIfNotExists("FAUTE LOURDE", "RUPTURE DU CONTRAT SUITE À UNE FAUTE LOURDE.");
        addMotifIfNotExists("FAUTE GRAVE", "RUPTURE DU CONTRAT SUITE À UNE FAUTE GRAVE.");
        addMotifIfNotExists("DÉMISSION", "LE SALARIÉ A DÉCIDÉ DE METTRE FIN AU CONTRAT DE TRAVAIL.");
        addMotifIfNotExists("ESSAI NON CONCLUANT", "PÉRIODE D’ESSAI NON VALIDÉE PAR L’EMPLOYEUR OU LE SALARIÉ.");
        addMotifIfNotExists("SUSPENSION", "SUSPENSION TEMPORAIRE DU CONTRAT.");
        addMotifIfNotExists("LICENCIEMENT", "FIN DE CONTRAT DÉCIDÉE PAR L’EMPLOYEUR.");
        addMotifIfNotExists("FIN DE BESOIN", "LE POSTE N’EST PLUS NÉCESSAIRE OU MISSION TERMINÉE.");
        addMotifIfNotExists("RETRAITE", "Départ du salarié à la retraite.");
        addMotifIfNotExists("FIN DE CDD", "Fin d’un contrat à durée déterminée.");
    }

    private void addMotifIfNotExists(String libelle, String description) {
        if (!motifArretContratService.existsByLibelle(libelle)) {
            MotifArretContrat motif = new MotifArretContrat();
            motif.setLibelle(libelle);
            motif.setDescription(description);
            motif.setCreated_at(LocalDateTime.now());
            motif.setUpdated_at(LocalDateTime.now());
            motifArretContratRepository.save(motif);
        }
    }

    private void initMotifAbsences() {
            addMotifAbsenceIfNotExists("MALADIE","ABSENCE");
            addMotifAbsenceIfNotExists("MARIAGE","ABSENCE");
            addMotifAbsenceIfNotExists("SABBATIQUE","ABSENCE");
            addMotifAbsenceIfNotExists("FORMATION","ABSENCE");
            addMotifAbsenceIfNotExists("DECES","ABSENCE");
            addMotifAbsenceIfNotExists("PATERNITE","CONGE");
            addMotifAbsenceIfNotExists("MATERNITE","CONGE");
            addMotifAbsenceIfNotExists("CONGE ADMINISTRATIF","CONGE");

        addMotifAbsenceIfNotExists("CONGE","");
        addMotifAbsenceIfNotExists("CONVENANCE PERSONNELLE","");
        addMotifAbsenceIfNotExists("LEGAL","");

        }

        private void addMotifAbsenceIfNotExists(String libelle, String categorie) {
            if (!motifAbsenceRepository.existsByLibelle(libelle)) {
                MotifAbsence motifAbsence = new MotifAbsence();
                motifAbsence.setLibelle(libelle);
                motifAbsence.setCategorie(categorie);
                motifAbsence.setCreated_at(LocalDateTime.now());
                motifAbsence.setUpdated_at(LocalDateTime.now());
                motifAbsenceRepository.save(motifAbsence);
            }
        }

        //type absence

        private void initTypeAbsences() {

            addTypeAbsenceIfNotExists("CONGE");
            addTypeAbsenceIfNotExists("CONVENANCE PERSONNELLE");
            addTypeAbsenceIfNotExists("MALADIE");
            addTypeAbsenceIfNotExists("MARIAGE");
            addTypeAbsenceIfNotExists("SABBATIQUE");
            addTypeAbsenceIfNotExists("FORMATION");
            addTypeAbsenceIfNotExists("DECES");
            addTypeAbsenceIfNotExists("PATERNITE");
            addTypeAbsenceIfNotExists("MATERNITE");
            addTypeAbsenceIfNotExists("NAISSANCE AU FOYER");
            addTypeAbsenceIfNotExists("CONGE ADMINISTRATIF");

        }
        private void addTypeAbsenceIfNotExists(String libelle) {
            if (!typeAbsenceRepository.existsByLibelle(libelle)) {
                TypeAbsence typeAbsence = new TypeAbsence();
                typeAbsence.setLibelle(libelle);
                typeAbsence.setCreated_at(LocalDateTime.now());
                typeAbsence.setUpdated_at(LocalDateTime.now());
                typeAbsenceRepository.save(typeAbsence);
            }
        }


        //Category employe

        private void initCategoryEmployes() {
            addCategoryEmployeIfNotExists(new String("TRAVAILLEUR PAYE A L'HEURE"), "");
            addCategoryEmployeIfNotExists(new String("EMPLOYE, MANOEUVRE ET OUVRIER"), "");
            addCategoryEmployeIfNotExists(new String("AGENT DE MAITRISE, CADRE ET ASSIMILE"), "");
        }

        private void addCategoryEmployeIfNotExists(String name, String description) {
            if (!categorieEmployeRepository.existsByName(name)) {
                CategorieEmploye categorieEmploye = new CategorieEmploye();
                categorieEmploye.setName(name);
                categorieEmploye.setDescription(description);
                categorieEmploye.setCreatedAt(LocalDateTime.now());
                categorieEmploye.setUpdatedAt(LocalDateTime.now());
                categorieEmployeRepository.save(categorieEmploye);
            }
        }

        private void initNiveauAffichages() {
            addNiveauAffichageIfNotExists(new String("AVANT SALAIRE BRUT"), "");
            addNiveauAffichageIfNotExists(new String("A PARTIR DE SALAIRE BRUT ET AVANT RETENUES LEGALES"), "");
            addNiveauAffichageIfNotExists(new String("A PARTIR RETENUES LEGALES JUSQU'A LA FIN"), "");
        }

        private void addNiveauAffichageIfNotExists(String libelle, String description) {
            if (!niveauAffichageRepository.existsByLibelle(libelle)) {
                NiveauAffichage niveauAffichage = new NiveauAffichage();
                niveauAffichage.setLibelle(libelle);
                niveauAffichage.setDescription(description);
                niveauAffichage.setCreated_at(LocalDateTime.now());
                niveauAffichage.setUpdated_at(LocalDateTime.now());
                niveauAffichageRepository.save(niveauAffichage);
            }
        }


        private void initColonneAffichages() {
            addColonneAffichageIfNotExists(new String("GAIN"), "");
            addColonneAffichageIfNotExists(new String("RETENUES LEGALES"), "");
            addColonneAffichageIfNotExists(new String("AUTRES RETENUES"), "");
            addColonneAffichageIfNotExists(new String("CHARGES PATRONALES"), "");
        }
        private void addColonneAffichageIfNotExists(String libelle, String description) {
            if (!colonneAffichageRepository.existsByLibelle(libelle)) {
                ColonneAffichage colonneAffichage = new ColonneAffichage();
                colonneAffichage.setLibelle(libelle);
                colonneAffichage.setDescription(description);
                colonneAffichage.setCreated_at(LocalDateTime.now());
                colonneAffichage.setUpdated_at(LocalDateTime.now());
                colonneAffichageRepository.save(colonneAffichage);
            }
        }

        private void initRubriques() {


            addRubriqueIfNotExists(
                    "SALAIRE 13e MOIS",  // libelle vide
                    "AVANTAGE",
                    "OUI",
                    niveauAffichageService.findByLibelle("AVANT SALAIRE BRUT").get(),
                    colonneAffichageService.findByLibelle("GAIN").get(),
                    false,
                    "NON",
                    "NON",
                    0.0,
                    "NON",
                    0,
                    "OUI"
            );

            addRubriqueIfNotExists(
                    "PRIMES EXCEPTIONNELLES",  // libelle vide
                    "AVANTAGE",
                    "OUI",
                    niveauAffichageService.findByLibelle("AVANT SALAIRE BRUT").get(),
                    colonneAffichageService.findByLibelle("GAIN").get(),
                    false,
                    "NON",
                    "NON",
                    0.0,
                    "NON",
                    0,
                    "OUI"
            );

                    addRubriqueIfNotExists(
                            "SALAIRE DE BASE",
                            "AVANTAGE",
                            "OUI",
                            niveauAffichageService.findByLibelle("AVANT SALAIRE BRUT").get(),
                            colonneAffichageService.findByLibelle("GAIN").get(),
                            true,
                            "NON",
                            "NON",
                            0.0,
                            "NON",
                            0,
                            "OUI"
                    );

            addRubriqueIfNotExists(
                    "SURSALAIRE",
                    "AVANTAGE",
                    "OUI",
                    niveauAffichageService.findByLibelle("AVANT SALAIRE BRUT").get(),
                    colonneAffichageService.findByLibelle("GAIN").get(),
                    true,
                    "NON",
                    "NON",
                    0.0,
                    "NON",
                    0,
                    "OUI"
            );

            addRubriqueIfNotExists(
                    "PRIME DE SUJETION",
                    "AVANTAGE",
                    "OUI",
                    niveauAffichageService.findByLibelle("AVANT SALAIRE BRUT").get(),
                    colonneAffichageService.findByLibelle("GAIN").get(),
                    true,
                    "NON",
                    "NON",
                    0.0,
                    "NON",
                    0,
                    "OUI"
            );

            addRubriqueIfNotExists(
                    "SALAIRE MOYEN JOURNALIER",
                    "AVANTAGE",
                    "OUI",
                    niveauAffichageService.findByLibelle("AVANT SALAIRE BRUT").get(),
                    colonneAffichageService.findByLibelle("GAIN").get(),
                    true,
                    "NON",
                    "NON",
                    0.0,
                    "NON",
                    0,
                    "OUI"
            );

            addRubriqueIfNotExists(
                    "SALAIRE MOYEN",
                    "AVANTAGE",
                    "OUI",
                    niveauAffichageService.findByLibelle("AVANT SALAIRE BRUT").get(),
                    colonneAffichageService.findByLibelle("GAIN").get(),
                    true,
                    "NON",
                    "NON",
                    0.0,
                    "NON",
                    0,
                    "OUI"
            );
            addRubriqueIfNotExists(
                    "HONORAIRE DE BASE",
                    "AVANTAGE",
                    "OUI",
                    niveauAffichageService.findByLibelle("AVANT SALAIRE BRUT").get(),
                    colonneAffichageService.findByLibelle("GAIN").get(),
                    false,
                    "NON",
                    "NON",
                    0.0,
                    "NON",
                    1,
                    "NON"
            );


            addRubriqueIfNotExists(
                    "FORFAIT HEURES SUPPLEMENTAIRES",
                    "AVANTAGE",
                    "OUI",
                    niveauAffichageService.findByLibelle("AVANT SALAIRE BRUT").get(),
                    colonneAffichageService.findByLibelle("GAIN").get(),
                    false,
                    "NON",
                    "NON",
                    0.0,
                    "NON",
                    0,
                    "NON"
            );

            addRubriqueIfNotExists(
                    "PRIME D'ANCIENNETE",
                    "AVANTAGE",
                    "OUI",
                    niveauAffichageService.findByLibelle("AVANT SALAIRE BRUT").get(),
                    colonneAffichageService.findByLibelle("GAIN").get(),
                    false,
                    "NON",
                    "NON",
                    0.0,
                    "NON",
                    0,
                    "NON"
            );

            addRubriqueIfNotExists(
                    "CAUTION",
                    "RETENUE",
                    "NON",
                    niveauAffichageService.findByLibelle("A PARTIR RETENUES LEGALES JUSQU'A LA FIN").get(),
                    colonneAffichageService.findByLibelle("AUTRES RETENUES").get(),
                    false,
                    "NON",
                    "NON",
                    0.0,
                    "NON",
                    0,
                    "OUI"
            );

            addRubriqueIfNotExists(
                    "PRIME DE TRANSPORT",
                    "AVANTAGE",
                    "OUI",
                    niveauAffichageService.findByLibelle("A PARTIR RETENUES LEGALES JUSQU'A LA FIN").get(),
                    colonneAffichageService.findByLibelle("AUTRES RETENUES").get(),
                    false,
                    "NON",
                    "NON",
                    0.0,
                    "NON",
                    0,
                    "OUI"
            );

            addRubriqueIfNotExists(
                    "PRIME DE RESPONSABILITE",
                    "AVANTAGE",
                    "OUI",
                    niveauAffichageService.findByLibelle("A PARTIR RETENUES LEGALES JUSQU'A LA FIN").get(),
                    colonneAffichageService.findByLibelle("AUTRES RETENUES").get(),
                    false,
                    "NON",
                    "NON",
                    0.0,
                    "NON",
                    0,
                    "OUI"
            );

        }

        private void addRubriqueIfNotExists(String libelle, String nature, String imposable, NiveauAffichage niveauAffichage, ColonneAffichage colonneAffichage, boolean prorata, String aPartirCoefficient,String aPartirDuSalaireBrut, double coefficient, String partPatronale, int numeroOrdre, String estRubriqueSysteme) {
            if (!rubriqueRepository.existsByLibelle(libelle)) {
                Rubrique rubrique = new Rubrique();
                rubrique.setLibelle(libelle);
                rubrique.setNature(nature);
                rubrique.setRubriqueImposable(imposable);
                rubrique.setNiveauAffichage(niveauAffichage);
                rubrique.setColonneAffichage(colonneAffichage);
                rubrique.setCalculeAuProrataTempsTravail(prorata);
                rubrique.setCalculeAPartirSalaireBrut(aPartirDuSalaireBrut);
                rubrique.setCalculeAPartirCoefficient(aPartirCoefficient);
                rubrique.setCoefficient(coefficient);
                rubrique.setPartPatronale(partPatronale);
                rubrique.setNumeroOrdre(numeroOrdre);
                rubrique.setRubriqueSysteme(estRubriqueSysteme);
                rubrique.setCreated_at(LocalDateTime.now());
                rubrique.setUpdated_at(LocalDateTime.now());
                rubriqueRepository.save(rubrique);
            }
        }

        private void initLienParente() {
            addLienParenteIfNotExists(new String("PERE"), "");
            addLienParenteIfNotExists(new String("MERE"), "");
            addLienParenteIfNotExists(new String("ONCLE"), "");
            addLienParenteIfNotExists(new String("TANTE"), "");
            addLienParenteIfNotExists(new String("FILS"), "");
            addLienParenteIfNotExists(new String("FILLE"), "");
            addLienParenteIfNotExists(new String("GRAND-PARENT"), "");
            addLienParenteIfNotExists(new String("GRAND-PÈRE"), "");
            addLienParenteIfNotExists(new String("GRAND-MÈRE"), "");
            addLienParenteIfNotExists(new String("PETIT-FILS"), "");
            addLienParenteIfNotExists(new String("PETITE-FILLE"), "");
            addLienParenteIfNotExists(new String("FRÈRE"), "");
            addLienParenteIfNotExists(new String("SŒUR"), "");
            addLienParenteIfNotExists(new String("COUSIN"), "");
            addLienParenteIfNotExists(new String("COUSINE"), "");
            addLienParenteIfNotExists(new String("NEVEU"), "");
            addLienParenteIfNotExists(new String("NIÈCE"), "");
            addLienParenteIfNotExists(new String("BELLE-MÈRE"), "");
            addLienParenteIfNotExists(new String("BEAU-PÈRE"), "");
            addLienParenteIfNotExists(new String("BELLE-SŒUR"), "");
            addLienParenteIfNotExists(new String("BEAU-FRÈRE"), "");
            addLienParenteIfNotExists(new String("CONJOINT"), "");
            addLienParenteIfNotExists(new String("CONJOINTE"), "");
            addLienParenteIfNotExists(new String("TUTEUR"), "");
            addLienParenteIfNotExists(new String("TUTRICE"), "");
        }

        private void addLienParenteIfNotExists(String libelle, String description) {
            if (!lienParenteRepository.existsByLibelle(libelle)) {
                LienParente lienParente = new LienParente();
                lienParente.setLibelle(libelle);
                lienParente.setDescription(description);
                lienParente.setCreated_at(LocalDateTime.now());
                lienParente.setUpdated_at(LocalDateTime.now());
                lienParenteRepository.save(lienParente);
            }
        }

        //Banques
        private void initBanques() {
            addBanqueIfNotExists("Bank of Africa");
            addBanqueIfNotExists("NSIA Banque");
            addBanqueIfNotExists("Banque Atlantique");
            addBanqueIfNotExists("Coris Bank International");
            addBanqueIfNotExists("Banque Internationale pour l’Industrie et le Commerce");
            addBanqueIfNotExists("BGFIBank");
            addBanqueIfNotExists("Ecobank");
            addBanqueIfNotExists("Orabank");
            addBanqueIfNotExists("Société Générale");
            addBanqueIfNotExists("United Bank for Africa");
            addBanqueIfNotExists("MTN MOMO");
            addBanqueIfNotExists("MOOV MONEY");
            addBanqueIfNotExists("CELTIS CASH");

        }

        private void addBanqueIfNotExists(String name) {
            if (!banqueRepository.existsByName(name)) {
                Banque banque = new Banque();
                banque.setName(name);
                banque.setCreated_at(LocalDateTime.now());
                banque.setUpdated_at(LocalDateTime.now());
                banqueRepository.save(banque);
            }
        }

        //Banques
        //Institutions
        private void initInstitutions() {
            addInstitutionIfNotExists("VITAL FINANCE");
            addInstitutionIfNotExists("ALIDE");
            addInstitutionIfNotExists("CLCAM");
        }

        private void addInstitutionIfNotExists(String name) {
            if (!institutionRepository.existsByName(name)) {
                Institution institution = new Institution();
                institution.setName(name);
                institution.setCreated_at(LocalDateTime.now());
                institution.setUpdated_at(LocalDateTime.now());
                institutionRepository.save(institution);
            }
        }


    /**
     * 🌍 Initialise les jours fériés nationaux pour plusieurs pays
     */
    private void initJoursFeriesNationaux() {
        // Initialiser pour l'année en cours
        int anneeEnCours = LocalDate.now().getYear();

        // Bénin
        initJoursFeriesBenin(anneeEnCours);

        // France (optionnel - décommenter si besoin)
        // initJoursFeriesFrance(anneeEnCours);

        // Côte d'Ivoire (optionnel - décommenter si besoin)
        // initJoursFeriesCoteIvoire(anneeEnCours);

        // Togo (optionnel - décommenter si besoin)
        // initJoursFeriesTogo(anneeEnCours);
    }

    /**
     * 🇧🇯 Initialise les jours fériés du Bénin
     */
    private void initJoursFeriesBenin(int annee) {
        String pays = "BJ";

        // Vérifier si déjà initialisé
//        if (jourFerieRepository.countByPaysAndAnneeAndDeletedAtIsNull(pays, annee) > 0) {
//            return; // Déjà initialisé
//        }

        // ===== JOURS FÉRIÉS FIXES =====

        // 1er janvier - Jour de l'An
        addJourFerieFixeIfNotExists(
                "JOUR_AN", "Jour de l'An",
                annee, 1, 1, pays, true, TypeJourFerie.NATIONAL
        );

        // 10 janvier - Fête du Vodoun

        addJourFerieFixeIfNotExists(
                "FETE_VODOUN", "Fête du Vodoun",
                annee, 1, 10, pays, true, TypeJourFerie.NATIONAL
        );

        // 1er mai - Fête du Travail
        addJourFerieFixeIfNotExists(
                "FETE_TRAVAIL", "Fête du Travail",
                annee, 5, 1, pays, true, TypeJourFerie.NATIONAL
        );

        // 1er août - Fête Nationale
        addJourFerieFixeIfNotExists(
                "FETE_NATIONALE", "Fête Nationale du Bénin",
                annee, 8, 1, pays, true, TypeJourFerie.NATIONAL
        );

        // 15 août - Assomption
        addJourFerieFixeIfNotExists(
                "ASSOMPTION", "Assomption",
                annee, 8, 15, pays, true, TypeJourFerie.NATIONAL
        );

        // 26 octobre - Fête des Forces Armées
//        addJourFerieFixeIfNotExists(
//                "FORCES_ARMEES", "Fête des Forces Armées",
//                annee, 10, 26, pays, true, TypeJourFerie.NATIONAL
//        );

        // 1er novembre - Toussaint
        addJourFerieFixeIfNotExists(
                "TOUSSAINT", "Toussaint",
                annee, 11, 1, pays, true, TypeJourFerie.NATIONAL
        );

        // 30 novembre - Saint André
//        addJourFerieFixeIfNotExists(
//                "SAINT_ANDRE", "Saint André",
//                annee, 11, 30, pays, true, TypeJourFerie.NATIONAL
//        );

        // 25 décembre - Noël
        addJourFerieFixeIfNotExists(
                "NOEL", "Noël",
                annee, 12, 25, pays, true, TypeJourFerie.NATIONAL
        );

        // ===== JOURS FÉRIÉS MOBILES (CHRÉTIENS) =====

        // Lundi de Pâques
        LocalDate lundiPaques = calculerLundiPaques(annee);
        addJourFerieMobileIfNotExists(
                "LUNDI_PAQUES", "Lundi de Pâques",
                lundiPaques, annee, pays, true, TypeJourFerie.MOBILE
        );

        // Ascension (Jeudi, 39 jours après Pâques)
        LocalDate ascension = calculerAscension(annee);
        addJourFerieMobileIfNotExists(
                "ASCENSION", "Ascension",
                ascension, annee, pays, true, TypeJourFerie.MOBILE
        );

        // Lundi de Pentecôte (50 jours après Pâques)
        LocalDate lundiPentecote = calculerLundiPentecote(annee);
        addJourFerieMobileIfNotExists(
                "LUNDI_PENTECOTE", "Lundi de Pentecôte",
                lundiPentecote, annee, pays, true, TypeJourFerie.MOBILE
        );

        // ===== JOURS FÉRIÉS MOBILES (MUSULMANS) =====
        // Note: Dates approximatives basées sur le calendrier lunaire

        // Korité (Aïd el-Fitr) - fin du Ramadan
//        LocalDate korite = calculerKorite(annee);
//        if (korite != null) {
//            addJourFerieMobileIfNotExists(
//                    "KORITE", "Korité (Aïd el-Fitr)",
//                    korite, annee, pays, true, TypeJourFerie.MOBILE
//            );
//        }

        // Tabaski (Aïd el-Kebir) - fête du mouton
//        LocalDate tabaski = calculerTabaski(annee);
//        if (tabaski != null) {
//            addJourFerieMobileIfNotExists(
//                    "TABASKI", "Tabaski (Aïd el-Kébir)",
//                    tabaski, annee, pays, true, TypeJourFerie.MOBILE
//            );
//        }

        // Maouloud (Anniversaire du Prophète)
//        LocalDate maouloud = calculerMaouloud(annee);
//        if (maouloud != null) {
//            addJourFerieMobileIfNotExists(
//                    "MAOULOUD", "Maouloud (Anniversaire du Prophète)",
//                    maouloud, annee, pays, true, TypeJourFerie.MOBILE
//            );
//        }
    }

    /**
     * 🇫🇷 Initialise les jours fériés de France (OPTIONNEL)
     */

    // ===== MÉTHODES UTILITAIRES =====

    /**
     * Ajoute un jour férié fixe s'il n'existe pas
     */
    private void addJourFerieFixeIfNotExists(
            String code, String libelle,
            int annee, int mois, int jour,
            String pays, boolean estNational,
            TypeJourFerie type
    ) {
        if (jourFerieRepository.findBySlugAndPaysAndAnnee(code, pays, annee).isEmpty()) {
            JourFerie jourFerie = JourFerie.builder()
                    .slug(code)
                    .libelle(libelle)
                    .dateFerie(LocalDate.of(annee, mois, jour))
                    .pays(pays)
                    .estFixe(true)
                    .estRecurrent(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            jourFerieRepository.save(jourFerie);
        }
    }

    /**
     * Ajoute un jour férié mobile s'il n'existe pas
     */
    private void addJourFerieMobileIfNotExists(
            String code, String libelle,
            LocalDate date, int annee,
            String pays, boolean estNational,
            TypeJourFerie type
    ) {
        if (jourFerieRepository.findBySlugAndPaysAndAnnee(code, pays, annee).isEmpty()) {
            JourFerie jourFerie = JourFerie.builder()
                    .slug(code)
                    .libelle(libelle)
                    .dateFerie(date)
                    .pays(pays)
                    .estFixe(false)
                    .estRecurrent(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            jourFerieRepository.save(jourFerie);
        }
    }

    // ===== CALCUL DES JOURS MOBILES =====

    /**
     * Calcule la date de Pâques (algorithme de Computus)
     */
    private LocalDate calculerPaques(int annee) {
        int a = annee % 19;
        int b = annee / 100;
        int c = annee % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int mois = (h + l - 7 * m + 114) / 31;
        int jour = ((h + l - 7 * m + 114) % 31) + 1;

        return LocalDate.of(annee, mois, jour);
    }

    /**
     * Calcule le Lundi de Pâques (Pâques + 1 jour)
     */
    private LocalDate calculerLundiPaques(int annee) {
        return calculerPaques(annee).plusDays(1);
    }

    /**
     * Calcule l'Ascension (Pâques + 39 jours)
     */
    private LocalDate calculerAscension(int annee) {
        return calculerPaques(annee).plusDays(39);
    }

    /**
     * Calcule le Lundi de Pentecôte (Pâques + 50 jours)
     */
    private LocalDate calculerLundiPentecote(int annee) {
        return calculerPaques(annee).plusDays(50);
    }

    /**
     * Calcule le Korité (approximatif - calendrier lunaire)
     * Dates approximatives pour 2024-2030
     */
    private LocalDate calculerKorite(int annee) {
        return switch (annee) {
            case 2024 -> LocalDate.of(2024, 4, 10);
            case 2025 -> LocalDate.of(2025, 3, 30);
            case 2026 -> LocalDate.of(2026, 3, 20);
            case 2027 -> LocalDate.of(2027, 3, 9);
            case 2028 -> LocalDate.of(2028, 2, 26);
            case 2029 -> LocalDate.of(2029, 2, 14);
            case 2030 -> LocalDate.of(2030, 2, 4);
            default -> null; // Retourner null si année non supportée
        };
    }

    /**
     * Calcule la Tabaski (approximatif - calendrier lunaire)
     * Dates approximatives pour 2024-2030
     */
    private LocalDate calculerTabaski(int annee) {
        return switch (annee) {
            case 2024 -> LocalDate.of(2024, 6, 16);
            case 2025 -> LocalDate.of(2025, 6, 6);
            case 2026 -> LocalDate.of(2026, 5, 27);
            case 2027 -> LocalDate.of(2027, 5, 16);
            case 2028 -> LocalDate.of(2028, 5, 4);
            case 2029 -> LocalDate.of(2029, 4, 23);
            case 2030 -> LocalDate.of(2030, 4, 13);
            default -> null;
        };
    }

    /**
     * Calcule le Maouloud (approximatif - calendrier lunaire)
     * Dates approximatives pour 2024-2030
     */
    private LocalDate calculerMaouloud(int annee) {
        return switch (annee) {
            case 2024 -> LocalDate.of(2024, 9, 15);
            case 2025 -> LocalDate.of(2025, 9, 4);
            case 2026 -> LocalDate.of(2026, 8, 25);
            case 2027 -> LocalDate.of(2027, 8, 14);
            case 2028 -> LocalDate.of(2028, 8, 2);
            case 2029 -> LocalDate.of(2029, 7, 22);
            case 2030 -> LocalDate.of(2030, 7, 12);
            default -> null;
        };
    }
    }