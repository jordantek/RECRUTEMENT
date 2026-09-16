package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.Employe;
import com.tpc.tpcgestpaie.localapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  User findByUsername(String username);
  //Optional<User> findByUsername(String username); // 👈 Cette méthode est essentielle
  Optional<User> findByEmail(String email);
  // Charge l'utilisateur avec son statut (status)

  @Query("SELECT u FROM User u " +
          "LEFT JOIN FETCH u.status " +
          "LEFT JOIN FETCH u.employe " +
          "WHERE u.username = :username")
  User findByUsernameWithStatus(@Param("username") String username);

  @Query("SELECT u FROM User u JOIN FETCH u.status")
  List<User> findAllUsersWithStatus();
  Optional<User> findByEmployeId(Long employeId);
  Optional<User> findByEmploye(Employe employe);

  Optional<User> findBySageMatricule(String sageMatricule);


  boolean existsByEmail(String email);

  boolean existsBySageMatricule(String sageMatricule);

  boolean existsByUsername(String username);

  @Query("SELECT u FROM User u " +
          "LEFT JOIN FETCH u.roles " +
          "LEFT JOIN FETCH u.status " +
          "LEFT JOIN FETCH u.employe " +
          "LEFT JOIN FETCH u.company " +
          "WHERE u.id = :userId")
  Optional<User> findByIdWithRelations(@Param("userId") Long userId);

  @Query("SELECT u FROM User u " +
          "LEFT JOIN FETCH u.roles " +
          "LEFT JOIN FETCH u.status " +
          "LEFT JOIN FETCH u.employe " +
          "LEFT JOIN FETCH u.company " +
          "WHERE u.username = :username")
  Optional<User> findByUsernameWithRelations(@Param("username") String username);



  boolean existsByEmployeId(Long employeId);

  // Pour la vérification des doublons
  @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.id != :excludeId")
  boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("excludeId") Long excludeId);

  @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id != :excludeId")
  boolean existsByEmailAndIdNot(@Param("email") String email, @Param("excludeId") Long excludeId);



  /**
   * Trouver tous les utilisateurs d'une entreprise
   */
//  @Query("SELECT u FROM User u WHERE u.company.id = :companyId ORDER BY u.fullName")
//  List<User> findByCompanyId(@Param("companyId") Long companyId);

  @Query("""
       SELECT u 
       FROM User u 
       WHERE u.company.id = :companyId
       AND u.employe IS NOT NULL
       ORDER BY u.fullName
       """)
  List<User> findByCompanyId(@Param("companyId") Long companyId);

  /**
   * Trouver les utilisateurs actifs d'une entreprise
   */
  @Query("SELECT u FROM User u WHERE u.company.id = :companyId AND u.deletedAt IS NULL ORDER BY u.fullName")
  List<User> findByCompanyIdAndDeletedAtIsNull(@Param("companyId") Long companyId);

  /**
   * Rechercher des utilisateurs dans une entreprise par nom, prénom ou email
   */
  @Query("SELECT u FROM User u WHERE u.company.id = :companyId AND " +
          "(LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
          "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
          "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
          "(u.employe IS NOT NULL AND (" +
          "LOWER(u.employe.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
          "LOWER(u.employe.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))" +
          "))) " +
          "ORDER BY u.fullName")
  List<User> findByCompanyIdAndSearchTerm(@Param("companyId") Long companyId,
                                          @Param("searchTerm") String searchTerm);
}