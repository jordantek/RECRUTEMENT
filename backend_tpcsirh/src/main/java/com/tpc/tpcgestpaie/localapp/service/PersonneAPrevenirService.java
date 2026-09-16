package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.PersonneAPrevenirDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.PersonneAPrevenirRequestDTO;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.LienParente;
import com.tpc.tpcgestpaie.localapp.model.PersonneAPrevenir;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.PersonneAPrevenirRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PersonneAPrevenirService {

    @Autowired
    private PersonneAPrevenirRepository repository;

    public List<PersonneAPrevenir> getAll() {
        return repository.findAll();
    }

    public Optional<PersonneAPrevenir> getById(Long id) {
        return repository.findById(id);
    }

    public PersonneAPrevenir create(PersonneAPrevenir personne) {
        return repository.save(personne);
    }

    public PersonneAPrevenir update(Long id, PersonneAPrevenir newPersonne) {
        return repository.findById(id).map(personne -> {
            personne.setNomPrenom(newPersonne.getNomPrenom());
            personne.setTelephone(newPersonne.getTelephone());
            personne.setAdresse(newPersonne.getAdresse());
            personne.setEmail(newPersonne.getEmail());
            personne.setLienParente(newPersonne.getLienParente());
            personne.setUpdatedBy(newPersonne.getUpdatedBy());
            personne.setUpdatedAt(java.time.LocalDateTime.now());
            return repository.save(personne);
        }).orElse(null);
    }

    public boolean delete(Long id) {
        return repository.findById(id).map(personne -> {
            personne.setDeletedAt(java.time.LocalDateTime.now());
            repository.save(personne);
            return true;
        }).orElse(false);
    }

    public void softDelete(Long id) {
        Optional<PersonneAPrevenir> optional = repository.findById(id);
        if (optional.isPresent()) {
            PersonneAPrevenir personne = optional.get();
            personne.setDeletedAt(java.time.LocalDateTime.now());
            repository.save(personne);
        } else {
            throw new RuntimeException("Personne à prévenir non trouvée");
        }
    }

    public PersonneAPrevenirDTO toDto(PersonneAPrevenir p) {
        PersonneAPrevenirDTO dto = new PersonneAPrevenirDTO();
        dto.setId(p.getId());
        dto.setNomPrenom(p.getNomPrenom());
        dto.setTelephone(p.getTelephone());
        dto.setAdresse(p.getAdresse());
        dto.setEmail(p.getEmail());
        dto.setLienParenteLibelle(p.getLienParente() != null ? p.getLienParente().getLibelle() : null);
        return dto;
    }


    public List<PersonneAPrevenirDTO> findByEmployeId(Long employeId) {
        return repository.findByEmployeIdAndDeletedAtIsNull(employeId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public PersonneAPrevenir mapToPersonne(
            PersonneAPrevenirRequestDTO dto,
            Employe employe,
            User user,
            LienParente lienParente
    ) {
        PersonneAPrevenir personne = new PersonneAPrevenir();

        personne.setNomPrenom(dto.nomPrenom());
        personne.setTelephone(dto.telephone());
        personne.setLienParente(lienParente);

        personne.setEmploye(employe);
        personne.setCreatedAt(LocalDateTime.now());
        personne.setCreatedBy(user);

        return personne;
    }

}
