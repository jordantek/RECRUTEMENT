package com.tpc.tpcgestpaie.localapp.service.employe;


import com.tpc.tpcgestpaie.localapp.dto.EmployeFamilleDTO;
import com.tpc.tpcgestpaie.localapp.dto.EnfantEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.PersonneAPrevenirDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EnfantEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.PersonneAPrevenirRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeFamilleService {


    private final EnfantEmployeRepository enfantRepo;

    private final PersonneAPrevenirRepository personneRepo;
    private final EmployeRepository employeRepository;
    private final EnfantEmployeRepository enfantEmployeRepository;
    private final PersonneAPrevenirRepository personneAPrevenirRepository;

    public EmployeFamilleService(EnfantEmployeRepository enfantRepo, PersonneAPrevenirRepository personneRepo, EmployeRepository employeRepository, EnfantEmployeRepository enfantEmployeRepository, PersonneAPrevenirRepository personneAPrevenirRepository) {
        this.enfantRepo = enfantRepo;
        this.personneRepo = personneRepo;
        this.employeRepository = employeRepository;
        this.enfantEmployeRepository = enfantEmployeRepository;
        this.personneAPrevenirRepository = personneAPrevenirRepository;
    }


    public EmployeFamilleDTO getById(Long id) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));

        List<EnfantEmployeDTO> enfants = enfantEmployeRepository.findByEmployeIdAndDeletedAtIsNull(id)
                .stream()
                .map(e -> {
                    EnfantEmployeDTO dto = new EnfantEmployeDTO();
                    dto.setId(e.getId());
                    dto.setNom(e.getNom());
                    dto.setPrenom(e.getPrenom());
                    dto.setDateNaissance(e.getDateNaissance());
                    return dto;
                }).toList();

        List<PersonneAPrevenirDTO> personnes = personneAPrevenirRepository.findByEmployeIdAndDeletedAtIsNull(id)
                .stream()
                .map(p -> {
                    PersonneAPrevenirDTO dto = new PersonneAPrevenirDTO();
                    dto.setId(p.getId());
                    dto.setNomPrenom(p.getNomPrenom());
                    dto.setTelephone(p.getTelephone());
                    dto.setLienParenteLibelle(p.getLienParente().getLibelle());
                    return dto;
                }).toList();

        EmployeDTO employeDTO = new EmployeDTO();
        employeDTO.setId(employe.getId());
        employeDTO.setNom(employe.getNom());
        employeDTO.setPrenom(employe.getPrenom());
        employeDTO.setEmail(employe.getEmail());

        EmployeFamilleDTO dto = new EmployeFamilleDTO();
        dto.setEmploye(employeDTO);
        dto.setEnfants(enfants);
        dto.setPersonnesAPrevenir(personnes);

        return dto;
    }

}
