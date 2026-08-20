import {
  Calendar,
  FileText,
  MapPin,
  User,
  AlertCircle,
  Clock,
  Settings2,
  Repeat,
} from "lucide-react";
import {
  Card,
  CardHeader,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { AbsenceType } from "@/types/AbsenceType";

interface AbsenceDetailType extends AbsenceType {
  employeNomComplet?: string;
  typeAbsenceLabel?: string;
}

interface Props {
  absence: AbsenceDetailType | null;
}

export default function DetailAbsence({ absence }: Props) {
  const formatDate = (dateStr?: string) =>
    dateStr ? new Date(dateStr).toLocaleDateString() : "N/A";

  const formatConditionAcceptation = (condition?: string) => {
    if (!condition) return "Non spécifié";
    
    const conditionsMap: Record<string, string> = {
      A_DEDUIRE_DES_CONGES: "Déduire des congés",
      A_DEDUIRE_DU_SALAIRE_DE_PRESENCE: "Déduire du salaire",
      SANS_CONDITION: "Sans condition"
    };

    return conditionsMap[condition] || condition.replace(/_/g, ' ');
  };

  const formatModeJouissance = (mode?: string) => {
    if (!mode) return "Non spécifié";
    
    const modesMap: Record<string, string> = {
      NUMERAIRE: "Numéraire",
      REEL: "Réel",
      DIFFERE: "Différé",
      EPARGNE: "Épargne"
    };

    return modesMap[mode] || mode;
  };

  if (!absence) {
    return (
      <div className="flex flex-col items-center justify-center h-64 text-center p-6 border rounded-lg bg-muted/50">
        <AlertCircle className="w-8 h-8 text-muted-foreground mb-2" />
        <h3 className="text-lg font-medium text-muted-foreground">
          Aucune absence sélectionnée
        </h3>
        <p className="text-sm text-muted-foreground">
          Sélectionnez une absence pour voir les détails
        </p>
      </div>
    );
  }

  return (
    <Card>
      <CardHeader className="bg-muted/10 border-b">
        <h3 className="text-xl font-semibold">Détails de l'absence</h3>
      </CardHeader>

      <CardContent className="space-y-6 py-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* Employé */}
          <div className="flex items-start gap-3">
            <div className="p-2 bg-primary/10 rounded-lg text-primary">
              <User size={18} />
            </div>
            <div>
              <h4 className="text-sm font-medium text-muted-foreground">
                Employé
              </h4>
              <p className="font-medium">
                {absence.employeNomComplet ?? "Non renseigné"}
              </p>
            </div>
          </div>

          {/* Type d'absence */}
          <div className="flex items-start gap-3">
            <div className="p-2 bg-primary/10 rounded-lg text-primary">
              <FileText size={18} />
            </div>
            <div>
              <h4 className="text-sm font-medium text-muted-foreground">
                Type d'absence
              </h4>
              <p className="font-medium">
                {absence.typeAbsenceLabel ?? "Non renseigné"}
              </p>
            </div>
          </div>

          {/* Dates */}
          <div className="flex items-start gap-3">
            <div className="p-2 bg-primary/10 rounded-lg text-primary">
              <Calendar size={18} />
            </div>
            <div>
              <h4 className="text-sm font-medium text-muted-foreground">
                Dates
              </h4>
              <p className="font-medium">
                Du {formatDate(absence.dateDebut)} au{" "}
                {formatDate(absence.dateFin)}
              </p>
            </div>
          </div>

          {/* Durée */}
          <div className="flex items-start gap-3">
            <div className="p-2 bg-primary/10 rounded-lg text-primary">
              <Clock size={18} />
            </div>
            <div>
              <h4 className="text-sm font-medium text-muted-foreground">
                Durée
              </h4>
              <p className="font-medium">{absence.duree ?? "Non calculée"} jour(s)</p>
            </div>
          </div>

          {/* Mode de jouissance */}
          <div className="flex items-start gap-3">
            <div className="p-2 bg-primary/10 rounded-lg text-primary">
              <Repeat size={18} />
            </div>
            <div>
              <h4 className="text-sm font-medium text-muted-foreground">
                Mode de jouissance
              </h4>
              <p className="font-medium">
                {formatModeJouissance(absence.modeJouissance)}
              </p>
            </div>
          </div>

          {/* Condition d'acceptation */}
          <div className="flex items-start gap-3">
            <div className="p-2 bg-primary/10 rounded-lg text-primary">
              <Settings2 size={18} />
            </div>
            <div>
              <h4 className="text-sm font-medium text-muted-foreground">
                Condition d'acceptation
              </h4>
              <p className="font-medium">
                {formatConditionAcceptation(absence.conditionAcceptation)}
              </p>
            </div>
          </div>

          {/* Commentaire */}
          <div className="flex items-start gap-3 md:col-span-2">
            <div className="p-2 bg-primary/10 rounded-lg text-primary">
              <MapPin size={18} />
            </div>
            <div>
              <h4 className="text-sm font-medium text-muted-foreground">
                Commentaire
              </h4>
              <p className="font-medium">{absence.commentaire || "Aucun"}</p>
            </div>
          </div>
        </div>
      </CardContent>

      <CardFooter className="border-t text-xs text-muted-foreground p-4">
        Dernière mise à jour : {new Date().toLocaleDateString()}
      </CardFooter>
    </Card>
  );
}