import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import { UserPlusIcon, AlarmClock, CalendarCheck, Ban } from "lucide-react";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import FormModal from "@/components/useful/form-modal";
import StatCard from "@/components/useful/StatCard";
import { Icon } from "@tabler/icons-react";
import { AbsenceForm, absenceSchema } from "@/validators-forms/absence-schema.ts";
import apiService from "@/api/apiService.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import { useAuth } from "@/lib/auth.ts";
import useCompanyStore from "@/contexts/CompanyContext.ts";
import { DateHelpers } from "@/helpers/DateHelpers.ts";
import useEmployeeStore from "@/contexts/useEmployeeStore.ts";
import { EmployeeActifType } from "@/types/employee/EmployeeType.ts";
import { AbsenceType } from "@/types/AbsenceType.ts";
import { DetailDialog } from "@/components/useful/detail-modal";
import DetailAbsence from "@/components/layout/administrative-manager/detail-absence";
import DeleteModal from "@/components/useful/delete-modal";
import {CalendarDate} from "@internationalized/date";

type AbsenceDetailType = AbsenceType & {
  employeNomComplet?: string;
  typeAbsenceLabel?: string;
};

export default function WorkAbsenceTab() {
  const { user, logout } = useAuth();
  const { selectedCompany } = useCompanyStore();
  const { employees } = useEmployeeStore();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [absences, setAbsences] = useState<AbsenceType[]>([]);
  const [selectedAbsence, setSelectedAbsence] = useState<AbsenceType | null>(null);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [filteredData, setFilteredData] = useState<AbsenceType[]>([]);
  const [loadingSubmit, setLoadingSubmit] = useState(false);
  const [loadingPage, setLoadingPage] = useState(false);
  const [absenceTypes, setAbsenceTypes] = useState<{ label: string; value: string }[]>([]);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isDeleteLoading, setIsDeleteLoading] = useState(false);

  const absenceFields = [
    {
      tag: "employeId",
      label: "Employé",
      input_type: "select",
      size: "col-span-12",
      placeholder: "",
      required: true,
      options: employees?.map((em: EmployeeActifType) => ({
        value: em.employeId.toString(),
        label: `${em.nom} ${em.prenom}`,
      })) || [],
    },
    {
      tag: "typeAbsence",
      label: "Type d'absence",
      input_type: "select",
      size: "col-span-6",
      placeholder: "",
      required: true,
      options: absenceTypes,
    },
    {
      tag: "modeJouissance",
      label: "Mode de jouissance",
      input_type: "select",
      size: "col-span-6",
      placeholder: "",
      required: true,
      options: [
        { label: "Numéraire", value: "NUMERAIRE" },
        { label: "Réel", value: "REEL" },
        { label: "Différé", value: "DIFFERE" },
        { label: "Epargne", value: "EPARGNE" },
      ],
    },
    {
      tag: "conditionAcceptation",
      label: "Condition d'acceptation",
      input_type: "select",
      size: "col-span-6",
      placeholder: "",
      required: true,
      options: [
        { label: "Déduire des congés", value: "A_DEDUIRE_DES_CONGES" },
        { label: "Déduire du salaire", value: "A_DEDUIRE_DU_SALAIRE_DE_PRESENCE" },
        { label: "Sans condition", value: "SANS_CONDITION" },
      ],
    },
    {
      tag: "dateDebut",
      label: "Date de début",
      input_type: "date",
      size: "col-span-6",
      placeholder: "",
      required: true,
    },
    {
      tag: "dateFin",
      label: "Date de fin",
      input_type: "date",
      size: "col-span-6",
      placeholder: "",
      required: true,
    },
    {
      tag: "commentaire",
      label: "Commentaire",
      input_type: "textarea",
      size: "col-span-12",
      placeholder: "",
      required: false,
      rows: 4,
    },
    {
      tag: "preuve",
      label: "Preuve",
      input_type: "file",
      size: "col-span-12",
      placeholder: "",
      required: false,
      accept: ".pdf,.jpg,.png,.doc,.docx",
    },
  ];

  const form = useForm<AbsenceForm>({
    resolver: zodResolver(absenceSchema),
    defaultValues: {},
    shouldFocusError: true,
  });

  function toCalendarDate(date: Date | string): CalendarDate {
    const d = typeof date === "string" ? new Date(date) : date;
    return new CalendarDate(d.getFullYear(), d.getMonth() + 1, d.getDate());
  }

  const handleEditAbsence = (absence: AbsenceType) => {
    setSelectedAbsence(absence);
    form.reset({
      employeId: absence.employeId.toString(),
      modeJouissance: absence.modeJouissance,
      conditionAcceptation: absence.conditionAcceptation,
      typeAbsence: absence.typeAbsenceId?.toString() || "",
      dateDebut: toCalendarDate(absence.dateDebut),
      dateFin: toCalendarDate(absence.dateFin),
      commentaire: absence.commentaire || "",
      preuve: absence.preuve ? [absence.preuve] : [],
    });
    setIsModalOpen(true);
  };

  const handleViewAbsence = (absence: AbsenceType) => {
    const employe = employees.find((e) => e.employeId === absence.employeId);
    const typeAbsence = absenceTypes.find((t) => t.value === absence.typeAbsenceId?.toString());
    const absenceDetails: AbsenceDetailType = {
      ...absence,
      employeNomComplet: employe ? `${employe.prenom} ${employe.nom}` : "Inconnu",
      typeAbsenceLabel: typeAbsence?.label ?? "Inconnu",
    };
    setSelectedAbsence(absenceDetails);
    setIsDetailModalOpen(true);
  };

  const handleDeleteAbsence = (absence: AbsenceType) => {
    setSelectedAbsence(absence);
    setIsDeleteModalOpen(true);
  };

  const confirmDeleteAbsence = async () => {
    if (!selectedAbsence) return;
    setIsDeleteLoading(true);
    try {
      await apiService.remove(
        {
          url: `${apiRoutes.admin.app.employee.absences.delete}${selectedAbsence.id}`,
        },
        {
          userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
          hasNoSuccessModal: false,
          onTokenExpired: logout,
        }
      );
      setSelectedAbsence(null);
      setIsDeleteModalOpen(false);
      await fetchAbsences();
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message);
      }
    } finally {
      setIsDeleteLoading(false);
    }
  };

  const fetchAbsenceTypes = async () => {
    try {
      const response = await apiService.get(
        {
          url: apiRoutes.admin.app.employee.absences.type,
        },
        {
          userToken: `${user?.type} ${user?.token}`,
          hasNoSuccessModal: true,
          onTokenExpired: logout,
        }
      );
      if (response.data) {
        setAbsenceTypes(
            response.data.map((type: any) => ({
              label: type.libelle,
              value: type.id.toString(),
            }))
        );
      }
    } catch (error) {
      console.error("Error fetching absence types:", error);
    }
  };

  const fetchAbsences = async () => {
    setLoadingPage(true);
    try {
      if (!selectedCompany?.id) return;

      const response = await apiService.get(
          {
            url: `${apiRoutes.admin.app.employee.absences.list_by_company}/${selectedCompany.id}`,
          },
          {
            userToken: `${user?.type} ${user?.token}`,
            hasNoSuccessModal: true,
            onTokenExpired: logout,
          }
      );
      if (response.data) {
        setAbsences(response.data);
        setFilteredData(response.data);
      }
    } catch (error) {
      console.error("Error fetching company absences:", error);
    } finally {
      setLoadingPage(false);
    }
  };

  const handleSubmitAbsence = async (values: AbsenceForm) => {
    setLoadingSubmit(true);
    try {
      const employee = employees.find((em) => em.employeId === Number(values.employeId));
      if (!employee || !selectedCompany?.id || !employee.contratEmployeId) {
        throw new Error("Informations manquantes");
      }

      const formData = new FormData();
      formData.append("contratEmployeId", employee.contratEmployeId.toString());
      formData.append("companyId", selectedCompany.id.toString());
      formData.append("typeAbsenceId", values.typeAbsence);
      formData.append("libelle", values.commentaire || "");
      formData.append("modeJouissance", values.modeJouissance);
      formData.append("conditionAcceptation", values.conditionAcceptation);
      const dateDebutISO = DateHelpers.convertDateToISO(values.dateDebut) || "";
      const dateFinISO = DateHelpers.convertDateToISO(values.dateFin) || "";
      formData.append("dateDebut", dateDebutISO);
      formData.append("dateFin", dateFinISO);
      if (values.preuve?.[0]) formData.append("preuve", values.preuve[0]);

      if (selectedAbsence) {
        await apiService.put(
            {
              url: `${apiRoutes.admin.app.employee.absences.update}${selectedAbsence.id}`,
              body: formData,
            },
            { userToken: `${user?.type} ${user?.token}`, onTokenExpired: logout }
        );
      } else {
        await apiService.post(
            {
              url: apiRoutes.admin.app.employee.absences.create,
              body: formData,
            },
            { userToken: `${user?.type} ${user?.token}`, onTokenExpired: logout }
        );
      }

      form.reset();
      setIsModalOpen(false);
      setSelectedAbsence(null);
      await fetchAbsences();
    } catch (error) {
      if (error instanceof Error) apiService.handleError(error.message);
    } finally {
      setLoadingSubmit(false);
    }
  };

  const handleFilter = (query: string) => {
    const lowerQuery = query.trim().toLowerCase();
    if (!lowerQuery) return setFilteredData(absences);
    setFilteredData(
        absences.filter(
            (item) =>
                item.employe?.nom?.toLowerCase().includes(lowerQuery) ||
                item.employe?.prenom?.toLowerCase().includes(lowerQuery) ||
                item.typeAbsence?.toLowerCase().includes(lowerQuery) ||
                item.status?.toLowerCase().includes(lowerQuery)
        )
    );
  };

  useEffect(() => {
    fetchAbsenceTypes();
    fetchAbsences();
  }, [selectedCompany?.id]);

  return (
      <div className="px-4">
        <FormModal
            icon={Ban as Icon}
            title={selectedAbsence ? "Modifier une absence" : "Enregistrement d'une absence"}
            description={
              selectedAbsence
                  ? "Remplissez les informations ci-dessous pour modifier une absence."
                  : "Remplissez les informations ci-dessous pour déclarer une nouvelle absence."
            }
            isOpen={isModalOpen}
            setIsOpen={setIsModalOpen}
            loading={loadingPage}
            form={form}
            fields={absenceFields}
            onSubmit={handleSubmitAbsence}
            onSubmitSuccess={() => {
              fetchAbsences();
            }}
            size="lg"
            isSubmitLoading={loadingSubmit}
            onClose={() => {
              setIsModalOpen(false);
              setSelectedAbsence(null);
              form.reset();
            }}
        />

        <DetailDialog
            isOpen={isDetailModalOpen}
            setIsOpen={setIsDetailModalOpen}
            title="Détail de l'absence"
            description="Informations sur l'absence"
            size="lg"
        >
          <DetailAbsence absence={selectedAbsence} />
        </DetailDialog>

        <DeleteModal
            title="⚠️ Confirmation de suppression"
            description="Cette action est irréversible. Voulez-vous vraiment supprimer cette absence ?"
            isOpen={isDeleteModalOpen}
            isetIsOpen={setIsDeleteModalOpen}
            isDeleteLoading={isDeleteLoading}
            onDelete={confirmDeleteAbsence}
            onCancel={() => {
              setIsDeleteModalOpen(false);
              setSelectedAbsence(null);
            }}
        />

        <div className="flex items-center justify-between mb-4">
          <div className={"text-left"}>
            <h2 className="text-lg font-semibold text-black">Gestion des absences</h2>
            <p className="text-xs text-muted-foreground">
              Visualisez et gérez les absences des agents
            </p>
          </div>
          <div>
            <Button
                size={"sm"}
                className="flex items-center gap-2 rounded-md bg-primary text-primary-foreground hover:bg-primary/80"
                onClick={() => setIsModalOpen(true)}
            >
              <UserPlusIcon className="h-4 w-4 mr-2"/>
              Enregistrement d'une absence
            </Button>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
          <StatCard
              icon={CalendarCheck}
              title="Total Absences"
              value={filteredData.length}
              bgColor="bg-gray-100"
              textColor="text-red-600"
          />
          <StatCard
              icon={AlarmClock}
              title="En Attente"
              value={filteredData.filter((a) => a.status === "EN_ATTENTE").length}
              bgColor="bg-yellow-100"
              textColor="text-yellow-600"
          />
          <StatCard
              icon={Ban}
              title="Refusées"
              value={filteredData.filter((a) => a.status === "REFUSEE").length}
              bgColor="bg-red-100"
              textColor="text-red-600"
          />
        </div>

        <div className="m-auto w-full gap-2">
          <DynamicTable3
              columns={[
                {
                  key: "employeId",
                  label: "Agent",
                  render: (employeId) => {
                    const employee = employees.find((e) => e.employeId === employeId);
                    return employee ? (
                        <div className="flex items-center gap-2">
                          <p className="w-8 h-8 flex items-center justify-center rounded-full border text-sm font-medium bg-muted text-muted-foreground">
                            {employee.nom?.charAt(0)?.toUpperCase() || "?"}
                          </p>
                          <span className="font-medium">
                      {employee.nom} {employee.prenom}
                    </span>
                        </div>
                    ) : (
                        "N/A"
                    );
                  },
                },
                {
                  key: "typeAbsenceId",
                  label: "Type d'absence",
                  render: (typeId) => {
                    const type = absenceTypes.find((t) => t.value === typeId?.toString());
                    return type?.label || typeId || "N/A";
                  },
                },
                { key: "dateDebut", label: "Début" },
                { key: "dateFin", label: "Fin" },
                { key: "modeJouissance", label: "Jouissance" },
              ]}
              data={filteredData}
              onAdd={() => setIsModalOpen(true)}
              onEdit={handleEditAbsence}
              onDelete={handleDeleteAbsence}
              onView={handleViewAbsence}
              onFilter={handleFilter}
              filterPlaceholder="Rechercher par nom, type ou statut..."
              isLoading={loadingPage}
          />
        </div>
      </div>
  );
}