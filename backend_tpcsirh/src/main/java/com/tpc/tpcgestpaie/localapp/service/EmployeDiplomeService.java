package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDiplomeDTO;
import com.tpc.tpcgestpaie.localapp.model.EmployeDiplome;
import com.tpc.tpcgestpaie.localapp.repository.EmployeDiplomeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.DiplomeRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeDiplomeService {

    @Autowired
    private EmployeDiplomeRepository employeDiplomeRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private DiplomeRepository diplomeRepository;

    @Autowired
    private UserRepository userRepository;

    public List<EmployeDiplomeDTO> getAll() {
        return employeDiplomeRepository.findAll().stream()
                .filter(ed -> ed.getDeleted_at() == null)
                .map(EmployeDiplomeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<EmployeDiplomeDTO> getById(Long id) {
        return employeDiplomeRepository.findById(id)
                .filter(ed -> ed.getDeleted_at() == null)
                .map(EmployeDiplomeDTO::fromEntity);
    }

    public EmployeDiplomeDTO save(EmployeDiplomeDTO dto) {
        EmployeDiplome entity;

        if (dto.getId() != null) {
            entity = employeDiplomeRepository.findById(dto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("EmployeDiplome not found"));
        } else {
            entity = new EmployeDiplome();

            if (dto.getAddedById() != null) {
                entity.setAdded_by(userRepository.getReferenceById(dto.getAddedById()));
            } else {
                throw new IllegalArgumentException("addedById est requis pour la création");
            }
        }

        if (dto.getEmployeId() != null) {
            entity.setEmploye(employeRepository.getReferenceById(dto.getEmployeId()));
        } else {
            throw new IllegalArgumentException("employeId est requis");
        }

        if (dto.getDiplomeId() != null) {
            entity.setDiplome(diplomeRepository.getReferenceById(dto.getDiplomeId()));
        } else {
            throw new IllegalArgumentException("diplomeId est requis");
        }

        entity.setAnnee_obtention(dto.getAnneeObtention());
        entity.setDenomination(dto.getDenomination());

        if (dto.getUpdatedById() != null) {
            entity.setUpdated_by(userRepository.getReferenceById(dto.getUpdatedById()));
        }

        EmployeDiplome saved = employeDiplomeRepository.save(entity);
        return EmployeDiplomeDTO.fromEntity(saved);
    }

    public void delete(Long id) {
        EmployeDiplome entity = employeDiplomeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("EmployeDiplome not found"));

        entity.setDeleted_at(LocalDateTime.now());
        employeDiplomeRepository.save(entity);
    }

    public boolean exists(Long id) {
        return employeDiplomeRepository.existsById(id);
    }

    public List<EmployeDiplomeDTO> getAllByEmploye(Long employeId) {
        return employeDiplomeRepository.findByEmployeId(employeId)
                .stream()
                .map(d -> new EmployeDiplomeDTO().fromEntity(d))
                .collect(Collectors.toList());
    }

}
