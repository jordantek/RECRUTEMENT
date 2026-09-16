package com.tpc.tpcgestpaie.localapp.service;


import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.event.HrAlertSettingsResponsDTO;
import com.tpc.tpcgestpaie.localapp.dto.event.HrEventRequestDTO;
import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.repository.HrEventRepository;
import com.tpc.tpcgestpaie.localapp.repository.HrEventTypeRepository;
import com.tpc.tpcgestpaie.localapp.util.HrEventTypes;
import jakarta.transaction.Transactional;

import com.tpc.tpcgestpaie.localapp.model.HrEvent;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Company;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HrEventService {

    private final HrEventRepository hrEventRepository;
    private final HrEventTypeRepository hrEventTypeRepository;
    private final UserService userService;
    private final HrAlertSettingsService hrAlertSettingsService;

    public java.util.Optional<HrEvent> findById(Long eventId) {
        if (eventId == null) {
            return java.util.Optional.empty();
        }
        return hrEventRepository.findById(eventId)
                .filter(e -> e.getDeletedAt() == null); // on ignore les événements supprimés
    }

    public HrEvent getByIdOrThrow(Long eventId) {
        return hrEventRepository.findById(eventId)
                .filter(e -> e.getDeletedAt() == null)
                .orElseThrow(() -> new IllegalArgumentException("Événement introuvable ou supprimé"));
    }

    // Événement unique
    public HrEvent createUniqueEvent(
            Employe employee,
            ContratEmploye contrat,
            Company company,
            String type,
            LocalDate date,
            String title,
            String description
    ) {
        boolean exists = hrEventRepository
                .existsByCompanyAndEmployeeAndContratEmployeAndTypeAndEventDateAndRecurringFalseAndDeletedAtIsNull(
                        company, employee, contrat, type, date
                );

        if (exists) {
            throw new IllegalStateException("Un événement identique existe déjà");
        }

        HrEventType eventType = hrEventTypeRepository.findBySlug(type)
                .orElseThrow(() -> new RuntimeException("Event type introuvable"));

        HrEvent event = HrEvent.builder()
                .employee(employee)
                .contratEmploye(contrat)
                .company(company)
                .type(type)
                .eventDate(date)
                .title(title)
                .description(description) // 👈 peut être null
                .recurring(false)
                .eventType(eventType)
                .build();

        return hrEventRepository.save(event);
    }

    public HrEvent createUniqueEvent(
            Employe employee,
            ContratEmploye contrat,
            Company company,
            String type,
            LocalDate date,
            String title,
            String description, // 👈 ajout
            Boolean actionRequired
    ) {

        boolean exists = hrEventRepository
                .existsByCompanyAndEmployeeAndContratEmployeAndTypeAndEventDateAndRecurringFalseAndDeletedAtIsNull(
                        company, employee, contrat, type, date
                );

        if (exists) {
            throw new IllegalStateException("Un événement identique existe déjà");
        }
        HrEventType eventType = hrEventTypeRepository.findBySlug(type)
                .orElseThrow(() -> new RuntimeException("Event type introuvable"));

        HrEvent event = HrEvent.builder()
                .employee(employee)
                .contratEmploye(contrat)
                .company(company)
                .type(type)
                .eventDate(date)
                .title(title)
                .description(description) // 👈 peut être null
                .recurring(false)
                .eventType(eventType)
                .actionRequired(actionRequired)
                .build();

        return hrEventRepository.save(event);
    }


    public HrEvent createRecurringEvent(
            Employe employee,
            ContratEmploye contrat,
            Company company,
            String type,
            LocalDate baseDate,
            LocalDate endDate,
            String title,
            String description // 👈 ajout
    ) {

        boolean exists = hrEventRepository
                .existsByCompanyAndEmployeeAndTypeAndRecurringTrueAndDeletedAtIsNull(
                        company, employee, type
                );

        if (exists) {
            throw new IllegalStateException("Un événement récurrent de ce type existe déjà pour cet employé");
        }

        HrEventType eventType = hrEventTypeRepository.findBySlug(type)
                .orElseThrow(() -> new RuntimeException("Event type introuvable"));

        HrEvent event = HrEvent.builder()
                .employee(employee)
                .contratEmploye(contrat)
                .company(company)
                .type(type)
                .eventDate(baseDate)
                .title(title)
                .description(description) // 👈 peut être null
                .recurring(true)
                .endDate(endDate)
                .eventType(eventType)
                .build();

        return hrEventRepository.save(event);
    }

    public HrEvent createRecurringEvent(
            Employe employee,
            ContratEmploye contrat,
            Company company,
            String type,
            LocalDate baseDate,
            LocalDate endDate,
            String title,
            String description ,// 👈 ajout
            Boolean actionRequired
    ) {

        boolean exists = hrEventRepository
                .existsByCompanyAndEmployeeAndTypeAndRecurringTrueAndDeletedAtIsNull(
                        company, employee, type
                );

        if (exists) {
            throw new IllegalStateException("Un événement récurrent de ce type existe déjà pour cet employé");
        }
        HrEventType eventType = hrEventTypeRepository.findBySlug(type)
                .orElseThrow(() -> new RuntimeException("Event type introuvable"));

        HrEvent event = HrEvent.builder()
                .employee(employee)
                .contratEmploye(contrat)
                .company(company)
                .type(type)
                .eventDate(baseDate)
                .title(title)
                .description(description) // 👈 peut être null
                .recurring(true)
                .endDate(endDate)
                .actionRequired(actionRequired)
                .eventType(eventType)
                .build();

        return hrEventRepository.save(event);
    }

    // ---------------------------
// Événement UNIQUE - CREATE OR UPDATE
// ---------------------------
    public HrEvent upsertUniqueEvent(
            Employe employee,
            ContratEmploye contrat,
            Company company,
            String type,
            LocalDate date,
            String title,
            String description,
            Boolean actionRequired
    ) {
        HrEvent event = hrEventRepository
                .findByCompanyAndEmployeeAndContratEmployeAndTypeAndEventDateAndRecurringFalseAndDeletedAtIsNull(
                        company, employee, contrat, type, date
                )
                .orElse(HrEvent.builder()
                        .employee(employee)
                        .contratEmploye(contrat)
                        .company(company)
                        .type(type)
                        .eventDate(date)
                        .recurring(false)
                        .eventType(hrEventTypeRepository.findBySlug(type)
                                .orElseThrow(() -> new RuntimeException("Event type introuvable")))
                        .build()
                );

        // Mise à jour des champs
        event.setTitle(title);
        event.setDescription(description);
        event.setActionRequired(actionRequired);

        return hrEventRepository.save(event);
    }

    // ---------------------------
// Événement RÉCURRENT - CREATE OR UPDATE
// ---------------------------
    public HrEvent upsertRecurringEvent(
            Employe employee,
            ContratEmploye contrat,
            Company company,
            String type,
            LocalDate baseDate,
            LocalDate endDate,
            String title,
            String description,
            Boolean actionRequired
    ) {
        HrEvent event = hrEventRepository
                .findByCompanyAndEmployeeAndTypeAndRecurringTrueAndDeletedAtIsNull(
                        company, employee, type
                )
                .orElse(HrEvent.builder()
                        .employee(employee)
                        .contratEmploye(contrat)
                        .company(company)
                        .type(type)
                        .eventDate(baseDate)
                        .recurring(true)
                        .eventType(hrEventTypeRepository.findBySlug(type)
                                .orElseThrow(() -> new RuntimeException("Event type introuvable")))
                        .build()
                );

        // Mise à jour des champs
        event.setTitle(title);
        event.setDescription(description);
        event.setActionRequired(actionRequired);
        event.setEndDate(endDate);

        return hrEventRepository.save(event);
    }

    public HrEvent updateEvent(
            Long eventId,
            String type,
            LocalDate eventDate,
            boolean recurring,
            LocalDate endDate,
            Employe employee,
            ContratEmploye contratEmploye
    ) {

        HrEvent event = hrEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Événement introuvable"));

        if (event.getDeletedAt() != null) {
            throw new IllegalStateException("Impossible de modifier un événement supprimé");
        }

        Company company = event.getCompany();

        // =========================
        // 🚫 Anti-doublon
        // =========================
        boolean duplicate;

        if (recurring) {
            duplicate = hrEventRepository
                    .existsByIdNotAndCompanyAndEmployeeAndTypeAndRecurringTrueAndDeletedAtIsNull(
                            eventId,
                            company,
                            employee,
                            type
                    );
        } else {
            duplicate = hrEventRepository
                    .existsByIdNotAndCompanyAndEmployeeAndContratEmployeAndTypeAndEventDateAndRecurringFalseAndDeletedAtIsNull(
                            eventId,
                            company,
                            employee,
                            contratEmploye,
                            type,
                            eventDate
                    );
        }

        if (duplicate) {
            throw new IllegalStateException("Un événement identique existe déjà");
        }

        HrEventType eventType = hrEventTypeRepository.findBySlug(type)
                .orElseThrow(() -> new RuntimeException("Event type introuvable"));

        event.setType(type);
        event.setEventType(eventType);
        event.setEventDate(eventDate);
        event.setRecurring(recurring);
        event.setEndDate(recurring ? endDate : null);
        event.setEmployee(employee);
        event.setContratEmploye(contratEmploye);

        return hrEventRepository.save(event);
    }

    // Récupération des événements à venir pour 1 entreprise
    public List<HrEvent> getUpcomingEvents(Company company, LocalDate from, LocalDate to) {
        return hrEventRepository.findAll().stream()
                .filter(e -> e.getCompany().equals(company))
                .filter(e -> e.getDeletedAt() == null)
                .filter(e -> {
                    if (e.isRecurring() && e.getEventDate()!=null) {
                        LocalDate base = e.getEventDate();
                        LocalDate nextOccurrence = LocalDate.of(from.getYear(), base.getMonthValue(), base.getDayOfMonth());
                        return !nextOccurrence.isBefore(from) && !nextOccurrence.isAfter(to)
                                && (e.getEndDate() == null || !e.getEndDate().isBefore(from));
                    } else {
                        return !e.getEventDate().isBefore(from) && !e.getEventDate().isAfter(to);
                    }
                })
                .collect(Collectors.toList());
    }

    // Récupération des événements à venir pour plusieurs entreprises
    public List<HrEvent> getUpcomingEventsByCompanies(
            List<Long> companyIds,
            LocalDate from,
            LocalDate to
    ) {
        return hrEventRepository.findAll().stream()
                .filter(e -> e.getCompany() != null)
                .filter(e -> companyIds.contains(e.getCompany().getId()))
                .filter(e -> e.getDeletedAt() == null)
                .filter(e -> e.getEventDate() != null) // <-- très important !
                .filter(e -> {
                    if (e.isRecurring()) {
                        LocalDate base = e.getEventDate();
                        LocalDate nextOccurrence = LocalDate.of(from.getYear(), base.getMonthValue(), base.getDayOfMonth());
                        boolean inRange = !nextOccurrence.isBefore(from) && !nextOccurrence.isAfter(to);
                        boolean stillValid = e.getEndDate() == null || !e.getEndDate().isBefore(from);
                        return inRange && stillValid;
                    } else {
                        // ⚡ Ici eventDate est garanti non-null
                        return !e.getEventDate().isBefore(from) && !e.getEventDate().isAfter(to);
                    }
                })
                .collect(Collectors.toList());
    }

    // Récupération des événements à venir pour plusieurs entreprises
    public List<HrEvent> getUpcomingEventsByCompanies(List<Long> companyIds) {

        User currentUser = userService.getCurrentUser();
        HrAlertSettingsResponsDTO config =
                hrAlertSettingsService.findByUserId(currentUser.getId());

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.plusDays(1);
        LocalDate endDate = today.plusDays(config.upcomingEventsDays());

        return hrEventRepository.findUpcomingEvents(companyIds, startDate, endDate);
    }


    // Récupération des événements du jour
    public List<HrEvent> getTodaysEvents(List<Long> companyIds) {
        LocalDate today = LocalDate.now();
        return getUpcomingEventsByCompanies(companyIds, today, today);
    }


    public List<HrEvent> getTodaysEventsByCompanyIds(List<Long> companyIds) {

        if (companyIds == null || companyIds.isEmpty()) {
            return List.of();
        }

        LocalDate today = LocalDate.now();

        return hrEventRepository.findAll().stream()
                .filter(e -> e.getCompany() != null)
                .filter(e -> companyIds.contains(e.getCompany().getId()))
                .filter(e -> e.getDeletedAt() == null)
                .filter(e -> isEventToday(e, today))
                .collect(Collectors.toList());
    }

    /**
     * Retourne des statistiques sur les événements pour les entreprises données
     * @param companyIds Liste des IDs des entreprises
     * @return Map avec "today" et "upcoming" => nombre d'événements
     */
    public Map<String, Integer> getEventStatistics(List<Long> companyIds) {
        Map<String, Integer> stats = new HashMap<>();

        if (companyIds == null || companyIds.isEmpty()) {
            stats.put("today", 0);
            stats.put("upcoming", 0);
            stats.put("actionRequired", 0);
            return stats;
        }
        User currentUser = userService.getCurrentUser();
        HrAlertSettingsResponsDTO config = hrAlertSettingsService.findByUserId(currentUser.getId());

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(config.upcomingEventsDays());

        // Événements du jour
        int todayCount = getTodaysEvents(companyIds).size();

        // Événements à venir (hors aujourd'hui)
        int upcomingCount = getUpcomingEventsByCompanies(companyIds, today.plusDays(1), endDate).size();

        // Événements à action requise
        int actionRequiredCount = getUpcomingActionRequiredEvents(companyIds).size();

        stats.put("today", todayCount);
        stats.put("upcoming", upcomingCount);
        stats.put("actionRequired", actionRequiredCount);

        return stats;
    }
    /**
     * Récupération des événements à action requise à venir
     */
    // Récupération des événements à venir pour plusieurs entreprises
    public List<HrEvent> getUpcomingActionRequiredEvents(List<Long> companyIds) {
        // Récupération de l'utilisateur courant
        User currentUser = userService.getCurrentUser();

        System.out.println("User : " + currentUser.getUsername());
        // Récupération de la configuration des alertes pour l'utilisateur
        HrAlertSettingsResponsDTO config = hrAlertSettingsService.findByUserId(currentUser.getId());

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(config.requiredActionsDays());

        return hrEventRepository.findUpcomingActionRequiredEvents(
                companyIds,
                today,
                endDate
        );
    }

    public List<HrEvent> getEventsByMonthAndCompanies(
            List<Long> companyIds,
            int year,
            int month
    ) {

        if (companyIds == null || companyIds.isEmpty()) {
            return List.of();
        }

        LocalDate startOfMonth = LocalDate.of(year, month, 1);

        return hrEventRepository.findEventsByMonthAndCompanies(
                companyIds,
                year,
                month,
                startOfMonth
        );
    }

    public List<HrEvent> getEventsByMonthAndCompanies(
            List<Long> companyIds,
            YearMonth date
    ) {
        return getEventsByMonthAndCompanies(
                companyIds,
                date.getYear(),
                date.getMonthValue()
        );
    }

    // =========================
    // Soft delete / restore
    // =========================

    public void deleteEvent(HrEvent event) {
        event.softDelete();
        hrEventRepository.save(event);
    }

    public void restoreEvent(HrEvent event) {
        event.restore();
        hrEventRepository.save(event);
    }

    private boolean isEventToday(HrEvent event, LocalDate today) {

        // 🔁 Événements récurrents (anniversaires)
        if (event.isRecurring()) {

            LocalDate base = event.getEventDate();

            boolean sameDayMonth =
                    base.getDayOfMonth() == today.getDayOfMonth()
                            && base.getMonth() == today.getMonth();

            boolean stillValid =
                    event.getEndDate() == null || !event.getEndDate().isBefore(today);

            return sameDayMonth && stillValid;
        }
        // 🎯 Événement unique
        return event.getEventDate().isEqual(today);
    }



    /**
     * Transforme une entité HrEvent en HrEventRequestDTO pour l'envoi au frontend.
     * Centraliser cette logique ici permet de garder le contrôleur propre (DRY).
     */
    public HrEventRequestDTO mapToDTO(HrEvent event) {
        return new HrEventRequestDTO(
                event.getId(),
                event.getEmployee() != null ? event.getEmployee().getId() : null,
                null,
                event.getContratEmploye() != null ? event.getContratEmploye().getId() : null,
                // Conversion du contrat en DTO (pour affichage riche côté front)
                event.getContratEmploye() != null ? ContractEmployeDTO.fromEntity(event.getContratEmploye()) : null,
                // Type d'événement complet (objet HrEventType)
                event.getEventType(),
                // ID de l'entreprise
                event.getCompany() != null ? event.getCompany().getId() : null,
                // Détails de l'entreprise (si nécessaire, sinon null)
                null,
                // Slug ou code du type d'événement
                event.getType(),
                // Date de l'événement
                event.getEventDate(),
                // Est-ce récurrent ?
                event.isRecurring(),
                // Date de fin (pour les récurrents)
                event.getEndDate(),
                // Date de création
                event.getCreatedAt(),
                // Titre de l'alerte
                event.getTitle(),
                // Description optionnelle
                event.getDescription(),
                // Action RH requise ou non
                event.getActionRequired()
        );
    }
}
