import { z } from "zod";

const ouiNonEnum = z.enum(["OUI", "NON"], {
  required_error: "Ce champ est requis",
  invalid_type_error: "La valeur doit être 'OUI' ou 'NON'",
});

export const RubriquePaieSchema = z.object({
  libelle: z.string().min(1, "Le libellé est requis"),
  nature: z.enum(["AVANTAGE", "RETENUE"], {
    required_error: "La nature est requise",
  }),
  partPatronale: ouiNonEnum,
  rubriqueImposable: ouiNonEnum,
  calculeAPartirCoefficient: ouiNonEnum,
  coefficient: z.number().default(0),
  calculeAPartirSalaireBrut: ouiNonEnum,
  numeroOrdre: z.number().int("Doit être un entier").default(0),
});

export type RubriquePaieFormValues = z.infer<typeof RubriquePaieSchema>;