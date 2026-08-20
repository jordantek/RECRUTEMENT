"use client"

import { useEffect, useState } from "react";
import { AlertTriangle, FileText, Clock, Check, X } from "lucide-react";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import StatCard from "@/components/useful/StatCard";
import { Icon } from "@tabler/icons-react";
import PageTitle from "@/components/seo/pageTitle";
import usePageTitleStore from "@/contexts/usePageTitleStore";
import useCompanyStore from "@/contexts/CompanyContext.ts";

// Données fixes pour les sanctions
const sanctionsData = [
  {
    date_plainte: "2025-05-15",
    plainte: "Retard répété sans justification",
    date_demande_explication: "2025-05-16",
    date_reponse: "2025-05-18",
    sanction: "Avertissement écrit",
    status: "Clôturé"
  },
  {
    date_plainte: "2025-06-02",
    plainte: "Non-respect des procédures de sécurité",
    date_demande_explication: "2025-06-03",
    date_reponse: null,
    sanction: null,
    status: "En cours"
  },
  {
    date_plainte: "2025-04-10",
    plainte: "Absence non justifiée",
    date_demande_explication: "2025-04-11",
    date_reponse: "2025-04-12",
    sanction: "Retenue sur salaire",
    status: "Clôturé"
  },
];

export default function EmployeeSanctionsPage() {
  const [filteredData, setFilteredData] = useState(sanctionsData);

  useEffect(() => {
    usePageTitleStore.getState().setTitle("Sanctions", "Mes sanctions disciplinaires", AlertTriangle as Icon);
    useCompanyStore.getState().setShowCompanySelect(false);
  }, []);

  const handleFilter = (query: string) => {
    const lowerQuery = query.trim().toLowerCase();
    if (!lowerQuery) {
      setFilteredData(sanctionsData);
      return;
    }
    const result = sanctionsData.filter((item) =>
      item.plainte?.toLowerCase().includes(lowerQuery) ||
      item.sanction?.toLowerCase().includes(lowerQuery) ||
      item.status?.toLowerCase().includes(lowerQuery)
    );
    setFilteredData(result);
  };

  const closedCases = filteredData.filter(s => s.status === "Clôturé").length;
  const inProgressCases = filteredData.filter(s => s.status === "En cours").length;
  const warningSanctions = filteredData.filter(s => s.sanction?.includes("Avertissement")).length;

  return (
    <>
      <PageTitle title="Sanctions" />
      <div className="p-6 bg-background min-h-[calc(100vh-64px)]">
        <div className="max-w-7xl mx-auto space-y-6">
          {/* Section Statistiques */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
            <StatCard
              icon={FileText}
              title="Total Plaintes"
              value={filteredData.length}
              bgColor="bg-rose-100"
              textColor="text-rose-600"
            />
            <StatCard
              icon={Clock}
              title="En cours"
              value={inProgressCases}
              bgColor="bg-yellow-100"
              textColor="text-yellow-600"
            />
            <StatCard
              icon={Check}
              title="Clôturées"
              value={closedCases}
              bgColor="bg-green-100"
              textColor="text-green-600"
            />
            <StatCard
              icon={AlertTriangle}
              title="Avertissements"
              value={warningSanctions}
              bgColor="bg-orange-100"
              textColor="text-orange-600"
            />
          </div>

          {/* Section Progression */}
          <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-200">
            <div className="flex items-center justify-between mb-2">
              <h3 className="font-medium text-gray-900">Taux de résolution</h3>
              <span className="text-sm text-blue-600">
                {filteredData.length > 0 ? Math.round((closedCases / filteredData.length) * 100) : 0}% résolu
              </span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2.5">
              <div 
                className="bg-blue-600 h-2.5 rounded-full" 
                style={{ 
                  width: '${filteredData.length > 0 ? Math.round((closedCases / filteredData.length) * 100 : 0}%'
                }}
              ></div>
            </div>
          </div>

          {/* Tableau avec en-tête intégré */}
          <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-200">
            <DynamicTable3
              columns={[
                { 
                  key: "date_plainte", 
                  label: "Date de Plainte",
                  render: (date_plainte) => (
                    <span>{new Date(date_plainte).toLocaleDateString()}</span>
                  )
                },
                { 
                  key: "plainte", 
                  label: "Plainte",
                  render: (plainte) => (
                    <div className="max-w-xs">
                      <p className="line-clamp-2">{plainte}</p>
                    </div>
                  )
                },
                { 
                  key: "date_demande_explication", 
                  label: "Demande Explication",
                  render: (date) => (
                    <span>{date ? new Date(date).toLocaleDateString() : "-"}</span>
                  )
                },
                { 
                  key: "date_reponse", 
                  label: "Date Réponse",
                  render: (date) => (
                    <span>{date ? new Date(date).toLocaleDateString() : "-"}</span>
                  )
                },
                { 
                  key: "sanction", 
                  label: "Sanction",
                  render: (sanction) => (
                    <span>{sanction || "En attente"}</span>
                  )
                },
                {
                  key: "status",
                  label: "Statut",
                  render: (status: "En cours" | "Clôturé") => {
                    const statusMap = {
                      "En cours": { class: "bg-yellow-50 text-yellow-600", icon: Clock },
                      "Clôturé": { class: "bg-green-50 text-green-600", icon: Check },
                    };
                    const IconComponent = statusMap[status].icon;
                  
                    return (
                      <div className="flex items-center gap-2">
                        <span className={`${statusMap[status].class} py-1 px-3 text-xs rounded-full font-medium`}>
                          {status}
                        </span>
                        {status === "Clôturé" && (
                          <span className="flex items-center text-xs bg-blue-50 text-blue-600 px-2 py-0.5 rounded-full">
                            <Check className="w-3 h-3 mr-1" />
                            Traité
                          </span>
                        )}
                      </div>
                    );
                  },
                },
              ]}
              data={filteredData}
              onFilter={handleFilter}
              filterPlaceholder="Rechercher par plainte, sanction ou statut..."
              onAdd={undefined}
              onEdit={undefined}
              onDelete={undefined}
            />
          </div>

          {/* Section Dernière sanction */}
          {closedCases > 0 && (
            <div className="bg-rose-50 p-4 rounded-lg border border-rose-100">
              <h3 className="font-medium text-rose-800 mb-2">Dernière sanction appliquée</h3>
              <div className="flex items-center justify-between">
                <div>
                  <p className="font-medium">
                    {filteredData.find(t => t.status === "Clôturé")?.plainte}
                  </p>
                  <p className="text-sm text-rose-600">
                    {new Date(filteredData.find(t => t.status === "Clôturé")?.date_plainte || "").toLocaleDateString()}
                  </p>
                </div>
                <div className="bg-white px-3 py-1 rounded-full shadow-sm">
                  <span className="text-rose-800 font-medium">
                    {filteredData.find(t => t.status === "Clôturé")?.sanction}
                  </span>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </>
  );
}