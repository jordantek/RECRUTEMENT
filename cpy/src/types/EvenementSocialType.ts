export interface EvenementSocialType {
    id: number;
    employeId: number;
    contratEmployeId: number;
    companyId: number;
    dateEvenement: Date | string;
    designation: string;
    actionMenee?: string;
    montant?: number;
    observation?: string;
    createdAt?: Date | string;
    updatedAt?: Date | string;
    // Relations optionnelles (si vous les utilisez)
    employe?: {
        nom: string;
        prenom: string;
    };
    company?: {
        nom: string;
    };
}