import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import { BookOpen, PlusCircle, CalendarCheck, Clock, CheckCircle, User, X } from "lucide-react";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import FormModal from "@/components/useful/form-modal";
import StatCard from "@/components/useful/StatCard";
import { Icon } from "@tabler/icons-react";
import { FormationForm, formationSchema } from "@/validators-forms/formation-schema";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";
import useCompanyStore from "@/contexts/CompanyContext";
import { Badge } from "@/components/ui/badge";
import { MultiEmployeeSelect } from "@/components/ui/multi-employee-select";
import useEmployeeStore from "@/contexts/useEmployeeStore";
import { DetailDialog } from "@/components/useful/detail-modal";
import DetailTraining from "@/components/layout/administrative-manager/detail-training";
import DeleteModal from "@/components/useful/delete-modal";
import {CalendarDate} from "@internationalized/date";

type Employee = {
  id: number;
  prenom: string;
  nom: string;
};

type Training = {
  id: number;
  theme: string;
  description?: string;
  lieu: string;
  dateDebut: string;
  dateFin: string;
  duree?: number;
  employes?: Employee[];
  status: "Prévue" | "En cours" | "Terminée";
};

type FormationResponse = {
  id: number;
  theme: string;
  description?: string;
  lieu: string;
  dateDebut: string;
  dateFin: string;
};

export default function TrainingTab() {
  const { selectedCompany } = useCompanyStore();
  const { user } = useAuth();
  const { employees, fetchEmployees } = useEmployeeStore();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [trainings, setTrainings] = useState<Training[]>([]);
  const [filteredData, setFilteredData] = useState<Training[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [selectedEmployeeIds, setSelectedEmployeeIds] = useState<number[]>([]);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [selectedTraining, setSelectedTraining] = useState<Training | null>(null);
  const [isOpenDelete, setIsOpenDelete] = useState(false);

  const form = useForm<FormationForm>({
    resolver: zodResolver(formationSchema),
    defaultValues: {},
    shouldFocusError: true,
  });

  const getTrainingStatus = (startDate: string, endDate: string): "Prévue" | "En cours" | "Terminée" => {
    const today = new Date(); 
    const start = new Date(startDate);
    const end = new Date(endDate);
    if (today < start) return "Prévue";
    if (today > end) return "Terminée";
    return "En cours";
  };

  useEffect(() => {
    const fetchTrainings = async () => {
      if (!selectedCompany?.id) return;
      setIsLoading(true);
      try {
        const response = await apiService.get(
          {
            url: apiRoutes.admin.app.employee.formations.listByCompany(selectedCompany.id),
          },
          {
            userToken: `${user?.type} ${user?.token}`,
            hasNoSuccessModal: true,
          }
        );

        const data = response.data || [];
        const formattedTrainings = data.map((training: any) => {
          const start = new Date(training.dateDebut);
          const end = new Date(training.dateFin);
          const duree = Math.max(1, Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)) + 1);

          return {
            id: training.id,
            theme: training.theme,
            description: training.description,
            lieu: training.lieu,
            dateDebut: training.dateDebut,
            dateFin: training.dateFin,
            duree,
            employes: training.employes || [],
            status: getTrainingStatus(training.dateDebut, training.dateFin),
          };
        });

        setTrainings(formattedTrainings);
        setFilteredData(formattedTrainings);
      } catch (error) {
        console.error("Erreur lors du chargement des formations:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchTrainings();
  }, [selectedCompany?.id]);

  useEffect(() => {
    if (selectedCompany && user) {
      fetchEmployees(selectedCompany, user);
    }
  }, [selectedCompany, user]);

  function toCalendarDate(date: Date | string): CalendarDate {
    const d = typeof date === "string" ? new Date(date) : date;
    return new CalendarDate(d.getFullYear(), d.getMonth() + 1, d.getDate());
  }

  const handleEditTraining = (training: Training) => {
    setSelectedTraining(training);
    form.reset({
      theme: training.theme,
      description: training.description ?? "",
      employees: training.employes?.map((e) => e.id) ?? [],
      lieu: training.lieu,
      dateDebut: toCalendarDate(training.dateDebut),
      dateFin: toCalendarDate(training.dateFin),
    });
    setSelectedEmployeeIds(training.employes?.map((e) => e.id) ?? []);
    setIsModalOpen(true);
  };

  const handleSubmitTraining = async (values: FormationForm) => {
    if (!selectedCompany?.id) return;

    try {
      const start = new Date(values.dateDebut.toString());
      const end = new Date(values.dateFin.toString());
      const duree = Math.max(1, Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)) + 1);
      const dateDebut = start.toISOString().split("T")[0];
      const dateFin = end.toISOString().split("T")[0];

      const payload = {
        theme: values.theme,
        description: values.description,
        lieu: values.lieu,
        dateDebut,
        dateFin,
        companyId: selectedCompany.id,
        employeIds: selectedEmployeeIds,
      };

      if (selectedTraining) {
        await apiService.put(
          {
            url: `${apiRoutes.admin.app.employee.formations.update}${selectedTraining.id}`,
            body: payload,
          },
          {
            userToken: `${user?.type} ${user?.token}`,
            hasNoSuccessModal: false,
          }
        );

        const updated: Training = {
          ...selectedTraining,
          ...values,
          dateDebut,
          dateFin,
          duree,
          employes: employees
            .filter((e) => selectedEmployeeIds.includes(e.employeId))
            .map((e) => ({ id: e.employeId, nom: e.nom, prenom: e.prenom })),
          status: getTrainingStatus(dateDebut, dateFin),
        };

        setTrainings((prev) => prev.map((t) => (t.id === selectedTraining.id ? updated : t)));
        setFilteredData((prev) => prev.map((t) => (t.id === selectedTraining.id ? updated : t)));
      } else {
        const response = await apiService.post(
          {
            url: apiRoutes.admin.app.employee.formations.create,
            body: payload,
          },
          {
            userToken: `${user?.type} ${user?.token}`,
            hasNoSuccessModal: false,
          }
        );

        const data = response.data as unknown as FormationResponse;
        const newTraining: Training = {
          id: data.id,
          ...values,
          dateDebut,
          dateFin,
          duree,
          status: getTrainingStatus(dateDebut, dateFin),
        };

        setTrainings((prev) => [...prev, newTraining]);
        setFilteredData((prev) => [...prev, newTraining]);
      }

      form.reset();
      setSelectedEmployeeIds([]);
      setSelectedTraining(null);
      setIsModalOpen(false);
    }  catch (error) {
      console.error("Erreur lors de l'enregistrement de la formation:", error);
    }
  };

  const handleViewTraining = (training: Training) => {
    setSelectedTraining(training);
    setIsDetailModalOpen(true);
  };

  const handleDeleteTraining = (row: Training) => {
    setSelectedTraining(row);
    setIsOpenDelete(true);
  };

  const handleOnDeleteTraining = async () => {
    if (!selectedTraining) return;
    try {
      setIsLoading(true);
      await apiService.remove(
        {
          url: `${apiRoutes.admin.app.employee.formations.delete}/${selectedTraining.id}`,
        },
        {
          userToken: `${user?.type} ${user?.token}`,
          hasNoSuccessModal: false,
        }
      );
      setTrainings((prev) => prev.filter((t) => t.id !== selectedTraining.id));
      setFilteredData((prev) => prev.filter((t) => t.id !== selectedTraining.id));
      setSelectedTraining(null);
      setIsOpenDelete(false);
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message);
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleFilter = (query: string) => {
    const lowerQuery = query.trim().toLowerCase();
    if (!lowerQuery) return setFilteredData(trainings);
    const result = trainings.filter((item) =>
      item.theme?.toLowerCase().includes(lowerQuery) ||
      item.status?.toLowerCase().includes(lowerQuery) ||
      item.lieu?.toLowerCase().includes(lowerQuery)
    );
    setFilteredData(result);
  };

  const customRenders = {
    employees: () => (
      <div className="space-y-2">
        <MultiEmployeeSelect
          selectedEmployeeIds={selectedEmployeeIds}
          onSelectionChange={setSelectedEmployeeIds}
        />
        {selectedEmployeeIds.length > 0 && (
          <div className="flex flex-wrap gap-2">
            {selectedEmployeeIds.map((id) => {
              const employee = employees.find((e) => e.employeId === id);
              return (
                <Badge key={id} variant="outline" className="pl-2">
                  <div className="flex items-center gap-2">
                    <User className="h-4 w-4" />
                    <span>{`${employee?.prenom} ${employee?.nom}`}</span>
                    <button
                      onClick={() =>
                        setSelectedEmployeeIds((prev) => prev.filter((eId) => eId !== id))
                      }
                      className="ml-2 rounded-full p-0.5 hover:bg-accent"
                    >
                      <X className="h-3 w-3" />
                    </button>
                  </div>
                </Badge>
              );
            })}
          </div>
        )}
      </div>
    ),
  };

  const trainingFields = [
    {
      tag: "theme",
      label: "Thème",
      input_type: "text",
      size: "col-span-12",
      placeholder: "",
      required: true,
    },
    {
      tag: "description",
      label: "Description",
      input_type: "textarea",
      size: "col-span-12",
      required: false,
    },
    {
      tag: "employees",
      label: "Employés concernés",
      input_type: "custom",
      size: "col-span-12",
      required: false,
    },
    {
      tag: "lieu",
      label: "Lieu",
      input_type: "text",
      size: "col-span-6",
      required: true,
    },
    {
      tag: "dateDebut",
      label: "Date de début",
      input_type: "date",
      size: "col-span-6",
      required: true,
    },
    {
      tag: "dateFin",
      label: "Date de fin",
      input_type: "date",
      size: "col-span-6",
      required: true,
    },
  ];

  return (
    <div className="px-4">
      <DetailDialog
        isOpen={isDetailModalOpen}
        setIsOpen={setIsDetailModalOpen}
        title="Détail de la formation"
        description="Informations sur la formation"
        size="lg"
      >
        <DetailTraining training={selectedTraining} />
      </DetailDialog>

      <DeleteModal
        title="⚠️ Confirmation de suppression"
        description="Cette action est irréversible. Voulez-vous vraiment supprimer cette formation ?"
        isOpen={isOpenDelete}
        isetIsOpen={setIsOpenDelete}
        isDeleteLoading={isLoading}
        onDelete={handleOnDeleteTraining}
        onCancel={() => {
          setIsOpenDelete(false);
          setSelectedTraining(null);
        }}
      />

      <FormModal
        icon={BookOpen as Icon}
        title={selectedTraining ? "Modifier une formation" : "Ajouter une formation"}
        description={
          selectedTraining
            ? "Remplissez les informations ci-dessous pour modifier une formation."
            : "Remplissez les informations ci-dessous pour planifier une nouvelle formation."
        }
        isOpen={isModalOpen}
        setIsOpen={setIsModalOpen}
        loading={isLoading}
        form={form}
        fields={trainingFields}
        customRenders={customRenders}
        onSubmit={handleSubmitTraining}
        onSubmitSuccess={() => {}}
        size="lg"
        isSubmitLoading={isLoading}
        onClose={() => {
          form.reset();
          setSelectedEmployeeIds([]);
          setSelectedTraining(null);
          setIsModalOpen(false);
        }}
      />

      <div className="flex items-center justify-between mb-4">
        <div className={"text-left"}>
          <h2 className="text-lg font-semibold text-black">Gestion des formations</h2>
          <p className="text-xs text-muted-foreground">
            Formations planifiées, en cours ou terminées.
          </p>
        </div>
        <div>
          <Button
            size={"sm"}
            className="flex items-center gap-2 rounded-md bg-primary text-primary-foreground hover:bg-primary/80"
            onClick={() => setIsModalOpen(true)}
          >
            <PlusCircle className="h-4 w-4 mr-2"/>
            Nouvelle formation
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
        <StatCard icon={CalendarCheck} title="Total" value={filteredData.length} bgColor="bg-sky-100" textColor="text-sky-600" />
        <StatCard icon={Clock} title="Prévue" value={filteredData.filter(t => t.status === "Prévue").length} bgColor="bg-yellow-100" textColor="text-yellow-600" />
        <StatCard icon={CheckCircle} title="Terminée" value={filteredData.filter(t => t.status === "Terminée").length} bgColor="bg-green-100" textColor="text-green-600" />
      </div>

      <div className="m-auto w-full gap-2">
        {isLoading ? (
          <div className="flex justify-center items-center py-8">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
          </div>
        ) : (
          <DynamicTable3
            columns={[
              { key: "theme", label: "Thème" },
              { key: "lieu", label: "Lieu" },
              { key: "dateDebut", label: "Date de début" },
              { key: "dateFin", label: "Date de fin" },
              { key: "duree", label: "Durée (jours)" },
              {
                key: "status",
                label: "Statut",
                render: (status: "Prévue" | "En cours" | "Terminée") => {
                  const statusMap: Record<typeof status, string> = {
                    Prévue: "bg-yellow-50 text-yellow-600",
                    "En cours": "bg-blue-50 text-blue-600",
                    Terminée: "bg-green-50 text-green-600",
                  };
                  return (
                    <span className={`${statusMap[status]} py-0.5 px-2 text-[0.7rem] rounded font-medium`}>
                      {status}
                    </span>
                  );
                },
              },
            ]}
            data={filteredData}
            onAdd={() => setIsModalOpen(true)}
            onEdit={handleEditTraining}
            onDelete={handleDeleteTraining}
            onView={handleViewTraining}
            onFilter={handleFilter}
            filterPlaceholder="Rechercher par thème, lieu ou statut..."
          />
        )}
      </div>
    </div>
  );
}