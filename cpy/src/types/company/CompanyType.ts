export type CompanyType = {
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
    statusId: number | null;
    tvaVal: number;
    createdAt: string; // ISO date string
    updatedAt: string;
    deletedAt: string | null;
    createdBy: number;
    updatedBy: number;
    activityAreas: string[] | null; // ou un type plus complexe si nécessaire
};
