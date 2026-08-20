import { Button } from "@/components/ui/button.tsx";
import { useEffect, useState } from "react";
import { useAuth } from "@/lib/auth.ts";
import { Link } from "react-router-dom";
import { routeHelpers } from "@/helpers/routeHelpers.ts";
import {UserPlusIcon, FileText, CheckCircle, Clock} from "lucide-react";
import { ContratEmploye } from "@/types/ContratType.ts";
import apiService from "@/api/apiService.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3.tsx";
import { DetailDialog } from "@/components/useful/detail-modal.tsx";
import DetailContrat from "@/components/layout/company/detail-contrat.tsx";
import StatCard from "@/components/useful/StatCard.tsx";

interface ContratListTabsProps {
    company?: { id: number; name: string };
}

export default function ContratListTabs({ company }: ContratListTabsProps) {
    const { user } = useAuth();
    const [contrats, setContrats] = useState<ContratEmploye[]>([]);
    const [filteredContrats, setFilteredContrats] = useState<ContratEmploye[]>([]);
    const [loadingContrat, setLoadingContrat] = useState(false);
    const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
    const [selectedContrat, setSelectedContrat] = useState<ContratEmploye | null>(null);

    const [stats, setStats] = useState({
        total: 0,
        enCours: 0,
        termines: 0,
    });

    const handleView = (contrat: ContratEmploye) => {
        setSelectedContrat(contrat);
        setIsDetailModalOpen(true);
    };

    const handleFilter = (search: string) => {
        if (!search) {
            setFilteredContrats(contrats);
            return;
        }
        const lowerSearch = search.toLowerCase();
        const filtered = contrats.filter((contrat) => {
            const fullName = `${contrat.employe?.nom ?? ''} ${contrat.employe?.prenom ?? ''}`.toLowerCase();
            const departement = contrat.departement?.libelle?.toLowerCase() ?? '';
            const poste = contrat.poste?.libelle?.toLowerCase() ?? '';
            const mouvement = contrat.mouvementContrat?.toLowerCase() ?? '';
            return (
                fullName.includes(lowerSearch) ||
                departement.includes(lowerSearch) ||
                poste.includes(lowerSearch) ||
                mouvement.includes(lowerSearch)
            );
        });
        setFilteredContrats(filtered);
    };

    const fetchData = async () => {
        try {
            setLoadingContrat(true);
            const response = await apiService.get(
                { url: `${apiRoutes.admin.app.contrat.list_byCompany}/${company?.id}` },
                {
                    userToken: `${user?.type ?? ''} ${user?.token ?? ''}`,
                    hasNoSuccessModal: true,
                }
            );
            setContrats(response.data);
            setFilteredContrats(response.data); // Initialise aussi les données filtrées
        } catch (error) {
            apiService.handleError(error instanceof Error ? error.message : "Erreur inconnue");
        } finally {
            setLoadingContrat(false);
        }
    };

    const fetchStats = async () => {
        // Remplace cette logique par ton propre fetch
        setStats({
            total: 15,
            enCours: 12,
            termines: 3,
        });
    };

    useEffect(() => {
        fetchStats();
        fetchData();
    }, [company]);

    return (
        <div className="flex flex-col gap-4">
            <DetailDialog
                isOpen={isDetailModalOpen}
                setIsOpen={setIsDetailModalOpen}
                size={"xl"}
                title={"Détails du contrat"}
                description={"Informations sur le contrat"}
            >
                <DetailContrat contrat={selectedContrat} />
            </DetailDialog>

            <div className="flex items-center justify-between bg-background px-4 rounded-md">
                <div>
                    <h2 className="text-lg font-semibold">Liste des contrats</h2>
                    <p className="text-xs text-muted-foreground">
                        Vous pouvez créer, modifier ou supprimer des contrats pour les employés de cette entreprise.
                    </p>
                </div>
                <Link to={routeHelpers.dashboard.company.contract.create(company?.id ?? 0, company?.name ?? "")}>
                    <Button
                        size={"sm"}
                        className="flex items-center"
                    >
                        <UserPlusIcon className="h-4 w-4 mr-2" />
                        Nouveau contrat
                    </Button>
                </Link>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 px-4">

                    <StatCard
                        icon={FileText}
                        title="Contrats totaux"
                        value={stats.total}
                        bgColor="bg-gray-100"
                        textColor="text-gray-700"
                    />
                    <StatCard
                        icon={Clock}
                        title="En cours"
                        value={stats.enCours}
                        bgColor="bg-yellow-100"
                        textColor="text-yellow-600"
                    />
                    <StatCard
                        icon={CheckCircle}
                        title="Terminés"
                        value={stats.termines}
                        bgColor="bg-green-100"
                        textColor="text-green-600"
                    />

            </div>

            <div className="px-4">
                <DynamicTable3<ContratEmploye>
                    columns={[
                        {
                            key: "employe",
                            label: "Employé",
                            render: (value) =>
                                value ? (
                                    <div className="flex items-center gap-2">
                                        <p className="w-8 h-8 flex items-center justify-center rounded-full border text-sm font-medium bg-muted text-muted-foreground">
                                            {value.nom?.toString().toUpperCase().charAt(0) || "?"}
                                        </p>
                                        <span className="font-medium">{value.nom} {value.prenom}</span>
                                    </div>
                                ) : null,
                        },
                        { key: "departement.libelle", label: "Département" },
                        { key: "poste.libelle", label: "Poste" },
                        { key: "mouvementContrat", label: "Mouvement" },
                        { key: "dateDebut", label: "Date début" },
                        { key: "dateFin", label: "Date fin" },
                    ]}
                    isLoading={loadingContrat}
                    data={filteredContrats}
                    onView={handleView}
                    onFilter={handleFilter}
                    onDownload={() => {}}
                    onRefresh={fetchData}
                />
            </div>
        </div>
    );
}
