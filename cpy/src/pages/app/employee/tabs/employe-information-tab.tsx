import { useState, useEffect } from "react";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
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
import { Button } from "@/components/ui/button";
import { IconGenderTransgender } from "@tabler/icons-react";
import { EmployeeType } from "@/types/employee/EmployeeType";
import { UserHelpers } from "@/helpers/UserHelpers";
import { cn } from "@/lib/utils";
import { EmployeeEditModal } from "./employee-edit-modal";
import { toast } from "sonner";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";
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
    <div className={cn(
      "flex items-start gap-3 p-2 border border-gray-200 min-h-[44px]",
      className
    )}>
      <div className="text-muted-foreground mt-1">
        {icon}
      </div>
      <div className="flex-1 min-w-0">
        {label && (
          <p className="text-xs font-bold text-gray-800 mb-1">
            {label}:
          </p>
        )}
        <p className={cn(
          "text-xs font-normal text-gray-800 break-all",
          valueClassName
        )}>
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

interface EnployeeInfoProps {
  employee: EmployeeType;
  onEmployeeUpdated?: (updatedEmployee: EmployeeType) => void;
}

export default function EmployeInformationTab({ employee, onEmployeeUpdated }: EnployeeInfoProps) {
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [currentEmployee, setCurrentEmployee] = useState<EmployeeType>(employee);
  const avatarInfo = UserHelpers.getInitialUser(`${currentEmployee?.nom} ${currentEmployee?.prenom}`);
  const { user } = useAuth();

  useEffect(() => {
    setCurrentEmployee(employee);
  }, [employee]);

  const handleSave = async (values: any) => {
    try {
      // Formatage de la date si nécessaire
      const payload = {
        ...values,
        date_naissance: values.date_naissance 
          ? format(new Date(values.date_naissance), 'yyyy-MM-dd')
          : currentEmployee.date_naissance
      };

      const response = await apiService.put(
        {
          url: apiRoutes.admin.app.employee.update(currentEmployee.id),
          body: payload,
        },
        {
          userToken: `${user?.type} ${user?.token}`,
          hasNoSuccessModal: false,
        }
      );

      const updatedEmployee = response.data;
      setCurrentEmployee(updatedEmployee);
      onEmployeeUpdated?.(updatedEmployee);
      return updatedEmployee;
    } catch (error) {
      console.error("Erreur lors de la mise à jour:", error);
      throw error;
    }
  };

  return (
    <div className="space-y-2">
      <EmployeeEditModal
        employee={currentEmployee}
        open={isEditModalOpen}
        onOpenChange={setIsEditModalOpen}
        onSave={handleSave}
      />

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
                {currentEmployee.prenom} <span className="font-semibold">{currentEmployee.nom}</span>
              </h2>
              {currentEmployee.profession && (
                <span className="inline-flex items-center px-3 py-1 rounded-sm text-xs font-medium bg-blue-100 text-blue-800">
                  {currentEmployee.profession}
                </span>
              )}
            </div>

            <div className="space-y-1.5">
              <div className="flex items-center gap-2 text-sm text-gray-600">
                <Phone size={14} className="text-primary flex-shrink-0"/>
                <span className="break-all">{currentEmployee.telephone ?? "Non renseigné"}</span>
              </div>

              {currentEmployee.email && (
                <TooltipProvider>
                  <Tooltip>
                    <TooltipTrigger asChild>
                      <div className="flex items-center gap-2 text-sm text-gray-600">
                        <Mail size={14} className="text-primary flex-shrink-0"/>
                        <a
                          href={`mailto:${currentEmployee.email}`}
                          className="text-blue-600 hover:text-blue-800 hover:underline break-all"
                        >
                          {currentEmployee.email.length > 30
                            ? `${currentEmployee.email.substring(0, 30)}...`
                            : currentEmployee.email}
                        </a>
                      </div>
                    </TooltipTrigger>
                    <TooltipContent>
                      <p className="max-w-xs break-all">{currentEmployee.email}</p>
                    </TooltipContent>
                  </Tooltip>
                </TooltipProvider>
              )}

              {currentEmployee.quartier && (
                <div className="flex items-center gap-2 text-sm text-gray-600">
                  <MapPinHouse size={14} className="text-primary flex-shrink-0"/>
                  <span className="break-all">{currentEmployee.quartier}</span>
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
                  <Printer size={18}/>
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
                  <Pencil size={18}/>
                </Button>
              </TooltipTrigger>
              <TooltipContent>Modifier</TooltipContent>
            </Tooltip>
          </TooltipProvider>
        </div>
      </div>

      {/* Sections d'informations */}
      <div className="space-y-2 pb-6">
        {/* Section 1: Identifiants Personnels */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
          <div className="bg-gray-50 px-4 py-2 border-b border-gray-200">
            <h3 className="font-semibold flex items-center gap-2 text-sm">
              <CrownIcon size={16} className="text-primary"/>
              Identité personnelle
            </h3>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
            <InfoLine
              icon={<IconGenderTransgender size={16}/>}
              label="Genre"
              value={currentEmployee.sexe}
              className="border-b md:border-b-0 md:border-r lg:border-b-0"
            />
            <InfoLine
              icon={<CakeIcon size={16}/>}
              label="Date de naissance"
              value={currentEmployee.date_naissance}
              className="border-b md:border-b-0 md:border-r lg:border-b-0 lg:border-r"
            />
            <InfoLine
              icon={<MapPin size={16}/>}
              label="Lieu de naissance"
              value={currentEmployee.lieu_naissance}
              className="border-b md:border-b md:border-r-0"
            />
            <InfoLine
              icon={<Landmark size={16}/>}
              label="Nationalité"
              value={currentEmployee.nationalite}
              className="border-b md:border-r"
            />
            <InfoLine
              icon={<HeartHandshake size={16}/>}
              label="Situation matrimoniale"
              value={formatSituationMatrimoniale(currentEmployee.situationMatrimoniale)}
              className="border-b lg:border-r"
            />
            <div className="border-b border-gray-200 p-3"></div>
          </div>
        </div>

        {/* Section 2: Identifiants Administratifs */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
          <div className="bg-gray-50 px-4 py-3 border-b border-gray-200">
            <h3 className="font-semibold flex items-center gap-2 text-sm">
              <BadgeInfo size={16} className="text-primary"/>
              Identifiants administratifs
            </h3>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
            <InfoLine
              icon={<BadgeInfo size={16}/>}
              label="CNSS"
              value={currentEmployee.numero_cnss}
              valueClassName="font-mono break-all"
              className="border-b md:border-r"
            />
            <InfoLine
              icon={<BadgeInfo size={16}/>}
              label="Matricule"
              value={currentEmployee.matricule}
              valueClassName="font-mono break-all"
              className="border-b lg:border-b-0 lg:border-r"
            />
            <InfoLine
              icon={<BadgeInfo size={16}/>}
              label="IFU"
              value={currentEmployee.numero_ifu}
              valueClassName="font-mono break-all"
              className="border-b md:border-b md:border-r-0"
            />
          </div>
        </div>

        {/* Section 3: Coordonnées */}
        <div className="bg-white rounded-lg shadow-sm border overflow-hidden">
          <div className="bg-gray-50 px-4 py-3 border-b">
            <h3 className="font-semibold flex items-center gap-2 text-sm">
              <MapPinHouse size={16} className="text-primary"/>
              Coordonnées
            </h3>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
            <InfoLine
              icon={<Phone size={16}/>}
              label="Téléphone"
              value={currentEmployee.telephone}
              valueClassName="text-blue-600 break-all"
            />
            <InfoLine
              icon={<Mail size={16}/>}
              label="Email"
              value={currentEmployee.email}
              valueClassName="text-blue-600 hover:text-blue-800 hover:underline break-all"
            />
            <InfoLine
              icon={<MapPin size={16}/>}
              label="Adresse"
              value={currentEmployee.quartier}
              valueClassName="break-all"
            />
          </div>
        </div>
      </div>
    </div>
  );
}