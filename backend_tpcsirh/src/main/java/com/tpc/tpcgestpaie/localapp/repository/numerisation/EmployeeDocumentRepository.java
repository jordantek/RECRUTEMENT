package com.tpc.tpcgestpaie.localapp.repository.numerisation;

import com.tpc.tpcgestpaie.localapp.model.numerisation.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> {


    @Query("SELECT d FROM EmployeeDocument d " +
            "LEFT JOIN FETCH d.subCategory " +
            "LEFT JOIN FETCH d.employe " +  // Ajout de l'employé
            "LEFT JOIN FETCH d.qrCode " +   // Optionnel : si vous avez besoin du QR code
            "WHERE d.employe.id = :employeId " +
            "ORDER BY d.createdAt DESC")
    List<EmployeeDocument> findByEmployeIdWithRelations(@Param("employeId") Long employeId);

    @Query("SELECT d FROM EmployeeDocument d " +
            "LEFT JOIN FETCH d.subCategory " +
            "LEFT JOIN FETCH d.employe " +  // Ajout de l'employé
            "LEFT JOIN FETCH d.qrCode " +   // Optionnel
            "WHERE d.employe.id = :employeId AND " +
            "(LOWER(d.fileName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.subCategory.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY d.createdAt DESC")
    List<EmployeeDocument> searchByEmployeeAndKeywordWithRelations(@Param("employeId") Long employeId,
                                                                   @Param("keyword") String keyword);
    List<EmployeeDocument> findByEmployeIdAndSubCategoryCategoryId(Long employeId, Long categoryId);


    boolean existsByEmployeIdAndSubCategoryId(Long employeId, Long subCategoryId);

    @Query("SELECT d FROM EmployeeDocument d WHERE d.employe.matricule = :matricule")
    List<EmployeeDocument> findByEmployeeMatricule(@Param("matricule") String matricule);

    // ⚠️ CORRECTION : Utiliser une requête JPQL explicite au lieu de la méthode dérivée
    @Query("SELECT d FROM EmployeeDocument d WHERE d.expirationDate < :date " +
            "AND d.employe.id IN (" +
            "SELECT ce.employe.id FROM ContratEmploye ce WHERE ce.company.id = :companyId)")
    List<EmployeeDocument> findDocumentsExpiringByCompany(@Param("date") LocalDate date,
                                                          @Param("companyId") Long companyId);

    // ⚠️ CORRECTION : Méthode alternative pour les documents obligatoires manquants
    @Query("SELECT d FROM EmployeeDocument d WHERE d.employe.id IN (" +
            "SELECT ce.employe.id FROM ContratEmploye ce WHERE ce.company.id = :companyId) " +
            "AND d.subCategory.isMandatory = true " +
            "AND d.id NOT IN (" +
            "SELECT d2.id FROM EmployeeDocument d2 WHERE d2.employe.id = :employeId)")
    List<EmployeeDocument> findMissingMandatoryDocuments(@Param("companyId") Long companyId,
                                                         @Param("employeId") Long employeId);

    @Query("SELECT d FROM EmployeeDocument d WHERE d.employe.id IN (" +
            "SELECT e.id FROM Employe e WHERE e.company.id = :companyId) " +
            "AND d.subCategory.id = :subCategoryId")
    List<EmployeeDocument> findByCompanyIdAndSubCategoryId(@Param("companyId") Long companyId,
                                                           @Param("subCategoryId") Long subCategoryId);
    // ⚠️ AJOUTER LA MÉTHODE MANQUANTE
    List<EmployeeDocument> findByEmployeIdAndSubCategoryId(Long employeId, Long subCategoryId);

    // Vérifier l'existence par nom de fichier pour un employé et sous-catégorie
    boolean existsByEmployeIdAndSubCategoryIdAndFileName(Long employeId, Long subCategoryId, String fileName);

    // Vérifier l'existence par hash de fichier pour un employé
    boolean existsByEmployeIdAndFileHash(Long employeId, String fileHash);

    // Vérifier l'existence pour l'update (exclure le document actuel)
    boolean existsByEmployeIdAndSubCategoryIdAndFileNameAndIdNot(
            Long employeId, Long subCategoryId, String fileName, Long id);

    // Vérifier l'existence par hash pour l'update (exclure le document actuel)
    boolean existsByEmployeIdAndFileHashAndIdNot(Long employeId, String fileHash, Long id);

    List<EmployeeDocument> findByEmployeId(Long employeId);
    List<EmployeeDocument> findByEmployeIdAndDocumentType(Long employeId, EmployeeDocument.DocumentType documentType);
    Optional<EmployeeDocument> findByFileHash(String fileHash);

}