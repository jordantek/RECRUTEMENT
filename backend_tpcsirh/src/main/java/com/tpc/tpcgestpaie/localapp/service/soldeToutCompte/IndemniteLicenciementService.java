package com.tpc.tpcgestpaie.localapp.service.soldeToutCompte;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpc.tpcgestpaie.localapp.dto.accessoire.IndemniteLicenciementRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.accessoire.IndemniteLicenciementResponseDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.IndemniteLicenciement;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.stc.IndemniteLicenciementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndemniteLicenciementService {

    private final IndemniteLicenciementRepository repository;
    private final ContratEmployeRepository contratRepo;
    private final CompanyRepository companyRepo;

    public IndemniteLicenciementService(IndemniteLicenciementRepository repository, ContratEmployeRepository contratRepo, CompanyRepository companyRepo) {
        this.repository = repository;
        this.contratRepo = contratRepo;
        this.companyRepo = companyRepo;    }

    public IndemniteLicenciement saveFromDTO(IndemniteLicenciementRequestDTO dto) throws Exception {
        IndemniteLicenciement entity = new IndemniteLicenciement();

        // Récupérer contrat
        ContratEmploye contrat = contratRepo.findById(dto.getIdContratEmploye())
                .orElseThrow(() -> new Exception("ContratEmploye introuvable"));

        // Récupérer company
        Company company = companyRepo.findById(dto.getIdCompany())
                .orElseThrow(() -> new Exception("Company introuvable"));

        entity.setContratEmploye(contrat);
        entity.setCompany(company);

        Employe employe = contrat.getEmploye();
        entity.setEmploye(employe);

        // Conversion String "2025-08" en YearMonth
        YearMonth ym = YearMonth.parse(dto.getData().getMoisCalculSalaire());
        entity.setMoisCalculSalaire(ym);

        entity.setTypeLicencement(dto.getData().getTypeLicencement());
        entity.setIndemniteSelonAnciennete(dto.getData().getIndemniteSelonAnciennete());
        entity.setAnciennete(dto.getData().getAnciennete());
        entity.setMontantMoyen(dto.getData().getMontantMoyen());
        entity.setMontantTotal(dto.getData().getMontantTotal());

        // Convertir la liste montantsMensuels en JSON string (Jackson)
        ObjectMapper mapper = new ObjectMapper();
        String montantsMensuelsJson = mapper.writeValueAsString(dto.getData().getMontantsMensuels());
        entity.setMontantsMensuels(montantsMensuelsJson);
        return repository.save(entity);
    }


    public List<IndemniteLicenciementResponseDTO> listByEmployeAndCompany(Long employeId, Long companyId) {
        List<IndemniteLicenciement> list = repository.findByEmployeIdAndCompanyId(employeId, companyId);
        return list.stream()
                .map(IndemniteLicenciementResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<IndemniteLicenciementResponseDTO> listByEmploye(Long employeId) {
        List<IndemniteLicenciement> list = repository.findByEmployeId(employeId);
        return list.stream()
                .map(IndemniteLicenciementResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<IndemniteLicenciementResponseDTO> listByCompany(Long companyId) {
        List<IndemniteLicenciement> list = repository.findByCompanyId(companyId);
        return list.stream()
                .map(IndemniteLicenciementResponseDTO::new)
                .collect(Collectors.toList());
    }
}
