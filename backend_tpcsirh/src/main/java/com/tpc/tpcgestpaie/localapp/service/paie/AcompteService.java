package com.tpc.tpcgestpaie.localapp.service.paie;

import com.tpc.tpcgestpaie.localapp.dto.paie.AcompteDTO;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.administration.AcompteRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AcompteService {

    @Autowired
    private AcompteRepository acompteRepository;

    public List<AcompteDTO> getAll() {
        return acompteRepository.findAll()
                .stream()
                .map(AcompteDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<AcompteDTO> getById(Long id) {
        return acompteRepository.findById(id)
                .map(AcompteDTO::fromEntity);
    }

    public AcompteDTO save(AcompteDTO dto,
                          ContratEmploye contratEmploye,
                          Employe employe,
                          Company company,
                          User addedBy) {
        Acompte entity = dto.toEntity(contratEmploye, employe, company,addedBy);
        Acompte saved = acompteRepository.save(entity);
        return AcompteDTO.fromEntity(saved);
    }

    @Transactional
    public Acompte update(Acompte existing, AcompteDTO dto) {
        existing.setMontant(dto.getMontant());
        existing.setMoisFromYearMonth(dto.getMois());
        existing.setId(existing.getId());
        // ... autres champs
        return acompteRepository.save(existing);
    }

    public List<AcompteDTO> findByCompanyId(Long companyId) {
        return acompteRepository.findAll()
                .stream()
                .filter(a -> a.getCompany() != null && a.getCompany().getId().equals(companyId))
                .map(AcompteDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        acompteRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        return acompteRepository.existsById(id);
    }

    public List<AcompteDTO> findByEmployeId(Long employeId) {
        return acompteRepository.findByEmployeId(employeId)
                .stream()
                .map(AcompteDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AcompteDTO> findByCompanyAndMois(Long companyId, String mois) {
        return acompteRepository.findByCompanyAndMois(companyId, mois)
                .stream()
                .map(AcompteDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<AcompteDTO> findByEmployeAndMois(Long employeId, String mois) {
        return acompteRepository.findByEmployeAndMois(employeId, YearMonth.parse(mois)).stream()
                .findFirst()
                .map(AcompteDTO::fromEntity);
    }


}
