import { Button } from "@/components/ui/button.tsx";
import { UserPlusIcon, Gavel } from "lucide-react";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3.tsx";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import FormModal from "@/components/useful/form-modal.tsx";
import { Icon } from "@tabler/icons-react";
import { useEffect, useState } from "react";
import useEmployeeStore from "@/contexts/useEmployeeStore.ts";
import { EmployeeActifType } from "@/types/employee/EmployeeType.ts";
import { SanctionFormValues, sanctionSchema } from "@/validators-forms/sanctionSchema.ts";
import apiService from "@/api/apiService.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import { useAuth } from "@/lib/auth.ts";
import useCompanyStore from "@/contexts/CompanyContext.ts";
import { DateHelpers } from "@/helpers/DateHelpers.ts";
import { SanctionType } from "@/types/SanctionType.ts";
import { DetailDialog } from "@/components/useful/detail-modal";
import DetailSanction from "@/components/layout/administrative-manager/DetailSanction.tsx";
import DeleteModal from "@/components/useful/delete-modal.tsx";
import {CalendarDate} from "@internationalized/date";
import { form } from "framer-motion/client";

export default function SanctionTab() {
    const { user, logout } = useAuth();
    const { selectedCompany } = useCompanyStore();
    const { employees } = useEmployeeStore();
    const [isOpenForm, setIsOpenForm] = useState(false);
    const [sanctions, setSanctions] = useState<SanctionType[]>([]);
    const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
    const [filteredData, setFilteredData] = useState<SanctionType[]>([]);
    const [selectedSanction, setSelectedSanction] = useState<SanctionType | null>(null);
    const [loadingSubmit, setLoadingSubmit] = useState(false);
    const [loadingPage, setLoadingPage] = useState(false);
    const [isOpenDelete, setIsOpenDelete] = useState(false);

    const sanctionFields = [
        {
            tag: "employeId",
            label: "Employé",
            input_type: "select",
            size: "col-span-12",
            placeholder: "",
            required: true,
            options: employees?.map((em: EmployeeActifType) => ({
                value: em.employeId.toString() ?? '0',
                label: `${em.nom.toString()} ${em.prenom.toString()}`,
            }))
        },
        {
            tag: "datePlainte",
            label: "Date de Plainte",
            input_type: "date",
            size: "col-span-6",
            placeholder: "",
            required: true
        },
        {
            tag: "contenuePlainte",
            label: "Plainte",
            input_type: "textarea",
            size: "col-span-12",
            placeholder: "",
            required: true
        },
        {
            tag: "dateDemandeExplication",
            label: "Date de DE",
            input_type: "date",
            size: "col-span-6",
            placeholder: "",
            required: true
        },
        {
            tag: "dateReponse",
            label: "Date de reponse",
            input_type: "date",
            size: "col-span-6",
            placeholder: "",
            required: true
        },
        {
            tag: "sanctionDonnee",
            label: "Sanction",
            input_type: "textarea",
            size: "col-span-6",
            placeholder: "",
            required: true
        },
        {
            tag: "observation",
            label: "Observation",
            input_type: "textarea",
            size: "col-span-6",
            placeholder: "",
            required: false
        },
    ];

    const formSanction = useForm<SanctionFormValues>({
        resolver: zodResolver(sanctionSchema),
        defaultValues: {},
        shouldFocusError: true,
    });

    const resetSanctionForm = formSanction.reset;

    const handleOnCreate = async (data: z.infer<typeof sanctionSchema>) => {
        setLoadingSubmit(true);
        try {
            const employee = employees.find((em) => em.employeId === Number(data.employeId));
            if (!employee || !selectedCompany?.id) {
                throw new Error("Employee or company not found");
            }
             await apiService.post(
                {
                    url: apiRoutes.admin.app.sanction.create,
                    body: JSON.stringify({
                        ...data,
                        contratEmployeId: employee.contratEmployeId,
                        companyId: selectedCompany.id,
                        datePlainte: DateHelpers.convertDateToISO(data.datePlainte),
                        dateDemandeExplication: DateHelpers.convertDateToISO(data.dateDemandeExplication),
                        dateReponse: DateHelpers.convertDateToISO(data.dateReponse),
                    }),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: false,
                    onTokenExpired: logout,
                }
            );

            resetSanctionForm();
            setIsOpenForm(false);
            await OnFetchSanction();

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

    const handleOnUpdate = async (data:z.infer<typeof sanctionSchema>) =>{
        setLoadingSubmit(true);
        const employee = employees.find((em) => em.employeId === Number(data.employeId));
        if (!employee || !selectedCompany?.id) {
            throw new Error("Employee or company not found");
        }
        try {
            await apiService.put(
                {
                    url: `${apiRoutes.admin.app.sanction.update}/${selectedSanction?.id}`,
                    body: JSON.stringify({
                        ...data,
                        contratEmployeId: employee.contratEmployeId,
                        companyId: selectedCompany.id,
                        datePlainte: DateHelpers.convertDateToISO(data.datePlainte),
                        dateDemandeExplication: DateHelpers.convertDateToISO(data.dateDemandeExplication),
                        dateReponse: DateHelpers.convertDateToISO(data.dateReponse),
                    }),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: false,
                    onTokenExpired: logout,
                }
            );
            resetSanctionForm();
            setSelectedSanction(null)
            await OnFetchSanction();
            setIsOpenForm(false);
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { form });
            }
        }
        finally {
            setLoadingSubmit(false);
        }
    }

    const handleOnDelete = async () => {
        try {
            setLoadingSubmit(true);
            await apiService.remove(
                {
                    url: `${apiRoutes.admin.app.sanction.delete}/${selectedSanction?.id}`,
                },
                {
                    userToken: `${user?.type} ${user?.token}`,
                    hasNoSuccessModal: false,
                }
            );
            selectedSanction && setSelectedSanction(null)
            setSanctions([]);
            OnFetchSanction();
            setIsOpenDelete(false);
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message);
            }
        } finally {
            setLoadingSubmit(false);
        }
    }

    const handleDelete = (row: { id: number }) => {
        const sanction = sanctions.find(s => s.id === row.id);
        if (sanction) {
            setSelectedSanction(sanction);
            setIsOpenDelete(true);
        }
    };

    function toCalendarDate(date: Date | string): CalendarDate {
        const d = typeof date === "string" ? new Date(date) : date;
        return new CalendarDate(d.getFullYear(), d.getMonth() + 1, d.getDate());
    }

    const handleEdit = (row: { id: number }) => {
        const sanction = sanctions.find(s => s.id === row.id);
       if (sanction) {
            setSelectedSanction(sanction);
            formSanction.reset({
                employeId: sanction.employeId.toString(),
                datePlainte: toCalendarDate(sanction.datePlainte),
                contenuePlainte: sanction.contenuePlainte,
                dateDemandeExplication: toCalendarDate(sanction.dateDemandeExplication),
                dateReponse: toCalendarDate(sanction.dateReponse),
                sanctionDonnee: sanction.sanctionDonnee,
                observation: sanction.observation,
            });
            setIsOpenForm(true);
        }
    };

    const handleView = (sanction: SanctionType) => {
        setSelectedSanction(sanction);
        setIsDetailModalOpen(true);
    }

    const OnFetchSanction = async () => {
        if (!selectedCompany?.id) return;

        setLoadingPage(true);
        try {
            const response = await apiService.get(
                {
                    url: `${apiRoutes.admin.app.sanction.list_byCompany}${selectedCompany.id}`,
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: true,
                    onTokenExpired: logout,
                }
            );

            if (response.data) {
                const sanctionsData: SanctionType[] = response.data;
                setSanctions(sanctionsData);
                setFilteredData(sanctionsData);
            }
        } catch (error) {
            console.error("Error fetching sanctions:", error);
        } finally {
            setLoadingPage(false);
        }
    };

    const handleFilter = (query: string) => {
        const lowerQuery = query.trim().toLowerCase();
        if (!lowerQuery) {
            setFilteredData(sanctions);
            return;
        }
        const result = sanctions.filter((sanction) =>
            sanction.employe.nom?.toLowerCase().includes(lowerQuery) ||
            sanction.employe.prenom?.toLowerCase().includes(lowerQuery)
        );
        setFilteredData(result);
    };

    useEffect(() => {
        if (selectedCompany?.id) {
            OnFetchSanction();
        }
    }, [selectedCompany?.id]);

    return (
        <div className="px-4">
            <DetailDialog
                isOpen={isDetailModalOpen}
                setIsOpen={setIsDetailModalOpen}
                title="Détail de la sanction"
                description="Informations sur la sanction"
                size="lg"
            >
                <DetailSanction sanction={selectedSanction} />
            </DetailDialog>
            
            <DeleteModal
                title="⚠️ Confirmation de suppression"
                description="Cette action est irréversible. Voulez-vous vraiment supprimer cette sanction ?"
                isOpen={isOpenDelete}
                isetIsOpen={setIsOpenDelete}
                isDeleteLoading={loadingSubmit}
                onDelete={handleOnDelete}
                onCancel={()=>{
                    selectedSanction && setSelectedSanction(null)
                    setIsOpenDelete(false)
                }}
            />
            
            <FormModal
                icon={Gavel as Icon}
                title={selectedSanction ? "Modifier la sanction" : "Ajouter une sanction"}
                description={
                    selectedSanction 
                        ? "Remplissez les informations ci-dessous pour modifier la sanction."
                        : "Remplissez les informations ci-dessous pour déclarer une nouvelle sanction."
                }
                isOpen={isOpenForm}
                setIsOpen={setIsOpenForm}
                loading={loadingPage}
                form={formSanction}
                fields={sanctionFields}
                onSubmit={selectedSanction ? handleOnUpdate : handleOnCreate}
                onSubmitSuccess={() => {
                    OnFetchSanction();
                }}
                size="lg"
                isSubmitLoading={loadingSubmit}
                onClose={() => {
                    setSelectedSanction(null);
                    setLoadingSubmit(false);
                    formSanction.reset()
                }}
            />

            <div className="flex items-center justify-between mb-4">
                <div className={"text-left"}>
                    <h2 className="text-lg font-semibold text-black">Gestion des sanctions</h2>
                    <p className="text-xs text-muted-foreground">
                        Gérez les sanctions de votre entreprise
                    </p>
                </div>
                <div>
                    <Button
                        size={"sm"}
                        className="flex items-center gap-2 rounded-md bg-primary text-primary-foreground hover:bg-primary/80"
                        onClick={() => {
                            setSelectedSanction(null);
                            setIsOpenForm(true);
                        }}
                    >
                        <UserPlusIcon className="h-4 w-4 mr-2"/>
                        Ajouter une sanction
                    </Button>
                </div>
            </div>

            <div className="m-auto w-full gap-2">
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
                        { key: "datePlainte", label: "Date de P." },
                        { key: "contenuePlainte", label: "Plainte" },
                        { key: "dateDemandeExplication", label: "Date de DE." },
                        { key: "dateReponse", label: "Date de R." },
                        { key: "sanctionDonnee", label: "Sanction" },
                    ]}
                    data={filteredData}
                    onAdd={() => setIsOpenForm(true)}
                    onEdit={handleEdit}
                    onDelete={handleDelete}
                    onFilter={handleFilter}
                    filterPlaceholder={"Rechercher par nom d'employé..."}
                    isLoading={loadingPage}
                    onView={handleView}
                />
            </div>
        </div>
    );
}