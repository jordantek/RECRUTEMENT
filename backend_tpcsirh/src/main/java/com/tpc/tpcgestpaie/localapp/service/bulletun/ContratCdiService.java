package com.tpc.tpcgestpaie.localapp.service.bulletun;

import com.tpc.tpcgestpaie.localapp.dto.contrat.ContratCdiDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDiplomeDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.ContratEmployeRubriqueDTO;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.EmployeDiplome;
import com.tpc.tpcgestpaie.localapp.model.MinistereTravail;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeDiplomeRepository;
import com.tpc.tpcgestpaie.localapp.service.MinistereTravailService;
import com.tpc.tpcgestpaie.localapp.service.numerisation.SecureQRService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
public class ContratCdiService {

    private final TemplateEngine templateEngine;
    private final ContratEmployeRepository contratEmployeRepository;
    private final EmployeDiplomeRepository employeDiplomeRepository;
    private final MinistereTravailService ministereTravailService;
    private final SecureQRService secureQRService;

    public ContratCdiService(TemplateEngine templateEngine, ContratEmployeRepository contratEmployeRepository, EmployeDiplomeRepository employeDiplomeRepository, MinistereTravailService ministereTravailService, SecureQRService secureQRService) {
        this.templateEngine = templateEngine;
        this.contratEmployeRepository = contratEmployeRepository;
        this.employeDiplomeRepository = employeDiplomeRepository;
        this.ministereTravailService = ministereTravailService;
        this.secureQRService = secureQRService;
    }

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String generateHtml(ContratEmploye contrat) {
        // Vérification de sécurité
        if (contrat == null) {
            throw new IllegalArgumentException("Le contrat ne peut pas être null");
        }
        MinistereTravail ministereTravail = ministereTravailService.getUniqueMinistere();
        Context context = new Context();

        // Charger le logo en base64 pour le PDF
        String logoUrl = contrat.getCompany().getLogo();
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

        // Ajouter TOUTES les variables nécessaires au contexte
        context.setVariable("contrat", contrat);
        context.setVariable("entreprise", contrat.getCompany().getName());
        context.setVariable("adresseEntreprise", contrat.getCompany().getAddress());
        context.setVariable("nomPrenomSignataire", contrat.getCompany().getSignatoryName());
        context.setVariable("fonctionEmploye", contrat.getPoste().getLibelle());

        context.setVariable("directeurDepartementalTravail", ministereTravail.getDirecteurDepartementalTravail());
        context.setVariable("numeroEnregistrement", contrat.getNumeroContrat());
        context.setVariable("directionEnregistrementContrat", ministereTravail.getDirectionEnregistrementContrat());

        context.setVariable("employe", contrat.getEmploye().getNom() + " " + contrat.getEmploye().getPrenom());
        context.setVariable("lieuNaissance", contrat.getEmploye().getLieu_naissance());
        context.setVariable("nationaliteEmploye", contrat.getEmploye().getNationalite());
        context.setVariable("adresseEmploye", contrat.getEmploye().getMaison());
        context.setVariable("diplomeRequis", contrat.getDiplome_requis());
        context.setVariable("missions", contrat.getMissions());
        context.setVariable("lieuTravail", contrat.getLieu_execution());

        String situationText;

        switch (contrat.getEmploye().getSituationMatrimoniale()) {
            case "CELIBATAIRE_SANS_ENFANT":
                situationText = "Célibataire sans enfant";
                break;
            case "CELIBATAIRE_AVEC_ENFANT":
                situationText = "Célibataire avec enfant";
                break;
            case "MARIE":
                situationText = "Marié";
                break;
            case "DIVORCE":
                situationText = "Divorcé";
                break;
            default:
                situationText = "Non spécifié";
        }

        context.setVariable("situationMatrimoniale", situationText);
        String genre = "Madame";
        if ("MASCULIN".equalsIgnoreCase(contrat.getEmploye().getSexe())) {
            genre = "Monsieur";
        }
        context.setVariable("genre", genre);
        // Variables formatées pour l'affichage
        BigDecimal salaire = BigDecimal.valueOf(contrat.getSalaire_brut());
        context.setVariable("salaireBrutContrat",
                NumberFormat.getNumberInstance(Locale.FRANCE).format(salaire));
        context.setVariable("salaireEnLettres",
                convertirNombreEnLettres(salaire));

        // Dates formatées
        if (contrat.getDate_debut() != null) {
            context.setVariable("dateEffet",
                    contrat.getDate_debut());
        }
        if (contrat.getDate_debut() != null) {
            context.setVariable("dateSignature",
                    contrat.getDate_debut());
        }

        if (contrat.getDebut_essai() != null) {
            context.setVariable("dateDebutEssai",
                    contrat.getDebut_essai());
        }

        if (contrat.getFin_essai() != null) {
            context.setVariable("dateFinEssai",
                    contrat.getFin_essai());
        }

        if (contrat.getEmploye().getDate_naissance() != null) {
            context.setVariable("dateNaissanceFormatee",
                    contrat.getEmploye().getDate_naissance());
        }

        ContratEmploye contrat1 = contratEmployeRepository.findByIdWithRubriques(contrat.getId())
                .orElseThrow(() -> new RuntimeException("Contrat introuvable"));

        List<ContratEmployeRubriqueDTO> rubriquesDTO = contrat1.getRubriques().stream()
                .map(ContratEmployeRubriqueDTO::fromEntity)
                .collect(Collectors.toList());

        context.setVariable("rubriques", rubriquesDTO);

        List<EmployeDiplome> diplomes = employeDiplomeRepository.findByEmployeId(contrat.getEmploye().getId());

        List<EmployeDiplomeDTO> diplomesDTO = diplomes.stream()
                .map(EmployeDiplomeDTO::fromEntity)
                .collect(Collectors.toList());

        context.setVariable("diplomes", diplomesDTO);
        // Variables par défaut au cas où certains champs seraient null
        context.setVariable("libelleTitre", "CONTRAT DE TRAVAIL À DURÉE INDÉTERMINÉE");


        try {
            // Générer les données sécurisées et chiffrées pour le CONTRAT CDI
            String secureQRData = secureQRService.generateSecureQRDataForContrat(contrat);

            // Générer l'image QR en Base64
            String qrCodeBase64 = secureQRService.generateQRImage(secureQRData);

            // Ajouter au contexte
            context.setVariable("qrCode", "data:image/png;base64," + qrCodeBase64);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur génération QR code: " + e.getMessage());
        }

        return templateEngine.process("contrat_cdi", context);
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

    public byte[] generatePdf(ContratEmploye contrat) throws Exception {
        // Déclarer une seule fois la variable
        String htmlContent;

        // Condition pour choisir le bon template selon le type de contrat
        if ("CDD".equalsIgnoreCase(contrat.getType_contrat())) {


            htmlContent = generateHtmlCdd(contrat);
        } else {
            // Par défaut CDI
            htmlContent = generateHtml(contrat);
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        }
    }

    //CDD CONTRAT
    @Transactional
    public String generateHtmlCdd(ContratEmploye contrat) {
        // Vérification de sécurité
        if (contrat == null) {
            throw new IllegalArgumentException("Le contrat ne peut pas être null");
        }

        MinistereTravail ministereTravail = ministereTravailService.getUniqueMinistere();

        Context context = new Context();

        // Charger le logo en base64 pour le PDF
        String logoUrl = contrat.getCompany().getLogo();
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
        // Ajouter TOUTES les variables nécessaires au contexte
        context.setVariable("contrat", contrat);
        context.setVariable("entreprise", contrat.getCompany().getName());
        context.setVariable("adresseEntreprise", contrat.getCompany().getAddress());
        context.setVariable("telEntreprise", contrat.getCompany().getPhone());
        context.setVariable("dgEntreprise", contrat.getCompany().getDirectorName());
        context.setVariable("nomPrenomSignataire", contrat.getCompany().getSignatoryName());
        context.setVariable("fonctionEmploye", contrat.getPoste().getLibelle());

        context.setVariable("directeurDepartementalTravail", ministereTravail.getDirecteurDepartementalTravail());
        context.setVariable("numeroEnregistrement", contrat.getNumeroContrat());
        context.setVariable("directionEnregistrementContrat", ministereTravail.getDirectionEnregistrementContrat());


        context.setVariable("employe", contrat.getEmploye().getNom() + " " + contrat.getEmploye().getPrenom());
        context.setVariable("lieuNaissance", contrat.getEmploye().getLieu_naissance());
        context.setVariable("emailEmploye", contrat.getEmploye().getEmail());
        context.setVariable("sexeEmploye", contrat.getEmploye().getSexe());
        context.setVariable("nationaliteEmploye", contrat.getEmploye().getNationalite());
        context.setVariable("adresseEmploye", contrat.getEmploye().getQuartier());
        context.setVariable("diplomeRequis", contrat.getDiplome_requis());
        context.setVariable("missions", contrat.getMissions());
        context.setVariable("lieuTravail", contrat.getLieu_execution());

        String situationText;

        switch (contrat.getEmploye().getSituationMatrimoniale()) {
            case "CELIBATAIRE_SANS_ENFANT":
                situationText = "Célibataire sans enfant";
                break;
            case "CELIBATAIRE_AVEC_ENFANT":
                situationText = "Célibataire avec enfant";
                break;
            case "MARIE":
                situationText = "Marié";
                break;
            case "DIVORCE":
                situationText = "Divorcé";
                break;
            default:
                situationText = "Non spécifié";
        }

        context.setVariable("situationMatrimoniale", situationText);

        String genre = "Madame";
        if ("MASCULIN".equalsIgnoreCase(contrat.getEmploye().getSexe())) {
            genre = "Monsieur";
        }
        context.setVariable("genre", genre);
        // Variables formatées pour l'affichage
        BigDecimal salaire = BigDecimal.valueOf(contrat.getSalaire_brut());
        context.setVariable("salaireBrutContrat",
                NumberFormat.getNumberInstance(Locale.FRANCE).format(salaire));
        context.setVariable("salaireEnLettres",
                convertirNombreEnLettres(salaire));

        // Dates formatées
        if (contrat.getDate_debut() != null) {
            context.setVariable("dateEffet",
                    contrat.getDate_debut());
        }

        if (contrat.getDate_fin() != null) {
            context.setVariable("dateFin",
                    contrat.getDate_fin());
        }

        LocalDate debutContrat = contrat.getDate_debut();
        LocalDate finContrat = contrat.getDate_fin();

        if (debutContrat != null && finContrat != null) {
            Period duree = Period.between(debutContrat, finContrat);

            int annees = duree.getYears();
            int mois = duree.getMonths();
            int jours = duree.getDays();

            String dureeLisible = "";
            if (annees > 0) dureeLisible += annees + " an(s) ";
            if (mois > 0) dureeLisible += mois + " mois ";
            if (jours > 0) dureeLisible += jours + " jour(s)";

            context.setVariable("dureeContrat", dureeLisible.trim());
        }

        if (contrat.getDate_debut() != null) {
            context.setVariable("dateSignature",
                    contrat.getDate_debut());
        }

        if (contrat.getDebut_essai() != null) {
            context.setVariable("dateDebutEssai",
                    contrat.getDebut_essai());
        }

        if (contrat.getFin_essai() != null) {
            context.setVariable("dateFinEssai",
                    contrat.getFin_essai());
        }

        LocalDate debut = contrat.getDebut_essai();
        LocalDate fin = contrat.getFin_essai();

        if (debut != null && fin != null) {
            long jours = ChronoUnit.DAYS.between(debut, fin);
            long mois = ChronoUnit.MONTHS.between(debut, fin);

            context.setVariable("periodeEssaiJours", jours + " jours");
            context.setVariable("periodeEssaiMois", mois + " mois");
        }

        if (contrat.getEmploye().getDate_naissance() != null) {
            context.setVariable("dateNaissanceFormatee",
                    contrat.getEmploye().getDate_naissance());
        }

        ContratEmploye contrat1 = contratEmployeRepository.findByIdWithRubriques(contrat.getId())
                .orElseThrow(() -> new RuntimeException("Contrat introuvable"));

        List<ContratEmployeRubriqueDTO> rubriquesDTO = contrat1.getRubriques().stream()
                .map(ContratEmployeRubriqueDTO::fromEntity)
                .collect(Collectors.toList());

        context.setVariable("rubriques", rubriquesDTO);

        List<EmployeDiplome> diplomes = employeDiplomeRepository.findByEmployeId(contrat.getEmploye().getId());

        List<EmployeDiplomeDTO> diplomesDTO = diplomes.stream()
                .map(EmployeDiplomeDTO::fromEntity)
                .collect(Collectors.toList());

        context.setVariable("diplomes", diplomesDTO);

        // Variables par défaut au cas où certains champs seraient null
        context.setVariable("libelleTitre", "CONTRAT DE TRAVAIL À DURÉE INDÉTERMINÉE");

        context.setVariable("diplomes", diplomesDTO);
        context.setVariable("libelleTitre", "CONTRAT DE TRAVAIL À DURÉE INDÉTERMINÉE");

        // ========================================
        // 🔥 GÉNÉRATION QR CODE SÉCURISÉ POUR CONTRAT
        // ========================================
        try {
            // Générer les données sécurisées et chiffrées pour le CONTRAT
            String secureQRData = secureQRService.generateSecureQRDataForContrat(contrat);

            // Générer l'image QR en Base64
            String qrCodeBase64 = secureQRService.generateQRImage(secureQRData);

            // Ajouter au contexte
            context.setVariable("qrCode", "data:image/png;base64," + qrCodeBase64);

//          log.info("✅ QR Code sécurisé généré pour contrat: {}", contrat.getNumeroContrat());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur génération QR code: " + e.getMessage());
        }

        return templateEngine.process("contrat_cdd", context);
    }

    public byte[] generatePdfCdd(ContratEmploye contrat) throws Exception {
        String htmlContent = generateHtmlCdd(contrat);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        }
    }


    //Contrat EXPATRIE
    @Transactional
    public String generateHtmlExpatrie(ContratEmploye contrat) {
        // Vérification de sécurité
        if (contrat == null) {
            throw new IllegalArgumentException("Le contrat ne peut pas être null");
        }

        MinistereTravail ministereTravail = ministereTravailService.getUniqueMinistere();

        Context context = new Context();
        // Charger le logo en base64 pour le PDF
        String logoUrl = contrat.getCompany().getLogo();
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
        // Ajouter TOUTES les variables nécessaires au contexte
        context.setVariable("contrat", contrat);
        context.setVariable("entreprise", contrat.getCompany().getName());
        context.setVariable("adresseEntreprise", contrat.getCompany().getAddress());
        context.setVariable("telEntreprise", contrat.getCompany().getPhone());
        context.setVariable("dgEntreprise", contrat.getCompany().getDirectorName());
        context.setVariable("nomPrenomSignataire", contrat.getCompany().getSignatoryName());
        context.setVariable("fonctionEmploye", contrat.getPoste().getLibelle());

        context.setVariable("directeurDepartementalTravail", ministereTravail.getDirecteurDepartementalTravail());
        context.setVariable("numeroEnregistrement", contrat.getNumeroContrat());
        context.setVariable("directionEnregistrementContrat", ministereTravail.getDirectionEnregistrementContrat());

        context.setVariable("employe", contrat.getEmploye().getNom() + " " + contrat.getEmploye().getPrenom());
        context.setVariable("lieuNaissance", contrat.getEmploye().getLieu_naissance());
        context.setVariable("emailEmploye", contrat.getEmploye().getEmail());
        context.setVariable("sexeEmploye", contrat.getEmploye().getSexe());
        context.setVariable("nationaliteEmploye", contrat.getEmploye().getNationalite());
        context.setVariable("adresseEmploye", contrat.getEmploye().getQuartier());
        context.setVariable("diplomeRequis", contrat.getDiplome_requis());
        context.setVariable("missions", contrat.getMissions());
        context.setVariable("lieuTravail", contrat.getLieu_execution());
        context.setVariable("telEmploye", contrat.getEmploye().getTelephone());

        String situationText;

        switch (contrat.getEmploye().getSituationMatrimoniale()) {
            case "CELIBATAIRE_SANS_ENFANT":
                situationText = "Célibataire sans enfant";
                break;
            case "CELIBATAIRE_AVEC_ENFANT":
                situationText = "Célibataire avec enfant";
                break;
            case "MARIE":
                situationText = "Marié";
                break;
            case "DIVORCE":
                situationText = "Divorcé";
                break;
            default:
                situationText = "Non spécifié";
        }

        context.setVariable("situationMatrimoniale", situationText);

        String genre = "Madame";
        if ("MASCULIN".equalsIgnoreCase(contrat.getEmploye().getSexe())) {
            genre = "Monsieur";
        }
        context.setVariable("genre", genre);
        // Variables formatées pour l'affichage
        BigDecimal salaire = BigDecimal.valueOf(contrat.getSalaire_brut());
        context.setVariable("salaireBrutContrat",
                NumberFormat.getNumberInstance(Locale.FRANCE).format(salaire));
        context.setVariable("salaireEnLettres",
                convertirNombreEnLettres(salaire));

        // Dates formatées
        if (contrat.getDate_debut() != null) {
            context.setVariable("dateEffet",
                    contrat.getDate_debut());
        }

        if (contrat.getDate_fin() != null) {
            context.setVariable("dateFin",
                    contrat.getDate_fin());
        }

        LocalDate debutContrat = contrat.getDate_debut();
        LocalDate finContrat = contrat.getDate_fin();

        if (debutContrat != null && finContrat != null) {
            Period duree = Period.between(debutContrat, finContrat);

            int annees = duree.getYears();
            int mois = duree.getMonths();
            int jours = duree.getDays();

            String dureeLisible = "";
            if (annees > 0) dureeLisible += annees + " an(s) ";
            if (mois > 0) dureeLisible += mois + " mois ";
            if (jours > 0) dureeLisible += jours + " jour(s)";

            context.setVariable("dureeContrat", dureeLisible.trim());
        }

        if (contrat.getDate_debut() != null) {
            context.setVariable("dateSignature",
                    contrat.getDate_debut());
        }

        if (contrat.getDebut_essai() != null) {
            context.setVariable("dateDebutEssai",
                    contrat.getDebut_essai());
        }

        if (contrat.getFin_essai() != null) {
            context.setVariable("dateFinEssai",
                    contrat.getFin_essai());
        }

        LocalDate debut = contrat.getDebut_essai();
        LocalDate fin = contrat.getFin_essai();

        if (debut != null && fin != null) {
            long jours = ChronoUnit.DAYS.between(debut, fin);
            long mois = ChronoUnit.MONTHS.between(debut, fin);

            context.setVariable("periodeEssaiJours", jours + " jours");
            context.setVariable("periodeEssaiMois", mois + " mois");
        }

        if (contrat.getEmploye().getDate_naissance() != null) {
            context.setVariable("dateNaissanceFormatee",
                    contrat.getEmploye().getDate_naissance());
        }

        ContratEmploye contrat1 = contratEmployeRepository.findByIdWithRubriques(contrat.getId())
                .orElseThrow(() -> new RuntimeException("Contrat introuvable"));

        List<ContratEmployeRubriqueDTO> rubriquesDTO = contrat1.getRubriques().stream()
                .map(ContratEmployeRubriqueDTO::fromEntity)
                .collect(Collectors.toList());

        context.setVariable("rubriques", rubriquesDTO);

        List<EmployeDiplome> diplomes = employeDiplomeRepository.findByEmployeId(contrat.getEmploye().getId());

        List<EmployeDiplomeDTO> diplomesDTO = diplomes.stream()
                .map(EmployeDiplomeDTO::fromEntity)
                .collect(Collectors.toList());

        context.setVariable("diplomes", diplomesDTO);

        // Variables par défaut au cas où certains champs seraient null
        context.setVariable("libelleTitre", "CONTRAT DE TRAVAIL À DURÉE INDÉTERMINÉE");

        // ✅ AJOUTER CETTE SECTION AVANT LE RETURN :
        try {
            // Générer les données sécurisées et chiffrées pour le CONTRAT EXPATRIE
            String secureQRData = secureQRService.generateSecureQRDataForContrat(contrat);

            // Générer l'image QR en Base64
            String qrCodeBase64 = secureQRService.generateQRImage(secureQRData);

            // Ajouter au contexte
            context.setVariable("qrCode", "data:image/png;base64," + qrCodeBase64);



        } catch (Exception e) {
           e.printStackTrace();
            throw new RuntimeException("Erreur génération QR code: " + e.getMessage());
        }

        return templateEngine.process("contrat_expatrie", context);

    }

    public byte[] generatePdfExpatrie(ContratEmploye contrat) throws Exception {
        String htmlContent = generateHtmlExpatrie(contrat);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        }
    }

    //STAGE
    @Transactional
    public String generateHtmlConvention(ContratEmploye contrat) {
        // Vérification de sécurité
        if (contrat == null) {
            throw new IllegalArgumentException("Le contrat ne peut pas être null");
        }

        MinistereTravail ministereTravail = ministereTravailService.getUniqueMinistere();

        Context context = new Context();
        // Charger le logo en base64 pour le PDF
        String logoUrl = contrat.getCompany().getLogo();
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
        // Ajouter TOUTES les variables nécessaires au contexte
        context.setVariable("contrat", contrat);
        context.setVariable("entreprise", contrat.getCompany().getName());
        context.setVariable("adresseEntreprise", contrat.getCompany().getAddress());
        context.setVariable("telEntreprise", contrat.getCompany().getPhone());
        context.setVariable("dgEntreprise", contrat.getCompany().getDirectorName());
        context.setVariable("nomPrenomSignataire", contrat.getCompany().getSignatoryName());
        context.setVariable("fonctionEmploye", contrat.getPoste().getLibelle());

        context.setVariable("directeurDepartementalTravail", ministereTravail.getDirecteurDepartementalTravail());
        context.setVariable("numeroEnregistrement", contrat.getNumeroContrat());
        context.setVariable("directionEnregistrementContrat", ministereTravail.getDirectionEnregistrementContrat());

        context.setVariable("employe", contrat.getEmploye().getNom() + " " + contrat.getEmploye().getPrenom());
        context.setVariable("lieuNaissance", contrat.getEmploye().getLieu_naissance());
        context.setVariable("emailEmploye", contrat.getEmploye().getEmail());
        context.setVariable("sexeEmploye", contrat.getEmploye().getSexe());
        context.setVariable("nationaliteEmploye", contrat.getEmploye().getNationalite());
        context.setVariable("adresseEmploye", contrat.getEmploye().getQuartier());
        context.setVariable("diplomeRequis", contrat.getDiplome_requis());
        context.setVariable("missions", contrat.getMissions());
        context.setVariable("lieuTravail", contrat.getLieu_execution());
        context.setVariable("telEmploye", contrat.getEmploye().getTelephone());

        String situationText;

        switch (contrat.getEmploye().getSituationMatrimoniale()) {
            case "CELIBATAIRE_SANS_ENFANT":
                situationText = "Célibataire sans enfant";
                break;
            case "CELIBATAIRE_AVEC_ENFANT":
                situationText = "Célibataire avec enfant";
                break;
            case "MARIE":
                situationText = "Marié";
                break;
            case "DIVORCE":
                situationText = "Divorcé";
                break;
            default:
                situationText = "Non spécifié";
        }

        context.setVariable("situationMatrimoniale", situationText);

        String genre = "Madame";
        if ("MASCULIN".equalsIgnoreCase(contrat.getEmploye().getSexe())) {
            genre = "Monsieur";
        }
        context.setVariable("genre", genre);
        // Variables formatées pour l'affichage
        BigDecimal salaire = BigDecimal.valueOf(contrat.getSalaire_brut());
        context.setVariable("salaireBrutContrat",
                NumberFormat.getNumberInstance(Locale.FRANCE).format(salaire));
        context.setVariable("salaireEnLettres",
                convertirNombreEnLettres(salaire));

        // Dates formatées
        if (contrat.getDate_debut() != null) {
            context.setVariable("dateEffet",
                    contrat.getDate_debut());
        }

        if (contrat.getDate_fin() != null) {
            context.setVariable("dateFin",
                    contrat.getDate_fin());
        }

        LocalDate debutContrat = contrat.getDate_debut();
        LocalDate finContrat = contrat.getDate_fin();

        if (debutContrat != null && finContrat != null) {
            Period duree = Period.between(debutContrat, finContrat);

            int annees = duree.getYears();
            int mois = duree.getMonths();
            int jours = duree.getDays();

            String dureeLisible = "";
            if (annees > 0) dureeLisible += annees + " an(s) ";
            if (mois > 0) dureeLisible += mois + " mois ";
            if (jours > 0) dureeLisible += jours + " jour(s)";

            context.setVariable("dureeContrat", dureeLisible.trim());
        }

        if (contrat.getDate_debut() != null) {
            context.setVariable("dateSignature",
                    contrat.getDate_debut());
        }

        if (contrat.getDebut_essai() != null) {
            context.setVariable("dateDebutEssai",
                    contrat.getDebut_essai());
        }

        if (contrat.getFin_essai() != null) {
            context.setVariable("dateFinEssai",
                    contrat.getFin_essai());
        }

        LocalDate debut = contrat.getDebut_essai();
        LocalDate fin = contrat.getFin_essai();

        if (debut != null && fin != null) {
            long jours = ChronoUnit.DAYS.between(debut, fin);
            long mois = ChronoUnit.MONTHS.between(debut, fin);

            context.setVariable("periodeEssaiJours", jours + " jours");
            context.setVariable("periodeEssaiMois", mois + " mois");
        }

        if (contrat.getEmploye().getDate_naissance() != null) {
            context.setVariable("dateNaissanceFormatee",
                    contrat.getEmploye().getDate_naissance());
        }

        ContratEmploye contrat1 = contratEmployeRepository.findByIdWithRubriques(contrat.getId())
                .orElseThrow(() -> new RuntimeException("Contrat introuvable"));

        List<ContratEmployeRubriqueDTO> rubriquesDTO = contrat1.getRubriques().stream()
                .map(ContratEmployeRubriqueDTO::fromEntity)
                .collect(Collectors.toList());

        context.setVariable("rubriques", rubriquesDTO);

        List<EmployeDiplome> diplomes = employeDiplomeRepository.findByEmployeId(contrat.getEmploye().getId());

        List<EmployeDiplomeDTO> diplomesDTO = diplomes.stream()
                .map(EmployeDiplomeDTO::fromEntity)
                .collect(Collectors.toList());

        context.setVariable("diplomes", diplomesDTO);

        // Variables par défaut au cas où certains champs seraient null
        context.setVariable("libelleTitre", "CONTRAT DE TRAVAIL À DURÉE INDÉTERMINÉE");

        try {
            // Générer les données sécurisées et chiffrées pour la CONVENTION STAGE
            String secureQRData = secureQRService.generateSecureQRDataForContrat(contrat);

            // Générer l'image QR en Base64
            String qrCodeBase64 = secureQRService.generateQRImage(secureQRData);

            // Ajouter au contexte
            context.setVariable("qrCode", "data:image/png;base64," + qrCodeBase64);

        } catch (Exception e) {
          e.printStackTrace();
            throw new RuntimeException("Erreur génération QR code: " + e.getMessage());
        }

        return templateEngine.process("convention_stage", context);
    }

    private void addSecureQRCodeToContext(Context context, ContratEmploye contrat, String typeLabel) {
        try {
            String secureQRData = secureQRService.generateSecureQRDataForContrat(contrat);
            String qrCodeBase64 = secureQRService.generateQRImage(secureQRData);
            context.setVariable("qrCode", "data:image/png;base64," + qrCodeBase64);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur génération QR code: " + e.getMessage());
        }
    }
    public byte[] generatePdfConvention(ContratEmploye contrat) throws Exception {
        String htmlContent = generateHtmlConvention(contrat);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        }
    }

    private String convertirNombreEnLettres(BigDecimal montant) {
        if (montant == null) return "zéro francs CFA";

        long partie = montant.longValue();
        if (partie == 0) return "zéro francs CFA";

        StringBuilder resultat = new StringBuilder();

        if (partie >= 1000000) {
            long millions = partie / 1000000;
            resultat.append(millions).append(millions == 1 ? " million " : " millions ");
            partie %= 1000000;
        }

        if (partie >= 1000) {
            long milliers = partie / 1000;
            resultat.append(milliers).append(" mille ");
            partie %= 1000;
        }

        if (partie > 0) {
            resultat.append(partie).append(" ");
        }

        resultat.append("francs CFA");
        return resultat.toString().trim();
    }
    /**
     * Simule un DTO depuis la base
     */

    /**
     * Rendu HTML via Thymeleaf
     */
    public String generateHtml(ContratCdiDTO dto) {
        Context ctx = new Context(Locale.FRENCH);
        ctx.setVariable("raisonSociale", dto.getRaisonSociale());
        ctx.setVariable("adresseEntreprise", dto.getAdresseEntreprise());
        ctx.setVariable("telephoneEntreprise", dto.getTelephoneEntreprise());
        ctx.setVariable("emailEntreprise", dto.getEmailEntreprise());
        ctx.setVariable("numeroRegistreCommerce", dto.getNumeroRegistreCommerce());
        ctx.setVariable("nomPrenomSignataire", dto.getNomPrenomSignataire());

        ctx.setVariable("nomPrenomEmploye", dto.getNomPrenomEmploye());
        ctx.setVariable("nationaliteEmploye", dto.getNationaliteEmploye());
        ctx.setVariable("sexeEmploye", dto.getSexeEmploye());
        ctx.setVariable("dateNaissance", dto.getDateNaissance());
        ctx.setVariable("lieuNaissance", dto.getLieuNaissance());
        ctx.setVariable("situationMatrimoniale", dto.getSituationMatrimoniale());
        ctx.setVariable("adresseEmploye", dto.getAdresseEmploye());
        ctx.setVariable("telephoneEmploye", dto.getTelephoneEmploye());
        ctx.setVariable("emailEmploye", dto.getEmailEmploye());
        ctx.setVariable("libelleCategorieEmploye", dto.getLibelleCategorieEmploye());
        ctx.setVariable("libellePoste", dto.getLibellePoste());

        ctx.setVariable("dateDebutContrat", dto.getDateDebutContrat());
        ctx.setVariable("dateFinContrat", dto.getDateFinContrat());
        ctx.setVariable("dureeContrat", dto.getDureeContrat());
        ctx.setVariable("salaireBaseContrat", dto.getSalaireBaseContrat());
        ctx.setVariable("montantPrimeIndemnite", dto.getMontantPrimeIndemnite());
        ctx.setVariable("salaireBrutContrat", dto.getSalaireBrutContrat());

        ctx.setVariable("securiteSociale", dto.getSecuriteSociale());
        ctx.setVariable("directionEnregistrementContrat", dto.getDirectionEnregistrementContrat());
        ctx.setVariable("directeurDepartementalTravail", dto.getDirecteurDepartementalTravail());

        return templateEngine.process("contrat_cdi", ctx);
    }

    /**
     * Génère un PDF avec OpenHTMLtoPDF
     */
    public byte[] generatePdf(ContratCdiDTO dto) throws Exception {
        String html = generateHtml(dto);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        }
    }
}
