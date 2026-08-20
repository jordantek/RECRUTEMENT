    import {
        Tabs,
        TabsContent,
        TabsList,
        TabsTrigger,
    } from '@/components/ui/tabs.tsx';
    import { ScrollArea, ScrollBar } from '@/components/ui/scroll-area.tsx';
    import { ClipboardList, PlusCircle } from 'lucide-react';
    
    import NewAbsenceRequestForm from './new-absence-request-form'; // ✅ ton composant actuel
    import AbsenceRequestList from './absence-request-list';
    
    export default function AbsenceRequestTabs() {
        return (
        <Tabs defaultValue="view-requests" className="w-full">
            <ScrollArea>
            <TabsList className="text-blue-900 h-auto rounded-none border-b bg-transparent px-1 w-full justify-start">
                <TabsTrigger value="view-requests" className={tabTriggerClass}>
                <ClipboardList className={iconClass} size={16} />
                Mes Demandes
                </TabsTrigger>
                <TabsTrigger value="new-request" className={tabTriggerClass}>
                <PlusCircle className={iconClass} size={16} />
                Nouvelle Demande
                </TabsTrigger>
            </TabsList>
            <ScrollBar orientation="horizontal" />
            </ScrollArea>
    
            <TabsContent value="view-requests" className="px-6 py-4">
            <AbsenceRequestList />
            </TabsContent>
            <TabsContent value="new-request" className="px-6 py-4">
            <NewAbsenceRequestForm />
            </TabsContent>
        </Tabs>
        );
    }
    
    const tabTriggerClass =
        "hover:bg-blue-100 text-blue-900 data-[state=active]:after:bg-blue-900 data-[state=active]:font-semibold relative after:absolute after:inset-x-0 after:bottom-0 after:-mb-1 after:h-0.5";
    const iconClass = "-ms-0.5 me-1.5 opacity-60";
    