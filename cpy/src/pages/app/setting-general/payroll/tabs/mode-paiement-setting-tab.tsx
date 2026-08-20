import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Search, CreditCard, ArrowDownUp } from "lucide-react";
import { useEffect, useState } from "react";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";
import { ModeDePaiementType } from "@/types/UtilsTypes";
import FormModal from "@/components/useful/form-modal.tsx";
import { Icon } from "@tabler/icons-react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import DeleteModal from "@/components/useful/delete-modal.tsx";

export default function ModePaiementSettingTab() {
    const { user } = useAuth();
    const [isLoading, setIsLoading] = useState(false);
    const [modes, setModes] = useState<ModeDePaiementType[]>([]);
    const [searchQuery, setSearchQuery] = useState<string>("");
    const [isOpenFormModal, setIsOpenFormModal] = useState(false);
    const [loadingSubmit, setLoadingSubmit] = useState(false);
    const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
    const [selectedMode, setSelectedMode] = useState<ModeDePaiementType | null>(null);
    const [isOpenDelete, setIsOpenDelete] = useState(false);

    const fetchData = async () => {
        try {
            setIsLoading(true);
            const res = await apiService.get({
                url: apiRoutes.admin.app.contrat.modeDePaiement.list
            }, {
                userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
                hasNoSuccessModal: true
            });
            setModes(res.data || []);
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: true });
            }
        } finally {
            setIsLoading(false);
        }
    };

    const modeFields = [
        {
            tag: "libelle",
            label: "Nom du mode de paiement",
            type: "text",
            placeholder: "Entrez le nom du mode de paiement",
            size: "col-span-12",
            required: true,
        },
        {
            tag: "description",
            label: "Description du mode de paiement",
            type: "text",
            placeholder: "Entrez la description du mode de paiement",
            size: "col-span-12",
            required: false,
        },
    ];

    const modeSchema = z.object({
        libelle: z.string().min(1, "Le nom du mode de paiement est requis"),
        description: z.string().optional(),
    });

    const form = useForm<z.infer<typeof modeSchema>>({
        resolver: zodResolver(modeSchema),
        defaultValues: {
            libelle: "",
            description: "",
        },
        mode: "onSubmit",
        shouldFocusError: true,
    });

    const { reset } = form;

    const onSubmit = async (data: z.infer<typeof modeSchema>) => {
        setLoadingSubmit(true);
        try {
            if (selectedMode) {
                // Mise à jour
                await apiService.put({
                    url: `${apiRoutes.admin.app.contrat.modeDePaiement.update}${selectedMode.id}`,
                    body: data
                }, {
                    userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
                    hasNoSuccessModal: false
                });
            } else {
                // Création
                await apiService.post({
                    url: apiRoutes.admin.app.contrat.modeDePaiement.create,
                    body: data
                }, {
                    userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
                    hasNoSuccessModal: false
                });
            }
            await fetchData();
            setIsOpenFormModal(false);
            reset();
            setSelectedMode(null);
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: false });
            }
        } finally {
            setLoadingSubmit(false);
        }
    };

    const handleEdit = (mode: ModeDePaiementType) => {
        setSelectedMode(mode);
        reset({
            libelle: mode.libelle,
            description: mode.description || ""
        });
        setIsOpenFormModal(true);
    };

    const handleDelete = (mode: ModeDePaiementType) => {
        setSelectedMode(mode);
        setIsOpenDelete(true);
    };

    const handleConfirmDelete = async () => {
        if (!selectedMode) return;
        
        try {
            setLoadingSubmit(true);
            await apiService.remove({
                url: `${apiRoutes.admin.app.contrat.modeDePaiement.delete}${selectedMode.id}`
            }, {
                userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
                hasNoSuccessModal: false
            });
            await fetchData();
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message);
            }
        } finally {
            setLoadingSubmit(false);
            setIsOpenDelete(false);
            setSelectedMode(null);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    const filteredModes = [...modes]
        .filter(b => b.libelle.toLowerCase().includes(searchQuery.toLowerCase()))
        .sort((a, b) => sortOrder === "asc" 
            ? a.libelle.localeCompare(b.libelle) 
            : b.libelle.localeCompare(a.libelle));

    const columns = [
        { 
            key: "libelle", 
            label: "Nom du mode",
            className: "font-medium"
        },
        { 
            key: "description", 
            label: "Description",
            className: "text-muted-foreground"
        },
    ];

    return (
        <div className="flex flex-col gap-4">
            <DeleteModal
                title={"⚠️ Êtes-vous sûr de vouloir supprimer ce mode de paiement ?"}
                description={`Le mode "${selectedMode?.libelle}" sera définitivement supprimé. Cette action est irréversible.`}
                isOpen={isOpenDelete}
                isetIsOpen={setIsOpenDelete}
                isDeleteLoading={loadingSubmit}
                onDelete={handleConfirmDelete}
                onCancel={() => {
                    setSelectedMode(null);
                    setIsOpenDelete(false);
                }}
            />

            <FormModal
                icon={CreditCard as Icon}
                title={selectedMode ? "Modifier le mode de paiement" : "Ajouter un mode de paiement"}
                description={selectedMode 
                    ? "Modifiez les informations du mode de paiement" 
                    : "Remplissez les informations pour ajouter un nouveau mode de paiement."}
                isOpen={isOpenFormModal}
                setIsOpen={setIsOpenFormModal}
                form={form}
                fields={modeFields}
                onSubmit={onSubmit}
                isSubmitLoading={loadingSubmit}
                onClose={() => {
                    reset();
                    setIsOpenFormModal(false);
                    setSelectedMode(null);
                }}
            />

            <div className="flex items-center justify-between bg-background mx-5">
                <div>
                    <h2 className="text-lg font-semibold">Modes de paiement</h2>
                    <p className="text-xs text-muted-foreground mb-4">
                        Gérez les modes de paiement disponibles dans le système.
                    </p>
                </div>
                <div className="flex gap-3">
                    <div className="relative">
                        <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
                        <Input
                            placeholder="Rechercher un mode..."
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            className="pl-8 w-[250px]"
                        />
                    </div>
                    <Button
                        variant="outline"
                        size="sm"
                        onClick={() => setSortOrder(prev => (prev === "asc" ? "desc" : "asc"))}
                    >
                        <ArrowDownUp className="h-4 w-4 mr-1" />
                        {sortOrder === "asc" ? "A → Z" : "Z → A"}
                    </Button>
                    <Button
                        variant="outline"
                        size="sm"
                        onClick={() => {
                            setSelectedMode(null);
                            setIsOpenFormModal(true);
                        }}
                    >
                        Ajouter un nouveau
                    </Button>
                </div>
            </div>

            <div className="m-5">
                <DynamicTable3
                    columns={columns}
                    data={filteredModes}
                    isLoading={isLoading}
                    onAdd={() => {
                        setSelectedMode(null);
                        setIsOpenFormModal(true);
                    }}
                    onEdit={handleEdit}
                    onDelete={handleDelete}
                    onFilter={(query: string) => setSearchQuery(query)}
                    filterPlaceholder="Rechercher un mode..."
                />
            </div>
        </div>
    );
}