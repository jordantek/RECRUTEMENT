const DOC_TEMPLATE_ROOT = "/doc-templates/";

export const DocTemplate = {
    Attestation: {
        title: "Attestation de travail",
        description: "Modèles d'attestation de travail",
        files: [
            {
                name: "MODELE 1",
                path: `${DOC_TEMPLATE_ROOT}attestations/attestation_de_travail_1.rtf`,
                extension: "rtf",
                sizeKB: 35,
                isTemplate: true,
                description: "Modèle d'attestation de travail 1",
                tags: ["attestation", "travail", "modèle"]
            },
            {
                name: "MODELE 2",
                path: `${DOC_TEMPLATE_ROOT}attestations/attestation_de_travail_2.docx`,
                extension: "docx",
                sizeKB: 33,
                isTemplate: true,
                description: "Modèle d'attestation de travail 2",
                tags: ["attestation", "sans salaire"]
            }
        ]
    },

    Certificat: {
        title: "Certificat de travail",
        description: "Modèles de certificat de travail",
        files: [
            {
                name: "MODELE 1",
                path: `${DOC_TEMPLATE_ROOT}certificats/certificat_de_travail_1.docx`,
                extension: "docx",
                sizeKB: 32,
                isTemplate: true,
                description: "Modèle de certificat de travail",
                tags: ["certificat", "travail"]
            }
        ]
    },

    Discipline: {
        title: "Discipline",
        description: "Modèles types de documents disciplinaires : demande d'explications, blâme, avertissement et mise à pied",
        files: [
            {
                name: "MODELE 1 - DEMANDE D’EXPLICATION",
                path: `${DOC_TEMPLATE_ROOT}discipline/model_1.docx`,
                extension: "docx",
                sizeKB: 27,
                isTemplate: true,
                description: "Modèle de lettre de demande d’explication à un salarié",
                tags: ["discipline", "explication", "modèle"]
            },
            {
                name: "MODELE 2 - MISE À PIED DISCIPLINAIRE",
                path: `${DOC_TEMPLATE_ROOT}discipline/model_2.rtf`,
                extension: "rtf",
                sizeKB: 30,
                isTemplate: true,
                description: "Modèle de lettre de mise à pied disciplinaire",
                tags: ["discipline", "mise à pied", "disciplinaire", "modèle"]
            },
            {
                name: "MODELE 3 - MISE À PIED CONSERVATOIRE",
                path: `${DOC_TEMPLATE_ROOT}discipline/model_3.rtf`,
                extension: "rtf",
                sizeKB: 31,
                isTemplate: true,
                description: "Modèle de lettre de mise à pied conservatoire",
                tags: ["discipline", "mise à pied", "conservatoire", "modèle"]
            },
            {
                name: "MODELE 4 - BLÂME",
                path: `${DOC_TEMPLATE_ROOT}discipline/model_4.rtf`,
                extension: "rtf",
                sizeKB: 26,
                isTemplate: true,
                description: "Modèle de lettre de blâme adressée à un salarié",
                tags: ["discipline", "blâme", "modèle"]
            },
            {
                name: "MODELE 5 - AVERTISSEMENT POUR ABSENCE",
                path: `${DOC_TEMPLATE_ROOT}discipline/model_5.rtf`,
                extension: "rtf",
                sizeKB: 27,
                isTemplate: true,
                description: "Modèle de lettre d’avertissement pour absence injustifiée",
                tags: ["discipline", "avertissement", "absence", "modèle"]
            }
        ]
    },

    Contrat: {
        title: "Contrats",
        description: "Modèles types de contrats de travail : CDI, CDD, expatrié, etc.",
        files: [
            {
                name: "MODELE 1 - CONTRAT CDI",
                path: `${DOC_TEMPLATE_ROOT}contrats/model_1.docx`,
                extension: "docx",
                sizeKB: 43,
                isTemplate: true,
                description: "Modèle de contrat à durée indéterminée (CDI)",
                tags: ["contrat", "CDI", "modèle"]
            },
            {
                name: "MODELE 2 - CONTRAT EXPATRIE",
                path: `${DOC_TEMPLATE_ROOT}contrats/model_2.docx`,
                extension: "docx",
                sizeKB: 46,
                isTemplate: true,
                description: "Modèle de contrat de travail pour salarié expatrié",
                tags: ["contrat", "expatrié", "modèle"]
            },
            {
                name: "MODELE 3 - CONTRAT CDD",
                path: `${DOC_TEMPLATE_ROOT}contrats/model_3.docx`,
                extension: "docx",
                sizeKB: 41,
                isTemplate: true,
                description: "Modèle de contrat à durée déterminée (CDD)",
                tags: ["contrat", "CDD", "modèle"]
            }
        ]
    },

    Conge: {
        title: "Congés",
        description: "Modèles de lettres pour différents types de congés",
        files: [
            {
                name: "MODELE 1 - CONGÉ DÉCÈS",
                path: `${DOC_TEMPLATE_ROOT}conges/model_1.rtf`,
                extension: "rtf",
                sizeKB: 32,
                isTemplate: true,
                description: "Modèle de lettre de demande de congé pour décès d’un proche",
                tags: ["congé", "décès", "modèle"]
            },
            {
                name: "MODELE 2 - CONGÉ PATERNITÉ",
                path: `${DOC_TEMPLATE_ROOT}conges/model_2.rtf`,
                extension: "rtf",
                sizeKB: 34,
                isTemplate: true,
                description: "Modèle de lettre de demande de congé de paternité",
                tags: ["congé", "paternité", "modèle"]
            },
            {
                name: "MODELE 3 - CONGÉ PAYÉ",
                path: `${DOC_TEMPLATE_ROOT}conges/model_3.rtf`,
                extension: "rtf",
                sizeKB: 35,
                isTemplate: true,
                description: "Modèle de lettre de demande de congé payé",
                tags: ["congé", "payé", "modèle"]
            },
            {
                name: "MODELE 4 - CONGÉ POUR FORMATION",
                path: `${DOC_TEMPLATE_ROOT}conges/model_4.rtf`,
                extension: "rtf",
                sizeKB: 36,
                isTemplate: true,
                description: "Modèle de lettre de demande de congé pour suivre une formation",
                tags: ["congé", "formation", "modèle"]
            },
            {
                name: "MODELE 5 - CONGÉ MARIAGE",
                path: `${DOC_TEMPLATE_ROOT}conges/model_5.rtf`,
                extension: "rtf",
                sizeKB: 35,
                isTemplate: true,
                description: "Modèle de lettre de demande de congé pour mariage",
                tags: ["congé", "mariage", "modèle"]
            },
            {
                name: "MODELE 6 - CONGÉ SABBATIQUE",
                path: `${DOC_TEMPLATE_ROOT}conges/model_6.rtf`,
                extension: "rtf",
                sizeKB: 37,
                isTemplate: true,
                description: "Modèle de lettre de demande de congé sabbatique",
                tags: ["congé", "sabbatique", "modèle"]
            },
            {
                name: "MODELE 7 - REPRISE DE SERVICE",
                path: `${DOC_TEMPLATE_ROOT}conges/model_7.docx`,
                extension: "docx",
                sizeKB: 33,
                isTemplate: true,
                description: "Modèle de lettre de reprise de service après congé",
                tags: ["congé", "reprise", "service", "modèle"]
            }
        ]
    },

    Stage: {
        title: "Stage",
        description: "Modèles pour conventions et attestations de stage",
        files: [
            {
                name: "MODELE 1 - ATTESTATION STAGE ACADÉMIQUE",
                path: `${DOC_TEMPLATE_ROOT}stage/model_1.rtf`,
                extension: "rtf",
                sizeKB: 30,
                isTemplate: true,
                description: "Modèle d'attestation de fin de stage dans un cadre académique",
                tags: ["stage", "attestation", "académique"]
            },
            {
                name: "MODELE 2 - LETTRE DE STAGE ACADÉMIQUE",
                path: `${DOC_TEMPLATE_ROOT}stage/model_2.docx`,
                extension: "docx",
                sizeKB: 32,
                isTemplate: true,
                description: "Modèle de lettre de demande ou confirmation de stage académique",
                tags: ["stage", "lettre", "académique"]
            },
            {
                name: "MODELE 3 - CONVENTION STAGE PROFESSIONNEL",
                path: `${DOC_TEMPLATE_ROOT}stage/model_3.docx`,
                extension: "docx",
                sizeKB: 39,
                isTemplate: true,
                description: "Modèle de convention pour un stage professionnel",
                tags: ["stage", "convention", "professionnel"]
            }
        ]
    },

    NoteDeService: {
        title: "Note de service",
        description: "Modèles de notes de service",
        files: [
            {
                name: "MODELE 1 - Horaire collectif de travail",
                path: `${DOC_TEMPLATE_ROOT}note_de_service/model_1.rtf`,
                extension: "rtf",
                sizeKB: 28,
                isTemplate: true,
                description: "Note de service relative à l'horaire collectif de travail",
                tags: ["note", "service", "horaire", "travail"]
            },
            {
                name: "MODELE 2 - Composition CHS Entreprise",
                path: `${DOC_TEMPLATE_ROOT}note_de_service/model_2.rtf`,
                extension: "rtf",
                sizeKB: 30,
                isTemplate: true,
                description: "Note de service relative à la composition du CHS Entreprise ou Établissement",
                tags: ["note", "service", "CHS", "entreprise"]
            },
            {
                name: "MODELE 3 - Composition CHST Chantier",
                path: `${DOC_TEMPLATE_ROOT}note_de_service/model_3.rtf`,
                extension: "rtf",
                sizeKB: 30,
                isTemplate: true,
                description: "Note de service relative à la composition du CHST Chantier",
                tags: ["note", "service", "CHST", "chantier"]
            }
        ]
    }
};
