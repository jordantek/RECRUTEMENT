
        package com.tpc.tpcgestpaie.localapp.service.numerisation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;

@Service
@Slf4j
public class NumerisationFileStorageService {


    @Value("${app.storage.base-dir:./storage}")
    private String baseStorageDir;

    public Path storeFile(MultipartFile file, String subdirectory, String filename) throws IOException {
        Path storageDir = Paths.get(baseStorageDir, subdirectory);

        // CRÉER RÉPERTOIRE SI N'EXISTE PAS
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
            log.info("Répertoire créé: {}", storageDir);
        }

        Path filePath = storageDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        log.info("Fichier stocké: {} ({} bytes)", filePath, Files.size(filePath));
        return filePath;
    }

    public String getOutputPath(String originalFileName, String subdirectory) {
        // NETTOYER LE NOM DE FICHIER
        String cleanName = cleanFileName(originalFileName);
        String extension = getFileExtension(cleanName);
        String baseName = cleanName.substring(0, cleanName.lastIndexOf('.'));

        String outputFilename = baseName + "_RECONSTRUCTED" + extension;
        return Paths.get(baseStorageDir, subdirectory, outputFilename).toString();
    }



    private String cleanFileName(String filename) {
        if (filename == null) return "document";

        // Supprimer les caractères problématiques
        return filename
                .replace(" ", "_")
                .replace("/", "_")
                .replace("\\", "_")
                .replace(":", "_")
                .replace("*", "_")
                .replace("?", "_")
                .replace("\"", "_")
                .replace("<", "_")
                .replace(">", "_")
                .replace("|", "_");
    }

    public String calculateFileHash(MultipartFile file) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(file.getBytes());
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new IOException("Erreur calcul hash fichier", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return ".dat";
        }
        int lastIndex = filename.lastIndexOf(".");
        return lastIndex > 0 ? filename.substring(lastIndex) : ".dat";
    }

    // NOUVELLE MÉTHODE: Vérifier l'existence d'un fichier
    public boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    // NOUVELLE MÉTHODE: Créer un répertoire
    public void createDirectory(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("Répertoire créé: {}", path);
        }
    }
}