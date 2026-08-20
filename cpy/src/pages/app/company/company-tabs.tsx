import {
    BadgeInfo, Folder, Globe, Landmark, Mail, MapPin,
    Pencil, Phone, Printer, Settings, ShieldCheck,
    User, Users, Building2, FileText, ScrollText
  } from "lucide-react";
  
  import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area.tsx";
  import {
    Tabs,
    TabsContent,
    TabsList,
    TabsTrigger,
  } from "@/components/ui/tabs.tsx";
  import { CompanyType } from "@/types/company/CompanyType.ts";
  import { Skeleton } from "@/components/ui/skeleton.tsx";
  import { ReactNode } from "react";
  import { Avatar, AvatarFallback } from "@/components/ui/avatar.tsx";
  import { cn } from "@/lib/utils.ts";
  import { UserHelpers } from "@/helpers/UserHelpers.ts";
  import { Button } from "@/components/ui/button.tsx";
  import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip.tsx";
  import SettingGeneralTab from "@/pages/app/company/setting/setting-general-tab.tsx";
  import ContratListTabs from "@/pages/app/company/tabs/contrat-list-tabs.tsx";
  import { iconClass, tabTriggerClass } from "@/style.ts";
  
  interface CompanyTabsProps {
    company?: CompanyType | null;
  }
  
  interface InfoLineProps {
    icon: ReactNode;
    label?: string;
    value: string | number | null | undefined;
    className?: string;
    valueClassName?: string;
  }
  
  function InfoLine({ icon, label, value, className, valueClassName }: InfoLineProps) {
    return (
      <div className={cn(
        "flex items-start gap-3 p-3 border border-gray-200 min-h-[48px]", // Augmentation de la taille
        className
      )}>
        <div className="text-muted-foreground mt-1">
          {icon}
        </div>
        <div className="flex-1 min-w-0">
          {label && (
            <p className="text-sm font-bold text-gray-800 mb-1"> 
              {label}:
            </p>
          )}
          <p className={cn(
            "text-sm font-normal text-gray-800 break-all", // Taille augmentée
            valueClassName
          )}>
            {value ?? <span className="italic">—</span>}
          </p>
        </div>
      </div>
    );
  }
  
  export default function CompanyTabs({ company }: CompanyTabsProps) {
    const isLoading = !company;
    const avatarInfo = UserHelpers.getInitialUser(company?.name ?? "");
  
    const renderTabs = () => {
      if (isLoading) {
        return (
          <>
            {Array.from({ length: 4 }).map((_, i) => (
              <Skeleton key={i} className="h-8 w-24 rounded-sm" />
            ))}
          </>
        );
      }
  
      return (
        <>
          <TabsTrigger value="tab-1" className={tabTriggerClass}>
            <BadgeInfo className={iconClass} size={16} />
            Informations
          </TabsTrigger>
          <TabsTrigger value="employes" className={tabTriggerClass}>
            <ScrollText className={iconClass} size={16} />
            Contrats
          </TabsTrigger>
          <TabsTrigger value="parametres" className={tabTriggerClass}>
            <Settings className={iconClass} size={16} />
            Paramètres
          </TabsTrigger>
        </>
      );
    };
  
    const renderEmailWithTooltip = (email: string) => (
      <TooltipProvider>
        <Tooltip>
          <TooltipTrigger asChild>
            <span className="text-blue-600 hover:text-blue-800 hover:underline break-all">
              {email.length > 30 ? `${email.substring(0, 30)}...` : email}
            </span>
          </TooltipTrigger>
          <TooltipContent>
            <p className="max-w-xs break-all">{email}</p>
          </TooltipContent>
        </Tooltip>
      </TooltipProvider>
    );
  
    return (
      <Tabs defaultValue="tab-1" className="h-full flex flex-col">
        <ScrollArea>
          <TabsList
            className="text-foreground mb-3 h-auto gap-2 rounded-none border-b bg-transparent px-1 w-full justify-start"
            aria-disabled={isLoading}
          >
            {renderTabs()}
          </TabsList>
          <ScrollBar orientation="horizontal" />
        </ScrollArea>
  
        <TabsContent value="tab-1" className="flex-1 overflow-y-auto px-2">
          {isLoading ? (
            <p className="text-sm italic text-gray-500 text-center py-4"> 
              Aucune entreprise sélectionnée. Veuillez en choisir une pour voir les détails.
            </p>
          ) : (
            <div className="space-y-3 pb-6"> {/* Espacement augmenté */}
              {/* Header avec avatar et actions */}
              <div className="flex justify-between items-start gap-4 p-4 bg-white rounded-lg shadow-sm border">
                <div className="flex items-start gap-4">
                  <Avatar className="w-20 h-20"> 
                    <AvatarFallback
                      className="h-20 w-20 text-2xl font-semibold" 
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
                      <h2 className="text-2xl font-bold text-gray-800 tracking-tight"> 
                        {company.name}
                      </h2>
                      {company.activityAreas && (
                        <span className="inline-flex items-center px-3 py-1 rounded-sm text-sm font-medium bg-blue-100 text-blue-800"> 
                          {company.activityAreas.join(", ")}
                        </span>
                      )}
                    </div>
  
                    <div className="space-y-2"> {/* Espacement augmenté */}
                      <div className="flex items-center gap-2 text-base text-gray-600"> 
                        <Phone size={16} className="text-primary flex-shrink-0"/>
                        <span className="break-all">{company.phone ?? "Non renseigné"}</span>
                      </div>
  
                      {company.email && (
                        <div className="flex items-center gap-2 text-base text-gray-600"> 
                          <Mail size={16} className="text-primary flex-shrink-0"/>
                          {renderEmailWithTooltip(company.email)}
                        </div>
                      )}
  
                      {company.address && (
                        <div className="flex items-center gap-2 text-base text-gray-600"> 
                          <MapPin size={16} className="text-primary flex-shrink-0"/>
                          <span className="break-all">{company.address}</span>
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
                          <Printer size={20}/> 
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
                        >
                          <Pencil size={20}/> 
                        </Button>
                      </TooltipTrigger>
                      <TooltipContent>Modifier</TooltipContent>
                    </Tooltip>
                  </TooltipProvider>
                </div>
              </div>
  
              {/* Sections d'informations */}
              <div className="space-y-3"> {/* Espacement augmenté */}
                {/* Section 1: Informations Générales */}
                <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
                  <div className="bg-gray-50 px-4 py-3 border-b border-gray-200"> 
                    <h3 className="font-semibold flex items-center gap-2 text-base"> 
                      <Building2 size={18} className="text-primary"/> 
                      Informations générales
                    </h3>
                  </div>
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
                    <InfoLine 
                      icon={<Mail size={18}/>} 
                      label="Email" 
                      value={company.email}
                      valueClassName="text-blue-600 hover:text-blue-800 hover:underline break-all"
                      className="border-b md:border-b-0 md:border-r lg:border-b-0"
                    />
                    <InfoLine 
                      icon={<Globe size={18}/>} 
                      label="Site web" 
                      value={company.webSite}
                      valueClassName="text-blue-600 hover:text-blue-800 hover:underline break-all"
                      className="border-b lg:border-b-0 lg:border-r"
                    />
                    <InfoLine 
                      icon={<MapPin size={18}/>} 
                      label="Adresse" 
                      value={company.address}
                      className="border-b md:border-b md:border-r-0"
                    />
                    <InfoLine 
                      icon={<ShieldCheck size={18}/>} 
                      label="TVA" 
                      value={`${company.tvaVal}%`}
                      className="border-b md:border-r"
                    />
                    <InfoLine 
                      icon={<Landmark size={18}/>} 
                      label="Pays" 
                      value={company.country}
                      className="border-b lg:border-r"
                    />
                    <InfoLine 
                      icon={<BadgeInfo size={18}/>} 
                      label="Secteurs d'activité" 
                      value={company.activityAreas?.join(", ")}
                    />
                  </div>
                </div>
  
                {/* Section 2: Identifiants Légaux */}
                <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
                  <div className="bg-gray-50 px-4 py-3 border-b border-gray-200"> 
                    <h3 className="font-semibold flex items-center gap-2 text-base"> 
                      <ScrollText size={18} className="text-primary"/> 
                      Identifiants légaux
                    </h3>
                  </div>
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
                    <InfoLine 
                      icon={<FileText size={18}/>} 
                      label="RCCM" 
                      value={company.rccm}
                      className="border-b md:border-r"
                    />
                    <InfoLine 
                      icon={<FileText size={18}/>} 
                      label="IFU" 
                      value={company.ifu}
                      className="border-b lg:border-b-0 lg:border-r"
                    />
                    <InfoLine 
                      icon={<FileText size={18}/>} 
                      label="NSS" 
                      value={company.nss}
                      className="border-b md:border-b-0 md:border-r lg:border-b-0"
                    />
                    <InfoLine 
                      icon={<FileText size={18}/>} 
                      label="RSS" 
                      value={company.rss}
                      className="border-b md:border-r"
                    />
                    <div className="border-b border-gray-200 p-3 hidden md:block"></div>
                    <div className="border-b border-gray-200 p-3 hidden lg:block"></div>
                  </div>
                </div>
                
                {/* Section 3: Direction */}
                <div className="bg-white rounded-lg shadow-sm border overflow-hidden">
                  <div className="bg-gray-50 px-4 py-3 border-b"> 
                    <h3 className="font-semibold flex items-center gap-2 text-base"> 
                      <User size={18} className="text-primary"/> 
                      Direction
                    </h3>
                  </div>
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
                    <InfoLine 
                      icon={<User size={18}/>} 
                      label="Nom" 
                      value={company.directorName}
                      className="border-b md:border-r"
                    />
                    <InfoLine 
                      icon={<Mail size={18}/>} 
                      label="Email" 
                      value={company.directorEmail}
                      valueClassName="text-blue-600 hover:text-blue-800 hover:underline break-all"
                      className="border-b lg:border-b-0 lg:border-r"
                    />
                    <InfoLine 
                      icon={<Phone size={18}/>} 
                      label="Téléphone" 
                      value={company.directorPhone}
                      className="border-b md:border-b md:border-r-0"
                    />
                  </div>
                </div>
              </div>
            </div>
          )}
        </TabsContent>
  
        {/* Autres onglets */}
        {!isLoading && (
          <>
            <TabsContent value="employes" className="flex-1 overflow-y-auto space-y-4">
              <ContratListTabs company={company} />
            </TabsContent>
            <TabsContent value="parametres" className="flex-1 overflow-y-auto px-1">
              <SettingGeneralTab company={company} />
            </TabsContent>
          </>
        )}
      </Tabs>
    );
  }