import { Button } from "@/components/ui/button.tsx";
import {CalendarIcon, UsersIcon} from "lucide-react";
import {DynamicTable3} from "@/components/tables/dynamic-table-3.tsx";

import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";

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

import {EvenementSocialForm, evenementSocialSchema} from "@/validators-forms/evenementSocialSchema.ts";
import {EvenementSocialType} from "@/types/EvenementSocialType.ts";
import {DetailDialog} from "@/components/useful/detail-modal.tsx";
import DetailEvenSocial from "@/components/layout/administrative-manager/detail-even-social.tsx";
import DeleteModal from "@/components/useful/delete-modal.tsx";
import {CalendarDate} from "@internationalized/date";
import {form} from "framer-motion/client";

export default function SocialEventTab() {
  const {user, logout} = useAuth()
  const { selectedCompany } = useCompanyStore();
  const { employees } = useEmployeeStore();
  const [evenements, setEvenements] = useState<EvenementSocialType[]>([])
  const [selectedEvent, setSelectedEvent] = useState<EvenementSocialType | null>(null);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [isAddEventModalOpen, setIsAddEventModalOpen] = useState(false);
  const [loadingSubmit, setLoadingSubmit] = useState(false)
  const [loadingPage, setLoadingPage] = useState(false);
    const [isOpenDelete, setIsOpenDelete] = useState(false);

  const eventFields = [
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
      })),
        readonly:false,
    },
    {
      tag: "dateEvenement",
      label: "Date de l'événement",
      input_type: "date",
      size: "col-span-6",
      required: true
    },
    {
      tag: "designation",
      label: "Désignation",
      input_type: "text",
      size: "col-span-6",
      required: true
    },
    {
      tag: "montant",
      label: "Montant (FCFA)",
      input_type: "number",
      size: "col-span-12",
      required: false,
      step: "0.01"
    },
    {
      tag: "actionMenee",
      label: "Action menée",
      input_type: "textarea",
      size: "col-span-12",
      required: false
    },

    {
      tag: "observation",
      label: "Observations",
      input_type: "textarea",
      size: "col-span-12",
      required: false
    },
  ];



  const formEvent = useForm<EvenementSocialForm>({
    resolver: zodResolver(evenementSocialSchema),
    defaultValues: {},
    shouldFocusError: true,
  });

  const resetEvent = formEvent.reset;

  const handleAddEventOnSubmit = async (data: z.infer<typeof evenementSocialSchema>) => {
    setLoadingSubmit(true);
    try {
      const employee = employees.find((em) => em.employeId === Number(data.employeId));

      if (!employee || !selectedCompany?.id) {
        throw new Error("Employee or company not found");
      }

      await apiService.post(
          {
            url: apiRoutes.admin.app.evenementsSociaux.create,
            body: JSON.stringify({
              ...data,
              contratEmployeId: employee.contratEmployeId,
              companyId: selectedCompany.id,
              dateEvenement: DateHelpers.convertDateToISO(data.dateEvenement),
            }),
            headers: { "Content-Type": "application/json" },
          },
          {
            userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
            hasNoSuccessModal: false,
            onTokenExpired: logout,
          }
      );

      resetEvent();
      setIsAddEventModalOpen(false);
      OnFetchEvents();

    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, {hasNoFailureModal: true});
      } else {
        console.log("Une erreur inconnue est survenue");
      }
    } finally {
      setLoadingSubmit(false);
    }
  };

    const handleOnUpdate = async (data:z.infer<typeof evenementSocialSchema>) =>{
        setLoadingSubmit(true);
        const employee = employees.find((em) => em.employeId === Number(data.employeId));
        if (!employee || !selectedCompany?.id) {

        }
        try {

            await apiService.put(
                {
                    url: `${apiRoutes.admin.app.evenementsSociaux.update}/${selectedEvent?.id}`,
                    body: JSON.stringify({
                        ...data,
                        contratEmployeId: employee?.contratEmployeId,
                        companyId: selectedCompany?.id,
                        dateEvenement: DateHelpers.convertDateToISO(data.dateEvenement),
                    }),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: false,
                    onTokenExpired: logout,
                }
            );

            resetEvent();
            setIsAddEventModalOpen(false);
            OnFetchEvents();
            setSelectedEvent(null)

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
                    url: `${apiRoutes.admin.app.evenementsSociaux.delete}/${selectedEvent?.id}`,
                },
                {
                    userToken: `${user?.type} ${user?.token}`,
                    hasNoSuccessModal: false,
                }
            );
            selectedEvent && setSelectedEvent(null)

            setEvenements([]);
            OnFetchEvents();
            setIsOpenDelete(false);
        } catch (error) {
            console.error("Erreur lors de la suppression de la formation:", error);
        } finally {
            setLoadingSubmit(false);
        }
    }

  function toCalendarDate(date: Date | string): CalendarDate {
    const d = typeof date === "string" ? new Date(date) : date;
    return new CalendarDate(d.getFullYear(), d.getMonth() + 1, d.getDate());
}

  const handleEdit = (row: { id: number }) => {
     // eventFields[0].readonly = true;
    const events_ = evenements.find(s => s.id === row.id);
    console.log(events_)

   if (events_) {
    setSelectedEvent(events_);

        formEvent.reset({employeId: events_.employeId.toString(),
        designation: events_.designation,
        dateEvenement: toCalendarDate(events_.dateEvenement),
        actionMenee: events_.actionMenee,
        montant: events_.montant,
        observation: events_.observation,
        });
        setIsAddEventModalOpen(true);
    }
  };

  const handleView = (evenement: EvenementSocialType) => {
    setSelectedEvent(evenement);
    setIsDetailModalOpen(true);
  }

    const handleDelete = (row: { id: number }) => {
        const events_ = evenements.find(s => s.id === row.id);
        if (events_) {
            setSelectedEvent(events_);
            setIsOpenDelete(true);
        }
    };

    const OnFetchEvents = async () => {
    if (!selectedCompany?.id) return;

    setLoadingPage(true);
    try {
      const response = await apiService.get(
          {
            url: `${apiRoutes.admin.app.evenementsSociaux.list_byCompany}${selectedCompany.id}`,
          },
          {
            userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
            hasNoSuccessModal: true,
            onTokenExpired: logout,
          }
      );

      if (response.data) {
        const eventData: EvenementSocialType[] = response.data;
        setEvenements(eventData);
      }
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, {hasNoFailureModal: false});
      } else {
        console.log("Une erreur inconnue est survenue");
      }
    } finally {
      setLoadingPage(false);
    }
  };

  useEffect(() => {
    OnFetchEvents();
  }, [selectedCompany]);

  return (
      <div>
        <DetailDialog isOpen={isDetailModalOpen} setIsOpen={setIsDetailModalOpen}  title={"Détail de l'événement"} description={"Informations de l'événement"}>
          <DetailEvenSocial evenSocial={selectedEvent} />
        </DetailDialog>
          <DeleteModal
              title={"⚠️ Etes-vous sur de vouloir supprimer ?"}
              description={`Cette action est irréversible. En cliquant sur supprimer, vous supprimerez cet événement.`}
              isOpen={isOpenDelete}
              isetIsOpen={setIsOpenDelete}
              isDeleteLoading={loadingSubmit}
              onDelete={handleOnDelete}
              onCancel={()=>{
                  selectedEvent && setSelectedEvent(null)
                  setIsOpenDelete(false)
              }}
          />
        <FormModal
            icon={UsersIcon as Icon}
            title={selectedEvent ? "Modifier l'evenement" : "Ajouter un événement social"}
            description={selectedEvent ? "Remplissez les informations ci-dessous pour modifier un événement social." : 
            "Remplissez les informations ci-dessous pour planifier un nouvel événement social."}
            isOpen={isAddEventModalOpen}
            setIsOpen={setIsAddEventModalOpen}
            loading={loadingPage}
            form={formEvent}
            fields={eventFields}
            onSubmit={selectedEvent? handleOnUpdate : handleAddEventOnSubmit}
            onSubmitSuccess={() => {
              OnFetchEvents();
            }}
            size={"lg"}
            isSubmitLoading={loadingSubmit}
            onClose={() => {
              resetEvent();
              setIsAddEventModalOpen(false);
            }}
        />

        {/* Header */}
        <div className="flex items-center justify-between px-4 mb-4">
          <div className={"text-left"}>
            <h2 className="text-lg font-semibold text-black">Gestion des événements sociaux</h2>
            <p className="text-xs text-muted-foreground">
              Gérez les événements sociaux de votre entreprise
            </p>
          </div>

          <div>
            <Button
                size={"sm"}
                className="flex items-center gap-2 rounded-md bg-primary text-primary-foreground hover:bg-primary/80"
                onClick={() => setIsAddEventModalOpen(true)}
            >
              <CalendarIcon className="h-4 w-4 mr-2" />
              Ajouter un Événement
            </Button>
          </div>
        </div>

        {/* Liste des événements */}
        <div className="px-4 ">
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
                                {value.nom?.toString().toUpperCase().charAt(0) || "?"}
                              </p>
                              <span className="font-medium">{value.nom} {value.prenom}</span>
                            </div>
                        ) : null,
                  },
                  { key: "dateEvenement", label: "Date de l'événement" },
                  { key: "designation", label: "Désignation" },
                  { key: "actionMenee", label: "Action menée" },
                  {
                    key: "montant",
                    label: "Montant (FCFA)",
                    render: (row) =>
                        typeof row === "number"
                            ? `${row.toLocaleString()} FCFA`
                            : "—",
                  },

                ]}
                isLoading={loadingPage}
                data={evenements}
                onAdd={() => {}}
                onEdit={handleEdit}
                onDelete={handleDelete}
                onView={
                  handleView
                }
            />
          </div>
        </div>
      </div>
  );
}