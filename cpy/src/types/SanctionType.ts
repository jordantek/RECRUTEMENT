export type SanctionType = {
    id: number;
    employeId: number;
    employe:{
      nom:string;
      prenom:string;
    };
    contratEmployeId: number;
    companyId: number;
    datePlainte: string;
    contenuePlainte: string;
    dateDemandeExplication: string;
    dateReponse: string;
    sanctionDonnee: string;
    observation: string;
    addedById: number;
    createdAt: string;
    updatedById: number | null;
    updatedAt: string;
    deletedAt: string | null;
};