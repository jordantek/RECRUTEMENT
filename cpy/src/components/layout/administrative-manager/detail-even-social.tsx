import { EvenementSocialType } from "@/types/EvenementSocialType";
import {
    CalendarDays,
    User,
    FileText,
    ActivitySquare,
    BadgeDollarSign,
    StickyNote
} from "lucide-react";

interface DetailEvenSocialProps {
    evenSocial: EvenementSocialType | null;
}

export default function DetailEvenSocial({ evenSocial }: DetailEvenSocialProps) {
    if (!evenSocial) {
        return <p className="text-sm text-muted-foreground">Aucun événement social sélectionné.</p>;
    }

    return (
        <div className="grid grid-cols-1  gap-4 text-sm">
            <DetailItem icon={<User className="w-6 h-6 text-blue-500 p-1 bg-gray-100 rounded" />} label="Employé"
                        value={`${evenSocial.employe?.prenom ?? ""} ${evenSocial.employe?.nom ?? ""}`} />
            <DetailItem icon={<CalendarDays className="w-6 h-6 text-emerald-500 p-1 bg-gray-100 rounded" />} label="Date de l'événement"
                        value={formatDate(evenSocial.dateEvenement)} />
            <DetailItem icon={<FileText className="w-6 h-6 text-indigo-500 p-1 bg-gray-100 rounded" />} label="Désignation"
                        value={evenSocial.designation} />
            <DetailItem icon={<ActivitySquare className="w-6 h-6 text-orange-500 p-1 bg-gray-100 rounded" />} label="Action menée"
                        value={evenSocial.actionMenee ?? "-"} />
            <DetailItem icon={<BadgeDollarSign className="w-6 h-6 text-green-500 p-1 bg-gray-100 rounded" />} label="Montant"
                        value={formatCurrency(evenSocial.montant)} />
            <DetailItem icon={<StickyNote className="w-6 h-6 text-gray-600 p-1 bg-gray-100 rounded" />} label="Observation"
                        value={evenSocial.observation ?? "-"} />
        </div>
    );
}

function DetailItem({ icon, label, value }: { icon: React.ReactNode; label: string; value: string }) {
    return (
        <div className="flex items-start gap-2">
            {icon}
            <div className="flex flex-col">
                <strong className="text-black">{label}</strong>
                <span className="font-semibold text-xs text-gray-600 break-words">{value}</span>
            </div>
        </div>
    );
}

function formatDate(date: string | Date | undefined): string {
    if (!date) return "-";
    const d = new Date(date);
    return !isNaN(d.getTime()) ? d.toLocaleDateString("fr-FR") : "-";
}

function formatCurrency(value?: number): string {
    if (value == null) return "-";
    return new Intl.NumberFormat("fr-FR", {
        style: "currency",
        currency: "XOF",
    }).format(value);
}
