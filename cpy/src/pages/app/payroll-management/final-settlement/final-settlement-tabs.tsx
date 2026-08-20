import {
    HandCoinsIcon,
    SunIcon,
    AlarmClockIcon,
    FileTextIcon
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
import LeaveAllowanceTab from "./leave-allowance-tab";
import TerminationAllowanceTab from "./termination-allowance-tab"
// import NoticePeriodTab from "./notice-period-tab"; // À créer
// import SummaryTab from "./summary-tab"; // À créer

interface FinalSettlementTabsProps {
    company?: CompanyType | null;
    isLoading?: boolean;
}

export default function FinalSettlementTabs({ 
    company,
    isLoading = false 
}: FinalSettlementTabsProps) {
    const tabTriggerClass =
        "hover:bg-accent hover:text-foreground data-[state=active]:after:bg-primary data-[state=active]:hover:bg-accent relative after:absolute after:inset-x-0 after:bottom-0 after:-mb-1 after:h-0.5 data-[state=active]:bg-transparent data-[state=active]:shadow-none"
    const iconClass = "-ms-0.5 me-1.5 opacity-60"

    const renderTabs = () => {
        if (isLoading) {
            return Array.from({ length: 4 }).map((_, i) => (
                <Skeleton key={i} className="h-8 w-48 rounded-sm" />
            ))
        }

        return (
            <>
                <TabsTrigger value="paidleave" className={tabTriggerClass}>
                    <SunIcon size={16} className={iconClass} />
                    Indemnités de congés
                </TabsTrigger>
                <TabsTrigger value="termination" className={tabTriggerClass}>
                    <HandCoinsIcon size={16} className={iconClass} />
                    Indemnités de licenciement
                </TabsTrigger>
                <TabsTrigger value="notice" className={tabTriggerClass}>
                    <AlarmClockIcon size={16} className={iconClass} />
                    Préavis
                </TabsTrigger>
                <TabsTrigger value="summary" className={tabTriggerClass}>
                    <FileTextIcon size={16} className={iconClass} />
                    Récapitulatif
                </TabsTrigger>
            </>
        )
    }

    return (
        <Tabs defaultValue="paidleave">
            <ScrollArea>
                <TabsList className="text-foreground h-auto border-b bg-transparent w-full justify-start">
                    {renderTabs()}
                </TabsList>
                <ScrollBar orientation="horizontal" />
            </ScrollArea>

            {!isLoading && (
                <>
                    <TabsContent value="paidleave">
                        <LeaveAllowanceTab />
                    </TabsContent>
                    
                    <TabsContent value="termination">
                        <TerminationAllowanceTab />
                    </TabsContent>

                    <TabsContent value="notice">
                        {/* <NoticePeriodTab company={company} /> */}
                    </TabsContent>

                    <TabsContent value="summary">
                        {/* <SummaryTab company={company} /> */}
                    </TabsContent>
                </>
            )}
        </Tabs>
    )
}