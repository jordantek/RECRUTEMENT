package com.tpc.tpcgestpaie.localapp.service.paie.bulletin;

import com.tpc.tpcgestpaie.localapp.dto.accessoire.LigneBulletinDTO;
import com.tpc.tpcgestpaie.localapp.dto.accessoire.TableauBulletinDTO;
import com.tpc.tpcgestpaie.localapp.dto.employe.EmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.paie.BulletinPaieDTO;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.*;
import com.tpc.tpcgestpaie.localapp.repository.administration.AbsenceRepository;
import com.tpc.tpcgestpaie.localapp.repository.paie.bulletin.BulletinPaieRepository;
import com.tpc.tpcgestpaie.localapp.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BulletinPaieService {

    private final BulletinPaieRepository repository;
    private final CompanyService companyService;
    private final EmployeService employeService;
    private final ContratEmployeService contratEmployeService;
    private final BanqueService banqueService;
    private final UserService userService;
    private final ContratEmployeRepository contratEmployeRepository;
    private final EmployeRepository employeRepository;
    private final CompanyRepository companyRepository;
    private final BanqueRepository banqueRepository;
    private final UserRepository userRepository;
    private final BulletinPaieRepository bulletinPaieRepository;


    private final AbsenceRepository absenceRepository;

    public BulletinPaieService(BulletinPaieRepository repository, CompanyService companyService, EmployeService employeService, ContratEmployeService contratEmployeService, BanqueService banqueService, UserService userService, ContratEmployeRepository contratEmployeRepository, EmployeRepository employeRepository, CompanyRepository companyRepository, BanqueRepository banqueRepository, UserRepository userRepository, BulletinPaieRepository bulletinPaieRepository, AbsenceRepository absenceRepository) {
        this.repository = repository;
        this.companyService = companyService;
        this.employeService = employeService;
        this.contratEmployeService = contratEmployeService;
        this.banqueService = banqueService;
        this.userService = userService;
        this.contratEmployeRepository = contratEmployeRepository;
        this.employeRepository = employeRepository;
        this.companyRepository = companyRepository;
        this.banqueRepository = banqueRepository;
        this.userRepository = userRepository;
        this.bulletinPaieRepository = bulletinPaieRepository;
        this.absenceRepository = absenceRepository;
    }

    public BulletinPaieDTO save(BulletinPaieDTO dto) {
        BulletinPaie entity = convertToEntity(dto);
        BulletinPaie saved = repository.save(entity);
        return convertToDTO(saved);
    }

    public List<BulletinPaie> getAllBulletins() {
        return repository.findAll();
    }

    public List<BulletinPaie> getByNatureContrat(Long natureContratId) {
        return repository.findByContratEmployeNatureContratId(natureContratId);
    }

    public BulletinPaie getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteBulletinPaie(Long id) {
        repository.deleteById(id);
    }

    public List<Company> getCompaniesByMois(String mois) {
        return repository.findDistinctCompanyByMois(mois);
    }

    public List<Company> getCompaniesByMoisAndTva(String mois, java.math.BigDecimal tvaVal) {
        return repository.findDistinctCompanyByMoisAndTvaVal(mois, tvaVal);
    }

    public List<Company> getCompaniesHonoraireByMois(String mois) {
        return repository.findDistinctCompanyByMoisHonoraire(mois);
    }

    public List<BulletinPaie> getByMoisAndBanque(String mois, Long banqueId) {
        return repository.findByMoisAndDomiciliationBancaireEmployeBanqueId(mois, banqueId);
    }

    @Transactional
    public List<BulletinPaieDTO> getByMoisAndCompany(String mois, Long companyId) {
        return repository.findByMoisAndCompanyId(mois, companyId)
               .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BulletinPaieDTO> getByMoisAndCompanyAndStatus(String mois, Long companyId,String status) {
        return repository.findByMoisAndCompanyIdStatus(mois, companyId,status.trim())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean deleteOldBulletinsPendingByCompanyAndMoisAndStatus(Long companyId,String mois, String statut){
        int deletedCount = repository.deleteByCompanyIdAndMoisAndStatut(companyId, mois, statut);
        return deletedCount > 0;
    }

    public List<BulletinPaie> getByMoisCompanyNatureContrat(String mois, Long companyId, Long natureContratId) {
        return repository.findByMoisAndCompanyIdAndContratEmployeNatureContratId(mois, companyId, natureContratId);
    }

    public List<BulletinPaie> getByMoisCompanyDepartement(String mois, Long companyId, Long departementId) {
        return repository.findByMoisAndCompanyIdAndDepartementId(mois, companyId, departementId);
    }

    public List<BulletinPaie> getByMoisCompanyDepartementBanqueNature(
            String mois, Long companyId, Long departementId, Long banqueId, Long natureContratId) {
        return repository.findByMoisAndCompanyIdAndDepartementIdAndDomiciliationBancaireEmployeBanqueIdAndContratEmployeNatureContratId(
                mois, companyId, departementId, banqueId, natureContratId);
    }

    public BulletinPaie getByMoisAndEmploye(String mois, Long employeId) {
        return repository.findByMoisAndEmployeId(mois, employeId);
    }

    public BulletinPaie getByMoisEmployeNatureContrat(String mois, Long employeId, Long natureContratId) {
        return repository.findByMoisAndEmployeIdAndContratEmployeNatureContratId(mois, employeId, natureContratId);
    }

    public Long countByMoisCompanyNatureContrat(String mois, Long companyId, Long natureContratId) {
        return repository.countByMoisAndEntrepriseAndNatureContrat(mois, companyId, natureContratId);
    }

    //Tout les bulletin d'un employé
    @Transactional(readOnly = true)
    public List<BulletinPaieDTO> getAllBulletinsByEmploye(Long employeId) {

        if (employeId == null) {
            return new ArrayList<>();
        }

        return repository
                .findByEmployeIdOrderByMoisDesc(employeId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BulletinPaieDTO convertToDTO(BulletinPaie bulletin) {
        if (bulletin == null) return null;

        BulletinPaieDTO dto = new BulletinPaieDTO();

        dto.setId(bulletin.getId());
        dto.setDateCalculSalaire(bulletin.getDateCalculSalaire());
        dto.setMois(bulletin.getMois());

        // IDs des entités associées

        // IDs des entités associées
        if (bulletin.getEmploye() != null) {
            dto.setEmployeId(bulletin.getEmploye().getId());
            dto.setEmploye(EmployeDTO.fromEntity(bulletin.getEmploye()));
        }

        if (bulletin.getCompany() != null) {
            dto.setCompanyId(bulletin.getCompany().getId());
            //dto.setCompany(bulletin.getCompany());
        }

        if (bulletin.getContratEmploye() != null) {
            dto.setContratEmployeId(bulletin.getContratEmploye().getId());
        }

        if (bulletin.getDomiciliationBancaireEmploye() != null) {
            dto.setBanqueId(bulletin.getDomiciliationBancaireEmploye().getId());
            dto.setBanque(bulletin.getDomiciliationBancaireEmploye());
        }
        if (bulletin.getAdded_by() != null) {
            dto.setAddedById(bulletin.getAdded_by().getId());
        }

        System.out.println("je uis dans apres et voici le temps"+  bulletin.getTempsTravail());
        // Champs simples
        dto.setTempsTravail(bulletin.getTempsTravail());
        dto.setSalaireBrut(bulletin.getSalaireBrut());
        dto.setSalaireBrutArrondi(bulletin.getSalaireBrutArrondi());
        dto.setMontantCnss(bulletin.getMontantCnss());
        dto.setNombreEnfant(bulletin.getNombreEnfant());
        dto.setMontantIpts(bulletin.getMontantIpts());
        dto.setMontantAib(bulletin.getMontantAib());
        dto.setTotalRetenue(bulletin.getTotalRetenue());
        dto.setSalaireNet(bulletin.getSalaireNet());
        dto.setAutreAvantage(bulletin.getAutreAvantage());
        dto.setAutreRetenue(bulletin.getAutreRetenue());
        dto.setMontantCnssEmployeur(bulletin.getMontantCnssEmployeur());
        dto.setMontantVps(bulletin.getMontantVps());
        dto.setTotalChargePatronale(bulletin.getTotalChargePatronale());
        dto.setNetAPayer(bulletin.getNetAPayer());

        dto.setNumeroCompteEmploye(bulletin.getNumeroCompteEmploye());

        dto.setCongePris(bulletin.getCongePris());
        dto.setSoldeConge(bulletin.getSoldeConge());

        dto.setTempsTravail(bulletin.getTempsTravail());
        dto.setDepartement(bulletin.getDepartement());
        dto.setTaxeRadiophonique(bulletin.getTaxeRadiophonique());
        dto.setTaxeTelevisuel(bulletin.getTaxeTelevisuel());

        dto.setPrimesExceptionnelles(bulletin.getPrimesExceptionnelles());
        dto.setTreiziemeMois(bulletin.getTreiziemeMois());
        dto.setSalaireBrutMoisPasse(bulletin.getSalaireBrutMoisPasse());
        dto.setAllocationConge(bulletin.getAllocationConge());
        dto.setPrimeAnciennete(bulletin.getPrimeAnciennete());

        dto.setSignataire(bulletin.getSignataire());
        dto.setStatut(bulletin.getStatut());

        dto.setValidatedAt(bulletin.getValidated_at());
        dto.setDescription(bulletin.getDescription());

        dto.setCreatedAt(bulletin.getCreated_at());
        dto.setUpdatedAt(bulletin.getUpdated_at());
        dto.setDeletedAt(bulletin.getDeleted_at());

        return dto;
    }

    public BulletinPaie convertToEntity(BulletinPaieDTO dto) {
        BulletinPaie bulletin = new BulletinPaie();

        bulletin.setId(dto.getId());
        bulletin.setDateCalculSalaire(dto.getDateCalculSalaire());
        bulletin.setMois(dto.getMois());

        // Tu dois aller chercher ces entités avec leurs repositories avant l'appel
        if (dto.getContratEmployeId() != null) {
            ContratEmploye ce = contratEmployeRepository.findById(dto.getContratEmployeId()).orElse(null);
            bulletin.setContratEmploye(ce);
        }

        if (dto.getEmployeId() != null) {
            Employe employe = employeRepository.findById(dto.getEmployeId()).orElse(null);
            bulletin.setEmploye(employe);
        }

        if (dto.getCompanyId() != null) {
            Company company = companyRepository.findById(dto.getCompanyId()).orElse(null);
            bulletin.setCompany(company);
        }

        if (dto.getBanqueId() != null) {
            Banque db = banqueRepository.findById(dto.getBanqueId()).orElse(null);
            bulletin.setDomiciliationBancaireEmploye(db);
        }

        if (dto.getAddedById() != null) {
            User addedBy = userRepository.findById(dto.getAddedById()).orElse(null);
            bulletin.setAdded_by(addedBy);
        }

        bulletin.setTempsTravail(dto.getTempsTravail());

        bulletin.setSalaireBrut(dto.getSalaireBrut());
        bulletin.setSalaireBrutArrondi(dto.getSalaireBrutArrondi());
        bulletin.setMontantCnss(dto.getMontantCnss());
        bulletin.setNombreEnfant(dto.getNombreEnfant());
        bulletin.setMontantIpts(dto.getMontantIpts());
        bulletin.setMontantAib(dto.getMontantAib());
        bulletin.setTotalRetenue(dto.getTotalRetenue());
        bulletin.setSalaireNet(dto.getSalaireNet());
        bulletin.setAutreAvantage(dto.getAutreAvantage());
        bulletin.setAutreRetenue(dto.getAutreRetenue());
        bulletin.setMontantCnssEmployeur(dto.getMontantCnssEmployeur());
        bulletin.setMontantVps(dto.getMontantVps());
        bulletin.setTotalChargePatronale(dto.getTotalChargePatronale());
        bulletin.setNetAPayer(dto.getNetAPayer());

        bulletin.setTempsTravail(dto.getTempsTravail());
        bulletin.setDepartement(dto.getDepartement());
        bulletin.setTaxeRadiophonique(dto.getTaxeRadiophonique());
        bulletin.setTaxeTelevisuel(dto.getTaxeTelevisuel());

        bulletin.setPrimesExceptionnelles(dto.getPrimesExceptionnelles());
        bulletin.setTreiziemeMois(dto.getTreiziemeMois());
        bulletin.setSalaireBrutMoisPasse(dto.getSalaireBrutMoisPasse());
        bulletin.setAllocationConge(dto.getAllocationConge());
        bulletin.setPrimeAnciennete(dto.getPrimeAnciennete());

        bulletin.setNumeroCompteEmploye(dto.getNumeroCompteEmploye());

        bulletin.setCongePris(dto.getCongePris());
        bulletin.setSoldeConge(dto.getSoldeConge());

        bulletin.setSignataire(dto.getSignataire());
        bulletin.setStatut(dto.getStatut());

        bulletin.setValidated_at(dto.getValidatedAt());
        bulletin.setValidated_by(dto.getValidatedBy());

        bulletin.setDescription(dto.getDescription());
        bulletin.setCreated_at(dto.getCreatedAt());
        bulletin.setUpdated_at(dto.getUpdatedAt());
        bulletin.setDeleted_at(dto.getDeletedAt());

        return bulletin;
    }

    public Double getSalaireBrutMoisPrecedent(Long contratEmployeId, String moisActuel) {
        // Parser le mois actuel pour calculer le mois précédent
        YearMonth moisActuelParsed = YearMonth.parse(moisActuel); // ex: "2024-01"
        YearMonth moisPrecedent = moisActuelParsed.minusMonths(1);
        String moisPrecedentStr = moisPrecedent.toString(); // ex: "2023-12"

        // Requête pour récupérer le bulletin validé du mois précédent
        Optional<BulletinPaie> bulletinPrecedent = bulletinPaieRepository.findByContratEmploye_IdAndMoisAndStatut(
                        contratEmployeId,
                        moisPrecedentStr,
                        "VALIDATED"
                );

        if (bulletinPrecedent.isPresent()) {
            return bulletinPrecedent.get().getSalaireBrut().doubleValue();
        }

        return 0.0; // ou null selon ton besoin
    }


    public TableauBulletinDTO genererTableauBulletin12Mois(Long employeId) {
        // Mois courant
        LocalDate premierJourMoisCourant = LocalDate.now().withDayOfMonth(1);
        LocalDate dernierJourMoisCourant = premierJourMoisCourant.withDayOfMonth(premierJourMoisCourant.lengthOfMonth());

        // 12 mois précédents (M-12 à M-1)
        LocalDate dateDebut = premierJourMoisCourant.minusMonths(12);
        LocalDate dateFin = premierJourMoisCourant.minusDays(1);

        // --- 1. Bulletins de paie sur 12 mois glissants (hors mois courant)
        List<BulletinPaie> bulletins = bulletinPaieRepository
                .findByEmployeIdAndDateCalculSalaireBetweenOrderByDateCalculSalaireAsc(employeId, dateDebut, dateFin);

        List<LigneBulletinDTO> lignes = new ArrayList<>();
        double totalTemps = 0;
        BigDecimal totalSalaire = BigDecimal.ZERO;

        for (BulletinPaie b : bulletins) {
            double temps = b.getTempsTravail();
            BigDecimal salaire = b.getSalaireBrut() != null ? b.getSalaireBrut() : BigDecimal.ZERO;

            lignes.add(new LigneBulletinDTO(b.getMois(), temps, salaire));
            totalTemps += temps;
            totalSalaire = totalSalaire.add(salaire);
        }

        // --- 2. Jours d'absences "A_DEDUIRE_DES_CONGES" du mois courant uniquement
        List<Absence> absences = absenceRepository.findByEmployeIdAndConditionAcceptation(employeId, "A_DEDUIRE_DES_CONGES");

        long totalJoursAbsenceMoisCourant = absences.stream()
                .mapToLong(abs -> {
                    LocalDate absenceDebut = abs.getDateDebut();
                    LocalDate absenceFin = abs.getDateFin();
                    if (absenceDebut == null || absenceFin == null) return 0;

                    // Déterminer l'intersection entre la période d'absence et le mois courant
                    LocalDate debutEffectif = absenceDebut.isBefore(premierJourMoisCourant) ? premierJourMoisCourant : absenceDebut;
                    LocalDate finEffectif = absenceFin.isAfter(dernierJourMoisCourant) ? dernierJourMoisCourant : absenceFin;

                    if (debutEffectif.isAfter(finEffectif)) return 0;

                    return ChronoUnit.DAYS.between(debutEffectif, finEffectif) + 1;
                })
                .sum();

        // --- 3. Retour du résultat enrichi
        return new TableauBulletinDTO(lignes, totalTemps, totalSalaire, totalJoursAbsenceMoisCourant);
    }



}
