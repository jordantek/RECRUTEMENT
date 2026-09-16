package com.tpc.tpcgestpaie.localapp.service.util;

import com.tpc.tpcgestpaie.localapp.dto.util.CompanyConfigDTO;
import com.tpc.tpcgestpaie.localapp.dto.util.CompanyInitRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.util.CompanyInitResponseDTO;
import com.tpc.tpcgestpaie.localapp.dto.util.EmailConfigCreateDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.util.EmailConfig;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.UserRepository;
import com.tpc.tpcgestpaie.localapp.repository.util.EmailConfigRepository;
import com.tpc.tpcgestpaie.localapp.repository.util.InitialConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class CompanyInitService {

    private final CompanyRepository companyRepository;
    private final EmailConfigRepository emailConfigRepository;
    private final InitialConfigRepository initialConfigRepository;
    private final UserRepository userRepository;

    public CompanyInitService(CompanyRepository companyRepository,
                              EmailConfigRepository emailConfigRepository,
                              InitialConfigRepository initialConfigRepository,
                              UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.emailConfigRepository = emailConfigRepository;
        this.initialConfigRepository = initialConfigRepository;
        this.userRepository = userRepository;
    }

    // Vérifie si une configuration initiale existe déjà
    public boolean initialConfigExists() {
        return initialConfigRepository.count() > 0;
    }

    @Transactional
    public EmailConfig updateEmailConfig(Long id, EmailConfigCreateDTO payload) {
        EmailConfig config = emailConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuration email introuvable"));

        // Met à jour uniquement les champs modifiables
        config.setHost(payload.getHost());
        config.setPort(payload.getPort());
        config.setProtocole(payload.getProtocole());
        config.setMailFrom(payload.getMailFrom());
        config.setPassword(payload.getPassword());

        return emailConfigRepository.save(config);
    }


    @Transactional
    public CompanyInitResponseDTO initCompany(CompanyInitRequestDTO payload, MultipartFile logoFile) {
        try {
            // -------- 0) Gérer le logo
            String logoPath = null;
            if (logoFile != null && !logoFile.isEmpty()) {
                String filename = System.currentTimeMillis() + "_" + logoFile.getOriginalFilename();
                Path path = Paths.get("uploads/logo/" + filename);
                Files.createDirectories(path.getParent());
                Files.copy(logoFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                logoPath = "/uploads/logo/" + filename; // chemin relatif à stocker en DB
            }

            // -------- 1) Créer Company
            CompanyConfigDTO c = payload.getCompany();
            Company company = mapToCompany(c, logoPath);
            company = companyRepository.save(company);

//            // -------- 2) Vérifier User et créer EmailConfig
//            EmailConfigCreateDTO e = payload.getEmailConfig();
//
//            EmailConfig emailConfig = e.fromCreateDTO(e);
//            emailConfig.setCompany(company);
//            emailConfig = emailConfigRepository.save(emailConfig);
//
//            // -------- 3) Créer InitialConfig
//            InitialConfig initialConfig = new InitialConfig();
//            initialConfig.setCompany(company);
//            initialConfig.setDateInitialConfig(LocalDate.now());
//            initialConfig.setConfigStatus(true);
//            initialConfig = initialConfigRepository.save(initialConfig);

            // -------- 4) Retour
            return new CompanyInitResponseDTO(
                    true,
                    "Configuration initiale de l'entreprise effectuée avec succès.",
                    company.getId(),
                  null,
                    null
            );

        } catch (Exception ex) {
            throw new RuntimeException("Erreur lors de la configuration initiale : " + ex.getMessage(), ex);
        }
    }

    // Mapper CompanyConfigDTO -> Company avec chemin logo
    private Company mapToCompany(CompanyConfigDTO dto, String logoPath) {
        Company company = new Company();
        company.setName(dto.getName());
        company.setCreationDate(dto.getCreationDate());
        company.setNss(dto.getNss());
        company.setRss(dto.getRss());
        company.setVps(dto.getVps());
        company.setVpsEffectDate(dto.getVpsEffectDate());
        company.setAddress(dto.getAddress());
        company.setCountry(dto.getCountry());
        company.setEmail(dto.getEmail());
        company.setPhone(dto.getPhone());
        company.setWebSite(dto.getWebSite());
        company.setRccm(dto.getRccm());
        company.setIfu(dto.getIfu());
        company.setLogo(logoPath); // chemin provenant du front
        company.setDirectorName(dto.getDirectorName());
        company.setDirectorEmail(dto.getDirectorEmail());
        company.setDirectorPhone(dto.getDirectorPhone());
        company.setSignatoryName(dto.getSignatoryName());
        company.setCa(dto.getCa());
        company.setModeJouissanceConge(dto.getModeJouissanceConge());
        company.setNbrJourTravail(dto.getNbrJourTravail());
        company.setNbrJourConge(dto.getNbrJourConge());
        company.setHeuresParJour(dto.getHeuresParJour());
        company.setHeuresParSemaine(dto.getHeuresParSemaine());
        company.setTvaVal(dto.getTvaVal());
        company.setEstEntreprisePrincipale(true);
        return company;
    }

}
