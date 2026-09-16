package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.company.CompanyDTO;
import com.tpc.tpcgestpaie.localapp.dto.company.CompanyPaieConfigDTO;
import com.tpc.tpcgestpaie.localapp.helper.RequestHelper;
import com.tpc.tpcgestpaie.localapp.model.ActivityArea;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.ActivityAreaRepository;
import com.tpc.tpcgestpaie.localapp.repository.BanqueRepository;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.service.util.FileStorageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    private final RequestHelper requestHelper;
    private final BanqueRepository banqueRepository;
    private final FileStorageService fileStorageService;
    private final ActivityAreaRepository activityAreaRepository;
    private final UserService userService;

    public CompanyService(CompanyRepository companyRepository,
                          NotificationService notificationService,
                          AuditLogService auditLogService,
                          RequestHelper requestHelper,
                          BanqueRepository banqueRepository,
                          FileStorageService fileStorageService,
                          ActivityAreaRepository activityAreaRepository, UserService userService) {
        this.companyRepository = companyRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
        this.requestHelper = requestHelper;
        this.banqueRepository = banqueRepository;
        this.fileStorageService = fileStorageService;
        this.activityAreaRepository = activityAreaRepository;
        this.userService = userService;
    }

    // Récupère toutes les entreprises enregistrées en base
    public List<Company> getAll() {
        return companyRepository.findAll();
    }

    @Transactional
    public List<CompanyDTO> getAllCompaniesDTO() {
        List<Company> companies = companyRepository.findAll();

        return companies.stream()
                .map(company -> {
                    try {
                        // ⭐ CORRECTION : Utiliser une méthode de mapping sécurisée
                        return mapCompanyToSafeDTO(company);
                    } catch (Exception e) {
                        // ⭐ GESTION D'ERREUR : Retourner un DTO basique en cas d'erreur
                        System.err.println("Erreur lors du mapping de l'entreprise " + company.getId() + ": " + e.getMessage());
                        return createBasicCompanyDTO(company);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * ⭐ NOUVELLE MÉTHODE : Mapping sécurisé sans accéder aux relations problématiques
     */
    private CompanyDTO mapCompanyToSafeDTO(Company company) {
        CompanyDTO dto = new CompanyDTO();

        // Champs de base uniquement (pas de relations)
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setNss(company.getNss());
        dto.setRss(company.getRss());
        dto.setAddress(company.getAddress());
        dto.setCountry(company.getCountry());
        dto.setEmail(company.getEmail());
        dto.setPhone(company.getPhone());
        dto.setWebSite(company.getWebSite());
        dto.setRccm(company.getRccm());
        dto.setIfu(company.getIfu());
        dto.setLogo(company.getLogo());
        dto.setDirectorName(company.getDirectorName());
        dto.setDirectorEmail(company.getDirectorEmail());
        dto.setDirectorPhone(company.getDirectorPhone());
        dto.setStatusId(company.getStatusId());
        dto.setTvaVal(company.getTvaVal());
        dto.setCreatedAt(company.getCreatedAt());
        dto.setUpdatedAt(company.getUpdatedAt());
        dto.setDeletedAt(company.getDeletedAt());
        dto.setCreatedBy(company.getCreatedBy());
        dto.setUpdatedBy(company.getUpdatedBy());

        dto.setCreationDate(company.getCreationDate());
        dto.setVps(company.getVps());
        dto.setVpsEffectDate(company.getVpsEffectDate());
        dto.setSignatoryName(company.getSignatoryName());
        dto.setCa(company.getCa());
        dto.setModeJouissanceConge(company.getModeJouissanceConge());
        dto.setNbrJourTravail(company.getNbrJourTravail());
        dto.setNbrJourConge(company.getNbrJourConge());
        dto.setHeuresParJour(company.getHeuresParJour());
        dto.setHeuresParSemaine(company.getHeuresParSemaine());
        dto.setEstEntreprisePrincipale(company.getEstEntreprisePrincipale());

        // ⭐ CORRECTION : Champs de gestion client sans accéder aux relations
        dto.setLicenseType(company.getLicenseType());
        dto.setClientCompany(company.isClientCompany());
        dto.setManagedCompany(company.isManagedCompany());
        dto.setCanManageCompanies(company.canManageCompanies());
        dto.setStandalone(company.isStandalone());

        // ⭐ CORRECTION : Ne pas accéder aux relations directes
        // Utiliser des méthodes de repository pour charger les données si nécessaire
//        try {
//            // ActivityAreas - charger via repository si vraiment nécessaire
//            List<ActivityArea> activityAreas = companyRepository.findActivityAreasByCompanyId(company.getId());
//            if (activityAreas != null) {
//                Set<String> activityAreaNames = activityAreas.stream()
//                        .map(ActivityArea::getName)
//                        .collect(Collectors.toSet());
//                dto.setActivityAreas(activityAreaNames);
//            } else {
//                dto.setActivityAreas(new HashSet<>());
//            }
//        } catch (Exception e) {
//            dto.setActivityAreas(new HashSet<>());
//        }

        // ⭐ CORRECTION : Managed companies count sans accéder à la relation directe
        try {
            int managedCount = companyRepository.countManagedCompaniesByClientId(company.getId());
            dto.setManagedCompaniesCount(managedCount);
            dto.setManagedCompanies(new ArrayList<>()); // Liste vide pour l'instant
        } catch (Exception e) {
            dto.setManagedCompaniesCount(0);
            dto.setManagedCompanies(new ArrayList<>());
        }

        // Informations du client si c'est une entreprise gérée
        if (company.isManagedCompany() && company.getClient() != null) {
            dto.setClientId(company.getClient().getId());
            dto.setClientName(company.getClient().getName());
        }

        return dto;
    }

    /**
     * ⭐ NOUVELLE MÉTHODE : DTO basique pour les cas d'erreur
     */
    private CompanyDTO createBasicCompanyDTO(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setEmail(company.getEmail());
        dto.setPhone(company.getPhone());
        dto.setAddress(company.getAddress());
        dto.setLicenseType(company.getLicenseType());
        dto.setClientCompany(company.isClientCompany());
        dto.setManagedCompany(company.isManagedCompany());
        dto.setActivityAreas(new HashSet<>());
        dto.setManagedCompanies(new ArrayList<>());
        dto.setManagedCompaniesCount(0);
        return dto;
    }

//    @Transactional
//    public List<CompanyDTO> getAllCompaniesDTO() {
//        List<Company> companies = companyRepository.findAll();
//
//        return companies.stream()
//                .map(company -> {
//                    // Récupérer les relations via requêtes natives pour chaque company
//                    List<ActivityArea> activityAreas = companyRepository.findActivityAreasByCompanyId(company.getId());
//                    List<Banque> banques = companyRepository.findBanquesByCompanyId(company.getId());
//
//                    // ⭐ UTILISER LE NOUVEAU MAPPER
//                    return CompanyDTO.fromEntity(company);
//                })
//                .collect(Collectors.toList());
//    }

    // Récupère une entreprise par son ID ou lève une exception si non trouvée
    public Company getById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }

    public boolean existsById(Long id) {
        return companyRepository.existsById(id);
    }

    // Enregistre une nouvelle entreprise en base
    public Company create(Company company) {
        return companyRepository.save(company);
    }

    // ⭐ NOUVELLE MÉTHODE : Créer un client (entreprise principale)
    @Transactional
    public CompanyDTO createClientCompany(CompanyDTO companyDTO, User currentUser) {
        Company company = new Company();

        // Mapping des champs de base
        mapCompanyFromDTO(company, companyDTO, currentUser);

        // ⭐ SPÉCIFICITÉ CLIENT : client_id = NULL et licence MULTI_COMPANY par défaut
        company.setClient(null);
        company.setLicenseType(Company.LicenseType.MULTI_COMPANY);

        company = companyRepository.save(company);

        // Notifications et audit
        logCompanyCreation(company, currentUser, "client");

        return CompanyDTO.fromEntity(company);
    }

    // ⭐ NOUVELLE MÉTHODE : Créer une entreprise gérée
    @Transactional
    public CompanyDTO createManagedCompany(CompanyDTO companyDTO, Long clientId, User currentUser) {
        // Vérifier que le client existe et peut gérer des entreprises
        Company clientCompany = companyRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client non trouvé avec l'ID : " + clientId));

        if (!clientCompany.canManageCompanies()) {
            throw new RuntimeException("Ce client ne peut pas gérer d'autres entreprises");
        }

        Company company = new Company();

        // Mapping des champs de base
        mapCompanyFromDTO(company, companyDTO, currentUser);

        // ⭐ SPÉCIFICITÉ ENTREPRISE GÉRÉE : liée au client et licence SINGLE_COMPANY
        company.setClient(clientCompany);
        company.setLicenseType(Company.LicenseType.SINGLE_COMPANY);

        company = companyRepository.save(company);

        // Notifications et audit
        logCompanyCreation(company, currentUser, "entreprise gérée");

        return CompanyDTO.fromEntity(company);
    }

    // ⭐ MÉTHODE GÉNÉRIQUE DE MAPPAGE
    private void mapCompanyFromDTO(Company company, CompanyDTO companyDTO, User currentUser) {
        // Champs de base
        company.setName(companyDTO.getName());
        company.setAddress(companyDTO.getAddress());
        company.setPhone(companyDTO.getPhone());
        company.setEmail(companyDTO.getEmail());
        company.setRss(companyDTO.getRss());
        company.setNss(companyDTO.getNss());
        company.setWebsite(companyDTO.getWebSite());
        company.setCountry(companyDTO.getCountry());
        company.setCountryCode(companyDTO.getCountryCode());
        company.setRccm(companyDTO.getRccm());
        company.setIfu(companyDTO.getIfu());
        company.setDirectorName(companyDTO.getDirectorName());
        company.setDirectorPhone(companyDTO.getDirectorPhone());
        company.setDirectorEmail(companyDTO.getDirectorEmail());
        company.setTvaVal(companyDTO.getTvaVal());
        company.setCreatedBy(currentUser.getId());
        company.setUpdatedBy(currentUser.getId());

        // Gestion du logo
        if (companyDTO.getLogo() != null) {
            company.setLogo(companyDTO.getLogo());
        }

        // Champs de configuration
        company.setCreationDate(companyDTO.getCreationDate());
        company.setVps(companyDTO.getVps() != null ? companyDTO.getVps() : 0.0);
        company.setVpsEffectDate(companyDTO.getVpsEffectDate());
        company.setSignatoryName(companyDTO.getSignatoryName());
        company.setCa(companyDTO.getCa() != null ? companyDTO.getCa() : BigDecimal.ZERO);
        company.setModeJouissanceConge(companyDTO.getModeJouissanceConge());
        company.setNbrJourTravail(companyDTO.getNbrJourTravail() != null ? companyDTO.getNbrJourTravail() : 0.0);
        company.setNbrJourConge(companyDTO.getNbrJourConge() != null ? companyDTO.getNbrJourConge() : 0.0);
        company.setHeuresParJour(companyDTO.getHeuresParJour() != null ? companyDTO.getHeuresParJour() : 0.0);
        company.setHeuresParSemaine(companyDTO.getHeuresParSemaine() != null ? companyDTO.getHeuresParSemaine() : 0.0);
        company.setEstEntreprisePrincipale(companyDTO.getEstEntreprisePrincipale() != null ? companyDTO.getEstEntreprisePrincipale() : false);

        // Associer les banques
//        if (companyDTO.getBanqueIds() != null && !companyDTO.getBanqueIds().isEmpty()) {
//            List<Banque> banques = banqueRepository.findAllById(companyDTO.getBanqueIds());
//            company.setBanques(new HashSet<>(banques));
//        }

        // Gestion des ActivityAreas
//        if (companyDTO.getActivityAreas() != null && !companyDTO.getActivityAreas().isEmpty()) {
//            Set<ActivityArea> activityAreas = handleActivityAreas(companyDTO.getActivityAreas());
//            company.setActivityAreas(activityAreas);
//        }
    }

    @Transactional
    public CompanyPaieConfigDTO updatePaieConfig(Long companyId, Map<String, Object> paieConfigData, User currentUser) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise non trouvée"));

        // Mettre à jour uniquement les champs de configuration paie
        if (paieConfigData.containsKey("vps")) {
            company.setVps(Double.parseDouble(paieConfigData.get("vps").toString()));
        }
        if (paieConfigData.containsKey("vpsEffectDate")) {
            company.setVpsEffectDate(LocalDate.parse(paieConfigData.get("vpsEffectDate").toString()));
        }
        if (paieConfigData.containsKey("signatoryName")) {
            company.setSignatoryName(paieConfigData.get("signatoryName").toString());
        }
//        if (paieConfigData.containsKey("ca")) {
//            company.setCa(BigDecimal.valueOf(Double.parseDouble(paieConfigData.get("ca").toString())));
//        }
        if (paieConfigData.containsKey("modeJouissanceConge")) {
            company.setModeJouissanceConge(paieConfigData.get("modeJouissanceConge").toString());
        }
        if (paieConfigData.containsKey("nbrJourTravail")) {
            company.setNbrJourTravail((double) Integer.parseInt(paieConfigData.get("nbrJourTravail").toString()));
        }
        if (paieConfigData.containsKey("nbrJourConge")) {
            company.setNbrJourConge((double) Integer.parseInt(paieConfigData.get("nbrJourConge").toString()));
        }
        if (paieConfigData.containsKey("heuresParJour")) {
            company.setHeuresParJour((double) Integer.parseInt(paieConfigData.get("heuresParJour").toString()));
        }
        if (paieConfigData.containsKey("heuresParSemaine")) {
            company.setHeuresParSemaine((double) Integer.parseInt(paieConfigData.get("heuresParSemaine").toString()));
        }

        company.setUpdatedAt(LocalDateTime.now());

        Company savedCompany = companyRepository.save(company);

        return new CompanyPaieConfigDTO(savedCompany);
    }

    @Transactional
    public CompanyDTO updateCompany(Long id, CompanyDTO companyDTO, User currentUser) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise non trouvée avec l'ID : " + id));

        // Sauvegarder l'ancien logo pour suppression si nécessaire
        String oldLogo = company.getLogo();

        // Mise à jour des champs
        company.setName(companyDTO.getName());
        company.setAddress(companyDTO.getAddress());
        company.setPhone(companyDTO.getPhone());
        company.setEmail(companyDTO.getEmail());
        company.setRss(companyDTO.getRss());
        company.setNss(companyDTO.getNss());
        company.setWebsite(companyDTO.getWebSite());
        company.setCountry(companyDTO.getCountry());
        company.setCountryCode(companyDTO.getCountryCode());
        company.setRccm(companyDTO.getRccm());
        company.setIfu(companyDTO.getIfu());
        company.setDirectorName(companyDTO.getDirectorName());
        company.setDirectorPhone(companyDTO.getDirectorPhone());
        company.setDirectorEmail(companyDTO.getDirectorEmail());
        company.setTvaVal(companyDTO.getTvaVal());
        company.setUpdatedBy(currentUser.getId());

        // ⭐ GESTION DE LA LICENCE (si modifiée)
        if (companyDTO.getLicenseType() != null) {
            // Vérifier si on peut changer de licence
            if (companyDTO.getLicenseType() == Company.LicenseType.SINGLE_COMPANY &&
                    company.getLicenseType() == Company.LicenseType.MULTI_COMPANY &&
                    !company.getManagedCompanies().isEmpty()) {
                throw new RuntimeException("Impossible de passer en SINGLE_COMPANY : des entreprises sont gérées");
            }
            company.setLicenseType(companyDTO.getLicenseType());
        }

        // Gestion du logo
        if (companyDTO.getLogo() != null && !companyDTO.getLogo().equals(oldLogo)) {
            company.setLogo(companyDTO.getLogo());
            if (oldLogo != null) {
                fileStorageService.deleteLogo(oldLogo);
            }
        }

        // Champs de configuration
        company.setCreationDate(companyDTO.getCreationDate());
        company.setVps(companyDTO.getVps() != null ? companyDTO.getVps() : company.getVps());
        company.setVpsEffectDate(companyDTO.getVpsEffectDate());
        company.setSignatoryName(companyDTO.getSignatoryName());
        company.setCa(companyDTO.getCa() != null ? companyDTO.getCa() : company.getCa());
        company.setModeJouissanceConge(companyDTO.getModeJouissanceConge());
        company.setNbrJourTravail(companyDTO.getNbrJourTravail() != null ? companyDTO.getNbrJourTravail() : company.getNbrJourTravail());
        company.setNbrJourConge(companyDTO.getNbrJourConge() != null ? companyDTO.getNbrJourConge() : company.getNbrJourConge());
        company.setHeuresParJour(companyDTO.getHeuresParJour() != null ? companyDTO.getHeuresParJour() : company.getHeuresParJour());
        company.setHeuresParSemaine(companyDTO.getHeuresParSemaine() != null ? companyDTO.getHeuresParSemaine() : company.getHeuresParSemaine());
        company.setEstEntreprisePrincipale(companyDTO.getEstEntreprisePrincipale() != null ? companyDTO.getEstEntreprisePrincipale() : company.getEstEntreprisePrincipale());

        // Mise à jour des banques
//        if (companyDTO.getBanqueIds() != null) {
//            List<Banque> banques = banqueRepository.findAllById(companyDTO.getBanqueIds());
//            company.setBanques(new HashSet<>(banques));
//        }

        // Mise à jour des ActivityAreas
//        if (companyDTO.getActivityAreas() != null) {
//            Set<ActivityArea> activityAreas = handleActivityAreas(companyDTO.getActivityAreas());
//            company.setActivityAreas(activityAreas);
//        }

        company = companyRepository.save(company);

        // Notifications et audit
        logCompanyUpdate(company, currentUser);

        // ⭐ UTILISER LE NOUVEAU MAPPER
        return CompanyDTO.fromEntity(company);
    }

    // Met à jour les informations d'une entreprise existante
    public Company update(Long id, Company newCompany) {
        Company existing = getById(id);
        existing.setName(newCompany.getName());
        // TODO: Ajouter les autres champs à mettre à jour
        return companyRepository.save(existing);
    }

    // Supprime une entreprise par son ID
    @Transactional
    public void delete(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise non trouvée avec l'ID : " + id));

        // ⭐ VÉRIFICATION : Ne pas supprimer un client qui a des entreprises gérées
        if (company.canManageCompanies() && !company.getManagedCompanies().isEmpty()) {
            throw new RuntimeException("Impossible de supprimer ce client : des entreprises sont gérées");
        }

        // Supprimer le logo associé
        if (company.getLogo() != null) {
            fileStorageService.deleteLogo(company.getLogo());
        }

        companyRepository.deleteById(id);
    }

    @Transactional
    public CompanyDTO createCompany(CompanyDTO companyDTO, User currentUser) {
        // ⭐ DÉTERMINER LE TYPE DE CRÉATION
        User user = userService.getCurrentUser();
        Long clientId = user.getCompany().getId();


            // Création d'une entreprise gérée
            return createManagedCompany(companyDTO, clientId, currentUser);

    }

    @Transactional
    public CompanyDTO createCompany(Company company, User currentUser) {
        company.setCreatedBy(currentUser.getId());
        company.setUpdatedBy(currentUser.getId());

        // ⭐ DÉTERMINER LE TYPE AUTOMATIQUEMENT
        if (company.getClient() == null) {
            company.setLicenseType(Company.LicenseType.MULTI_COMPANY); // Client par défaut
        } else {
            company.setLicenseType(Company.LicenseType.SINGLE_COMPANY); // Entreprise gérée
        }

        this.create(company);

        logCompanyCreation(company, currentUser, company.isClientCompany() ? "client" : "entreprise gérée");

        return CompanyDTO.fromEntity(company);
    }

    public Optional<Company> findById(Long companyId) {
        return companyRepository.findById(companyId);
    }

    public List<Company> getCompaniesNotAssigned() {
        return companyRepository.findCompaniesNotAssigned();
    }

    @Transactional
    public CompanyDTO getCompanyDTOById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise non trouvée avec l'ID : " + id));

        // ⭐ UTILISER LE NOUVEAU MAPPER
        if (company.canManageCompanies()) {
            return CompanyDTO.fromEntityWithManagedCompanies(company);
        } else {
            return CompanyDTO.fromEntity(company);
        }
    }

    // Méthode pour mettre à jour seulement le logo
    @Transactional
    public CompanyDTO updateCompanyLogo(Long id, MultipartFile logoFile, User currentUser) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise non trouvée avec l'ID : " + id));

        // Sauvegarder l'ancien logo
        String oldLogo = company.getLogo();

        // Traiter le nouveau logo
        String newLogoPath = processLogo(logoFile);
        if (newLogoPath != null) {
            company.setLogo(newLogoPath);
            company.setUpdatedBy(currentUser.getId());

            // Supprimer l'ancien logo
            if (oldLogo != null) {
                fileStorageService.deleteLogo(oldLogo);
            }

            company = companyRepository.save(company);

            // Notification
            notificationService.createNotification(
                    currentUser,
                    "🖼️ Logo mis à jour",
                    "📌 L'utilisateur " + currentUser.getFullName() + " a modifié le logo de l'entreprise : " + company.getName()
            );

            // Audit
            String ipAddress = requestHelper.getClientIp();
            String userAgent = requestHelper.getUserAgent();
            auditLogService.log(
                    "Mise à jour du logo",
                    "companies",
                    currentUser.getId(),
                    currentUser.getFullName() + " a modifié le logo de l'entreprise: " + company.getName(),
                    currentUser,
                    ipAddress,
                    userAgent
            );
        }

        return CompanyDTO.fromEntity(company);
    }

    // ⭐ NOUVELLES MÉTHODES POUR LA GESTION CLIENT-ENTREPRISES

    public List<CompanyDTO> getClientCompanies() {
        return companyRepository.findByClientIsNull().stream()
                .map(CompanyDTO::fromEntityWithManagedCompanies)
                .collect(Collectors.toList());
    }

    public List<CompanyDTO> getManagedCompanies(Long clientId) {
        return companyRepository.findByClientId(clientId).stream()
                .map(CompanyDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public CompanyDTO updateLicense(Long companyId, Company.LicenseType newLicenseType, User currentUser) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise non trouvée"));

        if (!company.isClientCompany()) {
            throw new RuntimeException("Seuls les clients peuvent changer de licence");
        }

        // Vérifier si passage de MULTI à SINGLE avec entreprises gérées
        if (company.getLicenseType() == Company.LicenseType.MULTI_COMPANY &&
                newLicenseType == Company.LicenseType.SINGLE_COMPANY &&
                !company.getManagedCompanies().isEmpty()) {
            throw new RuntimeException("Impossible de passer en SINGLE_COMPANY : " +
                    company.getManagedCompanies().size() + " entreprise(s) gérée(s)");
        }

        company.setLicenseType(newLicenseType);
        company.setUpdatedBy(currentUser.getId());

        Company updatedCompany = companyRepository.save(company);

        logCompanyUpdate(updatedCompany, currentUser);

        return CompanyDTO.fromEntity(updatedCompany);
    }

    // ⭐ MÉTHODES UTILITAIRES PRIVÉES

    private Set<ActivityArea> handleActivityAreas(Set<String> activityAreaNames) {
        Set<ActivityArea> result = new HashSet<>();

        for (String areaName : activityAreaNames) {
            ActivityArea existingArea = activityAreaRepository.findByName(areaName)
                    .orElseGet(() -> {
                        ActivityArea newArea = new ActivityArea();
                        newArea.setName(areaName);
                        newArea.setDescription(areaName + " - domaine d'activité");
                        return activityAreaRepository.save(newArea);
                    });
            result.add(existingArea);
        }

        return result;
    }

    private String processLogo(MultipartFile logoFile) {
        if (logoFile != null && !logoFile.isEmpty()) {
            return fileStorageService.storeLogo(logoFile);
        }
        return null;
    }

    private void logCompanyCreation(Company company, User currentUser, String type) {
        notificationService.createNotification(
                currentUser,
                "🏢 Nouvelle " + type + " enregistrée",
                "📌 L'utilisateur " + currentUser.getFullName() + " a ajouté une nouvelle " + type + " : " + company.getName()
        );

        String ipAddress = requestHelper.getClientIp();
        String userAgent = requestHelper.getUserAgent();
        auditLogService.log(
                "Ajout d'une nouvelle " + type,
                "companies",
                currentUser.getId(),
                currentUser.getFullName() + " a ajouté une nouvelle " + type + ": " + company.getName(),
                currentUser,
                ipAddress,
                userAgent
        );
    }

    private void logCompanyUpdate(Company company, User currentUser) {
        notificationService.createNotification(
                currentUser,
                "✏️ Entreprise mise à jour",
                "📌 L'utilisateur " + currentUser.getFullName() + " a modifié l'entreprise : " + company.getName()
        );

        String ipAddress = requestHelper.getClientIp();
        String userAgent = requestHelper.getUserAgent();
        auditLogService.log(
                "Modification d'une entreprise",
                "companies",
                currentUser.getId(),
                currentUser.getFullName() + " a modifié l'entreprise: " + company.getName(),
                currentUser,
                ipAddress,
                userAgent
        );
    }
}