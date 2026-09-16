package com.tpc.tpcgestpaie.localapp.repository.stc;

import com.tpc.tpcgestpaie.localapp.model.Company;
import com.tpc.tpcgestpaie.localapp.model.ContratEmploye;
import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.SoldeToutCompte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoldeToutCompteRepository extends JpaRepository<SoldeToutCompte, Long> {
    boolean existsByEmployeIdAndCompanyId(Long employeId, Long companyId);
    // Exemple : récupérer tous les STC par contrat employé
    List<SoldeToutCompte> findByContratEmployeId(Long contratEmployeId);

    // Exemple : récupérer tous les STC par mois
    List<SoldeToutCompte> findByMoisCalcul(String moisCalcul);

    // Exemple : récupérer STC par contrat et mois
    SoldeToutCompte findByContratEmployeIdAndMoisCalcul(Long contratEmployeId, String moisCalcul);

    boolean existsByContratEmployeAndMoisCalculAndCompany(ContratEmploye contrat, String moisCalcul, Company company);

    boolean existsByEmployeAndMoisCalculAndCompany(Employe employe, String moisCalcul, Company company);
    List<SoldeToutCompte> findByCompanyId(Long companyId);

}
