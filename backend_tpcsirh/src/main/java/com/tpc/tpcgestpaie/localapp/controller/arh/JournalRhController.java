package com.tpc.tpcgestpaie.localapp.controller.arh;

import com.tpc.tpcgestpaie.localapp.dto.rh.CategorieEvenementDto;
import com.tpc.tpcgestpaie.localapp.dto.rh.JournalRhDto;
import com.tpc.tpcgestpaie.localapp.model.CategorieEvenement;
import com.tpc.tpcgestpaie.localapp.model.JournalRh;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.CategorieEvenementService;
import com.tpc.tpcgestpaie.localapp.service.JournalRhService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/journal_rh")
public class JournalRhController {

    private final JournalRhService service;
    private final CategorieEvenementService categorieService;
    private final UserService userService;

    public JournalRhController(JournalRhService service, CategorieEvenementService categorieService, UserService userService) {
        this.service = service;
        this.categorieService = categorieService;
        this.userService = userService;
    }


    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            User currentUser = userService.getCurrentUser();
            // Vérifier que l'utilisateur est connecté
            if (currentUser == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Utilisateur non authentifié", null), HttpStatus.UNAUTHORIZED);
            }
            // 🔥 CORRECTION : Passer l'ID utilisateur au service
            List<JournalRhDto> list = service.findAllDto(currentUser.getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Liste récupérée avec succès", list));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la récupération de la liste", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<JournalRhDto> dtoOpt = service.findByIdDto(id);
        if (dtoOpt.isEmpty()) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Journal non trouvé", null), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Journal trouvé", dtoOpt.get()));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody JournalRh journal) {
        try {

            User currentUser = userService.getCurrentUser();
            // Vérifier que l'utilisateur est connecté
            if (currentUser == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Utilisateur non authentifié", null), HttpStatus.UNAUTHORIZED);
            }

            if (journal.getDate() == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "La date est obligatoire", null), HttpStatus.BAD_REQUEST);
            }
            if (journal.getContenu() == null || journal.getContenu().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le contenu est obligatoire", null), HttpStatus.BAD_REQUEST);
            }
            if (journal.getCategorieEvenement() == null || journal.getCategorieEvenement().getId() == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "La catégorie est obligatoire", null), HttpStatus.BAD_REQUEST);
            }

            // Vérifier que la catégorie existe
            Optional<CategorieEvenement> cat = categorieService.findById(journal.getCategorieEvenement().getId());
            if (cat.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Catégorie introuvable", null), HttpStatus.NOT_FOUND);
            }
            journal.setCategorieEvenement(cat.get());

            // 🔥 AJOUTER L'UTILISATEUR CONNECTÉ
            journal.setAdded_by(currentUser);

            // Vérification doublon (date + contenu + catégorie + utilisateur + deletedAt null)
            Optional<JournalRh> doublon = service.findDuplicate(journal.getDate(), journal.getContenu(), cat.get().getId(), currentUser.getId());

            if (doublon.isPresent()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Un journal avec la même date, contenu et catégorie existe déjà", null), HttpStatus.CONFLICT);
            }

            JournalRh saved = service.save(journal);
            return new ResponseEntity<>(new ApiResponse<>(true, "Journal créé avec succès", saved), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la création", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody JournalRh updated) {
        try {

            User currentUser = userService.getCurrentUser();
            // Vérifier que l'utilisateur est connecté
            if (currentUser == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Utilisateur non authentifié", null), HttpStatus.UNAUTHORIZED);
            }

            Optional<JournalRh> existing = service.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Journal non trouvé", null), HttpStatus.NOT_FOUND);
            }

            // Vérifier que l'utilisateur est propriétaire du journal
            JournalRh journal = existing.get();
            if (!journal.getAdded_by().getId().equals(currentUser.getId())) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Vous n'êtes pas autorisé à modifier ce journal", null), HttpStatus.FORBIDDEN);
            }

            if (updated.getDate() == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "La date est obligatoire", null), HttpStatus.BAD_REQUEST);
            }
            if (updated.getContenu() == null || updated.getContenu().trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Le contenu est obligatoire", null), HttpStatus.BAD_REQUEST);
            }
            if (updated.getCategorieEvenement() == null || updated.getCategorieEvenement().getId() == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "La catégorie est obligatoire", null), HttpStatus.BAD_REQUEST);
            }

            Optional<CategorieEvenement> cat = categorieService.findById(updated.getCategorieEvenement().getId());
            if (cat.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Catégorie introuvable", null), HttpStatus.NOT_FOUND);
            }

            // 🔥 CORRECTION : Ajouter l'ID utilisateur dans la vérification de doublon
            Optional<JournalRh> doublon = service.findDuplicate(updated.getDate(), updated.getContenu(), cat.get().getId(), currentUser.getId());

            // Vérifier que le doublon n'est pas l'entrée actuelle qu'on est en train de modifier
            if (doublon.isPresent() && !doublon.get().getId().equals(id)) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Un autre journal avec la même date, contenu et catégorie existe déjà", null), HttpStatus.CONFLICT);
            }

            // Mettre à jour le journal
            journal.setDate(updated.getDate());
            journal.setContenu(updated.getContenu());
            journal.setCategorieEvenement(cat.get());
            journal.setUpdatedAt(java.time.LocalDateTime.now());

            JournalRh saved = service.save(journal);

            // Forcer le chargement avant transformation en DTO
            saved.getCategorieEvenement().getLibelle();

            JournalRhDto dto = new JournalRhDto();
            dto.setId(saved.getId());
            dto.setDate(saved.getDate());
            dto.setContenu(saved.getContenu());
            CategorieEvenementDto catDto = new CategorieEvenementDto(
                    saved.getCategorieEvenement().getId(),
                    saved.getCategorieEvenement().getLibelle()
            );
            dto.setCategorieEvenement(catDto);

            return ResponseEntity.ok(new ApiResponse<>(true, "Journal mis à jour avec succès", dto));

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la mise à jour", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Optional<JournalRh> existing = service.findById(id);
            if (existing.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Journal non trouvé", null), HttpStatus.NOT_FOUND);
            }

            JournalRh journal = existing.get();
            if (journal.getDeletedAt() != null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Journal déjà supprimé", null), HttpStatus.BAD_REQUEST);
            }

            journal.setDeletedAt(java.time.LocalDateTime.now());
            service.save(journal);

            return ResponseEntity.ok(new ApiResponse<>(true, "Journal supprimé (soft delete) avec succès", null));
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Erreur lors de la suppression", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
