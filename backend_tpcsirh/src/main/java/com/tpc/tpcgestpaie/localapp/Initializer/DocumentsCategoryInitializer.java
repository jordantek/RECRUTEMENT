package com.tpc.tpcgestpaie.localapp.Initializer;

import com.tpc.tpcgestpaie.localapp.model.DocumentsCategory;
import com.tpc.tpcgestpaie.localapp.repository.DocumentsCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DocumentsCategoryInitializer implements CommandLineRunner {

    private final DocumentsCategoryRepository repository;

    @Override
    public void run(String... args) {

        if (repository.count() > 0) {
            return;
        }

        List<DocumentsCategory> categories = List.of(

                DocumentsCategory.builder()
                        .name("Pièces d'identité")
                        .code("IDN-001")
                        .description("Carte d'identité, passeport, permis")
                        .color("#2563EB")
                        .retentionPeriod(10)
                        .companyId(null)
                        .isActive(true)
                        .build(),

                DocumentsCategory.builder()
                        .name("Contrats de travail")
                        .code("CTR-001")
                        .description("Contrats et documents d'embauche")
                        .color("#059669")
                        .retentionPeriod(10)
                        .companyId(null)
                        .isActive(true)
                        .build(),

                DocumentsCategory.builder()
                        .name("Diplômes et formations")
                        .code("DIP-001")
                        .description("Diplômes et certificats")
                        .color("#7C3AED")
                        .retentionPeriod(20)
                        .companyId(null)
                        .isActive(true)
                        .build(),

                DocumentsCategory.builder()
                        .name("Documents médicaux")
                        .code("MED-001")
                        .description("Certificats médicaux et aptitude")
                        .color("#DC2626")
                        .retentionPeriod(5)
                        .companyId(null)
                        .isActive(true)
                        .build(),

                DocumentsCategory.builder()
                        .name("Bulletins de paie")
                        .code("PAY-001")
                        .description("Fiches de paie et historique")
                        .color("#EA580C")
                        .retentionPeriod(10)
                        .companyId(null)
                        .isActive(true)
                        .build(),

                DocumentsCategory.builder()
                        .name("Documents administratifs")
                        .code("ADM-001")
                        .description("Attestations et correspondances RH")
                        .color("#0891B2")
                        .retentionPeriod(10)
                        .companyId(null)
                        .isActive(true)
                        .build(),

                DocumentsCategory.builder()
                        .name("Congés et absences")
                        .code("LEV-001")
                        .description("Demandes de congés et justificatifs")
                        .color("#9333EA")
                        .retentionPeriod(5)
                        .companyId(null)
                        .isActive(true)
                        .build(),

                DocumentsCategory.builder()
                        .name("Discipline et sanctions")
                        .code("DSC-001")
                        .description("Avertissements et sanctions")
                        .color("#B91C1C")
                        .retentionPeriod(5)
                        .companyId(null)
                        .isActive(true)
                        .build(),

                DocumentsCategory.builder()
                        .name("Documents fiscaux")
                        .code("TAX-001")
                        .description("Documents fiscaux et sociaux")
                        .color("#0D9488")
                        .retentionPeriod(10)
                        .companyId(null)
                        .isActive(true)
                        .build(),

                DocumentsCategory.builder()
                        .name("Fin de contrat")
                        .code("END-001")
                        .description("Démission, licenciement, solde de tout compte")
                        .color("#6B7280")
                        .retentionPeriod(10)
                        .companyId(null)
                        .isActive(true)
                        .build()
        );

        repository.saveAll(categories);

        System.out.println("✅ Catégories de documents RH initialisées");
    }
}