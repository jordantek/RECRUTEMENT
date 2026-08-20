// stores/useNotificationStore.ts
import { create } from 'zustand';

export interface Notification {
    id: number;
    title: string;
    message: string;
    isRead: boolean;
    createdAt: string;
}

interface NotificationStore {
    notifications: Notification[];
    unreadCount: number;
    setUnreadCount: (count: number) => void;
    setNotifications: (notifications: Notification[]) => void;
    markAsRead: (id: number) => void;
    clearNotifications: () => void;
}

const useNotificationStore = create<NotificationStore>((set) => ({
    notifications: [],
    unreadCount: 0,
    setUnreadCount: (count: number) => set({ unreadCount: count }),
    setNotifications: (notifications) =>
        set({
            notifications,
            unreadCount: notifications.filter(n => !n.isRead).length
        }),
    markAsRead: (id) =>
        set((state) => {
            const updated = state.notifications.map(n =>
                n.id === id ? { ...n, isRead: true } : n
            );
            return {
                notifications: updated,
                unreadCount: updated.filter(n => !n.isRead).length
            };
        }),
    clearNotifications: () => set({ notifications: [], unreadCount: 0 }),
}));

export default useNotificationStore;
