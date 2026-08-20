import { z } from "zod";

// Fabrique un schéma avec ou sans suffixe
const makeEnfantSchema = (suffix: string = "") =>
    z.object({
            [`nom${suffix}`]: z.string().min(1, "Le nom est requis"),
            [`prenom${suffix}`]: z.string().min(1, "Le prénom est requis"),
            [`sexe${suffix}`]: z.enum(["MASCULIN", "FEMININ"]),
            [`dateNaissance${suffix}`]: z.coerce.date({
                required_error: "La date de naissance est requise.",
                invalid_type_error: "Format de date invalide."
            }),
            [`lieuNaissance${suffix}`]: z.string().min(1, "Le lieu de naissance est requis"),
    });

// Schémas
export const enfantSchema = makeEnfantSchema();
export const enfantSchema_ = makeEnfantSchema("_");

// Types TypeScript
export type EnfantFormData = z.infer<typeof enfantSchema>;
export type EnfantFormData_ = z.infer<typeof enfantSchema_>;