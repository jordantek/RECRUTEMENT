package com.tpc.tpcgestpaie.localapp.service.contrat;

import com.tpc.tpcgestpaie.localapp.dto.ContratEmployeRubriqueResponseDTO;
import com.tpc.tpcgestpaie.localapp.dto.contrat.ContratEmployeGlobalDTO;
import com.tpc.tpcgestpaie.localapp.dto.contrat.ContratEmployeRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.contrat.ContratEmployeResponseDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.event.EmployeChangedEvent;
import com.tpc.tpcgestpaie.localapp.dto.paie.ContratEmployeRubriqueDTO;
import com.tpc.tpcgestpaie.localapp.helper.UserHelper;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.*;
import com.tpc.tpcgestpaie.localapp.service.*;
import com.tpc.tpcgestpaie.localapp.service.administration.CreditCongeService;
import com.tpc.tpcgestpaie.localapp.service.emailConfig.EmailConfigService;
import com.tpc.tpcgestpaie.localapp.util.PasswordGenerator;
import com.tpc.tpcgestpaie.localapp.util.Status;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ContratGlobalService {
    private final ContratEmployeRepository contratRepository;
    private final CreditCongeService creditCongeService;
    private final DiplomeService diplomeService;
    private final RubriqueService rubriqueService;
    private final EmployeService employeService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailConfigService emailConfigService;
    private final ContratEmployeRubriqueService contratEmployeRubriqueService;
    private final StatusRepository statusRepository;
    private final EmployeDiplomeService employeDiplomeService;
    private final EmployeRepository employeRepository;
    private final CompanyRepository companyRepository;
    private final UserHelper userHelper;
    private final StatutContratRepository statutContratRepository;

    private final ApplicationEventPublisher eventPublisher;

    private String loginUrl;
    private final CategorieEmployeRepository categorieEmployeRepository;
    private final DepartementRepository departementRepository;
    private final PosteRepository posteRepository;
    private final NatureContratRepository natureContratRepository;
    private final ModeDePaiementRepository modeDePaiementRepository;
    private final BanqueRepository banqueRepository;



    @Transactional
    public Map<String, Object> createGlobalContrat(ContratEmployeGlobalDTO globalDto, User currentUser) {

        Map<String, Object> data = new HashMap<>();
        // 1. Sauvegarde contrat
        var contratDto = globalDto.getContratEmploye();
        contratDto.setStatusContrat(Status.CONTRAT_EN_COURS);
        contratDto.setSalaire_brut(0.0);
        contratDto.setSalaire_base(0.0);
        contratDto.setCreatedAt(LocalDateTime.now());
        contratDto.setAddedById(currentUser.getId());


        if (contratDto.getHoraireTravail() != null) contratDto.setHoraireTravail(contratDto.getHoraireTravail());
        if (contratDto.getNatureJuridiqueEmployeur() != null) contratDto.setNatureJuridiqueEmployeur(contratDto.getNatureJuridiqueEmployeur());


        // Convertir DTO en entité puis sauvegarder
        ContratEmploye savedContrat = contratRepository.save(contratDto.toEntity());
        Employe employe = savedContrat.getEmploye();
        Employe employe_ = employeService.findById1(contratDto.getEmployeId());
        Company company = savedContrat.getCompany();
        // 2. Initialiser crédit congé
        ContractEmployeDTO savedContratDto = ContractEmployeDTO.fromEntity(savedContrat);
        creditCongeService.initialiserCreditConge(savedContratDto, currentUser);
        // 3. Sauvegarde diplômes de l'employé
        if (globalDto.getDiplomes() != null) {
            for (var diplome : globalDto.getDiplomes()) {
                diplome.setEmployeId(savedContrat.getEmploye().getId());
                if (diplome.getId() == null) {
                    diplome.setAddedById(currentUser.getId());
                } else {
                    diplome.setUpdatedById(currentUser.getId());
                }
                employeDiplomeService.save(diplome);
            }
        }

        // 4. Sauvegarde rubriques du contrat
        double salaireBrut = 0;
        double salaireBase = 0;

        if (globalDto.getRubriques() != null) {
            for (ContratEmployeRubriqueDTO contratRubriqueDto : globalDto.getRubriques()) {

                // Calcul du salaire brut
                if (contratRubriqueDto.getMontant() != null) {
                    salaireBrut += contratRubriqueDto.getMontant().doubleValue();
                }

                // Identification du salaire de base (rubrique avec ID = 4 par ex.)
                if (contratRubriqueDto.getRubriqueId() == 4) {
                    salaireBase = contratRubriqueDto.getMontant().doubleValue();
                }

                // ⚠️ Correction ici → on utilise l'ID de l'entité persistée
                contratRubriqueDto.setContratEmployeId(savedContrat.getId());
                contratEmployeRubriqueService.save(
                        contratRubriqueDto,
                        currentUser,
                        savedContrat.getCompany().getId()
                );
            }
        }

        // Mise à jour des salaires calculés
        savedContrat.setSalaire_brut(salaireBrut);
        savedContrat.setSalaire_base(salaireBase);
        contratRepository.save(savedContrat);

        //***⚡⚡⚡Créaction des evenement
        System.out.println("Création des evenements");
        eventPublisher.publishEvent(
                new EmployeChangedEvent(
                        employe_,
                        savedContrat,
                        savedContrat.getCompany()
                )
        );

        // Créer le statut initial correspondant au contrat
        StatutContrat statutInitial = new StatutContrat();
        statutInitial.setContratEmploye(savedContrat);
        statutInitial.setCategorieEmploye(savedContrat.getCategorieEmploye());
        statutInitial.setTypeContrat(savedContrat.getType_contrat());
        statutInitial.setDepartement(savedContrat.getDepartement());
        statutInitial.setPoste(savedContrat.getPoste());
        statutInitial.setDateFinContrat(savedContrat.getDate_fin());
        statutInitial.setSalaireBase(savedContrat.getSalaire_base());
        statutInitial.setSalaireBrut(savedContrat.getSalaire_brut());
        statutInitial.setActif(true);          // actif dès le départ
        statutInitial.setInitialisation(true);
        statutInitial.setDateEffet(savedContrat.getDate_debut());

        statutInitial.setLieuContrat(savedContrat.getLieu_execution());
        statutInitial.setHoraireTravail(savedContrat.getHoraireTravail());
        statutInitial.setQualificationProfessionnelle(savedContrat.getDiplome_requis());
        statutInitial.setTravailAFaire(savedContrat.getMissions());


        // 🔥 Snapshot JSON du contrat initial (DTO complet du contrat + rubriques + diplômes…)
        // ici tu reconstruis ton DTO global avec toutes les infos
        ContratEmployeGlobalDTO globalDtoInitial = new ContratEmployeGlobalDTO();
        globalDtoInitial.setContratEmploye(savedContratDto);
        globalDtoInitial.setDiplomes(globalDto.getDiplomes());
        globalDtoInitial.setRubriques(globalDto.getRubriques());

        // snapshot en JSON
        String snapshot = globalDto.toJson();
        statutInitial.setSnapshotJson(snapshot);

        statutContratRepository.save(statutInitial);

        // 5. Création compte utilisateur employé
        String username = userHelper.generateUsername("user", 4);
        String rawPassword = PasswordGenerator.generateRandomPassword(8);
        Role employeRole = roleRepository.findByName("ROLE_EMPLOYEE").orElseThrow();
        Employe employeok = employeRepository.findById(savedContratDto.getEmployeId())
                .orElseThrow(() -> new IllegalStateException(
                        "Employé non trouvé avec ID : " + savedContratDto.getEmployeId()));

        Company companyOk = companyRepository.findById(savedContratDto.getCompanyId())
                .orElseThrow(() -> new IllegalStateException(
                        "Entreprise non trouvée avec ID : " + savedContratDto.getCompanyId()));

        data.put("contrat", savedContrat);
//        data.put("user", savedUser);
        return data;
    }

    @Transactional
    public Map<String, Object> updateContrat(Long id, ContratEmployeRequestDTO dto, User currentUser) {

        Map<String, Object> data = new HashMap<>();

        // 1. Récupération du contrat (⚠️ utiliser id et non dto.id())
        ContratEmploye contrat = contratRepository.findByIdWithFullRelations(id)
                .orElseThrow(() -> new RuntimeException("Contrat introuvable"));

        // 2. Associations
        CategorieEmploye categorie = categorieEmployeRepository
                .findById(dto.categorieEmployeId())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        System.out.println(categorie.getName());
        contrat.setCategorieEmploye(categorie);

        contrat.setPoste(
                posteRepository.findById(dto.posteId())
                        .orElseThrow(() -> new RuntimeException("Poste introuvable"))
        );

        contrat.setNatureContrat(
                natureContratRepository.findById(dto.natureContratId())
                        .orElseThrow(() -> new RuntimeException("Nature contrat introuvable"))
        );

        contrat.setModeDePaiement(
                modeDePaiementRepository.findById(dto.modeDePaiementId())
                        .orElseThrow(() -> new RuntimeException("Mode de paiement introuvable"))
        );

        contrat.setBanque(
                banqueRepository.findById(dto.banqueId())
                        .orElseThrow(() -> new RuntimeException("Banque introuvable"))
        );

        // 3. Champs simples
        contrat.setType_contrat(dto.typeContrat());
        contrat.setNumero_compte(dto.numeroCompte());

        contrat.setDate_debut(dto.dateDebut());
        contrat.setDate_fin(dto.dateFin());
        contrat.setDateEmbauche(dto.dateEmbauche());

        contrat.setDebut_essai(dto.debutEssai());
        contrat.setFin_essai(dto.finEssai());

        contrat.setAib_contrat_employe(dto.aibContratEmploye());
        contrat.setCaution_contrat_employe(dto.cautionContratEmploye());
        contrat.setTransfert_contrat_employe(dto.transfertContratEmploye());

        contrat.setHoraireTravail(dto.horaireTravail());
        contrat.setNatureJuridiqueEmployeur(dto.natureJuridiqueEmployeur());
        contrat.setAncienneSituation(dto.ancienneSituation());
        contrat.setNouvelleSituation(dto.nouvelleSituation());
        contrat.setCommentaire(dto.commentaire());

        contrat.setUpdatedAt(LocalDateTime.now());

        // 4. Rubriques (SANS DELETE)
        double salaireBrut = 0;
        double salaireBase = 0;

        if (dto.rubriques() != null) {

            for (var r : dto.rubriques()) {

                Double montant = r.montant() != null ? r.montant() : 0.0;

                salaireBrut += montant;

                if (r.rubriqueId() != null && r.rubriqueId() == 4) {
                    salaireBase = montant;
                }

                ContratEmployeRubriqueDTO entityDto = new ContratEmployeRubriqueDTO();
                entityDto.setContratEmployeId(contrat.getId());
                entityDto.setRubriqueId(r.rubriqueId());
                entityDto.setMontant(BigDecimal.valueOf(montant));

                // 🔥 UPDATE ou INSERT (important)
                contratEmployeRubriqueService.saveOrUpdate(
                        entityDto,
                        currentUser,
                        contrat.getCompany().getId()
                );
            }
        }

        contrat.setSalaire_brut(salaireBrut);
        contrat.setSalaire_base(salaireBase);

        // 5. Save contrat
        ContratEmploye saved = contratRepository.save(contrat);

        data.put("contrat", saved);

        return data;
    }

    @Transactional
    public ContratEmployeResponseDTO getContratById(Long id) {

        ContratEmploye contrat = contratRepository.findByIdWithFullRelations(id)
                .orElseThrow(() -> new RuntimeException("Contrat introuvable avec ID  "));
        return toFullDTO(contrat);
    }

    public static ContratEmployeResponseDTO toFullDTO(ContratEmploye e) {

        if (e == null) return null;
        List<ContratEmployeRubriqueResponseDTO> rubriques =
                e.getRubriques() != null ?
                        e.getRubriques().stream().map(r -> new ContratEmployeRubriqueResponseDTO(
                                r.getId(),
                                r.getRubrique() != null ? r.getRubrique().getId() : null,
                                r.getRubrique() != null ? r.getRubrique().getLibelle() : null,
                                r.getLibelle(),
                                r.getMontant() != null ? r.getMontant().doubleValue() : null,
                                r.getMontant_ajout() != null ? r.getMontant_ajout().doubleValue() : null
                        )).toList()
                        : null;

        //Puis construire le DTO
        return new ContratEmployeResponseDTO(

                e.getId(),

                // Employé
                e.getEmploye() != null ? e.getEmploye().getId() : null,
                e.getEmploye() != null ? e.getEmploye().getNom() : null,
                e.getEmploye() != null ? e.getEmploye().getPrenom() : null,

                // Company
                e.getCompany() != null ? e.getCompany().getId() : null,
                e.getCompany() != null ? e.getCompany().getName() : null,

                // Organisation
                e.getDepartement() != null ? e.getDepartement().getId() : null,
                e.getDepartement() != null ? e.getDepartement().getLibelle() : null,
                e.getPoste() != null ? e.getPoste().getId() : null,
                e.getPoste() != null ? e.getPoste().getLibelle() : null,
                e.getCategorieEmploye() != null ? e.getCategorieEmploye().getId() : null,
                e.getCategorieEmploye() != null ? e.getCategorieEmploye().getName() : null,

                // Contrat
                e.getNumeroContrat(),
                e.getType_contrat(),
                e.getDuree_contrat(),
                e.getMouvement_contrat(),
                e.getStatus_contrat(),

                // Nature / paiement
                e.getNatureContrat() != null ? e.getNatureContrat().getId() : null,
                e.getNatureContrat() != null ? e.getNatureContrat().getLibelle() : null,
                e.getModeDePaiement() != null ? e.getModeDePaiement().getId() : null,
                e.getModeDePaiement() != null ? e.getModeDePaiement().getLibelle() : null,
                e.getBanque() != null ? e.getBanque().getId() : null,
                e.getBanque() != null ? e.getBanque().getName() : null,
                e.getNumero_compte(),

                // Dates
                e.getDate_debut(),
                e.getDate_fin(),
                e.getDuree(),
                e.getDateEmbauche(),

                // Essai
                e.getDebut_essai(),
                e.getFin_essai(),
                e.getDuree_essai(),

                // Missions
                e.getMissions(),
                e.getDiplome_requis(),
                e.getLieu_execution(),


                e.getHoraireTravail(),
                e.getNatureJuridiqueEmployeur(),
                e.getAncienneSituation(),
                e.getNouvelleSituation(),
                e.getCommentaire(),


                // Fin contrat
                e.isArretContrat(),
                e.getDate_arret_contrat(),
                e.getMotif_arret_contrat(),

                // Financier
                e.getSalaire_brut(),
                e.getSalaire_base(),
                e.getAib_contrat_employe(),
                e.getCaution_contrat_employe(),
                e.getTransfert_contrat_employe(),



                // Calcul
                e.getAncienneteEnMois(),

                // Audit
                e.getAdded_by() != null ? e.getAdded_by().getId() : null,
                e.getAdded_by() != null ? e.getAdded_by().getFullName() : null,
                e.getCreatedAt(),
                e.getUpdatedAt(),


                // ✅ Ajout final
                rubriques
        );
    }
}
