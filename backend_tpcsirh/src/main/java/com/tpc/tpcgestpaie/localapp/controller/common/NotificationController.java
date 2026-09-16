package com.tpc.tpcgestpaie.localapp.controller.common;

import com.tpc.tpcgestpaie.localapp.dto.notification.NotificationDTO;
import com.tpc.tpcgestpaie.localapp.dto.notification.NotificationListResponse;
import com.tpc.tpcgestpaie.localapp.model.User;
import com.tpc.tpcgestpaie.localapp.service.NotificationService;
import com.tpc.tpcgestpaie.localapp.service.UserService;
import com.tpc.tpcgestpaie.localapp.util.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/common/notification")
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> listNotifications() {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.ok(new ApiResponse<>(false, "Utilisateur non trouvé", null));
            }

            List<NotificationDTO> notifications = notificationService.getUserNotifications(currentUser);
            ApiResponse<List<NotificationDTO>> successResponse = new ApiResponse<>(
                    true,
                    "Liste des notifications récupérées avec succès",
                    notifications
            );

            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            ApiResponse<Object> errorResponse = new ApiResponse<>(
                    false,
                    "Une erreur est survenue lors de la récupération des notifications : " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/list/unread")
    public ResponseEntity<?> listUnReadNotifications() {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.ok(new ApiResponse<>(false, "Utilisateur non trouvé", null));
            }


            List<NotificationDTO> notifications = notificationService.getNotificationsUnRead(currentUser);
            NotificationListResponse notificationUnreadList = new NotificationListResponse(notifications.size(), notifications);
            ApiResponse<NotificationListResponse> successResponse = new ApiResponse<>(
                    true,
                    "Liste des notifications non lues récupérées avec succès",
                    notificationUnreadList
            );

            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            ApiResponse<Object> errorResponse = new ApiResponse<>(
                    false,
                    "Une erreur est survenue lors de la récupération des notifications : " + e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/unread/count")
    public ResponseEntity<?> countUnreadNotifications() {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non trouvé", null));
            }
            System.out.println(currentUser.getId());
            long count = notificationService
                    .countUnreadNotificationsByUserId(currentUser.getId());

            Map<String, Long> data = Map.of("unreadCount", count);
            ApiResponse<Map> successResponse = new ApiResponse<>(
                    true,
                    "Nombre de notifications non lues récupéré avec succès",
                    data
            );

            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Erreur lors du comptage des notifications : " + e.getMessage(),
                            null
                    ));
        }
    }

    @PutMapping("/mark-as-read/{id}")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long id) {
        try {
            User currentUser = userService.getCurrentUser();

            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non trouvé", null));
            }

            boolean updated = notificationService.markAsRead(id, currentUser);

            if (!updated) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Notification introuvable ou non autorisée", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Notification marquée comme lue", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Erreur lors de la mise à jour : " + e.getMessage(), null));
        }
    }

    @PutMapping("/mark-all-as-read")
    public ResponseEntity<?> markAllAsRead() {
        try {
            User currentUser = userService.getCurrentUser();

            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Utilisateur non trouvé", null));
            }

            int updatedCount =
                    notificationService.markAllNotificationsAsRead(currentUser);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Notifications marquées comme lues",
                            Map.of("updatedCount", updatedCount)
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Erreur : " + e.getMessage(),
                            null
                    ));
        }
    }


}
