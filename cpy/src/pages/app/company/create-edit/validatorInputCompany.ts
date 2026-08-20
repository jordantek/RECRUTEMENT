import * as z from "zod";
//import {FileHelpers} from "@/helpers/FileHelpers.ts";

export const  companySchema = z.object({
    name: z.string({ message: "Le nom de l'entreprise est requis." }),
    nss: z.string({ message: "Le NSS est requis." }),
    rss: z.enum(["1", "2", "3", "4"], {
        required_error: "Le RSS est requis.",
        invalid_type_error: "Le RSS doit être une des valeurs suivantes : 1, 2, 3 ou 4.",
    }),
    address: z.string({ message: "L'adresse est requise." }),
    country: z.string({ message: "Le pays est requis." }),
    email: z.string({ message: "L'email est requis." })
        .email({ message: "L'email est invalide." }),
    phone: z.string({ message: "Le numéro de téléphone est requis." }),
    rccm: z.string({ message: "Le RCCM est requis." }),
    ifu: z.string({ message: "L'IFU est requis." }),
    /*logo: FileHelpers.generateFileSchema({maxSize: 1024 * 1024,allowedExtensions: ["image/jpeg", "image/jpg", "image/png", "image/svg+xml"]
    }),*/
    directorName: z.string({ message: "Le nom du directeur est requis." }),
    directorEmail: z.string({ message: "L’email du directeur est requis." })
        .email({ message: "L’email du directeur est invalide." }),
    directorPhone: z.string({ message: "Le numéro du directeur est requis." }),
    tvaVal: z.string({ message: "La TVA est requise." })
        .min(0, { message: "La TVA ne peut pas être négative." }),
    activityAreas: z.array(z.string({ message: "Les domaines d'activité doivent être des chaînes de caractères." }))
        .nonempty({ message: "Veuillez spécifier au moins un domaine d'activité." }),
    banqueIds: z.array(z.string({ message: "" }))
        .nonempty({ message: "Veuillez spécifier au moin une banque" }),
});