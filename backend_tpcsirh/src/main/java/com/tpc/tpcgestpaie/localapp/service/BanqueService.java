package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.model.Banque;
import com.tpc.tpcgestpaie.localapp.repository.BanqueRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BanqueService {

    private final BanqueRepository banqueRepository;

    public BanqueService(BanqueRepository banqueRepository) {
        this.banqueRepository = banqueRepository;
    }

    public List<Banque> findAll() {
        return banqueRepository.findAll();
    }

    public Optional<Banque> findById(Long id) {
        return banqueRepository.findById(id);
    }

    public Banque save(Banque banque) {
        return banqueRepository.save(banque);
    }

    public void deleteById(Long id) {
        banqueRepository.deleteById(id);
    }

    public Optional<Banque> findByName(String name) {
        return banqueRepository.findByName(name.trim());
    }

    public boolean existsByName(String name) {
        return banqueRepository.existsByName(name.trim());
    }
}
