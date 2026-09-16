package com.tpc.tpcgestpaie.localapp.service.numerisation;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class StorageCleanupService {

    @Value("${app.storage.processed-dir:./storage/processed}")
    private String processedDir;

    @Scheduled(cron = "0 0 2 * * ?") // Tous les jours à 2h du matin
    public void cleanupTempFiles() {
        try {
            Path tempDir = Paths.get(processedDir, "temp");
            if (Files.exists(tempDir)) {
                Files.walk(tempDir)
                        .filter(path -> !path.equals(tempDir))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                                log.info("Fichier temporaire nettoyé: {}", path);
                            } catch (IOException e) {
                                log.warn("Impossible de supprimer le fichier temporaire: {}", path);
                            }
                        });
            }
        } catch (Exception e) {
            log.error("Erreur lors du nettoyage des fichiers temporaires: {}", e.getMessage());
        }
    }
}