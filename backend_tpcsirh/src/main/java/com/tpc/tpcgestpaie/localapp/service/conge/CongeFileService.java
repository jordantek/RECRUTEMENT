package com.tpc.tpcgestpaie.localapp.service.conge;

import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.absence.DemandeAbsence;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
//import com.tpc.tpcgestpaie.localapp.repository.absence.DemandeAbsenceRepository;
import com.tpc.tpcgestpaie.localapp.service.numerisation.SecureQRService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;

@Service
public class CongeFileService {

    private final TemplateEngine templateEngine;
//    private final DemandeAbsenceRepository demandeAbsenceRepository;
    private final SecureQRService secureQRService;
    private final ContratEmployeRepository contratEmployeRepository;
    private final CompanyRepository companyRepository;

    public CongeFileService(TemplateEngine templateEngine,
//                            DemandeAbsenceRepository demandeAbsenceRepository,
                            SecureQRService secureQRService, ContratEmployeRepository contratEmployeRepository, CompanyRepository companyRepository) {
        this.templateEngine = templateEngine;
//        this.demandeAbsenceRepository = demandeAbsenceRepository;
        this.secureQRService = secureQRService;
        this.contratEmployeRepository = contratEmployeRepository;
        this.companyRepository = companyRepository;
    }

    // ========================================
    // TITRE DE REPRISE DE SERVICE
    // ========================================
    @Transactional
    public String generateHtmlRepriseService(DemandeAbsence demande) {
        if (demande == null) {
            throw new IllegalArgumentException("La demande d'absence ne peut pas être null");
        }

        Context context = new Context();
        Employe employe = demande.getEmployeDemandeur();

        Optional<ContratEmploye> contratEmploye = contratEmployeRepository.findActifByEmployeId(employe.getId());

        // Charger le logo en base64 pour le PDF
        String logoUrl = contratEmploye.get().getCompany().getLogo();
        System.out.println("Logo de l'entreprise : " + logoUrl);

        if (logoUrl != null && !logoUrl.isEmpty()) {
            try {
                // Enlever le "/" du début si présent
                String filePath = logoUrl.startsWith("/") ? logoUrl.substring(1) : logoUrl;

                // Charger le fichier depuis le système de fichiers
                Path logoPath = Paths.get(uploadDir, filePath.replace("uploads/", ""));
                System.out.println("Chemin complet du logo : " + logoPath.toAbsolutePath());

                byte[] imageBytes = Files.readAllBytes(logoPath);

                // Détecter le type MIME
                String mimeType = detectMimeType(imageBytes);

                // Encoder en base64
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                context.setVariable("enteteImage", "data:" + mimeType + ";base64," + base64Image);

                System.out.println("✅ Logo chargé en base64 pour PDF (" + mimeType + ", " + imageBytes.length + " bytes)");
            } catch (IOException e) {
                System.err.println("❌ Erreur chargement logo: " + e.getMessage());
                e.printStackTrace();
                // Fallback sur logo par défaut
                context.setVariable("enteteImage", "https://www.talentsplusafrique.com/assets/img/logo.png");
            }
        } else {
            context.setVariable("enteteImage", "https://www.talentsplusafrique.com/assets/img/logo.png");
            System.out.println("Logo par défaut utilisé");
        }


        context.setVariable("nomPrenom", employe.getNom() + " " + employe.getPrenom());
        context.setVariable("matricule", employe.getMatricule());
        context.setVariable("departement", contratEmploye.get().getDepartement() != null ? contratEmploye.get().getDepartement().getLibelle() : "");
        context.setVariable("fonction", contratEmploye.get().getPoste() != null ? contratEmploye.get().getPoste().getLibelle() : "");
        context.setVariable("natureCongé", demande.getTypeAbsence() != null ? demande.getTypeAbsence().getLibelle() : "");
        context.setVariable("dateDepartCongé", demande.getDateDebut());
        context.setVariable("dateReprisePrevue", demande.getDateFin() != null ? demande.getDateFin().plusDays(1) : null);
        context.setVariable("dateRepriseEffective", LocalDate.now());
        context.setVariable("sollicitations", null); // À adapter selon vos besoins
        context.setVariable("dateSignature", LocalDate.now());

        addSecureQRCodeToContext(context, demande);

        return templateEngine.process("titre_reprise_service", context);
    }

    public byte[] generatePdfRepriseService(DemandeAbsence demande) throws Exception {
        String htmlContent = generateHtmlRepriseService(demande);
        return convertHtmlToPdf(htmlContent);
    }

    // ========================================
    // CONGÉ DE DÉCÈS
    // ========================================
    @Transactional
    public String generateHtmlCongeDeces(DemandeAbsence demande) {
        if (demande == null) {
            throw new IllegalArgumentException("La demande d'absence ne peut pas être null");
        }

        Context context = new Context();
        Employe employe = demande.getEmployeDemandeur();
        Company company = getCompanyFromDemande(demande);


        // Charger le logo en base64 pour le PDF
        String logoUrl = company.getLogo();
        System.out.println("Logo de l'entreprise : " + logoUrl);

        if (logoUrl != null && !logoUrl.isEmpty()) {
            try {
                // Enlever le "/" du début si présent
                String filePath = logoUrl.startsWith("/") ? logoUrl.substring(1) : logoUrl;

                // Charger le fichier depuis le système de fichiers
                Path logoPath = Paths.get(uploadDir, filePath.replace("uploads/", ""));
                System.out.println("Chemin complet du logo : " + logoPath.toAbsolutePath());

                byte[] imageBytes = Files.readAllBytes(logoPath);

                // Détecter le type MIME
                String mimeType = detectMimeType(imageBytes);

                // Encoder en base64
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                context.setVariable("enteteImage", "data:" + mimeType + ";base64," + base64Image);

                System.out.println("✅ Logo chargé en base64 pour PDF (" + mimeType + ", " + imageBytes.length + " bytes)");
            } catch (IOException e) {
                System.err.println("❌ Erreur chargement logo: " + e.getMessage());
                e.printStackTrace();
                // Fallback sur logo par défaut
                context.setVariable("enteteImage", "https://www.talentsplusafrique.com/assets/img/logo.png");
            }
        } else {
            context.setVariable("enteteImage", "https://www.talentsplusafrique.com/assets/img/logo.png");
            System.out.println("Logo par défaut utilisé");
        }
        context.setVariable("entreprise", company.getName());
        context.setVariable("adresseEntreprise", company.getAddress());
        context.setVariable("telEntreprise", company.getPhone());
        context.setVariable("dateLettre", LocalDate.now());
        context.setVariable("reference", "ABS-" + demande.getId());
        context.setVariable("genre", getGenre(employe.getSexe()));
        context.setVariable("nomPrenomEmploye", employe.getNom() + " " + employe.getPrenom());
        context.setVariable("adresseEmploye", employe.getQuartier());
        context.setVariable("dateDemandeEmploye", demande.getCreatedAt().toLocalDate());
        context.setVariable("dateReceptionDemande", demande.getCreatedAt().toLocalDate());
        context.setVariable("nombreJoursCongé", demande.getNombreJours() + " jours");
        context.setVariable("dateDebutCongé", demande.getDateDebut());
        context.setVariable("dateFinCongé", demande.getDateFin());
        context.setVariable("nomPrenomSignataire", company.getSignatoryName());

        addSecureQRCodeToContext(context, demande);

        return templateEngine.process("titre_conge_deces", context);
    }

    public byte[] generatePdfCongeDeces(DemandeAbsence demande) throws Exception {
        String htmlContent = generateHtmlCongeDeces(demande);
        return convertHtmlToPdf(htmlContent);
    }

    // ========================================
    // CONGÉ PATERNITÉ
    // ========================================
    @Transactional
    public String generateHtmlCongePaternite(DemandeAbsence demande) {
        if (demande == null) {
            throw new IllegalArgumentException("La demande d'absence ne peut pas être null");
        }

        Context context = new Context();
        Employe employe = demande.getEmployeDemandeur();
        Company company = getCompanyFromDemande(demande);
        // Charger le logo en base64 pour le PDF
        String logoUrl = company.getLogo();
        System.out.println("Logo de l'entreprise : " + logoUrl);

        if (logoUrl != null && !logoUrl.isEmpty()) {
            try {
                // Enlever le "/" du début si présent
                String filePath = logoUrl.startsWith("/") ? logoUrl.substring(1) : logoUrl;

                // Charger le fichier depuis le système de fichiers
                Path logoPath = Paths.get(uploadDir, filePath.replace("uploads/", ""));
                System.out.println("Chemin complet du logo : " + logoPath.toAbsolutePath());

                byte[] imageBytes = Files.readAllBytes(logoPath);

                // Détecter le type MIME
                String mimeType = detectMimeType(imageBytes);

                // Encoder en base64
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                context.setVariable("enteteImage", "data:" + mimeType + ";base64," + base64Image);

                System.out.println("✅ Logo chargé en base64 pour PDF (" + mimeType + ", " + imageBytes.length + " bytes)");
            } catch (IOException e) {
                System.err.println("❌ Erreur chargement logo: " + e.getMessage());
                e.printStackTrace();
                // Fallback sur logo par défaut
                context.setVariable("enteteImage", "https://www.talentsplusafrique.com/assets/img/logo.png");
            }
        } else {
            context.setVariable("enteteImage", "https://www.talentsplusafrique.com/assets/img/logo.png");
            System.out.println("Logo par défaut utilisé");
        }

        context.setVariable("entreprise", company.getName());
        context.setVariable("adresseEntreprise", company.getAddress());
        context.setVariable("telEntreprise", company.getPhone());
        context.setVariable("dateLettre", LocalDate.now());
        context.setVariable("reference", "ABS-" + demande.getId());
        context.setVariable("genre", getGenre(employe.getSexe()));
        context.setVariable("nomPrenomEmploye", employe.getNom() + " " + employe.getPrenom());
        context.setVariable("adresseEmploye", employe.getQuartier());
        context.setVariable("dateDemandeEmploye", demande.getCreatedAt().toLocalDate());
        context.setVariable("dateReceptionDemande", demande.getCreatedAt().toLocalDate());
        context.setVariable("nombreJoursCongé", demande.getNombreJours() + " jours");
        context.setVariable("nombreEnfants", 1); // À adapter selon vos besoins
        context.setVariable("dateDebutCongé", demande.getDateDebut());
        context.setVariable("dateFinCongé", demande.getDateFin());
        context.setVariable("nomPrenomSignataire", company.getSignatoryName());

        addSecureQRCodeToContext(context, demande);

        return templateEngine.process("titre_conge_paternite", context);
    }

    public byte[] generatePdfCongePaternite(DemandeAbsence demande) throws Exception {
        String htmlContent = generateHtmlCongePaternite(demande);
        return convertHtmlToPdf(htmlContent);
    }

    // ========================================
    // CONGÉ PAYÉ
    // ========================================
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public String generateHtmlCongePayes(DemandeAbsence demande) {
        if (demande == null) {
            throw new IllegalArgumentException("La demande d'absence ne peut pas être null");
        }

        Context context = new Context();
        Employe employe = demande.getEmployeDemandeur();
        Company company = getCompanyFromDemande(demande);

        // Charger le logo en base64 pour le PDF
        String logoUrl = company.getLogo();
        System.out.println("Logo de l'entreprise : " + logoUrl);

        if (logoUrl != null && !logoUrl.isEmpty()) {
            try {
                // Enlever le "/" du début si présent
                String filePath = logoUrl.startsWith("/") ? logoUrl.substring(1) : logoUrl;

                // Charger le fichier depuis le système de fichiers
                Path logoPath = Paths.get(uploadDir, filePath.replace("uploads/", ""));
                System.out.println("Chemin complet du logo : " + logoPath.toAbsolutePath());

                byte[] imageBytes = Files.readAllBytes(logoPath);

                // Détecter le type MIME
                String mimeType = detectMimeType(imageBytes);

                // Encoder en base64
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                context.setVariable("enteteImage", "data:" + mimeType + ";base64," + base64Image);

                System.out.println("✅ Logo chargé en base64 pour PDF (" + mimeType + ", " + imageBytes.length + " bytes)");
            } catch (IOException e) {
                System.err.println("❌ Erreur chargement logo: " + e.getMessage());
                e.printStackTrace();
                // Fallback sur logo par défaut
                context.setVariable("enteteImage", "https://www.talentsplusafrique.com/assets/img/logo.png");
            }
        } else {
            context.setVariable("enteteImage", "https://www.talentsplusafrique.com/assets/img/logo.png");
            System.out.println("Logo par défaut utilisé");
        }

        context.setVariable("entreprise", company.getName());
        context.setVariable("adresseEntreprise", company.getAddress());
        context.setVariable("telEntreprise", company.getPhone());
        context.setVariable("dateLettre", LocalDate.now());
        context.setVariable("reference", "ABS-" + demande.getId());
        context.setVariable("genre", getGenre(employe.getSexe()));
        context.setVariable("nomPrenomEmploye", employe.getNom() + " " + employe.getPrenom());
        context.setVariable("adresseEmploye", employe.getQuartier());
        context.setVariable("dateDemandeEmploye", demande.getCreatedAt().toLocalDate());
        context.setVariable("dateDebutCongé", demande.getDateDebut());
        context.setVariable("dateFinCongé", demande.getDateFin());
        context.setVariable("dateReprise", demande.getDateFin() != null ? demande.getDateFin().plusDays(1) : null);
        context.setVariable("nomPrenomSignataire", company.getSignatoryName());

        addSecureQRCodeToContext(context, demande);

        return templateEngine.process("titre_conge_payes", context);
    }

    private String detectMimeType(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length < 4) {
            return "image/png";
        }

        // PNG
        if (imageBytes[0] == (byte) 0x89 && imageBytes[1] == 0x50 &&
                imageBytes[2] == 0x4E && imageBytes[3] == 0x47) {
            return "image/png";
        }

        // JPEG
        if (imageBytes[0] == (byte) 0xFF && imageBytes[1] == (byte) 0xD8 &&
                imageBytes[2] == (byte) 0xFF) {
            return "image/jpeg";
        }

        // GIF
        if (imageBytes[0] == 0x47 && imageBytes[1] == 0x49 && imageBytes[2] == 0x46) {
            return "image/gif";
        }

        return "image/png";
    }

    public byte[] generatePdfCongePayes(DemandeAbsence demande) throws Exception {
        String htmlContent = generateHtmlCongePayes(demande);
        return convertHtmlToPdf(htmlContent);
    }

    // ========================================
    // CONGÉ FORMATION
    // ========================================
    @Transactional
    public String generateHtmlCongeFormation(DemandeAbsence demande) {
        if (demande == null) {
            throw new IllegalArgumentException("La demande d'absence ne peut pas être null");
        }

        Context context = new Context();
        Employe employe = demande.getEmployeDemandeur();
        Company company = getCompanyFromDemande(demande);

        // Charger le logo en base64 pour le PDF
        String logoUrl = company.getLogo();
        System.out.println("Logo de l'entreprise : " + logoUrl);

        if (logoUrl != null && !logoUrl.isEmpty()) {
            try {
                // Enlever le "/" du début si présent
                String filePath = logoUrl.startsWith("/") ? logoUrl.substring(1) : logoUrl;

                // Charger le fichier depuis le système de fichiers
                Path logoPath = Paths.get(uploadDir, filePath.replace("uploads/", ""));
                System.out.println("Chemin complet du logo : " + logoPath.toAbsolutePath());

                byte[] imageBytes = Files.readAllBytes(logoPath);

                // Détecter le type MIME
                String mimeType = detectMimeType(imageBytes);

                // Encoder en base64
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                context.setVariable("enteteImage", "data:" + mimeType + ";base64," + base64Image);

                System.out.println("✅ Logo chargé en base64 pour PDF (" + mimeType + ", " + imageBytes.length + " bytes)");
            } catch (IOException e) {
                System.err.println("❌ Erreur chargement logo: " + e.getMessage());
                e.printStackTrace();
                // Fallback sur logo par défaut
                context.setVariable("enteteImage", "https://www.talentsplusafrique.com/assets/img/logo.png");
            }
        } else {
            context.setVariable("enteteImage", "https://www.talentsplusafrique.com/assets/img/logo.png");
            System.out.println("Logo par défaut utilisé");
        }
        context.setVariable("entreprise", company.getName());
        context.setVariable("adresseEntreprise", company.getAddress());
        context.setVariable("telEntreprise", company.getPhone());
        context.setVariable("dateLettre", LocalDate.now());
        context.setVariable("reference", "ABS-" + demande.getId());
        context.setVariable("genre", getGenre(employe.getSexe()));
        context.setVariable("nomPrenomEmploye", employe.getNom() + " " + employe.getPrenom());
        context.setVariable("adresseEmploye", employe.getQuartier());
        context.setVariable("dateDemandeEmploye", demande.getCreatedAt().toLocalDate());
        context.setVariable("dureeStage", demande.getNombreJours() + " jours");
        context.setVariable("dateDebutStage", demande.getDateDebut());
        context.setVariable("dateFinStage", demande.getDateFin());
        context.setVariable("nomPrenomSignataire", company.getSignatoryName());

        addSecureQRCodeToContext(context, demande);

        return templateEngine.process("titre_conge_formation", context);
    }

    public byte[] generatePdfCongeFormation(DemandeAbsence demande) throws Exception {
        String htmlContent = generateHtmlCongeFormation(demande);
        return convertHtmlToPdf(htmlContent);
    }

    // ========================================
    // CONGÉ MARIAGE
    // ========================================
    @Transactional
    public String generateHtmlCongeMariage(DemandeAbsence demande) {
        if (demande == null) {
            throw new IllegalArgumentException("La demande d'absence ne peut pas être null");
        }

        Context context = new Context();
        Employe employe = demande.getEmployeDemandeur();
        Company company = getCompanyFromDemande(demande);


        // Charger le logo en base64 pour le PDF
        String logoUrl = company.getLogo();
        System.out.println("Logo de l'entreprise : " + logoUrl);

        if (logoUrl != null && !logoUrl.isEmpty()) {
            try {
                // Enlever le "/" du début si présent
                String filePath = logoUrl.startsWith("/") ? logoUrl.substring(1) : logoUrl;

                // Charger le fichier depuis le système de fichiers
                Path logoPath = Paths.get(uploadDir, filePath.replace("uploads/", ""));
                System.out.println("Chemin complet du logo : " + logoPath.toAbsolutePath());

                byte[] imageBytes = Files.readAllBytes(logoPath);

                // Détecter le type MIME
                String mimeType = detectMimeType(imageBytes);

                // Encoder en base64
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                context.setVariable("enteteImage", "data:" + mimeType + ";base64," + base64Image);

                System.out.println("✅ Logo chargé en base64 pour PDF (" + mimeType + ", " + imageBytes.length + " bytes)");
            } catch (IOException e) {
                System.err.println("❌ Erreur chargement logo: " + e.getMessage());
                e.printStackTrace();
                // Fallback sur logo par défaut
                context.setVariable("enteteImage", "https://www.talentsplusafrique.com/assets/img/logo.png");
            }
        } else {
            context.setVariable("enteteImage", "https://www.talentsplusafrique.com/assets/img/logo.png");
            System.out.println("Logo par défaut utilisé");
        }
        context.setVariable("entreprise", company.getName());
        context.setVariable("adresseEntreprise", company.getAddress());
        context.setVariable("telEntreprise", company.getPhone());
        context.setVariable("dateLettre", LocalDate.now());
        context.setVariable("reference", "ABS-" + demande.getId());
        context.setVariable("genre", getGenre(employe.getSexe()));
        context.setVariable("nomPrenomEmploye", employe.getNom() + " " + employe.getPrenom());
        context.setVariable("adresseEmploye", employe.getQuartier());
        context.setVariable("dateDemandeEmploye", demande.getCreatedAt().toLocalDate());
        context.setVariable("dateReceptionDemande", demande.getCreatedAt().toLocalDate());
        context.setVariable("nombreJoursCongé", demande.getNombreJours() + " jours");
        context.setVariable("dateDebutCongé", demande.getDateDebut());
        context.setVariable("dateFinCongé", demande.getDateFin());
        context.setVariable("nomPrenomSignataire", company.getSignatoryName());

        addSecureQRCodeToContext(context, demande);

        return templateEngine.process("titre_conge_mariage", context);
    }

    public byte[] generatePdfCongeMariage(DemandeAbsence demande) throws Exception {
        String htmlContent = generateHtmlCongeMariage(demande);
        return convertHtmlToPdf(htmlContent);
    }

    // ========================================
    // CONGÉ SABBATIQUE
    // ========================================
    @Transactional
    public String generateHtmlCongeSabbatique(DemandeAbsence demande) {
        if (demande == null) {
            throw new IllegalArgumentException("La demande d'absence ne peut pas être null");
        }

        Context context = new Context();
        Employe employe = demande.getEmployeDemandeur();
        Company company = getCompanyFromDemande(demande);

        Optional<ContratEmploye> contratEmploye = contratEmployeRepository.findActifByEmployeId(employe.getId());

        // Charger le logo en base64 pour le PDF
        String logoUrl = company.getLogo();
        System.out.println("Logo de l'entreprise : " + logoUrl);

        if (logoUrl != null && !logoUrl.isEmpty()) {
            try {
                // Enlever le "/" du début si présent
                String filePath = logoUrl.startsWith("/") ? logoUrl.substring(1) : logoUrl;

                // Charger le fichier depuis le système de fichiers
                Path logoPath = Paths.get(uploadDir, filePath.replace("uploads/", ""));
                System.out.println("Chemin complet du logo : " + logoPath.toAbsolutePath());

                byte[] imageBytes = Files.readAllBytes(logoPath);

                // Détecter le type MIME
                String mimeType = detectMimeType(imageBytes);

                // Encoder en base64
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                context.setVariable("enteteImage", "data:" + mimeType + ";base64," + base64Image);

                System.out.println("✅ Logo chargé en base64 pour PDF (" + mimeType + ", " + imageBytes.length + " bytes)");
            } catch (IOException e) {
                System.err.println("❌ Erreur chargement logo: " + e.getMessage());
                e.printStackTrace();
                // Fallback sur logo par défaut
                context.setVariable("enteteImage", "https://www.talentsplusafrique.com/assets/img/logo.png");
            }
        } else {
            context.setVariable("enteteImage", "https://www.talentsplusafrique.com/assets/img/logo.png");
            System.out.println("Logo par défaut utilisé");
        }
        context.setVariable("entreprise", company.getName());
        context.setVariable("adresseEntreprise", company.getAddress());
        context.setVariable("telEntreprise", company.getPhone());
        context.setVariable("dateLettre", LocalDate.now());
        context.setVariable("reference", "ABS-" + demande.getId());
        context.setVariable("genre", getGenre(employe.getSexe()));
        context.setVariable("nomPrenomEmploye", employe.getNom() + " " + employe.getPrenom());
        context.setVariable("adresseEmploye", employe.getQuartier());
        context.setVariable("dateDemandeEmploye", demande.getCreatedAt().toLocalDate());
        context.setVariable("dateReceptionDemande", demande.getCreatedAt().toLocalDate());
        context.setVariable("dateDebutCongé", demande.getDateDebut());
        context.setVariable("dateFinCongé", demande.getDateFin());
        context.setVariable("posteActuel", contratEmploye.get().getPoste() != null ? contratEmploye.get().getPoste().getLibelle() : "");
        context.setVariable("nomPrenomSignataire", company.getSignatoryName());

        addSecureQRCodeToContext(context, demande);

        return templateEngine.process("titre_conge_sabbatique", context);
    }

    public byte[] generatePdfCongeSabbatique(DemandeAbsence demande) throws Exception {
        String htmlContent = generateHtmlCongeSabbatique(demande);
        return convertHtmlToPdf(htmlContent);
    }

    // ========================================
    // MÉTHODES UTILITAIRES
    // ========================================

    /**
     * Récupère la Company depuis la DemandeAbsence via le ContratEmploye
     */
    private Company getCompanyFromDemande(DemandeAbsence demande) {
        if (demande.getCompanyId() != null ) {
            Optional<Company> company = companyRepository.findById(demande.getCompanyId());
            return company.get();
        }
        throw new IllegalStateException("Impossible de récupérer l'entreprise depuis la demande d'absence");
    }

    private String getGenre(String sexe) {
        if ("MASCULIN".equalsIgnoreCase(sexe)) {
            return "Monsieur";
        }
        return "Madame";
    }

    private void addSecureQRCodeToContext(Context context, DemandeAbsence demande) {
        try {
            String secureQRData = secureQRService.generateSecureQRDataForDemandeAbsence(demande);
            String qrCodeBase64 = secureQRService.generateQRImage(secureQRData);
            context.setVariable("qrCode", "data:image/png;base64," + qrCodeBase64);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur génération QR code: " + e.getMessage());
        }
    }

    private byte[] convertHtmlToPdf(String htmlContent) throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        }
    }
}