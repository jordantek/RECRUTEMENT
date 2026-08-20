import { AccidentTravailType } from "@/types/AccidentTravail.ts";
import {
    User,
    CalendarDays,
    Landmark,
    Stethoscope,
    AlertTriangle
} from "lucide-react";

interface DetailWorkAccidentProps {
    workAccident: AccidentTravailType | null;
}

export default function DetailWorkAccident({ workAccident }: DetailWorkAccidentProps) {
    if (!workAccident)
        return <p className="text-sm text-muted-foreground">Aucun accident sélectionné.</p>;

    return (
        <div className="grid grid-cols-1 gap-4 text-sm">
            <DetailItem
                icon={<User className="w-6 h-6 text-blue-500 p-1 bg-gray-100 rounded" />}
                label="Employé"
                value={`${workAccident.employe?.nom} ${workAccident.employe?.prenom}`}
            />
            <DetailItem
                icon={<Landmark className="w-6 h-6 text-green-600 p-1 bg-gray-100 rounded" />}
                label="Dépenses liées"
                value={formatCurrency(workAccident.depense)}
            />
            <DetailItem
                icon={<CalendarDays className="w-6 h-6 text-orange-500 p-1 bg-gray-100 rounded" />}
                label="Date de l'accident"
                value={formatDate(workAccident.dateAccident)}
            />
            <DetailItem
                icon={<CalendarDays className="w-6 h-6 text-purple-600 p-1 bg-gray-100 rounded" />}
                label="Date déclaration CNSS"
                value={formatDate(workAccident.dateDeclaration)}
            />
            <DetailItem
                icon={<AlertTriangle className="w-6 h-6 text-rose-600 p-1 bg-gray-100 rounded" />}
                label="Effets de l'accident"
                value={workAccident.effetAccident}
            />
            <DetailItem
                icon={<Stethoscope className="w-6 h-6 text-indigo-600 p-1 bg-gray-100 rounded" />}
                label="Action corrective"
                value={workAccident.action}
            />
        </div>
    );
}

function DetailItem({
                        icon,
                        label,
                        value
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
                <span className="font-semibold text-xs text-gray-600 break-words">{value || "-"}</span>
            </div>
        </div>
    );
}

function formatDate(date: string | Date | undefined): string {
    if (!date) return "-";
    const d = new Date(date);
    return !isNaN(d.getTime()) ? d.toLocaleDateString("fr-FR") : "-";
}

function formatCurrency(value: number | null | undefined): string {
    if (value == null) return "-";
    return new Intl.NumberFormat("fr-FR", {
        style: "currency",
        currency: "XOF"
    }).format(value);
}
