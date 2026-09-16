package com.tpc.tpcgestpaie.localapp.controller;

import com.tpc.tpcgestpaie.localapp.CalculUtils.OvertimeCalculator;
import com.tpc.tpcgestpaie.localapp.CalculUtils.OvertimeCalculatorResultat;
import com.tpc.tpcgestpaie.localapp.CalculUtils.SalaryCalculator;
import com.tpc.tpcgestpaie.localapp.CalculUtils.SalaryCalculatorResult;
import com.tpc.tpcgestpaie.localapp.dto.accessoire.HistoriqueMontantsDTO;
import com.tpc.tpcgestpaie.localapp.repository.BanqueRepository;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.bulletin.BulletinPaieRepository;
import com.tpc.tpcgestpaie.localapp.service.EmployeService;
import com.tpc.tpcgestpaie.localapp.service.TraitementSalaireService;
import com.tpc.tpcgestpaie.localapp.service.bulletun.GenerateBulletin;
import com.tpc.tpcgestpaie.localapp.service.emailConfig.EmailConfigService;
import com.tpc.tpcgestpaie.localapp.service.paie.MontantRubriqueService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import com.tpc.tpcgestpaie.localapp.util.CountryUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/test")
public class TestController {

    private final MontantRubriqueService montantRubriqueService;
    private final BulletinPaieRepository bulletinPaieRepository;
    private final BanqueRepository banqueRepository;
    private final CompanyRepository companyRepository;
    private final TraitementSalaireService traitementSalaireService;
    private final GenerateBulletin generateBulletin;
    private final ContratEmployeRepository contratEmployeRepository;
    private final EmployeService employeService;
    private final EmployeRepository employeRepository;
    private final EmailConfigService emailConfigService;

    public TestController(MontantRubriqueService montantRubriqueService, BulletinPaieRepository bulletinPaieRepository, BanqueRepository banqueRepository, CompanyRepository companyRepository, TraitementSalaireService traitementSalaireService, GenerateBulletin generateBulletin, ContratEmployeRepository contratEmployeRepository, EmployeService employeService, EmployeRepository employeRepository, EmailConfigService emailConfigService) {
        this.montantRubriqueService = montantRubriqueService;
        this.bulletinPaieRepository = bulletinPaieRepository;
        this.banqueRepository = banqueRepository;
        this.companyRepository = companyRepository;
        this.traitementSalaireService = traitementSalaireService;
        this.generateBulletin = generateBulletin;
        this.contratEmployeRepository = contratEmployeRepository;
        this.employeService = employeService;
        this.employeRepository = employeRepository;
        this.emailConfigService = emailConfigService;
    }

    @GetMapping("/testendpoint")
    public String testApi() {

        String code = CountryUtils.getCodeFromName("Bénin", Locale.FRENCH);
        System.out.println(code);
        return "API Spring Boot fonctionne !";
    }



    @GetMapping("/logo/{filename:.+}")
    public ResponseEntity<Resource> getLogo(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(System.getProperty("user.dir"), "uploads/logo", filename);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG) // si tu veux gérer PNG/JPG dynamiquement je peux te montrer
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        OvertimeCalculator calculator = new OvertimeCalculator(0, 0, 3, 3, 108714);
        OvertimeCalculatorResultat result = calculator.calculateOvertime();
        return ResponseEntity.ok("Test Overtime: " + result.getTotal() + "***********************Percent12: " + result.getRate12Percent() + "********************Percent35: " + result.getRate35Percent() + " ***************Percent50: " + result.getRate50Percent() + "********* Percent100: " + result.getRate100Percent());
    }

    @GetMapping("/overtime")
    public ResponseEntity<String> testOvertime() {
        OvertimeCalculator calculator = new OvertimeCalculator(0, 0, 3, 3, 108714);
        OvertimeCalculatorResultat result = calculator.calculateOvertime();

        StringBuilder response = new StringBuilder();
        response.append("=== Exemple de calcul des heures supplémentaires ===\n\n");
        response.append("🔢 Paramètres d'entrée :\n");
        response.append(" - Heures jour (41 à 48) : 0\n");
        response.append(" - Heures jour (> 48)   : 0\n");
        response.append(" - Heures dimanche/jour férié : 3\n");
        response.append(" - Heures nuit dimanche/jour férié : 3\n");
        response.append(" - Salaire brut : 108714\n\n");

        response.append("🧮 Résultat du calcul :\n");
        response.append(" - Majoration 12%   : ").append(result.getRate12Percent()).append("\n");
        response.append(" - Majoration 35%   : ").append(result.getRate35Percent()).append("\n");
        response.append(" - Majoration 50%   : ").append(result.getRate50Percent()).append("\n");
        response.append(" - Majoration 100%  : ").append(result.getRate100Percent()).append("\n");
        response.append(" - 💰 Total         : ").append(result.getTotal()).append("\n");

        return ResponseEntity.ok(response.toString());
    }

    @GetMapping("/calcul-salaire")
    public ResponseEntity<?> calculeSalaire1() {
        try {
            double salaireContrat = 229097.0;
            double bonus =0.0;
            double heuresSup = 0;

            // Instanciation du calculateur
            SalaryCalculator salaryCalculator = new SalaryCalculator(0.04, 0.04, 0.09, 0.064,30);

            SalaryCalculatorResult result = salaryCalculator.salaryCalculate(salaireContrat,bonus);

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Calcul de salaire effectué avec succès",
                    result
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(
                            "Erreur lors du calcul de salaire",
                            "VALIDATION_ERROR",
                            List.of(e.getMessage())
                    ));
        }
    }

    @GetMapping("/calcul-salaire/with-heures-sup")
    public ResponseEntity<?> calculeSalaire2() {
        try {
            double salaireContrat = 260000.0;
            double bonus =87252.0;
            double heuresSup = 48592;


            // Instanciation du calculateur
            SalaryCalculator salaryCalculator = new SalaryCalculator(0.04, 0.01, 0.09, 0.064,30);

            SalaryCalculatorResult result = salaryCalculator.salaryCalculateWithHeuresSup(salaireContrat,bonus,heuresSup);

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Calcul de salaire effectué avec succès",
                    result
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(
                            "Erreur lors du calcul de salaire",
                            "VALIDATION_ERROR",
                            List.of(e.getMessage())
                    ));
        }
    }

    @GetMapping("/calcul-salaire/with-heures-sup/and-working-days")
    public ResponseEntity<?> calculeSalaire3() {
        try {
            double salaireContrat = 261162;
            double bonus =0.0;
            double heuresSup = 0;
            int nbrWorkingDays = 30;

            // Instanciation du calculateur
            SalaryCalculator salaryCalculator = new SalaryCalculator(0.04, 0.01, 0.09, 0.064,30);

            SalaryCalculatorResult result = salaryCalculator.salaryCalculateWithHeuresSupAndWorkingDays(salaireContrat,bonus,heuresSup,nbrWorkingDays);

            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Calcul de salaire effectué avec succès",
                    result
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(
                            "Erreur lors du calcul de salaire",
                            "VALIDATION_ERROR",
                            List.of(e.getMessage())
                    ));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> checkStatus() {
        return ResponseEntity.ok("The application is running fine.");
    }

    @PostMapping("/echo")
    public ResponseEntity<String> echoMessage(@RequestBody String message) {
        return ResponseEntity.ok("You sent: " + message);
    }

    @GetMapping("/calcul/12mois")
    public ResponseEntity<HistoriqueMontantsDTO> calculerIndemnite(
            @RequestParam Long idContratEmploye,
            @RequestParam String mois
    ) {

        HistoriqueMontantsDTO result = montantRubriqueService
                .calculerSalaireMoyens12Mois(idContratEmploye, mois);

        return ResponseEntity.ok(result);
    }
}
