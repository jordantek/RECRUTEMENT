"use client"

import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"

import { Form } from "@/components/ui/form"
import { BindFormItem, FieldOption } from "@/components/forms/bind-form-item"
import { Button } from "@/components/ui/button"

const schema = z.object({
  raisonSociale: z.string().min(1, "Raison sociale requise"),
  secteurActivite: z.string().min(1, "Secteur d'activité requis"),
  securiteSociale: z.string().min(1, "Numéro de sécurité sociale requis"),
  ifu: z.string().min(1, "IFU requis"),
  adresse: z.string().min(1, "Adresse requise"),
  civilite: z.enum(["MONSIEUR", "MADAME"], {
    required_error: "Civilité requise",
  }),
  nomComplet: z.string().min(1, "Nom complet requis"),
  email: z.string().email("Email invalide"),
  fonction: z.string().min(1, "Fonction requise"),
  rccm: z.string().min(1, "RCCM requis"),
})

const formFields: FieldOption[] = [
  // Section: Informations sur la structure
  {
    tag: "raisonSociale",
    label: "Raison sociale",
    placeholder: "Nom de l'entreprise",
    input_type: "text",
    size: "col-span-6",
    required: true,
  },
  {
    tag: "secteurActivite",
    label: "Secteur d'activité",
    placeholder: "Ex: Technologie, Santé, Finance, etc.",
    input_type: "text",
    size: "col-span-6",
    required: true,
  },
  {
    tag: "securiteSociale",
    label: "Sécurité sociale",
    placeholder: "Numéro de sécurité sociale",
    input_type: "text",
    size: "col-span-6",
    required: true,
  },
  {
    tag: "ifu",
    label: "IFU",
    placeholder: "Identifiant Fiscal Unique",
    input_type: "text",
    size: "col-span-6",
    required: true,
  },
  {
    tag: "adresse",
    label: "Adresse",
    placeholder: "Adresse de l'entreprise",
    input_type: "text",
    size: "col-span-12",
    required: true,
  },

  // Section: Informations du responsable
  {
    tag: "civilite",
    label: "Civilité",
    input_type: "radio",
    size: "col-span-4",
    required: true,
    options: [
      { value: "MONSIEUR", label: "Monsieur" },
      { value: "MADAME", label: "Madame" },
    ],
  },
  {
    tag: "nomComplet",
    label: "Nom complet",
    input_type: "text",
    size: "col-span-4",
    required: true,
  },
  {
    tag: "email",
    placeholder: "email@gmail.com",
    label: "Email",
    input_type: "email",
    size: "col-span-4",
    required: true,
  },
  {
    tag: "fonction",
    label: "Qualité / Fonction",
    placeholder: "Directeur Général, Responsable RH, etc.",
    input_type: "text",
    size: "col-span-6",
    required: true,
  },
  {
    tag: "rccm",
    label: "RCCM",
    placeholder: "Registre du Commerce et du Crédit Mobilier",
    input_type: "text",
    size: "col-span-6",
    required: true,
  },
]

export default function StateSettingsForm() {
  const form = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      raisonSociale: "",
      secteurActivite: "",
      securiteSociale: "",
      ifu: "",
      adresse: "",
      civilite: "MONSIEUR",
      nomComplet: "",
      email: "",
      fonction: "",
      rccm: "",
    },
  })

  const onSubmit = (data: any) => {
    console.log("Formulaire soumis :", data)
  }

  // Découper les champs par section
  const structureFields = formFields.slice(0, 5)
  const responsableFields = formFields.slice(5)

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="mt-10 space-y-8 w-full max-w-4xl">
        {/* Bloc 1 : Informations sur la structure */}
        <div className="space-y-4">
          <p className="text-lg font-semibold text-left text-gray-800">Informations sur la structure</p>
          <div className="grid grid-cols-12 gap-4">
            {structureFields.map((field, index) => (
              <BindFormItem
                key={index}
                index={index}
                option={field}
                form={form}
                tag={field.tag}
                readonly={false}
              />
            ))}
          </div>
        </div>

        {/* Bloc 2 : Informations du responsable */}
        <div className="space-y-4">
          <p className="text-lg font-semibold text-left text-gray-800">Informations du responsable de la structure</p>
          <div className="grid grid-cols-12 gap-4">
            {responsableFields.map((field, index) => (
              <BindFormItem
                key={index + 100} // éviter collisions d'index
                index={index}
                option={field}
                form={form}
                tag={field.tag}
                readonly={false}
              />
            ))}
          </div>
        </div>

        {/* Bouton de soumission */}
        <div className="flex justify-end pt-4">
          <Button type="submit" className="bg-blue-900 text-white">
            Enregistrer
          </Button>
        </div>
      </form>
    </Form>
  )
}
