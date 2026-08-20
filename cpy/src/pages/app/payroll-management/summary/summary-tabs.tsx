import {
    FileBarChart2Icon,
    ClipboardListIcon,
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
import MensuelCsRisqueTab from "./mensuel-cs-risque-tab";
import MensuelCfTab from "./mensuel-cf-tab";
  
  interface SummaryTabsProps {
    company?: CompanyType | null;
    isLoading?: boolean;
    setEmployee?: (employee: CompanyType | null) => void;
  }
  
  export default function SummaryTabs({
    company,
    isLoading = false,
    setEmployee,
  }: SummaryTabsProps) {
    const tabTriggerClass =
      "hover:bg-accent hover:text-foreground data-[state=active]:after:bg-primary data-[state=active]:hover:bg-accent relative after:absolute after:inset-x-0 after:bottom-0 after:-mb-1 after:h-0.5 data-[state=active]:bg-transparent data-[state=active]:shadow-none";
    const iconClass = "-ms-0.5 me-1.5 opacity-60";
  
    const renderTabs = () => {
      if (isLoading) {
        return Array.from({ length: 2 }).map((_, i) => (
          <Skeleton key={i} className="h-8 w-52 rounded-sm" />
        ));
      }
  
      return (
        <>
          <TabsTrigger value="monthly-cs" className={tabTriggerClass}>
            <FileBarChart2Icon size={16} className={iconClass} />
            Mensuel CS par risque professionnel
          </TabsTrigger>
          <TabsTrigger value="monthly-cf" className={tabTriggerClass}>
            <ClipboardListIcon size={16} className={iconClass} />
            Mensuel CF
          </TabsTrigger>
        </>
      );
    };
  
    return (
      <Tabs defaultValue="monthly-cs">
        <ScrollArea>
          <TabsList className="text-foreground h-auto border-b bg-transparent w-full justify-start">
            {renderTabs()}
          </TabsList>
          <ScrollBar orientation="horizontal" />
        </ScrollArea>
  
        {!isLoading && (
          <>
            <TabsContent value="monthly-cs" className="px-4 space-y-4">
            <MensuelCsRisqueTab/>
            </TabsContent>
  
            <TabsContent value="monthly-cf" className="px-4 space-y-4">
              <MensuelCfTab />
            </TabsContent>
          </>
        )}
      </Tabs>
    );
  }
  