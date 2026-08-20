import {
  BanknoteIcon,
  UserIcon,
  BuildingIcon,
  ShieldCheckIcon,
  LandmarkIcon,
  CalendarIcon,
} from "lucide-react";
import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from "@/components/ui/tabs";
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";
import { Skeleton } from "@/components/ui/skeleton";
import { CompanyType } from "@/types/company/CompanyType";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";

interface PaySheetTabsProps {
  company?: CompanyType | null;
  isLoading?: boolean;
  setEmployee?: (employee: CompanyType | null) => void;
}

export default function PaySheetTabs({
  isLoading = false,
}: PaySheetTabsProps) {
  const tabTriggerClass =
    "hover:bg-accent hover:text-foreground data-[state=active]:after:bg-primary data-[state=active]:hover:bg-accent relative after:absolute after:inset-x-0 after:bottom-0 after:-mb-1 after:h-0.5 data-[state=active]:bg-transparent data-[state=active]:shadow-none";
  const iconClass = "-ms-0.5 me-1.5 opacity-60";

  const renderTabs = () => {
    if (isLoading) {
      return Array.from({ length: 11 }).map((_, i) => (
        <Skeleton key={i} className="h-8 w-48 rounded-sm" />
      ));
    }

    return (
      <>
        <TabsTrigger value="netsalary" className={tabTriggerClass}>
          <BanknoteIcon size={16} className={iconClass} />
          E-SN
        </TabsTrigger>
        <TabsTrigger value="cs-employee" className={tabTriggerClass}>
          <UserIcon size={16} className={iconClass} />
          PCS / Emp
        </TabsTrigger>
        <TabsTrigger value="cs-company" className={tabTriggerClass}>
          <BuildingIcon size={16} className={iconClass} />
          PCS / Ent
        </TabsTrigger>
        <TabsTrigger value="cnss" className={tabTriggerClass}>
          <ShieldCheckIcon size={16} className={iconClass} />
          E-CNSS
        </TabsTrigger>
        <TabsTrigger value="tax" className={tabTriggerClass}>
          <LandmarkIcon size={16} className={iconClass} />
          E-Imp
        </TabsTrigger>
        <TabsTrigger value="cf-employee" className={tabTriggerClass}>
          <UserIcon size={16} className={iconClass} />
          PCF/ Emp
        </TabsTrigger>
        <TabsTrigger value="cf-company-monthly" className={tabTriggerClass}>
          <CalendarIcon size={16} className={iconClass} />
          MCF / Ent
        </TabsTrigger>
        <TabsTrigger value="cf-company-periodic" className={tabTriggerClass}>
          <CalendarIcon size={16} className={iconClass} />
         PCF / Ent
        </TabsTrigger>
        <TabsTrigger value="salary-company-monthly" className={tabTriggerClass}>
          <CalendarIcon size={16} className={iconClass} />
          MS / Ent
        </TabsTrigger>
        <TabsTrigger value="salary-company-periodic" className={tabTriggerClass}>
          <CalendarIcon size={16} className={iconClass} />
          PS / Ent
        </TabsTrigger>
        <TabsTrigger value="salary-employee-periodic" className={tabTriggerClass}>
          <CalendarIcon size={16} className={iconClass} />
          PS / Emp
        </TabsTrigger>
      </>
    );
  };

  return (
    <Tabs defaultValue="netsalary">
      <ScrollArea>
        <TabsList className="text-foreground h-auto border-b bg-transparent w-full justify-start">
          {renderTabs()}
        </TabsList>
        <ScrollBar orientation="horizontal" />
      </ScrollArea>

      {!isLoading && (
        <>
          <TabsContent value="netsalary" className="px-4 space-y-4">
            <h2 className="text-lg font-semibold">État des salaires nets par banques</h2>
            <Input type="month" placeholder="Mois concerné" />
            <Button>Générer le rapport</Button>
          </TabsContent>

          <TabsContent value="cs-employee" className="px-4 space-y-4">
            <h2 className="text-lg font-semibold">CS périodique par employé</h2>
            <Input placeholder="Nom de l'employé" />
            <Input type="date" placeholder="Date de début" />
            <Input type="date" placeholder="Date de fin" />
            <Button>Afficher</Button>
          </TabsContent>

          <TabsContent value="cs-company" className="px-4 space-y-4">
            <h2 className="text-lg font-semibold">CS périodique par entreprise</h2>
            <Input type="date" placeholder="Période de début" />
            <Input type="date" placeholder="Période de fin" />
            <Button>Exporter</Button>
          </TabsContent>

          <TabsContent value="cnss" className="px-4 space-y-4">
            <h2 className="text-lg font-semibold">État de la CNSS</h2>
            <Input type="month" placeholder="Mois concerné" />
            <Button>Générer le fichier CNSS</Button>
          </TabsContent>

          <TabsContent value="tax" className="px-4 space-y-4">
            <h2 className="text-lg font-semibold">État de l’impôt</h2>
            <Input type="month" placeholder="Mois concerné" />
            <Button>Exporter la déclaration fiscale</Button>
          </TabsContent>

          <TabsContent value="cf-employee" className="px-4 space-y-4">
            <h2 className="text-lg font-semibold">CF périodique par employé</h2>
            <Input placeholder="Nom de l'employé" />
            <Input type="date" placeholder="Date de début" />
            <Input type="date" placeholder="Date de fin" />
            <Button>Afficher</Button>
          </TabsContent>

          <TabsContent value="cf-company-monthly" className="px-4 space-y-4">
            <h2 className="text-lg font-semibold">CF mensuel par entreprise</h2>
            <Input type="month" placeholder="Mois" />
            <Button>Afficher le détail</Button>
          </TabsContent>

          <TabsContent value="cf-company-periodic" className="px-4 space-y-4">
            <h2 className="text-lg font-semibold">CF périodique par entreprise</h2>
            <Input type="date" placeholder="Date début" />
            <Input type="date" placeholder="Date fin" />
            <Button>Exporter</Button>
          </TabsContent>

          <TabsContent value="salary-company-monthly" className="px-4 space-y-4">
            <h2 className="text-lg font-semibold">Salaire mensuel par entreprise</h2>
            <Input type="month" placeholder="Mois" />
            <Button>Voir récapitulatif</Button>
          </TabsContent>

          <TabsContent value="salary-company-periodic" className="px-4 space-y-4">
            <h2 className="text-lg font-semibold">Salaire périodique par entreprise</h2>
            <Input type="date" placeholder="Date de début" />
            <Input type="date" placeholder="Date de fin" />
            <Button>Générer le tableau</Button>
          </TabsContent>

          <TabsContent value="salary-employee-periodic" className="px-4 space-y-4">
            <h2 className="text-lg font-semibold">Salaire périodique par employé</h2>
            <Input placeholder="Nom de l'employé" />
            <Input type="date" placeholder="Date de début" />
            <Input type="date" placeholder="Date de fin" />
            <Button>Consulter</Button>
          </TabsContent>
        </>
      )}
    </Tabs>
  );
}
