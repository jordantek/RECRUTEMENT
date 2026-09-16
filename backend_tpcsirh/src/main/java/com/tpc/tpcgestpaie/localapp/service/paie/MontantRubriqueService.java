package com.tpc.tpcgestpaie.localapp.service.paie;

import com.tpc.tpcgestpaie.localapp.dto.accessoire.HistoriqueMontantsDTO;
import com.tpc.tpcgestpaie.localapp.dto.accessoire.MontantMensuelDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.MontantRubriqueDTO;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.paie.MontantRubriqueRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.bulletin.BulletinPaieRepository;
import com.tpc.tpcgestpaie.localapp.service.ContratEmployeService;
import com.tpc.tpcgestpaie.localapp.service.EmployeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MontantRubriqueService {

    @Autowired
    public MontantRubriqueRepository montantRubriqueRepository;
    @Autowired
    private ContratEmployeService contratEmployeService;
    @Autowired
    private BulletinPaieRepository bulletinPaieRepository;
    @Autowired
    private EmployeService   employeService;

    public List<MontantRubriqueDTO> getAll() {
        return montantRubriqueRepository.findAll()
                .stream()
                .map(MontantRubriqueDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<MontantRubriqueDTO> getById(Long id) {
        return montantRubriqueRepository.findById(id)
                .map(MontantRubriqueDTO::fromEntity);
    }

//    public MontantRubriqueDTO save(MontantRubriqueDTO dto,
//                                   ContratEmploye contratEmploye,
//                                   Employe employe,
//                                   Company company,
//                                   Rubrique rubrique,
//                                   User addedBy) {
//        MontantRubrique entity = dto.toEntity(contratEmploye, employe, company, rubrique, addedBy);
//        MontantRubrique saved = montantRubriqueRepository.save(entity);
//        return MontantRubriqueDTO.fromEntity(saved);
//    }

    public MontantRubriqueDTO save(MontantRubriqueDTO dto,
                                   ContratEmploye contratEmploye,
                                   Employe employe,
                                   Company company,
                                   Rubrique rubrique,
                                   User addedBy) {
        // Ignorer si le montant est null ou égal à 0
        if (dto.getMontantRubrique() == null ||
                dto.getMontantRubrique().compareTo(BigDecimal.ZERO) == 0) {
            return null; // Ne pas enregistrer, continuer sans erreur
        }

        MontantRubrique entity = dto.toEntity(contratEmploye, employe, company, rubrique, addedBy);
        MontantRubrique saved = montantRubriqueRepository.save(entity);
        return MontantRubriqueDTO.fromEntity(saved);
    }

    public MontantRubrique update(MontantRubrique existing, MontantRubriqueDTO dto) {
        existing.setMontantRubrique(dto.getMontantRubrique());
        existing.setMoisRubrique(dto.getMoisRubrique());
        existing.setId(existing.getId());
        // ... autres champs
        return montantRubriqueRepository.save(existing);
    }


    public List<MontantRubriqueDTO> findByCompanyId(Long companyId) {
        return montantRubriqueRepository.findByCompanyId(companyId)
                .stream()
                .filter(montantRubrique -> {
                    String libelle = montantRubrique.getRubrique().getLibelle();
                    if (libelle == null) return true;
                    String libelleTrim = libelle.trim().toUpperCase();
                    return !libelleTrim.equals("SALAIRE 13E MOIS")
                            && !libelleTrim.equals("SALAIRE MOYEN")
                            && !libelleTrim.equals("PRIMES EXCEPTIONNELLES");
                })
                .map(MontantRubriqueDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<MontantRubriqueDTO> findByCompanyIdAndMoisRubrique(Long companyId, String moisRubrique){
        return montantRubriqueRepository.findByCompanyIdAndMoisRubrique(companyId,moisRubrique)
                .stream().map(MontantRubriqueDTO::fromEntity).collect(Collectors.toList());
    }

    //Salaire 13e Mois
    public List<MontantRubriqueDTO> findSalaire13eMoisByCompanyIdAndMoisRubrique(Long companyId, String moisRubrique){
        return montantRubriqueRepository.findSalaire13eMoisByCompanyIdAndMoisRubrique(companyId,moisRubrique)
                .stream().map(MontantRubriqueDTO::fromEntity).collect(Collectors.toList());
    }


    //Salaire Moyen
    public List<MontantRubriqueDTO> findSalaireMoyenByCompanyIdAndMoisRubrique(Long companyId, String moisRubrique){
        return montantRubriqueRepository.findSalaireMoyenByCompanyIdAndMoisRubrique(companyId,moisRubrique)
                .stream().map(MontantRubriqueDTO::fromEntity).collect(Collectors.toList());
    }

    //Salaire Moyen Journalier
    public List<MontantRubriqueDTO> findSalaireMoyenJournalierByCompanyIdAndMoisRubrique(Long companyId, String moisRubrique){
        return montantRubriqueRepository.findSalaireMoyenJournalierByCompanyIdAndMoisRubrique(companyId,moisRubrique)
                .stream().map(MontantRubriqueDTO::fromEntity).collect(Collectors.toList());
    }

    //Primes exceptionnelles
    public List<MontantRubriqueDTO> findPrimesExceptionnellesByCompanyIdAndMoisRubrique(Long companyId, String moisRubrique){
        return montantRubriqueRepository.findPrimesExceptionnellesByCompanyIdAndMoisRubrique(companyId,moisRubrique)
                .stream().map(MontantRubriqueDTO::fromEntity).collect(Collectors.toList());
    }

    public List<MontantRubriqueDTO> findByEmployeId(Long employeId) {
        return montantRubriqueRepository.findByEmployeId(employeId)
                .stream()
                .map(MontantRubriqueDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<MontantRubriqueDTO> findByEmployeIdAndMoisRubriqueAndRubriqueId(Long employeId, String mois, Long rubriqueId) {
        return montantRubriqueRepository.findByEmployeIdAndMoisRubriqueAndRubriqueId(employeId, mois, rubriqueId)
                .map(MontantRubriqueDTO::fromEntity);
    }

    public Optional<MontantRubriqueDTO> findByEmployeIdAndMoisRubriqueFiltre(Long employeId, String mois) {
        return montantRubriqueRepository.findFirstByEmployeIdAndMoisRubrique(employeId, mois)
                .map(MontantRubriqueDTO::fromEntity);
    }


    public List<MontantRubriqueDTO> findByMoisRubrique(String moisRubrique) {
        return montantRubriqueRepository.findByMoisRubrique(moisRubrique)
                .stream()
                .map(MontantRubriqueDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<MontantRubriqueDTO> findByDateRubriqueBetween(LocalDate startDate, LocalDate endDate) {
        return montantRubriqueRepository.findByDateRubriqueBetween(startDate, endDate)
                .stream()
                .map(MontantRubriqueDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        montantRubriqueRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        return montantRubriqueRepository.existsById(id);
    }

    public List<String> getLast12Months(String dateReference) {
        DateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.FRENCH);

        Calendar endCalendar = Calendar.getInstance();
        try {
            endCalendar.setTime(formatter.parse(dateReference));
        } catch (ParseException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

        // Exclure le mois courant : on recule d'un mois
        endCalendar.add(Calendar.MONTH, -1);

        // Début = 11 mois avant la fin
        Calendar startCalendar = (Calendar) endCalendar.clone();
        startCalendar.add(Calendar.MONTH, -11);

        List<String> dates = new ArrayList<>();
        while (!startCalendar.after(endCalendar)) {
            String date = firstLetterCaps(formatter.format(startCalendar.getTime()));
            dates.add(date);
            startCalendar.add(Calendar.MONTH, 1);
        }
        return dates;
    }

    public static String firstLetterCaps(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public HistoriqueMontantsDTO calculerSalaireMoyens12Mois(Long idContratEmploye, String moisStr) {
        Optional<ContractEmployeDTO> contratOpt = contratEmployeService.getById(idContratEmploye);
        if (contratOpt.isEmpty()) {
            throw new IllegalArgumentException("Contrat employé non trouvé");
        }

        Long employeId = contratOpt.get().getEmployeId();
        YearMonth moisTraitement;
        try {
            moisTraitement = YearMonth.parse(moisStr); // Format attendu : "yyyy-MM"
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format du mois invalide. Format attendu : yyyy-MM");
        }

        // 12 derniers mois avant le mois donné
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        List<String> moisBulletins = new ArrayList<>();
        for (int i = 12; i >= 1; i--) {
            YearMonth mois = moisTraitement.minusMonths(i);
            moisBulletins.add(mois.format(formatter));
        }

        // 2. Récupérer les bulletins avec le champ `mois` (pas `dateCalculSalaire`)
        List<BulletinPaie> bulletins = bulletinPaieRepository
                .findByEmployeIdAndMoisInOrderByMoisAsc(employeId, moisBulletins);

        List<MontantMensuelDTO> montants = new ArrayList<>();
        BigDecimal montantTotal = BigDecimal.ZERO;

        for (BulletinPaie b : bulletins) {
            BigDecimal montant = b.getSalaireBrut() != null ? b.getSalaireBrut() : BigDecimal.ZERO;
            String mois = b.getMois();

            montants.add(new MontantMensuelDTO(mois, montant));
            montantTotal = montantTotal.add(montant);
        }

        BigDecimal montantMoyen = montants.size() > 0
                ? montantTotal.divide(BigDecimal.valueOf(montants.size()), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        HistoriqueMontantsDTO dto = new HistoriqueMontantsDTO(
                montants,
                montantTotal,
                montantMoyen,
                null,
                null,
                moisStr
        );
        return dto;
    }

}
