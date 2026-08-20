import { Button } from "@/components/ui/button.tsx";
import { FileText, FileSpreadsheet, X, Printer, User } from "lucide-react";
import { useEffect, useState } from "react";
import { MonthYearPicker } from "@/components/inputs/MonthYearPicker.tsx";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3.tsx";
import { format, subMonths } from "date-fns";
import { DollarSign } from "lucide-react";

interface CsRisqueType {
    id: number;
    nomAgent: string;
    salaireBrut: number;
    cnssEmploye: string;
    cnssEmployeur: string;
}

export default function MensuelCsRisqueTab() {
    const [loadingPage, setLoadingPage] = useState(false);
    const [data, setData] = useState<CsRisqueType[]>([]);
    const [selectedDate, setSelectedDate] = useState<string>(format(subMonths(new Date(), 1), "yyyy-MM"));

    // Données fictives avec numéros CNSS à 10 chiffres
    const mockData: CsRisqueType[] = [
        {
            id: 1,
            nomAgent: "Jean Dupont",
            salaireBrut: 450000,
            cnssEmploye: "1234567890",
            cnssEmployeur: "0987654321"
        },
        {
            id: 2,
            nomAgent: "Marie Lambert",
            salaireBrut: 380000,
            cnssEmploye: "2345678901",
            cnssEmployeur: "9876543210"
        },
        {
            id: 3,
            nomAgent: "Paul Martin",
            salaireBrut: 520000,
            cnssEmploye: "3456789012",
            cnssEmployeur: "8765432109"
        },
    ];

    const fetchData = async () => {
        if (!selectedDate) return;
        
        setLoadingPage(true);
        await new Promise(resolve => setTimeout(resolve, 800));
        setData(mockData);
        setLoadingPage(false);
    };

    const handleDateChange = (date: string | null) => {
        setSelectedDate(date ? format(date, "yyyy-MM") : format(subMonths(new Date(), 1), "yyyy-MM"));
    };

    useEffect(() => {
        fetchData();
    }, [selectedDate]);

    return (
        <>
            <div className="flex items-center justify-between mb-4">
                <div>
                    <h2 className="text-lg font-semibold">Mensuel CS par risque professionnel</h2>
                    <p className="text-xs font-medium text-muted-foreground">
                        Cotisations sociales par agent
                    </p>
                </div>
            </div>

            <div className="flex items-center justify-between mb-2">
                <div className="flex items-center justify-start space-x-4">
                    <MonthYearPicker
                        value={selectedDate}
                        onChange={handleDateChange}
                        placeholder="Choisir un mois"
                    />

                    {selectedDate && (
                        <Button
                            variant="outline"
                            size="sm"
                            onClick={() => {
                                setSelectedDate(format(subMonths(new Date(), 1), "yyyy-MM"));
                                setData([]);
                            }}
                            className="flex items-center gap-1"
                            aria-label="Réinitialiser"
                        >
                            <X className="h-4 w-4"/>
                        </Button>
                    )}
                </div>

                <div className={"flex items-center justify-end space-x-4"}>
                    <Button
                        variant="outline"
                        size="sm"
                        className="text-blue-600 border-blue-600 hover:bg-blue-600 hover:text-white"
                        onClick={() => window.print()}
                    >
                        <Printer className="h-4 w-4 mr-2"/>
                        Imprimer
                    </Button>
                    
                    <Button
                        variant="outline"
                        size="sm"
                        className="text-red-600 border-red-600 hover:bg-red-600 hover:text-white"
                    >
                        <FileText className="h-4 w-4 mr-2"/>
                        Export PDF
                    </Button>

                    <Button
                        variant="outline"
                        size="sm"
                        className="text-green-600 border-green-600 hover:bg-green-600 hover:text-white"
                    >
                        <FileSpreadsheet className="h-4 w-4 mr-2"/>
                        Export Excel
                    </Button>
                </div>
            </div>

            {selectedDate && (
                <div>
                    <DynamicTable3
                        columns={[
                            {
                                key: "nomAgent",
                                label: "Nom de l'agent",
                                render: (value) => (
                                    <div className="flex items-center gap-2">
                                        <div className="w-8 h-8 flex items-center justify-center rounded-full border text-sm font-medium bg-muted text-muted-foreground">
                                            <User className="h-4 w-4" />
                                        </div>
                                        <span className="font-medium">{value}</span>
                                    </div>
                                )
                            },
                            {
                                key: "salaireBrut",
                                label: "Salaire brut",
                                render: (value) => (
                                    <div className="flex items-center gap-2">
                                        <DollarSign className="h-4 w-4 text-green-500"/>
                                        {value?.toLocaleString()} FCFA
                                    </div>
                                )
                            },
                            {
                                key: "cnssEmploye",
                                label: "CNSS Employé",
                                render: (value) => (
                                    <div className="font-mono">
                                        {value}
                                    </div>
                                )
                            },
                            {
                                key: "cnssEmployeur",
                                label: "CNSS Employeur",
                                render: (value) => (
                                    <div className="font-mono">
                                        {value}
                                    </div>
                                )
                            }
                        ]}
                        data={data}
                        isLoading={loadingPage}
                    />
                </div>
            )}
        </>
    );
}