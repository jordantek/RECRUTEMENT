package com.tpc.tpcgestpaie.localapp.service.conge;

import com.tpc.tpcgestpaie.localapp.dto.conge.initialisation.ImportSoldeBatchDTO;
import com.tpc.tpcgestpaie.localapp.dto.conge.initialisation.LigneImportSoldeDTO;
import com.tpc.tpcgestpaie.localapp.dto.conge.initialisation.SaisieSoldeInitialDTO;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.conge.SoldeCongeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportSoldeExcelService {

    private final EmployeRepository employeRepository;
    private final InitialisationSoldeService initialisationService;
    private final SoldeCongeRepository soldeCongeRepository;

    // ⭐ NOMS DES COLONNES ATTENDUES (insensible à la casse)
    private static final String[] HEADERS = {
            "matricule", "dateDebutContrat", "dateReference",
            "joursDejaPris", "joursRestant", "montantRestant", "commentaire"
    };


    /**
     * ⭐ PAS DE @Transactional GLOBALE - Chaque ligne gère sa propre transaction
     */
    public ImportSoldeBatchDTO importerExcel(MultipartFile fichier) {
        log.info("📥 Début import Excel: {}", fichier.getOriginalFilename());

        List<LigneImportSoldeDTO> resultats = new ArrayList<>();
        int succes = 0, erreurs = 0, dejaInitialises = 0;

        try (InputStream is = fichier.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            if (headerRow == null) {
                throw new RuntimeException("Fichier vide - pas d'en-tête");
            }

            int[] columnIndexes = mapColumns(headerRow);

            if (columnIndexes[0] == -1) {
                throw new RuntimeException("Colonne 'matricule' non trouvée");
            }

            // Traiter les lignes INDÉPENDAMMENT (pas de transaction globale)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row)) continue;

                // ⭐ Chaque ligne dans sa propre transaction
                LigneImportSoldeDTO ligne = traiterLigneAvecTransaction(row, columnIndexes, i + 1);
                resultats.add(ligne);

                switch (ligne.getStatut()) {
                    case "SUCCESS" -> succes++;
                    case "ERROR" -> erreurs++;
                    case "DEJA_INITIALISE" -> dejaInitialises++;
                }
            }

        } catch (Exception e) {
            log.error("Erreur import Excel: {}", e.getMessage());
            throw new RuntimeException("Fichier Excel invalide: " + e.getMessage());
        }

        return ImportSoldeBatchDTO.builder()
                .totalLignes(resultats.size())
                .succes(succes)
                .erreurs(erreurs)
                .warnings(dejaInitialises)
                .details(resultats)
                .rapportGlobal(buildRapport(succes, erreurs, dejaInitialises))
                .build();
    }


    private String buildRapport(int succes, int erreurs, int dejaInitialises) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Import terminé: %d succès, %d erreurs, %d déjà initialisés",
                succes, erreurs, dejaInitialises));

        if (erreurs == 0 && dejaInitialises == 0) {
            sb.append(". ✅ Tous les soldes ont été initialisés.");
        } else if (erreurs == 0) {
            sb.append(". ℹ️ Les déjà initialisés ont été ignorés.");
        } else {
            sb.append(". ⚠️ Vérifiez les erreurs.");
        }

        return sb.toString();
    }
    /**
     * ⭐ NOUVELLE MÉTHODE : Transaction par ligne (REQUIRES_NEW)
     * Si ça échoue, ça n'affecte pas les autres lignes
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected LigneImportSoldeDTO traiterLigneAvecTransaction(Row row, int[] columnIndexes, int rowNum) {
        return traiterLigneExcel(row, columnIndexes, rowNum);
    }

    private LigneImportSoldeDTO traiterLigneExcel(Row row, int[] columnIndexes, int rowNum) {
        LigneImportSoldeDTO.LigneImportSoldeDTOBuilder result = LigneImportSoldeDTO.builder();

        String matricule = getCellStringValue(row.getCell(columnIndexes[0]));
        result.matricule(matricule);

        if (matricule == null || matricule.trim().isEmpty()) {
            return result
                    .statut("ERROR")
                    .messageErreur("Ligne " + rowNum + ": Matricule vide")
                    .build();
        }

        try {
            Employe employe = employeRepository.findByMatricule(matricule.trim())
                    .orElseThrow(() -> new RuntimeException("Matricule inconnu: " + matricule));

            // ⭐ VÉRIFICATION : Déjà initialisé ?
            if (soldeCongeRepository.existsByEmployeId(employe.getId())) {
                return result
                        .employeId(employe.getId())
                        .nomEmploye(employe.getNom() + " " + employe.getPrenom())
                        .statut("DEJA_INITIALISE")
                        .messageErreur("Employé déjà initialisé - Ignoré")
                        .build();
            }

            // ⭐ VÉRIFICATION : Provision existe déjà ?
            if (initialisationService.estDejaInitialise(employe.getId())) {
                return result
                        .employeId(employe.getId())
                        .nomEmploye(employe.getNom() + " " + employe.getPrenom())
                        .statut("DEJA_INITIALISE")
                        .messageErreur("Provision déjà existante - Ignoré")
                        .build();
            }

            result.employeId(employe.getId());
            result.nomEmploye(employe.getNom() + " " + employe.getPrenom());

            LocalDate dateDebut = getCellDateValue(row.getCell(columnIndexes[1]));
            LocalDate dateRef = getCellDateValue(row.getCell(columnIndexes[2]));
            BigDecimal joursPris = getCellNumericValue(row.getCell(columnIndexes[3]));
            BigDecimal joursRestant = getCellNumericValue(row.getCell(columnIndexes[4]));
            BigDecimal montant = getCellNumericValue(row.getCell(columnIndexes[5]));
            String commentaire = columnIndexes[6] != -1 ?
                    getCellStringValue(row.getCell(columnIndexes[6])) :
                    "Import Excel - " + LocalDate.now();

            if (dateDebut == null) throw new RuntimeException("dateDebutContrat vide ou invalide");
            if (dateRef == null) throw new RuntimeException("dateReference vide ou invalide");

            SaisieSoldeInitialDTO dto = SaisieSoldeInitialDTO.builder()
                    .employeId(employe.getId())
                    .dateDebutPremierContrat(dateDebut)
                    .dateReference(dateRef)
                    .joursDejaPrisTotal(joursPris)
                    .joursRestantTotal(joursRestant)
                    .montantRestantEstime(montant)
                    .commentaire(commentaire)
                    .build();

            var initResult = initialisationService.initialiserSolde(dto);

            return result
                    .statut("SUCCESS")
                    .dateDebutContrat(dateDebut)
                    .dateReference(dateRef)
                    .joursDejaPris(joursPris)
                    .joursRestant(joursRestant)
                    .montantRestant(montant)
                    .build();

        } catch (Exception e) {
            log.error("Erreur ligne {}: {}", rowNum, e.getMessage());
            return result
                    .statut("ERROR")
                    .messageErreur("Ligne " + rowNum + ": " + e.getMessage())
                    .build();
        }
    }
    /**
     * ⭐ MAPPE LES COLONNES DU HEADER AUX INDEX
     */
    private int[] mapColumns(Row headerRow) {
        int[] indexes = new int[HEADERS.length];
        for (int i = 0; i < indexes.length; i++) indexes[i] = -1;

        for (Cell cell : headerRow) {
            String headerValue = getCellStringValue(cell).toLowerCase().trim();

            for (int i = 0; i < HEADERS.length; i++) {
                if (headerValue.equals(HEADERS[i].toLowerCase()) ||
                        headerValue.replace("_", "").equals(HEADERS[i].toLowerCase())) {
                    indexes[i] = cell.getColumnIndex();
                    log.debug("Colonne '{}' trouvée à l'index {}", HEADERS[i], indexes[i]);
                    break;
                }
            }
        }

        return indexes;
    }


    // ⭐ UTILITAIRES LECTURE CELLULES

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                }
                // Pour matricules numériques (001 → "1" mais on veut "001")
                double num = cell.getNumericCellValue();
                if (num == Math.floor(num)) {
                    yield String.format("%.0f", num);
                }
                yield String.valueOf(num);
            }
            case BLANK -> null;
            default -> cell.toString().trim();
        };
    }

    private LocalDate getCellDateValue(Cell cell) {
        if (cell == null) return null;

        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date date = cell.getDateCellValue();
                        yield date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    }
                    throw new RuntimeException("Cellule numérique non formatée en date");
                }
                case STRING -> parseDateString(cell.getStringCellValue());
                default -> null;
            };
        } catch (Exception e) {
            log.warn("Erreur parsing date: {}", e.getMessage());
            return null;
        }
    }

    private BigDecimal getCellNumericValue(Cell cell) {
        if (cell == null) return BigDecimal.ZERO;

        try {
            return switch (cell.getCellType()) {
                case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue());
                case STRING -> {
                    String val = cell.getStringCellValue()
                            .replace(",", ".")
                            .replace(" ", "")
                            .replace("€", "")
                            .trim();
                    if (val.isEmpty()) yield BigDecimal.ZERO;
                    yield new BigDecimal(val);
                }
                case BLANK -> BigDecimal.ZERO;
                default -> BigDecimal.ZERO;
            };
        } catch (Exception e) {
            log.warn("Erreur parsing nombre: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    private LocalDate parseDateString(String valeur) {
        if (valeur == null || valeur.trim().isEmpty()) return null;

        valeur = valeur.trim();

        // Format Excel français: dd/MM/yyyy
        if (valeur.contains("/")) {
            String[] parts = valeur.split("/");
            if (parts.length == 3) {
                return LocalDate.of(
                        Integer.parseInt(parts[2]),  // année
                        Integer.parseInt(parts[1]),  // mois
                        Integer.parseInt(parts[0])   // jour
                );
            }
        }

        // Format ISO: yyyy-MM-dd
        return LocalDate.parse(valeur);
    }

    private boolean isEmptyRow(Row row) {
        if (row == null) return true;
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String val = getCellStringValue(cell);
                if (val != null && !val.trim().isEmpty()) return false;
            }
        }
        return true;
    }

    private String buildRapport(int succes, int erreurs) {
        if (erreurs == 0) {
            return String.format("✅ Import Excel réussi: %d employés initialisés", succes);
        }
        return String.format("⚠️ Import Excel terminé: %d succès, %d erreurs", succes, erreurs);
    }
}