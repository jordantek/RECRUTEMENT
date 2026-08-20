import { z } from 'zod';
import {ZodHelper} from "@/helpers/ZodHelper.ts";

export const evenementSocialSchema = z.object({
    employeId: z.string({
        required_error: "L'organisateur est requis",
        invalid_type_error: "L'ID doit être une chaîne de caractères"
    }).min(1, "L'organisateur ne peut pas être vide"),

    dateEvenement: ZodHelper.calendarDateSchema,
    designation: z.string()
        .min(1, "La désignation doit contenir au moins 1 caractère")
        .max(200, "La désignation ne peut excéder 200 caractères"),
    actionMenee: z.string()
        .min(1, "L'action menée doit contenir au moins 1 caractère")
        .max(500, "L'action menée ne peut excéder 500 caractères")
        .optional(),
    montant: z.number()
        .min(0, "Le montant ne peut être négatif")
        .optional(),
    observation: z.string()
        .max(1000, "L'observation ne peut excéder 1000 caractères")
        .optional()
});

export type EvenementSocialForm = z.infer<typeof evenementSocialSchema>;