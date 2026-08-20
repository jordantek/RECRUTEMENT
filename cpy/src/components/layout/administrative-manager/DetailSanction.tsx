import { SanctionType } from "@/types/SanctionType";
import {
    User,
    FileWarning,
    CalendarDays,
    PenLine,
    ClipboardList,
    MessageSquareWarning,
} from "lucide-react";

interface DetailSanctionProps {
    sanction: SanctionType | null;
}

export default function DetailSanction({ sanction }: DetailSanctionProps) {
    if (!sanction) {
        return (
            <p className="text-sm text-muted-foreground">
                Aucune sanction sélectionnée.
            </p>
        );
    }

    return (
        <div className="grid grid-cols-1 gap-4 text-sm">
            <DetailItem
                icon={<User className="w-6 h-6 text-blue-500 p-1 bg-gray-100 rounded" />}
                label="Employé"

                value={`${sanction.employe?.prenom ?? ""} ${sanction.employe?.nom ?? ""}`}
            />
            <DetailItem
                icon={<CalendarDays className="w-6 h-6 text-green-600 p-1 bg-gray-100 rounded" />}
                label="Date de la plainte"
                value={formatDate(sanction.datePlainte)}
            />
            <DetailItem
                icon={<ClipboardList className="w-6 h-6 text-yellow-600 p-1 bg-gray-100 rounded" />}
                label="Demande d'explication"
                value={formatDate(sanction.dateDemandeExplication)}
            />
            <DetailItem
                icon={<CalendarDays className="w-6 h-6 text-purple-600 p-1 bg-gray-100 rounded" />}
                label="Réponse reçue le"
                value={formatDate(sanction.dateReponse)}
            />
            <DetailItem
                icon={<FileWarning className="w-6 h-6 text-rose-600 p-1 bg-gray-100 rounded" />}
                label="Contenu de la plainte"
                value={sanction.contenuePlainte}
            />
            <DetailItem
                icon={<PenLine className="w-6 h-6 text-indigo-600 p-1 bg-gray-100 rounded" />}
                label="Sanction appliquée"
                value={sanction.sanctionDonnee}
            />
            <DetailItem
                icon={<MessageSquareWarning className="w-6 h-6 text-gray-600 p-1 bg-gray-100 rounded" />}
                label="Observation"
                value={sanction.observation}
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
        <div className="flex items-start space-x-3">
            <div className="w-8 h-8 flex items-center justify-center rounded bg-gray-100">
                {icon}
            </div>
            <div className="flex flex-col">
                <strong className="text-black">{label}</strong>
                <span className="font-semibold text-xs text-gray-600 break-words">
                  {value || "-"}
                </span>
            </div>
        </div>
    );
}

function formatDate(date: string | Date | null | undefined): string {
    if (!date) return "-";
    const d = new Date(date);
    return !isNaN(d.getTime())
        ? d.toLocaleDateString("fr-FR", {
            year: "numeric",
            month: "long",
            day: "numeric",
        })
        : "-";
}
