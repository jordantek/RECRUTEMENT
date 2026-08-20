"use client"

import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"

import { Form } from "@/components/ui/form"
import { BindFormItem, FieldOption } from "@/components/forms/bind-form-item"
import { Button } from "@/components/ui/button"

const schema = z.object({
  joursCongeParMois: z.number().min(0, "Valeur requise"),
  heuresTravailParJour: z.number().min(0, "Valeur requise"),
  heuresTravailParSemaine: z.number().min(0, "Valeur requise"),
  dureeMensuelleTravail: z.number().min(0, "Valeur requise"),
  methodeDecompteConge: z.enum([
    "ouvrés",
    "calendaires",
    "heures",
    "periode_fixe",
    "fractionnement",
    "rtt"
  ], {
    required_error: "Méthode de décompte requise"
  }),
  heuresParJourConge: z.number().min(0, "Valeur requise"),
})

const formFields: FieldOption[] = [
  {
    tag: "joursCongeParMois",
    label: "Nombre de jour de congés par mois",
    input_type: "number",
    size: "col-span-6",
    required: true,
  },
  {
    tag: "heuresTravailParJour",
    label: "Heures de travail par jour",
    input_type: "number",
    size: "col-span-6",
    required: true,
  },
  {
    tag: "heuresTravailParSemaine",
    label: "Heures de travail par semaine",
    input_type: "number",
    size: "col-span-6",
    required: true,
  },
  {
    tag: "dureeMensuelleTravail",
    label: "Durée mensuelle collective de travail",
    input_type: "number",
    size: "col-span-6",
    required: true,
  },
  {
    tag: "methodeDecompteConge",
    label: "Méthode de décompte des jours de congés",
    input_type: "select",
    size: "col-span-6",
    required: true,
    options: [
      { value: "ouvrés", label: "Décompte en jours ouvrés" },
      { value: "calendaires", label: "Décompte en jours calendaires" },
      { value: "heures", label: "Décompte en heures" },
      { value: "periode_fixe", label: "Décompte par période fixe" },
      { value: "fractionnement", label: "Décompte par fractionnement" },
      { value: "rtt", label: "Décompte spécifique aux RTT" },
    ],
  },
  {
    tag: "heuresParJourConge",
    label: "Nombre d'heures par jour pour le décompte des congés",
    input_type: "number",
    size: "col-span-6",
    required: true,
  },
]

export default function StateSettingsFormStep2() {
  const form = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      joursCongeParMois: 0,
      heuresTravailParJour: 0,
      heuresTravailParSemaine: 0,
      dureeMensuelleTravail: 0,
      methodeDecompteConge: "ouvrés",
      heuresParJourConge: 0,
    },
  })

  const onSubmit = (data: any) => {
    console.log("Étape 2 soumise :", data)
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="mt-10 space-y-4 w-full max-w-4xl">
        <div className="grid grid-cols-12 gap-4">
          {formFields.map((field, index) => (
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
        <div className="flex justify-end">
        <Button type="submit" className="bg-blue-900 text-white">
            Enregistrer
          </Button>
        </div>
      </form>
    </Form>
  )
}
