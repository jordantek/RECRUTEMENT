// DocumentQRCodeRepository.java
package com.tpc.tpcgestpaie.localapp.repository.numerisation;

import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentQRCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentQRCodeRepository extends JpaRepository<DocumentQRCode, Long> {

    // ✅ SUPPRIME cette méthode qui cause l'erreur
    Optional<DocumentQRCode> findByDigitalId(String digitalId);

    Optional<DocumentQRCode> findByDocumentId(Long documentId);

    List<DocumentQRCode> findByDocument_EmployeId(Long employeId);

    // Méthode avec query personnalisée si nécessaire
    @Query("SELECT qr FROM DocumentQRCode qr WHERE qr.document.employe.id = :employeId")
    List<DocumentQRCode> findByEmployeId(@Param("employeId") Long employeId);
}