import { ZodHelper } from '@/helpers/ZodHelper';
import { z } from 'zod';

export const accidentSchema = z.object({
    employeId: z.string(),
    dateAccident: ZodHelper.calendarDateSchema,
    dateDeclaration:  ZodHelper.calendarDateSchema,
    effetAccident: z.string()
        .min(1, "La description doit contenir au moins 1 caractères")
        .optional(),

    action: z.string()
        .optional(),

    depense: z.number()
        .min(0, "La dépense ne peut être négative")
        .optional(),
});

export type AccidentForm = z.infer<typeof accidentSchema>;

