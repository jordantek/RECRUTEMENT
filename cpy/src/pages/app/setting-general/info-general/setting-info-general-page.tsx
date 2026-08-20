import { useEffect } from "react";
import PageTitle from "@/components/seo/pageTitle";
import usePageTitleStore from "@/contexts/usePageTitleStore";
import {
    Landmark,
    Settings,
    UsersRoundIcon,
} from "lucide-react";
import {
    Tabs,
    TabsContent,
    TabsList,
    TabsTrigger,
} from "@/components/ui/tabs.tsx";
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area.tsx";
import {Icon} from "@tabler/icons-react";
import DiplomeManagerSettingTab from "@/pages/app/setting-general/info-general/tabs/diplome-manager-setting-tab.tsx";
import TvaSettingTab from "@/pages/app/setting-general/info-general/tabs/tva-manager-setting-tab.tsx";
import DomaineSettingTab from "@/pages/app/setting-general/info-general/tabs/domaine-manager-setting-tab";
import CategorieSocioProTab from "@/pages/app/setting-general/info-general/tabs/categorie-socio-pro-tab";
import { tabTriggerClass } from "@/style";


export function SettingInfoGeneralPage() {
    // Mise à jour du titre de la page via le store
    useEffect(() => {
        usePageTitleStore.getState().setTitle(
            "Paramètres système",
            "Informations générales",
            Settings as Icon
        );
    }, []);

    return (
        <>
            {/* Titre SEO */}
            <PageTitle title="Paramètres système" />

            {/* Tabs principaux */}
            <Tabs defaultValue="diplomes">
                <ScrollArea className="w-full">
                    <TabsList className="text-foreground mb-3 h-auto gap-2 rounded-none border-b bg-transparent px-1 w-full justify-start">
                        <TabsTrigger value="diplomes" className={tabTriggerClass}>
                            <Landmark className="me-1.5 opacity-60" size={16} />
                            Diplômes
                        </TabsTrigger>

                        <TabsTrigger value="tva" className={tabTriggerClass}>
                            <Landmark className="me-1.5 opacity-60" size={16} />
                            TVA
                        </TabsTrigger>

                        <TabsTrigger value="domaines" className={tabTriggerClass}>
                            <UsersRoundIcon className="me-1.5 opacity-60" size={16} />
                            Domaines d'activité
                        </TabsTrigger>

                        <TabsTrigger value="categories" className={tabTriggerClass}>
                            <UsersRoundIcon className="me-1.5 opacity-60" size={16} />
                            Catégories socio-professionnelles
                        </TabsTrigger>
                    </TabsList>

                    <ScrollBar orientation="horizontal" />
                </ScrollArea>

                {/* Contenu des tabs */}
                <TabsContent value="diplomes">
                  <DiplomeManagerSettingTab/>
                </TabsContent>

                <TabsContent value="tva">
                    <TvaSettingTab/>
                </TabsContent>

                <TabsContent value="domaines">
                    <DomaineSettingTab/>
                </TabsContent>

                <TabsContent value="categories">
                    <CategorieSocioProTab/>
                </TabsContent>
            </Tabs>
        </>
    );
}