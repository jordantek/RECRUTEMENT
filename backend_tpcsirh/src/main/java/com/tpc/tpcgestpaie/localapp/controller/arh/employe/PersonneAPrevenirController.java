package com.tpc.tpcgestpaie.localapp.controller.arh.employe;

import com.tpc.tpcgestpaie.localapp.dto.PersonneAPrevenirDTO;
import com.tpc.tpcgestpaie.localapp.model.PersonneAPrevenir;
import com.tpc.tpcgestpaie.localapp.service.PersonneAPrevenirService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/arh/employes/personnes-a-prevenir")
public class PersonneAPrevenirController {

    private final PersonneAPrevenirService personneAPrevenirService;

    public PersonneAPrevenirController(PersonneAPrevenirService personneAPrevenirService) {
        this.personneAPrevenirService = personneAPrevenirService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        try {
            List<PersonneAPrevenirDTO> dtos = personneAPrevenirService.getAll()
                    .stream().map(personneAPrevenirService::toDto).toList();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste récupérée avec succès", dtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération : " + e.getMessage(), null));
        }
    }

    @GetMapping("/by-employe-id/{employeId}")
    public ResponseEntity<?> getByEmploye(@PathVariable Long employeId) {
        try {
            List<PersonneAPrevenirDTO> dtos = personneAPrevenirService.findByEmployeId(employeId);

            if (dtos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Aucune personne à prévenir trouvée pour cet employé", null));
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des personnes à prévenir récupérée", dtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération : " + e.getMessage(), null));
        }
    }

    @GetMapping("/by-id/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<PersonneAPrevenir> personne = personneAPrevenirService.getById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Personne trouvée", personneAPrevenirService.toDto(personne.orElse(null))));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Introuvable : " + e.getMessage(), null));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody PersonneAPrevenir personne) {
        try {
            PersonneAPrevenir created = personneAPrevenirService.create(personne);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Créée avec succès", personneAPrevenirService.toDto(created)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Erreur de création : " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PersonneAPrevenir personne) {
        try {
            PersonneAPrevenir updated = personneAPrevenirService.update(id, personne);
            return ResponseEntity.ok(new ApiResponse<>(true, "Mis à jour avec succès", personneAPrevenirService.toDto(updated)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Erreur de mise à jour : " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            personneAPrevenirService.softDelete(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Supprimée avec succès", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur de suppression : " + e.getMessage(), null));
        }
    }

}
