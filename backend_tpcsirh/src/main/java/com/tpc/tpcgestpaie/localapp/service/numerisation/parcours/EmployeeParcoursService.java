package com.tpc.tpcgestpaie.localapp.service.numerisation.parcours;

import com.tpc.tpcgestpaie.localapp.dto.numerisation.parcours.*;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.numerisation.parcours.EmployeeEvent;
import com.tpc.tpcgestpaie.localapp.model.numerisation.parcours.EventCategory;
import com.tpc.tpcgestpaie.localapp.repository.EmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.parcours.EmployeeEventRepository;
import com.tpc.tpcgestpaie.localapp.repository.numerisation.parcours.EventCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeParcoursService {

    private final EmployeRepository employeeRepository;
    private final EmployeeEventRepository eventRepository;
    private final EventCategoryRepository categoryRepository;

    public EmployeeParcoursDTO generate(Long employeeId) {

        Employe employe =
                employeeRepository.findById(employeeId)
                        .orElseThrow();

        List<EventCategory> categories =
                categoryRepository.findAllByOrderByDisplayOrderAsc();

        EmployeeParcoursDTO dto = new EmployeeParcoursDTO();

        dto.setEmployeId(employeeId);
        dto.setNom(employe.getNom());
        dto.setPrenom(employe.getPrenom());

        List<EmployeeParcoursSectionDTO> sectionDTOList = new ArrayList<>();

        for (EventCategory category : categories) {

            List<EmployeeEvent> events =
                    eventRepository.findByEmployeIdAndCategoryIdOrderByEventDateAsc(
                            employeeId,
                            category.getId()
                    );

            if (events.isEmpty())
                continue;

            EmployeeParcoursSectionDTO sectionDTO = new EmployeeParcoursSectionDTO();
            sectionDTO.setCategoryCode(category.getCode());
            sectionDTO.setCategoryLabel(category.getLabel());

            List<EmployeeParcoursItemDTO> itemDTOList = new ArrayList<>();

            for (EmployeeEvent event : events) {

                EmployeeParcoursItemDTO itemDTO = new EmployeeParcoursItemDTO();

                itemDTO.setLabel(event.getLabel());
                itemDTO.setEventDate(event.getEventDate());
                itemDTO.setStartDate(event.getStartDate());
                itemDTO.setEndDate(event.getEndDate());
                itemDTO.setConfidenceScore(event.getConfidenceScore());

                itemDTOList.add(itemDTO);
            }

            sectionDTO.setEvents(itemDTOList);

            sectionDTOList.add(sectionDTO);
        }

        dto.setSections(sectionDTOList);

        return dto;
    }
}
