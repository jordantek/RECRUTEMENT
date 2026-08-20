import * as z from "zod";

export const contratSchema = z.object({
  employeId: z
      .string()
      .nonempty({ message: "L'ID de l'employé est requis" })
      .transform(val => parseInt(val, 10))
      .refine(val => !isNaN(val), { message: "L'ID de l'employé doit être un nombre valide" }),
  categorieEmployeId: z
      .string()
      .nonempty({ message: "L'ID de l'employé est requis" })
      .transform(val => parseInt(val, 10))
      .refine(val => !isNaN(val), { message: "L'ID de l'employé doit être un nombre valide" }),

  departementId: z
      .string()
      .nonempty({ message: "L'ID du département est requis" })
      .transform(val => parseInt(val, 10))
      .refine(val => !isNaN(val), { message: "L'ID du département doit être un nombre valide" }),

  posteId: z
      .string()
      .nonempty({ message: "L'ID du poste est requis" })
      .transform(val => parseInt(val, 10))
      .refine(val => !isNaN(val), { message: "L'ID du poste doit être un nombre valide" }),



  mouvementContrat: z.enum(["NOUVEAU", "RENOUVELLEMENT", "FIN"], {
    required_error: "Le mouvement du contrat est requis",
    invalid_type_error: "Le mouvement du contrat doit être 'NOUVEAU', 'RENOUVELLEMENT' ou 'FIN'",
  }),
  typeContrat: z.enum(["CDI", "CDD", "AUTRE"], {
    required_error: "Le type de contrat est requis",
    invalid_type_error: "Le type de contrat doit être 'CDI', 'CDD' ou 'AUTRE'",
  }),
  natureContratId: z
      .string()
      .nonempty({ message: "L'ID de la nature du contrat est requis" })
      .transform(val => parseInt(val, 10))
      .refine(val => !isNaN(val), { message: "L'ID de la nature du contrat doit être un nombre valide" }),

  modeDePaiementId: z
      .string()
      .nonempty({ message: "L'ID de la banque est requis" })
      .transform(val => parseInt(val, 10))
      .refine(val => !isNaN(val), { message: "L'ID de la banque doit être un nombre valide" }),

  banqueId: z
      .string()
      .nonempty({ message: "L'ID de la banque est requis" })
      .transform(val => parseInt(val, 10))
      .refine(val => !isNaN(val), { message: "L'ID de la banque doit être un nombre valide" }),

  numeroCompte: z.string({
    required_error: "Le numéro de compte est requis",
    invalid_type_error: "Le numéro de compte doit être une chaîne de caractères",
  }),
  dateDebut: z.coerce.date({
    required_error: "La date de naissance est requise.",
    invalid_type_error: "Format de date invalide."
  }),
  dateFin: z.coerce.date({
    invalid_type_error: "Format de date invalide."
  }).optional(),
  debutEssai: z.coerce.date({
    required_error: "La date de naissance est requise.",
    invalid_type_error: "Format de date invalide."
  }),
  finEssai: z.coerce.date({
    required_error: "La date de naissance est requise.",
    invalid_type_error: "Format de date invalide."
  }),

  aibContratEmploye: z.number({
    required_error: "Le champ aibContratEmploye est requis",
    invalid_type_error: "Le champ aibContratEmploye doit être un nombre",
  }),
  cautionContratEmploye: z.number({
    required_error: "Le champ cautionContratEmploye est requis",
    invalid_type_error: "Le champ cautionContratEmploye doit être un nombre",
  }),
  transfertContratEmploye: z.number({
    required_error: "Le champ transfertContratEmploye est requis",
    invalid_type_error: "Le champ transfertContratEmploye doit être un nombre",
  }),
});

export const  contratdefaultValues = {
  aibContratEmploye: 0.0,
  cautionContratEmploye: 0.0,
  transfertContratEmploye: 0.0,
};


  