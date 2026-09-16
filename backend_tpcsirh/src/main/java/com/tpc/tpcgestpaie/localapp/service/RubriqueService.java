package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.accessoire.RubriqueResponseDTO;
import com.tpc.tpcgestpaie.localapp.model.Rubrique;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRubriqueRepository;
import com.tpc.tpcgestpaie.localapp.repository.RubriqueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RubriqueService {

    private final RubriqueRepository rubriqueRepository;
    private final ContratEmployeRubriqueRepository contratEmployeRubriqueRepository;

    public RubriqueService(RubriqueRepository rubriqueRepository, ContratEmployeRubriqueRepository contratEmployeRubriqueRepository) {
        this.rubriqueRepository = rubriqueRepository;
        this.contratEmployeRubriqueRepository = contratEmployeRubriqueRepository;
    }


    // AJOUT: Pagination pour toutes les rubriques
    public Page<Rubrique> findAllPaginated(Pageable pageable) {
        return rubriqueRepository.findAll(pageable);
    }

    // AJOUT: Pagination pour les rubriques par entreprise
    public Page<RubriqueResponseDTO> getRubriquesParEntreprisePaginated(Long companyId, Pageable pageable) {
        Page<Rubrique> rubriquesPage = rubriqueRepository.findDistinctRubriquesByCompanyIdPaginated(companyId, pageable);

        return rubriquesPage.map(r -> new RubriqueResponseDTO(r.getId(), r.getLibelle()));
    }

    // AJOUT: Pagination pour les rubriques variables par entreprise
    public Page<RubriqueResponseDTO> getRubriquesVariablesParEntreprisePaginated(Long companyId, Pageable pageable) {
        Page<Rubrique> rubriquesPage = rubriqueRepository.findRubriquesVariablesByCompanyIdPaginated(companyId, pageable);

        return rubriquesPage.map(r -> new RubriqueResponseDTO(r.getId(), r.getLibelle()));
    }

    public List<Rubrique> findAll() {
        return rubriqueRepository.findAllRubriques();
    }

    public List<Rubrique> findAllVariableRubrique() {
        return rubriqueRepository.findAllVariableRubriques();
    }

    public Optional<Rubrique> findById(Long id) {
        return rubriqueRepository.findById(id);
    }

    public Optional<Rubrique> getById(Long id) {
        return rubriqueRepository.findById(id);
    }

    public Rubrique save(Rubrique rubrique) {
        return rubriqueRepository.save(rubrique);
    }

    public void deleteById(Long id) {
        rubriqueRepository.deleteById(id);
    }

    public boolean existsByLibelle(String libelle) {
        return rubriqueRepository.existsByLibelle(libelle.trim());
    }

    public Optional<Rubrique> findByLibelle(String libelle) {
        return rubriqueRepository.findByLibelle(libelle.trim());
    }

    public boolean existsById(Long id) {
        return rubriqueRepository.existsById(id);
    }

//    public List<Rubrique> getRubriquesParEntreprise(Long companyId) {
//        return contratEmployeRubriqueRepository.findDistinctRubriquesByCompanyId(companyId);
//    }

    public List<RubriqueResponseDTO> getRubriquesParEntreprise(Long companyId) {
        List<Rubrique> rubriques = contratEmployeRubriqueRepository.findDistinctRubriquesByCompanyId(companyId);
        return rubriques.stream()
                .map(r -> new RubriqueResponseDTO(r.getId(), r.getLibelle()))
                .collect(Collectors.toList());
    }

    public List<RubriqueResponseDTO> getRubriquesVariablesParEntreprise(Long companyId) {
        List<Rubrique> rubriques = contratEmployeRubriqueRepository.findRubriquesVariablesByCompanyId(companyId);
        return rubriques.stream()
                .map(r -> new RubriqueResponseDTO(r.getId(), r.getLibelle()))
                .collect(Collectors.toList());
    }
    public Long getId13eMois() {
        return rubriqueRepository.findIdByLibelle("SALAIRE 13e MOIS")
                .orElseThrow(() -> new RuntimeException("Rubrique 13e mois introuvable"));
    }

    public Long getId1SalalireMoyen() {
        return rubriqueRepository.findIdByLibelle("SALAIRE MOYEN")
                .orElseThrow(() -> new RuntimeException("Rubrique SALAIRE MOYEN introuvable"));
    }

    public Long getIdPrimesExceptionnelles() {
        return rubriqueRepository.findIdByLibelle("PRIMES EXCEPTIONNELLES")
                .orElseThrow(() -> new RuntimeException("Rubrique primes exceptionnelles introuvable"));
    }




}
