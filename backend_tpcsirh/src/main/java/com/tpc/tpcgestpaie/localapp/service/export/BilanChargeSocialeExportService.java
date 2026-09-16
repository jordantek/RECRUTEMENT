package com.tpc.tpcgestpaie.localapp.service.export;

import com.tpc.tpcgestpaie.localapp.dto.etat.BilanChargeSocialeDTO;
import com.tpc.tpcgestpaie.localapp.dto.etat.BilanChargeSocialeResponse;
import com.tpc.tpcgestpaie.localapp.model.BulletinPaie;
import com.tpc.tpcgestpaie.localapp.repository.paie.bulletin.BulletinPaieRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BilanChargeSocialeExportService {

    private final BulletinPaieRepository bulletinPaieRepository;

    public BilanChargeSocialeExportService(BulletinPaieRepository bulletinPaieRepository) {
        this.bulletinPaieRepository = bulletinPaieRepository;
    }

    public BilanChargeSocialeResponse getBilan(Long idEmploye, String mois, String moisDebut, String moisFin) {
        List<BulletinPaie> bulletins;

        if (mois != null) {
            // cas mensuel
            bulletins = bulletinPaieRepository.findByEmployeAndMoisBetween(idEmploye, mois, mois);
        } else {
            // cas périodique
            bulletins = bulletinPaieRepository.findByEmployeAndMoisBetween(idEmploye, moisDebut, moisFin);
        }

        List<BilanChargeSocialeDTO> dtos = bulletins.stream()
                .map(b -> new BilanChargeSocialeDTO(
                        b.getSalaireBrut(),
                        b.getSalaireBrutArrondi(),
                        b.getMontantIpts(),
                        b.getMontantVps()
                ))
                .collect(Collectors.toList());

        BigDecimal totalSalaireBrut = dtos.stream()
                .map(d -> d.getSalaireBrut() != null ? d.getSalaireBrut() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSalaireBrutArrondi = dtos.stream()
                .map(d -> d.getSalaireBrutArrondi() != null ? d.getSalaireBrutArrondi() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalIpts = dtos.stream()
                .map(d -> d.getMontantIpts() != null ? d.getMontantIpts() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVps = dtos.stream()
                .map(d -> d.getMontantVps() != null ? d.getMontantVps() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGeneral = totalIpts.add(totalVps);

        BilanChargeSocialeResponse response = new BilanChargeSocialeResponse();
        response.setBulletins(dtos);
        response.setTotalSalaireBrut(totalSalaireBrut);
        response.setTotalSalaireBrutArrondi(totalSalaireBrutArrondi);
        response.setTotalIpts(totalIpts);
        response.setTotalVps(totalVps);
        response.setTotalGeneral(totalGeneral);

        // infos de période
        if (mois != null) {
            response.setMois(mois);
        } else {
            response.setPeriodeDebut(moisDebut);
            response.setPeriodeFin(moisFin);
        }

        return response;
    }
}