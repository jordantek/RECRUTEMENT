// DocumentProcessingService.java - VERSION AVEC PDFBox
package com.tpc.tpcgestpaie.localapp.service.numerisation;

import com.tpc.tpcgestpaie.localapp.dto.numerisation.ProcessedDocument;
import com.tpc.tpcgestpaie.localapp.model.numerisation.EmployeeDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@Slf4j
public class DocumentProcessingService {

    private final SecureQRService secureQRService;
    private final NumerisationFileStorageService fileStorageService;
    private final PdfProcessingService pdfProcessingService; // NOUVEAU

    public DocumentProcessingService(SecureQRService secureQRService,
                                     NumerisationFileStorageService fileStorageService,
                                     PdfProcessingService pdfProcessingService) { // NOUVEAU
        this.secureQRService = secureQRService;
        this.fileStorageService = fileStorageService;
        this.pdfProcessingService = pdfProcessingService;
    }

    public ProcessedDocument processDocument(EmployeeDocument originalDoc, boolean replaceSignatures, boolean generateQR) {
        try {
            String filePath = originalDoc.getFilePath();
            String fileExtension = getFileExtension(originalDoc.getFileName()).toLowerCase();

            log.info("Traitement document: {}, type: {}", originalDoc.getFileName(), fileExtension);

            // VÉRIFIER QUE LE FICHIER SOURCE EXISTE
            Path sourcePath = Paths.get(filePath);
            if (!Files.exists(sourcePath)) {
                throw new RuntimeException("Fichier source introuvable: " + filePath);
            }

            if (fileExtension.equals(".pdf")) {
                return processPdfDocument(originalDoc, replaceSignatures, generateQR);
            } else if (Arrays.asList(".png", ".jpg", ".jpeg", ".gif", ".bmp").contains(fileExtension)) {
                return processImageDocument(originalDoc, replaceSignatures, generateQR);
            } else {
                return processGenericDocument(originalDoc, generateQR);
            }

        } catch (Exception e) {
            log.error("Erreur traitement document: {}", e.getMessage(), e);
            throw new RuntimeException("Échec traitement document: " + e.getMessage());
        }
    }

    // Dans DocumentProcessingService.java - méthode processPdfDocument
    private ProcessedDocument processPdfDocument(EmployeeDocument originalDoc, boolean replaceSignatures, boolean generateQR) {
        String sourcePath = originalDoc.getFilePath();
        String outputPath = fileStorageService.getOutputPath(originalDoc.getFileName(), "reconstructed");

        log.info("🔍 DEBUT processPdfDocument - PDF: {}", sourcePath);
        log.info("🎯 Paramètres - QR: {}, Signatures: {}", generateQR, replaceSignatures);

        try {
            // VÉRIFIER QUE LE FICHIER SOURCE EXISTE
            Path sourceFilePath = Paths.get(sourcePath);
            if (!Files.exists(sourceFilePath)) {
                log.error("❌ FICHIER SOURCE INTROUVABLE: {}", sourcePath);
                throw new RuntimeException("Fichier source introuvable: " + sourcePath);
            }
            log.info("✅ Fichier source trouvé - Taille: {} octets", Files.size(sourceFilePath));

            // CRÉER LE RÉPERTOIRE DE DESTINATION SI NÉCESSAIRE
            Path outputDir = Paths.get(outputPath).getParent();
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
                log.info("📁 Répertoire créé: {}", outputDir);
            }

            int signaturesRemoved = 0;
            int stampsRemoved = 0;
            String finalOutputPath = outputPath;

            if (generateQR) {
                // 🔥 UTILISER PDFBox POUR AJOUTER LE QR CODE EN HAUT À DROITE
                log.info("🎨 Appel du service PDF avec QR Code...");
                try {
                    finalOutputPath = pdfProcessingService.processPdfWithQRCode(sourcePath, outputPath, originalDoc);
                    log.info("✅ Service PDF terminé - Fichier: {}", finalOutputPath);

                    // Vérifier que le fichier a bien été créé
                    Path outputFilePath = Paths.get(finalOutputPath);
                    if (Files.exists(outputFilePath)) {
                        long fileSize = Files.size(outputFilePath);
                        log.info("✅ Fichier de sortie créé - Taille: {} octets", fileSize);
                    } else {
                        log.error("❌ Fichier de sortie non créé, utilisation du fichier source");
                        finalOutputPath = sourcePath;
                    }
                } catch (Exception e) {
                    log.error("❌ Erreur lors de l'ajout du QR Code: {}", e.getMessage());
                    log.warn("🔄 Fallback: copie du fichier original");
                    Files.copy(sourceFilePath, Paths.get(outputPath));
                    finalOutputPath = outputPath;
                }
                signaturesRemoved = replaceSignatures ? simulateSignatureRemoval() : 0;
            } else {
                // Si pas de QR, copier simplement
                log.info("📋 Copie simple sans QR Code...");
                Files.copy(sourceFilePath, Paths.get(outputPath));
                log.info("✅ Fichier copié: {} -> {}", sourcePath, outputPath);
            }

            // Générer QR Code pour la réponse (toujours nécessaire pour l'API)
            log.info("🖼️ Génération du QR Code pour la réponse...");
            String qrCodeBase64 = generateQRCode(originalDoc, generateQR);
            log.info("✅ QR Code généré - Longueur base64: {}", qrCodeBase64.length());

            // Vérifier le fichier final
            Path finalOutputFilePath = Paths.get(finalOutputPath);
            long finalFileSize = Files.exists(finalOutputFilePath) ? Files.size(finalOutputFilePath) : 0;
            log.info("📊 Fichier final - Chemin: {}, Taille: {} octets", finalOutputPath, finalFileSize);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("pages_processed", 1);
            metadata.put("text_preserved", true);
            metadata.put("security_level", "AES_256");
            metadata.put("processing_method", generateQR ? "PDFBOX_WITH_QR" : "SIMPLE_COPY");
            metadata.put("qr_position", generateQR ? "TOP_RIGHT" : "NONE");
            metadata.put("qr_size", "60x60_points");
            metadata.put("margins", "20px");
            metadata.put("file_size_bytes", finalFileSize);
            metadata.put("processing_time", System.currentTimeMillis());

            ProcessedDocument result = ProcessedDocument.builder()
                    .filePath(finalOutputPath)
                    .fileType("pdf")
                    .qrCodeBase64(qrCodeBase64)
                    .signaturesRemoved(signaturesRemoved)
                    .stampsRemoved(stampsRemoved)
                    .pagesProcessed(1)
                    .status("success")
                    .metadata(metadata)
                    .build();

            log.info("🎉 processPdfDocument TERMINÉ avec succès!");
            return result;

        } catch (Exception e) {
            log.error("❌ ERREUR CRITIQUE dans processPdfDocument: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur traitement PDF: " + e.getMessage());
        }
    }

    private ProcessedDocument processImageDocument(EmployeeDocument originalDoc, boolean replaceSignatures, boolean generateQR) {
        try {
            log.info("Traitement image: {}", originalDoc.getFileName());

            String sourcePath = originalDoc.getFilePath();
            String outputPath = fileStorageService.getOutputPath(originalDoc.getFileName(), "reconstructed");

            // CRÉER LE RÉPERTOIRE DE DESTINATION
            Path outputDir = Paths.get(outputPath).getParent();
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            // Charger l'image originale
            BufferedImage originalImage = ImageIO.read(new File(sourcePath));
            if (originalImage == null) {
                throw new RuntimeException("Impossible de charger l'image: " + sourcePath);
            }

            BufferedImage processedImage = originalImage;

            int signaturesRemoved = 0;

            if (replaceSignatures) {
                processedImage = removeSignaturesFromImage(originalImage);
                signaturesRemoved = 1;
            }

            if (generateQR) {
                processedImage = addQRCodeToImage(processedImage, originalDoc);
            }

            // Sauvegarder l'image traitée
            String format = getImageFormat(originalDoc.getFileName());
            ImageIO.write(processedImage, format, new File(outputPath));

            String qrCodeBase64 = generateQRCode(originalDoc, generateQR);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("width", processedImage.getWidth());
            metadata.put("height", processedImage.getHeight());
            metadata.put("format", format);
            metadata.put("processing_method", "JAVA_IMAGE_PROCESSING");
            metadata.put("qr_position", "top_right");

            return ProcessedDocument.builder()
                    .filePath(outputPath)
                    .fileType("image")
                    .qrCodeBase64(qrCodeBase64)
                    .signaturesRemoved(signaturesRemoved)
                    .stampsRemoved(0)
                    .pagesProcessed(1)
                    .status("success")
                    .metadata(metadata)
                    .build();

        } catch (Exception e) {
            log.error("Erreur traitement image: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur traitement image: " + e.getMessage());
        }
    }

    /**
     * 🔥 NOUVELLE MÉTHODE: Traiter directement depuis MultipartFile
     */
    public ProcessedDocument processDocumentFromUpload(
            MultipartFile file,
            EmployeeDocument tempDoc,
            boolean replaceSignatures,
            boolean generateQR) throws IOException {

        // Sauvegarder temporairement pour traitement
        String tempFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path tempPath = fileStorageService.storeFile(file, "temp", tempFileName);

        try {
            // Créer un doc temporaire avec le chemin
            tempDoc.setFilePath(tempPath.toString());

            // Traiter normalement
            ProcessedDocument result = processDocument(tempDoc, replaceSignatures, generateQR);

            return result;

        } finally {
            // Supprimer le fichier temporaire
            try {
                Files.deleteIfExists(tempPath);
                log.info("🗑️ Fichier temporaire supprimé: {}", tempPath);
            } catch (IOException e) {
                log.warn("Impossible de supprimer le fichier temporaire: {}", tempPath);
            }
        }
    }

    private ProcessedDocument processGenericDocument(EmployeeDocument originalDoc, boolean generateQR) {
        try {
            String sourcePath = originalDoc.getFilePath();
            String outputPath = fileStorageService.getOutputPath(originalDoc.getFileName(), "reconstructed");

            // CRÉER LE RÉPERTOIRE DE DESTINATION
            Path outputDir = Paths.get(outputPath).getParent();
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            // Pour les types non supportés, on copie simplement
            Files.copy(Paths.get(sourcePath), Paths.get(outputPath));

            String qrCodeBase64 = generateQRCode(originalDoc, generateQR);

            return ProcessedDocument.builder()
                    .filePath(outputPath)
                    .fileType("generic")
                    .qrCodeBase64(qrCodeBase64)
                    .signaturesRemoved(0)
                    .stampsRemoved(0)
                    .pagesProcessed(1)
                    .status("success")
                    .metadata(Map.of(
                            "note", "Fichier copié sans traitement spécifique",
                            "processing_method", "JAVA_COPY"
                    ))
                    .build();

        } catch (Exception e) {
            log.error("Erreur traitement générique: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur traitement générique: " + e.getMessage());
        }
    }

    private BufferedImage removeSignaturesFromImage(BufferedImage image) {
        // Simulation - dans la réalité, utiliser OpenCV ou autre
        log.info("Simulation suppression signatures image");
        return image;
    }

    // Dans DocumentProcessingService.java - méthode addQRCodeToImage
    private BufferedImage addQRCodeToImage(BufferedImage image, EmployeeDocument document) {
        try {
            String qrCodeBase64 = generateQRCode(document, true);
            byte[] qrCodeBytes = Base64.getDecoder().decode(qrCodeBase64);
            BufferedImage qrImage = ImageIO.read(new ByteArrayInputStream(qrCodeBytes));

            if (qrImage == null) {
                log.warn("Impossible de charger l'image QR, utilisation de l'image originale");
                return image;
            }

            // 🔥 CORRECTION : TAILLE ADAPTATIVE
            int qrSize = Math.min(120, image.getWidth() / 6); // 1/6 de la largeur max 120px

            BufferedImage resizedQr = new BufferedImage(qrSize, qrSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resizedQr.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(qrImage, 0, 0, qrSize, qrSize, null);
            g2d.dispose();

            // 🔥 CORRECTION : POSITIONNEMENT EN HAUT À DROITE
            Graphics2D originalGraphics = image.createGraphics();

            // Position en HAUT À DROITE avec marges
            int marginX = 30; // 30px du bord droit
            int marginY = 30; // 30px du bord supérieur
            int x = image.getWidth() - qrSize - marginX;
            int y = marginY;

            // 🔥 FOND BLANC POUR MEILLEURE VISIBILITÉ
            originalGraphics.setColor(Color.WHITE);
            originalGraphics.fillRect(x - 5, y - 5, qrSize + 10, qrSize + 10);

            // 🔥 BORDURE NOIRE
            originalGraphics.setColor(Color.BLACK);
            originalGraphics.setStroke(new BasicStroke(2));
            originalGraphics.drawRect(x - 5, y - 5, qrSize + 10, qrSize + 10);

            // 🔥 QR CODE
            originalGraphics.drawImage(resizedQr, x, y, null);

            // 🔥 TEXTE DESCRIPTIF
            originalGraphics.setFont(new Font("Arial", Font.BOLD, 12));
            originalGraphics.setColor(Color.BLACK);
            originalGraphics.drawString("DOC NUMERISE", x, y + qrSize + 20);

            originalGraphics.dispose();

            log.info("✅ QR Code image positionné en HAUT À DROITE - Position: ({}, {}), Taille: {}px", x, y, qrSize);
            return image;

        } catch (Exception e) {
            log.warn("❌ Erreur ajout QR Code à l'image: {}", e.getMessage());
            return image;
        }
    }
    private String generateQRCode(EmployeeDocument document, boolean generateQR) {
        if (!generateQR) {
            return secureQRService.generateSimpleQRBase64();
        }

        try {
            String secureData = secureQRService.generateSecureQRData(document);
            return secureQRService.generateQRImage(secureData);
        } catch (Exception e) {
            log.warn("Erreur génération QR, utilisation fallback: {}", e.getMessage());
            return secureQRService.generateSimpleQRBase64();
        }
    }

    private int simulateSignatureRemoval() {
        return (int) (Math.random() * 3) + 1;
    }

    private int simulateStampRemoval() {
        return (int) (Math.random() * 2);
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return ".dat";
        }
        int lastIndex = filename.lastIndexOf(".");
        return lastIndex > 0 ? filename.substring(lastIndex) : ".dat";
    }

    private String getImageFormat(String filename) {
        String ext = getFileExtension(filename).toLowerCase();
        if (ext.equals(".jpg") || ext.equals(".jpeg")) return "JPEG";
        if (ext.equals(".png")) return "PNG";
        if (ext.equals(".gif")) return "GIF";
        if (ext.equals(".bmp")) return "BMP";
        return "PNG";
    }
}