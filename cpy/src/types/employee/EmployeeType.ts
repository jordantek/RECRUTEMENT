export type EmployeeType = {
    id: number;
    matricule: string;
    titre: string;
    nom: string;
    prenom: string;
    date_naissance: string;
    lieu_naissance: string;
    sexe: string;
    situationMatrimoniale: string;
    numero_ifu: string;
    telephone: string;
    email: string;
    nom_pere: string | null;
    nom_mere: string | null;
    boite_postale: string | null;
    maison: string | null;
    numeroCarre: string | null;
    quartier: string;
    nationalite: string;
    numero_cnss: string | null;
    profession: string;
    is_employe_interne: boolean | null;
    added_by: number | null;
    created_at: string;
    updated_by: number;
    updated_at: string;
    deleted_at: string | null;
};


export type EmployeeCategory = {
    id: number;
    name: string;
    description: string;
    createdAt: string | null;
    updatedAt: string | null;
    deletedAt: string | null;
  };
  

export type EnfantType = {
    id: number;
    employeId: number | null;
    nom: string;
    prenom: string;
    sexe: "MASCULIN" | "FÉMININ";
    dateNaissance: string; // format ISO date string, ex: "2015-06-12"
    lieuNaissance: string;
    estDecede: boolean;
};

export type PersonneAPrevenirType = {
    id: number;
    nomPrenom: string;
    telephone: string;
    adresse: string;
    email: string;
    lienParenteLibelle: string;
};

export type EmployeeActifType = {
    nom: string;
    prenom: string;
    employeId: number;
    contratEmployeId?: number;  // Optionnel
    matricule: string;
    companyId: number;
};