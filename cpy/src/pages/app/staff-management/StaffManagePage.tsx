import { useEffect, useState } from "react";
import {
    UserPlus,
    UserCog,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import PageTitle from "@/components/seo/pageTitle.tsx";
import AddStaffModal from "@/components/layout/staff/add-staff-modal.tsx";
import apiService from "@/api/apiService.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import { useUser } from "@/contexts/UserContext.ts";
import usePageTitleStore from "@/contexts/usePageTitleStore.ts";
import { Icon } from "@tabler/icons-react";
import { Input } from "@/components/ui/input.tsx";
import { StaffType } from "@/types/sttaf/StaffType.ts";
import StaffItem from "@/components/layout/staff/staff-item.tsx";
import {getRoleBadgeInfo, Role} from "@/helpers/RoleHelpers.ts";
import {Avatar, AvatarFallback} from "@/components/ui/avatar.tsx";
import {UserHelpers} from "@/helpers/UserHelpers.ts";

import {
  BadgeInfo,
  CakeIcon,
  CrownIcon,
  HeartHandshake,
  Landmark,
  Mail,
  MapPin,
  MapPinHouse,
  Pencil,
  Phone,
  Printer
} from "lucide-react";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import { toast } from "sonner";
import { format } from "date-fns";

interface InfoLineProps {
  icon: React.ReactNode;
  label?: string;
  value: string | number | null;
  className?: string;
  valueClassName?: string;
}

function InfoLine({ icon, label, value, className, valueClassName }: InfoLineProps) {
  return (
    <div className={`flex items-start gap-3 p-2 border border-gray-200 min-h-[44px] ${className ?? ""}`}>
      <div className="text-muted-foreground mt-1">
        {icon}
      </div>
      <div className="flex-1 min-w-0">
        {label && (
          <p className="text-xs font-bold text-gray-800 mb-1">
            {label}:
          </p>
        )}
        <p className={`text-xs font-normal text-gray-800 break-all ${valueClassName ?? ""}`}>
          {value ?? <span className="italic">—</span>}
        </p>
      </div>
    </div>
  );
}

function formatSituationMatrimoniale(situation: string | null): string {
  if (!situation) return "";

  const translations: Record<string, string> = {
    'CELIBATAIRE_SANS_ENFANT': 'Célibataire sans enfant',
    'CELIBATAIRE_AVEC_ENFANT': 'Célibataire avec enfant',
    'MARIE': 'Marié(e)',
    'DIVORCE': 'Divorcé(e)'
  };

  return translations[situation] || situation.replace(/_/g, ' ');
}

export function StaffManagePage() {
    const user = useUser();
    const [isOpen, setIsOpen] = useState(false);
    const [staffList, setStaffList] = useState<StaffType[]>([]);
    const [selectedStaff, setSelectedStaff] = useState<StaffType | null>(null);
    const [loading, setLoading] = useState(true);
    const [inputValue, setInputValue] = useState("");
    const [isEditModalOpen, setIsEditModalOpen] = useState(false);
    const [currentStaff, setCurrentStaff] = useState<StaffType | null>(null);

    const fetchStaff = async () => {
        try {
            setLoading(true);
            const response = await apiService.get(
                { url: apiRoutes.admin.app.staff.index },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: true,
                }
            );
            setStaffList(response.data);
            if (response.data.length > 0) {
                setSelectedStaff(response.data[0]);
                setCurrentStaff(response.data[0]);
            }
        } catch (error) {
            console.error("Erreur de chargement du personnel", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        usePageTitleStore
            .getState()
            .setTitle("Gestion de personnel", "Gérez vos membres du personnel", UserCog as Icon);
        fetchStaff();
    }, []);

    useEffect(() => {
        if (inputValue === "") {
            fetchStaff();
        } else {
            const filteredStaff = staffList.filter((staff) =>
                staff.fullName.toLowerCase().includes(inputValue.toLowerCase())
            );
            setStaffList(filteredStaff);
        }
    }, [inputValue]);

    useEffect(() => {
      setCurrentStaff(selectedStaff);
    }, [selectedStaff]);

    const handleSave = async (values: any) => {
      if (!currentStaff) return;
      try {
        const payload = {
          ...values,
        };
        const response = await apiService.put(
          {
            url: apiRoutes.admin.app.staff.update(currentStaff.id),
            body: payload,
          },
          {
            userToken: `${user?.type} ${user?.token}`,
            hasNoSuccessModal: false,
          }
        );
        const updatedStaff = response.data;
        setCurrentStaff(updatedStaff);
        fetchStaff();
        toast.success("Mise à jour réussie");
        return updatedStaff;
      } catch (error) {
        console.error("Erreur lors de la mise à jour:", error);
        toast.error("Erreur lors de la mise à jour");
        throw error;
      }
    };

    if (!currentStaff) {
      return <p className="p-4">Aucun personnel sélectionné</p>;
    }

    const avatarInfo = UserHelpers.getInitialUser(currentStaff.fullName ?? currentStaff.username ?? "");

    return (
        <>
            <PageTitle title="Gestion de personnel" />
            <AddStaffModal isOpen={isOpen} setIsOpen={setIsOpen} refresh={fetchStaff} />

            <div className="flex justify-between items-center mb-4">
                <div className="w-64">
                    <div className="relative">
                        <Input
                            className="w-full peer ps-9 pe-9"
                            placeholder="Rechercher..."
                            type="search"
                            value={inputValue}
                            onChange={(e) => setInputValue(e.target.value)}
                        />
                    </div>
                </div>
                <Button onClick={() => setIsOpen(true)}>
                    <UserPlus className="mr-2 h-4 w-4"/>
                    Ajouter un staff
                </Button>
            </div>

            <div className="flex h-[80vh] rounded overflow-hidden bg-background">
                {/* Liste des staffs - Largeur réduite */}
                <div className="w-64 border-r py-4 pe-2 overflow-y-auto scroll-hidden">
                    {loading ? (
                        <div className="space-y-2">
                            {Array.from({ length: 6 }).map((_, i) => (
                                <div key={i} className="p-4 space-y-2">
                                    <Skeleton className="h-4 w-2/3" />
                                    <Skeleton className="h-3 w-1/3" />
                                </div>
                            ))}
                        </div>
                    ) : staffList.length === 0 ? (
                        <div className="p-4 text-muted-foreground">
                            Aucun personnel disponible.
                            <Button className="mt-2" onClick={() => fetchStaff()}>
                                Rafraîchir
                            </Button>
                        </div>
                    ) : (
                        staffList.map((staff) => (
                            <StaffItem
                                key={staff.id}
                                staff={staff}
                                onClick={() => setSelectedStaff(staff)}
                                isActive={selectedStaff?.id === staff.id}
                            />
                        ))
                    )}
                </div>

                {/* Détails du staff - Prend tout l'espace restant */}
                <div className="flex-1 p-4 overflow-y-auto">
                  <div className="space-y-4">
                    {/* Header avec avatar et actions */}
                    <div className="flex justify-between items-start gap-4 p-4 bg-white rounded-lg shadow-sm border">
                      <div className="flex items-start gap-4">
                        <Avatar className="w-16 h-16">
                          <AvatarFallback
                            className="h-16 w-16 text-xl font-semibold transition-all duration-200 hover:scale-105"
                            style={{
                              background: `linear-gradient(135deg, ${avatarInfo.bgColor} 0%, ${avatarInfo.bgGradient} 100%)`,
                              color: '#FFFFFF',
                              boxShadow: '0 2px 10px rgba(0,0,0,0.1)'
                            }}
                          >
                            {avatarInfo.initials}
                          </AvatarFallback>
                        </Avatar>

                        <div className="space-y-2 flex-1">
                          <div className="flex items-center justify-between gap-2">
                            <h2 className="text-xl font-bold text-gray-800 tracking-tight">
                              {currentStaff.fullName ?? currentStaff.username}
                            </h2>
                            {currentStaff.profession && (
                              <span className="inline-flex items-center px-3 py-1 rounded-sm text-xs font-medium bg-blue-100 text-blue-800">
                                {currentStaff.profession}
                              </span>
                            )}
                          </div>

                          <div className="space-y-1.5">
                            <div className="flex items-center gap-2 text-sm text-gray-600">
                              <Phone size={14} className="text-primary flex-shrink-0" />
                              <span className="break-all">{currentStaff.telephone ?? "Non renseigné"}</span>
                            </div>

                            {currentStaff.email && (
                              <TooltipProvider>
                                <Tooltip>
                                  <TooltipTrigger asChild>
                                    <div className="flex items-center gap-2 text-sm text-gray-600">
                                      <Mail size={14} className="text-primary flex-shrink-0" />
                                      <a
                                        href={`mailto:${currentStaff.email}`}
                                        className="text-blue-600 hover:text-blue-800 hover:underline break-all"
                                      >
                                        {currentStaff.email.length > 30
                                          ? `${currentStaff.email.substring(0, 30)}...`
                                          : currentStaff.email}
                                      </a>
                                    </div>
                                  </TooltipTrigger>
                                  <TooltipContent>
                                    <p className="max-w-xs break-all">{currentStaff.email}</p>
                                  </TooltipContent>
                                </Tooltip>
                              </TooltipProvider>
                            )}

                            {currentStaff.quartier && (
                              <div className="flex items-center gap-2 text-sm text-gray-600">
                                <MapPinHouse size={14} className="text-primary flex-shrink-0" />
                                <span className="break-all">{currentStaff.quartier}</span>
                              </div>
                            )}
                          </div>
                        </div>
                      </div>

                      <div className="flex gap-2">
                        <TooltipProvider>
                          <Tooltip>
                            <TooltipTrigger asChild>
                              <Button
                                size="icon"
                                variant="ghost"
                                className="rounded-full hover:bg-primary/10 text-muted-foreground"
                                onClick={() => window.print()}
                              >
                                <Printer size={18} />
                              </Button>
                            </TooltipTrigger>
                            <TooltipContent>Imprimer</TooltipContent>
                          </Tooltip>
                        </TooltipProvider>
                        <TooltipProvider>
                          <Tooltip>
                            <TooltipTrigger asChild>
                              <Button
                                size="icon"
                                variant="ghost"
                                className="rounded-full hover:bg-primary/10 text-muted-foreground"
                                onClick={() => setIsEditModalOpen(true)}
                              >
                                <Pencil size={18} />
                              </Button>
                            </TooltipTrigger>
                            <TooltipContent>Modifier</TooltipContent>
                          </Tooltip>
                        </TooltipProvider>
                      </div>
                    </div>

                    {/* Sections d'informations */}
                    <div className="space-y-4">
                      {/* Identité personnelle */}
                      <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
                        <div className="bg-gray-50 px-4 py-2 border-b border-gray-200">
                          <h3 className="font-semibold flex items-center gap-2 text-sm">
                            <CrownIcon size={16} className="text-primary" />
                            Identité personnelle
                          </h3>
                        </div>
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
                          <InfoLine
                            icon={<CrownIcon size={16} />}
                            label="Nom complet"
                            value={currentStaff.fullName ?? "—"}
                            className="border-b md:border-b-0 md:border-r lg:border-b-0"
                          />
                          <InfoLine
                            icon={<Phone size={16} />}
                            label="Téléphone"
                            value={"00029 61453760"}
                            className="border-b md:border-b-0 md:border-r lg:border-b-0 lg:border-r"
                          />
                          <InfoLine
                            icon={<Mail size={16} />}
                            label="Email"
                            value={currentStaff.email ?? "—"}
                            className="border-b md:border-b md:border-r-0"
                          />
                          <InfoLine
                            icon={<MapPin size={16} />}
                            label="Adresse"
                            value={ "Godomey"}
                            className="border-b md:border-r"
                          />
                        </div>
                      </div>

                      {/* Identifiants administratifs */}
                      <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
                        <div className="bg-gray-50 px-4 py-3 border-b border-gray-200">
                          <h3 className="font-semibold flex items-center gap-2 text-sm">
                            <BadgeInfo size={16} className="text-primary" />
                            Identifiants administratif
                          </h3>
                        </div>
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
                          <InfoLine
                            icon={<BadgeInfo size={16} />}
                            label="Matricule"
                            value={"OXO9879600987"}
                            valueClassName="font-mono break-all"
                            className="border-b md:border-r"
                          />
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
            </div>
        </>
    );
}