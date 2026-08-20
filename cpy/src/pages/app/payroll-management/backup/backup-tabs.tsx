import {
    EyeIcon,
    CalculatorIcon,
    SaveIcon,
    EyeOffIcon,
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

  import { Button } from "@/components/ui/button";

import CalculeSalaireTab from "@/pages/app/payroll-management/backup/tabs/calcule-salaire-tab.tsx";
import ApercuAvantTraitementSalaireTab from "./tabs/apercu-avant-traitement-salaire-tab";
import ApercuApresTraitementSalaireTab from "./tabs/apercu-apres-traitement-salaire-tab";

  
  interface BackupTabsProps {
    company?: CompanyType | null;
    isLoading?: boolean;
    setEmployee?: (employee: CompanyType | null) => void;
  }
  
  export default function BackupTabs({
    isLoading = false,
  }: BackupTabsProps) {
    const tabTriggerClass =
      "hover:bg-accent hover:text-foreground data-[state=active]:after:bg-primary data-[state=active]:hover:bg-accent relative after:absolute after:inset-x-0 after:bottom-0 after:-mb-1 after:h-0.5 data-[state=active]:bg-transparent data-[state=active]:shadow-none";
    const iconClass = "-ms-0.5 me-1.5 opacity-60";
  
    const renderTabs = () => {
      if (isLoading) {
        return Array.from({ length: 4 }).map((_, i) => (
          <Skeleton key={i} className="h-8 w-44 rounded-sm" />
        ));
      }
  
      return (
        <>
          <TabsTrigger value="preview-before" className={tabTriggerClass}>
            <EyeIcon size={16} className={iconClass} />
            Aperçu avant traitement
          </TabsTrigger>
          <TabsTrigger value="calculation" className={tabTriggerClass}>
            <CalculatorIcon size={16} className={iconClass} />
            Calcul de salaire
          </TabsTrigger>
          <TabsTrigger value="preview-after" className={tabTriggerClass}>
            <EyeOffIcon size={16} className={iconClass} />
            Aperçu après traitement
          </TabsTrigger>
          <TabsTrigger value="save" className={tabTriggerClass}>
            <SaveIcon size={16} className={iconClass} />
            Sauvegarde
          </TabsTrigger>
        </>
      );
    };
  
    return (
      <Tabs defaultValue="preview-before">
        <ScrollArea>
          <TabsList className="text-foreground rounded-none h-auto border-b bg-transparent w-full justify-start">
            {renderTabs()}
          </TabsList>
          <ScrollBar orientation="horizontal" />
        </ScrollArea>
  
        {!isLoading && (
          <>
            <TabsContent value="preview-before" className="px-4 ">
              <ApercuAvantTraitementSalaireTab/>
            </TabsContent>
  
            <TabsContent value="calculation" className="px-4">
             <CalculeSalaireTab/>
            </TabsContent>
  
            <TabsContent value="preview-after" className="px-4">
              <ApercuApresTraitementSalaireTab/>
            </TabsContent>
  
            <TabsContent value="save" className="px-4 space-y-4">
              <h2 className="text-lg font-semibold">Sauvegarde</h2>
              <p className="text-sm text-muted-foreground">
                Finaliser et enregistrer définitivement le traitement de salaire.
              </p>
              <div className="flex space-x-2">
                <Button variant="outline">Exporter le bulletin</Button>
                <Button>Sauvegarder le traitement</Button>
              </div>
            </TabsContent>
          </>
        )}
      </Tabs>
    );
  }
  