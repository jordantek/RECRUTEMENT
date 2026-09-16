package com.tpc.tpcgestpaie.localapp.service.etat;

import com.tpc.tpcgestpaie.localapp.dto.etat.*;
import com.tpc.tpcgestpaie.localapp.model.Banque;
import com.tpc.tpcgestpaie.localapp.model.BulletinPaie;
import com.tpc.tpcgestpaie.localapp.repository.BanqueRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.bulletin.BulletinPaieRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EtatChargeSocialeService {

    private final BulletinPaieRepository bulletinPaieRepository;
    private final BanqueRepository banqueRepository;

    // constructeur pour injection
    public EtatChargeSocialeService(BulletinPaieRepository bulletinPaieRepository, BanqueRepository banqueRepository) {
        this.bulletinPaieRepository = bulletinPaieRepository;
        this.banqueRepository = banqueRepository;
    }

    @Transactional
    public Map<String, Object> getBilanMensuelCharges(Long companyId, String mois) {
        // Récupère tous les bulletins du mois pour l'entreprise
        List<BulletinPaie> bulletins = bulletinPaieRepository.findByCompanyIdAndMois(companyId, mois);

        // Chaque bulletin devient une ligne
        List<BilanMensuelChargeSocialeDTO> lignes = bulletins.stream().map(bp ->
                new BilanMensuelChargeSocialeDTO(
                        bp.getMois(),
                        bp.getCompany().getName(),
                        bp.getEmploye().getNom() + " " + bp.getEmploye().getPrenom(),
                        bp.getSalaireBrut(),
                        bp.getMontantCnss(),
                        bp.getMontantCnssEmployeur()
                )
        ).collect(Collectors.toList());

        // Totaux
        BigDecimal totalSalaire = lignes.stream().map(BilanMensuelChargeSocialeDTO::getSalaireBrutBulletinPaie)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCnss = lignes.stream().map(BilanMensuelChargeSocialeDTO::getMontantCnssBulletinPaie)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCnssEmployeur = lignes.stream().map(BilanMensuelChargeSocialeDTO::getMontantCnssEmployeurBulletinPaie)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCharges = lignes.stream().map(BilanMensuelChargeSocialeDTO::getTotalChargeSociale)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        BilanMensuelChargeSocialeTotalDTO totalDTO = new BilanMensuelChargeSocialeTotalDTO(
                totalSalaire, totalCnss, totalCnssEmployeur, totalCharges
        );

        Map<String, Object> result = new HashMap<>();
        result.put("lignes", lignes);
        result.put("totaux", totalDTO);

        return result;
    }

    @Transactional
    public Map<String, Object> getBilanPeriodiqueCharges(Long companyId, Long employeId, String debut, String fin) {

        // Récupère tous les bulletins pour la période et l'employé
        List<BulletinPaie> bulletins = bulletinPaieRepository
                .findByCompanyAndEmployeAndMoisBetween(companyId, employeId, debut, fin);

        // Chaque bulletin = une ligne
        List<BilanPeriodiqueChargeSocialeDTO> lignes = bulletins.stream().map(bp ->
                new BilanPeriodiqueChargeSocialeDTO(
                        bp.getMois(),                // début = mois du bulletin
                        bp.getMois(),                // fin = même mois
                        bp.getEmploye().getNom() + " " + bp.getEmploye().getPrenom(),
                        bp.getEmploye().getNumeroCnss(),
                        bp.getSalaireBrut(),
                        bp.getMontantCnss(),
                        bp.getMontantCnssEmployeur(),
                        bp.getMontantCnss().add(bp.getMontantCnssEmployeur()),
                        bp.getCompany().getName(),
                        bp.getCompany().getNss()
                )
        ).toList();

        // Totaux
        BigDecimal totalSalaire = lignes.stream().map(BilanPeriodiqueChargeSocialeDTO::getSalaireBrut)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCnssEmploye = lignes.stream().map(BilanPeriodiqueChargeSocialeDTO::getCnssEmploye)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCnssEmployeur = lignes.stream().map(BilanPeriodiqueChargeSocialeDTO::getCnssEmployeur)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCharges = lignes.stream().map(BilanPeriodiqueChargeSocialeDTO::getTotalChargeSociale)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

        BilanPeriodiqueChargeSocialeTotalDTO totalDTO = new BilanPeriodiqueChargeSocialeTotalDTO(
                totalSalaire, totalCnssEmploye, totalCnssEmployeur, totalCharges
        );

        Map<String, Object> result = new HashMap<>();
        result.put("lignes", lignes);
        result.put("totaux", totalDTO);

        return result;
    }

    @Transactional
    public Map<String, Object> getBilanPeriodiqueParEntreprise(Long companyId, String debut, String fin) {

        // Récupère tous les bulletins pour l'entreprise et la période
        List<BulletinPaie> bulletins = bulletinPaieRepository
                .findByCompanyIdAndMoisBetween(companyId, debut, fin);

        // Groupe par mois
        Map<String, List<BulletinPaie>> bulletinsParMois = bulletins.stream()
                .collect(Collectors.groupingBy(BulletinPaie::getMois));

        List<BilanPeriodiqueChargeSocialeDTO> lignes = new ArrayList<>();

        for (String mois : bulletinsParMois.keySet()) {
            List<BulletinPaie> bps = bulletinsParMois.get(mois);

            BigDecimal totalSalaire = bps.stream()
                    .map(BulletinPaie::getSalaireBrut)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalCnssEmploye = bps.stream()
                    .map(BulletinPaie::getMontantCnss)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalCnssEmployeur = bps.stream()
                    .map(BulletinPaie::getMontantCnssEmployeur)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalCharges = totalCnssEmploye.add(totalCnssEmployeur);

            lignes.add(new BilanPeriodiqueChargeSocialeDTO(
                    mois, // début
                    mois, // fin
                    null, // pas d'employé détaillé
                    null, // pas de numéro CNSS employé
                    totalSalaire,
                    totalCnssEmploye,
                    totalCnssEmployeur,
                    totalCharges,
                    null, // entreprise pas détaillée ici
                    null  // NSS entreprise
            ));
        }

        // Totaux généraux sur toute la période
        BigDecimal totalSalaireGlobal = lignes.stream()
                .map(BilanPeriodiqueChargeSocialeDTO::getSalaireBrut)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCnssEmployeGlobal = lignes.stream()
                .map(BilanPeriodiqueChargeSocialeDTO::getCnssEmploye)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCnssEmployeurGlobal = lignes.stream()
                .map(BilanPeriodiqueChargeSocialeDTO::getCnssEmployeur)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalChargesGlobal = lignes.stream()
                .map(BilanPeriodiqueChargeSocialeDTO::getTotalChargeSociale)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BilanPeriodiqueChargeSocialeTotalDTO totalDTO = new BilanPeriodiqueChargeSocialeTotalDTO(
                totalSalaireGlobal, totalCnssEmployeGlobal, totalCnssEmployeurGlobal, totalChargesGlobal
        );

        Map<String, Object> result = new HashMap<>();
        result.put("lignes", lignes);
        result.put("totaux", totalDTO);

        return result;
    }

    @Transactional
    public Map<String, Object> getTotauxParBanqueEtMois1(Long banqueId, String mois) {
        List<NetParEntrepriseBanqueDTO> details = bulletinPaieRepository.getTotauxParEntrepriseEtBanque(banqueId, mois);
        BigDecimal totalGlobal = bulletinPaieRepository.getTotalGlobalParBanqueEtMois(banqueId, mois);

        Map<String, Object> result = new HashMap<>();
        result.put("banqueId", banqueId);
        result.put("mois", mois);
        result.put("details", details);
        result.put("totalGlobal", totalGlobal);

        return result;
    }

    @Transactional
    public TotauxParBanqueResponse getTotauxParBanqueEtMois(Long banqueId, String mois) {
        // Récupère la liste des totaux par entreprise
        List<NetParEntrepriseBanqueDTO> details = bulletinPaieRepository.getTotauxParEntrepriseEtBanque(banqueId, mois);

        // Récupère le total global
        BigDecimal totalGlobal = bulletinPaieRepository.getTotalGlobalParBanqueEtMois(banqueId, mois);

        // On prend le nom de la banque depuis la première ligne si disponible
        Optional<Banque> banque = banqueRepository.findById(banqueId);

        // Crée le DTO final
        TotauxParBanqueResponse response = new TotauxParBanqueResponse();
        response.setBanqueId(banqueId);
        response.setBanque(banque.get().getName());
        response.setMoisCalcul(mois);
        response.setEntreprises(details);
        response.setTotalGlobal(totalGlobal);

        return response;
    }

    @Transactional
    public BilanFiscaleResponse getBilan(Long companyId, String mois, String moisDebut, String moisFin) {
        List<BulletinPaie> bulletins;

        if (mois != null) {
            // mensuel
            bulletins = bulletinPaieRepository.findByCompanyIdAndMois(companyId, mois);
        } else {
            // périodique
            bulletins = bulletinPaieRepository.findByCompanyIdAndMoisBetween(companyId, moisDebut, moisFin);
        }

        List<BilanFiscaleDTO> dtos = bulletins.stream()
                .map(b -> new BilanFiscaleDTO(
                        b.getMois(),
                        moisDebut,
                        moisFin,
                        b.getEmploye() != null ? b.getEmploye().getNom() + " " + b.getEmploye().getPrenom() : "",
                        b.getCompany() != null ? b.getCompany().getName() : "",
                        b.getSalaireBrut() != null ? b.getSalaireBrut() : BigDecimal.ZERO,
                        b.getSalaireBrutArrondi() != null ? b.getSalaireBrutArrondi() : BigDecimal.ZERO,
                        b.getMontantIpts() != null ? b.getMontantIpts() : BigDecimal.ZERO,
                        b.getMontantVps() != null ? b.getMontantVps() : BigDecimal.ZERO
                ))
                .collect(Collectors.toList());

        BigDecimal totalSalaireBrut = dtos.stream()
                .map(d -> d.getSalaireBrut())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSalaireBrutArrondi = dtos.stream()
                .map(d -> d.getSalaireBrutArrondi())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalIts = dtos.stream()
                .map(d -> d.getIts())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVps = dtos.stream()
                .map(d -> d.getVps())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGeneral = totalIts.add(totalVps);

        BilanFiscaleResponse response = new BilanFiscaleResponse();
        response.setBulletins(dtos);
        response.setTotalSalaireBrut(totalSalaireBrut);
        response.setTotalSalaireBrutArrondi(totalSalaireBrutArrondi);
        response.setTotalIts(totalIts);
        response.setTotalVps(totalVps);
        response.setTotalGeneral(totalGeneral);

        return response;
    }

    @Transactional
    public SalaireMensuelResponse getSalairePeriodique(Long companyId, String moisDebut, String moisFin) {

        List<BulletinPaie> bulletins = bulletinPaieRepository.findByCompanyIdAndMoisBetween(companyId, moisDebut, moisFin);

        List<SalaireMensuelDTO> dtos = bulletins.stream()
                .map(b -> {
                    String employeNom = (b.getEmploye() != null)
                            ? b.getEmploye().getNom() + " " + b.getEmploye().getPrenom() : "";

                    String entrepriseNom = (b.getCompany() != null) ? b.getCompany().getName() : "";

                    String departement = "";
                    if (b.getContratEmploye() != null && b.getContratEmploye().getDepartement() != null) {
                        departement = b.getContratEmploye().getDepartement().getLibelle();
                    }

                    String domiciliation = (b.getDomiciliationBancaireEmploye() != null)
                            ? b.getDomiciliationBancaireEmploye().getName() : "";

                    String numeroCompte = b.getNumeroCompteEmploye() != null ? b.getNumeroCompteEmploye() : "";

                    double tempsTravail = b.getTempsTravail();

                    BigDecimal netAPayer = b.getNetAPayer() != null ? b.getNetAPayer() : BigDecimal.ZERO;

                    return new SalaireMensuelDTO(employeNom, entrepriseNom, departement,
                            domiciliation, numeroCompte, tempsTravail, netAPayer);
                })
                .collect(Collectors.toList());

        BigDecimal totalNet = dtos.stream()
                .map(SalaireMensuelDTO::getNetAPayer)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        SalaireMensuelResponse response = new SalaireMensuelResponse();
        response.setSalaires(dtos);
        response.setTotalNetAPayer(totalNet);

        return response;
    }


}
