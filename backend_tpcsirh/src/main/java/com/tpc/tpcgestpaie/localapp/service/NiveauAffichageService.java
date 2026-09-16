package com.tpc.tpcgestpaie.localapp.service;


import com.tpc.tpcgestpaie.localapp.model.NiveauAffichage;
import com.tpc.tpcgestpaie.localapp.repository.NiveauAffichageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NiveauAffichageService {

    private final NiveauAffichageRepository niveauAffichageRepository;

    public NiveauAffichageService(NiveauAffichageRepository niveauAffichageRepository) {
        this.niveauAffichageRepository = niveauAffichageRepository;
    }


    public List<NiveauAffichage> findAll() {
        return niveauAffichageRepository.findAll();
    }

    public Optional<NiveauAffichage> findById(Long id) {
        return niveauAffichageRepository.findById(id);
    }

    public NiveauAffichage save(NiveauAffichage niveauAffichage) {
        return niveauAffichageRepository.save(niveauAffichage);
    }

    public void deleteById(Long id) {
        niveauAffichageRepository.deleteById(id);
    }

    public Optional<NiveauAffichage> findByLibelle(String libelle) {
        return niveauAffichageRepository.findByLibelle(libelle.trim());
    }

    public boolean existsByLibelle(String name) {
        return niveauAffichageRepository.existsByLibelle(name.trim());
    }
}
