package com.tpc.tpcgestpaie.localapp.service.numerisation.analysis;

import com.tpc.tpcgestpaie.localapp.model.numerisation.EmployeeDocument;
import com.tpc.tpcgestpaie.localapp.model.numerisation.parcours.EmployeeEvent;
import com.tpc.tpcgestpaie.localapp.model.numerisation.parcours.EventCategory;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.parcours.EmployeeEventRepository;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.parcours.EventCategoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ClassificationDecisionService {

    private final EmployeeEventRepository eventRepository;
    private final EventCategoryRepository categoryRepository;

    public ClassificationDecisionService(EmployeeEventRepository eventRepository,
                                         EventCategoryRepository categoryRepository) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
    }

    public EmployeeEvent createEvent(DocumentAnalysisResult analysis,
                                     Long matricule,
                                     EmployeeDocument document,
                                     boolean isLegacy) {

        EventCategory category = categoryRepository
                .findById(analysis.getCategoryId())
                .orElseThrow();

        EmployeeEvent event = new EmployeeEvent();
        event.setCategory(category);
        event.setEmployeId(matricule);
        event.setLabel(analysis.getCategoryLabel());
        event.setExtractedContent(document.getFileName());
        event.setProofFilePath(document.getFilePath());
        event.setLegacy(isLegacy);
        event.setConfidenceScore(analysis.getScore());
        event.setEventDate(LocalDate.now());

        return eventRepository.save(event);
    }
}
