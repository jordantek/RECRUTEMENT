import { z } from 'zod';
import {ZodHelper} from "@/helpers/ZodHelper.ts";


export const formationSchema = z.object({
  theme: z.string().min(1, "Le thème de la formation est requis"),
  description: z.string().optional(),
  lieu: z.string().min(1, "Le lieu est requis"),
  dateDebut:  ZodHelper.calendarDateSchema,

  dateFin:  ZodHelper.calendarDateSchema,

  employees: z.array(z.number()).optional(), // Pour `selectedEmployeeIds`
});

export type FormationForm = z.infer<typeof formationSchema>;
