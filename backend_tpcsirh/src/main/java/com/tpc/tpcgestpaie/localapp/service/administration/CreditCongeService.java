    package com.tpc.tpcgestpaie.localapp.service.administration;

    import com.tpc.tpcgestpaie.localapp.dto.administration.CreditCongeDTO;
    import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
    import com.tpc.tpcgestpaie.localapp.helper.CreditCongeHelper;
    import com.tpc.tpcgestpaie.localapp.model.Company;
    import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
    import com.tpc.tpcgestpaie.localapp.model.CreditConge;
    import com.tpc.tpcgestpaie.localapp.model.Employe;
    import com.tpc.tpcgestpaie.localapp.model.User;
    import com.tpc.tpcgestpaie.localapp.repository.administration.CreditCongeRepository;
    import com.tpc.tpcgestpaie.localapp.service.EmployeService;
    import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
    import jakarta.transaction.Transactional;
    import org.springframework.stereotype.Service;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;

    @Service
    public class CreditCongeService {

        private final CreditCongeRepository repository;

        private final CreditCongeRepository creditCongeRepository;
        private final CreditCongeHelper creditCongeHelper;
        private final EmployeService employeService;

        public CreditCongeService(CreditCongeRepository repository, CreditCongeRepository creditCongeRepository, CreditCongeHelper creditCongeHelper, EmployeService employeService) {
            this.repository = repository;
            this.creditCongeRepository = creditCongeRepository;
            this.creditCongeHelper = creditCongeHelper;
            this.employeService = employeService;
        }

        public List<CreditCongeDTO> getAll() {
            return creditCongeRepository.findAll()
                    .stream()
                    .map(CreditCongeDTO::fromEntity)
                    .collect(Collectors.toList());
        }

        public Optional<CreditCongeDTO> getById(Long id) {
            return creditCongeRepository.findById(id)
                    .map(CreditCongeDTO::fromEntity);
        }

        public CreditCongeDTO save(CreditCongeDTO dto,
                                   ContratEmploye contratEmploye,
                                   Employe employe,
                                   Company company,
                                   User addedBy) {
            CreditConge entity = dto.toEntity(contratEmploye, employe, company, addedBy);
            CreditConge saved = creditCongeRepository.save(entity);
            return CreditCongeDTO.fromEntity(saved);
        }


        public CreditConge updateCreditConge(CreditConge creditConge) {
            return creditCongeRepository.save(creditConge);
        }

        public List<CreditCongeDTO> findByCompanyId(Long companyId) {
            return creditCongeRepository.findByCompanyId(companyId)
                    .stream()
                    .map(CreditCongeDTO::fromEntity)
                    .collect(Collectors.toList());
        }

        public List<CreditCongeDTO> findByEmployeId(Long employeId) {
            return creditCongeRepository.findByEmployeId(employeId)
                    .stream()
                    .map(CreditCongeDTO::fromEntity)
                    .collect(Collectors.toList());
        }

        public void delete(Long id) {
            creditCongeRepository.deleteById(id);
        }

        public boolean exists(Long id) {
            return creditCongeRepository.existsById(id);
        }

        // Tu peux ajouter d'autres méthodes spécifiques au besoin, par exemple :

        public List<CreditCongeDTO> findByDateReferenceBetween(LocalDate startDate, LocalDate endDate) {
            return creditCongeRepository.findByDateReferenceBetween(startDate, endDate)
                    .stream()
                    .map(CreditCongeDTO::fromEntity)
                    .collect(Collectors.toList());
        }

        public CreditConge getCreditCongeActifByIdEmploye(Long idEmploye) {
            Optional<CreditConge> result = creditCongeRepository.findActifByEmployeId(idEmploye);
            return result.orElse(null);
        }
        @Transactional
        public CreditConge initialiserCreditConge(ContractEmployeDTO savedContrat, User currentUser) {
            // Vérifier que l'ID du contrat est présent
            if (savedContrat.getId() == null) {
                throw new IllegalArgumentException("Le contrat employé est obligatoire !");
            }

            // Vérifier que l'employé existe
            Optional<Employe> employeOpt = employeService.findById(savedContrat.getEmployeId());
            if (employeOpt.isEmpty()) {
                throw new IllegalArgumentException("Employé non trouvé avec ID : " + savedContrat.getEmployeId());
            }
            Employe employe = employeOpt.get();

            // Vérifier que la société est définie
//            Company company = savedContrat.getCompany();
            Company company = employe.getCompany();
            if (company == null) {
                throw new IllegalArgumentException("L'entreprise du contrat est obligatoire !");
            }

            // Transformer le DTO en entité ContratEmploye
            ContratEmploye contratEntity = savedContrat.toEntity();

            // Clôturer l’ancien crédit actif (s’il existe)
            CreditConge creditActif = getCreditCongeActifByIdEmploye(employe.getId());
            if (creditActif != null) {
                creditActif.setStatut("CLOTURE");
                creditActif.setUpdated_at(LocalDateTime.now());
                creditCongeRepository.save(creditActif);
            }

            // Créer un nouveau crédit congé
            CreditCongeDTO creditDto = new CreditCongeDTO();
            creditDto.setContratEmployeId(savedContrat.getId());
            creditDto.setDateReference(savedContrat.getDateDebut());
            creditDto.setStatut("ACTIF");
            creditDto.setCreatedAt(LocalDateTime.now());
            creditDto.setAddedById(currentUser.getId());

            CreditConge entity = creditDto.toEntity(contratEntity, employe, company, currentUser);

            // Validation du crédit congé
            List<ErrorResponse> validationErrors = creditCongeHelper.getInvalidFieldMessages(entity);
            if (!validationErrors.isEmpty()) {
                validationErrors.forEach(err ->
                        System.out.println("Champ: " + err.getField() + " -> " + err.getMessage())
                );
                throw new IllegalArgumentException("Crédit Congé invalide !");
            }

            // Sauvegarder le crédit congé
            return creditCongeRepository.save(entity);
        }

//        @Transactional
//        public CreditConge initialiserCreditConge(ContractEmployeDTO savedContrat, User currentUser) {
//            Employe employe = savedContrat.getEmploye();
//            Company company = savedContrat.getCompany();
//            ContratEmploye contratEntity = savedContrat.toEntity();
//
//            // Clôturer l’ancien crédit actif (s’il existe)
//
//            Optional <Employe> employe1 = employeService.findById(savedContrat.getEmployeId());
//
//            if (!employe1.isPresent()) {
//                Employe employe2 = employe1.get();
//                System.out.println("Email2 : " + employe2.getEmail());
//            }
//
//            CreditConge creditActif = getCreditCongeActifByIdEmploye(employe1.get().getId());
//
//
//            if (creditActif != null) {
//                creditActif.setStatut("CLOTURE");
//                creditActif.setUpdated_at(LocalDateTime.now());
//                creditCongeRepository.save(creditActif);
//            }
//
//            // Créer un nouveau crédit congé
//            CreditCongeDTO creditDto = new CreditCongeDTO();
//            creditDto.setContratEmployeId(savedContrat.getId());
//            creditDto.setDateReference(savedContrat.getDateDebut());
//            creditDto.setStatut("ACTIF");
//            creditDto.setCreatedAt(LocalDateTime.now());
//            creditDto.setAddedById(currentUser.getId());
//
//            CreditConge entity = creditDto.toEntity(contratEntity, employe, company, currentUser);
//
//            // Validation
//            List<ErrorResponse> validationErrors = creditCongeHelper.getInvalidFieldMessages(entity);
//            if (!validationErrors.isEmpty()) {
//                throw new IllegalArgumentException("Erreurs de validation Crédit Congé : " + validationErrors);
//            }
//
//            return creditCongeRepository.save(entity);
//        }

        /**
         * Initialiser un nouveau crédit de congés (cas d’un contrat sans continuité)
         */
        @Transactional
        public void initNouveauCredit(ContratEmploye contrat, Long currentUserId) {
            CreditConge nouveauCredit = new CreditConge();
            nouveauCredit.setContratEmploye(contrat);
            nouveauCredit.setDateReference(contrat.getDate_debut());
            nouveauCredit.setStatut("ACTIF");
            nouveauCredit.setCreated_at(LocalDateTime.now());

            creditCongeRepository.save(nouveauCredit);
        }

    }
