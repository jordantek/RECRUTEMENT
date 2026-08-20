import PageTitle from '@/components/seo/pageTitle';
import { CalendarDays } from 'lucide-react';
import { useEffect } from 'react';
import usePageTitleStore from '@/contexts/usePageTitleStore.ts';
import { Icon } from '@tabler/icons-react';
import AbsenceRequestTabs from './absence-request-tabs.tsx';

export function EmployeeAbsenceRequestPage() {
    useEffect(() => {
        usePageTitleStore
            .getState()
            .setTitle(
                'Demandes d\'absence',
                'Créer ou consulter vos demandes',
                CalendarDays as Icon
            );
    }, []);

    return (
        <>
            <PageTitle title="Demandes d'absence" />
            <div className="h-[calc(100vh-64px)] rounded overflow-hidden bg-background">
                <div className="overflow-y-auto border-l border-gray-200">
                    <AbsenceRequestTabs />
                </div>
            </div>
        </>
    );
}
