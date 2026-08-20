import { z } from 'zod';

export const socialEventSchema = z.object({
    nom_agent: z.string().min(1, "L'agent est requis"),
    date_evenement: z.string().regex(/^\d{4}-\d{2}-\d{2}$/, "Format de date invalide (YYYY-MM-DD)"),
    raison: z.string().min(1, "La raison de l'événement est requise"),
    action_menee: z.string().min(1, "L'action menée est requise"),
    montant_don: z.number().nonnegative("Le montant du don doit être positif ou nul").optional(),
    status: z.enum(['Prévu', 'Réalisé', 'Annulé'], {
        message: "Le statut doit être : 'Prévu', 'Réalisé' ou 'Annulé'",
    }),
});

export type SocialEventForm = z.infer<typeof socialEventSchema>;
