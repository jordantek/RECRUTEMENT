import {
  LayersIcon,
  ShuffleIcon,
  HandIcon,
  WalletIcon,
  MinusIcon,
} from "lucide-react";

import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from "@/components/ui/tabs";
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";
import { Skeleton } from "@/components/ui/skeleton";
import { CompanyType } from "@/types/company/CompanyType.ts";

import FixedSalaryTab
    from "@/pages/app/payroll-management/salary-processing/salary-processing-tabs/fixed-salary-tab.tsx";
import VariableSalaryTab
    from "@/pages/app/payroll-management/salary-processing/salary-processing-tabs/variable-salary-tab.tsx";

import MensualiteTab from "@/pages/app/payroll-management/salary-processing/salary-processing-tabs/mensualite-tab.tsx";
import EtatPrelevementMensualiteTab
    from "@/pages/app/payroll-management/salary-processing/salary-processing-tabs/etat-prelevement-mensualite-tab.tsx";
    import AdvanceTab
    from "@/pages/app/payroll-management/salary-processing/salary-processing-tabs/advance-tab.tsx";
import AcompteTab from "./salary-processing-tabs/accompte-tab";


interface SalaryProcessingTabsProps {
  company?: CompanyType | null;
  isLoading?: boolean;
  setEmployee?: (employee: CompanyType | null) => void;
}

export default function SalaryProcessingTabs({
  isLoading = false,
}: SalaryProcessingTabsProps) {
  const tabTriggerClass =
    "hover:bg-accent hover:text-foreground data-[state=active]:after:bg-primary data-[state=active]:hover:bg-accent relative after:absolute after:inset-x-0 after:bottom-0 after:-mb-1 after:h-0.5 data-[state=active]:bg-transparent data-[state=active]:shadow-none";
  const iconClass = "-ms-0.5 me-1.5 opacity-60";

  const renderTabs = () => {
    if (isLoading) {
      return Array.from({ length: 6 }).map((_, i) => (
        <Skeleton key={i} className="h-8 w-36 rounded-sm" />
      ));
    }

    return (
      <>
        <TabsTrigger value="fixed" className={tabTriggerClass}>
          <LayersIcon size={16} className={iconClass} />
          Éléments fixes
        </TabsTrigger>
        <TabsTrigger value="variable" className={tabTriggerClass}>
          <ShuffleIcon size={16} className={iconClass} />
          Éléments variables
        </TabsTrigger>
        <TabsTrigger value="advance" className={tabTriggerClass}>
          <HandIcon size={16} className={iconClass} />
          Avance
        </TabsTrigger>
        <TabsTrigger value="deposit" className={tabTriggerClass}>
          <WalletIcon size={16} className={iconClass} />
          Acompte
        </TabsTrigger>
          <TabsTrigger value="mensualite" className={tabTriggerClass}>
              <WalletIcon size={16} className={iconClass} />
              Mensualitées
          </TabsTrigger>
        <TabsTrigger value="deduction" className={tabTriggerClass}>
          <MinusIcon size={16} className={iconClass} />
          Prélèvements
        </TabsTrigger>
      </>
    );
  };

  return (
    <Tabs defaultValue="fixed">
      <ScrollArea>
        <TabsList className="text-foreground h-auto border-b bg-transparent w-full justify-start">
          {renderTabs()}
        </TabsList>
        <ScrollBar orientation="horizontal" />
      </ScrollArea>

      {!isLoading && (
        <>
          <TabsContent value="fixed" className="px-4 space-y-4">
           <FixedSalaryTab/>
          </TabsContent>

          <TabsContent value="variable" className="px-4 space-y-4">
              <VariableSalaryTab/>
          </TabsContent>
          <TabsContent value="advance" className="px-4 space-y-4">
            <AdvanceTab/>
          </TabsContent>

          <TabsContent value="deposit" className="px-4 space-y-4">
          <AcompteTab/>
          </TabsContent>

            <TabsContent value="mensualite" className="px-4 space-y-4">
                <MensualiteTab/>
            </TabsContent>
          <TabsContent value="deduction" className="px-4 space-y-4">
           <EtatPrelevementMensualiteTab/>
          </TabsContent>
        </>
      )}
    </Tabs>
  );
}
