package com.tpc.tpcgestpaie.localapp.service.jourtravail;

import com.tpc.tpcgestpaie.localapp.dto.jourtravail.JoursTravaillesDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.jourtravail.JoursTravailles;
import com.tpc.tpcgestpaie.localapp.repository.jourtravail.JoursTravaillesRepository;
import com.tpc.tpcgestpaie.localapp.service.CompanyService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class JoursTravaillesService {

    private final JoursTravaillesRepository repository;
    private final CompanyService companyService;

    public JoursTravaillesService(
            JoursTravaillesRepository repository,
            CompanyService companyService) {
        this.repository = repository;
        this.companyService = companyService;
    }

    // Récupérer les jours travaillés d'une entreprise
    public JoursTravaillesDTO getByCompanyId(Long companyId) {
        Optional<JoursTravailles> joursTravaillesOpt = repository.findByCompanyId(companyId);

        if (joursTravaillesOpt.isPresent()) {
            return toDTO(joursTravaillesOpt.get());
        }

        // Retourner une configuration par défaut (Lun-Ven)
        return getConfigurationDefaut(companyId);
    }

    // Créer ou mettre à jour
    @Transactional
    public JoursTravaillesDTO saveOrUpdate(JoursTravaillesDTO dto) {
        Optional<JoursTravailles> existingOpt = repository.findByCompanyId(dto.getCompanyId());

        JoursTravailles joursTravailles;
        if (existingOpt.isPresent()) {
            joursTravailles = existingOpt.get();
        } else {
            joursTravailles = new JoursTravailles();
            Company company = companyService.findById(dto.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));
            joursTravailles.setCompany(company);
        }

        // Mettre à jour les jours
        joursTravailles.setLundi(dto.isLundi());
        joursTravailles.setMardi(dto.isMardi());
        joursTravailles.setMercredi(dto.isMercredi());
        joursTravailles.setJeudi(dto.isJeudi());
        joursTravailles.setVendredi(dto.isVendredi());
        joursTravailles.setSamedi(dto.isSamedi());
        joursTravailles.setDimanche(dto.isDimanche());

        JoursTravailles saved = repository.save(joursTravailles);
        return toDTO(saved);
    }

    // Configurations prédéfinies
    public JoursTravaillesDTO setConfigurationStandard(Long companyId, String type) {
        JoursTravaillesDTO dto = new JoursTravaillesDTO();
        dto.setCompanyId(companyId);

        switch (type.toUpperCase()) {
            case "LUN_VEN":
                dto.setLundi(true);
                dto.setMardi(true);
                dto.setMercredi(true);
                dto.setJeudi(true);
                dto.setVendredi(true);
                dto.setSamedi(false);
                dto.setDimanche(false);
                break;

            case "LUN_SAM":
                dto.setLundi(true);
                dto.setMardi(true);
                dto.setMercredi(true);
                dto.setJeudi(true);
                dto.setVendredi(true);
                dto.setSamedi(true);
                dto.setDimanche(false);
                break;

            case "LUN_DIM":
                dto.setLundi(true);
                dto.setMardi(true);
                dto.setMercredi(true);
                dto.setJeudi(true);
                dto.setVendredi(true);
                dto.setSamedi(true);
                dto.setDimanche(true);
                break;

            default:
                throw new RuntimeException("Type de configuration inconnu: " + type);
        }

        return saveOrUpdate(dto);
    }

    // Vérifier si un jour est travaillé
    public boolean estJourTravaille(Long companyId, String jour) {
        JoursTravaillesDTO dto = getByCompanyId(companyId);
        return dto.estJourTravaille(jour);
    }

    // Calculer le nombre de jours travaillés entre deux dates
    public int calculerJoursTravailles(Long companyId, String dateDebut, String dateFin) {
        JoursTravaillesDTO dto = getByCompanyId(companyId);
        // Implémenter la logique de calcul des jours entre deux dates
        // en excluant les jours non travaillés
        return calculerJoursTravaillesEntreDates(dto, dateDebut, dateFin);
    }

    private int calculerJoursTravaillesEntreDates(JoursTravaillesDTO dto, String dateDebut, String dateFin) {
        // TODO: Implémenter le calcul réel
        // Utiliser LocalDate pour parcourir les jours entre dateDebut et dateFin
        // Compter uniquement les jours travaillés selon la configuration
        return 0;
    }

    // Méthodes utilitaires
    private JoursTravaillesDTO getConfigurationDefaut(Long companyId) {
        JoursTravaillesDTO dto = new JoursTravaillesDTO();
        dto.setCompanyId(companyId);
        dto.setLundi(true);
        dto.setMardi(true);
        dto.setMercredi(true);
        dto.setJeudi(true);
        dto.setVendredi(true);
        dto.setSamedi(false);
        dto.setDimanche(false);
        dto.setJoursSemaine(5);
        dto.setDescription("Lun Mar Mer Jeu Ven");
        return dto;
    }

    private JoursTravaillesDTO toDTO(JoursTravailles entity) {
        JoursTravaillesDTO dto = new JoursTravaillesDTO();
        dto.setId(entity.getId());
        dto.setCompanyId(entity.getCompany().getId());
        dto.setCompanyName(entity.getCompany().getName());
        dto.setLundi(entity.isLundi());
        dto.setMardi(entity.isMardi());
        dto.setMercredi(entity.isMercredi());
        dto.setJeudi(entity.isJeudi());
        dto.setVendredi(entity.isVendredi());
        dto.setSamedi(entity.isSamedi());
        dto.setDimanche(entity.isDimanche());
        dto.setJoursSemaine(entity.getJoursSemaine());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    // Dans JoursTravaillesService.java

    public int calculerJoursTravaillesEntreDates(Long companyId, LocalDate dateDebut, LocalDate dateFin) {
        JoursTravaillesDTO config = getByCompanyId(companyId);
        return calculerJoursTravaillesEntreDates(config, dateDebut, dateFin);
    }

    private int calculerJoursTravaillesEntreDates(JoursTravaillesDTO config, LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            return 0;
        }

        if (dateDebut.isAfter(dateFin)) {
            return 0;
        }

        int joursOuvres = 0;
        LocalDate currentDate = dateDebut;

        while (!currentDate.isAfter(dateFin)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            String jourNom = getNomJour(dayOfWeek);

            if (config.estJourTravaille(jourNom)) {
                joursOuvres++;
            }

            currentDate = currentDate.plusDays(1);
        }

        return joursOuvres;
    }

    private String getNomJour(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "lundi";
            case TUESDAY -> "mardi";
            case WEDNESDAY -> "mercredi";
            case THURSDAY -> "jeudi";
            case FRIDAY -> "vendredi";
            case SATURDAY -> "samedi";
            case SUNDAY -> "dimanche";
        };
    }


    public int calculerJoursCongePris(Long companyId,
                                      LocalDate dateDebut,
                                      LocalDate dateFin) {

        if (dateDebut == null || dateFin == null) {
            return 0;
        }

        if (dateDebut.isAfter(dateFin)) {
            return 0;
        }

        // récupérer l'entreprise
        Company company = companyService.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Entreprise non trouvée"));

        BigDecimal joursCongeParMois = BigDecimal.valueOf(company.getNbrJourConge());

        // sécurité si null
        if (joursCongeParMois == null) {
            joursCongeParMois = BigDecimal.valueOf(2.5);
        }

        // CAS 1 : 2,5 jours/mois → jours calendaires
        if (joursCongeParMois.compareTo(BigDecimal.valueOf(2.5)) == 0) {

            return (int) java.time.temporal.ChronoUnit.DAYS
                    .between(dateDebut, dateFin) + 1;
        }

        // CAS 2 : 2 jours/mois → jours travaillés uniquement

        JoursTravaillesDTO config = getByCompanyId(companyId);

        int joursOuvres = 0;
        LocalDate currentDate = dateDebut;

        while (!currentDate.isAfter(dateFin)) {

            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            String jourNom = getNomJour(dayOfWeek);

            if (config.estJourTravaille(jourNom)) {
                joursOuvres++;
            }

            currentDate = currentDate.plusDays(1);
        }

        return joursOuvres;
    }

}