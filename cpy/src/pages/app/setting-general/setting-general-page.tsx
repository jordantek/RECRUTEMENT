import { useEffect } from "react";
import { Link } from "react-router-dom";
import PageTitle from "@/components/seo/pageTitle";
import { InfoTooltip } from "@/components/useful/info-tooltip";
import {
    Globe,
    DollarSign,
    Banknote,
    Shield,
    Building2,
    Mail,
    Settings,
    FileText,
    Network,
    Briefcase
} from "lucide-react";
import { routeHelpers } from "@/helpers/routeHelpers";
import usePageTitleStore from "@/contexts/usePageTitleStore";
import { motion } from "framer-motion";
import useCompanyStore from "@/contexts/CompanyContext.ts";

export const settingsHref = [
    {
        title: "Paramètres Généraux",
        description: "Configuration des bases du système : diplômes, TVA, secteurs d'activité et CSP",
        icon: Globe,
        to: routeHelpers.dashboard.settings.general,
        category: "configuration",
    },
    {
        title: "Structure d'Entreprise",
        description: "Gestion des départements, services, unités et partenariats",
        icon: Building2,
        to: "#",
        category: "organisation",
    },
    {
        title: "Configuration de Paie",
        description: "Paramétrage des rubriques salariales, bulletins et règles de calcul",
        icon: DollarSign,
        to: routeHelpers.dashboard.settings.payroll,
        category: "paie",
    },
    // {
    //     title: "Moyens de Paiement",
    //     description: "Gestion des modes de versement (virements, chèques...) et banques",
    //     icon: Banknote,
    //     to: "#",
    //     category: "paie",
    // },
    {
        title: "Protection Sociale",
        description: "Configuration des mutuelles, prévoyance et couvertures sociales",
        icon: Shield,
        to: "#",
        category: "social",
    },
   /* {
        title: "Sécurité & Accès",
        description: "Gestion des utilisateurs, permissions et politiques de sécurité",
        icon: ShieldCheck,
        to: "#",
        category: "sécurité",
    },
    {
        title: "Communication RH",
        description: "Modèles d'emails automatiques et envoi des documents aux collaborateurs",
        icon: Mail,
        to: "#",
        category: "communication",
    },
    {
        title: "Paramètres Avancés",
        description: "Options complémentaires et configurations spécifiques",
        icon: Star,
        to: "#",
        category: "advanced",
    },*/
];

export function SettingGeneralPage() {

    const { currentCompany } = useCompanyStore();

    useEffect(() => {
        usePageTitleStore.getState().setTitle(
            ` ${currentCompany ? `- ${currentCompany.name}` : "Paramètres système"}`,
            "Administrez les paramètres globaux et spécifiques"
        );
    }, [currentCompany]);

    return (
        <>
            <PageTitle title="Tableau de configuration" />
            <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8 space-y-8">
                {/* En-tête contextuel */}
                <div className="text-center space-y-2">
                    <motion.h1
                        className="text-3xl font-bold text-gray-900 flex items-center justify-center gap-3"
                        initial={{ opacity: 0, y: -10 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ duration: 0.4 }}
                    >
                        <Settings className="text-blue-600" size={32} />
                        {currentCompany ? (
                            <>Configuration · <Briefcase className="text-emerald-600" size={26} /> {currentCompany.name}</>
                        ) : (
                            "Paramètres système"
                        )}
                    </motion.h1>

                    <p className="mt-2 text-lg text-gray-600 max-w-3xl mx-auto">
                        {currentCompany
                            ? "Paramétrez les spécificités de cette société ou accédez aux réglages globaux"
                            : "Administrez l'ensemble de vos structures depuis le portail central"}
                    </p>

                    {currentCompany && (
                        <div className="pt-4">
                            <Link
                                to={routeHelpers.dashboard.settings.multiCompany}
                                className="inline-flex items-center text-sm text-blue-600 hover:text-blue-800 hover:underline"
                            >
                                <Network className="mr-2" size={16} />
                                Accéder à la gestion multi-entreprises
                            </Link>
                        </div>
                    )}
                </div>

                {/* Grille améliorée */}
                <motion.div
                    className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6"
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    transition={{ staggerChildren: 0.1 }}
                >
                    {settingsHref.map((item, index) => (
                        <motion.div
                            key={item.title}
                            initial={{ opacity: 0, y: 20 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ duration: 0.3, delay: index * 0.05 }}
                            whileHover={{ y: -5 }}
                        >
                            <InfoTooltip
                                title={item.title}
                                description={item.description + (item.badge ? ` (${item.badge})` : "")}
                                side="top"
                                align="center"
                            >
                                <Link
                                    to={item.to}
                                    className={`group h-full flex flex-col bg-white rounded-xl border overflow-hidden shadow-sm hover:shadow-md transition-all duration-300 ${
                                        item.badge === "Multi-org"
                                            ? "border-purple-200 hover:border-purple-300"
                                            : item.badge === "Global"
                                                ? "border-blue-200 hover:border-blue-300"
                                                : "border-gray-200 hover:border-gray-300"
                                    }`}
                                >
                                    <div className="p-5 flex flex-col items-center text-center h-full">
                                        <div className={`p-3 rounded-lg mb-4 ${
                                            item.badge === "Multi-org"
                                                ? "bg-purple-50 text-purple-600 group-hover:bg-purple-100"
                                                : item.badge === "Global"
                                                    ? "bg-blue-50 text-blue-600 group-hover:bg-blue-100"
                                                    : "bg-gray-50 text-gray-600 group-hover:bg-gray-100"
                                        }`}>
                                            <item.icon size={24} strokeWidth={1.75} />
                                        </div>
                                        <h3 className="text-lg font-semibold text-gray-800 group-hover:text-blue-700 transition-colors mb-2">
                                            {item.title}
                                        </h3>
                                        <p className="text-sm text-gray-500 mt-auto">
                                            {item.description}
                                        </p>
                                        {item.badge && (
                                            <span className={`mt-3 text-xs font-medium px-2 py-1 rounded-full ${
                                                item.badge === "Multi-org"
                                                    ? "bg-purple-100 text-purple-800"
                                                    : item.badge === "Global"
                                                        ? "bg-blue-100 text-blue-800"
                                                        : "bg-gray-100 text-gray-800"
                                            }`}>
                                                {item.badge}
                                            </span>
                                        )}
                                    </div>
                                </Link>
                            </InfoTooltip>
                        </motion.div>
                    ))}
                </motion.div>

                {/* Section d'aide contextuelle */}
                <div className={`mt-12 rounded-xl p-6 text-center border hidden ${
                    currentCompany
                        ? "bg-emerald-50 border-emerald-100"
                        : "bg-blue-50 border-blue-100"
                }`}>
                    <h3 className="text-lg font-medium mb-2">
                        {currentCompany
                            ? `Besoin d'aide pour configurer ${currentCompany.name} ?`
                            : "Assistance multi-entreprises"}
                    </h3>
                    <p className="mb-4">
                        {currentCompany
                            ? "Notre équipe peut vous guider dans la configuration spécifique de cette société"
                            : "Nos experts en gestion multi-entités sont à votre disposition"}
                    </p>
                    <div className="flex justify-center gap-4 ">
                        <button className="px-4 py-2 bg-white border text-sm rounded-lg hover:bg-gray-50 transition-colors flex items-center">
                            <FileText className="mr-2" size={16} />
                            Documentation
                        </button>
                        <button className="px-4 py-2 bg-blue-600 text-white text-sm rounded-lg hover:bg-blue-700 transition-colors flex items-center">
                            <Mail className="mr-2" size={16} />
                            Contactez le support
                        </button>
                    </div>
                </div>
            </div>
        </>
    );
}