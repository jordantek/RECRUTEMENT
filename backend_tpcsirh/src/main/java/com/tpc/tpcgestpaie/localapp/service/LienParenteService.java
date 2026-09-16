package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.model.LienParente;
import com.tpc.tpcgestpaie.localapp.repository.LienParenteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LienParenteService {

    private final LienParenteRepository lienParenteRepository;

    public LienParenteService(LienParenteRepository lienParenteRepository) {
        this.lienParenteRepository = lienParenteRepository;
    }


    public List<LienParente> findAll() {
        return lienParenteRepository.findAll();
    }

    public Optional<LienParente> findById(Long id) {
        return lienParenteRepository.findById(id);
    }

    public LienParente findById2(Long id) {
        return lienParenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "LienParente avec l'id " + id + " introuvable."
                ));
    }


        public LienParente save(LienParente lienParente) {
        return lienParenteRepository.save(lienParente);
    }

    public void deleteById(Long id) {
        lienParenteRepository.deleteById(id);
    }

    public boolean existsByLibelle(String name) {
        return lienParenteRepository.existsByLibelle(name);
    }
}
