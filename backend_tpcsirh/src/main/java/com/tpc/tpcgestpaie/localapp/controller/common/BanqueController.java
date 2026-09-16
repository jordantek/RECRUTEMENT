package com.tpc.tpcgestpaie.localapp.controller.common;

import com.tpc.tpcgestpaie.localapp.model.Banque;
import com.tpc.tpcgestpaie.localapp.service.BanqueService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/arh/banques")
public class BanqueController {

    private final BanqueService banqueService;

    public BanqueController(BanqueService banqueService) {
        this.banqueService = banqueService;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<Banque> banques = banqueService.findAll();
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des banques récupérée avec succès", banques));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération des banques", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Optional<Banque> banque = banqueService.findById(id);
            if (banque.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Banque trouvée", banque.get()));
            }
            return new ResponseEntity<>(new ApiResponse<>(false, "Banque non trouvée", null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération de la banque", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Banque banque) {
        try {
            if (banque.getName() == null || banque.getName().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le nom de la banque est vide", null), HttpStatus.LENGTH_REQUIRED);
            }

            String nomFormate = banque.getName().trim().toUpperCase();

            if (banqueService.existsByName(nomFormate)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(false, "Le nom de la banque existe déjà.", null));
            }

            banque.setName(nomFormate);
            Banque saved = banqueService.save(banque);
            return new ResponseEntity<>(new ApiResponse<>(true, "Banque créée avec succès", saved), HttpStatus.CREATED);
        }catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(
                            false,
                            "Une banque avec ce nom existe déjà",
                            null
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Erreur lors de la création de la banque",
                            null
                    ));
        }
    }



    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Banque updatedBanque) {
        try {

            Optional<Banque> existing = banqueService.findById(id);
            if (existing.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Banque non trouvée", null));
            }

            if (updatedBanque.getName() == null || updatedBanque.getName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.LENGTH_REQUIRED)
                        .body(new ApiResponse<>(false, "Le nom de la banque est requis", null));
            }

            Banque banque = existing.get();
            String nomFormate = updatedBanque.getName().trim().toUpperCase();
            banque.setName(nomFormate);
            banque.setUpdated_at(LocalDateTime.now());

            Banque saved = banqueService.save(banque);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Banque mise à jour avec succès", saved)
            );

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(
                            false,
                            "Une banque avec ce nom existe déjà",
                            null
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Erreur lors de la mise à jour de la banque",
                            null
                    ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<Banque> existing = banqueService.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Banque non trouvée", null), HttpStatus.NOT_FOUND);
            }

            banqueService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Banque supprimée avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression de la banque", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
