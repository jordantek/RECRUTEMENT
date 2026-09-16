package com.tpc.tpcgestpaie.localapp.controller.etat;

import com.tpc.tpcgestpaie.localapp.dto.etat.BilanChargeSocialeResponse;
import com.tpc.tpcgestpaie.localapp.dto.etat.BilanFiscaleResponse;
import com.tpc.tpcgestpaie.localapp.dto.etat.SalaireMensuelResponse;
import com.tpc.tpcgestpaie.localapp.dto.etat.TotauxParBanqueResponse;
import com.tpc.tpcgestpaie.localapp.repository.BanqueRepository;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.service.etat.BilanChargeSocialeService;
import com.tpc.tpcgestpaie.localapp.service.etat.EtatChargeSocialeService;
import com.tpc.tpcgestpaie.localapp.service.paie.bulletin.BulletinPaieService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/etat-charges")
public class EtatChargeSocialeController {

    private final EtatChargeSocialeService etatChargeSocialeService;
    private final BulletinPaieService bulletinPaieService;
    private final BanqueRepository banqueRepository;
    private final BilanChargeSocialeService bilanChargeSocialeService;
    private final EmployeRepository employeRepository;
    private final CompanyRepository companyRepository;

    // ✅ Injection par constructeur (recommandée)
    public EtatChargeSocialeController(EtatChargeSocialeService etatChargeSocialeService, BulletinPaieService bulletinPaieService, BanqueRepository banqueRepository, BilanChargeSocialeService bilanChargeSocialeService, EmployeRepository employeRepository, CompanyRepository companyRepository) {
        this.etatChargeSocialeService = etatChargeSocialeService;
        this.bulletinPaieService = bulletinPaieService;
        this.banqueRepository = banqueRepository;
        this.bilanChargeSocialeService = bilanChargeSocialeService;
        this.employeRepository = employeRepository;
        this.companyRepository = companyRepository;
    }

    @GetMapping("/bilan-mensuel/{companyId}/{mois}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBilanMensuel(
            @PathVariable Long companyId,
            @PathVariable String mois) {

        Map<String, Object> data = etatChargeSocialeService.getBilanMensuelCharges(companyId, mois);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Bilan mensuel des charges sociales", data)
        );
    }

    @GetMapping("/bilan-periodique/{companyId}/{employeId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBilanPeriodique(
            @PathVariable Long companyId,
            @PathVariable Long employeId,
            @RequestParam("debut") String debut,
            @RequestParam("fin") String fin
    ) {
        Map<String, Object> data = etatChargeSocialeService.getBilanPeriodiqueCharges(companyId, employeId, debut, fin);
        return ResponseEntity.ok(new ApiResponse<>(true, "Bilan périodique des charges sociales", data));
    }

    @GetMapping("/bilan-periodique/entreprise/{companyId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBilanPeriodiqueParEntreprise(
            @PathVariable Long companyId,
            @RequestParam("debut") String debut,
            @RequestParam("fin") String fin
    ) {
        Map<String, Object> data = etatChargeSocialeService.getBilanPeriodiqueParEntreprise(companyId, debut, fin);
        return ResponseEntity.ok(new ApiResponse<>(true, "Bilan périodique des charges sociales par entreprise", data));
    }

    @GetMapping("/salaire-net-par-banque")
    public ResponseEntity<?> getTotauxParBanqueEtMois(
            @RequestParam Long banqueId,
            @RequestParam String mois
    ) {

        try {
            // Vérifie si la banque existe
            if (!banqueRepository.existsById(banqueId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Banque introuvable", null));
            }

            // Appel du service
            TotauxParBanqueResponse response = etatChargeSocialeService.getTotauxParBanqueEtMois(banqueId,mois);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Totaux calculés avec succès", response)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors du calcul des totaux : " + e.getMessage(), null));
        }
    }

    @GetMapping("/charge-sociale-par-employe")
    public ResponseEntity<?> getBilanChargeSociale(
            @RequestParam Long idEmploye,
            @RequestParam(required = false) String mois,
            @RequestParam(required = false) String moisDebut,
            @RequestParam(required = false) String moisFin
    ) {
        try {
            // Vérifie que l'employé existe
            if (!employeRepository.existsById(idEmploye)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Employé introuvable", null));
            }

            // Vérifie qu'on a soit un mois, soit une période complète
            if (mois == null && (moisDebut == null || moisFin == null)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Veuillez fournir soit un mois, soit une période (moisDebut et moisFin)", null));
            }

            // Appel du service
            BilanChargeSocialeResponse response = bilanChargeSocialeService.getBilan(idEmploye, mois, moisDebut, moisFin);

            return ResponseEntity.ok(new ApiResponse<>(true, "Bilan calculé avec succès", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors du calcul du bilan : " + e.getMessage(), null));
        }
    }

    @GetMapping("/mensuel-par-entreprise")
    public ResponseEntity<?> getSalaireMensuelParEntreprise(
            @RequestParam Long idEntreprise,
            @RequestParam String mois
    ) {
        try {
            if (!companyRepository.existsById(idEntreprise)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise introuvable", null));
            }

            SalaireMensuelResponse response = bilanChargeSocialeService.getSalaireMensuel(idEntreprise, mois);
            return ResponseEntity.ok(new ApiResponse<>(true, "Salaire mensuel récupéré avec succès", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors du calcul des salaires : " + e.getMessage(), null));
        }
    }

    @GetMapping("/charge-fiscale-par-entreprise")
    public ResponseEntity<?> getBilanFiscal(
            @RequestParam Long idEntreprise,
            @RequestParam(required = false) String mois,
            @RequestParam(required = false) String moisDebut,
            @RequestParam(required = false) String moisFin
    ) {
        try {
            if (!companyRepository.existsById(idEntreprise)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise introuvable", null));
            }

            if (mois == null && (moisDebut == null || moisFin == null)) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "Veuillez fournir soit un mois, soit une période complète", null));
            }

            BilanFiscaleResponse response = etatChargeSocialeService.getBilan(idEntreprise, mois, moisDebut, moisFin);

            return ResponseEntity.ok(new ApiResponse<>(true, "Bilan fiscal calculé avec succès", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors du calcul du bilan fiscal : " + e.getMessage(), null));
        }
    }

    @GetMapping("/fiscale-periodique-par-entreprise")
    public ResponseEntity<?> getSalairePeriodique(
            @RequestParam Long idEntreprise,
            @RequestParam String moisDebut,
            @RequestParam String moisFin
    ) {
        try {
            if (!companyRepository.existsById(idEntreprise)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Entreprise introuvable", null));
            }

            SalaireMensuelResponse response = etatChargeSocialeService.getSalairePeriodique(idEntreprise, moisDebut, moisFin);

            return ResponseEntity.ok(new ApiResponse<>(true, "Salaire périodique récupéré avec succès", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors du calcul des salaires : " + e.getMessage(), null));
        }
    }

}
