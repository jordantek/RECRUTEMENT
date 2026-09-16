package com.tpc.tpcgestpaie.localapp.controller.arh.employe;

import com.tpc.tpcgestpaie.localapp.dto.employe.ContractEmployeDTO;
import com.tpc.tpcgestpaie.localapp.helper.ContratEmployeHelper;
import com.tpc.tpcgestpaie.localapp.helper.RequestHelper;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.service.*;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employe/contrats")
public class ContratEmployeeController {

    private final ContratEmployeService contratService;
    private final ContratEmployeHelper contratHelper;
    private final UserService userService;
    private final DepartementService departementService;
    private final CompanyService companyService;
    private final ContratEmployeService contratEmployeService;
    private final RequestHelper requestHelper;
    private final AuditLogService auditService;
    private  final EmployeService employeService;
    private final NotificationService notificationService;
    private  final  EmployeDiplomeService employeDiplomeService;
    private final ContratEmployeRubriqueService contratEmployeRubriqueService;

    public ContratEmployeeController(
            ContratEmployeService contratService,
            ContratEmployeHelper contratHelper,
            UserService userService, DepartementService departementService, CompanyService companyService, ContratEmployeService contratEmployeService,
            RequestHelper requestHelper,
            AuditLogService auditService, EmployeService employeService,
            NotificationService notificationService, EmployeDiplomeService employeDiplomeService, ContratEmployeRubriqueService contratEmployeRubriqueService
    ) {
        this.contratService = contratService;
        this.contratHelper = contratHelper;
        this.userService = userService;
        this.departementService = departementService;
        this.companyService = companyService;
        this.contratEmployeService = contratEmployeService;
        this.requestHelper = requestHelper;
        this.auditService = auditService;
        this.employeService = employeService;
        this.notificationService = notificationService;
        this.employeDiplomeService = employeDiplomeService;
        this.contratEmployeRubriqueService = contratEmployeRubriqueService;
    }

    @GetMapping("/list/{employeId}")
    public ResponseEntity<?> viewContratEmployeGlobalByEmploye(@PathVariable Long employeId) {
        Employe employe = new Employe();
        employe.setId(employeId);

        // Récupérer les contrats de l’employé (même arrêtés)
        List<ContractEmployeDTO> contrats = contratEmployeService.getContratEmployeParEmploye(employe);

        if (contrats.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Aucun contrat trouvé pour cet employé", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Contrats trouvés", contrats));
    }

}
