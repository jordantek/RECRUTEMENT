package com.tpc.tpcgestpaie.localapp.controller.paie;

import com.tpc.tpcgestpaie.localapp.dto.paie.PrelevementMensualiteDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.PrelevementMensualiteListRequest;
import com.tpc.tpcgestpaie.localapp.dto.paie.PrelevementMensualiteListResponse;
import com.tpc.tpcgestpaie.localapp.service.paie.InstitutionService;
import com.tpc.tpcgestpaie.localapp.service.paie.PrelevementMensualiteService;
import com.tpc.tpcgestpaie.localapp.service.CompanyService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.ErrorResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


@RestController
@RequestMapping("/api/paie/prelevements")
public class  PrelevementMensualiteController {

    private final PrelevementMensualiteService prelevementMensualiteService;
    private final CompanyService companyService;
    private final InstitutionService institutionService;

    public PrelevementMensualiteController(PrelevementMensualiteService prelevementMensualiteService,
                                           CompanyService companyService, InstitutionService institutionService) {
        this.prelevementMensualiteService = prelevementMensualiteService;
        this.companyService = companyService;
        this.institutionService = institutionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PrelevementMensualiteDTO>>> getAll() {
        List<PrelevementMensualiteDTO> list = prelevementMensualiteService.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des prélèvements récupérée", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PrelevementMensualiteDTO>> getById(@PathVariable Long id) {
        Optional<PrelevementMensualiteDTO> opt = prelevementMensualiteService.findById(id);
        if (opt.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Prélèvement trouvé", opt.get()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, "Prélèvement non trouvé", null));
    }

    @GetMapping("/par-entreprise/{companyId}")
    public ResponseEntity<ApiResponse<List<PrelevementMensualiteDTO>>> getByCompany(@PathVariable Long companyId) {
        if (!companyService.existsById(companyId)) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise introuvable", null));
        }
        List<PrelevementMensualiteDTO> list = prelevementMensualiteService.findByCompanyId(companyId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Liste des prélèvements de l'entreprise", list));
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> create(@RequestBody PrelevementMensualiteDTO dto) {
        try {
            List<ErrorResponse> errors = validate(dto);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Champs requis manquants ou invalides", errors));
            }

            PrelevementMensualiteDTO saved = prelevementMensualiteService.save(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Prélèvement créé avec succès", saved));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la création du prélèvement", null));
        }
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PrelevementMensualiteDTO dto) {
        try {
            if (!prelevementMensualiteService.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Prélèvement non trouvé", null));
            }

            List<ErrorResponse> errors = validate(dto);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Champs requis manquants ou invalides", errors));
            }

            dto.setUpdatedAt(LocalDateTime.now());
            PrelevementMensualiteDTO saved = prelevementMensualiteService.update(id, dto);
            return ResponseEntity.ok(new ApiResponse<>(true, "Prélèvement mis à jour avec succès", saved));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la mise à jour du prélèvement", null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        if (!prelevementMensualiteService.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Prélèvement non trouvé", null));
        }
        prelevementMensualiteService.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Prélèvement supprimé avec succès", null));
    }

    @PostMapping("/liste")
    public ResponseEntity<ApiResponse<PrelevementMensualiteListResponse>> getPrelevementsByMoisCompanyAndOptionalInstitution(
            @RequestBody PrelevementMensualiteListRequest request) {
        try {
            // Validation minimale
            if (request.getMois() == null || request.getMois().isBlank()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Le mois est requis", null));
            }
            if (request.getCompanyId() == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "L'entreprise est requise", null));
            }

            List<PrelevementMensualiteDTO> prelevements;

            if (request.getInstitutionId() != null) {
                prelevements = prelevementMensualiteService.findByMoisCompanyAndInstitution(
                        request.getMois(), request.getCompanyId(), request.getInstitutionId());
            } else {
                prelevements = prelevementMensualiteService.findByMoisAndCompany(
                        request.getMois(), request.getCompanyId());
            }

            double total = prelevements.stream()
                    .mapToDouble(p -> p.getMontant() != null ? p.getMontant().doubleValue() : 0.0)
                    .sum();

            PrelevementMensualiteListResponse response = new PrelevementMensualiteListResponse();
            response.setMois(request.getMois());
            response.setCompanyId(request.getCompanyId());
            response.setInstitutionId(request.getInstitutionId());
            response.setPrelevements(prelevements);
            response.setTotal(total);

            return ResponseEntity.ok(new ApiResponse<>(true, "Liste des prélèvements récupérée", response));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la récupération des prélèvements", null));
        }
    }


    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPrelevementsPDF(
            @RequestParam String mois,
            @RequestParam Long companyId,
            @RequestParam(required = false) Long institutionId
    ) throws JRException, IOException {
        // Récupération des données
        PrelevementMensualiteListResponse responseDTO = prelevementMensualiteService
                .getPrelevementMensualiteListResponse(mois, companyId, institutionId);

        List<PrelevementMensualiteDTO> prelevements = responseDTO.getPrelevements();
        BigDecimal totalMontant = BigDecimal.valueOf(responseDTO.getTotal());

        // Préparation des données
        List<Map<String, Object>> data = new ArrayList<>();
        for (PrelevementMensualiteDTO dto : prelevements) {
            Map<String, Object> item = new HashMap<>();
            item.put("moisPrelevementMensualite", dto.getMoisPrelevement());
            item.put("employe", dto.getEmployeNomComplet() != null ? dto.getEmployeNomComplet() : "N/A");
            item.put("institution", dto.getInstitutionNom() != null ? dto.getInstitutionNom() : "N/A");
            item.put("montantPrelevementMensualite", dto.getMontant() != null ? dto.getMontant() : BigDecimal.ZERO);

            data.add(item);
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("totalPrelevementMensualite", Double.valueOf(String.valueOf(totalMontant)));

        // Compilation et remplissage
        InputStream reportStream = getClass().getResourceAsStream("/reports/prelevement_mensualite.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=prelevements_" + mois + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

//    @GetMapping("/export/pdf")
//    public ResponseEntity<byte[]> exportPrelevementsPDF(
//            @RequestParam String mois,
//            @RequestParam Long companyId,
//            @RequestParam(required = false) Long institutionId
//    ) throws JRException, IOException {
//        // Récupération des données complètes
//        PrelevementMensualiteListResponse responseDTO = prelevementMensualiteService
//                .getPrelevementMensualiteListResponse(mois, companyId, institutionId);
//
//        List<PrelevementMensualiteDTO> prelevements = responseDTO.getPrelevements();
//        double totalMontant = responseDTO.getTotal();
//
//        // Préparation des données pour Jasper
//        List<Map<String, Object>> data = new ArrayList<>();
//        for (PrelevementMensualiteDTO dto : prelevements) {
//            Map<String, Object> item = new HashMap<>();
//            item.put("moisPrelevementMensualite", dto.getMoisPrelevement());
//            item.put("employe", dto.getEmployeNomComplet() != null ? dto.getEmployeNomComplet() : "N/A");
//            item.put("institution", dto.getInstitutionNom() != null ? dto.getInstitutionNom() : "N/A");
//            item.put("montantPrelevementMensualite", dto.getMontant() != null ? dto.getMontant().doubleValue() : 0.0);
//
//            data.add(item);
//        }
//
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("totalPrelevementMensualite", totalMontant);
//
//        // Compilation et remplissage du rapport
//        InputStream reportStream = getClass().getResourceAsStream("/reports/prelevement_mensualite.jrxml");
//        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
//
//        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
//        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
//
//        byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=prelevements_" + mois + ".pdf")
//                .contentType(MediaType.APPLICATION_PDF)
//                .body(pdfBytes);
//    }

    @PostMapping("/export/excel")
    public void exportPrelevementsExcel(
            @RequestParam String mois,
            @RequestParam Long companyId,
            @RequestParam(required = false) Long institutionId,
            HttpServletResponse response) throws JRException, IOException {

        // Récupération des données
        PrelevementMensualiteListResponse responseDTO = prelevementMensualiteService
                .getPrelevementMensualiteListResponse(mois, companyId, institutionId);

        List<PrelevementMensualiteDTO> prelevements = responseDTO.getPrelevements();
        BigDecimal totalMontant = BigDecimal.valueOf(responseDTO.getTotal());

        // Préparer les données pour Jasper
        List<Map<String, Object>> data = new ArrayList<>();
        for (PrelevementMensualiteDTO dto : prelevements) {
            Map<String, Object> item = new HashMap<>();
            item.put("moisPrelevementMensualite", dto.getMoisPrelevement());
            item.put("employe", dto.getEmployeNomComplet() != null ? dto.getEmployeNomComplet() : "N/A");
            item.put("institution", dto.getInstitutionNom() != null ? dto.getInstitutionNom() : "N/A");
            item.put("montantPrelevementMensualite", dto.getMontant() != null ? dto.getMontant() : BigDecimal.ZERO);

            data.add(item);
        }

        // Paramètres Jasper
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("totalPrelevementMensualite", totalMontant);

        // Préparation de la réponse HTTP
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "inline; filename=prelevements_" + mois + ".xlsx");

        // Chargement et compilation du JRXML
        InputStream reportStream = getClass().getResourceAsStream("/reports/xls_etat_prelevement_mensualite.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Datasource
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // Export Excel
        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setOnePagePerSheet(false);
        configuration.setRemoveEmptySpaceBetweenRows(true);
        configuration.setDetectCellType(true);
        configuration.setWhitePageBackground(false);

        JRXlsxExporter exporter = new JRXlsxExporter(DefaultJasperReportsContext.getInstance());
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
        exporter.setConfiguration(configuration);

        exporter.exportReport();
    }


//    @PostMapping("/export/excel")
//    public void exportPrelevementsExcel(
//            @RequestParam String mois,
//            @RequestParam Long companyId,
//            @RequestParam(required = false) Long institutionId,
//            HttpServletResponse response) throws JRException, IOException {
//
//        // Récupération de l'objet DTO complet avec liste + total
//        PrelevementMensualiteListResponse responseDTO = prelevementMensualiteService
//                .getPrelevementMensualiteListResponse(mois, companyId, institutionId);
//
//        List<PrelevementMensualiteDTO> prelevements = responseDTO.getPrelevements();
//        double totalMontant = responseDTO.getTotal();
//
//        // Préparation des données pour Jasper
//        List<Map<String, Object>> data = new ArrayList<>();
//        for (PrelevementMensualiteDTO dto : prelevements) {
//            Map<String, Object> item = new HashMap<>();
//            item.put("moisPrelevementMensualite", dto.getMoisPrelevement());
//            item.put("employe", dto.getEmployeNomComplet() != null ? dto.getEmployeNomComplet() : "N/A");
//            item.put("institution", dto.getInstitutionNom() != null ? dto.getInstitutionNom() : "N/A");
//            // Conversion BigDecimal -> Double avec gestion null
//            item.put("montantPrelevementMensualite", dto.getMontant() != null ? dto.getMontant().doubleValue() : 0.0);
//
//            data.add(item);
//        }
//
//        // Paramètres Jasper (notamment le total)
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("totalPrelevementMensualite", totalMontant);
//
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setHeader("Content-Disposition", "inline; filename=prelevements_" + mois + ".xlsx");
//
//        InputStream reportStream = getClass().getResourceAsStream("/reports/xls_etat_prelevement_mensualite.jrxml");
//        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
//
//        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
//
//        // Passer les paramètres ici, pas null !
//        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
//
//        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
//        configuration.setOnePagePerSheet(false);
//        configuration.setRemoveEmptySpaceBetweenRows(true);
//        configuration.setDetectCellType(true);
//        configuration.setWhitePageBackground(false);
//
//        JRXlsxExporter exporter = new JRXlsxExporter(DefaultJasperReportsContext.getInstance());
//        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
//        exporter.setConfiguration(configuration);
//
//        exporter.exportReport();
//    }
//


    private List<ErrorResponse> validate(PrelevementMensualiteDTO dto) {
        List<ErrorResponse> errors = new ArrayList<>();
        if (dto.getMontant() == null) {
            errors.add(new ErrorResponse("montant", "Le montant est requis."));
        }
        if (dto.getCompanyId() == null) {
            errors.add(new ErrorResponse("companyId", "L'entreprise est requise."));
        }
        if (dto.getContratEmployeId() == null) {
            errors.add(new ErrorResponse("contratEmployeId", "Le contrat employé est requis."));
        }
        if (dto.getEmployeId() == null) {
            errors.add(new ErrorResponse("employeId", "L'employé est requis."));
        }
        if (dto.getInstitutionId() == null) {
            errors.add(new ErrorResponse("institutionId", "L'institution est requise."));
        }
        if (dto.getMensualiteId() == null) {
            errors.add(new ErrorResponse("mensualiteId", "La mensualité est requise."));
        }
        return errors;
    }
}
