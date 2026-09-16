package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.DocumentCategory.DocumentCategoryRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.DocumentCategory.DocumentCategoryResponseDTO;
import com.tpc.tpcgestpaie.localapp.model.DocumentsCategory;
import com.tpc.tpcgestpaie.localapp.repository.DocumentsCategoryRepository;
import com.tpc.tpcgestpaie.localapp.util.RandomCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentsCategoryService {

    private final DocumentsCategoryRepository repository;
    // ==============================
    // Mapper public
    // ==============================
    public static DocumentCategoryResponseDTO mapToResponse(DocumentsCategory category) {
        return new DocumentCategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getCode(),
                category.getDescription(),
                category.getColor(),
                category.getRetentionPeriod(),
                category.getIsActive(),
                category.getCreatedAt()
        );
    }

    // ==============================
    // CRUD
    // ==============================

    public DocumentCategoryResponseDTO create(DocumentCategoryRequestDTO request) {
        String code = request.code();

        // générer un code si null
        if (code == null || code.isBlank()) {
            code = generateUniqueCode(request.companyId());
        }

        // vérifier unicité
        if (repository.existsByCodeAndCompanyId(code, request.companyId())) {
            throw new RuntimeException("Ce code de catégorie de document existe déjà");
        }

        // Générer une couleur aléatoire à partir du nom si color non fourni
        String color = request.color();
        if (color == null || color.isBlank()) {
            color = generateColorFromName(request.name());
        }

        DocumentsCategory category = DocumentsCategory.builder()
                .name(request.name().trim())
                .code(code)
                .description(request.description())
                .color(color)
                .retentionPeriod(request.retentionPeriod())
                .isActive(true)
                .companyId(request.companyId())
                .build();

        return mapToResponse(repository.save(category));
    }

    public List<DocumentCategoryResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(DocumentsCategoryService::mapToResponse)
                .collect(Collectors.toList());
    }

    public DocumentCategoryResponseDTO findById(Long id) {

        DocumentsCategory category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return mapToResponse(category);
    }

    public DocumentCategoryResponseDTO update(Long id, DocumentCategoryRequestDTO request) {

        DocumentsCategory category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        // Vérifier company
        if (category.getCompanyId() == null) {
            throw new RuntimeException("Vous ne pouvez pas modifier cette catégorie");
        }

        if (!category.getCompanyId().equals(request.companyId())) {
            throw new RuntimeException("Vous ne pouvez pas modifier cette catégorie");
        }

        category.setName(request.name());
        category.setDescription(request.description());
        category.setRetentionPeriod(request.retentionPeriod());
        return mapToResponse(repository.save(category));
    }

    public List<DocumentCategoryResponseDTO> findAllByCompanyOrGlobal(Long companyId) {

        return repository.findByCompanyIdOrCompanyIdIsNull(companyId)
                .stream()
                .map(DocumentsCategoryService::mapToResponse)
                .collect(Collectors.toList());
    }
    public void delete(Long id) {

        DocumentsCategory category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        category.softDelete();

        repository.save(category);
    }


    private String generateUniqueCode(Long companyId) {

        String code;
        do {
            code = RandomCode.randomCode(3,3,"-");
        } while (repository.existsByCodeAndCompanyId(code, companyId));
        return code;
    }

    private String generateColorFromName(String name) {
        // Créer un hash du nom
        int hash = name.hashCode();
        // Transformer en valeurs RGB entre 0 et 255
        int r = (hash & 0xFF0000) >> 16;
        int g = (hash & 0x00FF00) >> 8;
        int b = (hash & 0x0000FF);
        // Formater en hex
        return String.format("#%02X%02X%02X", r, g, b);
    }
}