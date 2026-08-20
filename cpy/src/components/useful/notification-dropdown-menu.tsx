import {useState} from "react";
import {formatDistanceToNow} from "date-fns";
import { fr } from 'date-fns/locale';
import {
    Bell, EyeIcon, X,
} from "lucide-react"

import { Button } from "@/components/ui/button"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import useNotificationStore from "@/contexts/useNotificationStore.ts";
import {Notification as NotificationType} from "@/contexts/useNotificationStore.ts";

interface NotificationDropdownMenuProps {
    setIsOpenNotifModal: (isOpen: boolean) => void
    setNotification: (notification: NotificationType|null) => void
}

export default function NotificationDropdownMenu({ setIsOpenNotifModal, setNotification }: NotificationDropdownMenuProps) {
    const { notifications, unreadCount } = useNotificationStore();
    const [isOpen, setIsOpen] = useState(false);

    return (
        <DropdownMenu open={isOpen} onOpenChange={setIsOpen}>
            <DropdownMenuTrigger asChild>
                <Button
                    size="icon"
                    variant="ghost"
                    className="relative shadow-none p-1 rounded-full border-none hover:border-none"
                    aria-label="Open notifications menu"
                >
                    <Bell className="h-5 w-5" />
                    {unreadCount > 0 && (
                        <span className="absolute -top-1 -right-1 flex h-4 w-4 items-center justify-center rounded-full bg-red-500 text-[10px] font-bold text-white">
                            {unreadCount > 99 ? "99+" : unreadCount}
                        </span>
                    )}
                </Button>
            </DropdownMenuTrigger>

            <DropdownMenuContent
                className="w-72 h-96 pb-0 flex flex-col"
                align="end"
                forceMount
                aria-label="Notifications menu"
                sideOffset={10}
            >
                <DropdownMenuLabel>Notifications</DropdownMenuLabel>

                <div className="flex-1 overflow-y-auto px-1 pb-2 scroll-hidden">
                    {notifications?.length ? (
                        notifications.map((notification_) => (
                            <DropdownMenuItem
                                key={notification_.id}
                                className="flex items-start gap-3 px-2 py-2 hover:bg-muted relative group"
                                onClick={() => {
                                    setNotification(notification_);
                                    setIsOpenNotifModal(true);
                                    setIsOpen(false);
                                }}
                            >
                                {/* Icône à gauche */}
                                <div className="flex-shrink-0 bg-background flex size-8 items-center justify-center rounded-md border">
                                    <Bell className="h-5 w-5" />
                                </div>

                                {/* Contenu notification */}
                                <div className="flex flex-col min-w-0">
                                    <div className="text-sm font-medium truncate">{notification_.title}</div>
                                    <div className="text-muted-foreground text-xs truncate">{notification_.message}</div>
                                    <div className="text-muted-foreground text-[10px]">
                                        {formatDistanceToNow(new Date(notification_.createdAt), {
                                            addSuffix: true,
                                            locale: fr,
                                        })}
                                    </div>
                                </div>

                                {/* Bouton supprimer (en haut à droite) */}
                                <button
                                    onClick={(e) => {
                                        e.stopPropagation();
                                    }}
                                    className="absolute top-2 right-2 text-muted-foreground hover:text-destructive transition-opacity opacity-0 group-hover:opacity-100"
                                    title="Supprimer"
                                >
                                    <X size={14} />
                                </button>
                            </DropdownMenuItem>
                        ))
                    ) : (
                        <DropdownMenuItem disabled>Aucune notification</DropdownMenuItem>
                    )}
                </div>

                {/* Voir tout */}
                <div className="border-t">
                    <DropdownMenuItem
                        className="flex gap-2 items-center justify-center py-2 text-sm font-medium cursor-pointer hover:bg-muted"
                    >
                        <EyeIcon size={16} className="opacity-60" aria-hidden="true" />
                        <span>Voir tout</span>
                    </DropdownMenuItem>
                </div>
            </DropdownMenuContent>
        </DropdownMenu>
    );
}