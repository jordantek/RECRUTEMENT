

import {
    User,
    CalendarDays,
    Calculator,
    CircleDollarSign,
    AlertCircle,
} from "lucide-react";
import {HeureSupplementaireType} from "@/pages/app/payroll-management/salary-accessory/tabs/overtime-tab.tsx";


interface OvertimeDetailProps {
    overtime: HeureSupplementaireType | null;
}

export default function OvertimeDetail({ overtime }: OvertimeDetailProps) {
    if (!overtime)
        return <p className="text-sm text-muted-foreground">Aucune donnée sélectionnée.</p>;

    return (
        <div className="grid grid-cols-1 gap-4 text-sm">
            <DetailItem
                icon={<User className="w-6 h-6 text-blue-500 p-1 bg-gray-100 rounded" />}
                label="Employé"
                value={`${overtime.employe?.nom} ${overtime.employe?.prenom}`}
            />
            <DetailItem
                icon={<CalendarDays className="w-6 h-6 text-purple-600 p-1 bg-gray-100 rounded" />}
                label="Mois"
                value={formatMonth(overtime.mois)}
            />
            <DetailItem
                icon={<Calculator className="w-6 h-6 text-yellow-600 p-1 bg-gray-100 rounded" />}
                label="Total heures majorées"
                value={`${overtime.totalHeures} h`}
            />
            <DetailItem
                icon={<CircleDollarSign className="w-6 h-6 text-green-600 p-1 bg-gray-100 rounded" />}
                label="Montant total"
                value={formatCurrency(overtime.montant)}
            />
            <DetailItem
                icon={<AlertCircle className="w-6 h-6 text-rose-500 p-1 bg-gray-100 rounded" />}
                label="Observation"
                value={overtime.observation}
            />
        </div>
    );
}

function DetailItem({
                        icon,
                        label,
                        value,
                    }: {
    icon: React.ReactNode;
    label: string;
    value: string;
}) {
    return (
        <div className="flex items-start gap-2">
            {icon}
            <div className="flex flex-col">
                <strong className="text-black">{label}</strong>
                <span className="font-semibold text-xs text-gray-600 break-words">
          {value || "-"}
        </span>
            </div>
        </div>
    );
}

function formatCurrency(value: number | null | undefined): string {
    if (value == null) return "-";
    return new Intl.NumberFormat("fr-FR", {
        style: "currency",
        currency: "XOF",
    }).format(value);
}

function formatMonth(isoMois: string | undefined): string {
    if (!isoMois) return "-";
    const date = new Date(isoMois + "-01");
    return !isNaN(date.getTime())
        ? date.toLocaleDateString("fr-FR", { month: "long", year: "numeric" })
        : "-";
}
