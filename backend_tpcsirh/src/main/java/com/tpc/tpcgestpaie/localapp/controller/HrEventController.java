package com.tpc.tpcgestpaie.localapp.controller;
import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.dto.event.HrEventCreateRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.event.HrEventRequestDTO;

import com.tpc.tpcgestpaie.localapp.model.*;
import com.tpc.tpcgestpaie.localapp.service.*;

import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.HrEvent;
import com.tpc.tpcgestpaie.localapp.service.EmployeService;
import com.tpc.tpcgestpaie.localapp.service.HrEventService;
import com.tpc.tpcgestpaie.localapp.service.CompanyService;
import com.tpc.tpcgestpaie.localapp.service.ContratEmployeService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hr-events")
@RequiredArgsConstructor
public class HrEventController {

    private final HrEventService hrEventService;
    private final EmployeService employeeService;
    private final ContratEmployeService contratService;
    private final CompanyService companyService;
    private final HrEventTypeService hrEventTypeService;

    // =========================
    // ➕ Créer un événement
    // =========================
    @PostMapping
    public ResponseEntity<ApiResponse<HrEvent>> createEvent(
            @RequestBody HrEventCreateRequestDTO dto
    ) {
        try {
            // Récupération optionnelle de la société
            Company company = null;
            if (dto.companyId() != null) {
                company = companyService.findById(dto.companyId())
                        .orElseThrow(() -> new IllegalArgumentException("Entreprise introuvable"));
            }

            // Récupération optionnelle de l'employé
            Employe employee = null;
            if (dto.employeeId() != null) {
                employee = employeeService.findById(dto.employeeId())
                        .orElseThrow(() -> new IllegalArgumentException("Employé introuvable"));
            }

            // Récupération optionnelle du contrat
            ContratEmploye contrat = null;
            if (dto.contratEmployeId() != null) {
                contrat = contratService.findById(dto.contratEmployeId()).orElse(null);
            }

            // Création de l'événement (récurrent ou unique)
            HrEvent event = Boolean.TRUE.equals(dto.recurring())
                    ? hrEventService.createRecurringEvent(
                    employee,
                    contrat,
                    company,
                    dto.type(),
                    dto.eventDate(),
                    dto.endDate(),
                    dto.title(),
                    dto.description(),
                    dto.actionRequired()
            )
                    : hrEventService.createUniqueEvent(
                    employee,
                    contrat,
                    company,
                    dto.type(),
                    dto.eventDate(),
                    dto.title(),
                    dto.description(),
                    dto.actionRequired()
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Événement créé avec succès", event));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }


    // =========================
    // ✏️ Modifier un événement
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HrEvent>> updateEvent(
            @PathVariable Long id,
            @RequestParam String type,
            @RequestParam LocalDate eventDate,
            @RequestParam boolean recurring,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam Long employeeId,
            @RequestParam(required = false) Long contratEmployeId
    ) {
        try {
            Employe employee = employeeService.findById(employeeId)
                    .orElseThrow(() -> new IllegalArgumentException("Employé introuvable"));
            ContratEmploye contrat = contratEmployeId != null
                    ? contratService.findById(contratEmployeId).orElse(null)
                    : null;

            HrEvent updated = hrEventService.updateEvent(
                    id,
                    type,
                    eventDate,
                    recurring,
                    endDate,
                    employee,
                    contrat
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Événement modifié avec succès", updated));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // =========================
    // 🗑️ Supprimer (soft delete)
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        try {
            HrEvent event = hrEventService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Événement introuvable"));

            hrEventService.deleteEvent(event);

            return ResponseEntity.ok(new ApiResponse<>(true, "Événement supprimé", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // =========================
    // 📌 Événements du jour
    // =========================
    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<HrEventRequestDTO>>> getTodayEvents(
            @RequestParam List<Long> companyIds  // Reçoit ?companyIds=1&companyIds=2...
    ) {
        try {
            List<HrEvent> events = hrEventService.getTodaysEvents(companyIds);

            List<HrEventRequestDTO> events_ = events.stream().map(event -> new HrEventRequestDTO(
                    event.getId(),
                    event.getEmployee() != null ? event.getEmployee().getId() : null,
                    null,
                    event.getContratEmploye() != null ? event.getContratEmploye().getId() : null,
                    event.getContratEmploye() != null ?  ContractEmployeDTO.fromEntity(event.getContratEmploye()) : null,
                    event.getEventType(),
                    event.getCompany() != null ? event.getCompany().getId() : null,
                    null,
                    event.getType(),
                    event.getEventDate(),
                    event.isRecurring(),
                    event.getEndDate(),
                    event.getCreatedAt(),
                    event.getTitle(),
                    event.getDescription(),
                    event.getActionRequired()
            )).toList();

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Événements du jour récupérés", events_)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // =========================
    // 📅 Événements à venir (multi-entreprises)
    // =========================
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<HrEventRequestDTO>>> getUpcomingEvents(
            @RequestParam String companyIds
    ) {
        try {
            List<Long> ids = Arrays.stream(companyIds.split(","))
                    .map(String::trim)
                    .map(Long::valueOf)
                    .toList();

            List<HrEvent> events = hrEventService.getUpcomingEventsByCompanies(ids);

            List<HrEventRequestDTO> events_ = events.stream().map(event -> new HrEventRequestDTO(
                    event.getId(),
                    event.getEmployee() != null ? event.getEmployee().getId() : null,
                   null,
                    event.getContratEmploye() != null ? event.getContratEmploye().getId() : null,
                    event.getContratEmploye() != null ?  ContractEmployeDTO.fromEntity(event.getContratEmploye()) : null,
                    event.getEventType() ,
                    event.getCompany() != null ? event.getCompany().getId() : null,
                    null,
                    event.getType(),
                    event.getEventDate(),
                    event.isRecurring(),
                    event.getEndDate(),
                    event.getCreatedAt(),
                    event.getTitle(),
                    event.getDescription(),
                    event.getActionRequired()
            )).toList();

            return ResponseEntity.ok(new ApiResponse<>(true, "Événements à venir récupérés", events_));
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // =========================
    // ⚠️ Événements à action requise à venir
    // =========================

    @GetMapping("/action-requireds")
    public ResponseEntity<ApiResponse<List<HrEventRequestDTO>>> getUpcomingActionRequiredEvents(
            @RequestParam String companyIds
    ) {
        try {
            List<Long> ids = Arrays.stream(companyIds.split(","))
                    .map(String::trim)
                    .map(Long::valueOf)
                    .toList();

            List<HrEvent> events = hrEventService.getUpcomingActionRequiredEvents(ids);

            List<HrEventRequestDTO> events_ = events.stream().map(event -> new HrEventRequestDTO(
                    event.getId(),
                    event.getEmployee() != null ? event.getEmployee().getId() : null,
                    null,
                    event.getContratEmploye() != null ? event.getContratEmploye().getId() : null,
                    event.getContratEmploye() != null ?  ContractEmployeDTO.fromEntity(event.getContratEmploye()) : null,
                    event.getEventType() ,
                    event.getCompany() != null ? event.getCompany().getId() : null,
                    null,
                    event.getType(),
                    event.getEventDate(),
                    event.isRecurring(),
                    event.getEndDate(),
                    event.getCreatedAt(),
                    event.getTitle(),
                    event.getDescription(),
                    event.getActionRequired()
            )).toList();

            return ResponseEntity.ok(new ApiResponse<>(true, "Événements à venir récupérés", events_));
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }


    // =========================
// 📊 Statistiques des événements
// =========================
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getEventStatistics(
            @RequestParam String companyIds
    ) {
        try {
            List<Long> ids = Arrays.stream(companyIds.split(","))
                    .map(String::trim)
                    .map(Long::valueOf)
                    .toList();

            Map<String, Integer> stats = hrEventService.getEventStatistics(ids);

            return ResponseEntity.ok(new ApiResponse<>(true, "Statistiques des événements récupérées", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/calendar/view")
    public ResponseEntity<ApiResponse<List<HrEventRequestDTO>>> getEventsByYearMonth(
            @RequestParam String companyIds,
            // Accepte le format "2026-12" directement depuis l'URL
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth period
    ) {
        try {
            // 1. Conversion propre des IDs
            List<Long> ids = Arrays.stream(companyIds.split(","))
                    .map(String::trim)
                    .map(Long::valueOf)
                    .toList();

            // 2. Récupération via le service en utilisant YearMonth
            List<HrEvent> events = hrEventService.getEventsByMonthAndCompanies(
                    ids,
                    period
            );

            // 3. Mapping vers DTO (en utilisant la méthode mapToDTO créée plus haut)
            List<HrEventRequestDTO> eventsDTO = events.stream()
                    .map(hrEventService::mapToDTO)
                    .toList();

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Calendrier récupéré pour " + period, eventsDTO)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "Erreur de format ou de données : " + e.getMessage(), null));
        }
    }

}
