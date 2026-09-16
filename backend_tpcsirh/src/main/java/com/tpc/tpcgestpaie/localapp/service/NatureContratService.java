package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.model.NatureContrat;
import com.tpc.tpcgestpaie.localapp.repository.NatureContratRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NatureContratService {

    private final NatureContratRepository natureContratRepository;

    public NatureContratService(NatureContratRepository natureContratRepository) {
        this.natureContratRepository = natureContratRepository;
    }

    public List<NatureContrat> findAll() {
        return natureContratRepository.findAll();
    }

    public Optional<NatureContrat> findById(Long id) {
        return natureContratRepository.findById(id);
    }

    public NatureContrat save(NatureContrat natureContrat) {
        return natureContratRepository.save(natureContrat);
    }

    public void deleteById(Long id) {
        natureContratRepository.deleteById(id);
    }

    public Optional<NatureContrat> findByLibelle(String libelle) {
        return natureContratRepository.findByLibelle(libelle.trim());
    }

    public boolean existsByLibelle(String libelle) {
        return natureContratRepository.existsByLibelle(libelle.trim());
    }
}
