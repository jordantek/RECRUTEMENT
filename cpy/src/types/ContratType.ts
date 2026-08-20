// Employé
export interface Employe {
    id: number;
    matricule: string;
    titre: string;
    nom: string;
    prenom: string;
    date_naissance: string; // format: "dd-MM-yyyy"
    lieu_naissance: string;
    sexe: string;
    situationMatrimoniale: string;
    numero_ifu: string;
    telephone: string;
    email: string | null;
    nom_pere: string | null;
    nom_mere: string | null;
    boite_postale: string | null;
    maison: string | null;
    numeroCarre: string | null;
    quartier: string;
    nationalite: string;
    numero_cnss: string;
    profession: string;
    is_employe_interne: boolean;
    added_by: string | null;
    created_at: string; // ISO date
    updated_by: string | null;
    updated_at: string; // ISO date
    deleted_at: string | null;
}

// Entreprise
export interface Company {
    id: number;
    name: string;
    nss: string;
    rss: number;
    address: string;
    country: string;
    email: string;
    phone: string;
    webSite: string;
    rccm: string;
    ifu: string;
    logo: string | null;
    directorName: string;
    directorEmail: string;
    directorPhone: string;
    statusId: string | null;
    tvaVal: number;
    createdAt: string; // ISO date
    updatedAt: string; // ISO date
    deletedAt: string | null;
    createdBy: number;
    updatedBy: number;
}

// Département
export interface Departement {
    id: number;
    libelle: string;
    description: string | null;
    company_id: number;
    added_by: string | null;
    createdAt: string; // ISO date
    updatedAt: string | null;
}

// Poste
export interface Poste {
    id: number;
    libelle: string;
    description: string | null;
    departement: Departement;
    departement_id: number;
    added_by: string | null;
    createdAt: string; // ISO date
    updatedAt: string | null;
}

// Catégorie Employé
export interface CategorieEmploye {
    id: number;
    name: string;
    description: string;
    createdAt: string | null;
    updatedAt: string | null;
    deletedAt: string | null;
}

// Nature du Contrat
export interface NatureContrat {
    id: number;
    libelle: string;
    description: string | null;
    added_by: string | null;
    created_at: string; // ISO date
    updated_at: string; // ISO date
    deleted_at: string | null;
}

// Mode de Paiement
export interface ModeDePaiement {
    id: number;
    libelle: string;
    description: string | null;
    added_by: string | null;
    created_at: string; // ISO date
    updated_at: string; // ISO date
    deleted_at: string | null;
}

// Banque
export interface Banque {
    id: number;
    name: string;
    created_at: string; // ISO date
    updated_at: string; // ISO date
    deleted_at: string | null;
}

// Utilisateur
export interface AddedBy {
    id: number;
    username: string;
    fullName: string;
    phone: string | null;
    email: string;
    password: string;
    role: string;
    lastLoginAt: string; // ISO date
    lastLoginIp: string;
    addedBy: string | null;
    authToken: string;
    createdAt: string; // ISO date
    updatedAt: string; // ISO date
    deletedAt: string | null;
}

// Supérieur hiérarchique (optionnel)
export type SuperieurHierarchique = {
    id: number;
    // autres champs si nécessaire
} | null;

// Contrat Employé
export interface ContratEmploye {
    id: number;
    employeId: number;
    employe: Employe;
    companyId: number;
    company: Company;
    departementId: number;
    departement: Departement;
    posteId: number;
    poste: Poste;
    categorieEmployeId: number;
    categorieEmploye: CategorieEmploye;
    natureContratId: number;
    natureContrat: NatureContrat;
    modeDePaiementId: number;
    modeDePaiement: ModeDePaiement;
    banqueId: number;
    banque: Banque;
    addedById: number;
    addedBy: AddedBy;
    mouvementContrat: string;
    typeContrat: string;
    numeroCompte: string;
    dateDebut: string; // format: "yyyy-MM-dd"
    dateFin: string; // format: "yyyy-MM-dd"
    duree: string | null;
    debutEssai: string; // format: "yyyy-MM-dd"
    finEssai: string; // format: "yyyy-MM-dd"
    dureeEssai: string | null;
    dureeContrat: string | null;
    aibContratEmploye: number;
    cautionContratEmploye: number;
    transfertContratEmploye: number;
    statusContrat: string | null;
    dateArretContrat: string | null;
    motifArretContrat: string | null;
    supN_1Id: string | null;
    supN_1: SuperieurHierarchique;
    supN_2Id: string | null;
    supN_2: SuperieurHierarchique;
    createdAt: string; // ISO date
    updatedAt: string | null;
    deletedAt: string | null;
}