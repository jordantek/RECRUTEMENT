package com.tpc.tpcgestpaie.localapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpc.tpcgestpaie.localapp.dto.accessoire.AllocationCongeDetailDTO;
import com.tpc.tpcgestpaie.localapp.dto.accessoire.ResultatCongeDTO;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.IndemniteCongeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Indique que cette classe est un service Spring (logique métier)
public class IndemniteCongeService {


    private final ContratEmployeRepository contratEmployeRepository;
    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final IndemniteCongeRepository indemniteCongeRepository;

    public IndemniteCongeService(ContratEmployeRepository contratEmployeRepository, CompanyRepository companyRepository, UserService userService, IndemniteCongeRepository indemniteCongeRepository) {
        this.contratEmployeRepository = contratEmployeRepository;
        this.companyRepository = companyRepository;
        this.userService = userService;
        this.indemniteCongeRepository = indemniteCongeRepository;
    }

    @Transactional
    public IndemniteConge enregistrerIndemniteConge(ResultatCongeDTO resultat, Long idContratEmploye, Long companyId, String moisCalculSalaire) throws Exception {
        ContratEmploye contrat = contratEmployeRepository.findById(idContratEmploye)
                .orElseThrow(() -> new EntityNotFoundException("Contrat employé introuvable"));

        Employe employe = contrat.getEmploye();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise introuvable"));

        YearMonth mois = YearMonth.parse(resultat.getMoisCalculSalaire());

        // Vérification doublon
        Optional<IndemniteConge> existingIndemnite = indemniteCongeRepository.findByEmployeIdAndMoisCalculSalaire(employe.getId(), mois);
        if (existingIndemnite.isPresent()) {
            throw new IllegalStateException("Une indemnité existe déjà pour cet employé et ce mois.");
        }
        IndemniteConge indemniteConge = new IndemniteConge();
        indemniteConge.setContratEmploye(contrat);
        indemniteConge.setEmploye(employe);
        indemniteConge.setCompany(company);
        indemniteConge.setMoisCalculSalaire(mois);
        indemniteConge.setTotalTemps(resultat.getTotalTemps());
        indemniteConge.setTotalSalaire(resultat.getTotalSalaire());
        indemniteConge.setSalaireJournalierNormal(resultat.getSalaireJournalierNormal());
        indemniteConge.setNbJours(resultat.getNbJours());
        indemniteConge.setMontantTotal(resultat.getMontantTotal());

        ObjectMapper mapper = new ObjectMapper();
        String bulletinsJson = mapper.writeValueAsString(resultat.getBulletinsUtilises());
        indemniteConge.setBulletinsUtilises(bulletinsJson);

        User currentUser = userService.getCurrentUser();
        indemniteConge.setAdded_by(currentUser);

        return indemniteCongeRepository.save(indemniteConge);
    }

//    @Transactional
//    public List<AllocationCongeDTO> getAllocationsByCompanyAndContrat(Long companyId, Long contratEmployeId) {
//        List<AllocationConge> allocations = allocationCongeRepository.findAllocationsWithEmploye(companyId, contratEmployeId);
//
//        return allocations.stream().map(a -> new AllocationCongeDTO(
//                a.getId(),
//                a.getEmploye().getPrenom() + " " + a.getEmploye().getNom(),
//                a.getMoisCalculSalaire().toString(),
//                a.getMontantTotal(),
//                a.getSalaireJournalierNormal(),
//                a.getNbJours()
//        )).collect(Collectors.toList());
//    }

    @Transactional
    public List<AllocationCongeDetailDTO> getIndemniteDetailsByCompany(Long companyId) {
        List<IndemniteConge> indemniteConges = indemniteCongeRepository.findByCompanyId(companyId);

        return indemniteConges.stream().map(a -> new AllocationCongeDetailDTO(
                a.getId(),
                a.getEmploye() != null ? a.getEmploye().getId() : null,
                a.getEmploye() != null ? a.getEmploye().getNom() : null,
                a.getEmploye() != null ? a.getEmploye().getPrenom() : null,
                a.getCompany() != null ? a.getCompany().getId() : null,
                a.getCompany() != null ? a.getCompany().getName() : null,
                a.getMoisCalculSalaire().toString(),
                a.getTotalTemps(),
                a.getTotalSalaire(),
                a.getSalaireJournalierNormal(),
                a.getNbJours(),
                a.getMontantTotal(),
                a.getBulletinsUtilises(),
                a.getAdded_by() != null ? a.getAdded_by().getId() : null,
                a.getAdded_by() != null ? a.getAdded_by().getUsername() : null,
                a.getCreated_at(),
                a.getUpdated_at(),
                a.getDeleted_at()
        )).collect(Collectors.toList());
    }

}
