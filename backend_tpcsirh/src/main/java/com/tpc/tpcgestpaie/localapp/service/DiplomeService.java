package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.model.Diplome;
import com.tpc.tpcgestpaie.localapp.repository.DiplomeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiplomeService {

    private final DiplomeRepository diplomeRepository;

    public DiplomeService(DiplomeRepository diplomeRepository) {
        this.diplomeRepository = diplomeRepository;
    }

    public List<Diplome> findAll() {
        return diplomeRepository.findAll();
    }

    public Optional<Diplome> findById(Long id) {
        return diplomeRepository.findById(id);
    }

    public Diplome save(Diplome diplome) {
        return diplomeRepository.save(diplome);
    }

    public void deleteById(Long id) {
        diplomeRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return diplomeRepository.existsByName(name);
    }



}
