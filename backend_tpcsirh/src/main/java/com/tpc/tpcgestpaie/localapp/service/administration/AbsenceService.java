package com.tpc.tpcgestpaie.localapp.service.administration;

import com.tpc.tpcgestpaie.localapp.dto.administration.AbsenceDTO;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.AbsenceRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.CreditCongeRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.MotifAbsenceRepository;
import com.tpc.tpcgestpaie.localapp.repository.administration.TypeAbsenceRepository;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.service.jourtravail.JoursTravaillesService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AbsenceService {

    private final AbsenceRepository absenceRepository;
    private final EmployeRepository employeRepository;
    private final ContratEmployeRepository contratRepository;
    private final CompanyRepository companyRepository;
    private final MotifAbsenceRepository motifAbsenceRepository;
    private final TypeAbsenceRepository typeAbsenceRepository;
    private final CreditCongeRepository creditCongeRepository;
    private final UserService userService;
    private final JoursTravaillesService joursTravaillesService;

    public AbsenceService(AbsenceRepository absenceRepository, EmployeRepository employeRepository,
                          ContratEmployeRepository contratRepository, CompanyRepository companyRepository,
                          MotifAbsenceRepository motifAbsenceRepository, TypeAbsenceRepository typeAbsenceRepository,
                          CreditCongeRepository creditCongeRepository, UserService userService, JoursTravaillesService joursTravaillesService) {
        this.absenceRepository = absenceRepository;
        this.employeRepository = employeRepository;
        this.contratRepository = contratRepository;
        this.companyRepository = companyRepository;
        this.motifAbsenceRepository = motifAbsenceRepository;
        this.typeAbsenceRepository = typeAbsenceRepository;
        this.creditCongeRepository = creditCongeRepository;
        this.userService = userService;
        this.joursTravaillesService = joursTravaillesService;
    }

    // === MÉTHODES PAGINÉES ===

    public Page<AbsenceDTO> findAll(Pageable pageable) {
        Page<Absence> absencesPage = absenceRepository.findAll(pageable);
        return absencesPage.map(this::toDTO);
    }

    public Page<AbsenceDTO> findByCompany(Long companyId, Pageable pageable) {
        Page<Absence> absencesPage = absenceRepository.findByCompanyId(companyId, pageable);
        return absencesPage.map(this::toDTO);
    }

    public Page<AbsenceDTO> findByEmploye(Long employeId, Pageable pageable) {
        Page<Absence> absencesPage = absenceRepository.findByEmployeId(employeId, pageable);
        return absencesPage.map(this::toDTO);
    }


    // Votre méthode findById devrait retourner un Optional<AbsenceDTO>
    public Optional<AbsenceDTO> findById(Long id) {
        Optional<Absence> absenceOpt = absenceRepository.findById(id);

        return absenceOpt.map(AbsenceDTO::fromEntity);
    }

    public Page<AbsenceDTO> filterByMotifOrPeriode(Long typeId, LocalDate dateDebut, LocalDate dateFin, Pageable pageable) {
        Page<Absence> absencesPage = absenceRepository.filterByMotifOrPeriode(typeId, dateDebut, dateFin, pageable);
        return absencesPage.map(this::toDTO);
    }

    public Page<AbsenceDTO> findAbsencesValidesByContratEmployeId(Long contratEmployeId, Pageable pageable) {
        Page<Absence> absencesPage = absenceRepository.findAbsencesValidesByContratEmployeId(contratEmployeId, pageable);
        return absencesPage.map(this::toDTO);
    }

    public Page<AbsenceDTO> findAbsencesDeduitesDuMois(Long employeId, int mois, int annee, Pageable pageable) {
        Page<Absence> absencesPage = absenceRepository.findAbsencesDeduitesDuMois(employeId, mois, annee, pageable);
        return absencesPage.map(this::toDTO);
    }

    public Page<AbsenceDTO> findByEmployeIdAndConditionAcceptationAndDateIntersects(
            Long employeId, String conditionAcceptation, LocalDate debut, LocalDate fin, Pageable pageable) {
        Page<Absence> absencesPage = absenceRepository.findByEmployeIdAndConditionAcceptationAndDateIntersects(
                employeId, conditionAcceptation, debut, fin, pageable);
        return absencesPage.map(this::toDTO);
    }

    // === MÉTHODES NON PAGINÉES (pour compatibilité) ===

    public List<AbsenceDTO> findAll() {
        Pageable pageable = Pageable.unpaged();
        Page<Absence> absencesPage = absenceRepository.findAll(pageable);
        return absencesPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AbsenceDTO> findByCompany(Long companyId) {
        Pageable pageable = Pageable.unpaged();
        Page<Absence> absencesPage = absenceRepository.findByCompanyId(companyId, pageable);
        return absencesPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AbsenceDTO> findByEmploye(Long employeId) {
        Pageable pageable = Pageable.unpaged();
        Page<Absence> absencesPage = absenceRepository.findByEmployeId(employeId, pageable);
        return absencesPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }



    @Transactional
    public AbsenceDTO save(AbsenceDTO dto) {
        Employe employe = employeRepository.findById(dto.getEmployeId())
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));
        ContratEmploye contratEmploye = contratRepository.findById(dto.getContratEmployeId())
                .orElseThrow(() -> new RuntimeException("Contrat employé introuvable"));
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Entreprise introuvable"));
        TypeAbsence typeAbsence = typeAbsenceRepository.findById(dto.getTypeAbsenceId())
                .orElseThrow(() -> new RuntimeException("Type d'absence introuvable"));
        CreditConge creditConge = dto.getCreditCongeId() != null
                ? creditCongeRepository.findById(dto.getCreditCongeId()).orElse(null)
                : null;
        User currentUser = userService.getCurrentUser();

        // ✅ CALCUL AMÉLIORÉ - Prend en compte les jours travaillés de l'entreprise
        if (dto.getDateDebut() != null && dto.getDateFin() != null) {
            int joursOuvres = joursTravaillesService.calculerJoursCongePris(
                    dto.getCompanyId(),
                    dto.getDateDebut(),
                    dto.getDateFin()
            );
            dto.setDuree(String.valueOf(joursOuvres));
        } else {
            dto.setDuree(null);
        }

        Absence absence = toEntity(dto, employe, contratEmploye, company, typeAbsence, creditConge, currentUser);
        Absence saved = absenceRepository.save(absence);
        return toDTO(saved);
    }

    public void delete(Long id) {
        absenceRepository.deleteById(id);
    }

    public void deleteById(Long id) {
        absenceRepository.deleteById(id);
    }

    public int getDureeTotalAbsenceByIdContratEmploye(Long idContratEmploye) {
        Pageable pageable = Pageable.unpaged();
        Page<Absence> absencesPage = absenceRepository.findAbsencesValidesByContratEmployeId(idContratEmploye, pageable);
        List<Absence> absences = absencesPage.getContent();

        int total = 0;
        for (Absence absence : absences) {
            try {
                if (absence.getDuree() != null && !absence.getDuree().isEmpty()) {
                    double jours = Double.parseDouble(absence.getDuree());
                    total += (int) Math.round(jours);
                }
            } catch (NumberFormatException e) {
                // log optionnel si une valeur est mal formatée
            }
        }
        return total;
    }

    public List<Absence> getAbsencesDeductibles(Long employeId, LocalDate debut, LocalDate fin) {
        Pageable pageable = Pageable.unpaged();
        Page<Absence> absencesPage = absenceRepository.findByEmployeIdAndConditionAcceptationAndDateIntersects(
                employeId, "A_DEDUIRE_DES_CONGES", debut, fin, pageable);
        return absencesPage.getContent();
    }

    public int calculateCreditConge(LocalDate dateReference) {
        LocalDate now = LocalDate.now();
        long months = ChronoUnit.MONTHS.between(dateReference.withDayOfMonth(1), now.withDayOfMonth(1));
        return (int) months * 2;
    }

    // === MAPPING ===

    private AbsenceDTO toDTO(Absence entity) {
        AbsenceDTO dto = new AbsenceDTO();
        dto.setId(entity.getId());
        dto.setEmployeId(entity.getEmploye() != null ? entity.getEmploye().getId() : null);
        dto.setContratEmployeId(entity.getContratEmploye() != null ? entity.getContratEmploye().getId() : null);
        dto.setCompanyId(entity.getCompany() != null ? entity.getCompany().getId() : null);
        dto.setTypeAbsenceId(entity.getTypeAbsence() != null ? entity.getTypeAbsence().getId() : null);
        dto.setCreditCongeId(entity.getCreditConge() != null ? entity.getCreditConge().getId() : null);
        dto.setLibelle(entity.getLibelle());
        dto.setModeJouissance(entity.getModeJouissance());
        dto.setConditionAcceptation(entity.getConditionAcceptation());
        dto.setDateDebut(entity.getDateDebut());
        dto.setDateFin(entity.getDateFin());
        dto.setDuree(entity.getDuree());
        dto.setAddedById(entity.getAdded_by() != null ? entity.getAdded_by().getId() : null);
        dto.setCreatedAt(entity.getCreated_at());
        return dto;
    }

    private Absence toEntity(AbsenceDTO dto, Employe employe, ContratEmploye contratEmploye, Company company,
                             TypeAbsence typeAbsence, CreditConge creditConge, User addedBy) {
        Absence entity = new Absence();
        entity.setId(dto.getId());
        entity.setEmploye(employe);
        entity.setContratEmploye(contratEmploye);
        entity.setCompany(company);
        entity.setTypeAbsence(typeAbsence);
        entity.setCreditConge(creditConge);
        entity.setLibelle(dto.getLibelle());
        entity.setModeJouissance(dto.getModeJouissance());
        entity.setConditionAcceptation(dto.getConditionAcceptation());
        entity.setDateDebut(dto.getDateDebut());
        entity.setDateFin(dto.getDateFin());
        entity.setDuree(dto.getDuree());
        entity.setAdded_by(addedBy);
        entity.setCreated_at(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
        return entity;
    }
}