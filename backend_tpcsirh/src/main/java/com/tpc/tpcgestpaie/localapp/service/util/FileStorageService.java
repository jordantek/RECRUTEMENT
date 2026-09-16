package com.tpc.tpcgestpaie.localapp.service.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class    FileStorageService {

    private Path fileStorageLocation;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // Méthode d'initialisation après injection des propriétés
    @jakarta.annotation.PostConstruct
    public void init() {
        try {
            // Créer le répertoire principal
            this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(this.fileStorageLocation);

            // Créer le sous-répertoire pour les logos
            Path logoStorageLocation = Paths.get(uploadDir, "logos").toAbsolutePath().normalize();
            Files.createDirectories(logoStorageLocation);



        } catch (IOException ex) {
            throw new RuntimeException("Impossible de créer le répertoire de stockage: " + uploadDir, ex);
        }
    }

    public String storeLogo(MultipartFile logoFile) {
        try {
            if (logoFile == null || logoFile.isEmpty()) {
                return null;
            }
            // Validation du type de fichier
            String contentType = logoFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Le fichier doit être une image");
            }

            // Validation de la taille (5MB max)
            if (logoFile.getSize() > 5 * 1024 * 1024) {
                throw new RuntimeException("La taille du logo ne doit pas dépasser 5MB");
            }

            // Créer le répertoire s'il n'existe pas
            String filename = System.currentTimeMillis() + "_" + StringUtils.cleanPath(logoFile.getOriginalFilename());
            Path path = Paths.get(uploadDir + "/logos/" + filename);
            Files.createDirectories(path.getParent());

            // Sauvegarder le fichier
            Files.copy(logoFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // Retourner le chemin relatif
            return "/uploads/logos/" + filename;

        } catch (IOException ex) {
            throw new RuntimeException("Erreur lors du stockage du logo: " + ex.getMessage(), ex);
        }
    }

    public Resource loadLogo(String logoPath) {
        try {
            // Extraire le nom de fichier du chemin relatif
            String filename = logoPath.replace("/uploads/logos/", "");
            Path filePath = Paths.get(uploadDir + "/logos/" + filename).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Logo non trouvé: " + logoPath);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Logo non trouvé: " + logoPath, ex);
        }
    }

    public void deleteLogo(String logoPath) {
        try {
            if (logoPath != null && !logoPath.trim().isEmpty()) {
                String filename = logoPath.replace("/uploads/logos/", "");
                Path filePath = Paths.get(uploadDir + "/logos/" + filename).toAbsolutePath().normalize();
                Files.deleteIfExists(filePath);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Erreur lors de la suppression du logo: " + logoPath, ex);
        }
    }

    /**
     * Stocke un fichier uploadé
     */
    public String storeFile(MultipartFile file, String employeeMatricule) {
        try {
            // Validation du fichier
            if (file.isEmpty()) {
                throw new RuntimeException("Le fichier est vide");
            }

            // Nettoyage du nom de fichier
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

            if (originalFileName.contains("..")) {
                throw new RuntimeException("Nom de fichier invalide: " + originalFileName);
            }

            // Génération d'un nom de fichier unique
            String fileExtension = getFileExtension(originalFileName);
            String uniqueFileName = generateUniqueFileName(employeeMatricule, fileExtension);

            // Création du sous-répertoire pour l'employé
            Path employeeDirectory = this.fileStorageLocation.resolve(employeeMatricule);
            Files.createDirectories(employeeDirectory);

            // Chemin complet du fichier
            Path targetLocation = employeeDirectory.resolve(uniqueFileName);

            // Copie du fichier
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }


            return employeeMatricule + "/" + uniqueFileName;

        } catch (IOException ex) {
            throw new RuntimeException("Erreur lors du stockage du fichier: " + file.getOriginalFilename(), ex);
        }
    }

    /**
     * Stocke un fichier à partir de données binaires (pour les documents reconstruits)
     */
    public String storeFile(byte[] fileData, String fileName, String employeeMatricule) {
        try {
            // Création du sous-répertoire pour l'employé
            Path employeeDirectory = this.fileStorageLocation.resolve(employeeMatricule);
            Files.createDirectories(employeeDirectory);

            // Chemin complet du fichier
            Path targetLocation = employeeDirectory.resolve(fileName);

            // Écriture des données
            Files.write(targetLocation, fileData);


            return employeeMatricule + "/" + fileName;

        } catch (IOException ex) {
            throw new RuntimeException("Erreur lors du stockage du fichier: " + fileName, ex);
        }
    }

    /**
     * Charge un fichier comme Resource
     */
    public Resource loadFileAsResource(String filePath) {
        try {
            Path file = this.fileStorageLocation.resolve(filePath).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Fichier non trouvé: " + filePath);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Fichier non trouvé: " + filePath, ex);
        }
    }

    /**
     * Supprime un fichier
     */
    public boolean deleteFile(String filePath) {
        try {
            Path file = this.fileStorageLocation.resolve(filePath).normalize();
            boolean deleted = Files.deleteIfExists(file);
            if (deleted) {

            }
            return deleted;
        } catch (IOException ex) {

            return false;
        }
    }

    /**
     * Vérifie si un fichier existe
     */
    public boolean fileExists(String filePath) {
        try {
            Path file = this.fileStorageLocation.resolve(filePath).normalize();
            return Files.exists(file) && Files.isRegularFile(file);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Obtient la taille d'un fichier
     */
    public long getFileSize(String filePath) {
        try {
            Path file = this.fileStorageLocation.resolve(filePath).normalize();
            return Files.size(file);
        } catch (IOException ex) {
            throw new RuntimeException("Erreur lors de la lecture de la taille du fichier: " + filePath, ex);
        }
    }

    /**
     * Génère un nom de fichier unique
     */
    private String generateUniqueFileName(String employeeMatricule, String fileExtension) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomId = UUID.randomUUID().toString().substring(0, 8);
        return String.format("%s_%s_%s%s",
                employeeMatricule, timestamp, randomId, fileExtension);
    }

    /**
     * Extrait l'extension d'un fichier
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex);
        }
        return ".dat"; // Extension par défaut
    }

    /**
     * Obtient l'emplacement de stockage
     */
    public Path getStorageLocation() {
        return fileStorageLocation;
    }
}