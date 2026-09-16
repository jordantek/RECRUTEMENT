package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.model.CategorieEvenement;
import com.tpc.tpcgestpaie.localapp.repository.CategorieEvenementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorieEvenementService {

    private final CategorieEvenementRepository repository;

    public CategorieEvenementService(CategorieEvenementRepository repository) {
        this.repository = repository;
    }

    public List<CategorieEvenement> findAll() {
        return repository.findAllByDeletedAtIsNull();
    }

    public Optional<CategorieEvenement> findById(Long id) {
        return repository.findById(id);
    }

    public CategorieEvenement save(CategorieEvenement categorie) {
        return repository.save(categorie);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public boolean existsByLibelle(String libelle) {
        return repository.existsByLibelle(libelle);
    }
}
