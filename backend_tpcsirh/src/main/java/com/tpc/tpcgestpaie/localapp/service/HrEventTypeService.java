package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.event.HrEventTypeResponsDto;
import com.tpc.tpcgestpaie.localapp.model.HrEventType;
import com.tpc.tpcgestpaie.localapp.repository.HrEventTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HrEventTypeService {

    private final HrEventTypeRepository repository;

    // 🔹 Créer un type d'évènement
    public HrEventType create(HrEventType eventType) {

        if (repository.existsBySlug(eventType.getSlug())) {
            throw new RuntimeException("Un type avec ce slug existe déjà");
        }

        return repository.save(eventType);
    }

    // 🔹 Récupérer tous les types actifs
    public List<HrEventType> getAll() {
        return repository.findAll()
                .stream()
                .filter(e -> e.getDeletedAt() == null)
                .toList();
    }

    // 🔹 Récupérer par ID
    public HrEventType getById(Long id) {
        return repository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Type d'évènement introuvable"));
    }

    // 🔹 Récupérer par slug
    public HrEventType getBySlug(String slug) {
        return repository.findBySlug(slug)
                .filter(e -> e.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Type d'évènement introuvable"));
    }

    // 🔹 Mettre à jour
    public HrEventType update(Long id, HrEventType updatedData) {

        HrEventType eventType = getById(id);

        eventType.setLabel(updatedData.getLabel());
        eventType.setDescription(updatedData.getDescription());
        eventType.setIcon(updatedData.getIcon());
        eventType.setColor(updatedData.getColor());
        eventType.setActionRequired(updatedData.getActionRequired());
        eventType.setRecurring(updatedData.getRecurring());
        eventType.setEnabled(updatedData.getEnabled());

        return repository.save(eventType);
    }

    // 🔹 Soft delete
    public void delete(Long id) {
        HrEventType eventType = getById(id);
        eventType.softDelete();
        repository.save(eventType);
    }

    // 🔹 Activer / désactiver
    public HrEventType toggleEnabled(Long id) {

        HrEventType eventType = getById(id);
        eventType.setEnabled(!eventType.getEnabled());

        return repository.save(eventType);
    }

    // Méthode utilitaire pour convertir une entité en DTO
    public HrEventTypeResponsDto toDto(HrEventType eventType) {
        return new HrEventTypeResponsDto(
                eventType.getId(),
                eventType.getSlug(),
                eventType.getLabel(),
                eventType.getDescription(),
                eventType.getIcon(),
                eventType.getColor(),
                eventType.getActionRequired(),
                eventType.getRecurring(),
                eventType.getEnabled(),
                eventType.getCreatedAt(),
                eventType.getUpdatedAt(),
                eventType.getDeletedAt()
        );
    }


    // 🔹 Convertir DTO -> entité
    private HrEventType fromDto(HrEventTypeResponsDto dto) {
        return HrEventType.builder()
                .id(dto.id())
                .slug(dto.slug())
                .label(dto.label())
                .description(dto.description())
                .icon(dto.icon())
                .color(dto.color())
                .actionRequired(dto.actionRequired())
                .recurring(dto.recurring())
                .enabled(dto.enabled())
                .createdAt(dto.createdAt())
                .updatedAt(dto.updatedAt())
                .deletedAt(dto.deletedAt())
                .build();
    }

}
