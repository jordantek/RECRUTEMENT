package com.tpc.tpcgestpaie.localapp.controller;

import com.tpc.tpcgestpaie.localapp.dto.MinistereTravailDTO;
import com.tpc.tpcgestpaie.localapp.dto.MinistereTravailMapper;
import com.tpc.tpcgestpaie.localapp.model.MinistereTravail;
import com.tpc.tpcgestpaie.localapp.service.MinistereTravailService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ministere-travail")
public class MinistereTravailController {

    private final MinistereTravailService service;

    public MinistereTravailController(MinistereTravailService service) {
        this.service = service;
    }

    // 🔹 Create
    @PostMapping
    public ResponseEntity<?> create(@RequestBody MinistereTravail ministereTravail) {
        try {
            MinistereTravail saved = service.save(ministereTravail);
            return new ResponseEntity<>(new ApiResponse<>(true, "MinistereTravail créé avec succès", MinistereTravailMapper.toDTO(saved)), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 🔹 Read all
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<MinistereTravailDTO> list = service.findAll()
                    .stream()
                    .map(MinistereTravailMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste récupérée avec succès", list));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération de la liste", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 🔹 Read by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return service.findById(id)
                    .map(MinistereTravailMapper::toDTO)
                    .map(dto -> ResponseEntity.ok(new ApiResponse<>(true, "MinistereTravail trouvé", dto)))
                    .orElse(new ResponseEntity<>(new ApiResponse<>(false, "MinistereTravail non trouvé", null), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 🔹 Update
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody MinistereTravail ministereTravail) {
        try {
            MinistereTravail updated = service.update(id, ministereTravail);
            return ResponseEntity.ok(new ApiResponse<>(true, "MinistereTravail mis à jour avec succès", MinistereTravailMapper.toDTO(updated)));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 🔹 Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "MinistereTravail supprimé avec succès", null));
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
