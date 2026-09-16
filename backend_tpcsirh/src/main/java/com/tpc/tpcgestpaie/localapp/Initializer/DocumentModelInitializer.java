package com.tpc.tpcgestpaie.localapp.Initializer;

import com.tpc.tpcgestpaie.localapp.model.DocumentModel;
import com.tpc.tpcgestpaie.localapp.model.DocumentsCategory;
import com.tpc.tpcgestpaie.localapp.repository.DocumentModelRepository;
import com.tpc.tpcgestpaie.localapp.repository.DocumentsCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DocumentModelInitializer implements CommandLineRunner {

    private final DocumentModelRepository modelRepository;
    private final DocumentsCategoryRepository categoryRepository;

    @Override
    public void run(String... args) {

        createModel(1L, "Carte nationale d'identité", "CNI-001", 1L, true);
        createModel(2L, "Passeport", "PAS-001", 1L, true);
        createModel(3L, "Permis de conduire", "DRV-001", 1L, true);

        createModel(4L, "Contrat CDI", "CTR-CDI", 2L, true);
        createModel(5L, "Contrat CDD", "CTR-CDD", 2L, true);
        createModel(6L, "Contrat Stage", "CTR-STG", 2L, true);

        createModel(7L, "Diplôme principal", "DIP-001", 3L, true);
        createModel(8L, "Certificat formation", "DIP-FRM", 3L, true);

        createModel(9L, "Certificat médical", "MED-CER", 4L, false);
        createModel(10L, "Certificat aptitude", "MED-APT", 4L, false);

        createModel(11L, "Bulletin de paie", "PAY-SLP", 5L, false);
        createModel(12L, "Historique de paie", "PAY-HIS", 5L, false);

        createModel(13L, "Attestation travail", "ADM-ATT", 6L, true);
        createModel(14L, "Lettre administrative", "ADM-LTR", 6L, false);

        createModel(15L, "Demande de congé", "LEV-REQ", 7L, false);
        createModel(16L, "Justificatif absence", "LEV-JST", 7L, false);

        createModel(17L, "Avertissement disciplinaire", "DSC-WRN", 8L, false);
        createModel(18L, "Sanction disciplinaire", "DSC-SAN", 8L, false);

        createModel(19L, "Déclaration fiscale employé", "TAX-DEC", 9L, false);

        createModel(20L, "Lettre démission", "END-DMS", 10L, false);
        createModel(21L, "Lettre licenciement", "END-LCN", 10L, false);
        createModel(22L, "Solde tout compte", "END-STC", 10L, false);

        createModel(23L, "Photo employé", "EMP-PHO", 6L, true);
        createModel(24L, "CV employé", "EMP-CV", 6L, true);
        createModel(25L, "Lettre motivation", "EMP-LTR", 6L, false);

        System.out.println("✅ Modèles de documents RH initialisés (avec catégories par ID)");
    }

    private void createModel(Long id, String name, String code, Long categoryId, Boolean visibleOnProfile) {

        if (modelRepository.existsByCode(code)) {
            return;
        }

        DocumentsCategory category = categoryRepository.findById(categoryId).orElse(null);

        if (category == null) {
            System.out.println("⚠️ La catégorie ID " + categoryId + " n'existe pas, le modèle " + code + " n'a pas été créé.");
            return;
        }

        DocumentModel model = DocumentModel.builder()
               // .id(id)
                .name(name)
                .code(code)
                .description(name)
                .categoryId(category.getId())
                .categoryName(category.getName())
                .isActive(true)
                .ocrEnabled(false)
                .aiExtraction(false)
                .showOnEmployeeProfile(visibleOnProfile)
                .companyId(null)
                .build();

        modelRepository.save(model);
    }
}