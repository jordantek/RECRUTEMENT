package com.tpc.tpcgestpaie.localapp.controller.numerisation;

import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentTemplate;
import com.tpc.tpcgestpaie.localapp.service.numerisation.DocumentTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/document-templates")
@Slf4j
public class DocumentTemplateController {

    @Autowired
    private DocumentTemplateService templateService;

    /**
     * Templates par sous-catégorie
     */
    @GetMapping("/subcategory/{subCategoryId}")
    public ResponseEntity<List<DocumentTemplate>> getTemplatesBySubCategory(
            @PathVariable Long subCategoryId) {
        try {
            List<DocumentTemplate> templates = templateService.getTemplatesBySubCategory(subCategoryId);
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des templates pour la sous-catégorie {}", subCategoryId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Créer un template
     */
    @PostMapping
    public ResponseEntity<DocumentTemplate> createTemplate(@RequestBody DocumentTemplate template) {
        try {
            DocumentTemplate savedTemplate = templateService.createTemplate(template);
            return ResponseEntity.ok(savedTemplate);
        } catch (Exception e) {
            log.error("Erreur lors de la création du template", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Mettre à jour un template
     */
    @PutMapping("/{templateId}")
    public ResponseEntity<DocumentTemplate> updateTemplate(
            @PathVariable Long templateId,
            @RequestBody DocumentTemplate template) {
        try {
            template.setId(templateId);
            DocumentTemplate updatedTemplate = templateService.updateTemplate(template);
            return ResponseEntity.ok(updatedTemplate);
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du template {}", templateId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Activer/désactiver un template
     */
    @PatchMapping("/{templateId}/toggle")
    public ResponseEntity<DocumentTemplate> toggleTemplate(@PathVariable Long templateId) {
        try {
            DocumentTemplate template = templateService.toggleTemplate(templateId);
            return ResponseEntity.ok(template);
        } catch (Exception e) {
            log.error("Erreur lors du changement d'état du template {}", templateId, e);
            return ResponseEntity.badRequest().build();
        }
    }
}