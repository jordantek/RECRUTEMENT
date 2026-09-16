package com.tpc.tpcgestpaie.localapp.service;

import com.tpc.tpcgestpaie.localapp.dto.notification.NotificationDTO;
import com.tpc.tpcgestpaie.localapp.model.Notification;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationDTO createNotification(User user, String title, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());

        Notification saved = notificationRepository.save(notification);
        return new NotificationDTO(
                saved.getId(),
                saved.getTitle(),
                saved.getMessage(),
                saved.isRead(),
                saved.getReadAt(),
                saved.getCreatedAt()
        );
    }

    public List<NotificationDTO> getUserNotifications(User user) {
        return notificationRepository.findByUserAndDeletedAtIsNullOrderByCreatedAtDesc(user)
                .stream()
                .map(n -> new NotificationDTO(n.getId(), n.getTitle(), n.getMessage(), n.isRead(), n.getReadAt(), n.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public long countUnreadNotificationsByUserId(Long userId) {
        return notificationRepository.countByUserIdAndDeletedAtIsNullAndIsReadFalse(userId);
    }

    public List<NotificationDTO> getNotificationsUnRead(User user) {
        return notificationRepository
                .findByUserAndDeletedAtIsNullAndIsReadFalseOrderByCreatedAtDesc(user)
                .stream()
                .map(n -> new NotificationDTO(
                        n.getId(),
                        n.getTitle(),
                        n.getMessage(),
                        n.isRead(),
                        n.getReadAt(),
                        n.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public boolean markAsRead(Long notificationId, User user) {
        Optional<Notification> optionalNotification = notificationRepository.findByIdAndUser(notificationId, user);

        if (optionalNotification.isEmpty()) {
            return false;
        }

        Notification notification = optionalNotification.get();
        notification.setRead(true); // ou notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now()); // facultatif
        notificationRepository.save(notification);

        return true;
    }

    public int markAllNotificationsAsRead(User user) {
        if (user == null) return 0;

        return notificationRepository
                .markAllAsReadByUserId(user.getId());
    }
}
