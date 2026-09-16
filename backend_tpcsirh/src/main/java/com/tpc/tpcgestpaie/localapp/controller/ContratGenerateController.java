package com.tpc.tpcgestpaie.localapp.controller;

import com.tpc.tpcgestpaie.localapp.service.bulletun.ContratCdiService;
import com.tpc.tpcgestpaie.localapp.service.bulletun.GenerateBulletin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contrat")
public class ContratGenerateController {

    private final GenerateBulletin generateBulletin;
    private final ContratCdiService contratCdiService;

    public ContratGenerateController(GenerateBulletin generateBulletin, ContratCdiService contratCdiService) {
        this.generateBulletin = generateBulletin;
        this.contratCdiService = contratCdiService;
    }

//    @GetMapping("/contrat/html")
//    public String getContratHtml() {
//        ContratCdiDTO dto = contratCdiService.getSampleDto();
//        return contratCdiService.generateHtml(dto);
//    }
//
//    @GetMapping("/contrat/pdf")
//    public ResponseEntity<byte[]> getContratPdf() throws Exception {
//        ContratCdiDTO dto = contratCdiService.getSampleDto();
//        byte[] pdf = contratCdiService.generatePdf(dto);
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contrat_cdi.pdf")
//                .contentType(MediaType.APPLICATION_PDF)
//                .body(pdf);
//    }

}
