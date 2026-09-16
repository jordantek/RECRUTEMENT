package com.tpc.tpcgestpaie.localapp.service.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpc.tpcgestpaie.localapp.dto.bulletin.BulletinPaieGenerateDTO;
import com.tpc.tpcgestpaie.localapp.dto.export.ApercuApresExportDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.BulletinPaieDTO;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.*;

@Service
public class ApercuApresTraitementExportService {

    private List<ApercuApresExportDTO> transformToExportDTO(List<BulletinPaieDTO> bulletinList) {
        ObjectMapper mapper = new ObjectMapper();
        List<ApercuApresExportDTO> exportList = new ArrayList<>();

        for (BulletinPaieDTO bulletin : bulletinList) {
            ApercuApresExportDTO exportDTO = new ApercuApresExportDTO();

            // Informations employé
            if (bulletin.getEmploye() != null) {
                exportDTO.setEmployeNomPrenom(bulletin.getEmploye().getNom() + " " + bulletin.getEmploye().getPrenom());
            } else {
                exportDTO.setEmployeNomPrenom("N/A");
            }

            // Département
            exportDTO.setDepartement(bulletin.getDepartement() != null ? bulletin.getDepartement() : "N/A");

            // Banque
            if (bulletin.getBanque() != null) {
                exportDTO.setBanque(bulletin.getBanque().getName());
            } else {
                exportDTO.setBanque("N/A");
            }

            // Numéro de compte
            exportDTO.setNumeroCompteEmploye(bulletin.getNumeroCompteEmploye());

            // Éléments de salaire
            exportDTO.setTreiziemeMois(bulletin.getTreiziemeMois());
            exportDTO.setPrimesExceptionnelles(bulletin.getPrimesExceptionnelles());
            exportDTO.setBrutM(bulletin.getSalaireBrut()); // Brut (M)

            // Retenues
            exportDTO.setIts(bulletin.getMontantIpts()); // ITS
            exportDTO.setCnssSalarie(bulletin.getMontantCnss()); // CNSS salarié

            // Retenue légale = ITS + CNSS salarié
            BigDecimal retenueLegale = BigDecimal.ZERO;
            if (bulletin.getMontantIpts() != null) {
                retenueLegale = retenueLegale.add(bulletin.getMontantIpts());
            }
            if (bulletin.getMontantCnss() != null) {
                retenueLegale = retenueLegale.add(bulletin.getMontantCnss());
            }
            exportDTO.setRetenueLegale(retenueLegale);

            // Charges patronales
            exportDTO.setVps(bulletin.getMontantVps());
            exportDTO.setCnssEmployeur(bulletin.getMontantCnssEmployeur());
            exportDTO.setChargesPatronales(bulletin.getTotalChargePatronale());

            // Nets et retenues
            exportDTO.setSalaireNet(bulletin.getSalaireNet());

            // Extraire Mensualité, Avance, Acompte et Retenues net depuis description JSON
            try {
                if (bulletin.getDescription() != null && !bulletin.getDescription().isEmpty()) {
                   BulletinPaieGenerateDTO  generateDTO = mapper.readValue(
                            bulletin.getDescription(),
                            BulletinPaieGenerateDTO.class
                    );

                    exportDTO.setMensualite(convertToBigDecimal(generateDTO.getMensualite()));
                    exportDTO.setAvance(convertToBigDecimal(generateDTO.getAvance()));
                    exportDTO.setAcompte(convertToBigDecimal(generateDTO.getAcompte()));
                    exportDTO.setRetenuesNet(convertToBigDecimal(generateDTO.getTotalRetenueNet()));
                } else {
                    // Valeurs par défaut si description n'est pas disponible
                    setDefaultRetenues(exportDTO, bulletin);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du parsing du JSON description: " + e.getMessage());
                // Valeurs par défaut en cas d'erreur
                setDefaultRetenues(exportDTO, bulletin);
            }

            exportDTO.setNetAPayer(bulletin.getNetAPayer());
            exportDTO.setStatut(bulletin.getStatut() != null ? bulletin.getStatut() : "N/A");

            exportList.add(exportDTO);
        }

        return exportList;
    }

    /**
     * Définit les valeurs par défaut pour les retenues
     */
    private void setDefaultRetenues(ApercuApresExportDTO exportDTO, BulletinPaieDTO bulletin) {
        exportDTO.setMensualite(BigDecimal.ZERO);
        exportDTO.setAvance(BigDecimal.ZERO);
        exportDTO.setAcompte(BigDecimal.ZERO);
        exportDTO.setRetenuesNet(bulletin.getAutreRetenue() != null ? bulletin.getAutreRetenue() : BigDecimal.ZERO);
    }

    /**
     * Convertit un double en BigDecimal, retourne 0 si la valeur est 0 ou null
     */
    private BigDecimal convertToBigDecimal(Double value) {
        if (value == null || value == 0.0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(value);
    }

    public byte[] generatePdf(List<BulletinPaieDTO> bulletinList, String mois, String entrepriseNom, String departementNom) {
        try {
            JasperReport jasperReport = compileCompleteApercuApresTemplate();

            // Créer les paramètres
            Map<String, Object> parameters = createPdfParameters(mois, entrepriseNom, departementNom);

            // Transformer les données en DTO d'export
            List<ApercuApresExportDTO> exportData = transformToExportDTO(bulletinList);

            // Créer le datasource avec les données transformées
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(exportData);

            // Générer le PDF
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Exporter en PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

            return baos.toByteArray();

        } catch (JRException e) {
            throw new RuntimeException("Erreur JasperReports lors de la génération du PDF: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF: " + e.getMessage(), e);
        }
    }

    public byte[] generateExcel(List<BulletinPaieDTO> bulletinList, String mois, String entrepriseNom, String departementNom) {
        try {
            // Créer le workbook Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Aperçu Après Traitement " + mois);

            // Style pour l'en-tête
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);
            CellStyle centerStyle = createCenterStyle(workbook);

            // Créer l'en-tête
            createExcelHeader(sheet, headerStyle, centerStyle, mois, entrepriseNom, departementNom);

            // Remplir les données
            if (bulletinList != null && !bulletinList.isEmpty()) {
                fillExcelData(sheet, dataStyle, numberStyle, centerStyle, bulletinList);

                // Ajouter les totaux
                addExcelTotals(sheet, totalStyle, numberStyle, bulletinList);
            } else {
                Row row = sheet.createRow(2);
                Cell cell = row.createCell(0);
                cell.setCellValue("Aucune donnée disponible pour l'aperçu après traitement");
                sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 18));
            }

            // Auto-size les colonnes
            autoSizeColumns(sheet);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du Excel: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> createPdfParameters(String mois, String entrepriseNom, String departementNom) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("mois", mois);
        parameters.put("entreprise", entrepriseNom);
        parameters.put("departement", departementNom != null ? departementNom : "Tous les départements");
        parameters.put("employeur", "Talents Plus ETT");
        parameters.put("numeroEmployeur", "123456789");
        return parameters;
    }

    private JasperReport compileCompleteApercuApresTemplate() throws JRException {
        String completeTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\"\n" +
                "              xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "              xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports\n" +
                "                                  http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\"\n" +
                "              name=\"apercu_apres_traitement_complet\"\n" +
                "              pageWidth=\"1200\"\n" +
                "              pageHeight=\"595\"\n" +
                "              orientation=\"Landscape\"\n" +
                "              columnWidth=\"1160\"\n" +
                "              leftMargin=\"20\"\n" +
                "              rightMargin=\"20\"\n" +
                "              topMargin=\"20\"\n" +
                "              bottomMargin=\"20\">\n" +
                "\n" +
                "    <parameter name=\"mois\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"entreprise\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"departement\" class=\"java.lang.String\"/>\n" +
                "    \n" +
                "    <field name=\"employeNomPrenom\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"departement\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"banque\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"numeroCompteEmploye\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"treiziemeMois\" class=\"java.lang.Double\"/>\n" +
                "    <field name=\"primesExceptionnelles\" class=\"java.lang.Double\"/>\n" +
                "    <field name=\"brutM\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"its\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"cnssSalarie\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"retenueLegale\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"vps\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"cnssEmployeur\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"chargesPatronales\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"salaireNet\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"mensualite\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"avance\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"acompte\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"retenuesNet\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"netAPayer\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"statut\" class=\"java.lang.String\"/>\n" +
                "\n" +
                "    <noData>\n" +
                "        <band height=\"100\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"1160\" height=\"100\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"14\" isBold=\"true\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Aucune donnée disponible pour l'aperçu après traitement]]></text>\n" +
                "            </staticText>\n" +
                "        </band>\n" +
                "    </noData>\n" +
                "</jasperReport>";

        return JasperCompileManager.compileReport(new java.io.ByteArrayInputStream(completeTemplate.getBytes()));
    }

    // === MÉTHODES POUR EXCEL ===

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));

        return style;
    }

    private CellStyle createTotalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));

        return style;
    }

    private CellStyle createCenterStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private void createExcelHeader(Sheet sheet, CellStyle headerStyle, CellStyle centerStyle,
                                   String mois, String entrepriseNom, String departementNom) {
        // Ligne 0: Titre
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("APERÇU APRÈS TRAITEMENT DE PAIE - " + mois);
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 18));

        // Ligne 1: Informations
        Row infoRow = sheet.createRow(1);
        Cell infoCell = infoRow.createCell(0);
        infoCell.setCellValue("Entreprise: " + entrepriseNom + " - Département: " +
                (departementNom != null ? departementNom : "Tous les départements"));
        infoCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 18));

        // Ligne 2: En-têtes des colonnes
        Row headerRow = sheet.createRow(2);
        headerRow.setHeightInPoints(35);

        String[] headers = {
                "Employé",
                "Département",
                "Banque",
                "Numéro Compte",
                "13ème mois",
                "Primes exceptionnelles",
                "Brut (M)",
                "ITS",
                "CNSS salarié",
                "Retenue légale",
                "VPS",
                "CNSS employeur",
                "Charges patronales",
                "Salaire net",
                "Mensualité",
                "Avance",
                "Acompte",
                "Retenues net",
                "Net à Payer"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillExcelData(Sheet sheet, CellStyle dataStyle, CellStyle numberStyle,
                               CellStyle centerStyle, List<BulletinPaieDTO> bulletinList) {
        ObjectMapper mapper = new ObjectMapper();
        int rowNum = 3;

        for (BulletinPaieDTO bulletin : bulletinList) {
            Row row = sheet.createRow(rowNum++);
            row.setHeightInPoints(20);

            // Informations employé
            String employeNom = bulletin.getEmploye() != null ?
                    bulletin.getEmploye().getNom() + " " + bulletin.getEmploye().getPrenom() : "N/A";
            String departement = bulletin.getDepartement() != null ? bulletin.getDepartement() : "N/A";
            String banque = bulletin.getBanque() != null ? bulletin.getBanque().getName() : "N/A";

            createCell(row, 0, employeNom, dataStyle);
            createCell(row, 1, departement, centerStyle);
            createCell(row, 2, banque, centerStyle);
            createCell(row, 3, bulletin.getNumeroCompteEmploye(), centerStyle);

            // Éléments de salaire
            createNumericCell(row, 4, bulletin.getTreiziemeMois(), numberStyle);
            createNumericCell(row, 5, bulletin.getPrimesExceptionnelles(), numberStyle);
            createNumericCell(row, 6, bulletin.getSalaireBrut(), numberStyle);

            // Retenues
            createNumericCell(row, 7, bulletin.getMontantIpts(), numberStyle);
            createNumericCell(row, 8, bulletin.getMontantCnss(), numberStyle);

            // Retenue légale = ITS + CNSS salarié
            BigDecimal retenueLegale = BigDecimal.ZERO;
            if (bulletin.getMontantIpts() != null) {
                retenueLegale = retenueLegale.add(bulletin.getMontantIpts());
            }
            if (bulletin.getMontantCnss() != null) {
                retenueLegale = retenueLegale.add(bulletin.getMontantCnss());
            }
            createNumericCell(row, 9, retenueLegale, numberStyle);

            // Charges patronales
            createNumericCell(row, 10, bulletin.getMontantVps(), numberStyle);
            createNumericCell(row, 11, bulletin.getMontantCnssEmployeur(), numberStyle);
            createNumericCell(row, 12, bulletin.getTotalChargePatronale(), numberStyle);

            // Nets et retenues
            createNumericCell(row, 13, bulletin.getSalaireNet(), numberStyle);

            // Extraire les valeurs depuis description JSON
            try {
                if (bulletin.getDescription() != null && !bulletin.getDescription().isEmpty()) {
                    BulletinPaieGenerateDTO generateDTO = mapper.readValue(
                            bulletin.getDescription(),
                            BulletinPaieGenerateDTO.class
                    );

                    createNumericCell(row, 14, convertDoubleValue(generateDTO.getMensualite()), numberStyle);
                    createNumericCell(row, 15, convertDoubleValue(generateDTO.getAvance()), numberStyle);
                    createNumericCell(row, 16, convertDoubleValue(generateDTO.getAcompte()), numberStyle);
                    createNumericCell(row, 17, convertDoubleValue(generateDTO.getTotalRetenueNet()), numberStyle);
                } else {
                    createNumericCell(row, 14, 0.0, numberStyle); // Mensualité
                    createNumericCell(row, 15, 0.0, numberStyle); // Avance
                    createNumericCell(row, 16, 0.0, numberStyle); // Acompte
                    createNumericCell(row, 17, bulletin.getAutreRetenue(), numberStyle); // Retenues net
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du parsing du JSON description: " + e.getMessage());
                createNumericCell(row, 14, 0.0, numberStyle); // Mensualité
                createNumericCell(row, 15, 0.0, numberStyle); // Avance
                createNumericCell(row, 16, 0.0, numberStyle); // Acompte
                createNumericCell(row, 17, bulletin.getAutreRetenue(), numberStyle); // Retenues net
            }

            createNumericCell(row, 18, bulletin.getNetAPayer(), numberStyle);
        }
    }

    /**
     * Convertit un Double en double pour Excel, retourne 0 si null
     */
    private double convertDoubleValue(Double value) {
        return value != null ? value : 0.0;
    }

    private void addExcelTotals(Sheet sheet, CellStyle totalStyle, CellStyle numberStyle,
                                List<BulletinPaieDTO> bulletinList) {
        ObjectMapper mapper = new ObjectMapper();
        int totalRowNum = bulletinList.size() + 3;
        Row totalRow = sheet.createRow(totalRowNum);
        totalRow.setHeightInPoints(25);

        // Calcul de TOUS les totaux
        double totalTreiziemeMois = bulletinList.stream()
                .mapToDouble(BulletinPaieDTO::getTreiziemeMois)
                .sum();

        double totalPrimesExceptionnelles = bulletinList.stream()
                .mapToDouble(BulletinPaieDTO::getPrimesExceptionnelles)
                .sum();

        BigDecimal totalBrutM = bulletinList.stream()
                .map(BulletinPaieDTO::getSalaireBrut)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalIts = bulletinList.stream()
                .map(BulletinPaieDTO::getMontantIpts)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCnssSalarie = bulletinList.stream()
                .map(BulletinPaieDTO::getMontantCnss)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRetenueLegale = totalIts.add(totalCnssSalarie);

        BigDecimal totalVps = bulletinList.stream()
                .map(BulletinPaieDTO::getMontantVps)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCnssEmployeur = bulletinList.stream()
                .map(BulletinPaieDTO::getMontantCnssEmployeur)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalChargesPatronales = bulletinList.stream()
                .map(BulletinPaieDTO::getTotalChargePatronale)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSalaireNet = bulletinList.stream()
                .map(BulletinPaieDTO::getSalaireNet)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Totaux depuis description JSON
        double totalMensualite = 0.0;
        double totalAvance = 0.0;
        double totalAcompte = 0.0;
        double totalRetenuesNet = 0.0;

        for (BulletinPaieDTO bulletin : bulletinList) {
            try {
                if (bulletin.getDescription() != null && !bulletin.getDescription().isEmpty()) {
                    BulletinPaieGenerateDTO generateDTO = mapper.readValue(
                            bulletin.getDescription(),
                            BulletinPaieGenerateDTO.class
                    );

                    totalMensualite += convertDoubleValue(generateDTO.getMensualite());
                    totalAvance += convertDoubleValue(generateDTO.getAvance());
                    totalAcompte += convertDoubleValue(generateDTO.getAcompte());
                    totalRetenuesNet += convertDoubleValue(generateDTO.getTotalRetenueNet());
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du parsing du JSON description pour les totaux: " + e.getMessage());
            }
        }

        // Si aucune valeur n'a été extraite, utiliser autreRetenue
        if (totalRetenuesNet == 0.0) {
            totalRetenuesNet = bulletinList.stream()
                    .map(BulletinPaieDTO::getAutreRetenue)
                    .filter(Objects::nonNull)
                    .mapToDouble(BigDecimal::doubleValue)
                    .sum();
        }

        BigDecimal totalNetAPayer = bulletinList.stream()
                .map(BulletinPaieDTO::getNetAPayer)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Cellule TOTAL fusionnée
        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("TOTAL GÉNÉRAL");
        totalLabelCell.setCellStyle(totalStyle);
        sheet.addMergedRegion(new CellRangeAddress(totalRowNum, totalRowNum, 0, 3));

        // Cellules de TOUS les totaux numériques
        createNumericCell(totalRow, 4, totalTreiziemeMois, totalStyle);
        createNumericCell(totalRow, 5, totalPrimesExceptionnelles, totalStyle);
        createNumericCell(totalRow, 6, totalBrutM.doubleValue(), totalStyle);
        createNumericCell(totalRow, 7, totalIts.doubleValue(), totalStyle);
        createNumericCell(totalRow, 8, totalCnssSalarie.doubleValue(), totalStyle);
        createNumericCell(totalRow, 9, totalRetenueLegale.doubleValue(), totalStyle);
        createNumericCell(totalRow, 10, totalVps.doubleValue(), totalStyle);
        createNumericCell(totalRow, 11, totalCnssEmployeur.doubleValue(), totalStyle);
        createNumericCell(totalRow, 12, totalChargesPatronales.doubleValue(), totalStyle);
        createNumericCell(totalRow, 13, totalSalaireNet.doubleValue(), totalStyle);
        createNumericCell(totalRow, 14, totalMensualite, totalStyle);
        createNumericCell(totalRow, 15, totalAvance, totalStyle);
        createNumericCell(totalRow, 16, totalAcompte, totalStyle);
        createNumericCell(totalRow, 17, totalRetenuesNet, totalStyle);
        createNumericCell(totalRow, 18, totalNetAPayer.doubleValue(), totalStyle);
    }

    private void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    private void createNumericCell(Row row, int column, double value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createNumericCell(Row row, int column, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(column);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        } else {
            cell.setCellValue(0);
        }
        cell.setCellStyle(style);
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < 19; i++) {
            sheet.autoSizeColumn(i);
            if (sheet.getColumnWidth(i) < 3000) {
                sheet.setColumnWidth(i, 3500);
            }
        }
        // Ajuster les colonnes spécifiques
        sheet.setColumnWidth(0, 6000); // Employé
        sheet.setColumnWidth(1, 4000); // Département
        sheet.setColumnWidth(3, 4500); // Numéro Compte
    }
}