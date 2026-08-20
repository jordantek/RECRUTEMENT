import { z } from 'zod';
import {ZodHelper} from "@/helpers/ZodHelper.ts";




export const sanctionSchema = z.object({
        employeId: z.string({
            required_error: "La sélection d'un agent est obligatoire",
            invalid_type_error: "L'agent doit être sélectionné dans la liste"
        }),

    datePlainte: ZodHelper.calendarDateSchema,

    contenuePlainte: z.string({
            required_error: "Le contenu de la plainte est obligatoire",
            invalid_type_error: "La plainte doit être un texte"
        }),

        dateDemandeExplication:ZodHelper.calendarDateSchema,

        dateReponse:ZodHelper.calendarDateSchema,

    sanctionDonnee: z.string({
    required_error: "La sanction donnée est obligatoire",
    invalid_type_error: "La sanction doit être décrite sous forme de texte"
    }),

    observation: z
        .string({
            invalid_type_error: "Les observations doivent être un texte",
            required_error: "Les observations ne peuvent pas être vides",
        })
        .optional()

});

// Type TypeScript dérivé du schéma
export type SanctionFormValues = z.infer<typeof sanctionSchema>;