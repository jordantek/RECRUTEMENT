    import {
        GiftIcon,
        CalendarCheck2Icon,
        ClockIcon,
        TimerIcon,
    } from "lucide-react"
    import {
        Tabs,
        TabsContent,
        TabsList,
        TabsTrigger,
    } from "@/components/ui/tabs"
    import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area"
    import { Skeleton } from "@/components/ui/skeleton"
    import { CompanyType } from "@/types/company/CompanyType";
    import LeaveAllowanceTab from "./leave-allowance-tab"
    import WorkingTimeTab from "./working-time-tab"
    import {OvertimeTab} from "@/pages/app/payroll-management/salary-accessory/tabs/overtime-tab.tsx";
import AllowanceAndBonusTab from "./allowance-and-bonus-tab";


  interface SalaryAccessoryTabsProps {
    company?: CompanyType | null;
    isLoading?: boolean;
    setEmployee?: (employee: CompanyType | null) => void;
  }
  
  export default function SalaryAccessoryTabs({ isLoading = false }: SalaryAccessoryTabsProps) {
    const tabTriggerClass =
      "hover:bg-accent hover:text-foreground data-[state=active]:after:bg-primary data-[state=active]:hover:bg-accent relative after:absolute after:inset-x-0 after:bottom-0 after:-mb-1 after:h-0.5 data-[state=active]:bg-transparent data-[state=active]:shadow-none"
    const iconClass = "-ms-0.5 me-1.5 opacity-60"
  
    const renderTabs = () => {
      if (isLoading) {
        return Array.from({ length: 5 }).map((_, i) => (
          <Skeleton key={i} className="h-8 w-40 rounded-sm" />
        ))
      }
  
      return (
        <>
         <TabsTrigger value="bonuses" className={tabTriggerClass}>
                    <GiftIcon size={16} className={iconClass} />
                    Indemnités et primes
                </TabsTrigger>
          <TabsTrigger value="paidleave" className={tabTriggerClass}>
            <CalendarCheck2Icon size={16} className={iconClass} />
            Allocation de congés
          </TabsTrigger>
          <TabsTrigger value="overtime" className={tabTriggerClass}>
            <ClockIcon size={16} className={iconClass} />
            Heures supplémentaires
          </TabsTrigger>
          <TabsTrigger value="workingtime" className={tabTriggerClass}>
            <TimerIcon size={16} className={iconClass} />
            Temps de travail
          </TabsTrigger>
        </>
      )
    }
  
    return (
      <Tabs defaultValue="bonuses">
        <ScrollArea>
          <TabsList className="text-foreground h-auto border-b bg-transparent w-full justify-start">
            {renderTabs()}
          </TabsList>
          <ScrollBar orientation="horizontal" />
        </ScrollArea>
  
        {!isLoading && (
          <> 

            {/* Indemnités et primes */}
            <TabsContent value="bonuses">
              <AllowanceAndBonusTab />
            </TabsContent>

            {/* Indemnité de congés */}
            <TabsContent value="paidleave">
                <LeaveAllowanceTab />
            </TabsContent>

            {/* Heures supplémentaires */}
            <TabsContent value="overtime">
              <OvertimeTab />
            </TabsContent>
  
            {/* Temps de travail */}
            <TabsContent value="workingtime">
              <WorkingTimeTab />
            </TabsContent>
          </>
        )}
      </Tabs>
    )
  }
  