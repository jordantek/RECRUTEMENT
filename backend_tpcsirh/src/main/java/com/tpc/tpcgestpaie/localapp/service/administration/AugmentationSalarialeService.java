package com.tpc.tpcgestpaie.localapp.service.administration;

import com.tpc.tpcgestpaie.localapp.dto.administration.AugmentationSalarialeDTO;
import com.tpc.tpcgestpaie.localapp.exception.BusinessException;
import com.tpc.tpcgestpaie.localapp.helper.RequestHelper;
import com.tpc.tpcgestpaie.localapp.model.AugmentationRubrique;
import com.tpc.tpcgestpaie.localapp.model.AugmentationSalariale;
import com.tpc.tpcgestpaie.localapp.model.ContratEmployeRubrique;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.administration.AugmentationSalarialeRepository;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import com.tpc.tpcgestpaie.localapp.service.AuditLogService;
import com.tpc.tpcgestpaie.localapp.service.ContratEmployeRubriqueService;
import com.tpc.tpcgestpaie.localapp.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AugmentationSalarialeService {

    private final AugmentationSalarialeRepository augmentationRepository;
    private final EmployeRepository employeRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    private final RequestHelper requestHelper;
    private final ContratEmployeRubriqueService contratEmployeRubriqueService;

    public AugmentationSalarialeService(
            AugmentationSalarialeRepository augmentationRepository,
            EmployeRepository employeRepository,
            ContratEmployeRepository contratEmployeRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository,
            NotificationService notificationService,
            AuditLogService auditLogService,
            RequestHelper requestHelper,
            ContratEmployeRubriqueService contratEmployeRubriqueService) {
        this.augmentationRepository = augmentationRepository;
        this.employeRepository = employeRepository;
        this.contratEmployeRepository = contratEmployeRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
        this.requestHelper = requestHelper;
        this.contratEmployeRubriqueService = contratEmployeRubriqueService;
    }

    // === MÉTHODES PAGINÉES ===

    public Page<AugmentationSalarialeDTO> getAll(Pageable pageable) {
        Page<AugmentationSalariale> augmentationsPage = augmentationRepository.findAllWithAssociations(pageable);
        return augmentationsPage.map(AugmentationSalarialeDTO::fromEntity);
    }

    public Page<AugmentationSalarialeDTO> getByEntrepriseId(Long entrepriseId, Pageable pageable) {
        Page<AugmentationSalariale> augmentationsPage = augmentationRepository.findByCompanyId(entrepriseId, pageable);
        return augmentationsPage.map(AugmentationSalarialeDTO::fromEntity);
    }

    public Page<AugmentationSalarialeDTO> getByEmployeId(Long employeId, Pageable pageable) {
        Page<AugmentationSalariale> augmentationsPage = augmentationRepository.findByEmployeIdOrderByDateEffetDesc(employeId, pageable);
        return augmentationsPage.map(AugmentationSalarialeDTO::fromEntity);
    }

    public Page<AugmentationSalarialeDTO> getByContratEmployeId(Long contratEmployeId, Pageable pageable) {
        Page<AugmentationSalariale> augmentationsPage = augmentationRepository.findByContratEmployeIdOrderByCreatedAtDesc(contratEmployeId, pageable);
        return augmentationsPage.map(AugmentationSalarialeDTO::fromEntity);
    }

    // === MÉTHODES NON PAGINÉES (pour compatibilité) ===

    public List<AugmentationSalarialeDTO> getAll() {
        Pageable pageable = Pageable.unpaged();
        Page<AugmentationSalariale> augmentationsPage = augmentationRepository.findAllWithAssociations(pageable);
        return augmentationsPage.getContent().stream()
                .map(AugmentationSalarialeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public AugmentationSalarialeDTO getById(Long id) {
        return augmentationRepository.findById(id)
                .map(AugmentationSalarialeDTO::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Augmentation salariale introuvable"));
    }

    public List<AugmentationSalarialeDTO> getByEntrepriseId(Long entrepriseId) {
        Pageable pageable = Pageable.unpaged();
        Page<AugmentationSalariale> augmentationsPage = augmentationRepository.findByCompanyId(entrepriseId, pageable);
        return augmentationsPage.getContent().stream()
                .map(AugmentationSalarialeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AugmentationSalarialeDTO> getByEmployeId(Long employeId) {
        Pageable pageable = Pageable.unpaged();
        Page<AugmentationSalariale> augmentationsPage = augmentationRepository.findByEmployeIdOrderByDateEffetDesc(employeId, pageable);
        return augmentationsPage.getContent().stream()
                .map(AugmentationSalarialeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AugmentationSalarialeDTO> getByContratEmployeId(Long contratEmployeId) {
        Pageable pageable = Pageable.unpaged();
        Page<AugmentationSalariale> augmentationsPage = augmentationRepository.findByContratEmployeIdOrderByCreatedAtDesc(contratEmployeId, pageable);
        return augmentationsPage.getContent().stream()
                .map(AugmentationSalarialeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // === MÉTHODES DE CRÉATION/MODIFICATION ===

    @Transactional
    public AugmentationSalarialeDTO save(AugmentationSalarialeDTO dto, User currentUser) {
        AugmentationSalariale entity;

        if (dto.getId() != null) {
            entity = augmentationRepository.findById(dto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Augmentation salariale introuvable"));
        } else {
            entity = new AugmentationSalariale();
        }

        // Chargement des entités liées
        entity.setEmploye(dto.getEmployeId() != null ? employeRepository.getReferenceById(dto.getEmployeId()) : null);
        entity.setContratEmploye(dto.getContratEmployeId() != null ? contratEmployeRepository.getReferenceById(dto.getContratEmployeId()) : null);
        entity.setCompany(dto.getCompanyId() != null ? companyRepository.getReferenceById(dto.getCompanyId()) : null);

        entity.setDateEffet(dto.getDateEffet());
        entity.setMotifAugmentation(dto.getMotifAugmentation());
        entity.setPreuve(dto.getPreuve());

        // Sauvegarde de l'augmentation
        AugmentationSalariale saved = augmentationRepository.save(entity);

        // Association des rubriques
        if (dto.getRubriques() != null) {
            saved.setRubriques(dto.getRubriques().stream()
                    .map(AugmentationSalarialeDTO::toAugmentationRubriqueEntity)
                    .collect(Collectors.toList()));
            saved.getRubriques().forEach(rub -> rub.setAugmentation(saved));
        }

        // Traitement de chaque rubrique augmentée
        for (AugmentationRubrique rubrique : saved.getRubriques()) {
            ContratEmployeRubrique contratRubrique;

            try {
                // Recherche de la rubrique existante dans le contrat
                contratRubrique = contratEmployeRubriqueService
                        .getByContratEmployeIdCompanyIdRubriqueId(
                                dto.getContratEmployeId(),
                                rubrique.getRubrique().getId(),
                                dto.getCompanyId()
                        );
                // Vérification montant
                BigDecimal ancienMontant = rubrique.getAncienMontant() != null ? rubrique.getAncienMontant() : BigDecimal.ZERO;
                BigDecimal nouveauMontant = rubrique.getNouveauMontant() != null ? rubrique.getNouveauMontant() : BigDecimal.ZERO;

                if (nouveauMontant.compareTo(ancienMontant) <= 0) {
                    throw new BusinessException(String.format(
                            "Le montant de la rubrique '%s' doit être supérieur à l'ancien montant (ancien: %s, nouveau: %s)",
                            contratRubrique.getLibelle(), ancienMontant, nouveauMontant
                    ));
                }

                // Calcul de l'ajout
                BigDecimal ajoutActuel = contratRubrique.getMontant_ajout() != null ? contratRubrique.getMontant_ajout() : BigDecimal.ZERO;
                BigDecimal montantAjout = nouveauMontant.subtract(ancienMontant).add(ajoutActuel);

                contratRubrique.setMontant_ajout(montantAjout);
                contratRubrique.setUpdatedAt(LocalDateTime.now());

                contratEmployeRubriqueService.create(contratRubrique);

            } catch (EntityNotFoundException e) {
                // Si la rubrique n'existait pas encore dans le contrat → création
                ContratEmployeRubrique newRubrique = new ContratEmployeRubrique();

                BigDecimal nouveauMontant = rubrique.getNouveauMontant() != null ? rubrique.getNouveauMontant() : BigDecimal.ZERO;

                newRubrique.setContratEmploye(entity.getContratEmploye());
                newRubrique.setRubrique(rubrique.getRubrique());
                newRubrique.setCompany(entity.getCompany());
                newRubrique.setLibelle(rubrique.getLibelle());
                newRubrique.setMontant(BigDecimal.ZERO);
                newRubrique.setMontant_ajout(nouveauMontant);
                newRubrique.setStatut(ContratEmployeRubrique.Statut.ACTIF);
                newRubrique.setCreatedAt(LocalDateTime.now());
                newRubrique.setAdded_by(currentUser);

                if (dto.getDateEffet() != null) {
                    newRubrique.setDateDebut(dto.getDateEffet().atStartOfDay());
                }

                contratEmployeRubriqueService.create(newRubrique);
            }
        }

        // Notification
        notificationService.createNotification(
                currentUser,
                "📈 Nouvelle augmentation salariale enregistrée",
                "L'utilisateur " + currentUser.getFullName() + " a enregistré une augmentation salariale."
        );

        // Audit log
        auditLogService.log(
                "Création ou modification d'une augmentation salariale",
                "augmentations_salariales",
                currentUser.getId(),
                "Modification augmentation salariale ID: " + saved.getId(),
                currentUser,
                requestHelper.getClientIp(),
                requestHelper.getUserAgent()
        );

        return AugmentationSalarialeDTO.fromEntity(saved);
    }

    public void delete(Long id) {
        if (!augmentationRepository.existsById(id)) {
            throw new EntityNotFoundException("Augmentation salariale non trouvée");
        }
        augmentationRepository.deleteById(id);
    }
}