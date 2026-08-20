import { z } from "zod";

export const personneAPrevenirScheme = z.object({
    nomPrenom: z.string().min(1, "Le nom/prénom est requis"),
    lienParente:z.string().min(1, "Le nom/prénom est requis"),
    /*
        z.object({
        id: z.number().int().positive("L'id doit être un entier positif"),
    }),*/
    telephone: z
        .string().min(1, "Le numéro est requis"),

    adresse: z.string().min(1, "L'adresse est requise"),
    email: z.string().email("Email invalide"),
});

export type PresonneAPrevenirDataForm = z.infer<typeof personneAPrevenirScheme>;
