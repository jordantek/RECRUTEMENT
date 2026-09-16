package com.tpc.tpcgestpaie.localapp.controller.export;

import com.tpc.tpcgestpaie.localapp.service.etat.EtatChargeSocialeService;
import com.tpc.tpcgestpaie.localapp.service.export.BilanMensuelChargeSocialeExportService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/etats")
public class EtatsExportController {

    private final EtatChargeSocialeService etatChargeSocialeService;
    private final BilanMensuelChargeSocialeExportService exportService;

    public EtatsExportController(EtatChargeSocialeService etatChargeSocialeService,
                                       BilanMensuelChargeSocialeExportService exportService) {
        this.etatChargeSocialeService = etatChargeSocialeService;
        this.exportService = exportService;
    }

    @GetMapping("/bilan-mensuel/{companyId}/{mois}/pdf")
    public ResponseEntity<byte[]> exportBilanMensuelPdf(
            @PathVariable Long companyId,
            @PathVariable String mois) {
        try {
            byte[] pdfBytes = exportService.generatePdf(companyId, mois);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "bilan_charges_sociales_" + mois + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erreur lors de la génération du PDF: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/bilan-mensuel/{companyId}/{mois}/excel")
    public ResponseEntity<byte[]> exportBilanMensuelExcel(
            @PathVariable Long companyId,
            @PathVariable String mois) {
        try {
            byte[] excelBytes = exportService.generateExcel(companyId, mois);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment",
                    "bilan_charges_sociales_" + mois + ".xlsx");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Erreur lors de la génération du Excel: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/bilan-periodique/entreprise/{companyId}/pdf")
    public ResponseEntity<byte[]> exportBilanPeriodiquePdf(
            @PathVariable Long companyId,
            @RequestParam("debut") String debut,
            @RequestParam("fin") String fin) {
        try {
            byte[] pdfBytes = exportService.generatePdfBilanPeriodique(companyId, debut, fin);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment",
                    "bilan_periodique_charges_" + debut + "_a_" + fin + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/bilan-periodique/entreprise/{companyId}/excel")
    public ResponseEntity<byte[]> exportBilanPeriodiqueExcel(
            @PathVariable Long companyId,
            @RequestParam("debut") String debut,
            @RequestParam("fin") String fin) {
        try {
            byte[] excelBytes = exportService.generateExcelBilanPeriodique(companyId, debut, fin);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment",
                    "bilan_periodique_charges_" + debut + "_a_" + fin + ".xlsx");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/bilan-periodique/{companyId}/{employeId}/export-pdf")
    public ResponseEntity<byte[]> exportBilanPeriodiquePdf(
            @PathVariable Long companyId,
            @PathVariable Long employeId,
            @RequestParam("debut") String debut,
            @RequestParam("fin") String fin) {

        byte[] pdfBytes = exportService.generatePdfBilanPeriodiqueEmploye(companyId, employeId, debut, fin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("bilan_periodique_charges_" + employeId + "_" + debut + "_" + fin + ".pdf")
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/bilan-periodique/{companyId}/{employeId}/export-excel")
    public ResponseEntity<byte[]> exportBilanPeriodiqueExcel(
            @PathVariable Long companyId,
            @PathVariable Long employeId,
            @RequestParam("debut") String debut,
            @RequestParam("fin") String fin) {

        byte[] excelBytes = exportService.generateExcelBilanPeriodiqueEmploye(companyId, employeId, debut, fin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("bilan_periodique_charges_" + employeId + "_" + debut + "_" + fin + ".xlsx")
                .build());

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }

    // === BILAN FISCAL ===

    @GetMapping("/charge-fiscale-par-entreprise/export-pdf")
    public ResponseEntity<byte[]> exportBilanFiscalPdf(
            @RequestParam Long idEntreprise,
            @RequestParam(required = false) String mois,
            @RequestParam(required = false) String moisDebut,
            @RequestParam(required = false) String moisFin) {

        byte[] pdfBytes = exportService.generatePdfBilanFiscal(idEntreprise, mois, moisDebut, moisFin);

        String filename = "bilan_fiscal_" + idEntreprise;
        if (mois != null) {
            filename += "_" + mois;
        } else {
            filename += "_" + moisDebut + "_" + moisFin;
        }
        filename += ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(filename)
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/charge-fiscale-par-entreprise/export-excel")
    public ResponseEntity<byte[]> exportBilanFiscalExcel(
            @RequestParam Long idEntreprise,
            @RequestParam(required = false) String mois,
            @RequestParam(required = false) String moisDebut,
            @RequestParam(required = false) String moisFin) {

        byte[] excelBytes = exportService.generateExcelBilanFiscal(idEntreprise, mois, moisDebut, moisFin);

        String filename = "bilan_fiscal_" + idEntreprise;
        if (mois != null) {
            filename += "_" + mois;
        } else {
            filename += "_" + moisDebut + "_" + moisFin;
        }
        filename += ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(filename)
                .build());

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }

    // === BILAN CHARGE SOCIALE PAR EMPLOYÉ ===

    @GetMapping("/charge-sociale-par-employe/export-pdf")
    public ResponseEntity<byte[]> exportBilanChargeSocialePdf(
            @RequestParam Long idEmploye,
            @RequestParam(required = false) String mois,
            @RequestParam(required = false) String moisDebut,
            @RequestParam(required = false) String moisFin) {

        byte[] pdfBytes = exportService.generatePdfBilanChargeSocialeEmploye(idEmploye, mois, moisDebut, moisFin);

        String filename = "bilan_charge_sociale_employe_" + idEmploye;
        if (mois != null) {
            filename += "_" + mois;
        } else {
            filename += "_" + moisDebut + "_" + moisFin;
        }
        filename += ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(filename)
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/charge-sociale-par-employe/export-excel")
    public ResponseEntity<byte[]> exportBilanChargeSocialeExcel(
            @RequestParam Long idEmploye,
            @RequestParam(required = false) String mois,
            @RequestParam(required = false) String moisDebut,
            @RequestParam(required = false) String moisFin) {

        byte[] excelBytes = exportService.generateExcelBilanChargeSocialeEmploye(idEmploye, mois, moisDebut, moisFin);

        String filename = "bilan_charge_sociale_employe_" + idEmploye;
        if (mois != null) {
            filename += "_" + mois;
        } else {
            filename += "_" + moisDebut + "_" + moisFin;
        }
        filename += ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(filename)
                .build());

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}