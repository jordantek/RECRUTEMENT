import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import { UserPlusIcon, Clock, DollarSign, CheckCircle } from "lucide-react";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import FormModal from "@/components/useful/form-modal";
import StatCard from "@/components/useful/StatCard";
import { Icon } from "@tabler/icons-react";
import apiService from "@/api/apiService.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import { useAuth } from "@/lib/auth.ts";
import useCompanyStore from "@/contexts/CompanyContext.ts";
import useEmployeeStore from "@/contexts/useEmployeeStore.ts";
import { EmployeeActifType } from "@/types/employee/EmployeeType.ts";
import { z } from "zod";

// Schéma de validation pour les avances
const advanceSchema = z.object({
  employeId: z.string().min(1, "L'employé est requis"),
  montantTotal: z.number().min(1, "Le montant total doit être positif"),
  montantMensuel: z.number().min(1, "Le montant mensuel doit être positif"),
  moisDemarrage: z.string().min(1, "Le mois de démarrage est requis"),
  moisFin: z.string().min(1, "Le mois de fin est requis"),
  observation: z.string().optional(),
});

type AdvanceForm = z.infer<typeof advanceSchema>;

type AdvanceType = {
  id: number;
  contratEmployeId: number;
  employeId: number;
  employe: {
    id: number;
    nom: string;
    prenom: string;
    is_employe_interne: boolean;
  };
  companyId: number;
  rubriqueId: number;
  moisDemarrage: string;
  moisFin: string;
  dureeAvance: number;
  observation: string | null;
  montantTotal: number;
  montantMensuel: number;
  statut: boolean;
  addedById: number;
};

export default function AdvanceTab() {
  const { user, logout } = useAuth();
  const { selectedCompany } = useCompanyStore();
  const { employees } = useEmployeeStore();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [advances, setAdvances] = useState<AdvanceType[]>([]);
  const [filteredData, setFilteredData] = useState<AdvanceType[]>([]);
  const [loadingSubmit, setLoadingSubmit] = useState(false);
  const [loadingPage, setLoadingPage] = useState(false);

  const advanceFields = [
    {
      tag: "employeId",
      label: "Employé",
      input_type: "select",
      size: "col-span-12",
      required: true,
      options: employees?.map((em: EmployeeActifType) => ({
        value: em.employeId.toString(),
        label: `${em.nom} ${em.prenom}`,
      })) || []
    },
    {
      tag: "montantTotal",
      label: "Montant total",
      input_type: "number",
      size: "col-span-6",
      required: true,
      min: 1,
      step: 0.01
    },
    {
      tag: "montantMensuel",
      label: "Montant mensuel",
      input_type: "number",
      size: "col-span-6",
      required: true,
      min: 1,
      step: 0.01
    },
    {
      tag: "moisDemarrage",
      label: "Mois de démarrage",
      input_type: "month",
      size: "col-span-6",
      required: true,
    },
    {
      tag: "moisFin",
      label: "Mois de fin",
      input_type: "month",
      size: "col-span-6",
      required: true,
    },
    {
      tag: "observation",
      label: "Observations",
      input_type: "textarea",
      size: "col-span-full",
      required: false,
      rows: 3,
    },
  ];

  const form = useForm<AdvanceForm>({
    resolver: zodResolver(advanceSchema),
    defaultValues: {},
    shouldFocusError: true,
  });

  const fetchAdvances = async () => {
    setLoadingPage(true);
    try {
      if (!selectedCompany?.id) {
        setAdvances([]);
        setFilteredData([]);
        return;
      }
  
      const response = await apiService.get(
        {
          url: apiRoutes.admin.app.avance.listByCompany(selectedCompany.id),
        },
        {
          userToken: `${user?.type} ${user?.token}`,
          hasNoSuccessModal: true,
          onTokenExpired: logout,
        }
      );
  
      if (response.data) {
        setAdvances(response.data);
        setFilteredData(response.data);
      }
    } catch (error) {
      console.error("Error fetching company advances:", error);
    } finally {
      setLoadingPage(false);
    }
  };

  const handleSubmitAdvance = async (values: AdvanceForm) => {
    setLoadingSubmit(true);
    try {
      const employee = employees.find((em) => em.employeId === Number(values.employeId));
  
      if (!employee || !selectedCompany?.id) {
        throw new Error("Employee or company not found");
      }
  
      if (!employee.contratEmployeId) {
        throw new Error("L'employé n'a pas de contrat associé");
      }
  
      const advanceData = {
        contratEmployeId: employee.contratEmployeId,
        employeId: employee.employeId,
        companyId: selectedCompany.id,
        rubriqueId: 10, // À adapter selon votre besoin
        moisDemarrage: values.moisDemarrage,
        moisFin: values.moisFin,
        observation: values.observation || null,
        montantTotal: values.montantTotal,
        montantMensuel: values.montantMensuel,
      };
  
      await apiService.post(
        {
          url: apiRoutes.admin.app.avance.create,
          body: advanceData,
        },
        {
          userToken: `${user?.type} ${user?.token}`,
          hasNoSuccessModal: false,
          onTokenExpired: logout,
        }
      );
  
      form.reset();
      setIsModalOpen(false);
      await fetchAdvances();
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: false });
      } else {
        console.log("Une erreur inconnue est survenue");
      }
    } finally {
      setLoadingSubmit(false);
    }
  };

  const handleFilter = (query: string) => {
    const lowerQuery = query.trim().toLowerCase();
    if (!lowerQuery) {
      setFilteredData(advances);
      return;
    }
    const result = advances.filter((item) =>
      (item.employe?.nom?.toLowerCase().includes(lowerQuery)) ||
      (item.employe?.prenom?.toLowerCase().includes(lowerQuery)) ||
      (item.montantTotal.toString().includes(lowerQuery)) ||
      (item.statut.toString().toLowerCase().includes(lowerQuery))
    );
    setFilteredData(result);
  };

  useEffect(() => {
    fetchAdvances();
  }, [selectedCompany?.id]);

  return (
    <div className="space-y-4">
      <FormModal
        icon={DollarSign as Icon}
        title="Enregistrement d'une avance"
        description="Remplissez les informations ci-dessous pour enregistrer une avance."
        isOpen={isModalOpen}
        setIsOpen={setIsModalOpen}
        form={form}
        fields={advanceFields}
        onSubmit={handleSubmitAdvance}
        isSubmitLoading={loadingSubmit}
        onClose={() => {
          setIsModalOpen(false);
          form.reset();
        }}
      />

      <div className="flex items-center justify-between">
        <div className="text-left">
          <h2 className="text-lg font-semibold text-black">Gestion des avances</h2>
          <p className="text-xs text-muted-foreground">
            Visualisez et gérez les avances des employés
          </p>
        </div>
        <Button
          size="sm"
          className="flex items-center gap-2 rounded-md bg-primary text-primary-foreground hover:bg-primary/80"
          onClick={() => setIsModalOpen(true)}
        >
          <UserPlusIcon className="h-4 w-4 mr-2" />
          Nouvelle avance
        </Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatCard
          icon={DollarSign}
          title="Total Avances"
          value={filteredData.length}
          bgColor="bg-gray-100"
          textColor="text-blue-600"
        />
        <StatCard
          icon={Clock}
          title="En Cours"
          value={filteredData.filter((a) => !a.statut).length}
          bgColor="bg-yellow-100"
          textColor="text-yellow-600"
        />
        <StatCard
          icon={CheckCircle}
          title="Soldées"
          value={filteredData.filter((a) => a.statut).length}
          bgColor="bg-green-100"
          textColor="text-green-600"
        />
      </div>

      <div className="m-auto w-full gap-2">
        <DynamicTable3
          columns={[
            {
              key: "employe",
              label: "Employé",
              render: (employe) => (
                <div className="flex items-center gap-2">
                  <p className="w-8 h-8 flex items-center justify-center rounded-full border text-sm font-medium bg-muted text-muted-foreground">
                    {employe?.nom?.charAt(0)?.toUpperCase() || '?'}
                  </p>
                  <span className="font-medium">
                    {employe?.nom} {employe?.prenom}
                  </span>
                </div>
              )
            },
            { 
              key: "montantTotal",
              label: "Montant total",
              render: (montant) => `${montant.toFixed(2)} FCFA`
            },
            { 
              key: "montantMensuel",
              label: "Montant mensuel",
              render: (montant) => `${montant.toFixed(2)} FCFA`
            },
            { 
              key: "moisDemarrage",
              label: "Date début",
              render: (mois) => new Date(mois).toLocaleDateString('fr-FR', { month: 'short', year: 'numeric' })
            },
            { 
              key: "moisFin",
              label: "Date fin",
              render: (mois) => new Date(mois).toLocaleDateString('fr-FR', { month: 'short', year: 'numeric' })
            },
            { 
              key: "dureeAvance",
              label: "Durée",
              render: (duree) => `${duree} mois`
            },
            {
              key: "statut",
              label: "Statut",
              render: (statut) => (
                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                  statut ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
                }`}>
                  {statut ? 'Soldé' : 'En cours'}
                </span>
              )
            },
            { key: "observation", label: "Observations" },
          ]}
          data={filteredData}
          onAdd={() => setIsModalOpen(true)}
          onFilter={handleFilter}
          onEdit={undefined}
          onDelete={undefined}
          onView={undefined}
          filterPlaceholder="Rechercher par nom, montant ou statut..."
          isLoading={loadingPage}
        />
      </div>
    </div>
  );
}