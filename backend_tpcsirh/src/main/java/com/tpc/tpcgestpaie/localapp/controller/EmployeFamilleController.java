package com.tpc.tpcgestpaie.localapp.controller;

import com.tpc.tpcgestpaie.localapp.dto.EmployeFamilleDTO;
import com.tpc.tpcgestpaie.localapp.service.employe.EmployeFamilleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employes")
public class EmployeFamilleController {

    private final EmployeFamilleService employeFamilleService;

    public EmployeFamilleController(EmployeFamilleService employeFamilleService) {
        this.employeFamilleService = employeFamilleService;
    }

    @GetMapping("/{employeId}/famille")
    public ResponseEntity<EmployeFamilleDTO> getEmployeFamille(@PathVariable Long employeId) {
        EmployeFamilleDTO dto = employeFamilleService.getById(employeId);
        return ResponseEntity.ok(dto);
    }




}
