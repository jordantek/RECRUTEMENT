// types/AbsenceType.ts
export type AbsenceType = {
    id: number;
    employeId: number;
    employe: {
      nom: string;
      prenom: string;
    };
    typeAbsence: string;
    typeAbsenceId: number;
    contratEmployeId: number;
    modeJouissance: string;
    conditionAcceptation: string;
    dateDebut: string;
    dateFin: string;
    commentaire?: string;
    preuve?: string;
    status: "VALIDEE" | "EN_ATTENTE" | "REFUSEE";
    createdAt: string;
    updatedAt: string;
  };