package com.tpc.tpcgestpaie.localapp.model.numerisation.parcours;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "employee_events")
@Data
public class EmployeeEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private EventCategory category;

    @Column(nullable = false)
    private Long employeId;

    private LocalDate eventDate;

    private String label;

    @Column(columnDefinition = "TEXT")
    private String extractedContent;

    private String proofFilePath;

    private boolean isLegacy;

    private Double confidenceScore;

    private LocalDate startDate;
    private LocalDate endDate;

}
