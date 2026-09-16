package com.tpc.tpcgestpaie.localapp.service.export;

import com.tpc.tpcgestpaie.localapp.dto.TraitementSalaire.ApercuSalaireDTO;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Service
public class ApercuAvantTraitementExportService {

    public byte[] generatePdf(List<ApercuSalaireDTO> apercuList, String mois, String entrepriseNom, String departementNom) {
        try {
            JasperReport jasperReport = compileCompleteApercuTemplate();

            // Créer les paramètres
            Map<String, Object> parameters = createPdfParameters(mois, entrepriseNom, departementNom);

            // Créer le datasource avec TOUTES les données
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
                    apercuList != null ? apercuList : new ArrayList<>()
            );

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

    private JasperReport compileCompleteApercuTemplate() throws JRException {
        String completeTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\"\n" +
                "              xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "              xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports\n" +
                "                                  http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\"\n" +
                "              name=\"apercu_avant_traitement_complet\"\n" +
                "              pageWidth=\"842\"\n" +
                "              pageHeight=\"595\"\n" +
                "              orientation=\"Landscape\"\n" +
                "              columnWidth=\"802\"\n" +
                "              leftMargin=\"20\"\n" +
                "              rightMargin=\"20\"\n" +
                "              topMargin=\"20\"\n" +
                "              bottomMargin=\"20\">\n" +
                "\n" +
                "    <style name=\"TableHeader\" mode=\"Opaque\" backcolor=\"#4F81BD\" isBold=\"true\">\n" +
                "        <box>\n" +
                "            <pen lineWidth=\"0.5\" lineColor=\"#000000\"/>\n" +
                "            <topPen lineWidth=\"0.5\" lineColor=\"#000000\"/>\n" +
                "            <leftPen lineWidth=\"0.5\" lineColor=\"#000000\"/>\n" +
                "            <bottomPen lineWidth=\"0.5\" lineColor=\"#000000\"/>\n" +
                "            <rightPen lineWidth=\"0.5\" lineColor=\"#000000\"/>\n" +
                "        </box>\n" +
                "        <paragraph lineSpacing=\"Single\"/>\n" +
                "    </style>\n" +
                "    \n" +
                "    <style name=\"TableData\" mode=\"Opaque\" backcolor=\"#FFFFFF\">\n" +
                "        <box>\n" +
                "            <pen lineWidth=\"0.5\" lineColor=\"#000000\"/>\n" +
                "            <topPen lineWidth=\"0.5\" lineColor=\"#000000\"/>\n" +
                "            <leftPen lineWidth=\"0.5\" lineColor=\"#000000\"/>\n" +
                "            <bottomPen lineWidth=\"0.5\" lineColor=\"#000000\"/>\n" +
                "            <rightPen lineWidth=\"0.5\" lineColor=\"#000000\"/>\n" +
                "        </box>\n" +
                "        <paragraph lineSpacing=\"Single\"/>\n" +
                "    </style>\n" +
                "    \n" +
                "    <style name=\"TableTotal\" mode=\"Opaque\" backcolor=\"#D9E2F3\" isBold=\"true\">\n" +
                "        <box>\n" +
                "            <pen lineWidth=\"0.5\" lineColor=\"#000000\"/>\n" +
                "            <topPen lineWidth=\"0.5\" lineColor=\"#000000\"/>\n" +
                "            <leftPen lineWidth=\"0.5\" lineColor=\"#000000\"/>\n" +
                "            <bottomPen lineWidth=\"0.5\" lineColor=\"#000000\"/>\n" +
                "            <rightPen lineWidth=\"0.5\" lineColor=\"#000000\"/>\n" +
                "        </box>\n" +
                "        <paragraph lineSpacing=\"Single\"/>\n" +
                "    </style>\n" +
                "\n" +
                "    <parameter name=\"mois\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"entreprise\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"departement\" class=\"java.lang.String\"/>\n" +
                "    \n" +
                "    <!-- Champs du DTO pour PDF simplifié -->\n" +
                "    <field name=\"nomPrenomEmploye\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"departement\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"bank\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"numeroCompte\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"heureSup\" class=\"java.lang.Double\"/>\n" +
                "    <field name=\"tempsTravail\" class=\"java.lang.Double\"/>\n" +
                "    <field name=\"salaireBrut\" class=\"java.lang.Double\"/>\n" +
                "    <field name=\"primeAnciennete\" class=\"java.lang.Double\"/>\n" +
                "\n" +
                "    <!-- Variables pour les totaux -->\n" +
                "    <variable name=\"heureSup_SUM\" class=\"java.lang.Double\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{heureSup}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    <variable name=\"salaireBrut_SUM\" class=\"java.lang.Double\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{salaireBrut}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    <variable name=\"primeAnciennete_SUM\" class=\"java.lang.Double\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{primeAnciennete}]]></variableExpression>\n" +
                "    </variable>\n" +
                "\n" +
                "    <title>\n" +
                "        <band height=\"80\" splitType=\"Stretch\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"802\" height=\"30\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"16\" isBold=\"true\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[APERÇU AVANT TRAITEMENT DE PAIE]]></text>\n" +
                "            </staticText>\n" +
                "            \n" +
                "            <textField>\n" +
                "                <reportElement x=\"0\" y=\"40\" width=\"802\" height=\"20\"/>\n" +
                "                <textElement textAlignment=\"Center\">\n" +
                "                    <font isBold=\"true\" size=\"12\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[\"Mois : \" + $P{mois} + \" - Entreprise : \" + $P{entreprise} + \" - Département : \" + $P{departement}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "        </band>\n" +
                "    </title>\n" +
                "\n" +
                "    <columnHeader>\n" +
                "        <band height=\"30\" splitType=\"Stretch\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"120\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"8\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Employé]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"120\" y=\"0\" width=\"80\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"8\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Département]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"200\" y=\"0\" width=\"70\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"8\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Banque]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"270\" y=\"0\" width=\"100\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"8\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[N° Compte]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"370\" y=\"0\" width=\"80\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"8\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Prime Ancienneté]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"450\" y=\"0\" width=\"80\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"8\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Heure Sup]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"530\" y=\"0\" width=\"80\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"8\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Jours Travail]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"610\" y=\"0\" width=\"192\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"8\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Salaire Brut]]></text>\n" +
                "            </staticText>\n" +
                "        </band>\n" +
                "    </columnHeader>\n" +
                "\n" +
                "    <detail>\n" +
                "        <band height=\"25\" splitType=\"Stretch\">\n" +
                "            <textField>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"120\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Left\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"7\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{nomPrenomEmploye}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"120\" y=\"0\" width=\"80\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"7\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{departement}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"200\" y=\"0\" width=\"70\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"7\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{bank}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"270\" y=\"0\" width=\"100\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"7\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{numeroCompte}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField pattern=\"#,##0.00\">\n" +
                "                <reportElement x=\"370\" y=\"0\" width=\"80\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Right\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"7\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{primeAnciennete} != null ? $F{primeAnciennete} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField pattern=\"#,##0.00\">\n" +
                "                <reportElement x=\"450\" y=\"0\" width=\"80\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Right\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"7\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{heureSup} != null ? $F{heureSup} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField pattern=\"#,##0\">\n" +
                "                <reportElement x=\"530\" y=\"0\" width=\"80\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"7\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{tempsTravail} != null ? $F{tempsTravail} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField pattern=\"#,##0.00\">\n" +
                "                <reportElement x=\"610\" y=\"0\" width=\"192\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Right\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"7\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{salaireBrut} != null ? $F{salaireBrut} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "        </band>\n" +
                "    </detail>\n" +
                "\n" +
                "    <summary>\n" +
                "        <band height=\"35\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"370\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[TOTAL GÉNÉRAL]]></text>\n" +
                "            </staticText>\n" +
                "            <textField pattern=\"#,##0.00\" evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"370\" y=\"0\" width=\"80\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Right\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{primeAnciennete_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField pattern=\"#,##0.00\" evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"450\" y=\"0\" width=\"80\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Right\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{heureSup_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"530\" y=\"0\" width=\"80\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[-]]></text>\n" +
                "            </staticText>\n" +
                "            <textField pattern=\"#,##0.00\" evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"610\" y=\"0\" width=\"192\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Right\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{salaireBrut_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "        </band>\n" +
                "    </summary>\n" +
                "\n" +
                "    <noData>\n" +
                "        <band height=\"100\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"802\" height=\"100\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"14\" isBold=\"true\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Aucune donnée disponible pour l'aperçu avant traitement]]></text>\n" +
                "            </staticText>\n" +
                "        </band>\n" +
                "    </noData>\n" +
                "</jasperReport>";

        return JasperCompileManager.compileReport(new java.io.ByteArrayInputStream(completeTemplate.getBytes()));
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

    // ============================================================================
    // GÉNÉRATION EXCEL AVEC COLONNES DYNAMIQUES
    // ============================================================================

    public byte[] generateExcel(List<ApercuSalaireDTO> apercuList, String mois, String entrepriseNom, String departementNom) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Aperçu Avant Traitement " + mois);

            // Styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            // Créer l'en-tête principal
            createMainHeader(sheet, headerStyle, mois, entrepriseNom, departementNom);

            if (apercuList != null && !apercuList.isEmpty()) {
                // Collecter tous les libellés uniques
                Set<String> tousLibellesSalaire = new LinkedHashSet<>();
                Set<String> tousLibellesPrimes = new LinkedHashSet<>();

                for (ApercuSalaireDTO apercu : apercuList) {
                    if (apercu.getDetailsSalaire() != null) {
                        tousLibellesSalaire.addAll(apercu.getDetailsSalaire().keySet());
                    }
                    if (apercu.getDetailsPrimes() != null) {
                        tousLibellesPrimes.addAll(apercu.getDetailsPrimes().keySet());
                    }
                }

                // Créer les en-têtes de colonnes dynamiques
                int currentRow = createDynamicHeaders(sheet, headerStyle, tousLibellesSalaire, tousLibellesPrimes);

                // Remplir les données
                currentRow = fillDynamicData(sheet, dataStyle, numberStyle, apercuList,
                        tousLibellesSalaire, tousLibellesPrimes, currentRow);

                // Ajouter les totaux
                addDynamicTotals(sheet, totalStyle, numberStyle, apercuList,
                        tousLibellesSalaire, tousLibellesPrimes, currentRow);

                // Auto-size toutes les colonnes
                int totalColumns = 4 + tousLibellesSalaire.size() + tousLibellesPrimes.size() + 4;
                autoSizeAllColumns(sheet, totalColumns);
            } else {
                Row row = sheet.createRow(2);
                Cell cell = row.createCell(0);
                cell.setCellValue("Aucune donnée disponible pour l'aperçu avant traitement");
                sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 10));
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du Excel: " + e.getMessage(), e);
        }
    }

    private void createMainHeader(Sheet sheet, CellStyle headerStyle, String mois,
                                  String entrepriseNom, String departementNom) {
        // Ligne 0: Titre
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("APERÇU AVANT TRAITEMENT DE PAIE - " + mois);
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 20));

        // Ligne 1: Informations
        Row infoRow = sheet.createRow(1);
        Cell infoCell = infoRow.createCell(0);
        infoCell.setCellValue("Entreprise: " + entrepriseNom + " - Département: " +
                (departementNom != null ? departementNom : "Tous les départements"));
        infoCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 20));
    }

    private int createDynamicHeaders(Sheet sheet, CellStyle headerStyle,
                                     Set<String> libellesSalaire, Set<String> libellesPrimes) {
        Row headerRow = sheet.createRow(2);
        headerRow.setHeightInPoints(30);
        int colIndex = 0;

        // Colonnes fixes de base
        String[] baseHeaders = {"Employé", "Département", "Banque", "N° Compte"};
        for (String header : baseHeaders) {
            createHeaderCell(headerRow, colIndex++, header, headerStyle);
        }

        // Colonnes dynamiques pour les éléments de salaire (avec leurs libellés)
        for (String libelle : libellesSalaire) {
            createHeaderCell(headerRow, colIndex++, libelle, headerStyle);
        }

        // Colonnes dynamiques pour les primes (avec leurs libellés)
        for (String libelle : libellesPrimes) {
            createHeaderCell(headerRow, colIndex++, libelle, headerStyle);
        }

        // Colonnes calculées et finales
        String[] calculatedHeaders = {
                "Prime Ancienneté",
                "Heure Sup",
                "Jours Travail",
                "Salaire Brut"
        };

        for (String header : calculatedHeaders) {
            createHeaderCell(headerRow, colIndex++, header, headerStyle);
        }

        return 3; // Ligne suivante pour les données
    }

    private int fillDynamicData(Sheet sheet, CellStyle dataStyle, CellStyle numberStyle,
                                List<ApercuSalaireDTO> apercuList,
                                Set<String> libellesSalaire, Set<String> libellesPrimes,
                                int startRow) {
        int rowNum = startRow;

        for (ApercuSalaireDTO apercu : apercuList) {
            Row row = sheet.createRow(rowNum++);
            row.setHeightInPoints(20);
            int colIndex = 0;

            // Colonnes fixes de base
            createCell(row, colIndex++, apercu.getNomPrenomEmploye(), dataStyle);
            createCell(row, colIndex++, apercu.getDepartement(), dataStyle);
            createCell(row, colIndex++, apercu.getBank(), dataStyle);
            createCell(row, colIndex++, apercu.getNumeroCompte(), dataStyle);

            // Remplir les colonnes de salaire (dans l'ordre des en-têtes)
            for (String libelle : libellesSalaire) {
                Double montant = apercu.getDetailsSalaire() != null
                        ? apercu.getDetailsSalaire().get(libelle)
                        : null;
                createNumericCell(row, colIndex++, montant != null ? montant : 0.0, numberStyle);
            }

            // Remplir les colonnes de primes (dans l'ordre des en-têtes)
            for (String libelle : libellesPrimes) {
                Double montant = apercu.getDetailsPrimes() != null
                        ? apercu.getDetailsPrimes().get(libelle)
                        : null;
                createNumericCell(row, colIndex++, montant != null ? montant : 0.0, numberStyle);
            }

            // Colonnes calculées
            createNumericCell(row, colIndex++, apercu.getPrimeAnciennete(), numberStyle);
            createNumericCell(row, colIndex++, apercu.getHeureSup(), numberStyle);
            createNumericCell(row, colIndex++, apercu.getTempsTravail(), numberStyle);
            createNumericCell(row, colIndex++, apercu.getSalaireBrut(), numberStyle);
        }

        return rowNum;
    }

    private void addDynamicTotals(Sheet sheet, CellStyle totalStyle, CellStyle numberStyle,
                                  List<ApercuSalaireDTO> apercuList,
                                  Set<String> libellesSalaire, Set<String> libellesPrimes,
                                  int totalRow) {
        Row row = sheet.createRow(totalRow);
        row.setHeightInPoints(25);
        int colIndex = 0;

        // Label TOTAL
        Cell totalLabel = row.createCell(colIndex++);
        totalLabel.setCellValue("TOTAL GÉNÉRAL");
        totalLabel.setCellStyle(totalStyle);
        sheet.addMergedRegion(new CellRangeAddress(totalRow, totalRow, 0, 3));
        colIndex = 4; // Repositionner après la fusion

        // Totaux pour chaque libellé de salaire
        for (String libelle : libellesSalaire) {
            double total = apercuList.stream()
                    .filter(a -> a.getDetailsSalaire() != null && a.getDetailsSalaire().containsKey(libelle))
                    .mapToDouble(a -> a.getDetailsSalaire().get(libelle))
                    .sum();
            createNumericCell(row, colIndex++, total, totalStyle);
        }

        // Totaux pour chaque libellé de prime
        for (String libelle : libellesPrimes) {
            double total = apercuList.stream()
                    .filter(a -> a.getDetailsPrimes() != null && a.getDetailsPrimes().containsKey(libelle))
                    .mapToDouble(a -> a.getDetailsPrimes().get(libelle))
                    .sum();
            createNumericCell(row, colIndex++, total, totalStyle);
        }

        // Totaux des colonnes calculées
        createNumericCell(row, colIndex++,
                apercuList.stream().mapToDouble(ApercuSalaireDTO::getPrimeAnciennete).sum(), totalStyle);
        createNumericCell(row, colIndex++,
                apercuList.stream().mapToDouble(ApercuSalaireDTO::getHeureSup).sum(), totalStyle);
        colIndex++; // Skip jours travail (pas de total pertinent)
        createNumericCell(row, colIndex++,
                apercuList.stream().mapToDouble(ApercuSalaireDTO::getSalaireBrut).sum(), totalStyle);
    }

    // ============================================================================
    // MÉTHODES UTILITAIRES
    // ============================================================================

    private void createHeaderCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeightInPoints((short) 10);
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
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));
        return style;
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

    private void autoSizeAllColumns(Sheet sheet, int numberOfColumns) {
        for (int i = 0; i < numberOfColumns; i++) {
            sheet.autoSizeColumn(i);
            // Largeur minimale
            if (sheet.getColumnWidth(i) < 3000) {
                sheet.setColumnWidth(i, 3500);
            }
            // Largeur maximale pour éviter des colonnes trop larges
            if (sheet.getColumnWidth(i) > 8000) {
                sheet.setColumnWidth(i, 8000);
            }
        }
    }
}