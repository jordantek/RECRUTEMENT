import { z } from 'zod';
import {ZodHelper} from "@/helpers/ZodHelper.ts";


export const absenceSchema = z.object({
    employeId: z.string().min(1, "L'employé est requis"),
    typeAbsence: z.string().min(1, "Le type d'absence est requis"),
    modeJouissance: z.string().min(1, "Le mode de jouissance est requis"),
    conditionAcceptation: z.string().min(1, "La condition d'acceptation est requise"),
    dateDebut:  ZodHelper.calendarDateSchema,
    dateFin: ZodHelper.calendarDateSchema,
    commentaire: z.string().optional(),
    preuve: z.any().optional(),
  });

export type AbsenceForm = z.infer<typeof absenceSchema>;
