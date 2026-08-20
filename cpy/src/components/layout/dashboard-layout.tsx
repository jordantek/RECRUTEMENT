import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '@/lib/auth';
import { Sidebar } from '@/components/sidebar/sidebar';
import { UserContext } from '@/contexts/UserContext';
import { Header } from '@/components/layout/header';
import { useEffect, useState } from 'react';
import { AutoShowDialog } from '../ui/my-alert-dialog';
import { routeHelpers } from '@/helpers/routeHelpers';
import { Button } from '@/components/ui/button';
import { MoveLeft } from 'lucide-react';

import { Notification as NotificationType } from "@/contexts/useNotificationStore.ts";
import NotificationContentModal from "@/components/layout/notification/notification-content-modal.tsx";
import SessionExpiredModal from "@/components/useful/SessionExpiredModal.tsx";

export function DashboardLayout() {
  const { isAuthenticated, user,oldeUser,logoutAll } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [showLogOutModal, setShowLogOutModal] = useState(false);
  const [isOpenNotifModal, setIsOpenNotifModal] = useState(false);
  const [notification, setNotification] = useState<NotificationType | null>(null);

  const [sessionModalIsOpen,setSessionModalIsOpen]=useState(false)

  // Routes sans sidebar ni header
  const routesSansLayout = ["/state/setting"];

  // Routes sans sidebar mais avec header
  const routesSansSidebar = ["/dashboard"];

  const locationPath = location.pathname;

  const isRouteSansLayout = routesSansLayout.includes(locationPath);
  const isRouteSansSidebar = routesSansSidebar.includes(locationPath);

  useEffect(() => {
    console.log("L'état de la sidebar:", sidebarOpen);

    if((!isAuthenticated || !user?.token ) && oldeUser){
      setSessionModalIsOpen(true)
    }
    if (!isRouteSansLayout && !isRouteSansSidebar && (!isAuthenticated || !user?.token )&& !oldeUser ) {
      navigate('/login');
    }
  }, [isAuthenticated, user, navigate, isRouteSansLayout, isRouteSansSidebar]);


  return (
    <UserContext.Provider value={user}>
      {/* Overlay Notification */}
      <div
        className={`overlay ${isOpenNotifModal ? "visible" : "hidden"}`}
        style={{ zIndex: isOpenNotifModal ? 9999 : -1 }}
      >
        {isOpenNotifModal && (
          <NotificationContentModal
            isOpen={isOpenNotifModal}
            setIsOpen={setIsOpenNotifModal}
            notification={notification}
          />
        )}
      </div>
      <SessionExpiredModal isOpen={sessionModalIsOpen} setIsOpen={setSessionModalIsOpen}/>
      {/* Modal Déconnexion */}
      {showLogOutModal && (
        <AutoShowDialog
          title="Déconnexion"
          titleClass={"text-red-500"}
          message="Si vous quittez, vous pxerdrez votre session. Souhaitez-vous vraiment vous déconnecter."
          onSuccess={logoutAll}
          successBtnClass={"bg-red-500 hover:bg-red-600 text-white"}
          onCancel={() => setShowLogOutModal(false)}
          onDismiss={() => setShowLogOutModal(false)}
          cancelBtnText={"Annuler"}
          successBtnText="Déconnexion"
        />
      )}

      {/* Layout principal */}
      <div className="h-screen bg-background font-inter flex">
        {/* Sidebar seulement si pas dans routesSansLayout ni routesSansSidebar */}
        {!isRouteSansLayout && !isRouteSansSidebar && (
          <div className="w-fit">
            <Sidebar onLogout={() => setShowLogOutModal(true)} />
          </div>
        )}

        <div className="flex-1 flex flex-col overflow-hidden">
          {/* Header */}
          {!isRouteSansLayout && (
            <Header
              onLogout={() => setShowLogOutModal(true)}
              onMenuClick={() => setSidebarOpen(true)}
              setIsOpenNotifModal={setIsOpenNotifModal}
              setNotification={setNotification}
            />
          )}
        {/*
        bg-gradient-to-b from-gray-50  to-gray-50-*/}
        <main className="overflow-auto scroll-hidden ">
            {!isRouteSansLayout && routeHelpers.dashboard.index !== "/dashboard" && (
              <Button onClick={() => navigate(-1)} size="sm" variant="outline" className="bg-gray-100 mb-4">
                <MoveLeft size={16} className="mr-2" /> Retour
              </Button>
            )}

            <Outlet />
          </main>
        </div>
      </div>
    </UserContext.Provider>
  );
}
