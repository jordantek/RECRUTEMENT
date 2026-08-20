import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";

import { Button } from "@/components/ui/button";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import FormModal from "@/components/useful/form-modal";
import { MultiEmployeeSelect } from "@/components/ui/multi-employee-select";
import { Badge } from "@/components/ui/badge";
import { X, UserPlus2 } from "lucide-react";
import { Icon } from "@tabler/icons-react";

import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";
import useCompanyStore from "@/contexts/CompanyContext";
import useEmployeeStore from "@/contexts/useEmployeeStore";

type CreditConge = {
  id: number;
  employeId: number;
  nomComplet: string;
  dateReference: string;
  statut: string;
};

type CreditCongeForm = {
  dateReference: string;
};

const creditCongeSchema = z.object({
  dateReference: z.coerce.date({
    required_error: "La date de référence est requise.",
    invalid_type_error: "Format de date invalide.",
  }),
});

export default function CreditCongeTab() {
  const { user } = useAuth();
  const { selectedCompany } = useCompanyStore();
  const { employees, fetchEmployees } = useEmployeeStore();
  const [credits, setCredits] = useState<CreditConge[]>([]);
  const [selectedCredit, setSelectedCredit] = useState<CreditConge | null>(null);
  const [filteredData, setFilteredData] = useState<CreditConge[]>([]);
  const [loading, setLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedEmployeeIds, setSelectedEmployeeIds] = useState<number[]>([]);

  const form = useForm<CreditCongeForm>({
    resolver: zodResolver(creditCongeSchema),
    defaultValues: {},
  });

  useEffect(() => {
    if (selectedCompany && user) {
      fetchEmployees(selectedCompany, user);
    }
  }, [selectedCompany, user]);

  const fetchCredits = async () => {
    if (!selectedCompany?.id) return;

    setLoading(true);
    try {
      const response = await apiService.get(
        {
          url: apiRoutes.admin.app.employee.creditConge.listByCompany(
            selectedCompany.id
          ),
        },
        {
          userToken: `${user?.type} ${user?.token}`,
          hasNoSuccessModal: true,
        }
      );

      const data = response.data || [];
      const formatted: CreditConge[] = data.map((item: any) => ({
        id: item.id,
        employeId: item.employeId,
        nomComplet: `${item.employeDTO?.prenom ?? ""} ${
          item.employeDTO?.nom ?? ""
        }`,
        dateReference: item.dateReference,
        statut: item.statut,
      }));

      setCredits(formatted);
      setFilteredData(formatted);
    } catch (error) {
      console.error("Erreur lors du chargement des crédits de congé:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCredits();
  }, [selectedCompany?.id]);

  const handleEditCredit = (credit: CreditConge) => {
    setSelectedCredit(credit);
  
    form.reset({
      //dateReference: new Date(credit.dateReference),
    });
  
    setSelectedEmployeeIds([credit.employeId]); // Un seul employé ici
    setIsModalOpen(true);
  };
  

  const handleFilter = (query: string) => {
    const lower = query.trim().toLowerCase();
    if (!lower) return setFilteredData(credits);
    setFilteredData(
      credits.filter((c) => c.nomComplet.toLowerCase().includes(lower))
    );
  };

  const handleSubmitCredit = async (values: CreditCongeForm) => {
    if (!selectedCompany?.id || selectedEmployeeIds.length === 0) return;
  
    const dateFormatted = new Date(values.dateReference).toISOString().slice(0, 10);
  
    try {
      if (selectedCredit) {
        const employee = employees.find((e) => e.employeId === selectedEmployeeIds[0]);
        if (!employee?.contratEmployeId) return;
  
        const payload = {
          contratEmployeId: employee.contratEmployeId,
          dateReference: dateFormatted,
        };
  
        await apiService.put(
          {
            url: `${apiRoutes.admin.app.employee.creditConge.update}${selectedCredit.id}`,
            body: payload,
          },
          {
            userToken: `${user?.type} ${user?.token}`,
            hasNoSuccessModal: false,
          }
        );
      } else {
        // ➕ Ajout
        for (const employeeId of selectedEmployeeIds) {
          const employee = employees.find((e) => e.employeId === employeeId);
          if (!employee?.contratEmployeId) continue;
  
          const payload = {
            contratEmployeId: employee.contratEmployeId,
            dateReference: dateFormatted,
          };
  
          await apiService.post(
            {
              url: apiRoutes.admin.app.employee.creditConge.create,
              body: payload,
            },
            {
              userToken: `${user?.type} ${user?.token}`,
              hasNoSuccessModal: false,
            }
          );
        }
      }
  
      setIsModalOpen(false);
      form.reset();
      setSelectedEmployeeIds([]);
      setSelectedCredit(null);
      fetchCredits();
    } catch (error) {
      console.error("Erreur lors de la soumission du crédit de congé:", error);
    }
  };
  

  const creditCongeFields = [
    {
      tag: "employees",
      label: "Employés concernés",
      input_type: "custom",
      size: "col-span-12",
      required: true,
    },
    {
      tag: "dateReference",
      label: "Date de référence",
      input_type: "date",
      size: "col-span-12",
      required: true,
    },
  ];

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
                    <span>{`${employee?.prenom} ${employee?.nom}`}</span>
                    <button
                      onClick={() =>
                        setSelectedEmployeeIds((prev) =>
                          prev.filter((eId) => eId !== id)
                        )
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

  return (
    <div className="space-y-4">
      <FormModal
      icon={UserPlus2 as Icon}
      title={selectedCredit ? "Modifier un crédit de congé" : "Ajouter un crédit de congé"}
      description={
        selectedCredit
          ? "Modifiez la date de référence pour l'employé sélectionné."
          : "Sélectionnez les employés concernés et une date de référence."
      }
      isOpen={isModalOpen}
      setIsOpen={setIsModalOpen}
      form={form}
      fields={creditCongeFields}
      customRenders={customRenders}
      onSubmit={handleSubmitCredit}
      onClose={() => {
        setIsModalOpen(false);
        form.reset();
        setSelectedEmployeeIds([]);
        setSelectedCredit(null);
      }}
    />


      <div className="flex items-center justify-between px-4">
        <div className="text-left">
          <h2 className="text-lg font-semibold text-black">Enregistrement de date ref. congé</h2>
          <p className="text-xs text-muted-foreground">
            Date de référence de congés par employé.
          </p>
        </div>
        <Button
          size="sm"
          className="bg-primary text-white hover:bg-primary/80"
          onClick={() => setIsModalOpen(true)}
        >
          <UserPlus2 className="h-4 w-4 mr-2" />
          Ajouter une date
        </Button>
      </div>

      <div className="px-4">
        <DynamicTable3
          columns={[
            { key: "nomComplet", label: "Employé" },
            { key: "dateReference", label: "Date de référence" },
            {
              key: "statut",
              label: "Statut",
              render: (value: string) => {
                const isActif = value === "ACTIF";
                const badgeStyle = isActif
                  ? "bg-green-100 text-green-700 border-green-200"
                  : "bg-red-100 text-red-700 border-red-200";

                return (
                  <span
                    className={`text-xs px-2 py-0.5 rounded-full border ${badgeStyle}`}
                  >
                    {value}
                  </span>
                );
              },
            },
          ]}
          data={filteredData}
          isLoading={loading}
          onFilter={handleFilter}
          filterPlaceholder="Rechercher par nom..."
          onAdd={() => setIsModalOpen(true)}
          onEdit={handleEditCredit}
          onDelete={() => {}}
        />
      </div>
    </div>
  );
}
