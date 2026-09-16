package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.JournalRh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalRhRepository extends JpaRepository<JournalRh, Long> {
    List<JournalRh> findAllByDeletedAtIsNull();
    Optional<JournalRh> findByDateAndContenuAndCategorieEvenement_IdAndDeletedAtIsNull(LocalDate date, String contenu, Long categorieId);
    @Query("SELECT j FROM JournalRh j LEFT JOIN FETCH j.categorieEvenement WHERE j.deletedAt IS NULL")
    List<JournalRh> findAllWithCategorie();

    @Query("SELECT j FROM JournalRh j LEFT JOIN FETCH j.categorieEvenement WHERE j.id = :id")
    Optional<JournalRh> findByIdWithCategorie(@Param("id") Long id);


    @Query("SELECT j FROM JournalRh j WHERE j.added_by.id = :userId AND j.deletedAt IS NULL")
    List<JournalRh> findByUserId(@Param("userId") Long userId);

    @Query("SELECT j FROM JournalRh j WHERE j.added_by.id = :userId AND j.date >= :startDate AND j.date <= :endDate AND j.deletedAt IS NULL")
    List<JournalRh> findByUserIdAndDateRange(@Param("userId") Long userId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    @Query("SELECT j FROM JournalRh j WHERE j.added_by.id = :userId AND j.date = :date AND j.deletedAt IS NULL")
    List<JournalRh> findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT j FROM JournalRh j WHERE j.added_by.id = :userId AND j.date >= :today AND j.deletedAt IS NULL ORDER BY j.date ASC")
    List<JournalRh> findFutureEventsByUser(@Param("userId") Long userId, @Param("today") LocalDate today);

    @Query("SELECT j FROM JournalRh j WHERE j.added_by.id = :userId AND j.date BETWEEN :startDate AND :endDate AND j.deletedAt IS NULL ORDER BY j.date ASC")
    List<JournalRh> findUpcomingEvents(@Param("userId") Long userId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);


    @Query("SELECT j FROM JournalRh j WHERE j.date = :date AND j.contenu = :contenu AND j.categorieEvenement.id = :categorieId AND j.added_by.id = :userId AND j.deletedAt IS NULL")
    Optional<JournalRh> findByDateAndContenuAndCategorieAndUser(@Param("date") LocalDate date,
                                                                @Param("contenu") String contenu,
                                                                @Param("categorieId") Long categorieId,
                                                                @Param("userId") Long userId);
}
