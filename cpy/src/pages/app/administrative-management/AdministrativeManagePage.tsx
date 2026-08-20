import PageTitle from '@/components/seo/pageTitle';
import {
    BadgeAlert,
    SearchIcon,
    ShieldCheckIcon,
} from 'lucide-react';
import { useEffect, useState } from 'react';
import usePageTitleStore from '@/contexts/usePageTitleStore.ts';
import { Icon } from '@tabler/icons-react';
import { Input } from '@/components/ui/input.tsx';
import { Button } from '@/components/ui/button.tsx';
import { Skeleton } from '@/components/ui/skeleton.tsx';
import { useAuth } from '@/lib/auth.ts';

import CompanyItem from '@/components/layout/company/company-item.tsx';
import { CompanyType } from '@/types/company/CompanyType.ts';
import apiService from '@/api/apiService.ts';
import apiRoutes from '@/api/apiRoutes.ts';
import useCompanyStore from "@/contexts/CompanyContext.ts";
import AdministrativeTabs from "@/pages/app/administrative-management/administrative-tabs.tsx";

export function AdministrativeManagePage() {
    const {setShowCompanySelect,selectedCompany} = useCompanyStore();

    useEffect(() => {
        usePageTitleStore
            .getState()
            .setTitle('Gestion administrative', 'Données administratives des entreprises', ShieldCheckIcon as Icon);
        setShowCompanySelect(true);
    }, []);


    return (
        <>
            <PageTitle title="Gestion administrative" />
                {/*Contenu administratif*/}
                <div className="overflow-y-auto col-span-12">
                    {selectedCompany ? (
                       <>
                           <AdministrativeTabs company={selectedCompany} />
                       </>
                    ) : (
                        <div className="text-center text-muted-foreground text-sm">
                            Sélectionnez une entreprise pour voir ses données administratives.
                        </div>
                    )}
                </div>

        </>
    );
}
