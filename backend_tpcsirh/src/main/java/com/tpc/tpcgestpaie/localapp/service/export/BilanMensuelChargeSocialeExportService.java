package com.tpc.tpcgestpaie.localapp.service.export;

import com.tpc.tpcgestpaie.localapp.dto.etat.*;
import com.tpc.tpcgestpaie.localapp.dto.export.*;
import com.tpc.tpcgestpaie.localapp.model.BulletinPaie;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.bulletin.BulletinPaieRepository;
import com.tpc.tpcgestpaie.localapp.service.etat.EtatChargeSocialeService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BilanMensuelChargeSocialeExportService {

    private final BulletinPaieRepository bulletinPaieRepository;
    private final EtatChargeSocialeService etatChargeSocialeService;
    private final CompanyRepository companyRepository;
    private final EmployeRepository employeRepository;

    public BilanMensuelChargeSocialeExportService(BulletinPaieRepository bulletinPaieRepository, EtatChargeSocialeService etatChargeSocialeService, CompanyRepository companyRepository, EmployeRepository employeRepository) {
        this.bulletinPaieRepository = bulletinPaieRepository;
        this.etatChargeSocialeService = etatChargeSocialeService;
        this.companyRepository = companyRepository;
        this.employeRepository = employeRepository;
    }

    // === MÉTHODES POUR LE BILAN MENSUEL ===



    // === MÉTHODES DE SUPPORT ===

    private Map<String, Object> createPdfParameters(String mois, Map<String, Object> data) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("mois", mois);
        parameters.put("entreprise", data.get("entrepriseNom"));
        parameters.put("employeur", data.get("employeur"));
        parameters.put("numeroEmployeur", data.get("numeroEmployeur"));
        return parameters;
    }

    private Map<String, Object> createPdfParametersPeriodique(String debut, String fin, Map<String, Object> data) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("debut", debut);
        parameters.put("fin", fin);
        parameters.put("entreprise", data.get("entrepriseNom"));
        parameters.put("employeur", "Talents Plus ETT");
        parameters.put("numeroEmployeur", "123456789");
        return parameters;
    }

    public byte[] generatePdf(Long companyId, String mois) {
        try {
            // Récupérer les données
            Map<String, Object> data = getExportData(companyId, mois);
            List<BilanMensuelChargeSocialeExportDTO> lignes = (List<BilanMensuelChargeSocialeExportDTO>) data.get("lignes");

            JasperReport jasperReport;

            // Essayer de charger le template externe d'abord
            ClassPathResource resource = new ClassPathResource("reports/bilan_mensuel_charge_sociale.jrxml");
            if (resource.exists()) {
                try {
                    InputStream jrxmlInputStream = resource.getInputStream();
                    jasperReport = JasperCompileManager.compileReport(jrxmlInputStream);
                } catch (Exception e) {
                    // Si le template externe échoue, utiliser le template simple
                    System.err.println("Erreur avec le template externe, utilisation du template simple: " + e.getMessage());
                    jasperReport = compileSimpleTemplate();
                }
            } else {
                jasperReport = compileSimpleTemplate();
            }

            // Créer les paramètres
            Map<String, Object> parameters = createPdfParameters(mois, data);

            // Créer le datasource
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
                    lignes != null ? lignes : new ArrayList<>()
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

    public byte[] generateExcel(Long companyId, String mois) {
        try {
            // Récupérer les données
            Map<String, Object> data = getExportData(companyId, mois);
            List<BilanMensuelChargeSocialeExportDTO> lignes = (List<BilanMensuelChargeSocialeExportDTO>) data.get("lignes");
            BilanMensuelChargeSocialeTotalDTO totaux = (BilanMensuelChargeSocialeTotalDTO) data.get("totaux");

            // Créer le workbook Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Bilan Charges Sociales " + mois);

            // Style pour l'en-tête
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            // Créer l'en-tête
            createExcelHeader(sheet, headerStyle);

            // Remplir les données
            if (lignes != null && !lignes.isEmpty()) {
                fillExcelData(sheet, dataStyle, numberStyle, lignes);

                // Ajouter les totaux
                if (totaux != null) {
                    addExcelTotals(sheet, totalStyle, numberStyle, totaux, lignes.size());
                }
            } else {
                // Aucune donnée
                Row row = sheet.createRow(1);
                Cell cell = row.createCell(0);
                cell.setCellValue("Aucune donnée disponible pour la période sélectionnée");
                // Fusionner les cellules pour le message
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));
            }

            // Auto-size les colonnes
            autoSizeColumns(sheet);

            // Écrire dans le ByteArrayOutputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du Excel: " + e.getMessage(), e);
        }
    }

    private JasperReport compileSimpleTemplate() throws JRException {
        String simpleTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\"\n" +
                "              xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "              xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports\n" +
                "                                  http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\"\n" +
                "              name=\"bilan_mensuel_charge_sociale\"\n" +
                "              pageWidth=\"842\"\n" +
                "              pageHeight=\"595\"\n" +
                "              orientation=\"Landscape\"\n" +
                "              columnWidth=\"802\"\n" +
                "              leftMargin=\"20\"\n" +
                "              rightMargin=\"20\"\n" +
                "              topMargin=\"20\"\n" +
                "              bottomMargin=\"20\">\n" +
                "\n" +
                "    <!-- Styles pour les bordures -->\n" +
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
                "    <parameter name=\"employeur\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"numeroEmployeur\" class=\"java.lang.String\"/>\n" +
                "    \n" +
                "    <field name=\"mois\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"entreprise\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"employe\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"numeroCnss\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"salaireBrut\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"cnssEmploye\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"cnssEmployeur\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"totalChargeSociale\" class=\"java.math.BigDecimal\"/>\n" +
                "\n" +
                "    <variable name=\"salaireBrut_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{salaireBrut}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    \n" +
                "    <variable name=\"cnssEmploye_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{cnssEmploye}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    \n" +
                "    <variable name=\"cnssEmployeur_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{cnssEmployeur}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    \n" +
                "    <variable name=\"totalChargeSociale_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{totalChargeSociale}]]></variableExpression>\n" +
                "    </variable>\n" +
                "\n" +
                "    <title>\n" +
                "        <band height=\"80\" splitType=\"Stretch\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"802\" height=\"30\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"18\" isBold=\"true\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[BILAN MENSUEL DES CHARGES SOCIALES]]></text>\n" +
                "            </staticText>\n" +
                "            \n" +
                "            <textField>\n" +
                "                <reportElement x=\"0\" y=\"40\" width=\"802\" height=\"20\"/>\n" +
                "                <textElement textAlignment=\"Center\">\n" +
                "                    <font isBold=\"true\" size=\"12\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[\"Mois : \" + $P{mois} + \" - Entreprise : \" + $P{entreprise}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "        </band>\n" +
                "    </title>\n" +
                "\n" +
                "    <columnHeader>\n" +
                "        <band height=\"30\" splitType=\"Stretch\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"200\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Employé]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"200\" y=\"0\" width=\"150\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Numéro CNSS]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"350\" y=\"0\" width=\"120\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Salaire Brut]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"470\" y=\"0\" width=\"120\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[CNSS Employé]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"590\" y=\"0\" width=\"120\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[CNSS Employeur]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"710\" y=\"0\" width=\"92\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Total Charges]]></text>\n" +
                "            </staticText>\n" +
                "        </band>\n" +
                "    </columnHeader>\n" +
                "\n" +
                "    <detail>\n" +
                "        <band height=\"25\" splitType=\"Stretch\">\n" +
                "            <textField>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"200\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{employe}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"200\" y=\"0\" width=\"150\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{numeroCnss}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"350\" y=\"0\" width=\"120\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{salaireBrut} != null ? $F{salaireBrut} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"470\" y=\"0\" width=\"120\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{cnssEmploye} != null ? $F{cnssEmploye} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"590\" y=\"0\" width=\"120\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{cnssEmployeur} != null ? $F{cnssEmployeur} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"710\" y=\"0\" width=\"92\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{totalChargeSociale} != null ? $F{totalChargeSociale} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "        </band>\n" +
                "    </detail>\n" +
                "\n" +
                "    <summary>\n" +
                "        <band height=\"35\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"350\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[TOTAL]]></text>\n" +
                "            </staticText>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"350\" y=\"0\" width=\"120\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{salaireBrut_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"470\" y=\"0\" width=\"120\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{cnssEmploye_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"590\" y=\"0\" width=\"120\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{cnssEmployeur_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"710\" y=\"0\" width=\"92\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{totalChargeSociale_SUM}]]></textFieldExpression>\n" +
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
                "                <text><![CDATA[Aucune donnée disponible pour la période sélectionnée]]></text>\n" +
                "            </staticText>\n" +
                "        </band>\n" +
                "    </noData>\n" +
                "</jasperReport>";

        return JasperCompileManager.compileReport(new java.io.ByteArrayInputStream(simpleTemplate.getBytes()));
    }
    private JasperReport compilePeriodiqueTemplate() throws JRException {
        String periodiqueTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\"\n" +
                "              xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "              xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports\n" +
                "                                  http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\"\n" +
                "              name=\"bilan_periodique_charge_sociale\"\n" +
                "              pageWidth=\"842\"\n" +
                "              pageHeight=\"595\"\n" +
                "              orientation=\"Landscape\"\n" +
                "              columnWidth=\"802\"\n" +
                "              leftMargin=\"20\"\n" +
                "              rightMargin=\"20\"\n" +
                "              topMargin=\"20\"\n" +
                "              bottomMargin=\"20\">\n" +
                "\n" +
                "    <!-- Styles pour les bordures -->\n" +
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
                "    <parameter name=\"debut\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"fin\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"entreprise\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"employeur\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"numeroEmployeur\" class=\"java.lang.String\"/>\n" +
                "    \n" +
                "    <field name=\"mois\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"entreprise\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"salaireBrut\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"cnssEmploye\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"cnssEmployeur\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"totalChargeSociale\" class=\"java.math.BigDecimal\"/>\n" +
                "\n" +
                "    <variable name=\"salaireBrut_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{salaireBrut}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    \n" +
                "    <variable name=\"cnssEmploye_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{cnssEmploye}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    \n" +
                "    <variable name=\"cnssEmployeur_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{cnssEmployeur}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    \n" +
                "    <variable name=\"totalChargeSociale_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{totalChargeSociale}]]></variableExpression>\n" +
                "    </variable>\n" +
                "\n" +
                "    <title>\n" +
                "        <band height=\"100\" splitType=\"Stretch\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"802\" height=\"30\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"18\" isBold=\"true\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[BILAN PÉRIODIQUE DES CHARGES SOCIALES]]></text>\n" +
                "            </staticText>\n" +
                "            \n" +
                "            <textField>\n" +
                "                <reportElement x=\"0\" y=\"40\" width=\"802\" height=\"20\"/>\n" +
                "                <textElement textAlignment=\"Center\">\n" +
                "                    <font isBold=\"true\" size=\"12\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[\"Période : \" + $P{debut} + \" à \" + $P{fin}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            \n" +
                "            <textField>\n" +
                "                <reportElement x=\"0\" y=\"60\" width=\"802\" height=\"20\"/>\n" +
                "                <textElement textAlignment=\"Center\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[\"Entreprise : \" + $P{entreprise}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "        </band>\n" +
                "    </title>\n" +
                "\n" +
                "    <columnHeader>\n" +
                "        <band height=\"30\" splitType=\"Stretch\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"150\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Mois]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"150\" y=\"0\" width=\"150\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Entreprise]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"300\" y=\"0\" width=\"120\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Salaire Brut]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"420\" y=\"0\" width=\"120\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[CNSS Employé]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"540\" y=\"0\" width=\"120\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[CNSS Employeur]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"660\" y=\"0\" width=\"142\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Total Charges]]></text>\n" +
                "            </staticText>\n" +
                "        </band>\n" +
                "    </columnHeader>\n" +
                "\n" +
                "    <detail>\n" +
                "        <band height=\"25\" splitType=\"Stretch\">\n" +
                "            <textField>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"150\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{mois}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"150\" y=\"0\" width=\"150\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{entreprise}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"300\" y=\"0\" width=\"120\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{salaireBrut} != null ? $F{salaireBrut} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"420\" y=\"0\" width=\"120\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{cnssEmploye} != null ? $F{cnssEmploye} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"540\" y=\"0\" width=\"120\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{cnssEmployeur} != null ? $F{cnssEmployeur} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"660\" y=\"0\" width=\"142\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{totalChargeSociale} != null ? $F{totalChargeSociale} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "        </band>\n" +
                "    </detail>\n" +
                "\n" +
                "    <summary>\n" +
                "        <band height=\"35\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"300\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[TOTAL PÉRIODE]]></text>\n" +
                "            </staticText>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"300\" y=\"0\" width=\"120\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{salaireBrut_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"420\" y=\"0\" width=\"120\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{cnssEmploye_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"540\" y=\"0\" width=\"120\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{cnssEmployeur_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"660\" y=\"0\" width=\"142\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{totalChargeSociale_SUM}]]></textFieldExpression>\n" +
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
                "                <text><![CDATA[Aucune donnée disponible pour la période sélectionnée]]></text>\n" +
                "            </staticText>\n" +
                "        </band>\n" +
                "    </noData>\n" +
                "</jasperReport>";

        return JasperCompileManager.compileReport(new java.io.ByteArrayInputStream(periodiqueTemplate.getBytes()));
    }
    // === MÉTHODES PRIVÉES COMMUNES ===

    private Map<String, Object> getExportData(Long companyId, String mois) {
        try {
            // Récupérer les bulletins avec les relations nécessaires
            List<BulletinPaie> bulletins = bulletinPaieRepository.findByCompanyIdAndMoisWithRelations(companyId, mois);

            // Récupérer le nom de l'entreprise depuis la base de données
            String entrepriseNom = getEntrepriseName(companyId);

            // Transformer en DTO d'export
            List<BilanMensuelChargeSocialeExportDTO> lignes = new ArrayList<>();

            if (bulletins != null && !bulletins.isEmpty()) {
                lignes = bulletins.stream()
                        .filter(Objects::nonNull)
                        .map(bp -> {
                            try {
                                BilanMensuelChargeSocialeDTO dto = new BilanMensuelChargeSocialeDTO(
                                        bp.getMois(),
                                        entrepriseNom, // Utiliser le nom récupéré de la base
                                        (bp.getEmploye() != null ?
                                                bp.getEmploye().getNom() + " " + bp.getEmploye().getPrenom() : "N/A"),
                                        bp.getSalaireBrut(),
                                        bp.getMontantCnss(),
                                        bp.getMontantCnssEmployeur()
                                );

                                String numeroCnss = (bp.getEmploye() != null && bp.getEmploye().getNumeroCnss() != null)
                                        ? bp.getEmploye().getNumeroCnss()
                                        : "Non défini";

                                return new BilanMensuelChargeSocialeExportDTO(dto, numeroCnss);
                            } catch (Exception e) {
                                // Log l'erreur et retourner un DTO par défaut
                                System.err.println("Erreur lors de la transformation du bulletin: " + e.getMessage());
                                return createDefaultExportDTO(bp != null ? bp.getMois() : mois);
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }

            // Calculer les totaux
            BigDecimal totalSalaire = lignes.stream()
                    .map(BilanMensuelChargeSocialeExportDTO::getSalaireBrut)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalCnss = lignes.stream()
                    .map(BilanMensuelChargeSocialeExportDTO::getCnssEmploye)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalCnssEmployeur = lignes.stream()
                    .map(BilanMensuelChargeSocialeExportDTO::getCnssEmployeur)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalCharges = lignes.stream()
                    .map(BilanMensuelChargeSocialeExportDTO::getTotalChargeSociale)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BilanMensuelChargeSocialeTotalDTO totaux = new BilanMensuelChargeSocialeTotalDTO(
                    totalSalaire, totalCnss, totalCnssEmployeur, totalCharges
            );

            Map<String, Object> result = new HashMap<>();
            result.put("lignes", lignes);
            result.put("totaux", totaux);
            result.put("entrepriseNom", entrepriseNom);
            result.put("employeur", "Talents Plus ETT");
            result.put("numeroEmployeur", "123456789");

            return result;

        } catch (Exception e) {
            // Retourner des données par défaut en cas d'erreur
            System.err.println("Erreur lors de la récupération des données: " + e.getMessage());
            return createDefaultExportData(mois);
        }
    }

    // === MÉTHODE CRITIQUE : RÉCUPÉRATION DU NOM DE L'ENTREPRISE ===

    private String getEntrepriseName(Long companyId) {
        try {
            // Méthode 1: Utiliser le CompanyRepository directement
            Optional<Company> company = companyRepository.findById(companyId);
            if (company.isPresent()) {
                return company.get().getName();
            }

            // Méthode 2: Récupérer depuis les bulletins de paie
            List<BulletinPaie> bulletins = bulletinPaieRepository.findByCompanyIdAndMoisWithRelations(companyId, "2024-01");
            if (bulletins != null && !bulletins.isEmpty() && bulletins.get(0).getCompany() != null) {
                return bulletins.get(0).getCompany().getName();
            }

            // Méthode 3: Essayer avec n'importe quel mois
            List<BulletinPaie> anyBulletins = bulletinPaieRepository.findByCompanyId(companyId);
            if (anyBulletins != null && !anyBulletins.isEmpty() && anyBulletins.get(0).getCompany() != null) {
                return anyBulletins.get(0).getCompany().getName();
            }

            return "Entreprise ID: " + companyId;

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du nom de l'entreprise: " + e.getMessage());
            return "Entreprise ID: " + companyId;
        }
    }

    private Map<String, Object> getExportDataPeriodique(Long companyId, String debut, String fin) {
        try {
            // Utiliser votre service existant pour récupérer les données
            Map<String, Object> data = etatChargeSocialeService.getBilanPeriodiqueParEntreprise(companyId, debut, fin);

            List<BilanPeriodiqueChargeSocialeDTO> lignesOrigine = (List<BilanPeriodiqueChargeSocialeDTO>) data.get("lignes");
            BilanPeriodiqueChargeSocialeTotalDTO totaux = (BilanPeriodiqueChargeSocialeTotalDTO) data.get("totaux");

            // Récupérer le nom de l'entreprise depuis la base de données
            String entrepriseNom = getEntrepriseName(companyId);

            // Transformer en DTO d'export
            List<BilanPeriodiqueChargeSocialeExportDTO> lignes = new ArrayList<>();

            if (lignesOrigine != null && !lignesOrigine.isEmpty()) {
                lignes = lignesOrigine.stream()
                        .filter(Objects::nonNull)
                        .map(dto -> new BilanPeriodiqueChargeSocialeExportDTO(dto, entrepriseNom))
                        .collect(Collectors.toList());
            }

            Map<String, Object> result = new HashMap<>();
            result.put("lignes", lignes);
            result.put("totaux", totaux);
            result.put("entrepriseNom", entrepriseNom);
            result.put("debut", debut);
            result.put("fin", fin);

            return result;

        } catch (Exception e) {
            // Retourner des données par défaut en cas d'erreur
            System.err.println("Erreur lors de la récupération des données périodiques: " + e.getMessage());
            return createDefaultExportDataPeriodique(debut, fin);
        }
    }

    private BilanMensuelChargeSocialeExportDTO createDefaultExportDTO(String mois) {
        return new BilanMensuelChargeSocialeExportDTO(
                mois, "N/A", "N/A", "N/A",
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        );
    }

    private Map<String, Object> createDefaultExportData(String mois) {
        Map<String, Object> result = new HashMap<>();
        result.put("lignes", new ArrayList<>());
        result.put("totaux", new BilanMensuelChargeSocialeTotalDTO(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        ));
        result.put("entrepriseNom", "Entreprise non trouvée");
        result.put("employeur", "Talents Plus ETT");
        result.put("numeroEmployeur", "123456789");
        return result;
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

        // Format numérique avec 2 décimales
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

        // Format numérique pour les totaux
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));

        return style;
    }

    private void createExcelHeader(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        headerRow.setHeightInPoints(25);

        String[] headers = {
                "Mois", "Entreprise", "Employé", "Numéro CNSS",
                "Salaire Brut", "CNSS Employé", "CNSS Employeur", "Total Charges"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillExcelData(Sheet sheet, CellStyle dataStyle, CellStyle numberStyle,
                               List<BilanMensuelChargeSocialeExportDTO> lignes) {
        int rowNum = 1;

        for (BilanMensuelChargeSocialeExportDTO ligne : lignes) {
            Row row = sheet.createRow(rowNum++);
            row.setHeightInPoints(20);

            // Données textuelles
            createCell(row, 0, ligne.getMois(), dataStyle);
            createCell(row, 1, ligne.getEntreprise(), dataStyle);
            createCell(row, 2, ligne.getEmploye(), dataStyle);
            createCell(row, 3, ligne.getNumeroCnss(), dataStyle);

            // Données numériques
            createNumericCell(row, 4, ligne.getSalaireBrut(), numberStyle);
            createNumericCell(row, 5, ligne.getCnssEmploye(), numberStyle);
            createNumericCell(row, 6, ligne.getCnssEmployeur(), numberStyle);
            createNumericCell(row, 7, ligne.getTotalChargeSociale(), numberStyle);
        }
    }

    private void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
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


    private void addExcelTotals(Sheet sheet, CellStyle totalStyle, CellStyle numberStyle,
                                BilanMensuelChargeSocialeTotalDTO totaux, int dataSize) {
        int totalRowNum = dataSize + 1;
        Row totalRow = sheet.createRow(totalRowNum);
        totalRow.setHeightInPoints(25);

        // Cellule TOTAL fusionnée
        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("TOTAL");
        totalLabelCell.setCellStyle(totalStyle);

        // Fusionner les cellules du label TOTAL
        sheet.addMergedRegion(new CellRangeAddress(totalRowNum, totalRowNum, 0, 3));

        // Cellules de totaux numériques
        createNumericCell(totalRow, 4, totaux.getTotalSalaireBrut(), totalStyle);
        createNumericCell(totalRow, 5, totaux.getTotalMontantCnss(), totalStyle);
        createNumericCell(totalRow, 6, totaux.getTotalMontantCnssEmployeur(), totalStyle);
        createNumericCell(totalRow, 7, totaux.getTotalCharges(), totalStyle);
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
            // Ajuster la largeur minimale
            if (sheet.getColumnWidth(i) < 3000) {
                sheet.setColumnWidth(i, 4000);
            }
        }
    }

    // === MÉTHODES POUR LE BILAN PÉRIODIQUE ===

    public byte[] generatePdfBilanPeriodique(Long companyId, String debut, String fin) {
        try {
            // Récupérer les données
            Map<String, Object> data = getExportDataPeriodique(companyId, debut, fin);
            List<BilanPeriodiqueChargeSocialeExportDTO> lignes = (List<BilanPeriodiqueChargeSocialeExportDTO>) data.get("lignes");

            JasperReport jasperReport = compilePeriodiqueTemplate();

            // Créer les paramètres
            Map<String, Object> parameters = createPdfParametersPeriodique(debut, fin, data);

            // Créer le datasource
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
                    lignes != null ? lignes : new ArrayList<>()
            );

            // Générer le PDF
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Exporter en PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

            return baos.toByteArray();

        } catch (JRException e) {
            throw new RuntimeException("Erreur JasperReports lors de la génération du PDF périodique: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF périodique: " + e.getMessage(), e);
        }
    }

    public byte[] generateExcelBilanPeriodique(Long companyId, String debut, String fin) {
        try {
            // Récupérer les données
            Map<String, Object> data = getExportDataPeriodique(companyId, debut, fin);
            List<BilanPeriodiqueChargeSocialeExportDTO> lignes = (List<BilanPeriodiqueChargeSocialeExportDTO>) data.get("lignes");
            BilanPeriodiqueChargeSocialeTotalDTO totaux = (BilanPeriodiqueChargeSocialeTotalDTO) data.get("totaux");

            // Créer le workbook Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Bilan Periodique " + debut + " a " + fin);

            // Style pour l'en-tête
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            // Créer l'en-tête avec période
            createExcelHeaderPeriodique(sheet, headerStyle, debut, fin);

            // Remplir les données
            if (lignes != null && !lignes.isEmpty()) {
                fillExcelDataPeriodique(sheet, dataStyle, numberStyle, lignes);

                // Ajouter les totaux
                if (totaux != null) {
                    addExcelTotalsPeriodique(sheet, totalStyle, numberStyle, totaux, lignes.size());
                }
            } else {
                // Aucune donnée
                Row row = sheet.createRow(2);
                Cell cell = row.createCell(0);
                cell.setCellValue("Aucune donnée disponible pour la période sélectionnée");
                sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 5));
            }

            // Auto-size les colonnes
            autoSizeColumnsPeriodique(sheet);

            // Écrire dans le ByteArrayOutputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du Excel périodique: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> createDefaultExportDataPeriodique(String debut, String fin) {
        Map<String, Object> result = new HashMap<>();
        result.put("lignes", new ArrayList<>());
        result.put("totaux", new BilanPeriodiqueChargeSocialeTotalDTO(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        ));
        result.put("entrepriseNom", "Entreprise non trouvée");
        result.put("debut", debut);
        result.put("fin", fin);
        return result;
    }


    private void createExcelHeaderPeriodique(Sheet sheet, CellStyle headerStyle, String debut, String fin) {
        // Ligne 0: Titre
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BILAN PÉRIODIQUE DES CHARGES SOCIALES - " + debut + " à " + fin);
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

        // Ligne 1: En-têtes des colonnes
        Row headerRow = sheet.createRow(1);
        headerRow.setHeightInPoints(25);

        String[] headers = {
                "Mois", "Entreprise", "Salaire Brut", "CNSS Employé", "CNSS Employeur", "Total Charges"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }


    private void fillExcelDataPeriodique(Sheet sheet, CellStyle dataStyle, CellStyle numberStyle,
                                         List<BilanPeriodiqueChargeSocialeExportDTO> lignes) {
        int rowNum = 2;

        for (BilanPeriodiqueChargeSocialeExportDTO ligne : lignes) {
            Row row = sheet.createRow(rowNum++);
            row.setHeightInPoints(20);

            createCell(row, 0, ligne.getMois(), dataStyle);
            createCell(row, 1, ligne.getEntreprise(), dataStyle);
            createNumericCell(row, 2, ligne.getSalaireBrut(), numberStyle);
            createNumericCell(row, 3, ligne.getCnssEmploye(), numberStyle);
            createNumericCell(row, 4, ligne.getCnssEmployeur(), numberStyle);
            createNumericCell(row, 5, ligne.getTotalChargeSociale(), numberStyle);
        }
    }

    private void addExcelTotalsPeriodique(Sheet sheet, CellStyle totalStyle, CellStyle numberStyle,
                                          BilanPeriodiqueChargeSocialeTotalDTO totaux, int dataSize) {
        int totalRowNum = dataSize + 2;
        Row totalRow = sheet.createRow(totalRowNum);
        totalRow.setHeightInPoints(25);

        // Cellule TOTAL fusionnée
        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("TOTAL PÉRIODE");
        totalLabelCell.setCellStyle(totalStyle);

        // Fusionner les cellules du label TOTAL
        sheet.addMergedRegion(new CellRangeAddress(totalRowNum, totalRowNum, 0, 1));

        // Cellules de totaux numériques
        createNumericCell(totalRow, 2, totaux.getTotalSalaire(), totalStyle);
        createNumericCell(totalRow, 3, totaux.getTotalCnssEmploye(), totalStyle);
        createNumericCell(totalRow, 4, totaux.getTotalCnssEmployeur(), totalStyle);
        createNumericCell(totalRow, 5, totaux.getTotalCharges(), totalStyle);
    }

    private void autoSizeColumnsPeriodique(Sheet sheet) {
        for (int i = 0; i < 6; i++) {
            sheet.autoSizeColumn(i);
            if (sheet.getColumnWidth(i) < 3000) {
                sheet.setColumnWidth(i, 4000);
            }
        }
    }


// === MÉTHODES POUR LE BILAN PÉRIODIQUE PAR EMPLOYÉ ===

    public byte[] generatePdfBilanPeriodiqueEmploye(Long companyId, Long employeId, String debut, String fin) {
        try {
            // Récupérer les données du bilan périodique par employé
            Map<String, Object> data = getExportDataPeriodiqueEmploye(companyId, employeId, debut, fin);
            List<BilanPeriodiqueChargeSocialeEmployeExportDTO> lignes = (List<BilanPeriodiqueChargeSocialeEmployeExportDTO>) data.get("lignes");

            JasperReport jasperReport = compilePeriodiqueEmployeTemplate();

            // Créer les paramètres
            Map<String, Object> parameters = createPdfParametersPeriodiqueEmploye(debut, fin, data);

            // Créer le datasource
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
                    lignes != null ? lignes : new ArrayList<>()
            );

            // Générer le PDF
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Exporter en PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

            return baos.toByteArray();

        } catch (JRException e) {
            throw new RuntimeException("Erreur JasperReports lors de la génération du PDF périodique employé: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF périodique employé: " + e.getMessage(), e);
        }
    }

    public byte[] generateExcelBilanPeriodiqueEmploye(Long companyId, Long employeId, String debut, String fin) {
        try {
            // Récupérer les données
            Map<String, Object> data = getExportDataPeriodiqueEmploye(companyId, employeId, debut, fin);
            List<BilanPeriodiqueChargeSocialeEmployeExportDTO> lignes = (List<BilanPeriodiqueChargeSocialeEmployeExportDTO>) data.get("lignes");
            BilanPeriodiqueChargeSocialeTotalDTO totaux = (BilanPeriodiqueChargeSocialeTotalDTO) data.get("totaux");

            // Créer le workbook Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Bilan Periodique Employe " + debut + " a " + fin);

            // Style pour l'en-tête
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            // Créer l'en-tête avec période et informations employé
            createExcelHeaderPeriodiqueEmploye(sheet, headerStyle, debut, fin, data);

            // Remplir les données
            if (lignes != null && !lignes.isEmpty()) {
                fillExcelDataPeriodiqueEmploye(sheet, dataStyle, numberStyle, lignes);

                // Ajouter les totaux
                if (totaux != null) {
                    addExcelTotalsPeriodiqueEmploye(sheet, totalStyle, numberStyle, totaux, lignes.size());
                }
            } else {
                // Aucune donnée
                Row row = sheet.createRow(4);
                Cell cell = row.createCell(0);
                cell.setCellValue("Aucune donnée disponible pour la période sélectionnée");
                sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 7));
            }

            // Auto-size les colonnes
            autoSizeColumnsPeriodiqueEmploye(sheet);
            // Écrire dans le ByteArrayOutputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du Excel périodique employé: " + e.getMessage(), e);
        }
    }

// === MÉTHODES DE SUPPORT POUR LE BILAN PÉRIODIQUE EMPLOYÉ ===

    private Map<String, Object> getExportDataPeriodiqueEmploye(Long companyId, Long employeId, String debut, String fin) {
        try {
            // Utiliser votre service existant pour récupérer les données
            Map<String, Object> data = etatChargeSocialeService.getBilanPeriodiqueCharges(companyId, employeId, debut, fin);

            List<BilanPeriodiqueChargeSocialeDTO> lignesOrigine = (List<BilanPeriodiqueChargeSocialeDTO>) data.get("lignes");
            BilanPeriodiqueChargeSocialeTotalDTO totaux = (BilanPeriodiqueChargeSocialeTotalDTO) data.get("totaux");

            // Récupérer le nom de l'entreprise
            String entrepriseNom = getEntrepriseName(companyId);

            // Transformer en DTO d'export spécifique
            List<BilanPeriodiqueChargeSocialeEmployeExportDTO> lignes = new ArrayList<>();

            if (lignesOrigine != null && !lignesOrigine.isEmpty()) {
                lignes = lignesOrigine.stream()
                        .filter(Objects::nonNull)
                        .map(dto -> new BilanPeriodiqueChargeSocialeEmployeExportDTO(
                                dto.getDebut(),
                                dto.getFin(),
                                dto.getEmploye(),
                                dto.getNumeroCnssEmploye(),
                                dto.getSalaireBrut(),
                                dto.getCnssEmploye(),
                                dto.getCnssEmployeur(),
                                dto.getTotalChargeSociale(),
                                entrepriseNom,
                                dto.getNumeroCnssEntreprise()
                        ))
                        .collect(Collectors.toList());
            }

            // Récupérer les informations de l'employé depuis la première ligne
            String employeNom = lignes.isEmpty() ? "Employé non trouvé" : lignes.get(0).getEmploye();
            String numeroCnssEmploye = lignes.isEmpty() ? "N/A" : lignes.get(0).getNumeroCnssEmploye();

            Map<String, Object> result = new HashMap<>();
            result.put("lignes", lignes);
            result.put("totaux", totaux);
            result.put("entrepriseNom", entrepriseNom);
            result.put("employeNom", employeNom);
            result.put("numeroCnssEmploye", numeroCnssEmploye);
            result.put("debut", debut);
            result.put("fin", fin);

            return result;

        } catch (Exception e) {
            // Retourner des données par défaut en cas d'erreur
            System.err.println("Erreur lors de la récupération des données périodiques employé: " + e.getMessage());
            return createDefaultExportDataPeriodiqueEmploye(debut, fin);
        }
    }

    private Map<String, Object> createDefaultExportDataPeriodiqueEmploye(String debut, String fin) {
        Map<String, Object> result = new HashMap<>();
        result.put("lignes", new ArrayList<>());
        result.put("totaux", new BilanPeriodiqueChargeSocialeTotalDTO(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        ));
        result.put("entrepriseNom", "Entreprise non trouvée");
        result.put("employeNom", "Employé non trouvé");
        result.put("numeroCnssEmploye", "N/A");
        result.put("debut", debut);
        result.put("fin", fin);
        return result;
    }

    private Map<String, Object> createPdfParametersPeriodiqueEmploye(String debut, String fin, Map<String, Object> data) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("debut", debut);
        parameters.put("fin", fin);
        parameters.put("entreprise", data.get("entrepriseNom"));
        parameters.put("employe", data.get("employeNom"));
        parameters.put("numeroCnssEmploye", data.get("numeroCnssEmploye"));
        parameters.put("employeur", "Talents Plus ETT");
        parameters.put("numeroEmployeur", "123456789");
        return parameters;
    }

    private JasperReport compilePeriodiqueEmployeTemplate() throws JRException {
        String periodiqueEmployeTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\"\n" +
                "              xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "              xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports\n" +
                "                                  http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\"\n" +
                "              name=\"bilan_periodique_charge_sociale_employe\"\n" +
                "              pageWidth=\"842\"\n" +
                "              pageHeight=\"595\"\n" +
                "              orientation=\"Landscape\"\n" +
                "              columnWidth=\"802\"\n" +
                "              leftMargin=\"20\"\n" +
                "              rightMargin=\"20\"\n" +
                "              topMargin=\"20\"\n" +
                "              bottomMargin=\"20\">\n" +
                "\n" +
                "    <!-- Styles pour les bordures -->\n" +
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
                "    <parameter name=\"debut\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"fin\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"entreprise\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"employe\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"numeroCnssEmploye\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"employeur\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"numeroEmployeur\" class=\"java.lang.String\"/>\n" +
                "    \n" +
                "    <field name=\"debut\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"fin\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"employe\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"numeroCnssEmploye\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"salaireBrut\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"cnssEmploye\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"cnssEmployeur\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"totalChargeSociale\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"entreprise\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"numeroCnssEntreprise\" class=\"java.lang.String\"/>\n" +
                "\n" +
                "    <variable name=\"salaireBrut_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{salaireBrut}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    \n" +
                "    <variable name=\"cnssEmploye_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{cnssEmploye}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    \n" +
                "    <variable name=\"cnssEmployeur_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{cnssEmployeur}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    \n" +
                "    <variable name=\"totalChargeSociale_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{totalChargeSociale}]]></variableExpression>\n" +
                "    </variable>\n" +
                "\n" +
                "    <title>\n" +
                "        <band height=\"120\" splitType=\"Stretch\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"802\" height=\"30\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"18\" isBold=\"true\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[BILAN PÉRIODIQUE DES CHARGES SOCIALES - PAR EMPLOYÉ]]></text>\n" +
                "            </staticText>\n" +
                "            \n" +
                "            <textField>\n" +
                "                <reportElement x=\"0\" y=\"40\" width=\"802\" height=\"20\"/>\n" +
                "                <textElement textAlignment=\"Center\">\n" +
                "                    <font isBold=\"true\" size=\"12\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[\"Période : \" + $P{debut} + \" à \" + $P{fin}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            \n" +
                "            <textField>\n" +
                "                <reportElement x=\"0\" y=\"60\" width=\"802\" height=\"20\"/>\n" +
                "                <textElement textAlignment=\"Center\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[\"Entreprise : \" + $P{entreprise}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            \n" +
                "            <textField>\n" +
                "                <reportElement x=\"0\" y=\"80\" width=\"802\" height=\"20\"/>\n" +
                "                <textElement textAlignment=\"Center\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[\"Employé : \" + $P{employe} + \" - CNSS : \" + $P{numeroCnssEmploye}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "        </band>\n" +
                "    </title>\n" +
                "\n" +
                "    <columnHeader>\n" +
                "        <band height=\"30\" splitType=\"Stretch\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"100\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Début]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"100\" y=\"0\" width=\"100\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Fin]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"200\" y=\"0\" width=\"150\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Employé]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"350\" y=\"0\" width=\"120\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Salaire Brut]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"470\" y=\"0\" width=\"120\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[CNSS Employé]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"590\" y=\"0\" width=\"120\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[CNSS Employeur]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"710\" y=\"0\" width=\"92\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Total Charges]]></text>\n" +
                "            </staticText>\n" +
                "        </band>\n" +
                "    </columnHeader>\n" +
                "\n" +
                "    <detail>\n" +
                "        <band height=\"25\" splitType=\"Stretch\">\n" +
                "            <textField>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"100\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{debut}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"100\" y=\"0\" width=\"100\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{fin}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"200\" y=\"0\" width=\"150\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{employe}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"350\" y=\"0\" width=\"120\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{salaireBrut} != null ? $F{salaireBrut} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"470\" y=\"0\" width=\"120\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{cnssEmploye} != null ? $F{cnssEmploye} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"590\" y=\"0\" width=\"120\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{cnssEmployeur} != null ? $F{cnssEmployeur} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"710\" y=\"0\" width=\"92\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{totalChargeSociale} != null ? $F{totalChargeSociale} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "        </band>\n" +
                "    </detail>\n" +
                "\n" +
                "    <summary>\n" +
                "        <band height=\"35\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"350\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[TOTAL PÉRIODE]]></text>\n" +
                "            </staticText>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"350\" y=\"0\" width=\"120\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{salaireBrut_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"470\" y=\"0\" width=\"120\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{cnssEmploye_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"590\" y=\"0\" width=\"120\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{cnssEmployeur_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"710\" y=\"0\" width=\"92\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{totalChargeSociale_SUM}]]></textFieldExpression>\n" +
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
                "                <text><![CDATA[Aucune donnée disponible pour la période sélectionnée]]></text>\n" +
                "            </staticText>\n" +
                "        </band>\n" +
                "    </noData>\n" +
                "</jasperReport>";

        return JasperCompileManager.compileReport(new java.io.ByteArrayInputStream(periodiqueEmployeTemplate.getBytes()));
    }

    private void createExcelHeaderPeriodiqueEmploye(Sheet sheet, CellStyle headerStyle, String debut, String fin, Map<String, Object> data) {
        // Ligne 0: Titre
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BILAN PÉRIODIQUE DES CHARGES SOCIALES - PAR EMPLOYÉ");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

        // Ligne 1: Informations période
        Row periodRow = sheet.createRow(1);
        createCell(periodRow, 0, "Période: " + debut + " à " + fin, headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));

        // Ligne 2: Informations entreprise et employé
        Row infoRow = sheet.createRow(2);
        createCell(infoRow, 0, "Entreprise: " + data.get("entrepriseNom"), headerStyle);
        createCell(infoRow, 4, "Employé: " + data.get("employeNom"), headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 7));

        // Ligne 3: En-têtes des colonnes
        Row headerRow = sheet.createRow(3);
        headerRow.setHeightInPoints(25);

        String[] headers = {
                "Début", "Fin", "Employé", "Numéro CNSS",
                "Salaire Brut", "CNSS Employé", "CNSS Employeur", "Total Charges"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillExcelDataPeriodiqueEmploye(Sheet sheet, CellStyle dataStyle, CellStyle numberStyle,
                                                List<BilanPeriodiqueChargeSocialeEmployeExportDTO> lignes) {
        int rowNum = 4;

        for (BilanPeriodiqueChargeSocialeEmployeExportDTO ligne : lignes) {
            Row row = sheet.createRow(rowNum++);
            row.setHeightInPoints(20);

            createCell(row, 0, ligne.getDebut(), dataStyle);
            createCell(row, 1, ligne.getFin(), dataStyle);
            createCell(row, 2, ligne.getEmploye(), dataStyle);
            createCell(row, 3, ligne.getNumeroCnssEmploye(), dataStyle);
            createNumericCell(row, 4, ligne.getSalaireBrut(), numberStyle);
            createNumericCell(row, 5, ligne.getCnssEmploye(), numberStyle);
            createNumericCell(row, 6, ligne.getCnssEmployeur(), numberStyle);
            createNumericCell(row, 7, ligne.getTotalChargeSociale(), numberStyle);
        }
    }

    private void addExcelTotalsPeriodiqueEmploye(Sheet sheet, CellStyle totalStyle, CellStyle numberStyle,
                                                 BilanPeriodiqueChargeSocialeTotalDTO totaux, int dataSize) {
        int totalRowNum = dataSize + 4;
        Row totalRow = sheet.createRow(totalRowNum);
        totalRow.setHeightInPoints(25);

        // Cellule TOTAL fusionnée
        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("TOTAL PÉRIODE");
        totalLabelCell.setCellStyle(totalStyle);

        // Fusionner les cellules du label TOTAL
        sheet.addMergedRegion(new CellRangeAddress(totalRowNum, totalRowNum, 0, 3));

        // Cellules de totaux numériques
        createNumericCell(totalRow, 4, totaux.getTotalSalaire(), totalStyle);
        createNumericCell(totalRow, 5, totaux.getTotalCnssEmploye(), totalStyle);
        createNumericCell(totalRow, 6, totaux.getTotalCnssEmployeur(), totalStyle);
        createNumericCell(totalRow, 7, totaux.getTotalCharges(), totalStyle);
    }

    private void autoSizeColumnsPeriodiqueEmploye(Sheet sheet) {
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
            if (sheet.getColumnWidth(i) < 3000) {
                sheet.setColumnWidth(i, 4000);
            }
        }
    }

    // === MÉTHODES POUR LE BILAN FISCAL ===

    public byte[] generatePdfBilanFiscal(Long companyId, String mois, String moisDebut, String moisFin) {
        try {
            // Récupérer les données du bilan fiscal
            Map<String, Object> data = getExportDataBilanFiscal(companyId, mois, moisDebut, moisFin);
            List<BilanFiscaleExportDTO> lignes = (List<BilanFiscaleExportDTO>) data.get("lignes");

            JasperReport jasperReport = compileBilanFiscalTemplate();

            // Créer les paramètres
            Map<String, Object> parameters = createPdfParametersBilanFiscal(data);

            // Créer le datasource
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
                    lignes != null ? lignes : new ArrayList<>()
            );

            // Générer le PDF
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Exporter en PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

            return baos.toByteArray();

        } catch (JRException e) {
            throw new RuntimeException("Erreur JasperReports lors de la génération du PDF bilan fiscal: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF bilan fiscal: " + e.getMessage(), e);
        }
    }

    private String getMoisFromBulletin(BilanFiscaleDTO dto) {
        try {
            // Si le DTO a déjà un mois, on l'utilise
            if (dto.getMois() != null && !dto.getMois().trim().isEmpty()) {
                return dto.getMois();
            }

            // Sinon, on essaie de récupérer le mois depuis le bulletin de paie
            // Tu dois avoir accès à bulletinPaieRepository ou à une méthode pour récupérer le bulletin

            // Méthode 1: Si tu as l'ID du bulletin dans le DTO
            // return bulletinPaieRepository.findById(dto.getBulletinId()).map(BulletinPaie::getMois).orElse("N/A");

            // Méthode 2: Si tu peux récupérer par employé et période
            // List<BulletinPaie> bulletins = bulletinPaieRepository.findByEmployeAndPeriode(...);

            // Méthode 3: Temporaire - utiliser la période de début comme mois
            if (dto.getPeriodeDebut() != null && !dto.getPeriodeDebut().trim().isEmpty()) {
                return dto.getPeriodeDebut();
            }

            return "N/A";

        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du mois du bulletin: " + e.getMessage());
            return "N/A";
        }
    }

    public byte[] generateExcelBilanFiscal(Long companyId, String mois, String moisDebut, String moisFin) {
        try {
            // Récupérer les données
            Map<String, Object> data = getExportDataBilanFiscal(companyId, mois, moisDebut, moisFin);
            List<BilanFiscaleExportDTO> lignes = (List<BilanFiscaleExportDTO>) data.get("lignes");
            BilanFiscaleTotalDTO totaux = (BilanFiscaleTotalDTO) data.get("totaux");

            // Créer le workbook Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Bilan Fiscal");

            // Style pour l'en-tête
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            // Créer l'en-tête avec période
            createExcelHeaderBilanFiscal(sheet, headerStyle, data);

            // Remplir les données
            if (lignes != null && !lignes.isEmpty()) {
                fillExcelDataBilanFiscal(sheet, dataStyle, numberStyle, lignes);

                // Ajouter les totaux
                if (totaux != null) {
                    addExcelTotalsBilanFiscal(sheet, totalStyle, numberStyle, totaux, lignes.size());
                }
            } else {
                // Aucune donnée
                Row row = sheet.createRow(2);
                Cell cell = row.createCell(0);
                cell.setCellValue("Aucune donnée disponible pour la période sélectionnée");
                sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 8));
            }

            // Auto-size les colonnes
            autoSizeColumnsBilanFiscal(sheet);

            // Écrire dans le ByteArrayOutputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du Excel bilan fiscal: " + e.getMessage(), e);
        }
    }

// === MÉTHODES DE SUPPORT POUR LE BILAN FISCAL ===

    private Map<String, Object> getExportDataBilanFiscal(Long companyId, String mois, String moisDebut, String moisFin) {
        try {
            // Utiliser votre service existant pour récupérer les données
            BilanFiscaleResponse response = etatChargeSocialeService.getBilan(companyId, mois, moisDebut, moisFin);

            // Vérifier si la réponse est nulle
            if (response == null) {
                return createDefaultExportDataBilanFiscal();
            }

            // Transformer en DTO d'export
            List<BilanFiscaleExportDTO> lignes = new ArrayList<>();

            if (response.getBulletins() != null) {
                lignes = response.getBulletins().stream()
                        .filter(Objects::nonNull)
                        .map(dto -> new BilanFiscaleExportDTO(
                                dto.getMois(), // Maintenant le mois est correct
                                dto.getPeriodeDebut(),
                                dto.getPeriodeFin(),
                                dto.getEmploye(),
                                dto.getEntreprise(),
                                dto.getSalaireBrut(),
                                dto.getSalaireBrutArrondi(),
                                dto.getIts(),
                                dto.getVps(),
                                dto.getTotal()
                        ))
                        .collect(Collectors.toList());
            }

            // Créer l'objet totaux à partir de la réponse
            BilanFiscaleTotalDTO totaux = new BilanFiscaleTotalDTO(
                    response.getTotalSalaireBrut() != null ? response.getTotalSalaireBrut() : BigDecimal.ZERO,
                    response.getTotalSalaireBrutArrondi() != null ? response.getTotalSalaireBrutArrondi() : BigDecimal.ZERO,
                    response.getTotalIts() != null ? response.getTotalIts() : BigDecimal.ZERO,
                    response.getTotalVps() != null ? response.getTotalVps() : BigDecimal.ZERO,
                    response.getTotalGeneral() != null ? response.getTotalGeneral() : BigDecimal.ZERO
            );

            // Déterminer le titre de la période
            String titrePeriode;
            if (mois != null) {
                titrePeriode = "Mois: " + mois;
            } else {
                titrePeriode = "Période: " + moisDebut + " à " + moisFin;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("lignes", lignes);
            result.put("totaux", totaux);
            result.put("titrePeriode", titrePeriode);
            result.put("entrepriseNom", getEntrepriseName(companyId));
            result.put("mois", mois);
            result.put("moisDebut", moisDebut);
            result.put("moisFin", moisFin);

            return result;

        } catch (Exception e) {
            // Retourner des données par défaut en cas d'erreur
            System.err.println("Erreur lors de la récupération des données bilan fiscal: " + e.getMessage());
            return createDefaultExportDataBilanFiscal();
        }
    }


    // Méthode utilitaire pour déterminer le mois à afficher
    private String determineMoisAffichage(BilanFiscaleDTO dto, String moisParam, String moisDebutParam) {
        // Priorité 1: Le mois du DTO
        if (dto.getMois() != null && !dto.getMois().trim().isEmpty()) {
            return dto.getMois();
        }

        // Priorité 2: Le mois passé en paramètre
        if (moisParam != null && !moisParam.trim().isEmpty()) {
            return moisParam;
        }

        // Priorité 3: La période de début du DTO
        if (dto.getPeriodeDebut() != null && !dto.getPeriodeDebut().trim().isEmpty()) {
            return dto.getPeriodeDebut();
        }

        // Priorité 4: Le mois de début passé en paramètre
        if (moisDebutParam != null && !moisDebutParam.trim().isEmpty()) {
            return moisDebutParam;
        }

        // Par défaut
        return "N/A";
    }

    // Méthode utilitaire pour déterminer le titre de la période
    private String determineTitrePeriode(String mois, String moisDebut, String moisFin) {
        if (mois != null && !mois.trim().isEmpty()) {
            return "Mois: " + mois;
        } else if (moisDebut != null && !moisDebut.trim().isEmpty() && moisFin != null && !moisFin.trim().isEmpty()) {
            return "Période: " + moisDebut + " à " + moisFin;
        } else if (moisDebut != null && !moisDebut.trim().isEmpty()) {
            return "À partir de: " + moisDebut;
        } else {
            return "Période non spécifiée";
        }
    }


    private Map<String, Object> createDefaultExportDataBilanFiscal() {
        Map<String, Object> result = new HashMap<>();
        result.put("lignes", new ArrayList<>());
        result.put("totaux", new BilanFiscaleTotalDTO(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        ));
        result.put("titrePeriode", "Période non spécifiée");
        result.put("entrepriseNom", "Entreprise non trouvée");
        return result;
    }

    private Map<String, Object> createPdfParametersBilanFiscal(Map<String, Object> data) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("titrePeriode", data.get("titrePeriode"));
        parameters.put("entreprise", data.get("entrepriseNom"));
        parameters.put("employeur", "Talents Plus ETT");
        parameters.put("numeroEmployeur", "123456789");
        return parameters;
    }

    private JasperReport compileBilanFiscalTemplate() throws JRException {
        String bilanFiscalTemplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\"\n" +
                "              xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "              xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports\n" +
                "                                  http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\"\n" +
                "              name=\"bilan_fiscal\"\n" +
                "              pageWidth=\"842\"\n" +
                "              pageHeight=\"595\"\n" +
                "              orientation=\"Landscape\"\n" +
                "              columnWidth=\"802\"\n" +
                "              leftMargin=\"20\"\n" +
                "              rightMargin=\"20\"\n" +
                "              topMargin=\"20\"\n" +
                "              bottomMargin=\"20\">\n" +
                "\n" +
                "    <!-- Styles pour les bordures -->\n" +
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
                "    <parameter name=\"titrePeriode\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"entreprise\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"employeur\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"numeroEmployeur\" class=\"java.lang.String\"/>\n" +
                "    \n" +
                "    <field name=\"mois\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"periodeDebut\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"periodeFin\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"employe\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"entreprise\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"salaireBrut\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"salaireBrutArrondi\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"its\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"vps\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"total\" class=\"java.math.BigDecimal\"/>\n" +
                "\n" +
                "    <variable name=\"salaireBrut_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{salaireBrut}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    \n" +
                "    <variable name=\"salaireBrutArrondi_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{salaireBrutArrondi}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    \n" +
                "    <variable name=\"its_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{its}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    \n" +
                "    <variable name=\"vps_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{vps}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    \n" +
                "    <variable name=\"total_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{total}]]></variableExpression>\n" +
                "    </variable>\n" +
                "\n" +
                "    <title>\n" +
                "        <band height=\"100\" splitType=\"Stretch\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"802\" height=\"30\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"18\" isBold=\"true\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[BILAN FISCAL]]></text>\n" +
                "            </staticText>\n" +
                "            \n" +
                "            <textField>\n" +
                "                <reportElement x=\"0\" y=\"40\" width=\"802\" height=\"20\"/>\n" +
                "                <textElement textAlignment=\"Center\">\n" +
                "                    <font isBold=\"true\" size=\"12\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$P{titrePeriode}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            \n" +
                "            <textField>\n" +
                "                <reportElement x=\"0\" y=\"60\" width=\"802\" height=\"20\"/>\n" +
                "                <textElement textAlignment=\"Center\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[\"Entreprise : \" + $P{entreprise}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "        </band>\n" +
                "    </title>\n" +
                "\n" +
                "    <columnHeader>\n" +
                "        <band height=\"30\" splitType=\"Stretch\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"80\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Mois]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"80\" y=\"0\" width=\"120\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Employé]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"200\" y=\"0\" width=\"100\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Salaire Brut]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"300\" y=\"0\" width=\"100\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Salaire Arrondi]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"400\" y=\"0\" width=\"80\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[ITS]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"480\" y=\"0\" width=\"80\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[VPS]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"560\" y=\"0\" width=\"100\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Total ITS+VPS]]></text>\n" +
                "            </staticText>\n" +
                "        </band>\n" +
                "    </columnHeader>\n" +
                "\n" +
                "    <detail>\n" +
                "        <band height=\"25\" splitType=\"Stretch\">\n" +
                "            <textField>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"80\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[\n" +
                "        $F{mois} != null ? $F{mois} : \n" +
                "        ($F{periodeDebut} != null ? $F{periodeDebut} : \"N/A\")\n" +
                "    ]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"80\" y=\"0\" width=\"120\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{employe}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"200\" y=\"0\" width=\"100\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{salaireBrut} != null ? $F{salaireBrut} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"300\" y=\"0\" width=\"100\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{salaireBrutArrondi} != null ? $F{salaireBrutArrondi} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"400\" y=\"0\" width=\"80\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{its} != null ? $F{its} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"480\" y=\"0\" width=\"80\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{vps} != null ? $F{vps} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"560\" y=\"0\" width=\"100\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{total} != null ? $F{total} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "        </band>\n" +
                "    </detail>\n" +
                "\n" +
                "    <summary>\n" +
                "        <band height=\"35\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"200\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[TOTAL]]></text>\n" +
                "            </staticText>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"200\" y=\"0\" width=\"100\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{salaireBrut_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"300\" y=\"0\" width=\"100\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{salaireBrutArrondi_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"400\" y=\"0\" width=\"80\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{its_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"480\" y=\"0\" width=\"80\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{vps_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"560\" y=\"0\" width=\"100\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{total_SUM}]]></textFieldExpression>\n" +
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
                "                <text><![CDATA[Aucune donnée disponible pour la période sélectionnée]]></text>\n" +
                "            </staticText>\n" +
                "        </band>\n" +
                "    </noData>\n" +
                "</jasperReport>";

        return JasperCompileManager.compileReport(new java.io.ByteArrayInputStream(bilanFiscalTemplate.getBytes()));
    }
    private void createExcelHeaderBilanFiscal(Sheet sheet, CellStyle headerStyle, Map<String, Object> data) {
        // Ligne 0: Titre
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BILAN FISCAL");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

        // Ligne 1: Informations période et entreprise
        Row infoRow = sheet.createRow(1);
        createCell(infoRow, 0, data.get("titrePeriode") + " - Entreprise: " + data.get("entrepriseNom"), headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));

        // Ligne 2: En-têtes des colonnes
        Row headerRow = sheet.createRow(2);
        headerRow.setHeightInPoints(25);

        String[] headers = {
                "Mois", "Période Début", "Période Fin", "Employé", "Entreprise",
                "Salaire Brut", "Salaire Brut Arrondi", "ITS", "VPS", "Total"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillExcelDataBilanFiscal(Sheet sheet, CellStyle dataStyle, CellStyle numberStyle,
                                          List<BilanFiscaleExportDTO> lignes) {
        int rowNum = 3;

        for (BilanFiscaleExportDTO ligne : lignes) {
            Row row = sheet.createRow(rowNum++);
            row.setHeightInPoints(20);

            createCell(row, 0, ligne.getMois(), dataStyle);
            createCell(row, 1, ligne.getPeriodeDebut(), dataStyle);
            createCell(row, 2, ligne.getPeriodeFin(), dataStyle);
            createCell(row, 3, ligne.getEmploye(), dataStyle);
            createCell(row, 4, ligne.getEntreprise(), dataStyle);
            createNumericCell(row, 5, ligne.getSalaireBrut(), numberStyle);
            createNumericCell(row, 6, ligne.getSalaireBrutArrondi(), numberStyle);
            createNumericCell(row, 7, ligne.getIts(), numberStyle);
            createNumericCell(row, 8, ligne.getVps(), numberStyle);
            createNumericCell(row, 9, ligne.getTotal(), numberStyle);
        }
    }

    private void addExcelTotalsBilanFiscal(Sheet sheet, CellStyle totalStyle, CellStyle numberStyle,
                                           BilanFiscaleTotalDTO totaux, int dataSize) {
        int totalRowNum = dataSize + 3;
        Row totalRow = sheet.createRow(totalRowNum);
        totalRow.setHeightInPoints(25);

        // Cellule TOTAL fusionnée
        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("TOTAL");
        totalLabelCell.setCellStyle(totalStyle);

        // Fusionner les cellules du label TOTAL
        sheet.addMergedRegion(new CellRangeAddress(totalRowNum, totalRowNum, 0, 4));

        // Cellules de totaux numériques
        createNumericCell(totalRow, 5, totaux.getTotalSalaireBrut(), totalStyle);
        createNumericCell(totalRow, 6, totaux.getTotalSalaireBrutArrondi(), totalStyle);
        createNumericCell(totalRow, 7, totaux.getTotalIts(), totalStyle);
        createNumericCell(totalRow, 8, totaux.getTotalVps(), totalStyle);
        createNumericCell(totalRow, 9, totaux.getTotalGeneral(), totalStyle);
    }

    private void autoSizeColumnsBilanFiscal(Sheet sheet) {
        for (int i = 0; i < 10; i++) {
            sheet.autoSizeColumn(i);
            if (sheet.getColumnWidth(i) < 3000) {
                sheet.setColumnWidth(i, 4000);
            }
        }
    }


    // === MÉTHODES POUR LE BILAN CHARGE SOCIALE PAR EMPLOYÉ ===

    public byte[] generatePdfBilanChargeSocialeEmploye(Long employeId, String mois, String moisDebut, String moisFin) {
        try {
            // Récupérer les données
            Map<String, Object> data = getExportDataBilanChargeSocialeEmploye(employeId, mois, moisDebut, moisFin);
            List<BilanChargeSocialeExportDTO> lignes = (List<BilanChargeSocialeExportDTO>) data.get("lignes");

            JasperReport jasperReport = compileBilanChargeSocialeEmployeTemplate();

            // Créer les paramètres
            Map<String, Object> parameters = createPdfParametersBilanChargeSocialeEmploye(data);

            // Créer le datasource
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
                    lignes != null ? lignes : new ArrayList<>()
            );

            // Générer le PDF
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Exporter en PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

            return baos.toByteArray();

        } catch (JRException e) {
            throw new RuntimeException("Erreur JasperReports lors de la génération du PDF charge sociale employé: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF charge sociale employé: " + e.getMessage(), e);
        }
    }

    public byte[] generateExcelBilanChargeSocialeEmploye(Long employeId, String mois, String moisDebut, String moisFin) {
        try {
            // Récupérer les données
            Map<String, Object> data = getExportDataBilanChargeSocialeEmploye(employeId, mois, moisDebut, moisFin);
            List<BilanChargeSocialeExportDTO> lignes = (List<BilanChargeSocialeExportDTO>) data.get("lignes");
            BilanChargeSocialeTotalDTO totaux = (BilanChargeSocialeTotalDTO) data.get("totaux");

            // Créer le workbook Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Bilan Charges Sociales Employé");

            // Style pour l'en-tête
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle totalStyle = createTotalStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            // Créer l'en-tête avec période
            createExcelHeaderBilanChargeSocialeEmploye(sheet, headerStyle, data);

            // Remplir les données
            if (lignes != null && !lignes.isEmpty()) {
                fillExcelDataBilanChargeSocialeEmploye(sheet, dataStyle, numberStyle, lignes);

                // Ajouter les totaux
                if (totaux != null) {
                    addExcelTotalsBilanChargeSocialeEmploye(sheet, totalStyle, numberStyle, totaux, lignes.size());
                }
            } else {
                // Aucune donnée
                Row row = sheet.createRow(2);
                Cell cell = row.createCell(0);
                cell.setCellValue("Aucune donnée disponible pour la période sélectionnée");
                sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));
            }

            // Auto-size les colonnes
            autoSizeColumnsBilanChargeSocialeEmploye(sheet);

            // Écrire dans le ByteArrayOutputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du Excel charge sociale employé: " + e.getMessage(), e);
        }
    }

// === MÉTHODES DE SUPPORT ===
private Map<String, Object> getExportDataBilanChargeSocialeEmploye(Long employeId, String mois, String moisDebut, String moisFin) {
    try {
        // Récupérer directement les bulletins
        List<BulletinPaie> bulletins;
        if (mois != null) {
            bulletins = bulletinPaieRepository.findByEmployeAndMoisBetween(employeId, mois, mois);
        } else {
            bulletins = bulletinPaieRepository.findByEmployeAndMoisBetween(employeId, moisDebut, moisFin);
        }

        // Vérifier s'il y a des bulletins
        if (bulletins == null || bulletins.isEmpty()) {
            return createDefaultExportDataBilanChargeSocialeEmploye();
        }

        // Récupérer les informations depuis le premier bulletin
        BulletinPaie premierBulletin = bulletins.get(0);
        String employeNom = "Employé non trouvé";
        String entrepriseNom = "Entreprise non spécifiée";

        if (premierBulletin.getEmploye() != null) {
            Employe employe = premierBulletin.getEmploye();
            employeNom = employe.getNom() + " " + employe.getPrenom();
        }

        if (premierBulletin.getCompany() != null) {
            entrepriseNom = premierBulletin.getCompany().getName();
        }

        // Transformer en DTO d'export et calculer les totaux
        List<BilanChargeSocialeExportDTO> lignes = new ArrayList<>();

        BigDecimal totalSalaireBrut = BigDecimal.ZERO;
        BigDecimal totalSalaireBrutArrondi = BigDecimal.ZERO;
        BigDecimal totalIpts = BigDecimal.ZERO;
        BigDecimal totalVps = BigDecimal.ZERO;
        BigDecimal totalGeneral = BigDecimal.ZERO;

        for (BulletinPaie bulletin : bulletins) {
            BigDecimal salaireBrut = bulletin.getSalaireBrut() != null ? bulletin.getSalaireBrut() : BigDecimal.ZERO;
            BigDecimal salaireBrutArrondi = bulletin.getSalaireBrutArrondi() != null ? bulletin.getSalaireBrutArrondi() : BigDecimal.ZERO;
            BigDecimal ipts = bulletin.getMontantIpts() != null ? bulletin.getMontantIpts() : BigDecimal.ZERO;
            BigDecimal vps = bulletin.getMontantVps() != null ? bulletin.getMontantVps() : BigDecimal.ZERO;
            BigDecimal total = ipts.add(vps);

            // Ajouter aux totaux
            totalSalaireBrut = totalSalaireBrut.add(salaireBrut);
            totalSalaireBrutArrondi = totalSalaireBrutArrondi.add(salaireBrutArrondi);
            totalIpts = totalIpts.add(ipts);
            totalVps = totalVps.add(vps);
            totalGeneral = totalGeneral.add(total);

            lignes.add(new BilanChargeSocialeExportDTO(
                    bulletin.getMois(),
                    moisDebut,
                    moisFin,
                    salaireBrut,
                    salaireBrutArrondi,
                    ipts,
                    vps,
                    total
            ));
        }

        // Créer l'objet totaux
        BilanChargeSocialeTotalDTO totaux = new BilanChargeSocialeTotalDTO(
                totalSalaireBrut,
                totalSalaireBrutArrondi,
                totalIpts,
                totalVps,
                totalGeneral
        );

        // Déterminer le titre de la période
        String titrePeriode;
        if (mois != null) {
            titrePeriode = "Mois: " + mois;
        } else {
            titrePeriode = "Période: " + moisDebut + " à " + moisFin;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("lignes", lignes);
        result.put("totaux", totaux);
        result.put("titrePeriode", titrePeriode);
        result.put("employeNom", employeNom);
        result.put("entrepriseNom", entrepriseNom);
        result.put("mois", mois);
        result.put("moisDebut", moisDebut);
        result.put("moisFin", moisFin);
        return result;

    } catch (Exception e) {
        System.err.println("Erreur lors de la récupération des données charge sociale employé: " + e.getMessage());
        return createDefaultExportDataBilanChargeSocialeEmploye();
    }
}
    private Map<String, Object> createDefaultExportDataBilanChargeSocialeEmploye() {
        Map<String, Object> result = new HashMap<>();
        result.put("lignes", new ArrayList<>());
        result.put("totaux", new BilanChargeSocialeTotalDTO(
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
        ));
        result.put("titrePeriode", "Période non spécifiée");
        result.put("employeNom", "Employé non trouvé");
        result.put("entrepriseNom", "Entreprise non trouvée");
        return result;
    }

    private Map<String, Object> createPdfParametersBilanChargeSocialeEmploye(Map<String, Object> data) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("titrePeriode", data.get("titrePeriode"));
        parameters.put("employe", data.get("employeNom"));
        parameters.put("entreprise", data.get("entrepriseNom"));
        parameters.put("employeur", "Talents Plus ETT");
        parameters.put("numeroEmployeur", "123456789");
        return parameters;
    }


    private JasperReport compileBilanChargeSocialeEmployeTemplate() throws JRException {
        String template = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jasperReport xmlns=\"http://jasperreports.sourceforge.net/jasperreports\"\n" +
                "              xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "              xsi:schemaLocation=\"http://jasperreports.sourceforge.net/jasperreports\n" +
                "                                  http://jasperreports.sourceforge.net/xsd/jasperreport.xsd\"\n" +
                "              name=\"bilan_charge_sociale_employe\"\n" +
                "              pageWidth=\"842\"\n" +
                "              pageHeight=\"595\"\n" +
                "              orientation=\"Landscape\"\n" +
                "              columnWidth=\"802\"\n" +
                "              leftMargin=\"20\"\n" +
                "              rightMargin=\"20\"\n" +
                "              topMargin=\"20\"\n" +
                "              bottomMargin=\"20\"\n" +
                "              isFloatColumnFooter=\"true\">\n" +
                "\n" +
                "    <!-- Style pour les bordures - DOIT ÊTRE AVANT LES PARAMETERS -->\n" +
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
                "    <parameter name=\"titrePeriode\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"employe\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"entreprise\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"employeur\" class=\"java.lang.String\"/>\n" +
                "    <parameter name=\"numeroEmployeur\" class=\"java.lang.String\"/>\n" +
                "    \n" +
                "    <field name=\"mois\" class=\"java.lang.String\"/>\n" +
                "    <field name=\"salaireBrut\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"salaireBrutArrondi\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"montantIpts\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"montantVps\" class=\"java.math.BigDecimal\"/>\n" +
                "    <field name=\"total\" class=\"java.math.BigDecimal\"/>\n" +
                "\n" +
                "    <variable name=\"salaireBrut_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{salaireBrut}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    \n" +
                "    <variable name=\"salaireBrutArrondi_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{salaireBrutArrondi}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    \n" +
                "    <variable name=\"montantIpts_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{montantIpts}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    \n" +
                "    <variable name=\"montantVps_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{montantVps}]]></variableExpression>\n" +
                "    </variable>\n" +
                "    \n" +
                "    <variable name=\"total_SUM\" class=\"java.math.BigDecimal\" calculation=\"Sum\">\n" +
                "        <variableExpression><![CDATA[$F{total}]]></variableExpression>\n" +
                "    </variable>\n" +
                "\n" +
                "    <title>\n" +
                "        <band height=\"100\" splitType=\"Stretch\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"802\" height=\"30\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"16\" isBold=\"true\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[BILAN DES CHARGES SOCIALES - PAR EMPLOYÉ]]></text>\n" +
                "            </staticText>\n" +
                "            \n" +
                "            <textField>\n" +
                "                <reportElement x=\"0\" y=\"35\" width=\"802\" height=\"20\"/>\n" +
                "                <textElement textAlignment=\"Center\">\n" +
                "                    <font isBold=\"true\" size=\"12\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$P{titrePeriode}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            \n" +
                "            <textField>\n" +
                "                <reportElement x=\"0\" y=\"55\" width=\"802\" height=\"20\"/>\n" +
                "                <textElement textAlignment=\"Center\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[\"Employé : \" + $P{employe} + \" - Entreprise : \" + $P{entreprise}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "        </band>\n" +
                "    </title>\n" +
                "\n" +
                "    <columnHeader>\n" +
                "        <band height=\"30\" splitType=\"Stretch\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"120\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\" pdfFontName=\"Helvetica-Bold\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Mois]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"120\" y=\"0\" width=\"140\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\" pdfFontName=\"Helvetica-Bold\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Salaire Brut]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"260\" y=\"0\" width=\"140\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\" pdfFontName=\"Helvetica-Bold\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Salaire Arrondi]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"400\" y=\"0\" width=\"120\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\" pdfFontName=\"Helvetica-Bold\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[IPTS]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"520\" y=\"0\" width=\"120\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\" pdfFontName=\"Helvetica-Bold\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[VPS]]></text>\n" +
                "            </staticText>\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"640\" y=\"0\" width=\"142\" height=\"30\" style=\"TableHeader\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\" pdfFontName=\"Helvetica-Bold\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[Total IPTS+VPS]]></text>\n" +
                "            </staticText>\n" +
                "        </band>\n" +
                "    </columnHeader>\n" +
                "\n" +
                "    <detail>\n" +
                "        <band height=\"25\" splitType=\"Stretch\">\n" +
                "            <textField>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"120\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{mois}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"120\" y=\"0\" width=\"140\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{salaireBrut} != null ? $F{salaireBrut} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"260\" y=\"0\" width=\"140\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{salaireBrutArrondi} != null ? $F{salaireBrutArrondi} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"400\" y=\"0\" width=\"120\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{montantIpts} != null ? $F{montantIpts} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"520\" y=\"0\" width=\"120\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{montantVps} != null ? $F{montantVps} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField>\n" +
                "                <reportElement x=\"640\" y=\"0\" width=\"142\" height=\"25\" style=\"TableData\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font size=\"9\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$F{total} != null ? $F{total} : 0]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "        </band>\n" +
                "    </detail>\n" +
                "\n" +
                "    <summary>\n" +
                "        <band height=\"35\">\n" +
                "            <staticText>\n" +
                "                <reportElement x=\"0\" y=\"0\" width=\"120\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <text><![CDATA[TOTAL]]></text>\n" +
                "            </staticText>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"120\" y=\"0\" width=\"140\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{salaireBrut_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"260\" y=\"0\" width=\"140\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{salaireBrutArrondi_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"400\" y=\"0\" width=\"120\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{montantIpts_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"520\" y=\"0\" width=\"120\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{montantVps_SUM}]]></textFieldExpression>\n" +
                "            </textField>\n" +
                "            <textField evaluationTime=\"Report\">\n" +
                "                <reportElement x=\"640\" y=\"0\" width=\"142\" height=\"30\" style=\"TableTotal\"/>\n" +
                "                <textElement textAlignment=\"Center\" verticalAlignment=\"Middle\">\n" +
                "                    <font isBold=\"true\" size=\"10\"/>\n" +
                "                </textElement>\n" +
                "                <textFieldExpression><![CDATA[$V{total_SUM}]]></textFieldExpression>\n" +
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
                "                <text><![CDATA[Aucune donnée disponible pour la période sélectionnée]]></text>\n" +
                "            </staticText>\n" +
                "        </band>\n" +
                "    </noData>\n" +
                "</jasperReport>";

        return JasperCompileManager.compileReport(new java.io.ByteArrayInputStream(template.getBytes()));
    }

    private void createExcelHeaderBilanChargeSocialeEmploye(Sheet sheet, CellStyle headerStyle, Map<String, Object> data) {
        // Ligne 0: Titre
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BILAN DES CHARGES SOCIALES - PAR EMPLOYÉ");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5)); // 6 colonnes au total

        // Ligne 1: Informations période, employé et entreprise
        Row infoRow = sheet.createRow(1);
        createCell(infoRow, 0,
                data.get("titrePeriode") + " - Employé: " + data.get("employeNom") + " - Entreprise: " + data.get("entrepriseNom"),
                headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5)); // 6 colonnes au total

        // Ligne 2: En-têtes des colonnes
        Row headerRow = sheet.createRow(2);
        headerRow.setHeightInPoints(25);

        String[] headers = {
                "Mois", "Salaire Brut", "Salaire Brut Arrondi", "IPTS", "VPS", "Total IPTS+VPS"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillExcelDataBilanChargeSocialeEmploye(Sheet sheet, CellStyle dataStyle, CellStyle numberStyle,
                                                        List<BilanChargeSocialeExportDTO> lignes) {
        int rowNum = 3; // Commence à la ligne 3 (après l'en-tête)

        for (BilanChargeSocialeExportDTO ligne : lignes) {
            Row row = sheet.createRow(rowNum++);
            row.setHeightInPoints(20);

            createCell(row, 0, ligne.getMois(), dataStyle);
            createNumericCell(row, 1, ligne.getSalaireBrut(), numberStyle);
            createNumericCell(row, 2, ligne.getSalaireBrutArrondi(), numberStyle);
            createNumericCell(row, 3, ligne.getMontantIpts(), numberStyle);
            createNumericCell(row, 4, ligne.getMontantVps(), numberStyle);
            createNumericCell(row, 5, ligne.getTotal(), numberStyle);
        }
    }

    private void addExcelTotalsBilanChargeSocialeEmploye(Sheet sheet, CellStyle totalStyle, CellStyle numberStyle,
                                                         BilanChargeSocialeTotalDTO totaux, int dataSize) {
        int totalRowNum = dataSize + 3; // Après les données
        Row totalRow = sheet.createRow(totalRowNum);
        totalRow.setHeightInPoints(25);

        // Cellule TOTAL - pas de fusion nécessaire
        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("TOTAL");
        totalLabelCell.setCellStyle(totalStyle);

        // Cellules de totaux numériques - alignées avec les en-têtes
        createNumericCell(totalRow, 1, totaux.getTotalSalaireBrut(), totalStyle);
        createNumericCell(totalRow, 2, totaux.getTotalSalaireBrutArrondi(), totalStyle);
        createNumericCell(totalRow, 3, totaux.getTotalIpts(), totalStyle);
        createNumericCell(totalRow, 4, totaux.getTotalVps(), totalStyle);
        createNumericCell(totalRow, 5, totaux.getTotalGeneral(), totalStyle);
    }
    private void autoSizeColumnsBilanChargeSocialeEmploye(Sheet sheet) {
        for (int i = 0; i < 6; i++) { // 6 colonnes au total
            sheet.autoSizeColumn(i);
            if (sheet.getColumnWidth(i) < 3000) {
                sheet.setColumnWidth(i, 4000);
            }
        }
    }


}