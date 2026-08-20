export interface InstitutionType {
    id: number;
    name: string;
    created_at: string;  // ISO date string
    updated_at: string;  // ISO date string
}

export interface Departement {
    id: number;
    libelle: string;
    company_id: number;
    createdAt: string;  // ISO date string
}

export interface ContratEmploye {
    id: number;
    departement: Departement;
    aibContratEmploye: number;
    cautionContratEmploye: number;
    transfertContratEmploye: number;
    arretContrat: boolean;
}

export interface Employe {
    id: number;
    nom: string;
    prenom: string;
    is_employe_interne: boolean;
}

export interface MensualiteType {
    id: number;
    dureeMensualite: number;
    moisDemarrage: string; // Format: YYYY-MM
    moisFin: string; // Format: YYYY-MM
    montantMensuel: number;
    mensualiteSolde: boolean;
    statut: 'soldée' | 'en attente' | string; // adapte si besoin
    dateSoldee?: string; // ISO string ou null si pas encore soldée
    rubriqueId: number;
    companyId: number;
    institutionId: number;
    institution: InstitutionType;
    contratEmployeId: number;
    contratEmploye: ContratEmploye;
    employeId: number;
    employe: Employe;
    lastUpdateUserId: number;
    createdAt: string;  // ISO date string
    updatedAt: string;  // ISO date string
}


