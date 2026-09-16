package com.tpc.tpcgestpaie.localapp.service;


import com.tpc.tpcgestpaie.localapp.model.ColonneAffichage;
import com.tpc.tpcgestpaie.localapp.repository.ColonneAffichageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ColonneAffichageService {

    private final ColonneAffichageRepository colonneAffichageRepository;

    public ColonneAffichageService(ColonneAffichageRepository colonneAffichageRepository) {
        this.colonneAffichageRepository = colonneAffichageRepository;
    }

    public List<ColonneAffichage> findAll() {
        return colonneAffichageRepository.findAll();
    }

    public Optional<ColonneAffichage> findById(Long id) {
        return colonneAffichageRepository.findById(id);
    }

    public ColonneAffichage save(ColonneAffichage colonneAffichage) {
        return colonneAffichageRepository.save(colonneAffichage);
    }

    public void deleteById(Long id) {
        colonneAffichageRepository.deleteById(id);
    }

    public Optional<ColonneAffichage> findByLibelle(String libelle) {
        return colonneAffichageRepository.findByLibelle(libelle.trim());
    }

    public boolean existsByLibelle(String name) {
        return colonneAffichageRepository.existsByLibelle(name.trim());
    }
}
