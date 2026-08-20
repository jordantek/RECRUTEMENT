import { Button } from "@/components/ui/button.tsx";
import { FileText, FileSpreadsheet, X, Printer, User } from "lucide-react";
import { useEffect, useState } from "react";
import { MonthYearPicker } from "@/components/inputs/MonthYearPicker.tsx";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3.tsx";
import { format, subMonths } from "date-fns";
import { DollarSign, CircleDollarSign } from "lucide-react";

interface MensuelCfType {
    id: number;
    nomAgent: string;
    salaireBrut: number;
    salaireBrutArrondi: number;
    ipts: number;
    vps: number;
}

export default function MensuelCfTab() {
    const [loadingPage, setLoadingPage] = useState(false);
    const [data, setData] = useState<MensuelCfType[]>([]);
    const [selectedDate, setSelectedDate] = useState<string>(format(subMonths(new Date(), 1), "yyyy-MM"));

    // Données fictives
    const mockData: MensuelCfType[] = [
        {
            id: 1,
            nomAgent: "Jean Dupont",
            salaireBrut: 450000,
            salaireBrutArrondi: 450000,
            ipts: 22500,
            vps: 9000
        },
        {
            id: 2,
            nomAgent: "Marie Lambert",
            salaireBrut: 382500,
            salaireBrutArrondi: 383000,
            ipts: 19150,
            vps: 7660
        },
        {
            id: 3,
            nomAgent: "Paul Martin",
            salaireBrut: 520750,
            salaireBrutArrondi: 521000,
            ipts: 26050,
            vps: 10420
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

    // Calcul des totaux
    const totalBrut = data.reduce((acc, curr) => acc + curr.salaireBrut, 0);
    const totalIpts = data.reduce((acc, curr) => acc + curr.ipts, 0);
    const totalVps = data.reduce((acc, curr) => acc + curr.vps, 0);

    return (
        <>
            <div className="flex items-center justify-between mb-4">
                <div>
                    <h2 className="text-lg font-semibold">Mensuel CF</h2>
                    <p className="text-xs font-medium text-muted-foreground">
                        Cotisation fiscale mensuelle
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
                <>
                    <div className="grid grid-cols-3 gap-4 mb-4">
                        <div className="bg-muted p-3 rounded-lg ">
                            <p className="text-sm text-muted-foreground">Total Salaire Brut</p>
                            <p className="font-semibold">
                                {totalBrut.toLocaleString()} FCFA
                            </p>
                        </div>
                        <div className="bg-muted p-3 rounded-lg ">
                            <p className="text-sm text-muted-foreground">Total IPTS</p>
                            <p className="font-semibold">
                                {totalIpts.toLocaleString()} FCFA
                            </p>
                        </div>
                        <div className="bg-muted p-3 rounded-lg ">
                            <p className="text-sm text-muted-foreground">Total VPS</p>
                            <p className="font-semibold">
                                {totalVps.toLocaleString()} FCFA
                            </p>
                        </div>
                    </div>

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
                                    key: "salaireBrutArrondi",
                                    label: "Salaire brut arrondi",
                                    render: (value) => (
                                        <div className="flex items-center gap-2">
                                            <CircleDollarSign className="h-4 w-4 text-blue-500"/>
                                            {value?.toLocaleString()} FCFA
                                        </div>
                                    )
                                },
                                {
                                    key: "ipts",
                                    label: "IPTS",
                                    render: (value) => (
                                        <div className="text-orange-600">
                                            {value?.toLocaleString()} FCFA
                                        </div>
                                    )
                                },
                                {
                                    key: "vps",
                                    label: "VPS",
                                    render: (value) => (
                                        <div className="text-purple-600">
                                            {value?.toLocaleString()} FCFA
                                        </div>
                                    )
                                }
                            ]}
                            data={data}
                            isLoading={loadingPage}
                        />
                    </div>
                </>
            )}
        </>
    );
}