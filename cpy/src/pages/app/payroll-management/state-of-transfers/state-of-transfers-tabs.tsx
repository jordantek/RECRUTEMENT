import {
    BanknoteIcon,
    Building2Icon,
    CalendarIcon,
    UsersIcon,
    BarChartIcon,
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
  import { Input } from "@/components/ui/input";
  
  interface StateOfTransfersTabsProps {
    company?: CompanyType | null;
    isLoading?: boolean;
    setEmployee?: (employee: CompanyType | null) => void;
  }
  
  export default function StateOfTransfersTabs({
    company,
    isLoading = false,
    setEmployee,
  }: StateOfTransfersTabsProps) {
    const tabTriggerClass =
      "hover:bg-accent hover:text-foreground data-[state=active]:after:bg-primary data-[state=active]:hover:bg-accent relative after:absolute after:inset-x-0 after:bottom-0 after:-mb-1 after:h-0.5 data-[state=active]:bg-transparent data-[state=active]:shadow-none";
    const iconClass = "-ms-0.5 me-1.5 opacity-60";
  
    const renderTabs = () => {
      if (isLoading) {
        return Array.from({ length: 5 }).map((_, i) => (
          <Skeleton key={i} className="h-8 w-56 rounded-sm" />
        ));
      }
  
      return (
        <>
          <TabsTrigger value="monthly-bank" className={tabTriggerClass}>
            <BanknoteIcon size={16} className={iconClass} />
            Mensuel / banque
          </TabsTrigger>
          <TabsTrigger value="monthly-bank-company" className={tabTriggerClass}>
            <Building2Icon size={16} className={iconClass} />
            Mensuel / banque et entreprise
          </TabsTrigger>
          <TabsTrigger value="monthly-company" className={tabTriggerClass}>
            <CalendarIcon size={16} className={iconClass} />
            Mensuel / entreprise
          </TabsTrigger>
          <TabsTrigger value="periodic-bank" className={tabTriggerClass}>
            <BarChartIcon size={16} className={iconClass} />
            Périodique / banque
          </TabsTrigger>
          <TabsTrigger value="periodic-employee" className={tabTriggerClass}>
            <UsersIcon size={16} className={iconClass} />
            Périodique / employé
          </TabsTrigger>
        </>
      );
    };
  
    return (
      <Tabs defaultValue="monthly-bank">
        <ScrollArea>
          <TabsList className="text-foreground h-auto border-b bg-transparent w-full justify-start">
            {renderTabs()}
          </TabsList>
          <ScrollBar orientation="horizontal" />
        </ScrollArea>
  
        {!isLoading && (
          <>
            <TabsContent value="monthly-bank" className="px-4 space-y-4">
              <h2 className="text-lg font-semibold">État mensuel par banque</h2>
              <Input type="month" />
              <Button>Générer le rapport</Button>
            </TabsContent>
  
            <TabsContent value="monthly-bank-company" className="px-4 space-y-4">
              <h2 className="text-lg font-semibold">État mensuel par banque et entreprise</h2>
              <Input type="month" />
              <Button>Générer le rapport</Button>
            </TabsContent>
  
            <TabsContent value="monthly-company" className="px-4 space-y-4">
              <h2 className="text-lg font-semibold">État mensuel par entreprise</h2>
              <Input type="month" />
              <Button>Générer le rapport</Button>
            </TabsContent>
  
            <TabsContent value="periodic-bank" className="px-4 space-y-4">
              <h2 className="text-lg font-semibold">État périodique par banque</h2>
              <div className="flex space-x-2">
                <Input type="date" placeholder="Date début" />
                <Input type="date" placeholder="Date fin" />
              </div>
              <Button>Générer le rapport</Button>
            </TabsContent>
  
            <TabsContent value="periodic-employee" className="px-4 space-y-4">
              <h2 className="text-lg font-semibold">État périodique par employé</h2>
              <div className="flex space-x-2">
                <Input type="date" placeholder="Date début" />
                <Input type="date" placeholder="Date fin" />
              </div>
              <Button>Générer le rapport</Button>
            </TabsContent>
          </>
        )}
      </Tabs>
    );
  }
  