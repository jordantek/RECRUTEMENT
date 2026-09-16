package com.tpc.tpcgestpaie.localapp.repository;

import com.tpc.tpcgestpaie.localapp.model.Notification;
import com.tpc.tpcgestpaie.localapp.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Méthode pour récupérer les notifications d'un utilisateur
    List<Notification> findByUserId(Long userId);
    List<Notification> findByUserAndDeletedAtIsNullOrderByCreatedAtDesc(User user);
    //recuperer les notifications non lues
    List<Notification> findByUserAndDeletedAtIsNullAndIsReadFalseOrderByCreatedAtDesc(User user);

    long countByUserIdAndDeletedAtIsNullAndIsReadFalse(Long userId);

    Optional<Notification> findByIdAndUser(Long notificationId, User user);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Notification n 
        SET n.isRead = true,
            n.readAt = CURRENT_TIMESTAMP
        WHERE n.user.id = :userId
        AND n.isRead = false
        AND n.deletedAt IS NULL
    """)
    int markAllAsReadByUserId(Long userId);
}
