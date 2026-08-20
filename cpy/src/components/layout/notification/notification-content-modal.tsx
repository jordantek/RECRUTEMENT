import {
    MailOpen,
    MailWarning,
    ArrowRight,
} from "lucide-react";

import {
    AlertDialog,
    AlertDialogContent,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogDescription,
} from "@/components/ui/alert-dialog";

import useNotificationStore, { Notification as NotificationType } from "@/contexts/useNotificationStore.ts";
import { Button } from "@/components/ui/button";
import apiService from '@/api/apiService.ts'
import apiRoutes from '@/api/apiRoutes.ts'
import { useEffect } from 'react'
import {useAuth} from "@/lib/auth.ts";

interface NotificationContentModalProps {
    isOpen: boolean;
    setIsOpen: (isOpen: boolean) => void;
    notification: NotificationType | null;
    onViewAll?: () => void; // callback pour "Voir toutes les notifications"
}

export default function NotificationContentModal({
                                                     isOpen,
                                                     setIsOpen,
                                                     notification,
                                                     onViewAll,
                                                 }: NotificationContentModalProps) {
    const {user} = useAuth()
    const {markAsRead} = useNotificationStore();
    const isRead = notification?.isRead;

    if (!notification) {
        return null;
    }

    const _markAsRead = async () => {
        if (notification) {
            try {
               const respons =  await apiService.put(
                  { url: `${apiRoutes.admin.app.notifications.markAsRead}/${notification.id}` },
                  { userToken: `${user?.type??''} ${user?.token ?? ''}`, hasNoSuccessModal: false }
                )
                if (respons) {
                    markAsRead(notification.id);
                }
            } catch (error) {
                if (error instanceof Error) {
                    apiService.handleError(error.message, { hasNoFailureModal: true })
                }
            }
        }
    }

    useEffect(() => {
        _markAsRead().then(r => console.log(r));
    }, [])

    return (
        <AlertDialog open={isOpen} onOpenChange={setIsOpen}>
            <AlertDialogContent className="sm:max-w-md">
                <div className="flex items-start gap-3">
                    <div
                        className={`rounded-full p-2 ${
                            isRead ? "bg-green-100 text-green-600" : "bg-yellow-100 text-yellow-600"
                        }`}
                    >
                        {isRead ? <MailOpen size={20} /> : <MailWarning size={20} />}
                    </div>
                    <div className="flex flex-col gap-1">
                        <AlertDialogHeader className="p-0">
                            <AlertDialogTitle className="text-lg font-semibold">
                                {notification?.title ?? "Notification"}
                            </AlertDialogTitle>
                            <AlertDialogDescription className="text-sm text-gray-600 whitespace-pre-line">
                                {notification?.message ?? "Aucune information disponible."}
                            </AlertDialogDescription>
                        </AlertDialogHeader>

                        <div className="text-xs text-gray-500 mt-2">
                            <p><strong>Statut :</strong> {isRead ? "Lu" : "Non lu"}</p>
                            <p>
                                <strong>Reçue :</strong>{" "}
                                {notification?.createdAt
                                    ? new Date(notification.createdAt).toLocaleString()
                                    : "Inconnue"}
                            </p>
                        </div>
                    </div>
                </div>

                <AlertDialogFooter className="flex justify-between items-center mt-6 gap-2">
                    <Button
                        variant="secondary"
                        className="text-sm"
                        onClick={() => setIsOpen(false)}
                    >
                        Fermer
                    </Button>
                    <Button
                        variant="ghost"
                        onClick={() => {
                            setIsOpen(false);
                            onViewAll?.();
                        }}
                        className="flex items-center gap-1 text-sm"
                    >
                        Voir toutes les notifications <ArrowRight size={14} />
                    </Button>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    );
}
