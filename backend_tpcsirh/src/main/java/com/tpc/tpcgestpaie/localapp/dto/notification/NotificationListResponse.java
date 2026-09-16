package com.tpc.tpcgestpaie.localapp.dto.notification;
import com.tpc.tpcgestpaie.localapp.dto.notification.NotificationDTO;

import java.util.List;

public class NotificationListResponse {
    private int count;
    private List<NotificationDTO> notifications;

    public NotificationListResponse(int count, List<NotificationDTO> notifications) {
        this.count = count;
        this.notifications = notifications;
    }

    public int getCount() {
        return count;
    }

    public List<NotificationDTO> getNotifications() {
        return notifications;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setNotifications(List<NotificationDTO> notifications) {
        this.notifications = notifications;
    }
}
