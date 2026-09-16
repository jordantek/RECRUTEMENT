package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.model.ModeDePaiement;
import com.tpc.tpcgestpaie.localapp.repository.ModeDePaiementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModeDePaiementService {

    private final ModeDePaiementRepository modeDePaiementRepository;

    public ModeDePaiementService(ModeDePaiementRepository modeDePaiementRepository) {
        this.modeDePaiementRepository = modeDePaiementRepository;
    }

    public List<ModeDePaiement> findAll() {
        return modeDePaiementRepository.findAll();
    }

    public Optional<ModeDePaiement> findById(Long id) {
        return modeDePaiementRepository.findById(id);
    }

    public ModeDePaiement save(ModeDePaiement modeDePaiement) {
        return modeDePaiementRepository.save(modeDePaiement);
    }

    public void deleteById(Long id) {
        modeDePaiementRepository.deleteById(id);
    }

    public Optional<ModeDePaiement> findByLibelle(String libelle) {
        return modeDePaiementRepository.findByLibelle(libelle.trim());
    }

    public boolean existsByLibelle(String libelle) {
        return modeDePaiementRepository.existsByLibelle(libelle.trim());
    }
}
