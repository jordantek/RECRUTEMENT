package com.tpc.tpcgestpaie.localapp.controller.paie.bulletin;

import com.tpc.tpcgestpaie.localapp.dto.accessoire.TableauBulletinDTO;
import com.tpc.tpcgestpaie.localapp.service.paie.bulletin.BulletinPaieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paie/bulletin")
public class BulletinPaieController {

    private final BulletinPaieService bulletinPaieService;

    public BulletinPaieController(BulletinPaieService bulletinPaieService) {
        this.bulletinPaieService = bulletinPaieService;
    }
    @GetMapping("/employe/{id}/12mois")
    public ResponseEntity<TableauBulletinDTO> getTableauBulletins(@PathVariable Long id) {
        TableauBulletinDTO tableau = bulletinPaieService.genererTableauBulletin12Mois(id);
        return ResponseEntity.ok(tableau);
    }
}
