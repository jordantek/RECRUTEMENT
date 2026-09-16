package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.model.Domaine;
import com.tpc.tpcgestpaie.localapp.repository.DomaineRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DomaineService {

    private final DomaineRepository domaineRepository;

    public DomaineService(DomaineRepository domaineRepository) {
        this.domaineRepository = domaineRepository;
    }

    public List<Domaine> findAll() {
        return domaineRepository.findAll();
    }

    public Optional<Domaine> findById(Long id) {
        return domaineRepository.findById(id);
    }

    public Domaine save(Domaine domaine) {
        return domaineRepository.save(domaine);
    }

    public void deleteById(Long id) {
        domaineRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return domaineRepository.existsByName(name);
    }


}
