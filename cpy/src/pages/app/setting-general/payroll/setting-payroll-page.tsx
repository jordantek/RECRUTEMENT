import { useEffect } from "react";
import PageTitle from "@/components/seo/pageTitle";
import usePageTitleStore from "@/contexts/usePageTitleStore";
import {
    DollarSign,
    Landmark,
    Settings,
    CreditCard,
    Building,
    Columns,
    Layers
} from "lucide-react";
import {
    Tabs,
    TabsContent,
    TabsList,
    TabsTrigger,
} from "@/components/ui/tabs.tsx";
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area.tsx";
import { Icon } from "@tabler/icons-react";

import RubriqueSettingTab from "@/pages/app/setting-general/payroll/tabs/rubrique-setting-tab.tsx";
import BanqueSettingTab from "@/pages/app/setting-general/payroll/tabs/banque-setting-tab.tsx";
import ModePaiementSettingTab from "@/pages/app/setting-general/payroll/tabs/mode-paiement-setting-tab.tsx";
import {tabTriggerClass} from "@/style.ts";
import InstitutionFinancierTab from "@/pages/app/setting-general/payroll/tabs/institution-financier-tab.tsx";
import ColonneAffichageTab from "@/pages/app/setting-general/payroll/tabs/colonne-affichage-tab.tsx";
import NiveauAffichageTab from "@/pages/app/setting-general/payroll/tabs/niveau-affichage-tab.tsx";


export function SettingPayrollPage() {
    useEffect(() => {
        usePageTitleStore.getState().setTitle(
            "Paramètres système",
            "Informations générales",
            Settings as Icon
        );
    }, []);

    return (
        <>
            <PageTitle title="Paramètres système" />

            <Tabs defaultValue="rubriques">
                <ScrollArea className="w-full">
                    <TabsList className="text-foreground mb-3 h-auto gap-2 rounded-none border-b bg-transparent px-1 w-full justify-start">

                        <TabsTrigger value="colonnes-affichage" className={tabTriggerClass}>
                            <Columns className="me-1.5 opacity-60" size={16} />
                            Colonne Affichage
                        </TabsTrigger>

                        <TabsTrigger value="niveaux-affichage" className={tabTriggerClass}>
                            <Layers className="me-1.5 opacity-60" size={16} />
                            Niveau Affichage
                        </TabsTrigger>

                        <TabsTrigger value="rubriques" className={tabTriggerClass}>
                            <DollarSign className="me-1.5 opacity-60" size={16} />
                            Rubriques de paie
                        </TabsTrigger>

                        <TabsTrigger value="banques" className={tabTriggerClass}>
                            <Landmark className="me-1.5 opacity-60" size={16} />
                            Banques
                        </TabsTrigger>

                        <TabsTrigger value="modes-paiement" className={tabTriggerClass}>
                            <CreditCard className="me-1.5 opacity-60" size={16} />
                            Modes de paiements
                        </TabsTrigger>

                        <TabsTrigger value="institutions" className={tabTriggerClass}>
                            <Building className="me-1.5 opacity-60" size={16} />
                            Institutions financières
                        </TabsTrigger>

                    </TabsList>
                    <ScrollBar orientation="horizontal" />
                </ScrollArea>

                <TabsContent value="rubriques">
                    <RubriqueSettingTab />
                </TabsContent>

                <TabsContent value="banques">
                    <BanqueSettingTab />
                </TabsContent>

                <TabsContent value="modes-paiement">
                    <ModePaiementSettingTab />
                </TabsContent>

                <TabsContent value="institutions">
                    <InstitutionFinancierTab/>
                </TabsContent>

                <TabsContent value="colonnes-affichage">
                    <ColonneAffichageTab />
                </TabsContent>

                <TabsContent value="niveaux-affichage">
                    <NiveauAffichageTab />
                </TabsContent>
            </Tabs>
        </>
    );
}