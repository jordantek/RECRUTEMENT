
package com.tpc.tpcgestpaie.localapp.service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class StockageService {

    private final Path rootLocation = Paths.get("uploads"); // dossier de stockage

    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Créer le dossier s'il n'existe pas
        if (!Files.exists(rootLocation)) {
            Files.createDirectories(rootLocation);
        }

        // Générer un nom unique pour éviter les collisions
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path destinationFile = rootLocation.resolve(filename).normalize().toAbsolutePath();

        // Copier le fichier sur disque
        Files.copy(file.getInputStream(), destinationFile);

        // Retourner le chemin relatif ou juste le nom du fichier pour stockage en base
        return filename;
    }
}
