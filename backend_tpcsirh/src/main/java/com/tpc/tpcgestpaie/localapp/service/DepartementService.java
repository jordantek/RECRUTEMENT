package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.model.Departement;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.DepartementRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartementService {

    private final DepartementRepository departementRepository;

    public DepartementService(DepartementRepository departementRepository, CompanyRepository entrepriseRepository) {
        this.departementRepository = departementRepository;

    }

    public List<Departement> findAll() {
        return departementRepository.findAll();
    }

    public boolean existsByIdAndCompanyId(Long departementId, Long companyId) {
        return departementRepository.existsByIdAndCompanyId(departementId, companyId);
    }


    public Optional<Departement> findById(Long id) {
        return departementRepository.findById(id);
    }

    public Departement save(Departement departement) {
        if (departement.getLibelle() != null) {
            departement.setLibelle(departement.getLibelle().toUpperCase());
        }
        return departementRepository.save(departement);
    }

    public void deleteById(Long id) {
        departementRepository.deleteById(id);
    }

    public boolean existsByLibelle(String libelle) {
        return departementRepository.existsByLibelle(libelle);
    }

    public boolean existsById(Long id) {
        return departementRepository.existsById(id);
    }

    public boolean existsByLibelleAndCompanyId(String libelle, long companyId) {
        return departementRepository.existsByLibelleAndCompanyId(libelle, companyId);
    }

    public List<Departement> findByCompanyId(Long companyId) {
        return departementRepository.findByCompanyId(companyId);
    }

    // Récupérer un département par ID
    public Optional<Departement> getDepartementById(Long id) {
        return departementRepository.findById(id);
    }

}
