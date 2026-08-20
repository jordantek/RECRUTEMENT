
export type Role = "ROLE_SUPER_ADMIN" | "ROLE_COMPANY_ADMIN" | "ROLE_HR" | "ROLE_MANAGER" | "ROLE_EMPLOYEE";

const roleBadges = {
    ROLE_SUPER_ADMIN: "bg-red-100 text-red-700", // Super Admin : fond rouge clair, texte rouge foncé
    ROLE_COMPANY_ADMIN: "bg-blue-100 text-blue-700", // Company Admin : fond bleu clair, texte bleu foncé
    ROLE_HR: "bg-green-100 text-green-700", // HR : fond vert clair, texte vert foncé
    ROLE_MANAGER: "bg-yellow-100 text-yellow-700", // Manager : fond jaune clair, texte jaune foncé
    ROLE_EMPLOYEE: "bg-indigo-100 text-indigo-700", // Employee : fond indigo clair, texte indigo foncé
};

const roleLabels: Record<Role, string> = {
    ROLE_SUPER_ADMIN: "Super Admin",
    ROLE_COMPANY_ADMIN: "Admin Entreprise",
    ROLE_HR: "Ressources Humaines",
    ROLE_MANAGER: "Manager",
    ROLE_EMPLOYEE: "Employé",
};

// Fonction pour afficher les rôles avec un badge
export function getRoleBadgeInfo(role: Role): { role: string; color: string } {
    const badgeColor = roleBadges[role] || "bg-gray-500 text-white";
    return {
        role: roleLabels[role] || role, // Le libellé du rôle ou, si inexistant, le nom du rôle brut
        color: badgeColor,
    };
}

// Fonction pour afficher le rôle sans badge
export function getRoleText(role: Role): string {
    return roleLabels[role] || "Rôle inconnu";
}

