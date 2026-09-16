package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.model.ActivityArea;
import com.tpc.tpcgestpaie.localapp.repository.ActivityAreaRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
@Service // Indique que cette classe est un service Spring (logique métier)
public class ActivityAreaService {

    private final ActivityAreaRepository repository;
    public ActivityAreaService(ActivityAreaRepository repository) {
        this.repository = repository;
    }

    // Récupère la liste complète des zones d'activité
    @Transactional
    public List<ActivityArea> getAll() {
        return repository.findAll();
    }

    // NOUVEAU: Récupère les zones d'activité avec pagination
    @Transactional
    public Page<ActivityArea> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    // Récupère une zone d'activité par son ID
    // Lève une exception si elle n'existe pas
    public ActivityArea getById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    // Crée une nouvelle zone d'activité
    public ActivityArea create(ActivityArea area) {
        return repository.save(area);
    }

    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }


    // Met à jour une zone d'activité existante
    public ActivityArea update(Integer id, ActivityArea data) {
        ActivityArea existing = getById(id);
        existing.setName(data.getName());
        existing.setDescription(data.getDescription());
        return repository.save(existing);
    }

    // Supprime une zone d'activité par son ID
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
