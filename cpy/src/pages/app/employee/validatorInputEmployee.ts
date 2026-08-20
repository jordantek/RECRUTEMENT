import * as z from "zod";

export const employeeSchema = z.object({
  matricule: z.string().min(1, { message: "Le matricule est requis." }),
  titre: z.enum(["MONSIEUR", "MADAME", "MADEMOISELLE"], { message: "Le titre est requis." }),
  nom: z.string().min(1, { message: "Le nom est requis." }),
  prenom: z.string().min(1, { message: "Le prénom est requis." }),
  sexe: z.enum(["MASCULIN", "FEMININ"], { message: "Le sexe est requis." }),
  date_naissance: z.coerce.date({
    required_error: "La date de naissance est requise.",
    invalid_type_error: "Format de date invalide."
  })
      .refine(date => !isNaN(date.getTime()), { message: "Date invalide." }),
  //date_naissance: z.string().min(1, { message: "La date de naissance est requise." }), // à convertir en `date` si besoin
  lieu_naissance: z.string().min(1, { message: "Le lieu de naissance est requis." }),
  numero_ifu: z.string().min(1, { message: "Le numéro IFU est requis." }),
  situationMatrimoniale: z.enum(["CELIBATAIRE_SANS_ENFANT", "CELIBATAIRE_AVEC_ENFANT", "MARIE", "DIVORCE"], { message: "La situation matrimoniale est requise." }),
  numero_cnss: z.string().min(1, { message: "Le numéro CNSS est requis." }),
  telephone: z.string().min(1, { message: "Le numéro de téléphone est requis." }),
  email: z.string().min(1, { message: "L'adresse email est requise." }).email({ message: "Adresse email invalide." }),
  quartier: z.string().min(1, { message: "Le quartier est requis." }),
  nationalite: z.string().min(1, { message: "La nationalité est requise." }),
  profession: z.string().min(1, { message: "La profession est requise." }),
});
  