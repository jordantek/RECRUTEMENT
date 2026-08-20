export  interface AccidentTravailType {
    id: number;
    contratEmployeId: number;
    employeId: number;
    employe?: {
        nom: string;
        prenom: string;
    }
    companyId: number;
    dateAccident: string | Date;
    dateDeclaration: string | Date;
    effetAccident: string;
    action: string;
    depense: number;
    addedById: number;
    createdAt: string | Date;
    updatedAt: string | Date;
}