package com.tpc.tpcgestpaie.localapp.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FileManager {

    private String baseDirectory;

    // =============================
    // Constructeur
    // =============================
    public FileManager(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    // =============================
    // Génération nom fichier
    // =============================
    public static String generateUniqueFileName(String originalFileName) {
        String ext = getExtension(originalFileName);
        return UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);
    }

    public static String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static String addTimestampToFileName(String originalFileName) {
        String ext = getExtension(originalFileName);
        String baseName = originalFileName.replace("." + ext, "");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return baseName + "_" + timestamp + (ext.isEmpty() ? "" : "." + ext);
    }

    // =============================
    // Création de dossier
    // =============================
    public Path createDirectory(String relativePath) throws IOException {
        Path path = Paths.get(baseDirectory, relativePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return path;
    }

    // =============================
// Sauvegarde fichier
// =============================
    public Path saveFile(MultipartFile file, String relativeDir, boolean keepOriginalName, boolean overwrite) throws IOException {
        // Construire le chemin complet
        Path dir = Paths.get(baseDirectory, relativeDir);

        // Vérifier si le dossier existe, sinon le créer
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        // Déterminer le nom du fichier
        String fileName = keepOriginalName ? file.getOriginalFilename() : generateUniqueFileName(file.getOriginalFilename());
        Path target = dir.resolve(fileName);

        // Ajouter un timestamp si overwrite = false et le fichier existe
        if (!overwrite && Files.exists(target)) {
            target = dir.resolve(addTimestampToFileName(file.getOriginalFilename()));
        }

        // Copier le fichier
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return target;
    }

    // =============================
// Sauvegarde fichier (nom unique)
// =============================
    public Path saveFile(MultipartFile file, String relativeDir) throws IOException {

        // Construire le dossier
        Path dir = Paths.get(baseDirectory, relativeDir);

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        // Générer un nom unique
        String originalName = file.getOriginalFilename();
        String uniqueName = generateUniqueFileName(originalName);

        Path target = dir.resolve(uniqueName);

        // Copier le fichier
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return target;
    }
    // =============================
    // Lire un fichier
    // =============================
    public byte[] readFile(String relativePath) throws IOException {
        Path path = Paths.get(baseDirectory, relativePath);
        return Files.readAllBytes(path);
    }

    // =============================
    // Supprimer un fichier
    // =============================
    public boolean deleteFile(String relativePath) throws IOException {
        Path path = Paths.get(baseDirectory, relativePath);
        return Files.deleteIfExists(path);
    }

    // =============================
    // Déplacer un fichier
    // =============================
    public Path moveFile(String sourceRelative, String targetRelative, boolean overwrite) throws IOException {
        Path source = Paths.get(baseDirectory, sourceRelative);
        Path target = Paths.get(baseDirectory, targetRelative);
        createDirectory(target.getParent().toString());

        if (!overwrite && Files.exists(target)) {
            target = Paths.get(target.getParent().toString(), addTimestampToFileName(target.getFileName().toString()));
        }

        return Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    // =============================
    // Copier un fichier
    // =============================
    public Path copyFile(String sourceRelative, String targetRelative, boolean overwrite) throws IOException {
        Path source = Paths.get(baseDirectory, sourceRelative);
        Path target = Paths.get(baseDirectory, targetRelative);
        createDirectory(target.getParent().toString());

        if (!overwrite && Files.exists(target)) {
            target = Paths.get(target.getParent().toString(), addTimestampToFileName(target.getFileName().toString()));
        }

        return Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }

    // =============================
    // Vérifier si fichier existe
    // =============================
    public boolean fileExists(String relativePath) {
        Path path = Paths.get(baseDirectory, relativePath);
        return Files.exists(path);
    }

    // =============================
    // Taille du fichier
    // =============================
    public long getFileSize(String relativePath) throws IOException {
        Path path = Paths.get(baseDirectory, relativePath);
        return Files.size(path);
    }

    // =============================
    // Génération dynamique des chemins
    // =============================
    public String buildPathForCompany(Long companyId) {
        return "company_" + companyId;
    }

    public String buildPathForEmployee(Long companyId, String employeeCode) {
        return buildPathForCompany(companyId) + "/employees/" + employeeCode;
    }

    public String buildPathForCategory(Long companyId, String categoryCode) {
        return buildPathForCompany(companyId) + "/categories/" + categoryCode;
    }

    // =============================
    // Validation de type de fichier
    // =============================
    public static boolean validateFileExtension(String fileName, String[] allowedExtensions) {
        String ext = getExtension(fileName).toLowerCase();
        for (String allowed : allowedExtensions) {
            if (ext.equals(allowed.toLowerCase())) return true;
        }
        return false;
    }

}