package com.tpc.tpcgestpaie.localapp.service.contrat;

import com.tpc.tpcgestpaie.localapp.dto.administration.AugmentationSalarialeDTO;
import com.tpc.tpcgestpaie.localapp.dto.contrat.ContratConsolideDTO;
import com.tpc.tpcgestpaie.localapp.dto.contrat.ModificationContrat;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.service.bulletun.ContratCdiService;
import com.tpc.tpcgestpaie.localapp.service.administration.AugmentationSalarialeService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ContratPackageService {

    private final ContratConsolidationService contratConsolidationService;
    private final ContratCdiService contratService;
    private final ContratEmployeRepository contratEmployeRepository;
    private final AugmentationSalarialeService augmentationSalarialeService;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ContratPackageService.class);

    @Value("${app.upload.dir:uploads/preuves}")
    private String uploadDir;

    @Value("${app.upload.root:uploads}")
    private String uploadRootDir;

    public ContratPackageService(ContratConsolidationService contratConsolidationService,
                                 ContratCdiService contratService,
                                 ContratEmployeRepository contratEmployeRepository,
                                 AugmentationSalarialeService augmentationSalarialeService) {
        this.contratConsolidationService = contratConsolidationService;
        this.contratService = contratService;
        this.contratEmployeRepository = contratEmployeRepository;
        this.augmentationSalarialeService = augmentationSalarialeService;
    }

    @PostConstruct
    public void init() {
        log.info("📁 Répertoire de stockage des preuves: {}", uploadDir);
        log.info("📁 Répertoire racine des uploads: {}", uploadRootDir);

        try {
            // Créer les répertoires s'ils n'existent pas
            Files.createDirectories(Paths.get(uploadDir));
            Files.createDirectories(Paths.get(uploadRootDir));
            log.info("✅ Répertoires créés avec succès");
        } catch (Exception e) {
            log.error("❌ Erreur création répertoires", e);
        }
    }

    public byte[] createContratPackage(Long employeId, LocalDate dateReference) throws Exception {
        log.info("📦 DEBUT - Création package pour employé {} à {}", employeId, dateReference);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            // 1. Obtenir le contrat consolidé
            log.info("📊 Étape 1: Obtention du contrat consolidé...");
            ContratConsolideDTO contratConsolide = contratConsolidationService.getContratConsolide(employeId, dateReference);

            if (contratConsolide == null) {
                throw new IllegalArgumentException("Contrat consolidé non trouvé pour l'employé " + employeId);
            }

            log.info("✅ Contrat consolidé obtenu - {} modification(s)",
                    contratConsolide.getModifications() != null ? contratConsolide.getModifications().size() : 0);

            // 2. Obtenir les augmentations salariales dans la période
            log.info("📊 Étape 2: Récupération des augmentations salariales...");
            List<AugmentationSalarialeDTO> augmentations = getAugmentationsDansPeriode(employeId, dateReference);
            log.info("✅ {} augmentation(s) trouvée(s) dans la période", augmentations.size());

            // 3. Ajouter le PDF du contrat principal
            log.info("📊 Étape 3: Génération du PDF...");
            addContratPdfToZip(zos, contratConsolide);

            // 4. Ajouter toutes les preuves des modifications
            log.info("📊 Étape 4: Ajout des preuves des modifications...");
            addPreuvesToZip(zos, contratConsolide.getModifications());

            // 5. Ajouter les preuves des augmentations salariales
            log.info("📊 Étape 5: Ajout des preuves des augmentations...");
            addPreuvesAugmentationsToZip(zos, augmentations);

            // 6. Ajouter un fichier README avec le résumé complet
            log.info("📊 Étape 6: Création du README...");
            addReadmeToZip(zos, contratConsolide, augmentations);

            zos.finish();
            byte[] zipBytes = baos.toByteArray();

            log.info("🎉 ZIP créé avec succès - Taille: {} bytes", zipBytes.length);
            return zipBytes;

        } catch (Exception e) {
            log.error("❌ ERREUR CRITIQUE dans createContratPackage", e);
            throw new Exception("Erreur création package: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère les augmentations salariales pour l'employé dans la période
     */
    private List<AugmentationSalarialeDTO> getAugmentationsDansPeriode(Long employeId, LocalDate dateReference) {
        try {
            List<AugmentationSalarialeDTO> augmentations = augmentationSalarialeService.getByEmployeId(employeId);

            // Filtrer les augmentations jusqu'à la date de référence
            return augmentations.stream()
                    .filter(aug -> aug.getDateEffet() != null && !aug.getDateEffet().isAfter(dateReference))
                    .sorted((a1, a2) -> a1.getDateEffet().compareTo(a2.getDateEffet()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("❌ Erreur récupération augmentations", e);
            return new ArrayList<>();
        }
    }

    /**
     * Ajoute les preuves des augmentations salariales au ZIP
     */
    private void addPreuvesAugmentationsToZip(ZipOutputStream zos, List<AugmentationSalarialeDTO> augmentations) throws Exception {
        if (augmentations == null || augmentations.isEmpty()) {
            log.info("ℹ️ Aucune augmentation à traiter");
            return;
        }
        log.info("📈 Début ajout des preuves d'augmentation - {} augmentation(s) à traiter", augmentations.size());

        int preuvesAjoutees = 0;
        int counter = 1;

        for (AugmentationSalarialeDTO augmentation : augmentations) {
            log.info("🔍 Traitement augmentation {}: Date={}, Motif={}",
                    counter, augmentation.getDateEffet(), augmentation.getMotifAugmentation());

            if (augmentation.getPreuve() != null && !augmentation.getPreuve().trim().isEmpty()) {
                try {
                    // Rechercher le fichier de preuve d'augmentation
                    Path preuvePath = getAugmentationPreuveFilePath(augmentation.getPreuve().trim());

                    if (Files.exists(preuvePath) && Files.isRegularFile(preuvePath)) {
                        byte[] preuveBytes = Files.readAllBytes(preuvePath);
                        log.info("✅ Preuve d'augmentation trouvée - Taille: {} bytes", preuveBytes.length);

                        String zipEntryName = genererNomFichierAugmentation(counter, augmentation);

                        ZipEntry entry = new ZipEntry(zipEntryName);
                        zos.putNextEntry(entry);
                        zos.write(preuveBytes);
                        zos.closeEntry();

                        preuvesAjoutees++;
                        log.info("✅ Preuve d'augmentation ajoutée au ZIP: {}", zipEntryName);
                    } else {
                        log.warn("⚠️ Fichier preuve d'augmentation non trouvé: {}", preuvePath.toAbsolutePath());
                        addPlaceholderAugmentation(zos, counter, augmentation);
                    }
                } catch (Exception e) {
                    log.error("❌ Erreur lecture preuve d'augmentation: {}", augmentation.getPreuve(), e);
                    addPlaceholderAugmentation(zos, counter, augmentation);
                }
            } else {
                log.warn("⚠️ Augmentation sans nom de fichier de preuve: Date={}", augmentation.getDateEffet());
                addPlaceholderAugmentation(zos, counter, augmentation);
            }
            counter++;
        }

        log.info("📊 Résumé augmentations: {} preuve(s) ajoutée(s) sur {} augmentation(s) traitées",
                preuvesAjoutees, augmentations.size());
    }

    /**
     * Recherche le fichier de preuve d'augmentation
     */
    private Path getAugmentationPreuveFilePath(String filename) {
        try {
            // Les augmentations sont stockées dans le dossier racine "uploads/"
            Path[] cheminsPossibles = {
                    Paths.get(uploadRootDir, filename), // Chemin racine des uploads
                    Paths.get("uploads/", filename), // Chemin relatif
                    Paths.get("./uploads/", filename), // Chemin relatif au répertoire de travail
                    Paths.get(System.getProperty("user.dir"), "uploads/", filename) // Chemin absolu
            };

            for (Path chemin : cheminsPossibles) {
                log.info("🔍 Recherche fichier augmentation: {}", chemin.toAbsolutePath());
                if (Files.exists(chemin) && Files.isRegularFile(chemin)) {
                    log.info("✅ Fichier augmentation trouvé: {}", chemin.toAbsolutePath());
                    return chemin;
                }
            }

            log.warn("❌ Fichier augmentation non trouvé dans aucun des chemins: {}", filename);
            return Paths.get(uploadRootDir, filename);

        } catch (Exception e) {
            log.error("❌ Erreur recherche fichier augmentation: {}", filename, e);
            return Paths.get(uploadRootDir, filename);
        }
    }

    /**
     * Génère un nom de fichier pour les preuves d'augmentation
     */
    private String genererNomFichierAugmentation(int index, AugmentationSalarialeDTO augmentation) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = augmentation.getDateEffet().format(formatter);

        // Nettoyer le motif pour le nom de fichier
        String motifNettoye = augmentation.getMotifAugmentation() != null ?
                augmentation.getMotifAugmentation()
                        .replace(" ", "_")
                        .replace("/", "_")
                        .replace("\\", "_")
                        .replace(":", "_")
                        .substring(0, Math.min(30, augmentation.getMotifAugmentation().length()))
                : "AUGMENTATION";

        // Garder l'extension originale du fichier
        String extension = getFileExtension(augmentation.getPreuve());

        return String.format("augmentation_%02d_%s_%s%s", index, motifNettoye, dateStr, extension);
    }

    /**
     * Crée un placeholder pour les augmentations sans preuve
     */
    private void addPlaceholderAugmentation(ZipOutputStream zos, int index, AugmentationSalarialeDTO augmentation) throws Exception {
        try {
            String nomFichier = genererNomFichierAugmentation(index, augmentation).replaceFirst("\\.[^.]+$", "_PLACEHOLDER.txt");

            StringBuilder contenu = new StringBuilder();
            contenu.append("PREUVE D'AUGMENTATION NON DISPONIBLE\n\n");
            contenu.append("Date d'effet: ").append(augmentation.getDateEffet()).append("\n");
            contenu.append("Motif: ").append(augmentation.getMotifAugmentation()).append("\n");
            contenu.append("Fichier attendu: ").append(augmentation.getPreuve() != null ? augmentation.getPreuve() : "Aucun").append("\n");

            // Ajouter les détails des rubriques augmentées
            if (augmentation.getRubriques() != null && !augmentation.getRubriques().isEmpty()) {
                contenu.append("\nRUBRIQUES AUGMENTÉES:\n");
                for (AugmentationSalarialeDTO.AugmentationRubriqueDTO rubrique : augmentation.getRubriques()) {
                    contenu.append("- ").append(rubrique.getLibelle())
                            .append(": ").append(rubrique.getAncienMontant())
                            .append(" → ").append(rubrique.getNouveauMontant())
                            .append(" (+").append(rubrique.getMontantAugmentation()).append(")\n");
                }
            }

            contenu.append("\nGénéré le: ").append(LocalDate.now()).append("\n");

            ZipEntry entry = new ZipEntry(nomFichier);
            zos.putNextEntry(entry);
            zos.write(contenu.toString().getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            log.info("📝 Placeholder d'augmentation créé: {}", nomFichier);
        } catch (Exception e) {
            log.error("❌ Erreur création placeholder augmentation", e);
        }
    }

    /**
     * README mis à jour pour inclure les augmentations
     */
    private void addReadmeToZip(ZipOutputStream zos, ContratConsolideDTO contratConsolide,
                                List<AugmentationSalarialeDTO> augmentations) throws Exception {
        StringBuilder readmeContent = new StringBuilder();
        readmeContent.append("PACKAGE CONTRAT CONSOLIDÉ - DOCUMENTATION COMPLÈTE\n");
        readmeContent.append("==================================================\n\n");

        readmeContent.append("FICHIERS INCLUS:\n");
        readmeContent.append("================\n\n");

        readmeContent.append("1. CONTRAT PRINCIPAL:\n");
        readmeContent.append("- contrat_principal.pdf : Contrat consolidé au ").append(LocalDate.now()).append("\n\n");

        readmeContent.append("2. MODIFICATIONS DU CONTRAT:\n");
        if (contratConsolide.getModifications() != null && !contratConsolide.getModifications().isEmpty()) {
            int counter = 1;
            for (ModificationContrat mod : contratConsolide.getModifications()) {
                String nomFichier = genererNomFichierPreuve(counter, mod);
                String description = mod.getMotif() != null ? mod.getMotif() : mod.getTypeModification();

                readmeContent.append("- ").append(nomFichier)
                        .append(" : ").append(description)
                        .append(" (").append(mod.getDateEffet()).append(")\n");
                counter++;
            }
        } else {
            readmeContent.append("- Aucune modification documentée\n");
        }

        readmeContent.append("\n3. AUGMENTATIONS SALARIALES:\n");
        if (augmentations != null && !augmentations.isEmpty()) {
            int counter = 1;
            for (AugmentationSalarialeDTO aug : augmentations) {
                String nomFichier = genererNomFichierAugmentation(counter, aug);

                readmeContent.append("- ").append(nomFichier)
                        .append(" : ").append(aug.getMotifAugmentation())
                        .append(" (").append(aug.getDateEffet()).append(")\n");

                // Détails des rubriques augmentées
                if (aug.getRubriques() != null && !aug.getRubriques().isEmpty()) {
                    for (AugmentationSalarialeDTO.AugmentationRubriqueDTO rubrique : aug.getRubriques()) {
                        readmeContent.append("  * ").append(rubrique.getLibelle())
                                .append(": ").append(rubrique.getAncienMontant())
                                .append(" → ").append(rubrique.getNouveauMontant())
                                .append(" (+").append(rubrique.getMontantAugmentation()).append(")\n");
                    }
                }
                counter++;
            }
        } else {
            readmeContent.append("- Aucune augmentation salariale documentée\n");
        }

        readmeContent.append("\nINFORMATIONS GÉNÉRALES:\n");
        readmeContent.append("======================\n");
        readmeContent.append("- Date de génération: ").append(LocalDate.now()).append("\n");
        readmeContent.append("- Nombre de modifications: ")
                .append(contratConsolide.getModifications() != null ? contratConsolide.getModifications().size() : 0)
                .append("\n");
        readmeContent.append("- Nombre d'augmentations: ").append(augmentations != null ? augmentations.size() : 0).append("\n");
        readmeContent.append("- Période couverte: jusqu'au ").append(contratConsolide.getContratFinal().getDate_fin() != null ?
                contratConsolide.getContratFinal().getDate_fin() : "Indéterminée").append("\n");

        ZipEntry entry = new ZipEntry("README.txt");
        zos.putNextEntry(entry);
        zos.write(readmeContent.toString().getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();

        log.info("✅ README complet ajouté au ZIP");
    }

    // === MÉTHODES EXISTANTES (à conserver) ===

    private void addContratPdfToZip(ZipOutputStream zos, ContratConsolideDTO contratConsolide) throws Exception {

        log.info("🔧 Génération PDF pour contrat ID: {}",
                contratConsolide.getContratFinal() != null ?
                        contratConsolide.getContratFinal().getId() : "null");

        try {

            if (contratConsolide.getContratFinal() == null) {
                throw new IllegalArgumentException("Contrat final est null");
            }

            // ✅ RELOAD DEPUIS LA BASE AVEC COMPANY
            ContratEmploye contrat = contratEmployeRepository
                    .findByIdWithAllRelations(contratConsolide.getContratFinal().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Contrat introuvable ID=" + contratConsolide.getContratFinal().getId()));

            log.info("Contrat ID = {}", contrat.getId());
            log.info("Company object = {}", contrat.getCompany());
            log.info("Company ID = {}",
                    contrat.getCompany() != null ? contrat.getCompany().getId() : null);

            // Générer le PDF
            byte[] pdfBytes = contratService.generatePdf(contrat);

            log.info("✅ PDF généré - Taille: {} bytes", pdfBytes.length);

            ZipEntry entry = new ZipEntry("contrat_principal.pdf");

            zos.putNextEntry(entry);
            zos.write(pdfBytes);
            zos.closeEntry();

        } catch (Exception e) {

            log.error("❌ Erreur génération PDF", e);

            ZipEntry entry = new ZipEntry("erreur_generation.txt");

            zos.putNextEntry(entry);

            String errorContent =
                    "Erreur génération PDF: " + e.getMessage() + "\n" +
                            "Contrat ID: " + contratConsolide.getContratFinal().getId();

            zos.write(errorContent.getBytes(StandardCharsets.UTF_8));

            zos.closeEntry();

            throw e;
        }
    }

    private Path getPreuveFilePath(String filename) {
        try {
            // Essayer plusieurs chemins possibles
            Path[] cheminsPossibles = {
                    Paths.get(uploadDir, filename), // Chemin configuré
                    Paths.get("uploads/preuves/", filename), // Chemin relatif à la racine du projet
                    Paths.get("./uploads/preuves/", filename), // Chemin relatif au répertoire de travail
                    Paths.get(System.getProperty("user.dir"), "uploads/preuves/", filename) // Chemin absolu
            };

            for (Path chemin : cheminsPossibles) {
                log.info("🔍 Recherche du fichier: {}", chemin.toAbsolutePath());
                if (Files.exists(chemin) && Files.isRegularFile(chemin)) {
                    log.info("✅ Fichier trouvé: {}", chemin.toAbsolutePath());
                    return chemin;
                }
            }

            log.warn("❌ Fichier non trouvé dans aucun des chemins: {}", filename);
            return Paths.get(uploadDir, filename); // Retourner le chemin par défaut

        } catch (Exception e) {
            log.error("❌ Erreur recherche fichier: {}", filename, e);
            return Paths.get(uploadDir, filename);
        }
    }

    private void addPreuvesToZip(ZipOutputStream zos, List<ModificationContrat> modifications) throws Exception {
        if (modifications == null || modifications.isEmpty()) {
            log.info("ℹ️ Aucune modification à traiter");
            return;
        }

        log.info("📁 Début ajout des preuves - {} modification(s) à traiter", modifications.size());

        // Log du répertoire de recherche
        Path uploadPath = Paths.get(uploadDir);
        log.info("📁 Répertoire de recherche: {} (absolu: {})",
                uploadDir, uploadPath.toAbsolutePath());

        int preuvesAjoutees = 0;
        int counter = 1;

        for (ModificationContrat mod : modifications) {
            log.info("🔍 Traitement modification {}: Type={}, PreuveFilename={}",
                    counter, mod.getTypeModification(), mod.getPreuveFilename());

            if (mod.getPreuveFilename() != null && !mod.getPreuveFilename().trim().isEmpty()) {
                try {
                    // Utiliser la méthode améliorée de recherche de fichier
                    Path preuvePath = getPreuveFilePath(mod.getPreuveFilename().trim());

                    if (Files.exists(preuvePath) && Files.isRegularFile(preuvePath)) {
                        byte[] preuveBytes = Files.readAllBytes(preuvePath);
                        log.info("✅ Preuve trouvée - Taille: {} bytes", preuveBytes.length);

                        String zipEntryName = genererNomFichierPreuve(counter, mod);

                        ZipEntry entry = new ZipEntry(zipEntryName);
                        zos.putNextEntry(entry);
                        zos.write(preuveBytes);
                        zos.closeEntry();

                        preuvesAjoutees++;
                        log.info("✅ Preuve ajoutée au ZIP: {}", zipEntryName);
                    } else {
                        log.warn("⚠️ Fichier preuve non trouvé: {}", preuvePath.toAbsolutePath());
                        // Lister les fichiers disponibles dans le répertoire
                        listFichiersDisponibles(uploadPath);
                        addPlaceholderPreuve(zos, counter, mod);
                    }
                } catch (Exception e) {
                    log.error("❌ Erreur lecture preuve: {}", mod.getPreuveFilename(), e);
                    addPlaceholderPreuve(zos, counter, mod);
                }
            } else {
                log.warn("⚠️ Modification sans nom de fichier de preuve: Type={}", mod.getTypeModification());
                addPlaceholderPreuve(zos, counter, mod);
            }
            counter++;
        }

        log.info("📊 Résumé: {} preuve(s) ajoutée(s) sur {} modification(s) traitées",
                preuvesAjoutees, modifications.size());
    }

    private String genererNomFichierPreuve(int index, ModificationContrat mod) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = mod.getDateEffet().format(formatter);
        String typeModif = mod.getTypeModification() != null ?
                mod.getTypeModification().replace(" ", "_") : "MODIFICATION";

        // Garder l'extension originale du fichier
        String extension = getFileExtension(mod.getPreuveFilename());

        return String.format("preuve_%02d_%s_%s%s", index, typeModif, dateStr, extension);
    }

    private void addPlaceholderPreuve(ZipOutputStream zos, int index, ModificationContrat mod) throws Exception {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String nomFichier = String.format("preuve_%02d_%s_%s_PLACEHOLDER.txt",
                    index,
                    mod.getTypeModification() != null ? mod.getTypeModification().replace(" ", "_") : "MODIFICATION",
                    mod.getDateEffet().format(formatter));

            String contenu = "PREUVE NON DISPONIBLE\n\n";
            contenu += "Type de modification: " + mod.getTypeModification() + "\n";
            contenu += "Date d'effet: " + mod.getDateEffet() + "\n";
            contenu += "Motif: " + (mod.getMotif() != null ? mod.getMotif() : "Non spécifié") + "\n";
            contenu += "Fichier attendu: " + (mod.getPreuveFilename() != null ? mod.getPreuveFilename() : "Aucun") + "\n";
            contenu += "Généré le: " + LocalDate.now() + "\n";

            ZipEntry entry = new ZipEntry(nomFichier);
            zos.putNextEntry(entry);
            zos.write(contenu.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

            log.info("📝 Placeholder créé: {}", nomFichier);
        } catch (Exception e) {
            log.error("❌ Erreur création placeholder", e);
        }
    }

    private void listFichiersDisponibles(Path repertoire) {
        try {
            if (Files.exists(repertoire) && Files.isDirectory(repertoire)) {
                List<String> fichiers = Files.list(repertoire)
                        .filter(Files::isRegularFile)
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .collect(Collectors.toList());
                log.info("📋 Fichiers disponibles dans {}: {}", repertoire, fichiers);
            } else {
                log.warn("📋 Répertoire n'existe pas ou n'est pas un dossier: {}", repertoire);
            }
        } catch (Exception e) {
            log.error("❌ Erreur listing fichiers", e);
        }
    }

    private ContratEmploye reloadContratWithRelations(ContratEmploye contrat) {
        try {
            if (contrat.getId() != null && !contrat.getId().equals(1L)) {
                return contratEmployeRepository.findByIdWithAllRelations(contrat.getId())
                        .orElse(contrat);
            }
            return contrat;
        } catch (Exception e) {
            log.error("❌ Erreur rechargement contrat", e);
            return contrat;
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        return (lastDot > 0) ? filename.substring(lastDot) : ".pdf"; // extension par défaut
    }

    /**
     * Méthode de diagnostic pour vérifier l'état des preuves et augmentations
     */
    public Map<String, Object> diagnosticComplet(Long employeId, LocalDate dateReference) {
        Map<String, Object> diagnostic = new HashMap<>();

        try {
            // Diagnostic des modifications
            ContratConsolideDTO contratConsolide = contratConsolidationService.getContratConsolide(employeId, dateReference);
            diagnostic.put("nombreModifications",
                    contratConsolide.getModifications() != null ? contratConsolide.getModifications().size() : 0);

            // Diagnostic des augmentations
            List<AugmentationSalarialeDTO> augmentations = getAugmentationsDansPeriode(employeId, dateReference);
            diagnostic.put("nombreAugmentations", augmentations.size());

            // Détails des augmentations
            List<Map<String, Object>> detailsAugmentations = new ArrayList<>();
            for (AugmentationSalarialeDTO aug : augmentations) {
                Map<String, Object> detail = new HashMap<>();
                detail.put("dateEffet", aug.getDateEffet());
                detail.put("motif", aug.getMotifAugmentation());
                detail.put("preuveFilename", aug.getPreuve());

                if (aug.getPreuve() != null) {
                    Path preuvePath = getAugmentationPreuveFilePath(aug.getPreuve());
                    detail.put("cheminComplet", preuvePath.toAbsolutePath().toString());
                    detail.put("fichierExiste", Files.exists(preuvePath));

                    if (Files.exists(preuvePath)) {
                        try {
                            detail.put("taille", Files.size(preuvePath) + " bytes");
                        } catch (Exception e) {
                            detail.put("taille", "Erreur lecture");
                        }
                    }
                }
                detailsAugmentations.add(detail);
            }
            diagnostic.put("detailsAugmentations", detailsAugmentations);

        } catch (Exception e) {
            diagnostic.put("erreur", e.getMessage());
        }

        return diagnostic;
    }
}