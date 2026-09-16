package com.tpc.tpcgestpaie.localapp.service.paie;

import com.tpc.tpcgestpaie.localapp.dto.paie.AvanceDTO;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.administration.AvanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AvanceService {

    @Autowired
    private AvanceRepository avanceRepository;

    public List<AvanceDTO> getAll() {
        return avanceRepository.findAll()
                .stream()
                .map(AvanceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<AvanceDTO> getById(Long id) {
        return avanceRepository.findById(id)
                .map(AvanceDTO::fromEntity);
    }

    public AvanceDTO save(AvanceDTO dto,
                          ContratEmploye contratEmploye,
                          Employe employe,
                          Company company,
                          User addedBy) {
        Avance entity = dto.toEntity(contratEmploye, employe, company, addedBy);
        Avance saved = avanceRepository.save(entity);
        return AvanceDTO.fromEntity(saved);
    }


    public List<AvanceDTO> findByCompanyId(Long companyId) {
        return avanceRepository.findAll()
                .stream()
                .filter(a -> a.getCompany() != null && a.getCompany().getId().equals(companyId))
                .map(AvanceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        avanceRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        return avanceRepository.existsById(id);
    }


    public List<AvanceDTO> findByMoisDemarrageBetween(YearMonth start, YearMonth end) {
        return avanceRepository.findAll()
                .stream()
                .filter(a -> a.getMoisDemarrage() != null &&
                        !a.getMoisDemarrage().isBefore(start) &&
                        !a.getMoisDemarrage().isAfter(end))
                .map(AvanceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AvanceDTO> findAvancesByMoisFinBetween(YearMonth start, YearMonth end) {
        return avanceRepository.findByMoisFinBetween(start, end)
                .stream()
                .map(AvanceDTO::fromEntity)
                .collect(Collectors.toList());
    }
    public List<AvanceDTO> findByCompanyIdAndMois(Long companyId,YearMonth mois){
        return avanceRepository.findByCompanyIdAndMoisContaining(companyId,mois)
                .stream()
                .map(AvanceDTO::fromEntity)
                .collect(Collectors.toList());
    }
    public List<AvanceDTO> findAvancesByCompanyAndMoisFinBetween(Long companyId, YearMonth start, YearMonth end) {
        return avanceRepository.findByCompanyAndMoisFinBetween(companyId, start, end)
                .stream()
                .map(AvanceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AvanceDTO> findByCompanyAndMoisDemarrageBetween(Long companyId, YearMonth start, YearMonth end) {
        return avanceRepository.findByCompanyAndMoisDemarrageBetween(companyId, start, end)
                .stream()
                .map(AvanceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AvanceDTO> findByEmployeId(Long employeId) {
        return avanceRepository.findByEmployeId(employeId)
                .stream()
                .map(AvanceDTO::fromEntity)
                .collect(Collectors.toList());
    }

}
