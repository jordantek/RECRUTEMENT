// DiagnosticService.java - NOUVEAU SERVICE
package com.tpc.tpcgestpaie.localapp.service.numerisation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class DiagnosticService {

    @Value("${app.storage.base-dir:./storage}")
    private String baseStorageDir;

    public String checkStorageHealth() {
        StringBuilder report = new StringBuilder();
        report.append("=== DIAGNOSTIC STOCKAGE ===\n");

        try {
            // Vérifier le répertoire base
            Path basePath = Paths.get(baseStorageDir);
            report.append("Répertoire base: ").append(basePath.toAbsolutePath()).append("\n");
            report.append("Existe: ").append(Files.exists(basePath)).append("\n");
            report.append("Est un répertoire: ").append(Files.isDirectory(basePath)).append("\n");
            report.append("Peut écrire: ").append(Files.isWritable(basePath)).append("\n\n");

            // Vérifier les sous-répertoires
            String[] subdirs = {"originals", "reconstructed", "temp"};
            for (String subdir : subdirs) {
                Path subdirPath = basePath.resolve(subdir);
                report.append("Répertoire ").append(subdir).append(":\n");
                report.append("  Existe: ").append(Files.exists(subdirPath)).append("\n");

                if (Files.exists(subdirPath)) {
                    long fileCount = Files.list(subdirPath).count();
                    report.append("  Fichiers: ").append(fileCount).append("\n");
                } else {
                    // Essayer de créer le répertoire
                    try {
                        Files.createDirectories(subdirPath);
                        report.append("  CRÉÉ AVEC SUCCÈS\n");
                    } catch (Exception e) {
                        report.append("  ERREUR CRÉATION: ").append(e.getMessage()).append("\n");
                    }
                }
                report.append("\n");
            }

        } catch (Exception e) {
            report.append("ERREUR DIAGNOSTIC: ").append(e.getMessage()).append("\n");
        }

        return report.toString();
    }

    public void initializeStorage() throws IOException {
        log.info("Initialisation du stockage...");

        Path basePath = Paths.get(baseStorageDir);
        if (!Files.exists(basePath)) {
            Files.createDirectories(basePath);
            log.info("Répertoire base créé: {}", basePath);
        }

        // Créer tous les sous-répertoires nécessaires
        String[] subdirs = {"originals", "reconstructed", "temp", "backup"};
        for (String subdir : subdirs) {
            Path subdirPath = basePath.resolve(subdir);
            if (!Files.exists(subdirPath)) {
                Files.createDirectories(subdirPath);
                log.info("Répertoire créé: {}", subdirPath);
            }
        }

        log.info("Stockage initialisé avec succès");
    }
}