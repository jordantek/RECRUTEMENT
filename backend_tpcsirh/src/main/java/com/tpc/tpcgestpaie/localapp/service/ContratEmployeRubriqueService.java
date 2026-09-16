    package com.tpc.tpcgestpaie.localapp.service;

    import com.tpc.tpcgestpaie.localapp.dto.MontantParContratDTO;
    import com.tpc.tpcgestpaie.localapp.dto.RubriqueMontantDTO;
    import com.tpc.tpcgestpaie.localapp.dto.paie.ContratEmployeRubriqueDTO;
    import com.tpc.tpcgestpaie.localapp.helper.RequestHelper;
    import com.tpc.tpcgestpaie.localapp.model.*;
    import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
    import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRubriqueRepository;
    import com.tpc.tpcgestpaie.localapp.repository.RubriqueRepository;
    import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
    import jakarta.persistence.EntityNotFoundException;
    import jakarta.transaction.Transactional;
    import org.springframework.stereotype.Service;

    import java.math.BigDecimal;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;

    @Service
    public class ContratEmployeRubriqueService {

        private final ContratEmployeRubriqueRepository rubriqueRepository;
        private final ContratEmployeRepository contratEmployeRepository;
        private final RubriqueRepository rubriqueRefRepository;
        private final UserRepository userRepository;
        private final NotificationService notificationService;
        private final AuditLogService auditLogService;
        private final RequestHelper requestHelper;
        private final CompanyService companyService;

        public ContratEmployeRubriqueService(
                ContratEmployeRubriqueRepository rubriqueRepository,
                ContratEmployeRepository contratEmployeRepository,
                RubriqueRepository rubriqueRefRepository,
                UserRepository userRepository,
                NotificationService notificationService,
                AuditLogService auditLogService,
                RequestHelper requestHelper,
                CompanyService companyService) {
            this.rubriqueRepository = rubriqueRepository;
            this.contratEmployeRepository = contratEmployeRepository;
            this.rubriqueRefRepository = rubriqueRefRepository;
            this.userRepository = userRepository;
            this.notificationService = notificationService;
            this.auditLogService = auditLogService;
            this.requestHelper = requestHelper;
            this.companyService = companyService;
        }


        // 🔹 Create
        public ContratEmployeRubrique create(ContratEmployeRubrique rubrique) {
            return rubriqueRepository.save(rubrique);
        }

        // 🔹 Read - Get all
        public List<ContratEmployeRubrique> getAll() {
            return rubriqueRepository.findAll();
        }

        // 🔹 Read - Get by ID
        public ContratEmployeRubrique getBy_Id(Long id) {
            return rubriqueRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Rubrique de contrat employé non trouvée avec ID : " + id));
        }

        // 🔹 Update
        public ContratEmployeRubrique update(Long id, ContratEmployeRubrique rubriqueUpdated) {
            ContratEmployeRubrique rubrique = getBy_Id(id);
            rubrique.setLibelle(rubriqueUpdated.getLibelle());
            rubrique.setMontant(rubriqueUpdated.getMontant());
            rubrique.setMontant_ajout(rubriqueUpdated.getMontant_ajout());
            rubrique.setDateDebut(rubriqueUpdated.getDateDebut());
            rubrique.setDateFin(rubriqueUpdated.getDateFin());
            rubrique.setStatut(rubriqueUpdated.getStatut());
            rubrique.setRubrique(rubriqueUpdated.getRubrique());
            rubrique.setContratEmploye(rubriqueUpdated.getContratEmploye());
            rubrique.setCompany(rubriqueUpdated.getCompany());
            rubrique.setAdded_by(rubriqueUpdated.getAdded_by());

            return rubriqueRepository.save(rubrique);
        }

        public ContratEmployeRubrique getByContratEmployeIdCompanyIdRubriqueId(Long contratEmployeId, Long rubriqueId, Long companyId) {
            return rubriqueRepository.findByContratEmployeIdAndRubriqueIdAndCompanyId(contratEmployeId, rubriqueId, companyId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Aucune rubrique trouvée avec contratEmployeId=" + contratEmployeId +
                                    ", rubriqueId=" + rubriqueId + ", companyId=" + companyId
                    ));
        }


        public List<ContratEmployeRubriqueDTO> getAllRubriques() {
            return rubriqueRepository.findAll().stream()
                    .map(ContratEmployeRubriqueDTO::fromEntity)
                    .collect(Collectors.toList());
        }

        public ContratEmployeRubriqueDTO getById(Long id) {
            return rubriqueRepository.findById(id)
                    .map(ContratEmployeRubriqueDTO::fromEntity)
                    .orElseThrow(() -> new EntityNotFoundException("ContratEmployeRubrique introuvable"));
        }

        public List<ContratEmployeRubriqueDTO> getByCompanyId(Long id){
            return rubriqueRepository.findByCompanyId(id)
                    .stream()
                    .map(ContratEmployeRubriqueDTO::fromEntity)
                    .collect(Collectors.toList());
        }



      /*  @Transactional
        public List<MontantParContratDTO> listeByCompanyId(Long companyId) {
            return rubriqueRepository.findMontantsByContratAndEmploye(companyId);
        }*/
        @Transactional
        public List<MontantParContratDTO> listeByCompanyId(Long companyId) {
            List<Object[]> results = rubriqueRepository.findMontantsRawByContratAndEmploye(companyId);
            List<MontantParContratDTO> dtos = new ArrayList<>();
            for (Object[] row : results) {
                Long contratId = (Long) row[0];
                Long employeId = (Long) row[1];
                String nom = (String) row[2];
                String prenom = (String) row[3];
                BigDecimal montant = row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO;
                BigDecimal montantAjout = row[5] != null ? (BigDecimal) row[5] : BigDecimal.ZERO;
                dtos.add(new MontantParContratDTO(contratId, employeId, nom, prenom, montant, montantAjout));
            }
            return dtos;
        }

        @Transactional
        public List<RubriqueMontantDTO> getListRubriquesByContratId(Long contratId) {
            List<Object[]> rows = rubriqueRepository.findRubriquesMontantsRawByContratId(contratId);
            return rows.stream()
                    .map(RubriqueMontantDTO::fromTuple)
                    .collect(Collectors.toList());
        }


        @Transactional
        public ContratEmployeRubriqueDTO save(ContratEmployeRubriqueDTO dto, User currentUser) {
            ContratEmployeRubrique entity;

            if (dto.getId() != null) {
                entity = rubriqueRepository.findById(dto.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Rubrique du contrat introuvable"));
            } else {
                entity = new ContratEmployeRubrique();
                entity.setAdded_by(userRepository.getReferenceById(currentUser.getId()));
            }

            ContratEmploye contratEmploye = contratEmployeRepository.getReferenceById(dto.getContratEmployeId());
            Rubrique rubrique = rubriqueRefRepository.getReferenceById(dto.getRubriqueId());

            entity.setContratEmploye(contratEmploye);
            entity.setRubrique(rubrique);
            entity.setLibelle(rubrique.getLibelle());
            entity.setMontant(dto.getMontant());
            entity.setMontant_ajout(BigDecimal.ZERO);
            entity.setDateDebut(dto.getDateDebut());
            entity.setDateFin(dto.getDateFin());
            entity.setStatut(dto.getStatut() != null ? dto.getStatut() : ContratEmployeRubrique.Statut.ACTIF);


            ContratEmployeRubrique saved  = rubriqueRepository.save(entity);

            // Notifications
            notificationService.createNotification(
                    currentUser,
                    "📎 Nouvelle rubrique ajoutée",
                    "L'utilisateur " + currentUser.getFullName() + " a ajouté une rubrique au contrat de l'employé."
            );

            // Audit Log
            auditLogService.log(
                    "Ajout ou modification d’une rubrique de contrat",
                    "contrat_employe_rubriques",
                    currentUser.getId(),
                    "Modification rubrique contrat : " + rubrique.getLibelle(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            return ContratEmployeRubriqueDTO.fromEntity(saved);
        }

        @Transactional
        public ContratEmployeRubriqueDTO save(ContratEmployeRubriqueDTO dto, User currentUser, Long companyId) {
            ContratEmployeRubrique entity;
            Optional<Company> companyOpt = companyService.findById(companyId);
            Company company = new Company();
            if (companyOpt.isPresent()){
                company = companyOpt.get();
            }
            if (dto.getId() != null) {
                entity = rubriqueRepository.findById(dto.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Rubrique du contrat introuvable"));
            } else {
                entity = new ContratEmployeRubrique();
                entity.setAdded_by(userRepository.getReferenceById(currentUser.getId()));
            }

            ContratEmploye contratEmploye = contratEmployeRepository.getReferenceById(dto.getContratEmployeId());
            Rubrique rubrique = rubriqueRefRepository.getReferenceById(dto.getRubriqueId());

            entity.setContratEmploye(contratEmploye);
            entity.setRubrique(rubrique);
            entity.setLibelle(rubrique.getLibelle());
            entity.setMontant(dto.getMontant());
            entity.setMontant_ajout(BigDecimal.ZERO);
            entity.setDateDebut(dto.getDateDebut());
            entity.setDateFin(dto.getDateFin());
            entity.setCompany(company);
            entity.setStatut(dto.getStatut() != null ? dto.getStatut() : ContratEmployeRubrique.Statut.ACTIF);


            ContratEmployeRubrique saved = rubriqueRepository.save(entity);

            // Notifications
            notificationService.createNotification(
                    currentUser,
                    "📎 Nouvelle rubrique ajoutée",
                    "L'utilisateur " + currentUser.getFullName() + " a ajouté une rubrique au contrat de l'employé."
            );

            // Audit Log
            auditLogService.log(
                    "Ajout ou modification d’une rubrique de contrat",
                    "contrat_employe_rubriques",
                    currentUser.getId(),
                    "Modification rubrique contrat : " + rubrique.getLibelle(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            return ContratEmployeRubriqueDTO.fromEntity(saved);
        }

        public void delete(Long id) {
            if (!rubriqueRepository.existsById(id)) {
                throw new EntityNotFoundException("Rubrique de contrat non trouvée");
            }
            rubriqueRepository.deleteById(id);
        }

        public List<ContratEmployeRubriqueDTO> getAllByContrat(Long contratId) {
            return rubriqueRepository.findByContratEmployeId(contratId)
                    .stream()
                    .map(r -> new ContratEmployeRubriqueDTO().fromEntity(r))
                    .collect(Collectors.toList());
        }

        @Transactional
        public ContratEmployeRubrique saveOrUpdate(
                ContratEmployeRubriqueDTO dto,
                User currentUser,
                Long companyId
        ) {

            // 🔎 Recherche de l'existant
            ContratEmployeRubrique entity = rubriqueRepository
                    .findByContratEmployeIdAndRubriqueId(dto.getContratEmployeId(), dto.getRubriqueId())
                    .orElseGet(ContratEmployeRubrique::new);

            // 🔗 Relations via repository (IMPORTANT)
            ContratEmploye contratEmploye = contratEmployeRepository
                    .getReferenceById(dto.getContratEmployeId());

            Rubrique rubrique = rubriqueRefRepository
                    .getReferenceById(dto.getRubriqueId());

            Optional<Company> companyOpt = companyService.findById(companyId);
            Company company = companyOpt.orElse(null);

            // 🧩 Affectation des champs
            entity.setContratEmploye(contratEmploye);
            entity.setRubrique(rubrique);
            entity.setLibelle(rubrique.getLibelle());

            entity.setMontant(dto.getMontant());
            entity.setMontant_ajout(
                    dto.getMontant_ajouter() != null ? dto.getMontant_ajouter() : BigDecimal.ZERO
            );

            entity.setDateDebut(dto.getDateDebut());
            entity.setDateFin(dto.getDateFin());

            entity.setCompany(company);

            entity.setStatut(
                    dto.getStatut() != null
                            ? dto.getStatut()
                            : ContratEmployeRubrique.Statut.ACTIF
            );

            // 👤 Audit
            if (entity.getId() == null) {
                entity.setAdded_by(userRepository.getReferenceById(currentUser.getId()));
            }

            // 💾 Sauvegarde
            ContratEmployeRubrique saved = rubriqueRepository.save(entity);

            // 🔔 Notification
            notificationService.createNotification(
                    currentUser,
                    "📎 Rubrique contrat mise à jour",
                    "Une rubrique a été créée ou mise à jour pour le contrat."
            );

            // 📜 Audit log
            auditLogService.log(
                    "SaveOrUpdate rubrique contrat",
                    "contrat_employe_rubriques",
                    currentUser.getId(),
                    "Rubrique: " + rubrique.getLibelle(),
                    currentUser,
                    requestHelper.getClientIp(),
                    requestHelper.getUserAgent()
            );

            return saved;
        }

    }
