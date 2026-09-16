package com.tpc.tpcgestpaie.localapp.service.numerisation;

import com.tpc.tpcgestpaie.localapp.model.numerisation.EmployeeDocument;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Base64;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

@Service
@Slf4j
public class PdfProcessingService {

    private final SecureQRService secureQRService;

    public PdfProcessingService(SecureQRService secureQRService) {
        this.secureQRService = secureQRService;
    }

    public String processPdfWithQRCode(String sourcePdfPath, String outputPdfPath, EmployeeDocument document) {
        log.info("🚀 Traitement PDF avec QR Code: {}", sourcePdfPath);

        File sourceFile = new File(sourcePdfPath);
        if (!sourceFile.exists()) {
            throw new RuntimeException("Fichier source introuvable: " + sourcePdfPath);
        }

        try (PDDocument pdfDocument = Loader.loadPDF(sourceFile)) {

            log.info("✅ PDF chargé - {} pages", pdfDocument.getNumberOfPages());

            // Générer QR Code
            String qrData = secureQRService.generateSecureQRData(document);
            String qrCodeBase64 = secureQRService.generateQRImage(qrData);

            // Ajouter QR Code à la première page
            addQRCodeToFirstPage(pdfDocument, qrCodeBase64, document.getReference());

            // Sauvegarder
            pdfDocument.save(outputPdfPath);
            log.info("✅ PDF avec QR Code sauvegardé: {}", outputPdfPath);

            return outputPdfPath;

        } catch (Exception e) {
            log.error("❌ Erreur traitement PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur traitement PDF: " + e.getMessage());
        }
    }

    private void addQRCodeToFirstPage(PDDocument document, String qrCodeBase64, String ref) {
        try {
            PDPage page = document.getPage(0);

            // Gérer la rotation et utiliser la bonne box
            PDRectangle pageSize = getEffectivePageSize(page);
            int rotation = page.getRotation();

            log.info("📐 Page - Largeur: {}, Hauteur: {}, Rotation: {}",
                    pageSize.getWidth(), pageSize.getHeight(), rotation);

            // Convertir QR Code
            byte[] qrBytes = Base64.getDecoder().decode(qrCodeBase64);
            BufferedImage qrImage = ImageIO.read(new ByteArrayInputStream(qrBytes));

            if (qrImage == null) {
                throw new RuntimeException("Image QR invalide");
            }

            PDImageXObject pdImage = LosslessFactory.createFromImage(document, qrImage);

            // 🔥 QR COMPACT
            float qrSize = 70f;
            float margin = 15f;

            float x, y;

            // Ajuster position selon la rotation
            switch (rotation) {
                case 90:
                    x = margin;
                    y = pageSize.getHeight() - qrSize - margin;
                    break;
                case 180:
                    x = margin;
                    y = margin;
                    break;
                case 270:
                    x = pageSize.getWidth() - qrSize - margin;
                    y = margin;
                    break;
                default: // 0 ou autre
                    x = pageSize.getWidth() - qrSize - margin;
                    y = pageSize.getHeight() - qrSize - margin;
            }

            log.info("📍 Position QR: X={}, Y={}, Taille: {}", x, y, qrSize);

            try (PDPageContentStream contentStream = new PDPageContentStream(
                    document, page,
                    PDPageContentStream.AppendMode.APPEND,
                    true,
                    true
            )) {

                // QR Code (sans fond blanc)
                contentStream.drawImage(pdImage, x, y, qrSize, qrSize);

                // Référence du document
                String reference = ref != null ? ref : "REF-UNKNOWN";

                contentStream.beginText();
                contentStream.setFont(
                        new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),
                        7
                );
                contentStream.setNonStrokingColor(0f, 0f, 0f);

                // 🔥 Marge texte réduite à 8px
                PDType1Font font =
                        new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

                float textWidth =
                        font.getStringWidth(reference) / 1000f * 7;

                contentStream.newLineAtOffset(x + (qrSize - textWidth) / 2, y - 8);
                contentStream.showText(reference);
                contentStream.endText();
            }

            log.info("✅ QR Code ajouté avec succès!");

        } catch (Exception e) {
            log.error("❌ Erreur ajout QR: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur ajout QR: " + e.getMessage());
        }
    }

    /**
     * Obtenir la taille effective de la page
     */
    private PDRectangle getEffectivePageSize(PDPage page) {
        PDRectangle cropBox = page.getCropBox();
        PDRectangle mediaBox = page.getMediaBox();

        return cropBox != null ? cropBox : mediaBox;
    }
}