"use client"

import { useEffect, useState } from "react";
import { BookOpen, CalendarCheck, Clock, CheckCircle, AlertCircle, Award, BarChart2 } from "lucide-react";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import StatCard from "@/components/useful/StatCard";
import { Icon } from "@tabler/icons-react";
import PageTitle from "@/components/seo/pageTitle";
import usePageTitleStore from "@/contexts/usePageTitleStore";
import useCompanyStore from "@/contexts/CompanyContext.ts";

// Données fixes
const trainingData = [
  {
    theme: "Leadership",
    date_debut: "2025-06-01",
    date_fin: "2025-06-05",
    duree: 5,
    status: "Terminée",
    formateur: "Institut de Management",
    evaluation: 4.5
  },
  {
    theme: "Communication Interne",
    date_debut: "2025-06-10",
    date_fin: "2025-06-12",
    duree: 3,
    status: "Prévue",
    formateur: "Cabinet RH Conseil",
    evaluation: null
  },
  {
    theme: "Excel Avancé",
    date_debut: "2025-06-08",
    date_fin: "2025-06-09",
    duree: 2,
    status: "En cours",
    formateur: "Centre de Formation Digital",
    evaluation: null
  },
];

export default function EmployeeFormationPage() {
  const [filteredData, setFilteredData] = useState(trainingData);

  useEffect(() => {
    usePageTitleStore.getState().setTitle("Formations", "Mes formations", BookOpen as Icon);
    useCompanyStore.getState().setShowCompanySelect(false);
  }, []);

  const handleFilter = (query: string) => {
    const lowerQuery = query.trim().toLowerCase();
    if (!lowerQuery) {
      setFilteredData(trainingData);
      return;
    }
    const result = trainingData.filter((item) =>
      item.theme?.toLowerCase().includes(lowerQuery) ||
      item.status?.toLowerCase().includes(lowerQuery) ||
      item.formateur?.toLowerCase().includes(lowerQuery)
    );
    setFilteredData(result);
  };

  const completedTrainings = filteredData.filter(t => t.status === "Terminée").length;
  const completionRate = Math.round((completedTrainings / filteredData.length) * 100);
  const upcomingTrainings = filteredData.filter(t => t.status === "Prévue").length;

  return (
    <>
      <PageTitle title="Formations" />
      <div className="p-6 bg-background min-h-[calc(100vh-64px)]">
        <div className="max-w-7xl mx-auto space-y-6">
          {/* Section Statistiques */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
            <StatCard
              icon={BookOpen}
              title="Total Formations"
              value={filteredData.length}
              bgColor="bg-sky-100"
              textColor="text-sky-600"
            />
            <StatCard
              icon={Clock}
              title="En attente"
              value={upcomingTrainings}
              bgColor="bg-yellow-100"
              textColor="text-yellow-600"
            />
            <StatCard
              icon={CheckCircle}
              title="Terminées"
              value={completedTrainings}
              bgColor="bg-green-100"
              textColor="text-green-600"
            />
            <StatCard
              icon={BarChart2}
              title="Taux complétion"
              value={`${completionRate}%`}
              bgColor="bg-purple-100"
              textColor="text-purple-600"
            />
          </div>

          {/* Section Progression */}
          <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-200">
            <div className="flex items-center justify-between mb-2">
              <h3 className="font-medium text-gray-900">Votre progression</h3>
              <span className="text-sm text-blue-600">{completionRate}% complété</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2.5">
              <div 
                className="bg-blue-600 h-2.5 rounded-full" 
                style={{ width: `${completionRate}%` }}
              ></div>
            </div>
          </div>

          {/* Tableau avec en-tête intégré */}
          <div className="bg-white p-4 rounded-lg shadow-sm border border-gray-200">


            <DynamicTable3
              columns={[
                { 
                  key: "theme", 
                  label: "Formation",
                  render: (theme, row) => (
                    <div className="flex flex-col">
                      <span className="font-medium">{theme}</span>
                      <span className="text-xs text-gray-500">{row.formateur}</span>
                    </div>
                  )
                },
                { 
                  key: "date_debut", 
                  label: "Date de début",
                  render: (date_debut) => (
                    <span>{new Date(date_debut).toLocaleDateString()}</span>
                  )
                },
                { 
                  key: "date_fin", 
                  label: "Date de fin",
                  render: (date_fin) => (
                    <span>{new Date(date_fin).toLocaleDateString()}</span>
                  )
                },
                { key: "duree", label: "Durée (jours)" },
                {
                  key: "status",
                  label: "Statut",
                  render: (status: "Prévue" | "En cours" | "Terminée", row) => {
                    const statusMap = {
                      "Prévue": { class: "bg-yellow-50 text-yellow-600", icon: Clock },
                      "En cours": { class: "bg-blue-50 text-blue-600", icon: BookOpen },
                      "Terminée": { class: "bg-green-50 text-green-600", icon: CheckCircle },
                    };
                    const IconComponent = statusMap[status].icon;
                  
                    return (
                      <div className="flex items-center gap-2">
                        <span className={`${statusMap[status].class} py-1 px-3 text-xs rounded-full font-medium`}>
                          {status}
                        </span>
                        {row.evaluation && status === "Terminée" && (
                          <span className="flex items-center text-xs bg-purple-50 text-purple-600 px-2 py-0.5 rounded-full">
                            <Award className="w-3 h-3 mr-1" />
                            {row.evaluation}/5
                          </span>
                        )}
                      </div>
                    );
                  },
                },
              ]}
              data={filteredData}
              onFilter={handleFilter}
              filterPlaceholder="Rechercher par thème, formateur ou statut..."
              onAdd={undefined}
              onEdit={undefined}
              onDelete={undefined}
            />
          </div>

          {/* Section Dernière formation terminée */}
          {completedTrainings > 0 && (
            <div className="bg-blue-50 p-4 rounded-lg border border-blue-100">
              <h3 className="font-medium text-blue-800 mb-2">Dernière formation terminée</h3>
              <div className="flex items-center justify-between">
                <div>
                  <p className="font-medium">
                    {filteredData.find(t => t.status === "Terminée")?.theme}
                  </p>
                  <p className="text-sm text-blue-600">
                    {filteredData.find(t => t.status === "Terminée")?.formateur}
                  </p>
                </div>
                <div className="bg-white px-3 py-1 rounded-full shadow-sm">
                  <span className="text-blue-800 font-medium">
                    {filteredData.find(t => t.status === "Terminée")?.evaluation}/5
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