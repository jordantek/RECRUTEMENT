import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useEffect, useState } from "react";
import { BindFormItem, FieldOption } from "@/components/forms/bind-form-item";
import { Form } from "@/components/ui/form";
import ButtonWithLoading from "@/components/ui/button-with-loading";
import { useAuth } from "@/lib/auth";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { format, differenceInDays } from "date-fns";

const absenceSchema = z.object({
  motifAbsenceId: z.string().min(1),
  libelle: z.string().min(1),
  modeJouissance: z.string().min(1),
  dateDebut: z.preprocess((arg) => (typeof arg === "string" && arg !== "" ? new Date(arg) : arg), z.date()),
  dateFin: z.preprocess((arg) => (typeof arg === "string" && arg !== "" ? new Date(arg) : arg), z.date()),
  commentaire: z.string().optional(),
  preuve: z.any().optional(), // Pour la pièce jointe (peut être File, null, ou undefined)
});

export default function NewAbsenceRequestForm() {
  const { user, logout } = useAuth();

  const [motifs, setMotifs] = useState<{ value: string; label: string }[]>([]);
  const [loading, setLoading] = useState(false);
  const [isFetching, setIsFetching] = useState(false);

  const form = useForm<z.infer<typeof absenceSchema>>({
    resolver: zodResolver(absenceSchema),
    defaultValues: {
      motifAbsenceId: "",
      libelle: "",
      modeJouissance: "",
      dateDebut: undefined,
      dateFin: undefined,
      commentaire: "",
      preuve: undefined,
    },
  });

  const fields: FieldOption[] = [
    {
      tag: "motifAbsenceId",
      label: "Type de l'absence",
      input_type: "select",
      required: true,
      size: "col-span-6",
      placeholder: "Choisissez un motif",
      options: motifs,
    },
    {
      tag: "libelle",
      label: "Raison de votre absence",
      input_type: "text",
      required: true,
      size: "col-span-6",
      placeholder: "Saisir une raison",
    },
    {
      tag: "modeJouissance",
      label: "Mode de jouissance",
      input_type: "text",
      required: true,
      size: "col-span-6",
      placeholder: "Ex: Jours consécutifs",
    },
    {
      tag: "dateDebut",
      label: "Date de début",
      input_type: "date",
      required: true,
      size: "col-span-6",
    },
    {
      tag: "dateFin",
      label: "Date de fin",
      input_type: "date",
      required: true,
      size: "col-span-6",
    },
    {
      tag: "preuve",
      label: "Preuve (pièce jointe)",
      input_type: "file",
      required: false,
      size: "col-span-6",
    },
    {
      tag: "commentaire",
      label: "Commentaire",
      input_type: "textarea",
      required: false,
      size: "col-span-6",
      placeholder: "Ajoutez un commentaire (optionnel)",
    },
  ];

  const onSubmit = async (data: z.infer<typeof absenceSchema>) => {
    try {
      setLoading(true);

      const dateDebut = new Date(data.dateDebut);
      const dateFin = new Date(data.dateFin);
      const duration = differenceInDays(dateFin, dateDebut) + 1;

      const formData = new FormData();
      formData.append("motifAbsenceId", data.motifAbsenceId);
      formData.append("libelle", data.libelle);
      formData.append("modeJouissance", data.modeJouissance);
      formData.append("dateDebut", format(data.dateDebut, "yyyy-MM-dd"));
      formData.append("dateFin", format(data.dateFin, "yyyy-MM-dd"));
      formData.append("duree", `${duration} jours`);
      formData.append("addedById", String(user?.id));
      formData.append("createdAt", new Date().toISOString());
      if (data.commentaire) formData.append("commentaire", data.commentaire);
      if (data.preuve instanceof File) formData.append("preuve", data.preuve);

      await apiService.post(
        {
          url: apiRoutes.admin.app.employee.absences.create,
          body: formData,
          headers: {
            "Content-Type": "multipart/form-data",
          },
        },
        {
          userToken: `${user?.type} ${user?.token}`,
          hasNoSuccessModal: false,
          onTokenExpired: logout,
        }
      );

      form.reset();
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { form });
      }
    } finally {
      setLoading(false);
    }
  };

  const fetchSelectOptions = async () => {
    try {
      setIsFetching(true);

      const motifsRes = await apiService.get(
        { url: apiRoutes.admin.app.employee.absences.motifsList },
        {
          userToken: `${user?.type} ${user?.token}`,
          hasNoSuccessModal: true,
          onTokenExpired: logout,
        }
      );

      setMotifs((motifsRes.data || []).map((item: any) => ({ value: item.id.toString(), label: item.libelle })));
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: true });
      }
    } finally {
      setIsFetching(false);
    }
  };

  useEffect(() => {
    fetchSelectOptions();
  }, []);

  return (
    <div className="h-full max-h-[calc(100vh-150px)] overflow-y-auto px-4 py-6">
      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
          <div className="grid grid-cols-12 gap-4">
            {fields.map((field, index) => (
              <BindFormItem
                key={field.tag}
                option={field}
                form={form}
                tag={field.tag}
                index={index}
                readonly={false}
              />
            ))}
          </div>
          <div className="flex justify-end">
            <ButtonWithLoading
              type="submit"
              loading={loading}
              title="Soumettre"
              classList="bg-blue-900 text-white"
            />
          </div>
        </form>
      </Form>
    </div>
  );
  
}
