import {
    AlertCircleIcon,
    CheckCircle2Icon,
    ClockIcon,
    FileTextIcon,
    PauseCircleIcon,
    XCircleIcon,
    Loader2Icon,
    LockIcon,
    TrashIcon,
    EyeIcon,
    RefreshCwIcon,
    HandPlatterIcon,
    HandshakeIcon,
    SmileIcon,
    MailWarningIcon,
    HourglassIcon,
} from "lucide-react";
import { Badge } from "@/components/ui/badge.tsx";

interface StatusBadgeProps {
    status: string;
}

export const StatusBadge = ({ status }: StatusBadgeProps) => {
    const variants: Record<string, { bg: string; text: string; icon: React.ReactNode }> = {
        "Actif": {
            bg: "bg-green-100",
            text: "text-green-800",
            icon: <CheckCircle2Icon className="h-3 w-3" />,
        },
        "Inactif": {
            bg: "bg-gray-200",
            text: "text-gray-700",
            icon: <PauseCircleIcon className="h-3 w-3" />,
        },
        "Suspendu": {
            bg: "bg-yellow-100",
            text: "text-yellow-800",
            icon: <PauseCircleIcon className="h-3 w-3" />,
        },
        "Supprimé": {
            bg: "bg-red-100",
            text: "text-red-800",
            icon: <TrashIcon className="h-3 w-3" />,
        },
        "En attente": {
            bg: "bg-amber-100",
            text: "text-amber-800",
            icon: <ClockIcon className="h-3 w-3" />,
        },
        "Approuvé": {
            bg: "bg-blue-100",
            text: "text-blue-800",
            icon: <CheckCircle2Icon className="h-3 w-3" />,
        },
        "Rejeté": {
            bg: "bg-red-100",
            text: "text-red-800",
            icon: <AlertCircleIcon className="h-3 w-3" />,
        },
        "Brouillon": {
            bg: "bg-muted",
            text: "text-muted-foreground",
            icon: <FileTextIcon className="h-3 w-3" />,
        },
        "Validé": {
            bg: "bg-green-100",
            text: "text-green-800",
            icon: <CheckCircle2Icon className="h-3 w-3" />,
        },
        "Annulé": {
            bg: "bg-red-50",
            text: "text-red-700",
            icon: <XCircleIcon className="h-3 w-3" />,
        },
        "Clôturé": {
            bg: "bg-gray-100",
            text: "text-gray-700",
            icon: <LockIcon className="h-3 w-3" />,
        },
        "Ouvert": {
            bg: "bg-green-50",
            text: "text-green-700",
            icon: <EyeIcon className="h-3 w-3" />,
        },
        "En cours": {
            bg: "bg-blue-50",
            text: "text-blue-700",
            icon: <Loader2Icon className="h-3 w-3 animate-spin" />,
        },
        "Terminé": {
            bg: "bg-green-50",
            text: "text-green-700",
            icon: <CheckCircle2Icon className="h-3 w-3" />,
        },
        "Échoué": {
            bg: "bg-red-100",
            text: "text-red-800",
            icon: <XCircleIcon className="h-3 w-3" />,
        },
        "En pause": {
            bg: "bg-yellow-50",
            text: "text-yellow-700",
            icon: <PauseCircleIcon className="h-3 w-3" />,
        },
        "Expiré": {
            bg: "bg-gray-100",
            text: "text-gray-700",
            icon: <HourglassIcon className="h-3 w-3" />,
        },
        "Renouvelé": {
            bg: "bg-indigo-50",
            text: "text-indigo-700",
            icon: <RefreshCwIcon className="h-3 w-3" />,
        },
        "Temporaire": {
            bg: "bg-orange-100",
            text: "text-orange-800",
            icon: <HourglassIcon className="h-3 w-3" />,
        },
        "Permanent": {
            bg: "bg-green-200",
            text: "text-green-900",
            icon: <HandshakeIcon className="h-3 w-3" />,
        },
        "Congé maladie": {
            bg: "bg-yellow-100",
            text: "text-yellow-900",
            icon: <MailWarningIcon className="h-3 w-3" />,
        },
        "Congé maternité": {
            bg: "bg-pink-100",
            text: "text-pink-900",
            icon: <SmileIcon className="h-3 w-3" />,
        },
        "Congé paternité": {
            bg: "bg-purple-100",
            text: "text-purple-900",
            icon: <SmileIcon className="h-3 w-3" />,
        },
        "Congé sans solde": {
            bg: "bg-gray-200",
            text: "text-gray-900",
            icon: <PauseCircleIcon className="h-3 w-3" />,
        },
        "Congé payé": {
            bg: "bg-green-100",
            text: "text-green-900",
            icon: <HandPlatterIcon className="h-3 w-3" />,
        },
        "Résilié": {
            bg: "bg-red-200",
            text: "text-red-900",
            icon: <TrashIcon className="h-3 w-3" />,
        },
        "Retraité": {
            bg: "bg-slate-200",
            text: "text-slate-900",
            icon: <SmileIcon className="h-3 w-3" />,
        },
        "Bloqué": {
            bg: "bg-red-300",
            text: "text-red-900",
            icon: <LockIcon className="h-3 w-3" />,
        },
        "En révision": {
            bg: "bg-blue-50",
            text: "text-blue-900",
            icon: <FileTextIcon className="h-3 w-3" />,
        },
        "CONTRAT EN COURS": {
            bg: "bg-blue-50",
            text: "text-blue-900",
            icon: <Loader2Icon className="h-3 w-3 animate-spin" />,
        },
        "Soldée": {
            bg: "bg-emerald-100",
            text: "text-emerald-800",
            icon: <CheckCircle2Icon className="h-3 w-3" />,
        },
    };

    const variant = variants[status] ?? {
        bg: "bg-muted",
        text: "text-muted-foreground",
        icon: <FileTextIcon className="h-3 w-3" />,
    };

    return (
        <Badge className={`${variant.bg} ${variant.text} shadow-none gap-1`}>
            {variant.icon}
            {status}
        </Badge>
    );
};
