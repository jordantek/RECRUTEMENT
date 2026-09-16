package com.tpc.tpcgestpaie.localapp.controller.dashboard;

import com.tpc.tpcgestpaie.localapp.dto.dashboard.*;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.service.CompanyService;
import com.tpc.tpcgestpaie.localapp.service.dashboard.DashboardService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final CompanyService companyService;
    private final CompanyRepository companyRepository;

    public DashboardController(DashboardService dashboardService, CompanyService companyService, CompanyRepository companyRepository) {
        this.dashboardService = dashboardService;
        this.companyService = companyService;
        this.companyRepository = companyRepository;
    }

    @GetMapping("/overview/{entrepriseId}")
    public ResponseEntity<ApiResponse<DashboardEffectifOverviewDTO>> getOverview(@PathVariable Long entrepriseId) {
        if (!companyService.existsById(entrepriseId))
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise introuvable", null));

        DashboardEffectifOverviewDTO dto = dashboardService.getOverview(entrepriseId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Aperçu de l'effectif", dto));
    }

    @GetMapping("/masse-salariale/{entrepriseId}")
    public ResponseEntity<ApiResponse<DashboardMasseSalarialeDTO>> getMasseSalariale(@PathVariable Long entrepriseId) {
        if (!companyService.existsById(entrepriseId))
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise introuvable", null));

        DashboardMasseSalarialeDTO masse = dashboardService.getMasseSalarialeAnnuelle(entrepriseId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Masse salariale annuelle", masse));
    }

    @GetMapping("/salaire-moyen/{entrepriseId}")
    public ResponseEntity<ApiResponse<SalaireMoyenSexeDTO>> getSalaireMoyenParSexe(@PathVariable Long entrepriseId) {
        if (!companyService.existsById(entrepriseId))
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise introuvable", null));

        SalaireMoyenSexeDTO dto = dashboardService.getSalaireMoyenParSexe(entrepriseId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Salaire moyen par sexe", dto));
    }

    @GetMapping("/repartition-departement/{entrepriseId}")
    public ResponseEntity<ApiResponse<RepartitionDepartementDTO>> getRepartitionParDepartement(@PathVariable Long entrepriseId) {
        if (!companyService.existsById(entrepriseId))
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise introuvable", null));

        RepartitionDepartementDTO dto = dashboardService.getRepartitionParDepartement(entrepriseId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Répartition par département", dto));
    }

    @GetMapping("/repartition-type-contrat/{entrepriseId}")
    public ResponseEntity<ApiResponse<RepartitionTypeContratDTO>> getRepartitionParTypeContrat(@PathVariable Long entrepriseId) {
        if (!companyService.existsById(entrepriseId))
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise introuvable", null));

        RepartitionTypeContratDTO dto = dashboardService.getRepartitionParTypeContrat(entrepriseId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Répartition par type de contrat", dto));
    }
    @GetMapping("/repartition-situation-famille/{entrepriseId}")
    public ResponseEntity<ApiResponse<RepartitionSituationFamilleDTO>> getRepartitionSituationFamille(@PathVariable Long entrepriseId) {
        if (!companyService.existsById(entrepriseId))
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise introuvable", null));

        RepartitionSituationFamilleDTO dto = dashboardService.getRepartitionSituationFamille(entrepriseId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Répartition par situation de famille", dto));
    }

    @GetMapping("/repartition-anciennete/{entrepriseId}")
    public ResponseEntity<ApiResponse<RepartitionAncienneteDTO>> getRepartitionParAnciennete(@PathVariable Long entrepriseId) {
        if (!companyService.existsById(entrepriseId))
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise introuvable", null));

        RepartitionAncienneteDTO dto = dashboardService.getRepartitionParAnciennete(entrepriseId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Répartition par ancienneté", dto));
    }

    @GetMapping("/absences/{companyId}")
    public ResponseEntity<?> getAbsencesDashboard(@PathVariable Long companyId) {
        try {
            if (!companyService.existsById(companyId))
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise introuvable", null));

            AbsencesDashboardDTO dashboard = dashboardService.getDashboardAbsences(companyId);

            if (dashboard == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Aucune donnée trouvée pour l'entreprise ID: " + companyId);
            }

            return ResponseEntity.ok(new ApiResponse<>(true, "Répartition par ancienneté", dashboard));

        } catch (Exception e) {
            // Log l'erreur pour debug
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération des données : " + e.getMessage());
        }
    }

    @GetMapping("/paie/{companyId}")
    public ResponseEntity<?> getPaieDashboard(@PathVariable Long companyId) {
        try {
        // 🔹 Vérification que l'entreprise existe
        Company company = companyRepository.findById(companyId).orElse(null);
        if (company == null) {
            return ResponseEntity.badRequest()
                    .body("Entreprise avec l'ID " + companyId + " introuvable.");
        }

        // 🔹 Récupération du dashboard
        PaieDashboardDTO dashboard = dashboardService.getDashboardPaie(companyId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard paie", dashboard));
    } catch (Exception e) {
        // Log l'erreur pour debug
        e.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la récupération des données : " + e.getMessage());
    }
    }

    @GetMapping("/kpis/{companyId}")
    public ResponseEntity<ApiResponse<KpiDashboardDTO>> getKpiDashboard(@PathVariable Long companyId) {
        if (!companyService.existsById(companyId)) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Entreprise introuvable", null));
        }

        KpiDashboardDTO dto = dashboardService.getDashboardKpis(companyId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard KPIs", dto));
    }

}