package com.tpc.tpcgestpaie.localapp.service.soldeToutCompte;


import com.tpc.tpcgestpaie.localapp.dto.soldeToutCompte.IndemniteResumeDTO;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.IndemniteCongeRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.bulletin.BulletinPaieRepository;
import com.tpc.tpcgestpaie.localapp.repository.stc.IndemniteLicenciementRepository;
import com.tpc.tpcgestpaie.localapp.repository.stc.IndemniteResumeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;

@Service
public class IndemniteResumeService {

    private final IndemniteResumeRepository repository;
    private final IndemniteLicenciementService indemniteLicenciementService;
    private final BulletinPaieRepository bulletinPaieRepository;
    private final IndemniteLicenciementRepository indemniteLicenciementRepository;
    private final IndemniteCongeRepository indemniteCongeRepository;
    private final ContratEmployeRepository contratEmployeRepository;

    public IndemniteResumeService(IndemniteResumeRepository repository, IndemniteLicenciementService indemniteLicenciementService, BulletinPaieRepository bulletinPaieRepository, IndemniteLicenciementRepository indemniteLicenciementRepository, IndemniteCongeRepository indemniteCongeRepository, ContratEmployeRepository contratEmployeRepository) {
        this.repository = repository;
        this.indemniteLicenciementService = indemniteLicenciementService;
        this.bulletinPaieRepository = bulletinPaieRepository;
        this.indemniteLicenciementRepository = indemniteLicenciementRepository;
        this.indemniteCongeRepository = indemniteCongeRepository;
        this.contratEmployeRepository = contratEmployeRepository;
    }
//
//    public IndemniteResumeDTO getResume(Long contratId, String mois) {
//        return repository.findByContratAndMois(contratId, mois);
//    }

    public IndemniteResumeDTO getResume(Long contratEmployeId, YearMonth mois) {
        IndemniteResumeDTO dto = new IndemniteResumeDTO();

        // 1️⃣ Récupérer le contrat + employé
        ContratEmploye contrat = contratEmployeRepository.findById(contratEmployeId)
                .orElseThrow(() -> new IllegalArgumentException("Contrat employé non trouvé"));

        Employe employe = contrat.getEmploye();
        dto.setNomEmploye(employe.getNom());
        dto.setPrenomEmploye(employe.getPrenom());
        dto.setIdContratEmploye(contrat.getId());
        // Exemple : récupération directe depuis les tables existantes
        dto.setIndemniteSelonAnciennete(
                indemniteLicenciementRepository
                        .findMontantByContratEmployeIdAndMois(contratEmployeId, mois) // YearMonth
                        .orElse(BigDecimal.ZERO)
        );

        dto.setMontantMoyenLicenciement(
                indemniteLicenciementRepository
                        .findMontantMoyenByContratEmployeIdAndMois(contratEmployeId, mois) // YearMonth
                        .orElse(BigDecimal.ZERO)
        );

        dto.setMontantTotalConge(
                indemniteCongeRepository.findMontantByContratEmployeIdAndMois(contratEmployeId, mois)
                        .orElse(BigDecimal.ZERO)
        );

        dto.setSalaireBrutBulletin(
                bulletinPaieRepository.findSalaireBrutByContratEmployeIdAndMois(contratEmployeId, String.valueOf(mois))
                        .orElse(BigDecimal.ZERO)
        );

        return dto;
    }

}
