import { Badge } from "@/components/ui/badge";
import { CalendarDays } from "lucide-react";

// 1. Typage strict des statuts
type AbsenceStatus = "En attente" | "Approuvée" | "Refusée";

// 2. Typage de la structure de chaque demande
interface AbsenceRequest {
    id: number;
    type: string;
    startDate: string;
    endDate: string;
    status: AbsenceStatus;
}

// 3. Définir les couleurs associées aux statuts
const statusColor: Record<AbsenceStatus, string> = {
    "En attente": "bg-yellow-100 text-yellow-800",
    "Approuvée": "bg-green-100 text-green-800",
    "Refusée": "bg-red-100 text-red-800",
};

// 4. Données fictives
const mockRequests: AbsenceRequest[] = [
    {
        id: 1,
        type: "Congé",
        startDate: "2025-06-20",
        endDate: "2025-06-25",
        status: "En attente",
    },
    {
        id: 2,
        type: "Maladie",
        startDate: "2025-05-15",
        endDate: "2025-05-17",
        status: "Approuvée",
    },
    {
        id: 3,
        type: "Autre",
        startDate: "2025-04-10",
        endDate: "2025-04-12",
        status: "Refusée",
    },
];

// 5. Composant principal
export default function AbsenceRequestList() {
    return (
        <div className="space-y-4">
            {mockRequests.map((req) => (
                <div
                    key={req.id}
                    className="p-4 bg-white rounded-md shadow flex items-center justify-between"
                >
                    <div className="flex items-center gap-4">
                        <CalendarDays className="text-blue-900" size={24} />
                        <div>
                            <p className="text-sm font-medium text-gray-900">{req.type}</p>
                            <p className="text-xs text-gray-600">
                                {req.startDate} → {req.endDate}
                            </p>
                        </div>
                    </div>
                    <Badge
                        className={`text-xs font-semibold ${statusColor[req.status]}`}
                    >
                        {req.status}
                    </Badge>
                </div>
            ))}
        </div>
    );
}
