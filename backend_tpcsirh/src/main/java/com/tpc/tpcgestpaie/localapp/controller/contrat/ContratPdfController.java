package com.tpc.tpcgestpaie.localapp.controller.contrat;


import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.service.bulletun.ContratCdiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/contrats")
@Slf4j
public class ContratPdfController {

    private final ContratCdiService contratService;
    private final ContratEmployeRepository contratRepository; // Ou ton service pour récupérer le contrat

    public ContratPdfController(ContratCdiService contratService, ContratEmployeRepository contratRepository) {
        this.contratService = contratService;
        this.contratRepository = contratRepository;
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getContratPdf(@PathVariable Long id) {
        try {
            // Récupérer le contrat avec toutes ses relations
            ContratEmploye contrat = contratRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Contrat non trouvé avec l'ID: " + id));

            // Vérifications de sécurité
            if (contrat.getCompany() == null) {
                throw new RuntimeException("L'entreprise du contrat est manquante");
            }

            if (contrat.getEmploye() == null) {
                throw new RuntimeException("L'employé du contrat est manquant");
            }

            if (contrat.getNatureContrat() == null || contrat.getNatureContrat().getLibelle() == null) {
                throw new RuntimeException("La nature du contrat est manquante");
            }

            log.info("Génération PDF pour contrat ID: {}, employé: {}",
                    id, contrat.getEmploye().getNom() + " " + contrat.getEmploye().getPrenom());

            byte[] pdfBytes;
            String filename;

            String nature = contrat.getNatureContrat().getLibelle().toUpperCase();
            String type = (contrat.getType_contrat() != null && contrat.getType_contrat() != null)
                    ? contrat.getType_contrat().toUpperCase()
                    : "";

            System.out.println("type je suis : " + type);

            switch (nature) {
                case "TRAVAIL":
                    if ("CDI".equals(type)) {
                        pdfBytes = contratService.generatePdf(contrat);
                        filename = "Contrat_CDI";
                    } else if ("CDD".equals(type)) {
                        pdfBytes = contratService.generatePdfCdd(contrat);
                        filename = "Contrat_CDD";
                    } else {
                        throw new RuntimeException("Type de contrat TRAVAIL inconnu : " + type);
                    }
                    break;

                case "EXPATRIE":
                    pdfBytes = contratService.generatePdfExpatrie(contrat);
                    filename = "Contrat_Expatrie";
                    break;

                case "STAGE":
                    pdfBytes = contratService.generatePdfConvention(contrat);
                    filename = "Convention_Stage";
                    break;

                default:
                    throw new RuntimeException("Nature de contrat inconnue : " + nature);
            }

            // Construire le nom du fichier final
            filename = String.format("%s_%s_%s.pdf",
                    filename,
                    contrat.getEmploye().getNom().replaceAll("[^a-zA-Z0-9]", "_"),
                    contrat.getDate_debut() != null
                            ? contrat.getDate_debut().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            : "sans_date"
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(pdfBytes.length);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Erreur lors de la génération du PDF pour le contrat {}", id, e);

            Map<String, String> errorResponse = Map.of(
                    "error", "Erreur lors de la génération du PDF",
                    "message", e.getMessage()
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse.toString().getBytes());
        }
    }

    @GetMapping("/{id}/cdd_pdf")
    public ResponseEntity<byte[]> getContratCddPdf(@PathVariable Long id) {
        try {
            // Récupérer le contrat avec toutes ses relations
            ContratEmploye contrat = contratRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Contrat non trouvé avec l'ID: " + id));

            // Vérifications de sécurité
            if (contrat.getCompany() == null) {
                throw new RuntimeException("L'entreprise du contrat est manquante");
            }

            if (contrat.getEmploye() == null) {
                throw new RuntimeException("L'employé du contrat est manquant");
            }

            log.info("Génération PDF pour contrat ID: {}, employé: {}",
                    id, contrat.getEmploye().getNom() +  " " + contrat.getEmploye().getPrenom());

            byte[] pdfBytes = contratService.generatePdfCdd(contrat);

            // Nom de fichier sécurisé
            String filename = String.format("Contrat_CDD_%s_%s.pdf",
                    contrat.getEmploye().getNom()+ "" +contrat.getEmploye().getNom().replaceAll("[^a-zA-Z0-9]", "_"),
                    contrat.getDate_debut() != null ?
                            contrat.getDate_debut().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) :
                            "sans_date");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(pdfBytes.length);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Erreur lors de la génération du PDF pour le contrat {}", id, e);

            // Retourner une erreur JSON si c'est une API REST
            Map<String, String> errorResponse = Map.of(
                    "error", "Erreur lors de la génération du PDF",
                    "message", e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse.toString().getBytes());
        }
    }


    @GetMapping("/{id}/cdi_pdf")
    public ResponseEntity<byte[]> getContratPdf1(@PathVariable Long id) {
        try {
            // Récupérer le contrat avec toutes ses relations
            ContratEmploye contrat = contratRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Contrat non trouvé avec l'ID: " + id));

            // Vérifications de sécurité
            if (contrat.getCompany() == null) {
                throw new RuntimeException("L'entreprise du contrat est manquante");
            }

            if (contrat.getEmploye() == null) {
                throw new RuntimeException("L'employé du contrat est manquant");
            }

            log.info("Génération PDF pour contrat ID: {}, employé: {}",
                    id, contrat.getEmploye().getNom() +  " " + contrat.getEmploye().getPrenom());

            byte[] pdfBytes = contratService.generatePdf(contrat);

            // Nom de fichier sécurisé
            String filename = String.format("Contrat_CDI_%s_%s.pdf",
                    contrat.getEmploye().getNom()+ "" +contrat.getEmploye().getNom().replaceAll("[^a-zA-Z0-9]", "_"),
                    contrat.getDate_debut() != null ?
                            contrat.getDate_debut().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) :
                            "sans_date");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(pdfBytes.length);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Erreur lors de la génération du PDF pour le contrat {}", id, e);

            // Retourner une erreur JSON si c'est une API REST
            Map<String, String> errorResponse = Map.of(
                    "error", "Erreur lors de la génération du PDF",
                    "message", e.getMessage()
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse.toString().getBytes());
        }
    }

    @GetMapping("/{id}/expatrie_pdf")
    public ResponseEntity<byte[]> getContratPdfExpatrie(@PathVariable Long id) {
        try {
            // Récupérer le contrat avec toutes ses relations
            ContratEmploye contrat = contratRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Contrat non trouvé avec l'ID: " + id));

            // Vérifications de sécurité
            if (contrat.getCompany() == null) {
                throw new RuntimeException("L'entreprise du contrat est manquante");
            }

            if (contrat.getEmploye() == null) {
                throw new RuntimeException("L'employé du contrat est manquant");
            }

            log.info("Génération PDF pour contrat ID: {}, employé: {}",
                    id, contrat.getEmploye().getNom() +  " " + contrat.getEmploye().getPrenom());

            byte[] pdfBytes = contratService.generatePdfExpatrie(contrat);

            // Nom de fichier sécurisé
            String filename = String.format("Contrat_Expatrie_%s_%s.pdf",
                    contrat.getEmploye().getNom()+ "" +contrat.getEmploye().getNom().replaceAll("[^a-zA-Z0-9]", "_"),
                    contrat.getDate_debut() != null ?
                            contrat.getDate_debut().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) :
                            "sans_date");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(pdfBytes.length);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Erreur lors de la génération du PDF pour le contrat {}", id, e);

            // Retourner une erreur JSON si c'est une API REST
            Map<String, String> errorResponse = Map.of(
                    "error", "Erreur lors de la génération du PDF",
                    "message", e.getMessage()
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse.toString().getBytes());
        }
    }

    @GetMapping("/{id}/stage_pdf")
    public ResponseEntity<byte[]> getContratPdfStage(@PathVariable Long id) {
        try {
            // Récupérer le contrat avec toutes ses relations
            ContratEmploye contrat = contratRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Contrat non trouvé avec l'ID: " + id));

            // Vérifications de sécurité
            if (contrat.getCompany() == null) {
                throw new RuntimeException("L'entreprise du contrat est manquante");
            }

            if (contrat.getEmploye() == null) {
                throw new RuntimeException("L'employé du contrat est manquant");
            }

            log.info("Génération PDF pour contrat ID: {}, employé: {}",
                    id, contrat.getEmploye().getNom() +  " " + contrat.getEmploye().getPrenom());

            byte[] pdfBytes = contratService.generatePdfConvention(contrat);

            // Nom de fichier sécurisé
            String filename = String.format("Convention_stage_%s_%s.pdf",
                    contrat.getEmploye().getNom()+ "" +contrat.getEmploye().getNom().replaceAll("[^a-zA-Z0-9]", "_"),
                    contrat.getDate_debut() != null ?
                            contrat.getDate_debut().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) :
                            "sans_date");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentLength(pdfBytes.length);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Erreur lors de la génération du PDF pour le contrat {}", id, e);
            // Retourner une erreur JSON si c'est une API REST
            Map<String, String> errorResponse = Map.of(
                    "error", "Erreur lors de la génération du PDF",
                    "message", e.getMessage()
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse.toString().getBytes());
        }
    }

}