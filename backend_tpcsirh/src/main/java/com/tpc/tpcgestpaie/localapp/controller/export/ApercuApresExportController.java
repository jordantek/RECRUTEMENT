package com.tpc.tpcgestpaie.localapp.controller.export;


import com.tpc.tpcgestpaie.localapp.dto.paie.BulletinPaieDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.service.CompanyService;
import com.tpc.tpcgestpaie.localapp.service.TraitementSalaireService;
import com.tpc.tpcgestpaie.localapp.service.export.ApercuApresTraitementExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apercu-apres")
public class ApercuApresExportController {

    private final ApercuApresTraitementExportService exportService;
    private final TraitementSalaireService traitementSalaireService;
    private final CompanyService companyService;

    public ApercuApresExportController(ApercuApresTraitementExportService exportService,
                                       TraitementSalaireService traitementSalaireService,
                                       CompanyService companyService) {
        this.exportService = exportService;
        this.traitementSalaireService = traitementSalaireService;
        this.companyService = companyService;
    }

    @GetMapping("/{companyId}/{mois}/pdf")
    public ResponseEntity<byte[]>
    exportApercuApresPdf(
            @PathVariable Long companyId,
            @PathVariable String mois,
            @RequestParam(required = false) Long departement) {
        try {
            // Récupérer les données d'aperçu après traitement
            List<BulletinPaieDTO> bulletinList = traitementSalaireService.apercuApres(companyId, mois, departement);

            // Récupérer le nom de l'entreprise
            String entrepriseNom = companyService.findById(companyId)
                    .map(Company::getName)
                    .orElse("Entreprise inconnue");

            // Récupérer le nom du département si spécifié
            String departementNom = null; // À récupérer depuis votre service de département

            byte[] pdfBytes = exportService.generatePdf(bulletinList, mois, entrepriseNom, departementNom);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "apercu_apres_traitement_" + mois + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erreur lors de la génération du PDF: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/{companyId}/{mois}/excel")
    public ResponseEntity<byte[]> exportApercuApresExcel(
            @PathVariable Long companyId,
            @PathVariable String mois,
            @RequestParam(required = false) Long departement) {
        try {
            // Récupérer les données d'aperçu après traitement
            List<BulletinPaieDTO> bulletinList = traitementSalaireService.apercuApres(companyId, mois, departement);

            // Récupérer le nom de l'entreprise
            String entrepriseNom = companyService.findById(companyId)
                    .map(Company::getName)
                    .orElse("Entreprise inconnue");

            // Récupérer le nom du département si spécifié
            String departementNom = null; // À récupérer depuis votre service de département

            byte[] excelBytes = exportService.generateExcel(bulletinList, mois, entrepriseNom, departementNom);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment",
                    "apercu_apres_traitement_" + mois + ".xlsx");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erreur lors de la génération du Excel: " + e.getMessage()).getBytes());
        }
    }
}