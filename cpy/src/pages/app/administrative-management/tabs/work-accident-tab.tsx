import { Button } from "@/components/ui/button.tsx";
import {UserPlusIcon,Car} from "lucide-react";
import {DynamicTable3} from "@/components/tables/dynamic-table-3.tsx";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";
import {AccidentForm, accidentSchema} from "@/validators-forms/accident-schema.ts";
import FormModal from "@/components/useful/form-modal.tsx";
import {Icon} from "@tabler/icons-react";
import {useEffect, useState} from "react";
import useEmployeeStore from "@/contexts/useEmployeeStore";
import {EmployeeActifType} from "@/types/employee/EmployeeType.ts";
import useCompanyStore from "@/contexts/CompanyContext.ts";
import apiService from "@/api/apiService.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import {DateHelpers} from "@/helpers/DateHelpers.ts";
import {useAuth} from "@/lib/auth.ts";
import { AccidentTravailType} from "@/types/AccidentTravail.ts";
import {DetailDialog} from "@/components/useful/detail-modal.tsx";
import DetailWorkAccident from "@/components/layout/administrative-manager/detail-work-accident.tsx";
import DeleteModal from "@/components/useful/delete-modal.tsx";
import {CalendarDate} from "@internationalized/date";

import {form} from "framer-motion/client";


export default function WorkAccidentTab() {
    const {user,logout}=useAuth()
    const { selectedCompany } = useCompanyStore();
    const { employees } = useEmployeeStore();
    const [accidents,setAccidents]=useState<AccidentTravailType[]>([])
    const [isAddAccidentModalOpen, setIsAddAccidentModalOpen] = useState(false);
    const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
    const [selectedAccident, setSelectedAccident] = useState<AccidentTravailType | null>(null);
    const [loadingSubmit,setLoadingSubmit]=useState(false)
    const [loadingPage, setLoadingPage] = useState(false);
    const [isOpenDelete, setIsOpenDelete] = useState(false);

    const OnFetchAccident = async () => {
        if (!selectedCompany?.id) return;

        setLoadingPage(true);
        try {
            const response = await apiService.get(
                {
                    url: `${apiRoutes.admin.app.accidentsTravail.list_byCompany}${selectedCompany.id}`,
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: true,
                    onTokenExpired: logout,
                }
            );

            if (response.data) {
                const accidentData: AccidentTravailType[] = response.data;
                setAccidents(accidentData);
            }
        } catch (error) {
            console.error("Error fetching sanctions:", error);
        } finally {
            setLoadingPage(false);
        }
    };
    const accidentFields = [
        {
            tag: "employeId",
            label: "Nom de l'employé",
            input_type: "select",
            size: "col-span-12",
            placeholder: "",
            required: true,
            options:employees?.map((em: EmployeeActifType) => ({
                value: em.employeId.toString() ?? '0',
                label: `${em.nom.toString()} ${em.prenom.toString()}`,
            }))
        },
        {
            tag: "dateAccident",
            label: "Date de l'accident",
            input_type: "date",
            size: "col-span-6",
            required: true
        },

        {
            tag: "dateDeclaration",
            label: "Date de déclaration à la CNSS",
            input_type: "date",
            size: "col-span-6",
            required: true
        },
        {
            tag: "depense",
            label: "Dépense (FCFA)",
            input_type: "number",
            size: "col-span-12 ",
            required: false,
            step: "0.01"
        },
        {
            tag: "effetAccident",
            label: "Effet de l'accident sur l'employé",
            input_type: "textarea",
            size: "col-span-12",
            required: true
        },

        {
            tag: "action",
            label: "Action",
            input_type: "textarea",
            size: "col-span-12",
            required: true
        },

    ];
    const handleViewAccident = (accident: AccidentTravailType) => {
        setSelectedAccident(accident);
        setIsDetailModalOpen(true);
    };

    function toCalendarDate(date: Date | string): CalendarDate {
        const d = typeof date === "string" ? new Date(date) : date;
        return new CalendarDate(d.getFullYear(), d.getMonth() + 1, d.getDate());
    }

    const handleEdit = (row: { id: number }) => {
        const accidents_ = accidents.find(s => s.id === row.id);
        console.log(accidents_)

       if (accidents_) {
        setSelectedAccident(accidents_);
            formAccident.reset({

            employeId: accidents_.employeId.toString(),
            dateAccident: toCalendarDate(accidents_.dateAccident),
            dateDeclaration: toCalendarDate(accidents_.dateDeclaration),
            effetAccident: accidents_.effetAccident,
            action: accidents_.action,
            depense: accidents_.depense,
            });

            setIsAddAccidentModalOpen(true);
        }
      };

    const handleDelete = (row: { id: number }) => {
        const accidents_ = accidents.find(s => s.id === row.id);
        console.log(accidents_)
        if (accidents_) {
            setSelectedAccident(accidents_);
            setIsOpenDelete(true);
        }
    };

    const formAccident = useForm<AccidentForm>({
        resolver: zodResolver(accidentSchema),
        defaultValues: {depense:0},
        shouldFocusError: true,
    });

    const resetAccident = formAccident.reset;

    const handleAddAccidentOnSubmit = async (data: z.infer<typeof accidentSchema>) => {
        setLoadingSubmit(true);
        try {
            const employee = employees.find((em) => em.employeId === Number(data.employeId));
            if (!employee || !selectedCompany?.id) {

            }
            await apiService.post(
                {
                    url: apiRoutes.admin.app.accidentsTravail.create,
                    body: JSON.stringify({
                        ...data,
                        contratEmployeId: employee?.contratEmployeId,
                        companyId: selectedCompany?.id,
                        dateAccident: DateHelpers.convertDateToISO(data.dateAccident),
                        dateDeclaration: DateHelpers.convertDateToISO(data.dateDeclaration),
                    }),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: false,
                    onTokenExpired: logout,
                }
            );

            resetAccident();
            setAccidents([]);
            OnFetchAccident();
            setIsAddAccidentModalOpen(false);

        } catch (error)
        {
            if (error instanceof Error) {
                apiService.handleError(error.message, {hasNoFailureModal: false});
            } else {
                console.log("Une erreur inconnue est survenue");
            }
        }finally {
            setLoadingSubmit(false);
        }
    };

    const handleOnUpdate = async (data:z.infer<typeof accidentSchema>) =>{
        setLoadingSubmit(true);
        const employee = employees.find((em) => em.employeId === Number(data.employeId));
        if (!employee || !selectedCompany?.id) {

        }
        try {

            await apiService.put(
                {
                    url:`${apiRoutes.admin.app.accidentsTravail.update}/${selectedAccident?.id}`,
                    body: JSON.stringify({
                        ...data,
                        contratEmployeId: employee?.contratEmployeId,
                        companyId: selectedCompany?.id,
                        dateAccident: DateHelpers.convertDateToISO(data.dateAccident),
                        dateDeclaration: DateHelpers.convertDateToISO(data.dateDeclaration),
                    }),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: false,
                    onTokenExpired: logout,
                }
            );

            resetAccident();
            setSelectedAccident(null)
            setAccidents([]);
            OnFetchAccident();
            setIsAddAccidentModalOpen(false);


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
                    url: `${apiRoutes.admin.app.accidentsTravail.delete}/${selectedAccident?.id}`,
                },
                {
                    userToken: `${user?.type} ${user?.token}`,
                    hasNoSuccessModal: false,
                }
            );
            selectedAccident && setSelectedAccident(null)
            resetAccident();
            setAccidents([]);
            OnFetchAccident();
            setIsOpenDelete(false);
        } catch (error) {
            console.error("Erreur lors de la suppression de la formation:", error);
        } finally {
            setLoadingSubmit(false);
        }
    }


    useEffect(() => {
        OnFetchAccident()
    }, [selectedCompany]);

    return (
        <div >
            <DetailDialog isOpen={isDetailModalOpen} setIsOpen={setIsDetailModalOpen}>
                <DetailWorkAccident workAccident={selectedAccident} />
            </DetailDialog>
            <DeleteModal
                title={"⚠️ Etes-vous sur de vouloir supprimer ?"}
                description={`Cette action est irréversible. En cliquant sur supprimer, vous supprimerez cet accident de travail.`}
                isOpen={isOpenDelete}
                isetIsOpen={setIsOpenDelete}
                isDeleteLoading={loadingSubmit}
                onDelete={handleOnDelete}
                onCancel={()=>{
                    selectedAccident && setSelectedAccident(null)
                    setIsOpenDelete(false)
                }}
            />
            <FormModal
                icon={Car as Icon}
                title= {selectedAccident
                    ? "Modifiez les informations de cet accident." : "Ajouter un accident de travail"}
                description={selectedAccident
                    ? "Remplissez les informations ci-dessous pour modifier un nouveau accident de travail."
                    : "Remplissez les informations ci-dessous pour déclarer un accident de travail."}
                isOpen={isAddAccidentModalOpen}
                setIsOpen={setIsAddAccidentModalOpen}
                loading={loadingSubmit}
                form={formAccident}
                fields={accidentFields}
                onSubmit={selectedAccident ? handleOnUpdate : handleAddAccidentOnSubmit}
                onSubmitSuccess={()=>{
                    setAccidents([]);
                    OnFetchAccident();
                }}
                size={"lg"}

                isSubmitLoading={loadingSubmit}
                onClose={() => {
                    resetAccident();
                    setIsAddAccidentModalOpen(false);
                }}
            />

            {/* Header */}
            <div className="flex items-center justify-between  px-4  mb-4">
                <div className={"text-left"}>
                    <h2 className="text-lg font-semibold text-black">Gestion des accidents de travail</h2>
                    <p className="text-xs text-muted-foreground">
                         Gérez les accidents de travail de votre entreprise
                    </p>
                </div>

                <div>
                    <Button
                        size={"sm"}
                        className="flex items-center gap-2 rounded-md bg-primary text-primary-foreground hover:bg-primary/80"
                        onClick={
                            () => {
                                setIsAddAccidentModalOpen(true)
                            }
                        }
                    >
                        <UserPlusIcon className="h-4 w-4" />
                        Ajouter un Accident
                    </Button>
                </div>
            </div>

            {/* Liste des contrats */}
            <div className="px-4">
                {/* À remplacer par votre tableau plus tard */}
                <div className="m-auto w-fullgap-2">
                    <DynamicTable3
                        columns={[
                            {
                                key: "employe",
                                label: "Agent",
                                render: (value) =>
                                    value ? (
                                        <div className="flex items-center gap-2">
                                        <p className="w-8 h-8 flex items-center justify-center rounded-full border text-sm font-medium bg-muted text-muted-foreground uppercase">
                                            {`${(value.nom?.[0] ?? "")}${(value.prenom?.[0] ?? "")}`}
                                        </p>
                                        <span className="font-medium">{value.nom} {value.prenom}</span>
                                        </div>
                                    ) : null,

                            },
                            { key: "dateAccident", label: "Date d'accident" },
                            { key: "dateDeclaration", label: "Date de Déclaration" },
                            { key: "effetAccident", label: "Effet" },
                            { key: "action", label: "Action" },
                            {
                                key: "depense",
                                label: "Dépense (FCFA)",
                                render: (row) =>
                                    typeof row=== "number"
                                        ? `${row.toLocaleString()} FCFA`
                                        : "—",
                            }
                        ]}
                        data={accidents}
                        onAdd={() => {}}
                        onEdit={
                            handleEdit
                        }
                        onDelete={handleDelete}
                        isLoading={loadingPage}
                        onRefresh={OnFetchAccident}
                        onView={
                            handleViewAccident
                        }
                    />
                </div>
            </div>
        </div>
    );
}