package com.tpc.tpcgestpaie.localapp.config;

// FileStorageConfig.java

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class FileStorageConfig {

    @Value("${app.storage.upload-dir:./storage/uploads}")
    private String uploadDir;

    @Value("${app.storage.processed-dir:./storage/processed}")
    private String processedDir;

    @Value("${app.upload.dir:uploads/}")
    private String legacyUploadDir;

    @PostConstruct
    public void init() {
        try {
            // Créer les nouveaux répertoires
            Path uploadPath = Paths.get(uploadDir);
            Path processedPath = Paths.get(processedDir);
            Path legacyPath = Paths.get(legacyUploadDir);

            Files.createDirectories(uploadPath);
            Files.createDirectories(processedPath);
            Files.createDirectories(legacyPath);

        } catch (IOException e) {

        }
    }
}