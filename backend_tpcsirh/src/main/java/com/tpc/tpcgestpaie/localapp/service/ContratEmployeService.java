package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.contrat.ContratEmployeGlobalDTO;
import com.tpc.tpcgestpaie.localapp.dto.contrat.RubriqueDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeContratActifDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDiplomeDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.ContratEmployeRubriqueDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.SoldeCongeResponseDTO;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.*;
import com.tpc.tpcgestpaie.localapp.service.administration.AbsenceService;
import com.tpc.tpcgestpaie.localapp.service.administration.CreditCongeService;
//import com.tpc.tpcgestpaie.localapp.service.paie.ProvisionCongeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContratEmployeService {

    private final CreditCongeService  creditCongeService;
    private final AbsenceService  absenceService;
    private final ContratEmployeRepository contratEmployeRepository;
    private final EmployeService employeService;
    private final MotifArretContratRepository motifArretContratRepository;
    private final EmployeDiplomeService employeDiplomeService;
//    private final ProvisionCongeService provisionCongeService;
    private final EmployeRepository employeRepository;
    private final PosteRepository posteRepository;
    private final DepartementRepository departementRepository;
    private final CategorieEmployeRepository categorieEmployeRepository;
    private final ModeDePaiementRepository modeDePaiementRepository;
    private final BanqueRepository banqueRepository;
    private final RubriqueRepository rubriqueRepository;
    private final ContratEmployeRubriqueRepository contratEmployeRubriqueRepository;

    public ContratEmployeService(CreditCongeService creditCongeService, AbsenceService absenceService, ContratEmployeRepository contratEmployeRepository, EmployeService employeService, MotifArretContratRepository motifArretContratRepository, EmployeDiplomeService employeDiplomeService, EmployeRepository employeRepository, PosteRepository posteRepository, DepartementRepository departementRepository, CategorieEmployeRepository categorieEmployeRepository, ModeDePaiementRepository modeDePaiementRepository, BanqueRepository banqueRepository, RubriqueRepository rubriqueRepository, ContratEmployeRubriqueRepository contratEmployeRubriqueRepository) {
        this.creditCongeService = creditCongeService;
        this.absenceService = absenceService;
        this.contratEmployeRepository = contratEmployeRepository;
        this.employeService = employeService;
        this.motifArretContratRepository = motifArretContratRepository;
        this.employeDiplomeService = employeDiplomeService;
//        this.provisionCongeService = provisionCongeService;
        this.employeRepository = employeRepository;
        this.posteRepository = posteRepository;
        this.departementRepository = departementRepository;
        this.categorieEmployeRepository = categorieEmployeRepository;
        this.modeDePaiementRepository = modeDePaiementRepository;
        this.banqueRepository = banqueRepository;
        this.rubriqueRepository = rubriqueRepository;
        this.contratEmployeRubriqueRepository = contratEmployeRubriqueRepository;
    }

    // Scheduler qui s'exécute tous les jours à minuit
    // @Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(cron = "0/30 * * * * ?") // toutes les 30 secondes pour test
    public void checkContratsExpire() {
        LocalDate today = LocalDate.now();
        List<ContratEmploye> contrats = contratEmployeRepository.findAllActiveCddWithDateFin("CDD");
        for (ContratEmploye contrat : contrats) {
            if (!contrat.isArretContrat() && contrat.getDate_fin().isBefore(today.plusDays(1))) {
                // Motif FIN CDD
                String motifFinCDD = "FIN CDD";
                contrat.setArretContrat(true);
                contrat.setMotif_arret_contrat(motifFinCDD);
                contrat.setDate_arret_contrat(contrat.getDate_fin());
                contrat.setStatus_contrat("CONTRAT EXPIRE");
                contrat.setUpdatedAt(LocalDateTime.now());
                contratEmployeRepository.save(contrat);
            }
        }
    }
    // AJOUT: Méthodes paginées
    public Page<ContractEmployeDTO> getAllPaginated(Pageable pageable) {
        return contratEmployeRepository.findAllWithJoins(pageable)
                .map(ContractEmployeDTO::fromEntity);
    }

    public Page<ContractEmployeDTO> getAllContratEmployeParEntreprisePaginated(Long companyId, Pageable pageable) {
        return contratEmployeRepository.findByCompanyIdPaginated(companyId, pageable)
                .map(ContractEmployeDTO::fromEntity);
    }

    public Page<ContractEmployeDTO> getAllContratEmployeParEntreprisePaginated(
            Long companyId,
            String search,
            Pageable pageable
    ) {
        return contratEmployeRepository
                .searchByCompanyId(companyId, search.toLowerCase(), pageable)
                .map(ContractEmployeDTO::fromEntity);
    }


    public Page<ContractEmployeDTO> getAllContratEmployeEnCoursDeValiditePaginated(Pageable pageable) {
        return contratEmployeRepository.findAllContratEmployeEnCoursDeValiditePaginated(pageable)
                .map(ContractEmployeDTO::fromEntity);
    }

    @Transactional
    public Page<ContractEmployeDTO> getAllContratEmployeEnCoursDeValiditeParEntreprisePaginated(Long companyId, Pageable pageable) {
        return contratEmployeRepository.findAllContratEmployeEnCoursDeValiditeParEntreprisePaginated(companyId, pageable)
                .map(ContractEmployeDTO::fromEntity);
    }

    public Page<ContractEmployeDTO> getAllContratEmployeNonArretePaginated(Pageable pageable) {
        return contratEmployeRepository.findAllContratEmployeNonArretePaginated(pageable)
                .map(ContractEmployeDTO::fromEntity);
    }

    public Page<ContractEmployeDTO> getAllContratEmployeNonArreteParEntreprisePaginated(Long companyId, Pageable pageable) {
        return contratEmployeRepository.findAllContratEmployeNonArreteByEntreprisePaginated(companyId, pageable)
                .map(ContractEmployeDTO::fromEntity);
    }

    public Page<ContractEmployeDTO> getContratsByEmployeIdPaginated(Long employeId, Pageable pageable) {
        return contratEmployeRepository.findAllByEmployeWithAllJoinsOrderedPaginated(employeId, pageable)
                .map(ContractEmployeDTO::fromEntity);
    }

    public Page<EmployeContratActifDTO> getEmployesAvecContratActifParEntreprisePaginated(Long companyId, Pageable pageable) {
        return contratEmployeRepository.findEmployesAvecContratActifParEntreprisePaginated(companyId, pageable);
    }

    public List<ContractEmployeDTO> getAll() {
        return contratEmployeRepository.findAll()
                .stream()
                .map(entity -> new ContractEmployeDTO().fromEntity(entity))
                .collect(Collectors.toList());
    }

    public boolean existsById(Long id) {
        return contratEmployeRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public Optional<ContractEmployeDTO> getById(Long id) {
        return contratEmployeRepository.findById(id)
                .map(ContractEmployeDTO::fromEntity); // 🔥 appel direct au mapper
    }


    public Optional<ContratEmploye> findEntityById(Long id) {
        return contratEmployeRepository.findById(id);
    }

    @Transactional
    public ContractEmployeDTO save(ContractEmployeDTO dto) {
        ContratEmploye entity = dto.toEntity();
        ContratEmploye saved = contratEmployeRepository.save(entity);
        return new ContractEmployeDTO().fromEntity(saved);
    }

    public Optional<ContractEmployeDTO> getActifDTOByEmployeId(Long employeId) {
        return contratEmployeRepository.findActifByEmployeId(employeId)
                .map(ContractEmployeDTO::fromEntity);
    }

    public Optional<ContractEmployeDTO> getActifDTOByEmployeIdMois(Long employeId, LocalDate mois) {
        return contratEmployeRepository.findActifByEmployeIdMois(employeId, mois)
                .map(ContractEmployeDTO::fromEntity);
    }


    public Optional<ContractEmployeDTO> getActifByEmployeIdAndCompanyId(Long employeId,Long companyId) {
        return contratEmployeRepository.findActifByEmployeIdAndCompanyId(employeId,companyId)
                .map(ContractEmployeDTO::fromEntity);
    }

    //liste des contrats par entreprise
    public List<ContractEmployeDTO> getAllContratEmployeParEntreprise(Long companyId) {
        return contratEmployeRepository.findByCompanyId(companyId)
                .stream()
                .map(entity -> new ContractEmployeDTO().fromEntity(entity))
                .collect(Collectors.toList());
    }

    // liste des contrats valide ou en actif
    public List<ContractEmployeDTO> getAllContratEmployeEnCoursDeValidite() {
        List<ContratEmploye> contrats = contratEmployeRepository.findAllContratEmployeEnCoursDeValidite();
        return contrats.stream()
                .map(contrat -> new ContractEmployeDTO().fromEntity(contrat))
                .collect(Collectors.toList());
    }

    // liste des contrats en cous de validite par entreprise

    public List<ContractEmployeDTO> getAllContratEmployeEnCoursDeValiditeParEntreprise(Long companyId) {
        return contratEmployeRepository.findAllContratEmployeEnCoursDeValiditeParEntreprise(companyId)
                .stream()
                .map(entity -> new ContractEmployeDTO().fromEntity(entity))
                .collect(Collectors.toList());
    }

    public List<ContractEmployeDTO> getAllContratEmployeEnCoursDeValiditeParEntrepriseMois(Long companyId, LocalDate mois) {
        return contratEmployeRepository.findContratsPourCalculSalaireParMois(companyId,mois)
                .stream()
                .map(entity -> new ContractEmployeDTO().fromEntity(entity))
                .collect(Collectors.toList());
    }


    public List<ContractEmployeDTO> getAllContratEmployeEnCoursDeValiditeParEntrepriseMoIS(Long companyId) {
        return contratEmployeRepository.findAllContratEmployeEnCoursDeValiditeParEntreprise(companyId)
                .stream()
                .map(entity -> new ContractEmployeDTO().fromEntity(entity))
                .collect(Collectors.toList());
    }
    // Liste des contrats non arretés

    @Transactional
    public List<ContractEmployeDTO> getAllContratEmployeNonArrete() {
        return contratEmployeRepository.findAllContratEmployeNonArrete()
                .stream()
                .map(entity -> new ContractEmployeDTO().fromEntity(entity))
                .collect(Collectors.toList());
    }
    // liste des contrats non arrêtés par entreprise

    public List<ContractEmployeDTO> getAllContratEmployeNonArreteParEntreprise(Long companyId) {
        return contratEmployeRepository.findAllContratEmployeNonArreteByEntreprise(companyId)
                .stream()
                .map(entity -> new ContractEmployeDTO().fromEntity(entity))
                .collect(Collectors.toList());
    }

    public List<ContractEmployeDTO> getAllContratEmployeNonArreteParEntrepriseEtParDepartement(Long companyId, Long departementId) {
        return contratEmployeRepository.findAllContratEmployeNonArreteByEntrepriseAndDepartement(companyId, departementId)
                .stream()
                .map(entity -> new ContractEmployeDTO().fromEntity(entity))
                .collect(Collectors.toList());
    }
    public List<ContractEmployeDTO> getAllContratEmployeNonArreteParPeriode(LocalDateTime dateDebut, LocalDateTime dateFin) {
        return contratEmployeRepository.findAllContratEmployeNonArreteByPeriode(dateDebut, dateFin)
                .stream()
                .map(entity -> new ContractEmployeDTO().fromEntity(entity))
                .collect(Collectors.toList());
    }
    public List<ContractEmployeDTO> getAllContratEmployeNonArreteParPeriodePourFinPeriodeEssai(LocalDateTime dateDebut, LocalDateTime
            dateFin) {
        return contratEmployeRepository.findAllContratEmployeNonArreteFinPeriodeEssai(dateDebut, dateFin)
                .stream()
                .map(entity -> new ContractEmployeDTO().fromEntity(entity))
                .collect(Collectors.toList());
    }

    public Long getCountAllContratEmployeArreteParPeriodeEtParEntreprise(Company company, LocalDateTime dateDebut, LocalDateTime dateFin) {
        return contratEmployeRepository.countContratEmployeArreteParPeriodeEtParEntreprise(company, dateDebut, dateFin);
    }

    @Transactional
    public List<ContractEmployeDTO> getContratEmployeNonArreteParEntrepriseParDepartementEtParEmploye(
            Company company, Departement departement, Employe employe) {
        List<ContratEmploye> contrats = contratEmployeRepository.findAllContratEmployeNonArreteByEntrepriseDepartementEtEmploye(company, departement, employe);
        return contrats.stream()
                .map(c -> new ContractEmployeDTO().fromEntity(c))
                .collect(Collectors.toList());
    }

    public Optional<ContractEmployeDTO> getContratEmployeNonArreteParEmploye(Employe employe) {
        return contratEmployeRepository.findAllContratEmployeNonArreteByEmploye(employe).stream()
                .findFirst()
                .map(contrat -> new ContractEmployeDTO().fromEntity(contrat));
    }

    public List<ContratEmploye> findAllContratEmployeNonArreteByEmploye(Employe employe) {
        return contratEmployeRepository.findAllContratEmployeNonArreteByEmployeId(employe.getId());
    }

        // AJOUT: Pagination pour les contrats non arrêtés par entreprise et département
    public Page<ContractEmployeDTO> getAllContratEmployeNonArreteParEntrepriseEtParDepartementPaginated(
            Long companyId, Long departementId, Pageable pageable) {
        return contratEmployeRepository.findAllContratEmployeNonArreteByEntrepriseAndDepartementPaginated(companyId, departementId, pageable)
                .map(ContractEmployeDTO::fromEntity);
    }


    @Transactional(readOnly = true)
    public List<ContractEmployeDTO> getContratEmployeParEmploye(Employe employe) {
        List<ContratEmploye> contrats = contratEmployeRepository.findActiveByEmployeWithAllJoinsOrdered(employe);
        return contrats.stream()
                .map(contrat -> {
                    new ContractEmployeDTO();
                    return ContractEmployeDTO.fromEntity(contrat);
                })
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        contratEmployeRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        return contratEmployeRepository.existsById(id);
    }

    public Long getNombreContratEmployeEnCoursDeValiditeParEntreprise(Company company) {
        return contratEmployeRepository.countContratEmployeEnCoursDeValiditeParEntreprise(company);
    }

    public List<ContractEmployeDTO> getByEmploye(Employe employe) {
        List<ContratEmploye> contrats = contratEmployeRepository.findByEmploye(employe);
        return contrats.stream()
                .map(c -> new ContractEmployeDTO().fromEntity(c))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<EmployeContratActifDTO> getEmployesAvecContratActifParEntreprise(Long companyId) {
        return contratEmployeRepository
                .findAllContratEmployeEnCoursDeValiditeParEntreprise(companyId)
                .stream()
                .map(EmployeContratActifDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<ContratEmploye> findById(Long contratEmployeId) {
        return contratEmployeRepository.findById(contratEmployeId);
    }

    public Optional<ContratEmploye> getContratWithRubriques(Long contratId) {
        return contratEmployeRepository.findByIdWithRubriques(contratId);
    }

    public List<ContratEmployeRubrique> getRubriquesByContratId(Long contratId) {
        return contratEmployeRepository.findByIdWithRubriques(contratId)
                .map(ContratEmploye::getRubriques)
                .orElse(Collections.emptyList());
    }

    public List<RubriqueDTO> getRubriquesDtoByContratId(Long contratId) {
        return contratEmployeRepository.findByIdWithRubriques(contratId)
                .map(contrat -> contrat.getRubriques().stream()
                        .map(r -> new RubriqueDTO(r.getId(), r.getLibelle(), r.getMontant()))
                        .collect(Collectors.toList())
                )
                .orElse(Collections.emptyList());
    }


    public List<SoldeCongeResponseDTO> calculerSoldeCongeParEntreprise(Long idEntreprise) {
        List<ContractEmployeDTO> contratsActifs = this.getAllContratEmployeEnCoursDeValiditeParEntreprise(idEntreprise);
        return contratsActifs.stream()
                .filter(contrat -> contrat.getCompanyId().equals(idEntreprise))
                .map(contrat -> {
                    Long employeId = contrat.getEmployeId();
                    Long contratId = contrat.getId();

                    CreditConge dernierCredit = creditCongeService.getCreditCongeActifByIdEmploye(employeId);

                    LocalDate dateReference = (dernierCredit != null)
                            ? dernierCredit.getDateReference()
                            : LocalDate.now();

                    int credit = (dernierCredit != null)
                            ? absenceService.calculateCreditConge(dateReference)
                            : 0;

                    int totalPris = absenceService.getDureeTotalAbsenceByIdContratEmploye(contratId);

                    SoldeCongeResponseDTO dto = new SoldeCongeResponseDTO();
                    Optional<Employe> optionalEmploye = employeService.findById(employeId);
                    if (optionalEmploye.isPresent()) {
                        dto.setEmploye(convertEmployeToDTO(optionalEmploye.get()));
                    }
                    dto.setDateReferenceCreditConge(dateReference.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    dto.setCreditConge(credit);
                    dto.setNombreTotalJourPris(totalPris);
                    dto.setSoldeConge(credit - totalPris);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    private static EmployeDTO convertEmployeToDTO(Employe employe) {
        if (employe == null) return null;

        EmployeDTO dto = new EmployeDTO();
        dto.setId(employe.getId());
        dto.setNom(employe.getNom());
        dto.setPrenom(employe.getPrenom());
        return dto;
    }

    @Transactional
    public ContratEmploye arreterContrat(Long contratId, Long motifId) {
        ContratEmploye contrat = contratEmployeRepository.findById(contratId)
                .orElseThrow(() -> new RuntimeException("Contrat introuvable avec ID : " + contratId));

        MotifArretContrat motif = motifArretContratRepository.findById(motifId)
                .orElseThrow(() -> new RuntimeException("Motif introuvable avec ID : " + motifId));

        contrat.setArretContrat(true);
        contrat.setDate_arret_contrat(LocalDate.now());
        contrat.setMotif_arret_contrat(motif.getLibelle().toUpperCase()); // on enregistre le libellé
        contrat.setStatus_contrat("CONTRAT SUSPENDU");
        contrat.setUpdatedAt(LocalDateTime.now());

        return contratEmployeRepository.save(contrat);
    }

    @Transactional
    public ContratEmployeGlobalDTO preparerRenouvellement(Long contratId) throws Exception {
        ContratEmploye ancienContrat = contratEmployeRepository.findById(contratId)
                .orElseThrow(() -> new Exception("Contrat introuvable"));

        // Créer un nouveau DTO pré-rempli
        ContractEmployeDTO contratDTO = new ContractEmployeDTO();
        contratDTO.setEmployeId(ancienContrat.getEmploye().getId());
        contratDTO.setPosteId(ancienContrat.getPoste().getId());
        contratDTO.setDepartementId(ancienContrat.getDepartement().getId());
        contratDTO.setCategorieEmployeId(ancienContrat.getCategorieEmploye().getId());
        contratDTO.setTypeContrat(ancienContrat.getType_contrat());

     contratDTO.setModeDePaiement(ancienContrat.getModeDePaiement());
        contratDTO.setBanqueId(ancienContrat.getBanque().getId());
        contratDTO.setNumeroCompte(ancienContrat.getNumero_compte());
        contratDTO.setDateDebut(ancienContrat.getDate_debut());
        contratDTO.setDateFin(ancienContrat.getDate_fin());
        contratDTO.setSalaire_brut(ancienContrat.getSalaire_brut());
        contratDTO.setSalaire_base(ancienContrat.getSalaire_base());
        // Ignorer arretContrat, motif_arret_contrat, status_contrat

        // Copier rubriques et diplômes
        List<ContratEmployeRubriqueDTO> rubriquesDTO = ancienContrat.getRubriques().stream()
                .map(r -> ContratEmployeRubriqueDTO.fromEntity(r))
                .collect(Collectors.toList());

        List<EmployeDiplomeDTO> diplomesDTO = employeDiplomeService
                .getAllByEmploye(ancienContrat.getEmploye().getId());


        ContratEmployeGlobalDTO globalDTO = new ContratEmployeGlobalDTO();
        globalDTO.setContratEmploye(contratDTO);
        globalDTO.setRubriques(rubriquesDTO);
        globalDTO.setDiplomes(diplomesDTO);

        return globalDTO;
    }

    @Transactional
    public ContratEmploye validerRenouvellement(ContratEmployeGlobalDTO globalDTO) throws Exception {
        ContractEmployeDTO dto = globalDTO.getContratEmploye();

        // 1. Création du nouveau contrat
        ContratEmploye newContrat = new ContratEmploye();
        newContrat.setEmploye(employeRepository.findById(dto.getEmployeId())
                .orElseThrow(() -> new Exception("Employé introuvable")));
        newContrat.setPoste(posteRepository.findById(dto.getPosteId()).orElse(null));
        newContrat.setDepartement(departementRepository.findById(dto.getDepartementId()).orElse(null));
        newContrat.setCategorieEmploye(categorieEmployeRepository.findById(dto.getCategorieEmployeId()).orElse(null));
        newContrat.setType_contrat(dto.getTypeContrat());
        newContrat.setModeDePaiement(modeDePaiementRepository.findById(dto.getModeDePaiementId()).orElse(null));
        newContrat.setBanque(banqueRepository.findById(dto.getBanqueId()).orElse(null));
        newContrat.setNumero_compte(dto.getNumeroCompte());
        newContrat.setDate_debut(dto.getDateDebut());
        newContrat.setDate_fin(dto.getDateFin());
        newContrat.setSalaire_brut(dto.getSalaire_brut());
        newContrat.setSalaire_base(dto.getSalaire_base());
        newContrat.setStatus_contrat("CONTRAT EN COURS");

        ContratEmploye savedContrat = contratEmployeRepository.save(newContrat);

        // 2. Reprendre les rubriques
        ContratEmploye dernierContrat = contratEmployeRepository.findLastByEmployeId(dto.getEmployeId());

        if (globalDTO.getRubriques() != null && !globalDTO.getRubriques().isEmpty()) {
            // Rubriques saisies manuellement
            for (ContratEmployeRubriqueDTO r : globalDTO.getRubriques()) {
                ContratEmployeRubrique rub = new ContratEmployeRubrique();
                rub.setContratEmploye(savedContrat);
                rub.setRubrique(rubriqueRepository.findById(r.getRubriqueId())
                        .orElseThrow(() -> new Exception("Rubrique introuvable")));
                rub.setMontant(r.getMontant());
                contratEmployeRubriqueRepository.save(rub);
            }
        } else if (dernierContrat != null) {
            // Copier les rubriques du dernier contrat
            List<ContratEmployeRubrique> anciennesRubriques =
                    contratEmployeRubriqueRepository.findByContratEmployeId(dernierContrat.getId());
            for (ContratEmployeRubrique ancienne : anciennesRubriques) {
                ContratEmployeRubrique rub = new ContratEmployeRubrique();
                rub.setContratEmploye(savedContrat);
                rub.setRubrique(ancienne.getRubrique());
                rub.setMontant(ancienne.getMontant());
                contratEmployeRubriqueRepository.save(rub);
            }
        }
        // 3. Diplômes → déjà liés à l’employé, donc rien à faire
        // 4. Crédit congé
//        if (dernierContrat != null
//                && dernierContrat.getDate_fin() != null
//                && dernierContrat.getDate_fin().plusDays(1).isEqual(savedContrat.getDate_debut())) {
//            creditCongeService.renouvelerAvecCredit(dernierContrat, savedContrat, currentUser.getId());
//        } else {
//            creditCongeService.initNouveauCredit(savedContrat, currentUser.getId());
//        }

        return savedContrat;
    }

    @Transactional
    public List<ContractEmployeDTO> getContratsByEmployeId(Long employeId) {
        try {
            List<ContratEmploye> contrats = contratEmployeRepository.findAllByEmployeWithAllJoinsOrdered(employeId);
            return contrats.stream()
                    .map(ContractEmployeDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }


    // ==================== CONTRATS ARRÊTÉS ====================

    public Page<ContractEmployeDTO> getAllContratsArretesPaginated(Pageable pageable) {
        return contratEmployeRepository.findAllContratsArretesPaginated(pageable)
                .map(ContractEmployeDTO::fromEntity);
    }

    public Page<ContractEmployeDTO> getContratsArretesParEntreprisePaginated(Long companyId, Pageable pageable) {
        return contratEmployeRepository.findContratsArretesParEntreprisePaginated(companyId, pageable)
                .map(ContractEmployeDTO::fromEntity);
    }

// ==================== CONTRATS EXPIRÉS ====================

    public Page<ContractEmployeDTO> getAllContratsExpiresPaginated(Pageable pageable) {
        return contratEmployeRepository.findAllContratsExpiresPaginated(pageable)
                .map(ContractEmployeDTO::fromEntity);
    }

    public Page<ContractEmployeDTO> getContratsExpiresParEntreprisePaginated(Long companyId, Pageable pageable) {
        return contratEmployeRepository.findContratsExpiresParEntreprisePaginated(companyId, pageable)
                .map(ContractEmployeDTO::fromEntity);
    }

// ==================== CONTRATS ARRÊTÉS OU EXPIRÉS ====================

    public Page<ContractEmployeDTO> getContratsArretesOuExpiresPaginated(Pageable pageable) {
        return contratEmployeRepository.findContratsArretesOuExpiresPaginated(pageable)
                .map(ContractEmployeDTO::fromEntity);
    }

    public Page<ContractEmployeDTO> getContratsArretesOuExpiresParEntreprisePaginated(Long companyId, Pageable pageable) {
        return contratEmployeRepository.findContratsArretesOuExpiresParEntreprisePaginated(companyId, pageable)
                .map(ContractEmployeDTO::fromEntity);
    }

}
