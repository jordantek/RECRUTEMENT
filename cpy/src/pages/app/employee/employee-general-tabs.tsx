import {
    BabyIcon,
    BadgeCheckIcon,
    CalendarOffIcon,
    Info,
    Phone,
    Gavel, // Pour Sanctions
    GraduationCap, // Pour Formations
    Bandage, // Pour Accident de travail
    Users, // Pour Événement social
} from "lucide-react";

import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area.tsx";
import {
    Tabs,
    TabsContent,
    TabsList,
    TabsTrigger,
} from "@/components/ui/tabs.tsx";
import { EmployeeType } from "@/types/employee/EmployeeType.ts";
import { Skeleton } from "@/components/ui/skeleton.tsx";
import {useEffect} from "react";
import EmployeChildrenTab from "@/pages/app/employee/tabs/employe-children-tab.tsx";
import EmployePersonneAPrevenirTab from "@/pages/app/employee/tabs/employe-personne-a-prevenir-tab.tsx";
import EmployeContratTab from "@/pages/app/employee/tabs/employe-contrat-tab.tsx";
import {iconClass, tabTriggerClass} from "@/style.ts";
import EmployeAbsencesTab from "./tabs/employe-absences-tab.tsx";
import EmployeSanctionsTab from "@/pages/app/employee/tabs/employe-sanctions-tab.tsx";
import EmployeFormationsTab from "@/pages/app/employee/tabs/employe-formations-tab.tsx";
import EmployeAccidentsTab from "@/pages/app/employee/tabs/employe-accidents-tab.tsx";
import EmployeEvenementsTab from "@/pages/app/employee/tabs/employe-evenements-tab.tsx";
import EmployeInformationTab from "@/pages/app/employee/tabs/employe-information-tab.tsx";

interface EmployeeTabsProps {
    employee?: EmployeeType|null;
    setEmployee: (employee: EmployeeType | null) => void;
}

export default function EmployeeGeneralTabs({ employee, setEmployee }: EmployeeTabsProps) {
    const isLoading = !employee;

    useEffect(() => {
        const handleKeyDown = (e: KeyboardEvent) => {
            if (e.key === "Escape" && employee) {
                setEmployee(null);
            }
        };

        window.addEventListener("keydown", handleKeyDown);
        return () => window.removeEventListener("keydown", handleKeyDown);
    }, [employee, setEmployee]);

    const renderTabs = () => {
        if (isLoading) {
            return (
                <>
                    {Array.from({ length: 5 }).map((_, i) => (
                        <Skeleton key={i} className="h-8 w-24 rounded-sm" />
                    ))}
                </>
            );
        }

        return (
            <>
                <TabsTrigger value="tab-1" className={tabTriggerClass}>
                    <Info className={iconClass} size={16} />
                    Informations
                </TabsTrigger>
                <TabsTrigger value="contrat" className={tabTriggerClass}>
                    <BadgeCheckIcon className={iconClass} size={16} />
                    Contrats
                </TabsTrigger>
                <TabsTrigger value="enfant" className={tabTriggerClass}>
                    <BabyIcon className={iconClass} size={16} />
                    Enfants
                </TabsTrigger>
                <TabsTrigger value="personnePrevenir" className={tabTriggerClass}>
                    <Phone className={iconClass} size={16} />
                    Personnes à prévenir
                </TabsTrigger>
                <TabsTrigger value="absences" className={tabTriggerClass}>
                    <CalendarOffIcon className={iconClass} size={16} />
                    Absences
                </TabsTrigger>
                <TabsTrigger value="sanctions" className={tabTriggerClass}>
                    <Gavel className={iconClass} size={16} />
                    Sanctions
                </TabsTrigger>
                <TabsTrigger value="formations" className={tabTriggerClass}>
                    <GraduationCap className={iconClass} size={16} />
                    Formations
                </TabsTrigger>
                <TabsTrigger value="accidents" className={tabTriggerClass}>
                    <Bandage className={iconClass} size={16} />
                    Accidents
                </TabsTrigger>
                <TabsTrigger value="evenements" className={tabTriggerClass}>
                    <Users className={iconClass} size={16} />
                    Événements
                </TabsTrigger>
            </>
        );
    };

    return (
        <Tabs defaultValue="tab-1">
            <ScrollArea>    
                <TabsList
                    className="text-foreground mb-3 h-auto gap-2 rounded-none border-b bg-transparent px-1 w-full justify-start"
                    aria-disabled={isLoading}
                >
                    {renderTabs()}
                </TabsList>
                <ScrollBar orientation="horizontal" />
            </ScrollArea>

            <TabsContent value="tab-1" className="overflow-y-auto px-2 scroll-hidden">
                {isLoading ? (
                    <p className="text-xs italic text-gray-500 text-center py-4">
                        Aucun employé sélectionné. Veuillez en choisir un pour voir les détails.
                    </p>
                ) : (
                   <EmployeInformationTab employee={employee} />
                )}
            </TabsContent>

            {/* Autres onglets */}
            {!isLoading && (
                <>
                    <TabsContent value="enfant">
                        <EmployeChildrenTab employe={employee} />
                    </TabsContent>
                    <TabsContent value="personnePrevenir">
                        <EmployePersonneAPrevenirTab employe={employee} />
                    </TabsContent>
                    <TabsContent value="contrat">
                        <EmployeContratTab emmploye={employee} />
                    </TabsContent>
                    <TabsContent value="absences">
                        <EmployeAbsencesTab employe={employee} />
                    </TabsContent>

                    <TabsContent value="sanctions">
                        <EmployeSanctionsTab employe={employee} />
                    </TabsContent>
                    <TabsContent value="formations">
                        <EmployeFormationsTab employe={employee} />
                    </TabsContent>
                    <TabsContent value="accidents">
                       <EmployeAccidentsTab employe={employee}/>
                    </TabsContent>
                    <TabsContent value="evenements">
                        <EmployeEvenementsTab employe={employee} />
                    </TabsContent>

                </>
            )}
        </Tabs>
    );
}