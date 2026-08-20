import { EmployeeSelect } from "@/components/useful/EmployeeSelect";
import {Button} from "@/components/ui/button.tsx";
import {CheckCircle, CreditCard, Plus} from "lucide-react";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";
import apiService from "@/api/apiService.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import {useEffect, useState} from "react";
import FormModal from "@/components/useful/form-modal.tsx";
import {Icon} from "@tabler/icons-react";
import {useAuth} from "@/lib/auth.ts";
import useCompanyStore from "@/contexts/CompanyContext.ts";
import {DynamicTable3} from "@/components/tables/dynamic-table-3.tsx";
import DeleteModal from "@/components/useful/delete-modal.tsx";
import {EmployeeActifType} from "@/types/employee/EmployeeType.ts";
import useEmployeeStore from "@/contexts/useEmployeeStore.ts";
import { parse, isValid } from 'date-fns';
import {DateHelpers} from "@/helpers/DateHelpers.ts";
import {MensualiteType} from "@/types/MensualiteType.ts";
import ActionModal from "@/components/useful/action-modal.tsx";
import {UtilsHelpers} from "@/helpers/UtilsHelpers.ts";
import {StatusBadge} from "@/components/useful/StatusBadge.tsx";
import {toast} from "sonner";


type InstitutionType ={
    id:number,
    name:string,
}
export default function MensualiteTab() {
    const { user, logout } = useAuth();
    const { selectedCompany } = useCompanyStore();
    const { employees } = useEmployeeStore();
    const [isOpenForm, setIsOpenForm] = useState(false);
    const [isOpenDelete, setIsOpenDelete] = useState(false);
    const [isOpenAction, setIsOpenAction]=useState(false);
    const [mensualites, setMensualites] = useState<MensualiteType[]>([]);
    const [selected, setSelected] = useState<MensualiteType | null>(null);
    const [loadingSubmit, setLoadingSubmit] = useState(false);
    const [loadingPage, setLoadingPage] = useState(false);
    const [isFetchSetting, setIsFetchSetting] = useState(false);
    const [institutionOptions, setInstitutionOptions] = useState<InstitutionType[]>([]);

    const onFecthSetting = async ()=>{
        try {
            setIsFetchSetting(true);
            const response = await apiService.get(
                { url: apiRoutes.admin.app.institutionFinancier.list },
                { userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "", hasNoSuccessModal: true, onTokenExpired: logout, }
            );
            setInstitutionOptions(response.data || []);

        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: true });
            }
        } finally {
            setIsFetchSetting(false);
        }
    }

    const mensualiteFields = [
        {
            tag: "contratEmployeId",
            label: "Employé",
            input_type: "select",
            size: "col-span-6",
            placeholder: "",
            required: true,
            options:employees?.map((em: EmployeeActifType) => ({
                value: em.contratEmployeId?.toString() ?? '0',
                label: `${em.nom.toString()} ${em.prenom.toString()}`,
            }))
        },
        {
            tag: "institutionId",
            label: "Institution",
            input_type: "select",
            size: "col-span-6",
            required: true,
            options: institutionOptions?.map((ins:InstitutionType)=>({
                value: ins.id.toString(),
                label: ins.name,
            }))
        },
        {
            tag: "montantMensuel",
            label: "Montant",
            input_type:"number",
            size: "col-span-6",
            required: true
        },
        {
            tag:"dureeMensualite",
            label:"Durée",
            input_type:"number",
            size:"col-span-6",
            required:true
        },
        {
            tag: "moisDemarrage",
            label: "Date de début",
            input_type: "date2",
            size: "col-span-6",
            required: true
        },
        {
            tag: "moisFin",
            label: "Date de fin",
            input_type: "date2",
            size:"col-span-6",
            required: true
        },

    ];

    // Un regex pour vérifier rapidement le format JJ/MM/AAAA
    const dateRegex = /^(0[1-9]|[12][0-9]|3[01])\/(0[1-9]|1[0-2])\/\d{4}$/;

     const mensualiteSchema = z.object({
        // ... autres champs de votre formulaire
        contratEmployeId: z.string().min(1, "Veuillez sélectionner un employé."),
        institutionId: z.string().min(1, "Veuillez sélectionner une institution."),
        montantMensuel: z.coerce.number().positive("Le montant doit être positif."),

        moisDemarrage: z.string({ required_error: "La date de début est requise." })
            .refine(val => dateRegex.test(val), {
                message: "Le format de la date doit être JJ/MM/AAAA.",
            })
            .refine(val => isValid(parse(val, 'dd/MM/yyyy', new Date())), {
                message: "La date de début est invalide.",
            }),

        moisFin: z.string({ required_error: "La date de fin est requise." })
            .refine(val => dateRegex.test(val), {
                message: "Le format de la date doit être JJ/MM/AAAA.",
            })
            .refine(val => isValid(parse(val, 'dd/MM/yyyy', new Date())), {
                message: "La date de fin est invalide.",
            }),

        dureeMensualite: z.coerce.number().int().positive("La durée doit être un nombre positif."),

    }).refine(data => {
        if (!dateRegex.test(data.moisDemarrage) || !dateRegex.test(data.moisFin)) {
            return true; // La validation de champ s'en occupera
        }
        const startDate = parse(data.moisDemarrage, 'dd/MM/yyyy', new Date());
        const endDate = parse(data.moisFin, 'dd/MM/yyyy', new Date());
        return endDate >= startDate;
    }, {
        message: "La date de fin ne peut pas être antérieure à la date de début.",
        path: ["moisFin"],
    });

    type MensualiteFormValues = z.infer<typeof mensualiteSchema>;

    const formMensualite= useForm<MensualiteFormValues>({
        resolver: zodResolver(mensualiteSchema),
        defaultValues: {},
        shouldFocusError: true,
    });

    const resetForm= formMensualite.reset;

    const handleOnCreate = async (data: MensualiteFormValues) => {
        setLoadingSubmit(true);
        try {
            await apiService.post(
                {
                    url: apiRoutes.admin.app.mensualite.create,
                    body: JSON.stringify({
                        ...data,
                        moisDemarrage:DateHelpers.formatMoisAnnee(data.moisDemarrage),
                        moisFin:DateHelpers.formatMoisAnnee(data.moisFin),
                        companyId: selectedCompany?.id,
                        mensualiteSolde:0
                    }),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: false,
                    onTokenExpired: logout,
                }
            );
            resetForm();
            setMensualites([])
            OnFetchData()
            setIsOpenForm(false);

        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, {hasNoFailureModal: false});
            } else {
                console.log("Une erreur inconnue est survenue");
            }
        } finally {
            setLoadingSubmit(false);
        }
    };

    const handleOnEdit = async (data: MensualiteFormValues) => {
        setLoadingSubmit(true);
        try {
            await apiService.put(
                {
                    url:`${apiRoutes.admin.app.institutionFinancier.update}${selected?.id}`,
                    body: JSON.stringify(data),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: false,
                    onTokenExpired: logout,
                }
            );
            resetForm();
            setMensualites([])
            OnFetchData()
            setIsOpenForm(false);


        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, {hasNoFailureModal: false});
            } else {
                console.log("Une erreur inconnue est survenue");
            }
        } finally {
            setLoadingSubmit(false);
        }
    };

    const handleOnDelete = async () => {
        if (selected) {
            try {
                setLoadingSubmit(true);
                await apiService.remove(
                    {
                        url: `${apiRoutes.admin.app.mensualite.delete}/${selected.id}`,
                    },
                    {
                        userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                        hasNoSuccessModal: false,
                        onTokenExpired: logout,
                    }
                );
                setMensualites([])
                OnFetchData()
                setIsOpenDelete(false);
            } catch (error) {
                if (error instanceof Error) {
                    apiService.handleError(error.message, { hasNoFailureModal: false });
                }
            } finally {
                setLoadingSubmit(false);
            }
        }
    }

    const handleSoldeMensualite = async (mm: MensualiteType) => {
        if (!selected) return

        try {
            setLoadingSubmit(true)

            await apiService.put(
                {
                    url:`${ apiRoutes.admin.app.mensualite.solder}/${mm.id}`,
                    body: JSON.stringify({
                        mensualiteSolde:0
                    }),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: false,
                    onTokenExpired: logout,
                }
            );
            setMensualites([])
            OnFetchData()
            setIsOpenAction(false)
            setSelected(null)
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, {hasNoFailureModal: false});
            } else {
                console.log("Une erreur inconnue est survenue");
            }
        } finally {
            setLoadingSubmit(false)
        }
    }

    const OnFetchData = async () => {
        if (!selectedCompany?.id) return;

        setLoadingPage(true);
        try {
            const response = await apiService.get(
                {
                    url: `${apiRoutes.admin.app.mensualite.list_byCompany}${selectedCompany.id}`,
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: true,
                    onTokenExpired: logout,
                }
            );

            if (response.data) {
                const mensualite: MensualiteType[] = response.data;
                setMensualites(mensualite);
            }
        } catch (error) {
            console.error("Error fetching sanctions:", error);
        } finally {
            setLoadingPage(false);
        }
    };

    const handleDelete = (row: { id: number }) => {
        const inst = mensualites.find(s => s.id === row.id);
        if (inst) {
            setSelected(inst);
            setIsOpenDelete(true);
        }
    };

    useEffect(() => {
        onFecthSetting();
        OnFetchData().then(r => console.log(r))
    }, [selectedCompany?.id]);
    return (
        <>
            <div className="flex items-center justify-between mb-4">
                <div>
                    <h2 className="text-lg font-semibold">Mensualités</h2>
                    <p className="text-xs font-medium text-muted-foreground">
                        Visualisez, gérez et suivez les mensualités des employés
                    </p>
                </div>
                <EmployeeSelect/>
                <Button
                    size={"sm"}
                    className="flex items-center gap-2 rounded-md bg-primary text-primary-foreground hover:bg-primary/80"
                    onClick={() => {

                        setIsOpenForm(true);
                    }}
                >
                    <Plus className="h-4 w-4 "/>
                    Ajouter une mensualité
                </Button>

            </div>

            <div>
                <DynamicTable3
                    columns={[
                        {
                            key: "employe",
                            label: "Employé",
                            render: (value) =>
                                value ? (
                                    <div className="flex items-center gap-2">
                                        <p className="w-8 h-8 flex items-center justify-center rounded-full border text-sm font-medium bg-muted text-muted-foreground">
                                            {value.nom?.toString().toUpperCase().charAt(0) || '?'}
                                        </p>
                                        <span className="font-medium">{value.nom} {value.prenom}</span>
                                    </div>
                                ) : null,
                        },
                        {
                            key: "institution",
                            label: "Institution",
                            render: (value) =>
                                value ? (
                                    <div className="flex items-center gap-2">
                                        <span className="font-medium">{value.name}</span>
                                    </div>
                                ) : null,
                        },
                        {
                            key: "montantMensuel",
                            label: "Montant",
                            render: (value: string) => {
                                return UtilsHelpers.formatMontantWithSeparator(value, { currency: "F CFA", showCurrency: false })
                            },
                        },
                        {
                            key: "dureeMensualite",
                            label: "Durée",

                        },
                        {
                            key: "moisDemarrage",
                            label: "Mois de début",
                            render: (value: string) => {
                                return DateHelpers.formatMonthYearShort(value)
                            },
                        },
                        {
                            key: "moisFin",
                            label: "Mois de fin",
                            render: (value: string) => {
                                return DateHelpers.formatMonthYearShort(value)
                            },
                        },

                        {
                            key: "statut",
                            label: "Statut",
                            render: (value: string) => {
                                return (
                                    <StatusBadge status={value}/>
                                )
                            },
                        }

                    ]}
                    data={mensualites}
                    onAdd={() => setIsOpenForm(true)}
                    onDelete={(row) => {
                        if (row.statut?.toLowerCase() === "soldée") {
                            toast.error("Suppression impossible", {
                                description: "Cette mensualité a été soldée et ne peut pas être supprimée.",
                                position: "top-right",
                                duration: 5000,
                                style: {
                                    backgroundColor: "#fee2e2",        // bg-red-100
                                    border: "1px solid #f87171",       // red-400
                                    color: "#7f1d1d",                  // red-900
                                    boxShadow: "0 4px 12px rgba(0, 0, 0, 0.08)",
                                    padding: "16px 20px",
                                    borderRadius: "10px",
                                    fontSize: "0.875rem",
                                    fontWeight: 500,
                                },
                                icon: "🚫",
                            });
                            return;
                        }

                        handleDelete(row);
                    }}
                    onAction={(row) => {
                        return {
                            icon: <CheckCircle size={15} className="text-green-600" />,
                            onClick: () => {
                                if (row.statut?.toLowerCase() === "soldée") {
                                    toast.error("Action non autorisée", {
                                        description: "Cette mensualité est déjà soldée et ne peut plus être modifiée.",
                                        position: "top-right",
                                        duration: 5000,
                                        style: {
                                            backgroundColor: "#fee2e2",        // bg-red-100
                                            border: "1px solid #f87171",       // red-400
                                            color: "#7f1d1d",                  // red-900
                                            boxShadow: "0 4px 12px rgba(0, 0, 0, 0.08)",
                                            padding: "16px 20px",
                                            borderRadius: "10px",
                                            fontSize: "0.875rem",
                                            fontWeight: 500,
                                        },
                                        icon: "🚫",
                                    });
                                    return null;
                                }
                                setSelected(row);
                                setIsOpenAction(true);
                            },
                        };
                    }}
                    isLoading={loadingPage}
                    onRefresh={()=>{
                        OnFetchData().then(r => console.log(r))
                    }}

                />
            </div>

            <DeleteModal
                title={"⚠️ Etes-vous sur de vouloir supprimer ?"}
                description={`Cette action est irréversible. En cliquant sur supprimer, vous supprimerez cette mensualité.`}
                isOpen={isOpenDelete}
                isetIsOpen={setIsOpenDelete}
                isDeleteLoading={loadingSubmit}
                onDelete={handleOnDelete}
                onCancel={()=>{
                    selected && setSelected(null)
                    setIsOpenDelete(false)
                }}
            />
            <ActionModal
                title="✅ Confirmer la mensualité comme soldée ?"
                description="Cette action marquera définitivement la mensualité sélectionnée comme soldée. Souhaitez-vous continuer ?"
                isOpen={isOpenAction}
                isetIsOpen={setIsOpenAction}
                isLoading={loadingSubmit}
                onConfirm={() => {
                    if (selected) {
                        handleSoldeMensualite(selected)
                    }
                }}
                onCancel={() => {
                    selected && setSelected(null)
                    setIsOpenAction(false)
                }}
                confirmText="Confirmer la solde"
                confirmColor="green"
            />
            <FormModal
                size={"lg"}
                icon={CreditCard as Icon}
                title={selected? "Modifier la mensualité" : "Ajouter une mensualité"}
                description={selected ? "Modifiez les informations de la mensualité" : "Remplissez les informations ci-dessous pour ajouter une mensualité."}
                isOpen={isOpenForm}
                setIsOpen={setIsOpenForm}
                form={formMensualite}
                fields={mensualiteFields}
                loading={isFetchSetting}
                onSubmit={selected?handleOnEdit:handleOnCreate}
                isSubmitLoading={loadingSubmit}
                onClose={() => {
                    setSelected(null);
                    setLoadingSubmit(false);
                    resetForm();
                }}
            />
        </>
    );
}
