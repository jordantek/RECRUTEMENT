    import {
        CalendarCheck2,
        CalendarDays,
        Gavel,
        GraduationCap,
        HeartHandshake,
        ShieldAlert,
    } from "lucide-react";

    import {
        Tabs,
        TabsContent,
        TabsList,
        TabsTrigger,
    } from "@/components/ui/tabs.tsx";
    import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area.tsx";
    import { CompanyType } from "@/types/company/CompanyType.ts";
    import { Skeleton } from "@/components/ui/skeleton.tsx";
    import WorkAccidentTab from "@/pages/app/administrative-management/tabs/work-accident-tab.tsx";
    import {iconClass, tabTriggerClass} from "@/style.ts";

    import SanctionTab from "@/pages/app/administrative-management/tabs/sanction-tab.tsx";

    import WorkAbsenceTab from "./tabs/absence-tab";
import TrainingTab from "./tabs/formation-tab";
import SocialEventTab from "./tabs/social-event-tab";
import CreditCongeTab from "./tabs/credit-conge-tab";
import SoldeCongeTab from "./tabs/solde-conge-tab";


    interface AdministrativeTabsProps {
        company?: CompanyType | null;
    }

    export default function AdministrativeTabs({ company }: AdministrativeTabsProps) {
        const isLoading = !company;
        const renderTabs = () => {
          if (isLoading) {
            return (
              <>
                {Array.from({ length: 6 }).map((_, i) => (
                  <Skeleton key={i} className="h-8 w-24 rounded-sm" />
                ))}
              </>
            );
          }
      
          return (
            <>
              <TabsTrigger value="credit-conge" className={tabTriggerClass}>
                <CalendarCheck2 size={16} className={iconClass} />
                Date ref. congés
              </TabsTrigger>

              <TabsTrigger value="solde-conge" className={tabTriggerClass}>
                <CalendarCheck2 size={16} className={iconClass} />
                Soldes congés
              </TabsTrigger>
      
              {/* Onglets existants */}
              <TabsTrigger value="absences" className={tabTriggerClass}>
                <CalendarDays size={16} className={iconClass} />
                Absences / Congés
              </TabsTrigger>
              <TabsTrigger value="sanctions" className={tabTriggerClass}>
                <Gavel size={16} className={iconClass} />
                Sanctions
              </TabsTrigger>
              <TabsTrigger value="formation" className={tabTriggerClass}>
                <GraduationCap size={16} className={iconClass} />
                Formation
              </TabsTrigger>
              <TabsTrigger value="evenement" className={tabTriggerClass}>
                <HeartHandshake size={16} className={iconClass} />
                Événement social
              </TabsTrigger>
              <TabsTrigger value="accident" className={tabTriggerClass}>
                <ShieldAlert size={16} className={iconClass} />
                Accident de travail
              </TabsTrigger>
            </>
          );
        };
      
        return (
          <Tabs defaultValue="credit-conge">
            <ScrollArea>
              <TabsList className="text-foreground h-auto border-b bg-transparent w-full justify-start">
                {renderTabs()}
              </TabsList>
              <ScrollBar orientation="horizontal" />
            </ScrollArea>
      
            <TabsContent value="credit-conge">
              <CreditCongeTab />
            </TabsContent>

            <TabsContent value="solde-conge">
             <SoldeCongeTab />
            </TabsContent>
      
            <TabsContent value="absences">
                <WorkAbsenceTab />
            </TabsContent>
      
            <TabsContent value="sanctions">
              <SanctionTab />
            </TabsContent>
      
            <TabsContent value="formation">
                <TrainingTab />
            </TabsContent>
      
            <TabsContent value="evenement">
                <SocialEventTab />
            </TabsContent>
            <TabsContent value="accident">
                <WorkAccidentTab />
            </TabsContent>
          </Tabs>
        );
      }
      