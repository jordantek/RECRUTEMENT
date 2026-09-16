package com.tpc.tpcgestpaie.localapp.service.paie;

import com.tpc.tpcgestpaie.localapp.model.Institution;
import com.tpc.tpcgestpaie.localapp.repository.paie.InstitutionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    public InstitutionService(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    public List<Institution> findAll() {
        return institutionRepository.findAll();
    }

    public Optional<Institution> findById(Long id) {
        return institutionRepository.findById(id);
    }

    public Institution save(Institution institution) {
        return institutionRepository.save(institution);
    }

    public void deleteById(Long id) {
        institutionRepository.deleteById(id);
    }

    public Optional<Institution> findByName(String name) {
        return institutionRepository.findByName(name.trim());
    }

    public boolean existsByName(String name) {
        return institutionRepository.existsByName(name.trim());
    }
}
