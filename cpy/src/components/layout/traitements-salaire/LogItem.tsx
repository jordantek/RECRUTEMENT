import {
    CheckCircle2Icon,
    EyeIcon,
    RotateCcwIcon,
    XCircleIcon,
    FileTextIcon,
    ClockIcon,
    ShieldCheckIcon,
    AlertCircleIcon,

} from "lucide-react";
import { Button } from "@/components/ui/button";

import {TraitementSalaireType} from "@/pages/app/payroll-management/backup/tabs/save-traitement-salaire-tab.tsx";
import {Badge} from "@/components/ui/badge.tsx";
import {DateHelpers} from "@/helpers/DateHelpers.ts"; // ton badge si déjà stylisé


const formatDate = (dateString: string | null) => {
    if (!dateString) return "N/A";
    return new Date(dateString).toLocaleDateString("fr-FR", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    });
};
type Props = {
    log: TraitementSalaireType;
    onValider?: (log: TraitementSalaireType) => void;
    onRejeter?: (log: TraitementSalaireType) => void;
    onRestaurer?: (log: TraitementSalaireType) => void;
    onApercu?: (log: TraitementSalaireType) => void;
};


const statutStyles = {
    "En attente": {
        icon: <ClockIcon className="h-5 w-5 text-yellow-500" />,
        bg: "bg-yellow-100",
    },
    Vérifié: {
        icon: <ShieldCheckIcon className="h-5 w-5 text-blue-600" />,
        bg: "bg-blue-100",
    },
    Validé: {
        icon: <CheckCircle2Icon className="h-5 w-5 text-green-600" />,
        bg: "bg-green-100",
    },
    Rejeté: {
        icon: <XCircleIcon className="h-5 w-5 text-red-600" />,
        bg: "bg-red-100",
    },
};


const StatusBadge = ({ status }: { status: string }) => {
    const variants = {
        "En attente": { bg: "bg-amber-100", text: "text-amber-800", icon: <ClockIcon className="h-3 w-3" /> },
        "Vérifié": { bg: "bg-blue-100", text: "text-blue-800", icon: <FileTextIcon className="h-3 w-3" /> },
        "Validé": { bg: "bg-green-100", text: "text-green-800", icon: <CheckCircle2Icon className="h-3 w-3" /> },
        "Rejeté": { bg: "bg-red-100", text: "text-red-800", icon: <AlertCircleIcon className="h-3 w-3" /> },
    };

    return (
        <Badge className={`${variants[status].bg} ${variants[status].text} shadow-none gap-1`}>
            {variants[status].icon}
            {status}
        </Badge>
    );
};


export default function LogItem({ log, onValider, onRejeter, onRestaurer, onApercu }: Props) {
    const statut = log.statut;
    const style = statutStyles[statut] ?? statutStyles["En attente"];

    return (
        <div key={log.id} className="bg-gray-100 rounded-lg p-4">
            <div className="flex items-start gap-3">
                <div className={`p-2 rounded-lg ${style.bg} mt-1`}>
                    {style.icon}
                </div>
                <div className="flex-1">
                    <div className="flex justify-between items-start">
                        <h3 className="font-medium">
                            Traitement du mois de {DateHelpers.formatMonth(log.mois)}
                        </h3>
                        <StatusBadge status={statut} />
                    </div>

                {/*    <div className="mt-2 text-sm text-muted-foreground flex items-center gap-2 flex-wrap">
                        <Building2Icon className="h-4 w-4" />
                        <span>{log.companyName}</span>
                        {log.departementName && (
                            <>
                                <span>•</span>
                                <UsersIcon className="h-4 w-4" />
                                <span>{log.departementName}</span>
                            </>
                        )}
                    </div>*/}

                    <div className=" grid gap-2 text-sm">
                        <div className="flex space-x-3">
                            <span className="text-muted-foreground">Date traitement:</span>
                            <span>{formatDate(log.dateTraitement)}</span>
                        </div>

                        {log.message && (
                            <div className="bg-muted/50 p-2 rounded text-sm">
                                <p className="font-medium text-muted-foreground">Message:</p>
                                <p>{log.message}</p>
                            </div>
                        )}

                        {statut === "Rejeté" && log.motifRejet && (
                            <div className="bg-red-50 p-2 rounded text-sm text-red-600">
                                <p className="font-medium">Motif de rejet:</p>
                                <p>{log.motifRejet}</p>
                            </div>
                        )}
                    </div>

                    <div className="flex gap-2 mt-4 flex-wrap justify-end">
                        {log.statut === "En attente" && (
                            <>
                                <Button size="sm" variant="outline" onClick={() => onApercu?.(log)}>
                                    <EyeIcon className="w-4 h-4 mr-1"/>
                                    Aperçu
                                </Button>
                                <Button
                                    size="sm"
                                    className="bg-green-600 text-white hover:bg-green-700"
                                    onClick={() => onValider?.(log)}
                                >
                                    <CheckCircle2Icon className="w-4 h-4 mr-1"/>
                                    Valider
                                </Button>
                                {/*<Button
                                    size="sm"
                                    className="bg-red-600 text-white hover:bg-red-700"
                                    onClick={() => onRejeter?.(log)}
                                >
                                    <BanIcon className="w-4 h-4 mr-1"/>
                                    Rejeter
                                </Button>*/}
                            </>
                        )}

                        {log.statut === "Validé" && (
                            <>
                                <Button size="sm" variant="outline" onClick={() => onApercu?.(log)}>
                                    <EyeIcon className="w-4 h-4 mr-1"/>
                                    Aperçu
                                </Button>
                                <Button
                                    size="sm"
                                    className="bg-yellow-500 text-white hover:bg-yellow-600"
                                    onClick={() => onRestaurer?.(log)}
                                >
                                    <RotateCcwIcon className="w-4 h-4 mr-1"/>
                                    Restaurer
                                </Button>
                            </>
                        )}

                       {/* <Button size="sm" variant="secondary" onClick={() => onApercu?.(log)}>
                            <FileTextIcon className="w-4 h-4 mr-1"/>
                            Voir le détail
                        </Button>*/}
                    </div>

                </div>
            </div>
        </div>
    );
}
