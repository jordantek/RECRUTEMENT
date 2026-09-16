package com.tpc.tpcgestpaie.localapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpc.tpcgestpaie.localapp.dto.accessoire.AllocationCongeDTO;
import com.tpc.tpcgestpaie.localapp.dto.accessoire.AllocationCongeDetailDTO;
import com.tpc.tpcgestpaie.localapp.dto.accessoire.ResultatCongeDTO;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.AllocationCongeRepository;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Indique que cette classe est un service Spring (logique métier)
public class AllocationCongeService {


    private final ContratEmployeRepository contratEmployeRepository;
    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final AllocationCongeRepository allocationCongeRepository;

    public AllocationCongeService(ContratEmployeRepository contratEmployeRepository, CompanyRepository companyRepository, UserService userService, AllocationCongeRepository allocationCongeRepository) {
        this.contratEmployeRepository = contratEmployeRepository;
        this.companyRepository = companyRepository;
        this.userService = userService;
        this.allocationCongeRepository = allocationCongeRepository;
    }

    @Transactional
    public AllocationConge enregistrerAllocationConge(ResultatCongeDTO resultat, Long idContratEmploye, Long companyId, String moisCalculSalaire) throws Exception {
        ContratEmploye contrat = contratEmployeRepository.findById(idContratEmploye)
                .orElseThrow(() -> new EntityNotFoundException("Contrat employé introuvable"));

        Employe employe = contrat.getEmploye();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise introuvable"));

       // YearMonth mois = YearMonth.parse(moisCalculSalaire);

        // Vérification doublon
        Optional<AllocationConge> existingAllocation = allocationCongeRepository.findByEmployeIdAndMoisCalculSalaire(employe.getId(), moisCalculSalaire);
        if (existingAllocation.isPresent()) {
            throw new IllegalStateException("Une allocation existe déjà pour cet employé et ce mois.");
        }

        AllocationConge allocation = new AllocationConge();
        allocation.setContratEmploye(contrat);
        allocation.setEmploye(employe);
        allocation.setCompany(company);
        allocation.setMoisCalculSalaire(moisCalculSalaire);
        allocation.setTotalTemps(resultat.getTotalTemps());
        allocation.setTotalSalaire(resultat.getTotalSalaire());
        allocation.setSalaireJournalierNormal(resultat.getSalaireJournalierNormal());
        allocation.setNbJours(resultat.getNbJours());
        allocation.setMontantTotal(resultat.getMontantTotal());

        ObjectMapper mapper = new ObjectMapper();
        String bulletinsJson = mapper.writeValueAsString(resultat.getBulletinsUtilises());
        allocation.setBulletinsUtilisesJson(bulletinsJson);

        User currentUser = userService.getCurrentUser();
        allocation.setAdded_by(currentUser);

        return allocationCongeRepository.save(allocation);
    }

    @Transactional
    public List<AllocationCongeDTO> getAllocationsByCompanyAndContrat(Long companyId, Long contratEmployeId) {
        List<AllocationConge> allocations = allocationCongeRepository.findAllocationsWithEmploye(companyId, contratEmployeId);

        return allocations.stream().map(a -> new AllocationCongeDTO(
                a.getId(),
                a.getEmploye().getPrenom() + " " + a.getEmploye().getNom(),
                a.getMoisCalculSalaire().toString(),
                a.getMontantTotal(),
                a.getSalaireJournalierNormal(),
                a.getNbJours()
        )).collect(Collectors.toList());
    }

    @Transactional
    public AllocationCongeDetailDTO getAllocationDetailById(Long allocationId) {
        AllocationConge allocation = allocationCongeRepository.findById(allocationId)
                .orElseThrow(() -> new RuntimeException("Allocation non trouvée"));

        // Ici on suppose que tous les objets liés sont chargés ou fetchés (sinon tu peux forcer avec fetch join dans query custom)

        return new AllocationCongeDetailDTO(
                allocation.getId(),
                allocation.getEmploye() != null ? allocation.getEmploye().getId() : null,
                allocation.getEmploye() != null ? allocation.getEmploye().getNom() : null,
                allocation.getEmploye() != null ? allocation.getEmploye().getPrenom() : null, // ou autre champ utile
                allocation.getCompany() != null ? allocation.getCompany().getId() : null,
                allocation.getCompany() != null ? allocation.getCompany().getName() : null,
                allocation.getMoisCalculSalaire(),
                allocation.getTotalTemps(),
                allocation.getTotalSalaire(),
                allocation.getSalaireJournalierNormal(),
                allocation.getNbJours(),
                allocation.getMontantTotal(),
                allocation.getBulletinsUtilisesJson(),
                allocation.getAdded_by() != null ? allocation.getAdded_by().getId() : null,
                allocation.getAdded_by() != null ? allocation.getAdded_by().getUsername() : null,
                allocation.getCreated_at(),
                allocation.getUpdated_at(),
                allocation.getDeleted_at()
        );
    }

    @Transactional
    public List<AllocationCongeDetailDTO> getAllocationsDetailsByCompanyAndContrat(Long companyId, Long contratEmployeId) {
        List<AllocationConge> allocations;

        if (contratEmployeId != null) {
            allocations = allocationCongeRepository.findAllocationsWithEmployeAndContrat(companyId, contratEmployeId);
        } else {
            allocations = allocationCongeRepository.findAllocationsWithEmployeAndContrat(companyId, null);
        }
        return allocations.stream().map(a -> new AllocationCongeDetailDTO(
                a.getId(),
                a.getEmploye() != null ? a.getEmploye().getId() : null,
                a.getEmploye() != null ? a.getEmploye().getNom() : null,
                a.getEmploye() != null ? a.getEmploye().getPrenom() : null,
                a.getCompany() != null ? a.getCompany().getId() : null,
                a.getCompany() != null ? a.getCompany().getName() : null,
                a.getMoisCalculSalaire(),
                a.getTotalTemps(),
                a.getTotalSalaire(),
                a.getSalaireJournalierNormal(),
                a.getNbJours(),
                a.getMontantTotal(),
                a.getBulletinsUtilisesJson(),
                a.getAdded_by() != null ? a.getAdded_by().getId() : null,
                a.getAdded_by() != null ? a.getAdded_by().getUsername() : null,
                a.getCreated_at(),
                a.getUpdated_at(),
                a.getDeleted_at()
        )).collect(Collectors.toList());
    }


    @Transactional
    public List<AllocationCongeDetailDTO> getByCompanyAndMoisCalculeSalaire(Long companyId, String mois) {
        List<AllocationConge> allocations = allocationCongeRepository
                .findByCompanyIdAndMoisCalculSalaire(companyId, mois);

        return allocations.stream().map(a -> new AllocationCongeDetailDTO(
                a.getId(),
                a.getEmploye() != null ? a.getEmploye().getId() : null,
                a.getEmploye() != null ? a.getEmploye().getNom() : null,
                a.getEmploye() != null ? a.getEmploye().getPrenom() : null,
                a.getCompany() != null ? a.getCompany().getId() : null,
                a.getCompany() != null ? a.getCompany().getName() : null,
                a.getMoisCalculSalaire(),
                a.getTotalTemps(),
                a.getTotalSalaire(),
                a.getSalaireJournalierNormal(),
                a.getNbJours(),
                a.getMontantTotal(),
                a.getBulletinsUtilisesJson(),
                a.getAdded_by() != null ? a.getAdded_by().getId() : null,
                a.getAdded_by() != null ? a.getAdded_by().getUsername() : null,
                a.getCreated_at(),
                a.getUpdated_at(),
                a.getDeleted_at()
        )).collect(Collectors.toList());
    }

}
