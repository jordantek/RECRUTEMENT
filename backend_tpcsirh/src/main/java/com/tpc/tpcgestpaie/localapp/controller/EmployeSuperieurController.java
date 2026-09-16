package com.tpc.tpcgestpaie.localapp.controller;

import com.tpc.tpcgestpaie.localapp.dto.HierarchieRequest;
import com.tpc.tpcgestpaie.localapp.dto.SubordonneResponseDTO;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.service.EmployeSuperieurService;
import com.tpc.tpcgestpaie.localapp.repository.*;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/employe-superieur")
@RequiredArgsConstructor
public class EmployeSuperieurController {

    private final EmployeSuperieurService service;
    private final CompanyRepository companyRepository;
    private final EmployeRepository employeRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final UserService userService;
    private final EmployeSuperieurService employeSuperieurService;

    @PostMapping
    public ResponseEntity<?> createOrUpdateHierarchie(@RequestBody HierarchieRequest request) {

        List<String> errors = new ArrayList<>();

        try {
            // 🔹 Validation des champs obligatoires
           if (request.getContratEmployeId() == null) {
                errors.add("ContratEmployeId est obligatoire");
            }

            if (request.getSuperieurs() == null || request.getSuperieurs().isEmpty()) {
                errors.add("La liste des supérieurs hiérarchiques est obligatoire");
            }

            // 🔹 Arrêt immédiat si erreurs critiques
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            // 🔹 Récupération des entités
            ContratEmploye contrat = contratEmployeRepository
                    .findById(request.getContratEmployeId())
                    .orElse(null);
            if (contrat == null) {
                errors.add("Contrat employé non trouvé");
            }

            assert contrat != null;
            Company company = companyRepository.findById(contrat.getCompany().getId()).orElse(null);
            if (company == null) {
                errors.add("Entreprise non trouvée");
            }

            Employe employe = (contrat != null) ? contrat.getEmploye() : null;
            if (contrat != null && employe == null) {
                errors.add("Aucun employé associé à ce contrat");
            }

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            // 🔹 Construction du JSON hiérarchique
            HierarchieJson hierarchie = new HierarchieJson();
            List<String> hierarchyErrors = new ArrayList<>();

            hierarchie.setSuperieurs(
                    request.getSuperieurs().stream()
                            .map(sup -> {
                                ContratEmploye contratSup = contratEmployeRepository
                                        .findById(sup.getContratSuperieurId())
                                        .orElse(null);

                                if (contratSup == null) {
                                    hierarchyErrors.add(
                                            "Contrat supérieur introuvable : " + sup.getContratSuperieurId()
                                    );
                                    return null;
                                }

                                String fonction = contratSup.getPoste() != null
                                        ? contratSup.getPoste().getLibelle()
                                        : "Fonction non définie";

                                String companyName = contratSup.getCompany() != null
                                        ? contratSup.getCompany().getName()
                                        : "";

                                String nomComplet = contratSup.getEmploye() != null
                                        ? contratSup.getEmploye().getNom() + " " + contratSup.getEmploye().getPrenom()
                                        : "";

                                String departement = contratSup.getPoste().getDepartement() != null
                                        ? contratSup.getPoste().getDepartement().getLibelle()
                                        : "";

                                Long supEmpId = contratSup.getEmploye() != null
                                        ? contratSup.getEmploye().getId()
                                        : null;

                                return new HierarchieJson.SuperieurHierarchique(
                                        sup.getContratSuperieurId(),
                                        supEmpId,
                                        fonction,
                                        companyName,
                                        nomComplet,
                                        departement,
                                        sup.getOrdre()
                                );
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );

            if (!hierarchyErrors.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Erreurs dans la hiérarchie", hierarchyErrors));
            }

            // 🔹 Validation anti-boucle
            if (!service.validateNoCircularHierarchy(employe.getId(), hierarchie)) {
                errors.add("L'employé ne peut pas être son propre supérieur");
            }

            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Erreurs de validation", errors));
            }

            User currentUser = userService.getCurrentUser();

            EmployeSuperieur saved = service.saveOrUpdateHierarchie(
                    company, employe, contrat, hierarchie, currentUser
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Hiérarchie enregistrée avec succès", saved)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de l'enregistrement de la hiérarchie", null));
        }
    }

    @GetMapping("/contrat/{contratId}")
    public ResponseEntity<?> getHierarchieByContrat(@PathVariable Long contratId) {
        try {

            if ( contratId == null || !contratEmployeRepository.existsById(contratId)) {
                return ResponseEntity.ok(
                        new ApiResponse<>(false, "Contrat employé introuvable",
                                null)
                );
            }
            return service.getHierarchieByContrat(contratId)
                    .map(h -> ResponseEntity.ok(
                            new ApiResponse<>(true, "Hiérarchie trouvée", h)
                    ))
                    .orElse(
                            ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body(new ApiResponse<>(false, "Aucune hiérarchie trouvée pour ce contrat", null))
                    );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération de la hiérarchie", null));
        }
    }

    /**
     * Récupère tous les employés subordonnés à un employé donné
     *
     * GET /api/employe-superieur/subordonnees/{employeId}
     *
     * @param employeId ID de l'employé supérieur
     * @return Liste des employés subordonnés avec leurs informations complètes
     */


    /**
     * VERSION ALTERNATIVE avec JSON_CONTAINS
     */
    @GetMapping("/subordonnees/{employeId}")
    public ResponseEntity<Map<String, Object>> getSubordonnesV2(@PathVariable Long employeId) {

        try {
            List<SubordonneResponseDTO> subordonnes = employeSuperieurService.getSubordonnesV2(employeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("%d subordonnés trouvés (V2)", subordonnes.size()));
            response.put("data", subordonnes);
            response.put("total", subordonnes.size());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Récupère uniquement les subordonnés directs (N+1)
     */
    @GetMapping("/subordonnees-directs/{employeId}")
    public ResponseEntity<Map<String, Object>> getSubordonnesDirects(@PathVariable Long employeId) {

        try {
            List<SubordonneResponseDTO> subordonnes = employeSuperieurService.getSubordonnesDirects(employeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("%d subordonnés directs trouvés", subordonnes.size()));
            response.put("data", subordonnes);
            response.put("total", subordonnes.size());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    /**
     * Compte le nombre de subordonnés
     */
    @GetMapping("/subordonnees/{employeId}/count")
    public ResponseEntity<Map<String, Object>> compterSubordonnes(@PathVariable Long employeId) {

        try {
            Long count = employeSuperieurService.compterSubordonnes(employeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Comptage effectué avec succès");
            response.put("count", count);
            response.put("hasSubordonnes", count > 0);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Erreur lors du comptage"
            ));
        }
    }

    /**
     * Vérifie si un employé a des subordonnés
     */
    @GetMapping("/subordonnees/{employeId}/has-subordonnes")
    public ResponseEntity<Map<String, Object>> hasSubordonnes(@PathVariable Long employeId) {

        try {
            boolean hasSubordonnes = employeSuperieurService.hasSubordonnes(employeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("hasSubordonnes", hasSubordonnes);
            response.put("message", hasSubordonnes ?
                    "Cet employé a des subordonnés" :
                    "Cet employé n'a pas de subordonnés");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Erreur lors de la vérification"
            ));
        }
    }

    /**
     * ENDPOINT DE DEBUG : Affiche toutes les hiérarchies
     * À utiliser pour vérifier le contenu JSON de la colonne hierarchie
     */
    @GetMapping("/debug/hierarchies")
    public ResponseEntity<Map<String, Object>> debugHierarchies() {

        try {
            List<String> hierarchies = employeSuperieurService.debugHierarchies();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format("%d hiérarchies trouvées", hierarchies.size()));
            response.put("data", hierarchies);
            response.put("total", hierarchies.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Erreur lors du debug"
            ));
        }
    }
}
