package com.tpc.tpcgestpaie.localapp.service;


import com.tpc.tpcgestpaie.localapp.dto.DocumentsModel.DocumentModelRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.DocumentsModel.DocumentModelResponseDTO;
import com.tpc.tpcgestpaie.localapp.model.DocumentModel;
import com.tpc.tpcgestpaie.localapp.model.DocumentsCategory;
import com.tpc.tpcgestpaie.localapp.repository.DocumentModelRepository;
import com.tpc.tpcgestpaie.localapp.repository.DocumentsCategoryRepository;
import com.tpc.tpcgestpaie.localapp.util.RandomCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentModelService {

    private final DocumentModelRepository repository;
    private final DocumentsCategoryRepository categoryRepository;

    // ==============================
    // Mapper
    // ==============================

    public static DocumentModelResponseDTO mapToResponse(DocumentModel model) {
        return new DocumentModelResponseDTO(
                model.getId(),
                model.getName(),
                model.getCode(),
                model.getCategoryId(),
                model.getCategoryName(),
                model.getDescription(),
                model.getIsActive(),
                model.getOcrEnabled(),
                model.getAiExtraction(),
                model.getShowOnEmployeeProfile(),
                model.getCompanyId(),
                model.getCreatedAt()
        );
    }

    // ==============================
    // CREATE
    // ==============================

    public DocumentModelResponseDTO create(DocumentModelRequestDTO request) {

        String code = request.code();

        // générer un code si null
        if (code == null || code.isBlank()) {
            code = generateUniqueCode(request.companyId());
        }

        // vérifier unicité
        if (repository.existsByCodeAndCompanyId(code, request.companyId())) {
            throw new RuntimeException("Ce code de modèle de document existe déjà");
        }

        // vérifier que la catégorie existe
        DocumentsCategory category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("La catégorie du document est introuvable"));

        DocumentModel model = DocumentModel.builder()
                .name(request.name().trim())
                .code(code)
                .categoryId(category.getId())
                .categoryName(category.getName())
                .description(request.description())
                .isActive(true)
                .ocrEnabled( false)
                .aiExtraction( false)
                .showOnEmployeeProfile(request.showOnEmployeeProfile() != null ? request.showOnEmployeeProfile() : true)
                .companyId(request.companyId())
                .build();

        return mapToResponse(repository.save(model));
    }

    // ==============================
    // FIND ALL
    // ==============================

    public List<DocumentModelResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(DocumentModelService::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==============================
    // FIND BY ID
    // ==============================

    public DocumentModelResponseDTO findById(Long id) {

        DocumentModel model = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modèle de document introuvable"));

        return mapToResponse(model);
    }

    // ==============================
    // UPDATE
    // ==============================

    public DocumentModelResponseDTO update(Long id, DocumentModelRequestDTO request) {

        DocumentModel model = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modèle de document introuvable"));

        // vérifier la société
        if (model.getCompanyId() == null) {
            throw new RuntimeException("Vous ne pouvez pas modifier ce modèle");
        }

        if (!model.getCompanyId().equals(request.companyId())) {
            throw new RuntimeException("Vous ne pouvez pas modifier ce modèle");
        }

        // vérifier la catégorie
        DocumentsCategory category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("La catégorie du document est introuvable"));

        model.setName(request.name());
        model.setDescription(request.description());
        model.setCategoryId(category.getId());
        model.setCategoryName(category.getName());
        model.setIsActive(true);
        model.setOcrEnabled(false);
        model.setAiExtraction(false);
        model.setShowOnEmployeeProfile(request.showOnEmployeeProfile());
        return mapToResponse(repository.save(model));
    }

    // ==============================
    // FIND GLOBAL + COMPANY
    // ==============================

    public List<DocumentModelResponseDTO> findAllByCompanyOrGlobal(Long companyId) {

        return repository.findByCompanyIdOrCompanyIdIsNull(companyId)
                .stream()
                .map(DocumentModelService::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==============================
    // DELETE (SOFT)
    // ==============================

    public void delete(Long id) {

        DocumentModel model = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modèle de document introuvable"));
        model.softDelete();

        repository.save(model);
    }

    // ==============================
    // CODE UNIQUE
    // ==============================

    private String generateUniqueCode(Long companyId) {

        String code;

        do {
            code = RandomCode.randomCode(3, 3, "-");
        } while (repository.existsByCodeAndCompanyId(code, companyId));

        return code;
    }



}
