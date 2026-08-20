import { Calendar, Clock, MapPin, Users, BookOpen, AlertCircle } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Card, CardHeader, CardContent, CardFooter } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";

type Employee = {
  id: number;
  prenom: string;
  nom: string;
  avatar?: string;
};

type TrainingDetails = {
  id: number;
  theme: string;
  description?: string;
  lieu: string;
  dateDebut: string;
  dateFin: string;
  duree?: number;
  progress?: number;
  status?: "Prévue" | "En cours" | "Terminée";
  employes?: Employee[];
};

interface DetailTrainingProps {
  training: TrainingDetails | null;
}

export default function DetailTraining({ training }: DetailTrainingProps) {
  if (!training) {
    return (
      <div className="flex flex-col items-center justify-center h-64 text-center p-6 border rounded-lg bg-muted/50">
        <AlertCircle className="w-8 h-8 text-muted-foreground mb-2" />
        <h3 className="text-lg font-medium text-muted-foreground">
          Aucune formation sélectionnée
        </h3>
        <p className="text-sm text-muted-foreground">
          Sélectionnez une formation pour voir les détails
        </p>
      </div>
    );
  }

  // Calcul du nombre de jours restants
  const today = new Date();
  const endDate = new Date(training.dateFin);
  const daysRemaining = Math.ceil((endDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
  const isPast = daysRemaining < 0;

  return (
    <Card className="overflow-hidden">
      <CardHeader className="bg-gradient-to-r from-primary/10 to-muted/10 p-6 border-b">
        <div className="flex justify-between items-start">
          <div>
            <h3 className="text-2xl font-bold tracking-tight">{training.theme}</h3>
            <p className="text-muted-foreground mt-1">
            </p>
          </div>
          {training.status && (
            <Badge
              variant="outline"
              className={`text-xs py-1 px-3 rounded-full font-medium ${
                training.status === "Prévue"
                  ? "bg-yellow-50 text-yellow-600 border-yellow-200"
                  : training.status === "En cours"
                  ? "bg-blue-50 text-blue-600 border-blue-200"
                  : "bg-green-50 text-green-600 border-green-200"
              }`}
            >
              {training.status}
            </Badge>
          )}
        </div>
      </CardHeader>

      <CardContent className="p-6 space-y-6">
        {/* Section Infos Principales */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">

          <div className="flex items-start gap-3">
            <div className="p-2 bg-primary/10 rounded-lg text-primary">
              <MapPin size={18} />
            </div>
            <div>
              <h4 className="text-sm font-medium text-muted-foreground">Lieu</h4>
              <p className="font-medium">{training.lieu}</p>
            </div>
          </div>

          <div className="flex items-start gap-3">
            <div className="p-2 bg-primary/10 rounded-lg text-primary">
              <Calendar size={18} />
            </div>
            <div>
              <h4 className="text-sm font-medium text-muted-foreground">Dates</h4>
              <p className="font-medium">
                Du {training.dateDebut} au {training.dateFin}
              </p>
              {!isPast && daysRemaining !== 0 && (
                <p className="text-xs text-muted-foreground mt-1">
                  {daysRemaining > 0 
                    ? `${daysRemaining} jour(s) restant(s)` 
                    : "Aujourd'hui"}
                </p>
              )}
            </div>
          </div>

          {training.duree && (
            <div className="flex items-start gap-3">
              <div className="p-2 bg-primary/10 rounded-lg text-primary">
                <Clock size={18} />
              </div>
              <div>
                <h4 className="text-sm font-medium text-muted-foreground">Durée</h4>
                <p className="font-medium">{training.duree} jour(s)</p>
              </div>
            </div>
          )}
          <div className="flex items-start gap-3">
            <div className="p-2 bg-primary/10 rounded-lg text-primary">
              <BookOpen size={18} />
            </div>
            <div>
              <h4 className="text-sm font-medium text-muted-foreground">Description</h4>
              <p className="font-medium">{training.description}</p>
            </div>
          </div>
        </div>

        {/* Liste des employés */}
        {training.employes && training.employes.length > 0 && (
          <div>
            <div className="flex items-center gap-2 mb-3 text-sm font-medium text-muted-foreground">
              <Users size={16} />
              <span>Employés concernés ({training.employes.length})</span>
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
              {training.employes.map((emp) => (
                <div
                  key={emp.id}
                  className="flex items-center gap-3 p-3 rounded-lg border hover:bg-muted/50 transition-colors"
                >
                  {emp.avatar ? (
                    <img
                      src={emp.avatar}
                      alt={`${emp.prenom} ${emp.nom}`}
                      className="w-8 h-8 rounded-full object-cover"
                    />
                  ) : (
                    <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center text-primary font-medium">
                      {emp.prenom.charAt(0)}{emp.nom.charAt(0)}
                    </div>
                  )}
                  <div>
                    <p className="font-medium">
                      {emp.prenom} {emp.nom}
                    </p>
                    <p className="text-xs text-muted-foreground">ID: {emp.id}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </CardContent>

      <CardFooter className="bg-muted/50 p-4 border-t">
        <p className="text-xs text-muted-foreground">
          Dernière mise à jour: {new Date().toLocaleDateString()}
        </p>
      </CardFooter>
    </Card>
  );
}