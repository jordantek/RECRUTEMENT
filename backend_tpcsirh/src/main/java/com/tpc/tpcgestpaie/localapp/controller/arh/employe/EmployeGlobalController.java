package com.tpc.tpcgestpaie.localapp.controller.arh.employe;


import com.tpc.tpcgestpaie.localapp.dto.employe.CreateEmployeRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeGlobalDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EnfantEmployeRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.PersonneAPrevenirRequestDTO;
import com.tpc.tpcgestpaie.localapp.helper.EmployeHelper;
import com.tpc.tpcgestpaie.localapp.helper.EnfantEmployeHelper;
import com.tpc.tpcgestpaie.localapp.helper.PersonneAPrevenirHelper;
import com.tpc.tpcgestpaie.localapp.helper.UserHelper;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.RoleRepository;
import com.tpc.tpcgestpaie.localapp.repository.StatusRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import com.tpc.tpcgestpaie.localapp.service.*;
import com.tpc.tpcgestpaie.localapp.service.employe.EmployeGlobalService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/employes-global")
public class EmployeGlobalController {

    private final EmployeService employeService;
    private final EnfantEmployeService enfantService;
    private final PersonneAPrevenirService personneAPrevenirService;
    private final UserService userService;
    private  final EnfantEmployeHelper enfantEmployeHelper;
    private final PersonneAPrevenirHelper personneAPrevenirHelper;
    private final EmployeGlobalService service;
    private final LienParenteService lienParenteService;
    private final CompanyService companyService;

    private final EmployeHelper employeHelper;
    private final UserHelper userHelper;
    private final StatusRepository statusRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    public EmployeGlobalController(
            EmployeService employeService,CompanyService companyService,
            EnfantEmployeService enfantService,
            PersonneAPrevenirService personneAPrevenirService,
            UserService userService, EnfantEmployeHelper enfantEmployeHelper, PersonneAPrevenirHelper personneAPrevenirHelper, EmployeGlobalService service,
            EmployeHelper employeHelper,
            LienParenteService lienParenteService,
            UserHelper userHelper, StatusRepository statusRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.employeService = employeService;
        this.enfantService = enfantService;
        this.personneAPrevenirService = personneAPrevenirService;
        this.userService = userService;
        this.enfantEmployeHelper = enfantEmployeHelper;
        this.personneAPrevenirHelper = personneAPrevenirHelper;
        this.service = service;
        this.employeHelper = employeHelper;
        this.userHelper = userHelper;
        this.statusRepository = statusRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.lienParenteService = lienParenteService;
        this.companyService = companyService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createEmployeGlobal(
            @Valid @RequestBody CreateEmployeRequestDTO dto
    ) {
        try {
            // 🔹 Récupérer l'utilisateur courant
            User currentUser = userService.getCurrentUser();

            Company company =  companyService.getById( Long.valueOf(dto.companyId()));
            // 🔹 Mapping DTO → Entity pour l'employé
            Employe employe = employeService.mapToEmploye(dto, currentUser,company);

            // 🔹 Sauvegarde de l'employé
            Employe savedEmploye = employeService.save(employe);

            // 🔹 Sauvegarde des enfants
            if (dto.enfants() != null && !dto.enfants().isEmpty()) {
                for (EnfantEmployeRequestDTO enfantDTO : dto.enfants()) {
                    EnfantEmploye enfant = enfantService.mapToEnfant(enfantDTO, savedEmploye, currentUser);
                    enfantService.create(enfant, savedEmploye.getId());
                }
            }

            // 🔹 Sauvegarde des personnes à prévenir
            if (dto.personnesAPrevenir() != null && !dto.personnesAPrevenir().isEmpty()) {
                for (PersonneAPrevenirRequestDTO personneDTO : dto.personnesAPrevenir()) {
                    String lienParenteIdString = personneDTO.lienParenteId();

                    Long lienParenteId = null;
                    if (lienParenteIdString != null && !lienParenteIdString.isBlank()) {
                        lienParenteId = Long.valueOf(lienParenteIdString);
                    }

                   LienParente lienParente = lienParenteService.findById2(lienParenteId);

                    PersonneAPrevenir personne = personneAPrevenirService.mapToPersonne(personneDTO, savedEmploye, currentUser,lienParente);
                    personneAPrevenirService.create(personne);
                }
            }

            // 🔹 Préparer la réponse
            Map<String, Object> responseData = Map.of(
                    "id", savedEmploye.getId(),
                    "nom", savedEmploye.getNom(),
                    "prenom", savedEmploye.getPrenom()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Employé enregistré avec succès", responseData));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur interne lors de l'enregistrement", null));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        try {
            EmployeGlobalDTO employe = service.getById(id);
            if (employe == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(
                                "Employé introuvable",
                                "NOT_FOUND",
                                List.of("Aucun employé trouvé avec l’ID " + id)
                        ));
            }
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Employé récupéré avec succès",
                            employe
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(
                            "Erreur lors de la récupération de l’employé",
                            "INTERNAL_ERROR",
                            List.of(e.getMessage())
                    ));
        }
    }

}