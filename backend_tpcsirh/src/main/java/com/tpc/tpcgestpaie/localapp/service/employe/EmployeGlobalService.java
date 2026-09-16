package com.tpc.tpcgestpaie.localapp.service.employe;


import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeGlobalDTO;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.EnfantEmploye;
import com.tpc.tpcgestpaie.localapp.model.PersonneAPrevenir;
import com.tpc.tpcgestpaie.localapp.service.EmployeService;
import com.tpc.tpcgestpaie.localapp.service.EnfantEmployeService;
import com.tpc.tpcgestpaie.localapp.service.PersonneAPrevenirService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeGlobalService {

    private final EmployeService employeService;
    private final EnfantEmployeService enfantService;
    private final PersonneAPrevenirService personneService;

    public EmployeGlobalService(EmployeService employeService,
                                EnfantEmployeService enfantService,
                                PersonneAPrevenirService personneService) {
        this.employeService = employeService;
        this.enfantService = enfantService;
        this.personneService = personneService;
    }

    @Transactional
    public EmployeGlobalDTO save(EmployeGlobalDTO dto) {
        Employe savedEmploye = employeService.save(dto.getEmploye());

        // Lier et enregistrer les enfants
        List<EnfantEmploye> enfants = dto.getEnfants().stream().map(enfant -> {
            return enfantService.create(enfant, savedEmploye.getId());
        }).collect(Collectors.toList());

        // Lier et enregistrer les personnes à prévenir
        List<PersonneAPrevenir> personnes = dto.getPersonnesAPrevenir().stream().map(p -> {
            p.setEmploye(savedEmploye);
            return personneService.create(p);
        }).collect(Collectors.toList());

        EmployeGlobalDTO result = new EmployeGlobalDTO();
        result.setEmploye(savedEmploye);
        result.setEnfants(enfants);
        result.setPersonnesAPrevenir(personnes);

        return result;
    }

    public EmployeGlobalDTO getById(Long id) {
        Employe employe = employeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));

      //  List<EnfantEmploye> enfants = enfantService.findByEmployeId(id);
        /*List<PersonneAPrevenir> personnes = personneService.getAll().stream()
                .filter(p -> p.getEmploye().getId().equals(id) && p.getDeletedAt() == null)
                .toList();
*/
        EmployeGlobalDTO dto = new EmployeGlobalDTO();
        dto.setEmploye(employe);
       // dto.setEnfants(enfants);
        //dto.setPersonnesAPrevenir(personnes);

        return dto;
    }
}