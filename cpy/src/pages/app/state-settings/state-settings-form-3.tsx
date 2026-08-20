// components/settings/state-settings-form-step3.tsx
"use client"

import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"

import { Form } from "@/components/ui/form"
import { BindFormItem, FieldOption } from "@/components/forms/bind-form-item"
import { Button } from "@/components/ui/button"

const schema = z.object({
  host: z.string().min(1, "Hôte du serveur requis"),
  port: z
    .string()
    .regex(/^\d+$/, "Le port doit être un nombre")
    .min(2, "Port requis"),
  securityProtocol: z.string().min(1, "Protocole requis"),
  mail: z.string().email("Adresse email invalide"),
  password: z.string().min(1, "Mot de passe requis"),
})

const formFields: FieldOption[] = [
  {
    tag: "host",
    label: "Hôte du serveur",
    placeholder: "smtp.votre-serveur.com",
    input_type: "text",
    size: "col-span-6",
    required: true,
  },
  {
    tag: "port",
    label: "Port de connexion",
    placeholder: "Ex: 587",
    input_type: "text",
    size: "col-span-6",
    required: true,
  },
  {
    tag: "securityProtocol",
    label: "Protocole de sécurité",
    placeholder: "Ex: TLS, SSL",
    input_type: "text",
    size: "col-span-6",
    required: true,
  },
  {
    tag: "mail",
    label: "Email d’envoi",
    placeholder: "contact@entreprise.com",
    input_type: "email",
    size: "col-span-6",
    required: true,
  },
  {
    tag: "password",
    label: "Mot de passe",
    placeholder: "Mot de passe SMTP",
    input_type: "password",
    size: "col-span-6",
    required: true,
  },
]

export default function StateSettingsFormStep3() {
  const form = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      host: "",
      port: "",
      securityProtocol: "",
      mail: "",
      password: "",
    },
  })

  const onSubmit = (data: any) => {
    console.log("Formulaire SMTP soumis :", data)
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
