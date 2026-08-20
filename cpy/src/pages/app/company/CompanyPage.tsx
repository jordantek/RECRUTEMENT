import PageTitle from '@/components/seo/pageTitle';
import {
    Building2Icon,
} from 'lucide-react';
import { useEffect } from 'react';
import usePageTitleStore from '@/contexts/usePageTitleStore.ts';
import { Icon } from '@tabler/icons-react';
import CompanyTabs from '@/pages/app/company/company-tabs.tsx';
import useCompanyStore from "@/contexts/CompanyContext.ts";

export function CompanyPage() {
    const {setShowCompanySelect,selectedCompany} = useCompanyStore();
    useEffect(() => {
        usePageTitleStore
            .getState()
            .setTitle('Entreprises', "Gestion d'entreprises", Building2Icon as Icon);
        setShowCompanySelect(true);
    }, []);
    return (
        <>
            <PageTitle title="Entreprises" />
            <div className="h-[calc(100vh-64px)] rounded overflow-hidden bg-background">
              <div className="h-[600px] overflow-y-auto border-l border-gray-200">
                <CompanyTabs company={selectedCompany} />
              </div>
            </div>
        </>
    );
}
