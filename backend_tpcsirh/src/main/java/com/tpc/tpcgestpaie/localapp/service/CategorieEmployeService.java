package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.model.CategorieEmploye;
import com.tpc.tpcgestpaie.localapp.repository.CategorieEmployeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorieEmployeService {

    private final CategorieEmployeRepository categorieEmployeRepository;

    // Constructeur avec injection explicite (bonnes pratiques Spring Boot)
    public CategorieEmployeService(CategorieEmployeRepository categorieEmployeRepository) {
        this.categorieEmployeRepository = categorieEmployeRepository;
    }

    public List<CategorieEmploye> findAll() {
        return categorieEmployeRepository.findAll();
    }

    public Optional<CategorieEmploye> findById(Long id) {
        return categorieEmployeRepository.findById(id);
    }

    public CategorieEmploye save(CategorieEmploye categorieEmploye) {
        return categorieEmployeRepository.save(categorieEmploye);
    }

    public CategorieEmploye getById(Long id) {
        return categorieEmployeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable avec ID: " + id));
    }

    public void deleteById(Long id) {
        categorieEmployeRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return categorieEmployeRepository.existsByName(name);
    }
}
