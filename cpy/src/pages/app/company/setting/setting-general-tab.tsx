import { useEffect, useState } from "react";
import { Banknote, CalendarClock, Cog, Landmark, Plus} from "lucide-react";
import { Button } from "@/components/ui/button.tsx";
import { CompanyType } from "@/types/company/CompanyType.ts";
import { DepartmentType, PosteType} from "@/types/UtilsTypes.ts";
import { useAuth } from "@/lib/auth.ts";
import apiService from "@/api/apiService.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import AddDepartementCompanyModal from "@/components/layout/settings/add-departement-company.tsx";
import AddPosteDepartementModal from "@/components/layout/settings/add-poste-departement-modal.tsx";
import OrganizationSelectWithSearch from "@/components/inputs/organization-select-with-search.tsx";


const companyBanks = [
    { id: 1, name: "Bank of Africa" },
    { id: 2, name: "Ecobank" },
    { id: 3, name: "UBA" },
    { id: 4, name: "NSIA Banque" },
    { id: 5, name: "Banque Atlantique" },
];
interface SettingGeneralTabProps {
    company: CompanyType;
}

export default function SettingGeneralTab({ company }: SettingGeneralTabProps) {
    const { user } = useAuth();
    const [loading, setLoading] = useState(true);
    const [departments, setDepartments] = useState<DepartmentType[]>([]);
    const [postes, setPostes] = useState<PosteType[]>([]);
    const [selectedDepartement, setSelectedDepartement] = useState<DepartmentType>();
    const [loadingPostes, setLoadingPostes] = useState(true);
    const [isAddDepartmentOpen, setIsAddDepartmentOpen] = useState(false);
    const [isAddPosteOpen, setIsAddPosteOpen] = useState(false);

    const handleOrganizationChange = (value: DepartmentType) => {
        setSelectedDepartement(value);
        fetchPost(value.id);
    };

    const handleAddNewOrganization = () => {
        setIsAddDepartmentOpen(true);
    };



    const fetchSettingList = async () => {
       console.log(loading)
        try {
            setLoading(true);
            const [departements] = await Promise.all([
                apiService.get({ url: `${apiRoutes.admin.app.company.departement.list_byCompany }`, params: { companyId: company.id }}, { userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "", hasNoSuccessModal: true }),
            ]);
            const fetchedDepartments = departements.data || [];
            setDepartments(fetchedDepartments);

            // Sélectionner le premier département si aucun n'est sélectionné
            if (!selectedDepartement && fetchedDepartments.length > 0) {
               // setSelectedDepartement(fetchedDepartments[0]);
                //fetchPost(fetchedDepartments[0].id);
            } else if (selectedDepartement) {
                const stillExists = fetchedDepartments.find(
                    (dep: DepartmentType) => dep.id === selectedDepartement.id
                );
                if (!stillExists) {
                    setSelectedDepartement(undefined);
                    setPostes([]);
                }
            }
        } catch (error) {
            console.error("Erreur de chargement des départements", error);
        } finally {
            setLoading(false);
        }
    };

    const fetchPost = async (departementId: number) => {
        try {
            setLoadingPostes(true);
            const response = await apiService.get(
                {
                    url: apiRoutes.admin.app.company.departement.poste.list_byCompany,
                    params: { departementId }
                },
                {
                    userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
                    hasNoSuccessModal: true
                }
            );
            setPostes(response.data || []);
        } catch (error) {
            console.error("Erreur de chargement des postes", error);
        } finally {
            setLoadingPostes(false);
        }
    };

    useEffect(() => {
        fetchSettingList();
    }, [company]);

    function Section({ title,
                         description,
                         icon,
                         children, // ⛳ Obligatoire ici
                     }: {
        title: string;
        description: string;
        icon: React.ReactNode;
        children: React.ReactNode;
    }) {
        return (
            <section className=" rounded-lg p-4 bg-white dark:bg-muted  space-y-4">
                <div className="flex items-center gap-2">
                    <div className="p-2 bg-muted rounded text-primary">{icon}</div>
                    <div>
                        <h2 className="text-sm font-semibold">{title}</h2>
                        <p className="text-xs text-muted-foreground">{description}</p>
                    </div>
                </div>
                <div>{children}</div>
            </section>
        );
    }
    return (
        <div className="px-4 mb-1">
            <div>
                <h2 className="text-lg font-semibold">Page de paramétrage de l'entreprise</h2>
                <p className="text-xs text-muted-foreground">
                    Vous pouvez configurer les informations de l'entreprise ainsi que ses paramètres associés.
                </p>
            </div>
            {/* Département & Postes */}
            <Section
                icon={<Cog className="w-4 h-4"/>}
                title="Départements & Postes"
                description="Configurez les départements de l’entreprise et leurs postes."
            >
                <div className="grid grid-cols-1 md:grid-cols-2 gap-2 shadow-none">
                    {/* Section départements */}
                    <div className="bg-white dark:bg-muted rounded-lg border p-4 shadow-none flex flex-col gap-4">
                        <div className="flex items-center justify-between border-b  pb-2">
                            <h3 className="text-sm font-medium">Départements</h3>
                            <Button
                                size="sm"
                                onClick={handleAddNewOrganization}
                            >
                                Ajouter un Département
                            </Button>
                        </div>
                        <OrganizationSelectWithSearch
                            organizations={departments.map(dep => ({
                                value: dep.id.toString(),
                                label: dep.libelle,
                            }))}
                            value={selectedDepartement?.id?.toString() ?? ""}
                            onChange={(value: string) => {
                                const dep = departments.find(d => d.id.toString() === value);
                                if (dep) {
                                    setPostes([]);
                                    handleOrganizationChange(dep);
                                }
                            }}
                            onAddNewOrganization={handleAddNewOrganization}
                            label="Sélectionner un département"
                        />
                    </div>

                    {/* Section postes */}
                    <div className="bg-white dark:bg-muted rounded-lg border p-4 shadow-sm flex flex-col gap-4">
                        <div className="flex items-center justify-between border-b pb-2">
                            <h3 className="text-sm font-medium">Postes du département</h3>
                            <Button
                                size="sm"
                                onClick={() => setIsAddPosteOpen(true)}
                                disabled={!selectedDepartement}
                            >
                                Ajouter un poste
                            </Button>
                        </div>

                        <div className="space-y-2 overflow-y-auto max-h-40 pr-2">
                            {loadingPostes ? (
                                <div className="space-y-2">
                                    {[...Array(3)].map((_, i) => (
                                        <div key={i} className="w-1/2 h-4 bg-gray-200 rounded animate-pulse"/>
                                    ))}
                                </div>
                            ) : postes.length > 0 ? (
                                <div className="flex flex-wrap gap-2">
                                    {postes.map(poste => (
                                        <span
                                            key={poste.id}
                                            className="text-xs bg-muted px-2 py-1 rounded-full"
                                        >
                                        {poste.libelle}
                                      </span>
                                    ))}
                                </div>
                            ) : (
                                <p className="text-muted-foreground text-xs italic">
                                    Aucun poste pour ce département.
                                </p>
                            )}
                        </div>
                    </div>
                </div>
            </Section>

            {/* Banque */}
            <div className={"hidden"}>
                <Section
                    icon={<Landmark className="w-4 h-4"/>}
                    title="Banques"
                    description="Liste des banques associées à l’entreprise pour les virements."
                >
                    <div className="space-y-2">

                        {companyBanks.length > 0 ? (
                            <ul className="list-disc pl-5 text-sm text-muted-foreground">
                                {companyBanks.map((bank) => (
                                    <li key={bank.id} className="text-foreground">
                                        {bank.name}
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p className="text-sm text-muted-foreground italic">Aucune banque associée.</p>
                        )}

                        <div className="pt-2">
                            <Button size="sm">
                                Associer une banque
                            </Button>
                        </div>
                    </div>
                </Section>

            </div>

            {/* Type de contrat */}
            {/* <Section
                icon={<BadgeCheck className="w-4 h-4"/>}
                title="Types de contrat"
                description="Définissez les différents types de contrat disponibles (CDI, CDD, Stage, etc.)."
            >
                <div className="flex items-center justify-between">
                    <p className="text-sm">Activer les contrats de stage</p>
                    <Switch/>
                </div>
                <div className="flex items-center justify-between">
                    <p className="text-sm">Activer les contrats de stage</p>
                    <Switch/>
                </div>
                <div className="flex items-center justify-between">
                    <p className="text-sm">Activer les contrats de stage</p>
                    <Switch/>
                </div>
            </Section>*/}

            {/* Mode de paiement */}
            <div className={"hidden"}>
                <Section
                    icon={<Banknote className="w-4 h-4"/>}
                    title="Modes de paiement"
                    description="Spécifiez les moyens de paiement acceptés (virement, mobile money, espèces)."
                >
                    <div className="flex flex-wrap gap-2">
                        {["Virement bancaire", "Mobile Money", "Espèces"].map((mode, i) => (
                            <span key={i} className="text-xs bg-muted px-3 py-1 rounded-full">
              {mode}
            </span>
                        ))}
                        <Button size="sm" variant="outline">
                            <Plus className="w-4 h-4 mr-1"/>
                            Ajouter un mode
                        </Button>
                    </div>
                </Section>
            </div>


            {/* Avantages & primes */}

            <div className={"hidden"}>
                <Section
                    icon={<Banknote className="w-4 h-4"/>}
                    title="Primes & Avantages"
                    description="Ajoutez des primes fixes ou variables, indemnités ou bonus."

                >
                    <Button variant="outline" size="sm">
                        <Plus className="w-4 h-4 mr-1"/>
                        Ajouter une prime
                    </Button>
                </Section>
            </div>


            {/* Congés & absences */}
            <div className={"hidden"}>
                <Section
                    icon={<CalendarClock className="w-4 h-4"/>}
                    title="Congés & Absences"
                    description="Définissez les règles de congés, jours fériés, RTT, etc."
                >
                    <p className="text-sm">Nombre de jours de congés annuels : <strong>25</strong></p>
                </Section>
            </div>

            {/* Modale : Ajout département */}
            <AddDepartementCompanyModal
                isOpen={isAddDepartmentOpen}
                setIsOpen={setIsAddDepartmentOpen}
                companyId={company.id}
                refresh={fetchSettingList}
            />

            {/* Modale : Ajout poste */}
            {selectedDepartement && (
                <AddPosteDepartementModal
                    isOpen={isAddPosteOpen}
                    setIsOpen={setIsAddPosteOpen}
                    departementId={selectedDepartement.id}
                    refresh={() => fetchPost(selectedDepartement.id)}
                />
            )}
        </div>
    );
}
