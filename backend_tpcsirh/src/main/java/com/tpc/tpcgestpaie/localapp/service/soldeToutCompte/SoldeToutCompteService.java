package com.tpc.tpcgestpaie.localapp.service.soldeToutCompte;

import com.tpc.tpcgestpaie.localapp.CalculUtils.CalculITS;
import com.tpc.tpcgestpaie.localapp.dto.soldeToutCompte.SoldeToutCompteRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.soldeToutCompte.SoldeToutCompteResponseDTO;
import com.tpc.tpcgestpaie.localapp.dto.soldeToutCompte.SoldeToutCompteSaveRequestDTO;
import com.tpc.tpcgestpaie.localapp.dto.soldeToutCompte.SoldeToutCompteSaveResponseDTO;
import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.SoldeToutCompte;
import com.tpc.tpcgestpaie.localapp.repository.CompanyRepository;
import com.tpc.tpcgestpaie.localapp.repository.ContratEmployeRepository;
import com.tpc.tpcgestpaie.localapp.repository.stc.SoldeToutCompteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class SoldeToutCompteService {

    private final CompanyRepository companyRepository;
    private final ContratEmployeRepository contratEmployeRepository;
    private final SoldeToutCompteRepository soldeToutCompteRepository;

    public SoldeToutCompteService(CompanyRepository companyRepository, ContratEmployeRepository contratEmployeRepository, SoldeToutCompteRepository soldeToutCompteRepository) {
        this.companyRepository = companyRepository;
        this.contratEmployeRepository = contratEmployeRepository;
        this.soldeToutCompteRepository = soldeToutCompteRepository;
    }

    public SoldeToutCompteResponseDTO calculer(SoldeToutCompteRequestDTO req) {
        SoldeToutCompteResponseDTO res = new SoldeToutCompteResponseDTO();

        res.setMoisCalcul(req.getMoisCalcul());
        res.setSalairePresence(req.getSalairePresence());
        res.setIndemniteLicenciement(req.getIndemniteLicenciement());
        res.setIndemnitePreavis(req.getIndemnitePreavis());
        res.setIndemniteConge(req.getIndemniteConge());
        res.setGratification(req.getGratification());
        res.setSalaireMoyen(req.getSalaireMoyen());

        // 1. Brut du mois
        BigDecimal brutMois = req.getSalairePresence()
                .add(req.getIndemniteLicenciement())
                .add(req.getIndemnitePreavis())
                .add(req.getIndemniteConge())
                .add(req.getGratification());
        res.setBrutMois(brutMois);

        // 2. ITS moyen (fonction métier)
        CalculITS calculator = new CalculITS();
        BigDecimal itsMoyen;
        itsMoyen = calculator.determinationIts(calculator.baseImposable(req.getSalaireMoyen()));
        res.setItsMoyen(itsMoyen);

        // 3. ITS du mois
        BigDecimal itsMois = itsMoyen.multiply(brutMois)
                .divide(req.getSalaireMoyen(), 2, RoundingMode.HALF_UP);
        res.setItsMois(itsMois);

        // 4. CNSS du mois (3,6 %)
        BigDecimal cnssMois = brutMois.multiply(BigDecimal.valueOf(0.036))
                .setScale(2, RoundingMode.HALF_UP);
        res.setCnssMois(cnssMois);

        // 5. Net du mois
//        BigDecimal netMois = brutMois.subtract(itsMois).subtract(cnssMois);
//        res.setNetMois(netMois);
        BigDecimal netMois = brutMois.subtract(itsMois).subtract(cnssMois)
                .setScale(0, RoundingMode.HALF_UP);
        res.setNetMois(netMois);
        return res;
    }

    @Transactional
    public SoldeToutCompteSaveResponseDTO calculerEtEnregistrer(SoldeToutCompteSaveRequestDTO req) {

        // 1️⃣ Transformer SaveRequestDTO en RequestDTO pour ta fonction calculer
        SoldeToutCompteRequestDTO requestDTO = new SoldeToutCompteRequestDTO();
        requestDTO.setMoisCalcul(req.getMoisCalcul());
        requestDTO.setSalairePresence(req.getSalairePresence());
        requestDTO.setIndemniteLicenciement(req.getIndemniteLicenciement());
        requestDTO.setIndemnitePreavis(req.getIndemnitePreavis());
        requestDTO.setIndemniteConge(req.getIndemniteConge());
        requestDTO.setGratification(req.getGratification());
        requestDTO.setSalaireMoyen(req.getSalaireMoyen());

        // 2️⃣ Appeler la fonction calcul
        SoldeToutCompteResponseDTO res = calculer(requestDTO);

        // 3️⃣ Récupérer les entités pour la relation
        ContratEmploye contrat = contratEmployeRepository.findById(req.getContratEmployeId())
                .orElseThrow(() -> new RuntimeException("Contrat non trouvé"));
        Company company = companyRepository.findById(req.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company non trouvée"));

        Employe employe = contrat.getEmploye();

        // 4️⃣ Transformer ResponseDTO en entité pour sauvegarde
        SoldeToutCompte stc = new SoldeToutCompte();
        stc.setContratEmploye(contrat);
        stc.setCompany(company);
        stc.setEmploye(employe);
        stc.setMoisCalcul(res.getMoisCalcul());
        stc.setSalairePresence(res.getSalairePresence());
        stc.setIndemniteLicenciement(res.getIndemniteLicenciement());
        stc.setIndemnitePreavis(res.getIndemnitePreavis());
        stc.setIndemniteConge(res.getIndemniteConge());
        stc.setGratification(res.getGratification());
        stc.setSalaireMoyen(res.getSalaireMoyen());
        stc.setBrutMois(res.getBrutMois());
        stc.setItsMoyen(res.getItsMoyen());
        stc.setItsMois(res.getItsMois());
        stc.setCnssMois(res.getCnssMois());
        stc.setNetMois(res.getNetMois());

        soldeToutCompteRepository.save(stc);
        // 5️⃣ Retourner un DTO pour affichage
        SoldeToutCompteSaveResponseDTO responseDTO = new SoldeToutCompteSaveResponseDTO();
        responseDTO.setMoisCalcul(res.getMoisCalcul());
        responseDTO.setSalairePresence(res.getSalairePresence());
        responseDTO.setIndemniteLicenciement(res.getIndemniteLicenciement());
        responseDTO.setIndemnitePreavis(res.getIndemnitePreavis());
        responseDTO.setIndemniteConge(res.getIndemniteConge());
        responseDTO.setGratification(res.getGratification());
        responseDTO.setSalaireMoyen(res.getSalaireMoyen());
        responseDTO.setBrutMois(res.getBrutMois());
        responseDTO.setItsMoyen(res.getItsMoyen());
        responseDTO.setItsMois(res.getItsMois());
        responseDTO.setCnssMois(res.getCnssMois());
        responseDTO.setNetMois(res.getNetMois());

        return responseDTO;
    }

    @Transactional
    public List<SoldeToutCompteSaveResponseDTO> listerParEntreprise(Long companyId) {
        List<SoldeToutCompte> soldes = soldeToutCompteRepository.findByCompanyId(companyId);

        return soldes.stream().map(s -> {
            SoldeToutCompteSaveResponseDTO dto = new SoldeToutCompteSaveResponseDTO();

            // Infos entreprise
            dto.setCompanyId(s.getCompany().getId());
            dto.setId(s.getId());

            // Infos employé
            if (s.getEmploye() != null) {
                dto.setEmployeId(s.getEmploye().getId());
                dto.setNomEmploye(s.getEmploye().getNom());
                dto.setPrenomEmploye(s.getEmploye().getPrenom());
                dto.setMatriculeEmploye(s.getEmploye().getMatricule());
            }

            // Données financières
            dto.setMoisCalcul(s.getMoisCalcul());
            dto.setSalairePresence(s.getSalairePresence());
            dto.setIndemniteLicenciement(s.getIndemniteLicenciement());
            dto.setIndemnitePreavis(s.getIndemnitePreavis());
            dto.setIndemniteConge(s.getIndemniteConge());
            dto.setGratification(s.getGratification());
            dto.setSalaireMoyen(s.getSalaireMoyen());
            dto.setBrutMois(s.getBrutMois());
            dto.setItsMoyen(s.getItsMoyen());
            dto.setItsMois(s.getItsMois());
            dto.setCnssMois(s.getCnssMois());
            dto.setNetMois(s.getNetMois());

            return dto;
        }).toList();
    }

}
