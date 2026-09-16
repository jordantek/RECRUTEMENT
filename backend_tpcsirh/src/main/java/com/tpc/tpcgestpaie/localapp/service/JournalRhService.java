package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.rh.CategorieEvenementDto;
import com.tpc.tpcgestpaie.localapp.dto.rh.JournalRhDto;
import com.tpc.tpcgestpaie.localapp.model.CategorieEvenement;
import com.tpc.tpcgestpaie.localapp.model.JournalRh;
import com.tpc.tpcgestpaie.localapp.repository.CategorieEvenementRepository;
import com.tpc.tpcgestpaie.localapp.repository.JournalRhRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JournalRhService {

    private final JournalRhRepository repository;
    private final JournalRhRepository journalRhRepository;
    private final CategorieEvenementRepository categorieEvenementRepository;

    public JournalRhService(JournalRhRepository repository, JournalRhRepository journalRhRepository, CategorieEvenementRepository categorieEvenementRepository) {
        this.repository = repository;
        this.journalRhRepository = journalRhRepository;
        this.categorieEvenementRepository = categorieEvenementRepository;
    }

    @Transactional
    public List<JournalRh> findAll() {
        return repository.findAllByDeletedAtIsNull();
    }

    public Optional<JournalRh> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<JournalRh> findDuplicate(LocalDate date, String contenu, Long categorieId, Long userId) {
        return repository.findByDateAndContenuAndCategorieAndUser(date, contenu, categorieId, userId);
    }


    public JournalRh save(JournalRh journalRh) {
        return repository.save(journalRh);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<JournalRhDto> findAllDto(Long userId) {
        // 🔥 CORRECTION : Récupérer seulement les journaux de l'utilisateur spécifié
        List<JournalRh> journals = journalRhRepository.findByUserId(userId);

        return journals.stream().map(journal -> {
            JournalRhDto dto = new JournalRhDto();
            dto.setId(journal.getId());
            dto.setDate(journal.getDate());
            dto.setContenu(journal.getContenu());

            CategorieEvenement cat = journal.getCategorieEvenement();
            if (cat != null) {
                dto.setCategorieEvenement(new CategorieEvenementDto(cat.getId(), cat.getLibelle()));
            }
            return dto;
        }).toList();
    }
    @Transactional
    public Optional<JournalRhDto> findByIdDto(Long id) {
        return journalRhRepository.findById(id)
                .map(journal -> {
                    JournalRhDto dto = new JournalRhDto();
                    dto.setId(journal.getId());
                    dto.setDate(journal.getDate());
                    dto.setContenu(journal.getContenu());

                    CategorieEvenement cat = journal.getCategorieEvenement();
                    if (cat != null) {
                        dto.setCategorieEvenement(new CategorieEvenementDto(cat.getId(), cat.getLibelle()));
                    }
                    return dto;
                });
    }

    @Transactional
    public JournalRhDto updateJournal(Long id, JournalRhDto updatedDto) {
        JournalRh journal = journalRhRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Journal non trouvé"));

        // Charger la catégorie à jour
        CategorieEvenement cat = categorieEvenementRepository.findById(updatedDto.getCategorieEvenement().getId())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));

        // Mise à jour des champs
        journal.setDate(updatedDto.getDate());
        journal.setContenu(updatedDto.getContenu());
        journal.setCategorieEvenement(cat);
        journal.setUpdatedAt(LocalDateTime.now());

        // Sauvegarder la mise à jour
        JournalRh saved = journalRhRepository.save(journal);

        // Mapper en DTO avant de retourner
        JournalRhDto resultDto = new JournalRhDto();
        resultDto.setId(saved.getId());
        resultDto.setDate(saved.getDate());
        resultDto.setContenu(saved.getContenu());
        resultDto.setUpdatedAt(saved.getUpdatedAt());

        CategorieEvenementDto catDto = new CategorieEvenementDto();
        catDto.setId(cat.getId());
        catDto.setLibelle(cat.getLibelle());
        resultDto.setCategorieEvenement(catDto);

        return resultDto;
    }

    @Transactional
    public Optional<JournalRhDto> findDtoById(Long id) {
        Optional<JournalRh> journalOpt = journalRhRepository.findById(id);
        if (journalOpt.isEmpty()) return Optional.empty();

        JournalRh j = journalOpt.get();

        // forcer le chargement
        CategorieEvenement cat = j.getCategorieEvenement();
        CategorieEvenementDto catDto = new CategorieEvenementDto(cat.getId(), cat.getLibelle());

        JournalRhDto dto = new JournalRhDto();
        dto.setId(j.getId());
        dto.setDate(j.getDate());
        dto.setContenu(j.getContenu());
        dto.setCategorieEvenement(catDto);

        return Optional.of(dto);
    }



}
