import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import {
  UserPlusIcon,
  CalendarCheck,
  AlarmClock,
  Ban,
  ShieldX,
} from "lucide-react";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import FormModal from "@/components/useful/form-modal";
import StatCard from "@/components/useful/StatCard";
import { Icon } from "@tabler/icons-react";
import { EmployeeSelect } from "@/components/useful/EmployeeSelect";

const terminationAllowanceSchema = z.object({
  nom_agent: z.string().min(1),
  mois_traitement: z.string().min(1),
  date_debut_brut: z.string().min(1),
  date_fin_brut: z.string().min(1),
  date_debut_anciennete: z.string().min(1),
  date_fin_anciennete: z.string().min(1),
});

type TerminationAllowanceForm = z.infer<typeof terminationAllowanceSchema>;

const terminationFields = [
  {
    tag: "nom_agent",
    label: "Nom de l'agent",
    input_type: "text",
    size: "col-span-12",
    required: true,
  },
  {
    tag: "mois_traitement",
    label: "Mois de traitement",
    input_type: "month",
    size: "col-span-6",
    required: true,
  },
  {
    tag: "date_debut_brut",
    label: "Début P.R. Salaire brut",
    input_type: "date",
    size: "col-span-6",
    required: true,
  },
  {
    tag: "date_fin_brut",
    label: "Fin P.R. Salaire brut",
    input_type: "date",
    size: "col-span-6",
    required: true,
  },
  {
    tag: "date_debut_anciennete",
    label: "Début P.R. Ancienneté",
    input_type: "date",
    size: "col-span-6",
    required: true,
  },
  {
    tag: "date_fin_anciennete",
    label: "Fin P.R. Ancienneté",
    input_type: "date",
    size: "col-span-6",
    required: true,
  },
];

const terminationData: TerminationAllowanceForm[] = [
  {
    nom_agent: "Kouadio Jean",
    mois_traitement: "2025-06",
    date_debut_brut: "2024-06-01",
    date_fin_brut: "2025-05-31",
    date_debut_anciennete: "2015-01-01",
    date_fin_anciennete: "2025-05-31",
  },
  {
    nom_agent: "Samba Fatou",
    mois_traitement: "2025-06",
    date_debut_brut: "2024-07-01",
    date_fin_brut: "2025-06-30",
    date_debut_anciennete: "2018-03-15",
    date_fin_anciennete: "2025-06-30",
  },
];

export default function TerminationAllowanceTab() {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [filteredData, setFilteredData] = useState(terminationData);

  const form = useForm<TerminationAllowanceForm>({
    resolver: zodResolver(terminationAllowanceSchema),
    defaultValues: {},
  });

  const handleAdd = (values: TerminationAllowanceForm) => {
    console.log(values);
    setIsModalOpen(false);
    form.reset();
  };

  const handleFilter = (query: string) => {
    const lowerQuery = query.trim().toLowerCase();
    if (!lowerQuery) {
      setFilteredData(terminationData);
      return;
    }
    const result = terminationData.filter((item) =>
      item.nom_agent?.toLowerCase().includes(lowerQuery) ||
      item.mois_traitement?.toLowerCase().includes(lowerQuery)
    );
    setFilteredData(result);
  };

  return (
    <div className="space-y-4 px-4">
      {/* Modal */}
      <FormModal
        icon={ShieldX as Icon}
        title="Ajouter une indemnité de licenciement"
        description="Remplissez les informations demandées."
        isOpen={isModalOpen}
        setIsOpen={setIsModalOpen}
        form={form}
        fields={terminationFields}
        onSubmit={handleAdd}
        onClose={() => {
          setIsModalOpen(false);
          form.reset();
        }}
      />

      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-semibold text-black">Indemnités de licenciement</h2>
          <p className="text-xs text-muted-foreground">
            Données liées aux indemnités versées lors de licenciements.
          </p>
        </div>
        <EmployeeSelect />
        <Button
          size="sm"
          className="flex items-center gap-2 bg-primary text-primary-foreground hover:bg-primary/80"
          onClick={() => setIsModalOpen(true)}
        >
          <UserPlusIcon className="h-4 w-4 mr-2" />
          Ajouter une indemnité
        </Button>
      </div>

      {/* Statistiques */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatCard
          icon={CalendarCheck}
          title="Total licenciements"
          value={filteredData.length}
          bgColor="bg-blue-100"
          textColor="text-blue-600"
        />
        <StatCard
          icon={AlarmClock}
          title="Mois en cours"
          value={filteredData.filter((e) => e.mois_traitement === "2025-06").length}
          bgColor="bg-yellow-100"
          textColor="text-yellow-600"
        />
        <StatCard
          icon={Ban}
          title="Ancienneté > 5 ans"
          value={filteredData.filter((e) => {
            const debut = new Date(e.date_debut_anciennete);
            const fin = new Date(e.date_fin_anciennete);
            const diff = (fin.getTime() - debut.getTime()) / (1000 * 60 * 60 * 24 * 365);
            return diff > 5;
          }).length}
          bgColor="bg-red-100"
          textColor="text-red-600"
        />
      </div>

      {/* Tableau */}
      <DynamicTable3
        columns={[
          {
            key: "nom_agent",
            label: "Agent",
            render: (value) => (
              <div className="flex items-center gap-2">
                <p className="w-8 h-8 flex items-center justify-center rounded-full border text-sm font-medium bg-muted text-muted-foreground">
                  {value.toUpperCase().charAt(0)}
                </p>
                <span className="font-medium">{value}</span>
              </div>
            ),
          },
          { key: "mois_traitement", label: "Mois" },
          { key: "date_debut_brut", label: "Début P.R. Brut" },
          { key: "date_fin_brut", label: "Fin P.R. Brut" },
          { key: "date_debut_anciennete", label: "Début P.R. Ancienneté" },
          { key: "date_fin_anciennete", label: "Fin P.R. Ancienneté" },
        ]}
        data={filteredData}
        onAdd={() => {}}
        onEdit={() => {}}
        onDelete={() => {}}
        onFilter={handleFilter}
        filterPlaceholder="Rechercher par nom ou mois..."
      />
    </div>
  );
}