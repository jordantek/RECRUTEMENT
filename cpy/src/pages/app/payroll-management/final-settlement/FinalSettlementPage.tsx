import PageTitle from '@/components/seo/pageTitle';
import { FileText } from 'lucide-react';
import { useEffect } from 'react';
import usePageTitleStore from '@/contexts/usePageTitleStore.ts';
import { Icon } from '@tabler/icons-react';
import useCompanyStore from "@/contexts/CompanyContext.ts";
import FinalSettlementTabs from './final-settlement-tabs';

export function FinalSettlementPage() {
    const { selectedCompany } = useCompanyStore();

    useEffect(() => {
        usePageTitleStore
            .getState()
            .setTitle('Solde tout compte', 'Gestion des soldes de tout compte', FileText as Icon);
    }, []);

    return (
        <>
            <PageTitle title="Solde tout compte" />

            <div className="overflow-y-auto border-t border-gray-200">
                {selectedCompany ? (
                    <FinalSettlementTabs company={selectedCompany} />
                ) : (
                    <div className="p-4 text-center text-muted-foreground text-sm">
                        Veuillez sélectionner une entreprise pour accéder aux fonctionnalités de solde tout compte.
                    </div>
                )}
            </div>
        </>
    );
}