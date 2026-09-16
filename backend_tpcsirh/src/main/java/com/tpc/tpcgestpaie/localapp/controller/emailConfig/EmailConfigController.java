package com.tpc.tpcgestpaie.localapp.controller.emailConfig;

import com.tpc.tpcgestpaie.localapp.service.emailConfig.EmailConfigService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
public class EmailConfigController {

    private final EmailConfigService emailService;

    public EmailConfigController(EmailConfigService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/test")
    public String sendEmail(@RequestParam String to,
                            @RequestParam(required = false) String attachment) throws MessagingException {

        // Simulation récupération entreprise en BDD
        String logoPath = "C:/Users/LENOVO/Downloads/logo.png"; // récupéré en vrai depuis la base

        Map<String, Object> variables = new HashMap<>();
        variables.put("logoPath", logoPath);
        variables.put("title", "Bienvenue sur Talents Gest Paie !");
        variables.put("message", "Ceci est un test d’envoi d’email avec logo de l’entreprise.");

        emailService.sendEmail(to, "Test Email Template", variables, attachment);

        return "✅ Email envoyé à " + to;
    }

}