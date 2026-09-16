package com.tpc.tpcgestpaie.localapp.controller.soldeToutCompte;

import com.tpc.tpcgestpaie.localapp.dto.soldeToutCompte.*;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.stc.SoldeToutCompteRepository;
import com.tpc.tpcgestpaie.localapp.service.CompanyService;
import com.tpc.tpcgestpaie.localapp.service.ContratEmployeService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.soldeToutCompte.IndemniteResumeService;
import com.tpc.tpcgestpaie.localapp.service.soldeToutCompte.SoldeToutCompteService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stc")
public class SoldeToutCompteController {

    private final SoldeToutCompteService service;
    private final UserService userService;
    private final ContratEmployeService contratEmployeService;
    private final CompanyService companyService;
    private final SoldeToutCompteRepository soldeToutCompteRepository;
    private final SoldeToutCompteService soldeToutCompteService;
    private final CompanyRepository companyRepository;
    private final IndemniteResumeService indemniteResumeService;

    public SoldeToutCompteController(SoldeToutCompteService service, UserService userService, ContratEmployeService contratEmployeService, CompanyService companyService, SoldeToutCompteRepository soldeToutCompteRepository, SoldeToutCompteService soldeToutCompteService, CompanyRepository companyRepository, IndemniteResumeService indemniteResumeService) {
        this.service = service;
        this.userService = userService;
        this.contratEmployeService = contratEmployeService;
        this.companyService = companyService;
        this.soldeToutCompteRepository = soldeToutCompteRepository;
        this.soldeToutCompteService = soldeToutCompteService;
        this.companyRepository = companyRepository;
        this.indemniteResumeService = indemniteResumeService;
    }

    @PostMapping("/calcul")
    public ResponseEntity<SoldeToutCompteResponseDTO> calculer(@RequestBody SoldeToutCompteRequestDTO request) {
        SoldeToutCompteResponseDTO response = service.calculer(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody SoldeToutCompteSaveRequestDTO dto) {
        try {
            User currentUser = userService.getCurrentUser();
            List<ErrorResponse> errors = new ArrayList<>();

            // 1️⃣ Validations
            if (dto.getContratEmployeId() == null)
                errors.add(new ErrorResponse("contratEmployeId", "Le champ 'contratEmployeId' est requis."));
            if (dto.getCompanyId() == null)
                errors.add(new ErrorResponse("companyId", "Le champ 'companyId' est requis."));
            if (dto.getMoisCalcul() == null)
                errors.add(new ErrorResponse("moisCalcul", "Le champ 'moisCalcul' est requis."));
            if (dto.getSalairePresence() == null)
                errors.add(new ErrorResponse("salairePresence", "Le salaire de présence est requis."));
            if (dto.getSalaireMoyen() == null)
                errors.add(new ErrorResponse("salaireMoyen", "Le salaire moyen est requis."));

            if (!errors.isEmpty())
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Champs requis manquants", errors));

            // 2️⃣ Récupération des entités
            Optional<ContratEmploye> contratOpt = contratEmployeService.findById(dto.getContratEmployeId());
            Company company = companyService.getById(dto.getCompanyId());

            if (contratOpt.isEmpty())
                errors.add(new ErrorResponse("contratEmployeId", "Contrat employé introuvable."));
            if (company == null)
                errors.add(new ErrorResponse("companyId", "Entreprise introuvable."));


            if (!errors.isEmpty())
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de récupération", errors));

            // 3️⃣ Vérification doublon

            ContratEmploye contrat = contratOpt.get(); // Récupère l'objet ContratEmploye
            Employe employe = contrat.getEmploye();

            boolean exists = soldeToutCompteRepository.existsByEmployeIdAndCompanyId(employe.getId(), company.getId());

            if (exists) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Un solde tout compte existe déjà pour cet employé dans cette entreprise", null));
            }

            // 4️⃣ Calculer et enregistrer
            SoldeToutCompteSaveResponseDTO saved = soldeToutCompteService.calculerEtEnregistrer(dto);

            // 5️⃣ Retour
            return new ResponseEntity<>(new ApiResponse<>(true, "Solde tout compte créé avec succès", saved), HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création du solde tout compte", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/entreprise/list")
    public ResponseEntity<?> listerParEntreprise(@RequestParam Long companyId) {
        // Vérifier existence de l'entreprise
        if (!companyRepository.existsById(companyId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Entreprise introuvable", null));
        }
        List<SoldeToutCompteSaveResponseDTO> liste = soldeToutCompteService.listerParEntreprise(companyId);

        if (liste.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Aucun solde tout compte trouvé pour cette entreprise", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste récupérée avec succès", liste));
    }

    @GetMapping("/resume")
    public ResponseEntity<?> getIndemniteResume(@RequestParam Long contratEmployeId,
                                                @RequestParam String mois) {
        try {
        List<ErrorResponse> errors = new ArrayList<>();
        // 1️⃣ Validations
        if (contratEmployeId == null)
            errors.add(new ErrorResponse("contratEmployeId", "Le champ 'contratEmployeId' est requis."));
        Optional<ContratEmploye> contratOpt = contratEmployeService.findById(contratEmployeId);
        if (contratOpt.isEmpty())
            errors.add(new ErrorResponse("contratEmployeId", "Contrat employé introuvable."));

        if (!errors.isEmpty())
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Erreurs de récupération", errors));

        // Convertir le string "2025-08" en YearMonth
        YearMonth moisYearMonth = YearMonth.parse(mois); // Format attendu : "yyyy-MM"

        IndemniteResumeDTO indemniteResumeDTO = indemniteResumeService.getResume(contratEmployeId,moisYearMonth);
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste récupérée avec succès", indemniteResumeDTO));
    }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
