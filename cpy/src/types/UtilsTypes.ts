export type VatRate = {
    id: number;
    rate: number;
    description: string;
};

export type ActivityAreaType = {
    id: number;
    name: string;
    description: string;
    createdAt: string;
    updatedAt: string;
    deletedAt: string | null;
};

export type DepartmentType =  {
    id: number;
    libelle: string;
    description: string | null;
    company_id?: number | null;
    added_by: string | null;
    createdAt: string; // ou `Date` si tu veux le convertir en objet Date
    updatedAt: string | null; // ou `Date` si tu veux le convertir en objet Date
}

export type PosteType = {
    id: number;
    libelle: string;
    description: string | null;
};

export type NatureContratType = {
    id: number
    libelle: string
    description: string | null
    added_by: string | number | null // dépend du type de l'ID utilisateur dans ton système
    created_at: string // ou `Date` si tu veux parser les dates
    updated_at: string
    deleted_at: string | null
}

export type BankType = {
    id: number
    name: string
    created_at: string // ou `Date` si tu veux parser
    updated_at: string
    deleted_at: string | null
}


export interface RubriqueSalaireType {
    id: number;
    libelle: string;
    description: string | null;
    nature: string;
}

;

export type DiplomeType  = {
    id: number;
    name: string;
    description: string | null;
};

export type ModeDePaiementType = {
    id: number;
    libelle: string;
    description: string | null;
}

export type NiveauAffichageType = {
    id: number;
    libelle: string;
    description: string | null;
    added_by: number | null;
    created_at: string; // format ISO 8601
    updated_at: string; // format ISO 8601
    deleted_at: string | null; // peut être null
};

type ColonneAffichage = {
    id: number;
    libelle: string;
    description: string | null;
    added_by: number | null;
    created_at: string; // format ISO 8601
    updated_at: string; // format ISO 8601
    deleted_at: string | null; // peut être null
};

export type RubriquePaieType = {
    id: number;
    libelle: string;
    description: string | null;
    nature: string;

    niveauAffichage: NiveauAffichageType;
    niveau_affichage_id: number;

    colonneAffichage: ColonneAffichage;
    colonne_affichage_id: number;

    calculeAuProrataTempsTravail: boolean;
    calculeAPartirCoefficient: boolean;
    calculeAPartirSalaireBrut: boolean;
    coefficient: number;
    numeroOrdre: number;
    affichageDansGain: boolean;
    affichageDansRetenueLegale: boolean;
    affichageDansAutreRetenue: boolean;
    affichageDansChargePatronale: boolean;
    added_user_id: number | null;
    created_at: string; // format ISO 8601
    updated_at: string; // format ISO 8601
    deleted_at: string | null; // peut être null
    visible: boolean;
    imposable: boolean;
    rubriqueSysteme: boolean;
};

export type Relation = {
    id: number;
    libelle: string;
    description: string;
    added_by: string | null;
    created_at: string; // ISO datetime string
    updated_at: string; // ISO datetime string
    deleted_at: string | null;
};

export type FormationType = {
    id: number;
    theme: string;
    description: string;
    lieu: string;
    dateDebut: string;
    dateFin: string;
    duree: number | null;
    companyId: number;
    employeIds: number[];
};
