import {Bell, Menu} from 'lucide-react'
import { Button } from '@/components/ui/button'
import { UserNav } from '@/components/layout/user-nav'
import React, {useEffect} from "react";
import { useNavigate} from 'react-router-dom'
import { routeHelpers } from '@/helpers/routeHelpers'
import usePageTitleStore from "@/contexts/usePageTitleStore.ts";
import NotificationDropdownMenu from "@/components/useful/notification-dropdown-menu.tsx";
import useNotificationStore, {Notification as NotificationType} from "@/contexts/useNotificationStore.ts";
import apiService from "@/api/apiService.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import {useAuth} from "@/lib/auth.ts";
import {SparklesText} from "@/components/magicui/sparkles-text.tsx";
import {CompanySelect} from "@/components/useful/CompanySelect.tsx";
import {genralConfig} from "../../../config.ts";
import AiModal from "@/components/layout/ai/ai-modal.tsx";
import {SettingNav} from "@/components/layout/setting-nav.tsx";
/*
import Component from "@/components/comp-333.tsx";
import {EmployeeSelect} from "@/components/useful/EmployeeSelect.tsx";
*/

interface HeaderProps {
  onMenuClick: () => void
  onLogout: () => void
  setIsOpenNotifModal: (isOpen: boolean) => void
  setNotification: (notification: NotificationType|null) => void
}

export function Header({ onMenuClick, onLogout, setNotification, setIsOpenNotifModal}: HeaderProps) {
  const {user, logout } = useAuth();
  const {title, description,icon} = usePageTitleStore()
  const navigate = useNavigate()
  const { setNotifications, setUnreadCount } = useNotificationStore();
  const fetchNotifications = async () => {
    try {
      //setLoading(true);
      const response = await apiService.get(
          { url: apiRoutes.admin.app.notifications.unreadList },
          {
            userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
            hasNoSuccessModal: true,
            // onTokenExpired: logout
          }
      );
      setNotifications(response.data.notifications);
      setUnreadCount(response.data.count)

    } catch (error) {
      console.error("Erreur de chargement du personnel", error);
    } finally {
     // setLoading(false);
    }
  };
  useEffect(() => {
    fetchNotifications();
    const interval = setInterval(fetchNotifications, 30000);
    return () => clearInterval(interval);
  }, [setNotifications]);

  return (
      <header className="sticky top-0 z-50 w-full border-b bg-background/80 backdrop-blur supports-[backdrop-filter]:bg-background/60">
        <div className="flex h-16 items-center px-4 sm:px-6 lg:px-8 justify-between">
          {/* Left: Burger menu + app name */}
          <div className="flex items-center gap-3">
            <Button
                variant="ghost"
                size="icon"
                className="lg:hidden"
                onClick={onMenuClick}
            >
              <Menu className="h-5 w-5"/>
              <span className="sr-only">Ouvrir le menu</span>
            </Button>
            {
                location.pathname==='/dashboard' ? (
                  <span className="text-xl font-semibold whitespace-nowrap text-blue-600 bg-blue-200 p-1 rounded">
                    {genralConfig.appName}
                  </span>
              ) : (
                  <div className="flex flex-col">
                        <span className="text-blue-900 text-xl font-bold whitespace-nowrap gap-2 flex items-center">
                          <span className="text-blue-900">
                            {icon && React.createElement(icon, {className: 'h-5 w-5'})}
                          </span>
                          {title}
                        </span>
                        <span className="text-xs text-muted-foreground">{description}</span>
                  </div>
                )}
          </div>

         <div className={"space-x-3"}>
             <CompanySelect/>
          {/*   <EmployeeSelect/>*/}
         </div>

          <div className="flex items-center gap-3">
                    <SparklesText className={"m-0 p-0 text-sm font-semibold text-muted-foreground"} sparklesCount={2}>
                       <AiModal/>
                    </SparklesText>

                   <SettingNav/>

                    <NotificationDropdownMenu setIsOpenNotifModal={setIsOpenNotifModal} setNotification={setNotification}/>
                    <Button
                        variant="ghost"
                        size="icon"
                        className="relative hidden"
                        onClick={() => navigate(routeHelpers.dashboard.notifications)}
                    >
                      <Bell className="h-5 w-5"/>
                      <span className="sr-only">Notifications</span>
                    </Button>

                    <UserNav onLogout={onLogout}/>
                  </div>
        </div>
      </header>
  )
}
