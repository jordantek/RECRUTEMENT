package com.tpc.tpcgestpaie.localapp.controller.bulletin;

import com.tpc.tpcgestpaie.localapp.dto.bulletin.BulletinPaieGenerateDTO;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.repository.BanqueRepository;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.bulletin.BulletinPaieRepository;
import com.tpc.tpcgestpaie.localapp.service.EmployeService;
import com.tpc.tpcgestpaie.localapp.service.TraitementSalaireService;
import com.tpc.tpcgestpaie.localapp.service.bulletun.GenerateBulletin;
import com.tpc.tpcgestpaie.localapp.service.emailConfig.EmailConfigService;
import com.tpc.tpcgestpaie.localapp.service.paie.MontantRubriqueService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/api/bulletin")
public class BulletinController {

    private final MontantRubriqueService montantRubriqueService;
    private final BulletinPaieRepository bulletinPaieRepository;
    private final BanqueRepository banqueRepository;
    private final CompanyRepository companyRepository;
    private final TraitementSalaireService traitementSalaireService;
    private final GenerateBulletin generateBulletin;
    private final ContratEmployeRepository contratEmployeRepository;
    private final EmployeService employeService;
    private final EmployeRepository employeRepository;
    private final EmailConfigService emailConfigService;

    public BulletinController(MontantRubriqueService montantRubriqueService,
                              BulletinPaieRepository bulletinPaieRepository,
                              BanqueRepository banqueRepository,
                              CompanyRepository companyRepository,
                              TraitementSalaireService traitementSalaireService,
                              GenerateBulletin generateBulletin,
                              ContratEmployeRepository contratEmployeRepository,
                              EmployeService employeService,
                              EmployeRepository employeRepository,
                              EmailConfigService emailConfigService) {
        this.montantRubriqueService = montantRubriqueService;
        this.bulletinPaieRepository = bulletinPaieRepository;
        this.banqueRepository = banqueRepository;
        this.companyRepository = companyRepository;
        this.traitementSalaireService = traitementSalaireService;
        this.generateBulletin = generateBulletin;
        this.contratEmployeRepository = contratEmployeRepository;
        this.employeService = employeService;
        this.employeRepository = employeRepository;
        this.emailConfigService = emailConfigService;
    }

    private byte[] mergeBulletinsToPdf(List<BulletinPaieGenerateDTO> bulletins) throws Exception {
        PDFMergerUtility merger = new PDFMergerUtility();
        ByteArrayOutputStream mergedOutput = new ByteArrayOutputStream();
        merger.setDestinationStream(mergedOutput);

        for (BulletinPaieGenerateDTO dto : bulletins) {
            byte[] singlePdf = generateBulletin.generateBulletinPdf(dto);

            merger.addSource(new RandomAccessReadBuffer(singlePdf));
        }
        merger.mergeDocuments(null);
        return mergedOutput.toByteArray();
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadBulletin(
            @RequestParam Long employeId,
            @RequestParam String mois,
            @RequestParam Long companyId) {

        try {
            BulletinPaieGenerateDTO dto = traitementSalaireService.getBulletin(employeId, mois, companyId);
            // ✅ Vérification si le bulletin existe
            if (dto == null) {
                return new ResponseEntity<>(
                        new ApiResponse<>(
                                false,
                                "Aucun bulletin de paie trouvé pour le mois de " + mois + ". Veuillez d'abord effectuer le traitement des salaires pour ce mois.",
                                null
                        ),
                        HttpStatus.NOT_FOUND
                );
            }

            byte[] pdfBytes = generateBulletin.generateBulletinPdf(dto);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bulletin_" + mois + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Une erreur s'est produite lors du téléchargement du bulletin : " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/download/entreprise")
    public ResponseEntity<?> downloadBulletinsEntreprise(
            @RequestParam Long companyId,
            @RequestParam String moisDebut,
            @RequestParam String moisFin) {

        try {
            List<BulletinPaieGenerateDTO> bulletins = traitementSalaireService.getBulletinsByCompanyAndPeriode(companyId, moisDebut, moisFin);

            // ✅ Vérification si des bulletins existent
            if (bulletins == null || bulletins.isEmpty()) {
                return new ResponseEntity<>(
                        new ApiResponse<>(
                                false,
                                "Aucun bulletin de paie trouvé pour la période du " + moisDebut + " au " + moisFin + ". Veuillez d'abord effectuer le traitement des salaires pour cette période.",
                                null
                        ),
                        HttpStatus.NOT_FOUND
                );
            }

            byte[] pdfBytes = mergeBulletinsToPdf(bulletins);
            String filename = "bulletins_entreprise_" + moisDebut + "_a_" + moisFin + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Une erreur s'est produite lors du téléchargement des bulletins : " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/download/employe")
    public ResponseEntity<?> downloadBulletinsEmploye(
            @RequestParam Long employeId,
            @RequestParam Long companyId,
            @RequestParam String moisDebut,
            @RequestParam String moisFin) {

        try {
            List<BulletinPaieGenerateDTO> bulletins = traitementSalaireService.getBulletinsByEmployeAndPeriode(employeId, companyId, moisDebut, moisFin);

            // ✅ Vérification si des bulletins existent
            if (bulletins == null || bulletins.isEmpty()) {
                return new ResponseEntity<>(
                        new ApiResponse<>(
                                false,
                                "Aucun bulletin de paie trouvé pour cet employé pour la période du " + moisDebut + " au " + moisFin + ". Veuillez d'abord effectuer le traitement des salaires pour cette période.",
                                null
                        ),
                        HttpStatus.NOT_FOUND
                );
            }

            byte[] pdfBytes = mergeBulletinsToPdf(bulletins);
            String filename = "bulletins_employe_" + employeId + "_" + moisDebut + "_a_" + moisFin + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Une erreur s'est produite lors du téléchargement des bulletins : " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/download/departement")
    public ResponseEntity<?> downloadBulletinsDepartement(
            @RequestParam Long companyId,
            @RequestParam Long departementId,
            @RequestParam String moisDebut,
            @RequestParam String moisFin) {

        try {
            List<BulletinPaieGenerateDTO> bulletins = traitementSalaireService.getBulletinsByDepartementAndPeriode(departementId, companyId, moisDebut, moisFin);

            // ✅ Vérification si des bulletins existent
            if (bulletins == null || bulletins.isEmpty()) {
                return new ResponseEntity<>(
                        new ApiResponse<>(
                                false,
                                "Aucun bulletin de paie trouvé pour ce département pour la période du " + moisDebut + " au " + moisFin + ". Veuillez d'abord effectuer le traitement des salaires pour cette période.",
                                null
                        ),
                        HttpStatus.NOT_FOUND
                );
            }

            byte[] pdfBytes = mergeBulletinsToPdf(bulletins);
            String filename = "bulletins_departement_" + departementId + "_" + moisDebut + "_a_" + moisFin + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Une erreur s'est produite lors du téléchargement des bulletins : " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/send")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendBulletinByEmail(
            @RequestParam Long employeId,
            @RequestParam String mois,
            @RequestParam Long companyId) {

        try {
            // 1. Récupérer bulletin
            BulletinPaieGenerateDTO dto = traitementSalaireService.getBulletin(employeId, mois, companyId);

            // ✅ Vérification si le bulletin existe
            if (dto == null) {
                return new ResponseEntity<>(
                        new ApiResponse<>(
                                false,
                                "Aucun bulletin de paie trouvé pour le mois de " + mois + ". Veuillez d'abord effectuer le traitement des salaires pour ce mois.",
                                null
                        ),
                        HttpStatus.NOT_FOUND
                );
            }

            // 2. Générer PDF
            byte[] pdfBytes = generateBulletin.generateBulletinPdf(dto);

            // 3. Sauvegarde temporaire
            File tempFile = File.createTempFile("bulletin_" + employeId + "_" + mois, ".pdf");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(pdfBytes);
            }

            // 4. Préparer variables mail
            Map<String, Object> variables = new HashMap<>();
            variables.put("employeeName", dto.getNomPrenomEmploye());
            variables.put("companyName", dto.getNomEntreprise());
            variables.put("mois", mois);
            variables.put("logoPath", dto.getLogoEntreprise());

            // 5. Récupérer email employé
            Employe employe = employeRepository.findById(employeId)
                    .orElseThrow(() -> new RuntimeException("Employé introuvable"));
            String emailEmploye = employe.getEmail();

            // 6. Envoi email
            emailConfigService.sendBulletin(
                    emailEmploye,
                    "Bulletin de paie - " + mois,
                    variables,
                    tempFile.getAbsolutePath()
            );

            Map<String, Object> data = Map.of(
                    "employe", dto.getNomPrenomEmploye(),
                    "email", emailEmploye,
                    "mois", mois
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Le bulletin de paie a été envoyé avec succès à " + emailEmploye,
                            data
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Une erreur s'est produite lors de l'envoi du bulletin : " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/send/entreprise")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendBulletinsEntreprise(
            @RequestParam Long companyId,
            @RequestParam String moisDebut,
            @RequestParam String moisFin) {

        try {
            List<BulletinPaieGenerateDTO> bulletins = traitementSalaireService.getBulletinsByCompanyAndPeriode(companyId, moisDebut, moisFin);

            // ✅ Vérification si des bulletins existent
            if (bulletins == null || bulletins.isEmpty()) {
                return new ResponseEntity<>(
                        new ApiResponse<>(
                                false,
                                "Aucun bulletin de paie trouvé pour la période du " + moisDebut + " au " + moisFin + ". Veuillez d'abord effectuer le traitement des salaires pour cette période.",
                                null
                        ),
                        HttpStatus.NOT_FOUND
                );
            }

            List<Map<String, Object>> results = new ArrayList<>();
            int successCount = 0;
            int errorCount = 0;

            for (BulletinPaieGenerateDTO dto : bulletins) {
                try {
                    // ✅ Vérification supplémentaire pour chaque bulletin
                    if (dto == null) {
                        errorCount++;
                        continue;
                    }

                    byte[] pdfBytes = generateBulletin.generateBulletinPdf(dto);

                    File tempFile = File.createTempFile("bulletin_" + dto.getEmployeId() + "_" + moisDebut + "_" + moisFin, ".pdf");
                    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                        fos.write(pdfBytes);
                    }

                    Map<String, Object> variables = new HashMap<>();
                    variables.put("employeeName", dto.getNomPrenomEmploye());
                    variables.put("companyName", dto.getNomEntreprise());
                    variables.put("mois", moisDebut + " - " + moisFin);
                    variables.put("logoPath", dto.getLogoEntreprise());

                    Employe employe = employeRepository.findById(dto.getEmployeId())
                            .orElseThrow(() -> new RuntimeException("Employé introuvable"));
                    String emailEmploye = employe.getEmail();

                    emailConfigService.sendBulletin(
                            emailEmploye,
                            "Bulletin de paie - " + moisDebut + " - " + moisFin,
                            variables,
                            tempFile.getAbsolutePath()
                    );

                    results.add(Map.of(
                            "employe", dto.getNomPrenomEmploye(),
                            "email", emailEmploye,
                            "status", "Envoyé avec succès"
                    ));
                    successCount++;

                } catch (Exception e) {
                    results.add(Map.of(
                            "employeId", dto.getEmployeId(),
                            "status", "Échec de l'envoi",
                            "raison", e.getMessage()
                    ));
                    errorCount++;
                }
            }

            Map<String, Object> data = Map.of(
                    "totalBulletins", bulletins.size(),
                    "envoyesAvecSucces", successCount,
                    "echoues", errorCount,
                    "details", results
            );

            String message = String.format(
                    "%d bulletin(s) envoyé(s) avec succès sur %d. %d échec(s).",
                    successCount, bulletins.size(), errorCount
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(true, message, data)
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Une erreur s'est produite lors de l'envoi des bulletins : " + e.getMessage(),
                            null
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}