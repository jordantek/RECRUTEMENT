package com.tpc.tpcgestpaie.localapp.service.administration;

import com.tpc.tpcgestpaie.localapp.model.TypeAbsence;
import com.tpc.tpcgestpaie.localapp.repository.administration.TypeAbsenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TypeAbsenceService {

    private final TypeAbsenceRepository typeAbsenceRepository;

    public TypeAbsenceService(TypeAbsenceRepository typeAbsenceRepository) {
        this.typeAbsenceRepository = typeAbsenceRepository;
    }

    public List<TypeAbsence> findAll() {
        return typeAbsenceRepository.findAll();
    }

    public Optional<TypeAbsence> findById(Long id) {
        return typeAbsenceRepository.findById(id);
    }

    public TypeAbsence save(TypeAbsence typeAbsence) {
        return typeAbsenceRepository.save(typeAbsence);
    }

    public void deleteById(Long id) {
        typeAbsenceRepository.deleteById(id);
    }

    public Optional<TypeAbsence> findByLibelle(String libelle) {
        return typeAbsenceRepository.findByLibelle(libelle.trim());
    }

    public boolean existsByLibelle(String libelle) {
        return typeAbsenceRepository.existsByLibelle(libelle.trim());
    }
}
