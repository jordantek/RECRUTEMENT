package com.tpc.tpcgestpaie.localapp.service.numerisation.security;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

import org.springframework.stereotype.Service;
import org.apache.pdfbox.util.Matrix;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
@Service
public class PdfQrStampService {

    public File stampQrTopRight(File originalPdf, String qrText) {
        try (PDDocument document = Loader.loadPDF(originalPdf)) {

            BufferedImage qrImage = generateQrImage(qrText);
            PDImageXObject qr = LosslessFactory.createFromImage(document, qrImage);

            float qrSize = 70; // taille en points PDF
            float margin = 10;

            for (PDPage page : document.getPages()) {
                PDRectangle mediaBox = page.getMediaBox();

                // Position en haut à droite
                float x = mediaBox.getUpperRightX() - qrSize - margin;
                float y = mediaBox.getUpperRightY() - qrSize - margin;

                try (PDPageContentStream cs = new PDPageContentStream(
                        document,
                        page,
                        PDPageContentStream.AppendMode.APPEND,
                        true,
                        true)) {

                    // Dessin simple sans transformation complexe
                    cs.drawImage(qr, x, y, qrSize, qrSize);
                }
            }

            // Créer le fichier de sortie
            File output = new File(
                    originalPdf.getParent(),
                    "QR_" + originalPdf.getName()
            );

            document.save(output);
            System.out.println("✅ QR ajouté : " + output.getAbsolutePath());

            return output;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'ajout du QR code", e);
        }
    }

    private BufferedImage generateQrImage(String text) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 300, 300);

        BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_ARGB);

        // Fond transparent
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(java.awt.Color.WHITE);
        graphics.fillRect(0, 0, 300, 300);
        graphics.setColor(java.awt.Color.BLACK);

        for (int x = 0; x < 300; x++) {
            for (int y = 0; y < 300; y++) {
                if (matrix.get(x, y)) {
                    image.setRGB(x, y, java.awt.Color.BLACK.getRGB());
                } else {
                    image.setRGB(x, y, java.awt.Color.WHITE.getRGB());
                }
            }
        }
        graphics.dispose();

        return image;
    }
}