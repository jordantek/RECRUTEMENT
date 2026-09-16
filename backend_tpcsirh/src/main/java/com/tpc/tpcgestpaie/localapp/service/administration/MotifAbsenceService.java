package com.tpc.tpcgestpaie.localapp.service.administration;


import com.tpc.tpcgestpaie.localapp.model.MotifAbsence;
import com.tpc.tpcgestpaie.localapp.repository.administration.MotifAbsenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MotifAbsenceService {

    private final MotifAbsenceRepository motifAbsenceRepository;

    public MotifAbsenceService(MotifAbsenceRepository motifAbsenceRepository) {
        this.motifAbsenceRepository = motifAbsenceRepository;
    }

    public List<MotifAbsence> findAll() {
        return motifAbsenceRepository.findAll();
    }

    public Optional<MotifAbsence> findById(Long id) {
        return motifAbsenceRepository.findById(id);
    }

    public MotifAbsence save(MotifAbsence motifAbsence) {
        return motifAbsenceRepository.save(motifAbsence);
    }

    public void deleteById(Long id) {
        motifAbsenceRepository.deleteById(id);
    }

    public Optional<MotifAbsence> findByLibelle(String libelle) {
        return motifAbsenceRepository.findByLibelle(libelle.trim());
    }

    public boolean existsByLibelle(String libelle) {
        return motifAbsenceRepository.existsByLibelle(libelle.trim());
    }
}
