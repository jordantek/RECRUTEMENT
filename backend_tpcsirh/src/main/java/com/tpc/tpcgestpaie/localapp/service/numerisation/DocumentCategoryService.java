package com.tpc.tpcgestpaie.localapp.service.numerisation;

import com.tpc.tpcgestpaie.localapp.dto.numerisation.DocumentCategoryDTO;
import com.tpc.tpcgestpaie.localapp.dto.numerisation.DocumentCategoryMapper;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentCategory;
import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentSubCategory;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.DocumentCategoryRepository;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.DocumentSubCategoryRepository;
import com.tpc.tpcgestpaie.localapp.service.CompanyAccessService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.entreprise.CompanyTypeService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DocumentCategoryService {

    private final DocumentCategoryRepository categoryRepository;
    private final DocumentSubCategoryRepository subCategoryRepository;
    private final CompanyRepository companyRepository;
    private final CompanyAccessService companyAccessService;
    private final DocumentCategoryMapper categoryMapper;
    private final CompanyTypeService companyTypeService;
    private final UserService userService;

    public DocumentCategoryService(DocumentCategoryRepository categoryRepository,
                                   DocumentSubCategoryRepository subCategoryRepository,
                                   CompanyRepository companyRepository,
                                   CompanyAccessService companyAccessService,
                                   DocumentCategoryMapper categoryMapper, CompanyTypeService companyTypeService, UserService userService) {
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.companyRepository = companyRepository;
        this.companyAccessService = companyAccessService;
        this.categoryMapper = categoryMapper;
        this.companyTypeService = companyTypeService;
        this.userService = userService;
    }

    public Optional<DocumentCategoryDTO> findById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDTO);
    }

    @Transactional
    public List<DocumentCategoryDTO> getCategoriesByCompany(Long companyId) {
        log.info("📂 Récupération des catégories pour l'entreprise ID: {}", companyId);

        try {
            // ⭐ VÉRIFIER QUE L'ENTREPRISE EXISTE
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Entreprise non trouvée avec l'id: " + companyId));

            // ⭐ RÉCUPÉRER TOUTES LES CATÉGORIES ET FILTRER MANUELLEMENT
            List<DocumentCategory> allCategories = categoryRepository.findAll();

            List<DocumentCategoryDTO> validCategories = allCategories.stream()
                    .filter(category -> isValidCategory(category, companyId))
                    .sorted(Comparator.comparing(DocumentCategory::getDisplayOrder))
                    .map(category -> {
                        try {
                            return categoryMapper.toDTO(category);
                        } catch (Exception e) {
                            log.warn("⚠️ Erreur mapping catégorie ID {}, utilisation DTO basique: {}",
                                    category.getId(), e.getMessage());
                            return createSafeDTO(category);
                        }
                    })
                    .collect(Collectors.toList());

            log.info("✅ {} catégorie(s) valide(s) trouvée(s) pour l'entreprise ID: {}",
                    validCategories.size(), companyId);
            return validCategories;

        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des catégories pour l'entreprise ID: {}", companyId, e);
            throw new RuntimeException("Erreur lors de la récupération des catégories: " + e.getMessage());
        }
    }

    // ⭐ VÉRIFIER SI UNE CATÉGORIE EST VALIDE
    private boolean isValidCategory(DocumentCategory category, Long companyId) {
        try {
            // Vérifier que le client existe
            if (category.getClient() == null) {
                log.warn("🗑️ Catégorie ID {} ignorée: client null", category.getId());
                return false;
            }

            Optional<Company> client = companyRepository.findById(category.getClient().getId());
            if (client.isEmpty()) {
                log.warn("🗑️ Catégorie ID {} ignorée: client ID {} n'existe pas",
                        category.getId(), category.getClient().getId());
                return false;
            }

            // Vérifier l'entreprise si spécifique
            if (category.getCompany() != null) {
                Optional<Company> company = companyRepository.findById(category.getCompany().getId());
                if (company.isEmpty()) {
                    log.warn("🗑️ Catégorie ID {} ignorée: entreprise ID {} n'existe pas",
                            category.getId(), category.getCompany().getId());
                    return false;
                }

                // Vérifier si c'est pour l'entreprise demandée
                return category.getCompany().getId().equals(companyId);
            } else {
                // Catégorie partagée - vérifier si c'est le même client
                Company currentClient = companyAccessService.getClientCompany(companyId);
                return category.getClient().getId().equals(currentClient.getId());
            }

        } catch (Exception e) {
            log.warn("⚠️ Catégorie ID {} ignorée due à une erreur: {}", category.getId(), e.getMessage());
            return false;
        }
    }

    // ⭐ CRÉER UN DTO SÉCURISÉ
    private DocumentCategoryDTO createSafeDTO(DocumentCategory category) {
        DocumentCategoryDTO dto = new DocumentCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setDisplayOrder(category.getDisplayOrder());
        dto.setShared(category.isShared());

        // Valeurs sécurisées pour les relations
        if (category.getClient() != null) {
            try {
                dto.setClientId(category.getClient().getId());
                // Essayer de récupérer le nom, sinon utiliser une valeur par défaut
                Company client = companyRepository.findById(category.getClient().getId()).orElse(null);
                dto.setClientName(client != null ? client.getName() : "Client inconnu");
            } catch (Exception e) {
                dto.setClientId(0L);
                dto.setClientName("Erreur client");
            }
        }

        if (category.getCompany() != null) {
            try {
                dto.setCompanyId(category.getCompany().getId());
                Company company = companyRepository.findById(category.getCompany().getId()).orElse(null);
                dto.setCompanyName(company != null ? company.getName() : "Entreprise inconnue");
            } catch (Exception e) {
                dto.setCompanyId(0L);
                dto.setCompanyName("Erreur entreprise");
            }
        }

        return dto;
    }

    public DocumentCategoryDTO createCategoryFromDTO(DocumentCategoryDTO categoryDTO) {
        log.info("🚀 Début création catégorie avec DTO: {}", categoryDTO);

        try {
            // 1. VALIDATION DE BASE
            User currentUser = userService.getCurrentUser();
            Long companyId = categoryDTO.getCompanyId();
//            Long companyId = companyTypeService.getMainCompanyId(currentUser);

            if (categoryDTO.getName() == null || categoryDTO.getName().trim().isEmpty()) {
                throw new RuntimeException("Le nom de la catégorie est obligatoire");
            }

            // 2. VALEURS PAR DÉFAUT
            Integer displayOrder = categoryDTO.getDisplayOrder() != null ? categoryDTO.getDisplayOrder() : 0;
            boolean shared = categoryDTO.isShared();

            log.info("📋 Paramètres - CompanyId: {}, Name: {}, Shared: {}, DisplayOrder: {}",
                    categoryDTO.getCompanyId(), categoryDTO.getName(), shared, displayOrder);

            // 3. RÉCUPÉRER L'ENTREPRISE

//            Company currentCompany = companyRepository.findById(companyId)
//                    .orElseThrow(() -> new RuntimeException("Entreprise non trouvée avec l'id: " + companyId));

            Company currentCompany = null;

            if (companyId != null) {
                currentCompany = companyRepository.findById(companyId)
                        .orElseThrow(() -> new RuntimeException("Entreprise non trouvée avec l'id: " + companyId));

                log.info("🏢 Entreprise trouvée: {} (ID: {})", currentCompany.getName(), currentCompany.getId());
            } else {
                log.info("🏢 Aucune entreprise spécifiée (companyId = null)");
            }


            // 4. TROUVER LE CLIENT PROPRIÉTAIRE
            Company clientCompany = companyAccessService.getClientCompany(companyTypeService.getMainCompanyId(currentUser));
            log.info("👑 Client propriétaire: {} (ID: {}), Licence: {}, Peut gérer entreprises: {}",
                    clientCompany.getName(), clientCompany.getId(),
                    clientCompany.getLicenseType(), clientCompany.canManageCompanies());

            // 5. VÉRIFIER LES DOUBLONS
            boolean duplicateExists = checkDuplicateCategory(clientCompany, currentCompany, categoryDTO.getName(), shared, null);

            if (duplicateExists) {
                throw new RuntimeException("Une catégorie avec le nom '" + categoryDTO.getName() + "' existe déjà");
            }

            // 6. CRÉER LA CATÉGORIE ENTITY
            DocumentCategory category = new DocumentCategory();
            category.setClient(clientCompany);
            category.setName(categoryDTO.getName());
            category.setDescription(categoryDTO.getDescription());
            category.setDisplayOrder(displayOrder);

            // 7. DÉTERMINER LE TYPE DE CATÉGORIE
            if (shared && clientCompany.canManageCompanies()) {
                log.info("📂 Création d'une catégorie PARTAGÉE");
                category.setCompany(null); // Catégorie partagée
            } else {
                if (shared && !clientCompany.canManageCompanies()) {
                    log.warn("⚠️ Client ne peut pas créer de catégories partagées, création spécifique");
                    shared = false; // Forcer la création spécifique
                }

                log.info("📁 Création d'une catégorie SPÉCIFIQUE");

                // Gérer l'entreprise cible si spécifiée
                if (categoryDTO.getTargetCompanyId() != null && !categoryDTO.getTargetCompanyId().equals(categoryDTO.getCompanyId())) {
                    Company targetCompany = companyRepository.findById(categoryDTO.getTargetCompanyId())
                            .orElseThrow(() -> new RuntimeException("Entreprise cible non trouvée: " + categoryDTO.getTargetCompanyId()));

                    // Vérifier l'accès
                    if (!companyAccessService.hasAccessToCompany(categoryDTO.getCompanyId(), categoryDTO.getTargetCompanyId())) {
                        throw new RuntimeException("Accès non autorisé à l'entreprise cible: " + targetCompany.getName());
                    }

                    category.setCompany(targetCompany);
                    log.info("🎯 Catégorie spécifique à l'entreprise: {}", targetCompany.getName());
                } else {
                    category.setCompany(currentCompany); // Catégorie spécifique à l'entreprise courante
                    log.info("🎯 Catégorie spécifique à l'entreprise courante");
                }
            }

            // 8. SAUVEGARDE ET CONVERSION EN DTO
            DocumentCategory savedCategory = categoryRepository.save(category);
            DocumentCategoryDTO savedCategoryDTO = categoryMapper.toDTO(savedCategory);

            log.info("✅ Catégorie créée avec succès - ID: {}, Nom: {}, Type: {}",
                    savedCategoryDTO.getId(), savedCategoryDTO.getName(),
                    savedCategoryDTO.isShared() ? "PARTAGÉE" : "SPÉCIFIQUE");

            return savedCategoryDTO;

        } catch (RuntimeException e) {
            log.error("❌ Erreur métier lors de la création de la catégorie", e);
            throw e;
        } catch (Exception e) {
            log.error("❌ Erreur technique lors de la création de la catégorie", e);
            throw new RuntimeException("Erreur technique lors de la création de la catégorie: " + e.getMessage());
        }
    }

    public DocumentCategoryDTO createCategory(DocumentCategory category) {
        log.info("🚀 Création catégorie avec entity: {}", category.getName());

        try {
            // Validation
            if (category.getCompany() == null && category.getClient() == null) {
                throw new RuntimeException("Au moins une entreprise (client ou company) doit être spécifiée");
            }

            // Si seulement company est fourni, trouver le client
            if (category.getClient() == null && category.getCompany() != null) {
                Company clientCompany = companyAccessService.getClientCompany(category.getCompany().getId());
                category.setClient(clientCompany);
                log.info("👑 Client auto-détecté: {}", clientCompany.getName());
            }

            // Vérifier les doublons
            boolean duplicateExists = checkDuplicateCategory(
                    category.getClient(),
                    category.getCompany(),
                    category.getName(),
                    category.getCompany() == null,
                    null
            );

            if (duplicateExists) {
                throw new RuntimeException("Une catégorie avec le nom '" + category.getName() + "' existe déjà");
            }

            DocumentCategory savedCategory = categoryRepository.save(category);
            DocumentCategoryDTO savedCategoryDTO = categoryMapper.toDTO(savedCategory);

            log.info("✅ Catégorie créée avec entity - ID: {}", savedCategoryDTO.getId());

            return savedCategoryDTO;

        } catch (RuntimeException e) {
            log.error("❌ Erreur métier lors de la création de la catégorie avec entity", e);
            throw e;
        } catch (Exception e) {
            log.error("❌ Erreur technique lors de la création de la catégorie avec entity", e);
            throw new RuntimeException("Erreur technique lors de la création de la catégorie: " + e.getMessage());
        }
    }

    public DocumentCategoryDTO updateCategory(DocumentCategory category) {
        log.info("🔄 Mise à jour catégorie ID: {}", category.getId());

        try {
            DocumentCategory existingCategory = categoryRepository.findById(category.getId())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'id: " + category.getId()));

            // Vérifier les doublons
            boolean duplicateExists = checkDuplicateCategory(
                    existingCategory.getClient(),
                    existingCategory.getCompany(),
                    category.getName(),
                    existingCategory.isShared(),
                    category.getId()
            );

            if (duplicateExists) {
                throw new RuntimeException("Une catégorie avec le nom '" + category.getName() + "' existe déjà");
            }

            // Mettre à jour les champs modifiables
            existingCategory.setName(category.getName());
            existingCategory.setDescription(category.getDescription());
            existingCategory.setDisplayOrder(category.getDisplayOrder());

            DocumentCategory updatedCategory = categoryRepository.save(existingCategory);
            DocumentCategoryDTO updatedCategoryDTO = categoryMapper.toDTO(updatedCategory);

            log.info("✅ Catégorie mise à jour avec succès - ID: {}", updatedCategoryDTO.getId());

            return updatedCategoryDTO;

        } catch (RuntimeException e) {
            log.error("❌ Erreur métier lors de la mise à jour de la catégorie ID: {}", category.getId(), e);
            throw e;
        } catch (Exception e) {
            log.error("❌ Erreur technique lors de la mise à jour de la catégorie ID: {}", category.getId(), e);
            throw new RuntimeException("Erreur technique lors de la mise à jour de la catégorie: " + e.getMessage());
        }
    }

    public void deleteCategory(Long categoryId) {
        log.info("🗑️ Suppression catégorie ID: {}", categoryId);

        try {
            if (!categoryRepository.existsById(categoryId)) {
                throw new RuntimeException("Catégorie non trouvée avec l'id: " + categoryId);
            }

            List<DocumentSubCategory> subCategories = subCategoryRepository.findByCategoryIdOrderByDisplayOrderAsc(categoryId);
            if (!subCategories.isEmpty()) {
                throw new RuntimeException("Impossible de supprimer la catégorie car elle contient " + subCategories.size() + " sous-catégorie(s)");
            }

            categoryRepository.deleteById(categoryId);
            log.info("✅ Catégorie supprimée avec succès - ID: {}", categoryId);

        } catch (RuntimeException e) {
            log.error("❌ Erreur métier lors de la suppression de la catégorie ID: {}", categoryId, e);
            throw e;
        } catch (Exception e) {
            log.error("❌ Erreur technique lors de la suppression de la catégorie ID: {}", categoryId, e);
            throw new RuntimeException("Erreur technique lors de la suppression de la catégorie: " + e.getMessage());
        }
    }

    public List<DocumentSubCategory> getSubCategoriesByCategory(Long categoryId) {
        log.info("📂 Récupération des sous-catégories pour la catégorie ID: {}", categoryId);

        try {
            List<DocumentSubCategory> subCategories = subCategoryRepository.findByCategoryIdOrderByDisplayOrderAsc(categoryId);
            log.info("✅ {} sous-catégorie(s) trouvée(s) pour la catégorie ID: {}", subCategories.size(), categoryId);
            return subCategories;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des sous-catégories pour la catégorie ID: {}", categoryId, e);
            throw new RuntimeException("Erreur lors de la récupération des sous-catégories: " + e.getMessage());
        }
    }

    public DocumentSubCategory createSubCategory(Long categoryId, DocumentSubCategory subCategory) {
        log.info("🚀 Création sous-catégorie pour la catégorie ID: {}", categoryId);

        try {
            DocumentCategory category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'id: " + categoryId));

            // Vérifier les doublons
            if (subCategoryRepository.existsByCategoryIdAndNameIgnoreCase(categoryId, subCategory.getName())) {
                throw new RuntimeException("Une sous-catégorie avec le nom '" + subCategory.getName() + "' existe déjà dans cette catégorie");
            }

            subCategory.setCategory(category);
            DocumentSubCategory savedSubCategory = subCategoryRepository.save(subCategory);

            log.info("✅ Sous-catégorie créée avec succès - ID: {}, Nom: {}",
                    savedSubCategory.getId(), savedSubCategory.getName());

            return savedSubCategory;

        } catch (RuntimeException e) {
            log.error("❌ Erreur métier lors de la création de la sous-catégorie", e);
            throw e;
        } catch (Exception e) {
            log.error("❌ Erreur technique lors de la création de la sous-catégorie", e);
            throw new RuntimeException("Erreur technique lors de la création de la sous-catégorie: " + e.getMessage());
        }
    }

    // ⭐ MÉTHODES POUR LA GESTION AVANCÉE

    public List<DocumentCategoryDTO> getSharedCategories(Long companyId) {
        log.info("📂 Récupération des catégories partagées pour l'entreprise ID: {}", companyId);

        try {
            Company clientCompany = companyAccessService.getClientCompany(companyId);
            List<DocumentCategory> categories = categoryRepository.findByClientAndCompanyIsNull(clientCompany);

            List<DocumentCategoryDTO> categoryDTOs = categories.stream()
                    .map(categoryMapper::toDTO)
                    .collect(Collectors.toList());

            log.info("✅ {} catégorie(s) partagée(s) trouvée(s)", categoryDTOs.size());
            return categoryDTOs;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des catégories partagées", e);
            throw new RuntimeException("Erreur lors de la récupération des catégories partagées: " + e.getMessage());
        }
    }

    public List<DocumentCategoryDTO> getCompanySpecificCategories(Long companyId) {
        log.info("📂 Récupération des catégories spécifiques pour l'entreprise ID: {}", companyId);

        try {
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Entreprise non trouvée avec l'id: " + companyId));

            List<DocumentCategory> categories = categoryRepository.findByCompany(company);
            List<DocumentCategoryDTO> categoryDTOs = categories.stream()
                    .map(categoryMapper::toDTO)
                    .collect(Collectors.toList());

            log.info("✅ {} catégorie(s) spécifique(s) trouvée(s)", categoryDTOs.size());

            return categoryDTOs;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des catégories spécifiques", e);
            throw new RuntimeException("Erreur lors de la récupération des catégories spécifiques: " + e.getMessage());
        }
    }

    public DocumentCategoryDTO createSharedCategory(String name, String description, Integer displayOrder, Long companyId) {
        log.info("🚀 Création catégorie partagée - Nom: {}, Entreprise ID: {}", name, companyId);

        try {
            Company clientCompany = companyAccessService.getClientCompany(companyId);

            if (!clientCompany.canManageCompanies()) {
                throw new RuntimeException("Ce client ne peut pas créer de catégories partagées");
            }

            // Vérifier les doublons
            boolean duplicateExists = checkDuplicateCategory(clientCompany, null, name, true, null);
            if (duplicateExists) {
                throw new RuntimeException("Une catégorie partagée avec le nom '" + name + "' existe déjà");
            }

            DocumentCategory category = new DocumentCategory();
            category.setClient(clientCompany);
            category.setCompany(null);
            category.setName(name);
            category.setDescription(description);
            category.setDisplayOrder(displayOrder != null ? displayOrder : 0);

            DocumentCategory savedCategory = categoryRepository.save(category);
            DocumentCategoryDTO savedCategoryDTO = categoryMapper.toDTO(savedCategory);

            log.info("✅ Catégorie partagée créée avec succès - ID: {}", savedCategoryDTO.getId());

            return savedCategoryDTO;

        } catch (RuntimeException e) {
            log.error("❌ Erreur métier lors de la création de la catégorie partagée", e);
            throw e;
        } catch (Exception e) {
            log.error("❌ Erreur technique lors de la création de la catégorie partagée", e);
            throw new RuntimeException("Erreur technique lors de la création de la catégorie partagée: " + e.getMessage());
        }
    }

    public boolean hasAccessToCategory(Long companyId, Long categoryId) {
        log.info("🔐 Vérification accès - Entreprise ID: {}, Catégorie ID: {}", companyId, categoryId);

        try {
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));
            Company clientCompany = companyAccessService.getClientCompany(companyId);

            boolean hasAccess = categoryRepository.hasAccessToCategory(categoryId, clientCompany.getId(), company.getId());

            log.info("🔓 Accès {} pour l'entreprise ID: {} à la catégorie ID: {}",
                    hasAccess ? "AUTORISÉ" : "REFUSÉ", companyId, categoryId);

            return hasAccess;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la vérification d'accès", e);
            return false;
        }
    }

    // ⭐⭐ MÉTHODE POUR VÉRIFIER LES DOUBLONS ⭐⭐
    private boolean checkDuplicateCategory(Company client, Company company, String name, boolean shared, Long excludeId) {
        log.debug("🔍 Vérification doublon - Client: {}, Company: {}, Nom: {}, Shared: {}, Exclude: {}",
                client != null ? client.getId() : "null",
                company != null ? company.getId() : "null",
                name, shared, excludeId);

        try {
            if (shared) {
                // Vérifier les catégories partagées du client
                List<DocumentCategory> sharedCategories = categoryRepository.findByClientAndCompanyIsNull(client);
                return sharedCategories.stream()
                        .anyMatch(cat -> cat.getName().equalsIgnoreCase(name) &&
                                (excludeId == null || !cat.getId().equals(excludeId)));
            } else {
                // Vérifier les catégories spécifiques à l'entreprise
                List<DocumentCategory> companyCategories = categoryRepository.findByCompany(company);
                return companyCategories.stream()
                        .anyMatch(cat -> cat.getName().equalsIgnoreCase(name) &&
                                (excludeId == null || !cat.getId().equals(excludeId)));
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de la vérification des doublons", e);
            return false;
        }
    }

    public DocumentCategoryDTO updateCategoryFromDTO(DocumentCategoryDTO categoryDTO) {
        log.info("🔄 Mise à jour catégorie depuis DTO - ID: {}", categoryDTO.getId());

        try {
            DocumentCategory existingCategory = categoryRepository.findById(categoryDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'id: " + categoryDTO.getId()));

            // Vérifier les doublons
            boolean duplicateExists = checkDuplicateCategory(
                    existingCategory.getClient(),
                    existingCategory.getCompany(),
                    categoryDTO.getName(),
                    existingCategory.isShared(),
                    categoryDTO.getId()
            );

            if (duplicateExists) {
                throw new RuntimeException("Une catégorie avec le nom '" + categoryDTO.getName() + "' existe déjà");
            }

            // Mettre à jour les champs modifiables
            existingCategory.setName(categoryDTO.getName());
            existingCategory.setDescription(categoryDTO.getDescription());
            existingCategory.setDisplayOrder(categoryDTO.getDisplayOrder());

            DocumentCategory updatedCategory = categoryRepository.save(existingCategory);
            DocumentCategoryDTO updatedCategoryDTO = categoryMapper.toDTO(updatedCategory);

            log.info("✅ Catégorie mise à jour depuis DTO avec succès - ID: {}", updatedCategoryDTO.getId());

            return updatedCategoryDTO;

        } catch (RuntimeException e) {
            log.error("❌ Erreur métier lors de la mise à jour de la catégorie depuis DTO ID: {}", categoryDTO.getId(), e);
            throw e;
        } catch (Exception e) {
            log.error("❌ Erreur technique lors de la mise à jour de la catégorie depuis DTO ID: {}", categoryDTO.getId(), e);
            throw new RuntimeException("Erreur technique lors de la mise à jour de la catégorie: " + e.getMessage());
        }
    }
}