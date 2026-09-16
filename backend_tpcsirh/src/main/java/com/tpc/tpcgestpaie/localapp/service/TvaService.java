package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.model.Tva;
import com.tpc.tpcgestpaie.localapp.repository.TvaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TvaService {

    private final TvaRepository tvaRepository;

    public TvaService(TvaRepository tvaRepository) {
        this.tvaRepository = tvaRepository;
    }

    public List<Tva> getAllTauxTva() {
        return tvaRepository.findAll();
    }
}
