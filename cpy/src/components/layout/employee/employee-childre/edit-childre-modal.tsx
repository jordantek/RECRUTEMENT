import {Dialog,DialogContent,DialogDescription,DialogHeader,DialogTitle} from "@/components/ui/dialog.tsx";
import { EnfantFormData, enfantSchema } from "./childrenValidator";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Form } from "@/components/ui/form.tsx";
import {BindFormItem, FieldOption} from "@/components/forms/bind-form-item.tsx";
import ButtonWithLoading from "@/components/ui/button-with-loading.tsx";
import { useEffect, useState } from "react";
import { useAuth } from "@/lib/auth.ts";
import apiService from "@/api/apiService.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import { Button } from "@/components/ui/button.tsx";
import { EnfantType } from "@/types/employee/EmployeeType.ts";

interface EditChildreModalProps {
    isOpen: boolean;
    setIsOpen: (isOpen: boolean) => void;
    enfant: EnfantType | null;
    onUpdated?: () => void;
}

export function EditChildreModal({isOpen,setIsOpen,enfant,onUpdated}: EditChildreModalProps) {
    const { user, logout } = useAuth();
    const [isSubmitting, setIsSubmitting] = useState(false);

    const form = useForm<EnfantFormData>({
        resolver: zodResolver(enfantSchema),
        defaultValues: { },
        mode: "onSubmit",
    });

    const fields = [
        { tag: "nom", label: "Nom", input_type: "text", size: "col-span-12", required: true },
        { tag: "prenom", label: "Prénom", input_type: "text", size: "col-span-12", required: true },
        {
            tag: "sexe",
            label: "Sexe",
            input_type: "select",
            size: "col-span-12",
            required: true,
            options: ["MASCULIN", "FEMININ"].map((v) => ({ label: v, value: v })),
        },
        { tag: "dateNaissance", label: "Date de naissance", input_type: "date", size: "col-span-6", required: true },
        {
            tag: "lieuNaissance",
            label: "Lieu de naissance",
            input_type: "text",
            size: "col-span-6",
            required: true,
            placeholder: "Ex: Cotonou",
        },
    ];

    useEffect(() => {
        if (enfant) {
            form.reset({
                nom: enfant.nom,
                prenom: enfant.prenom,
                sexe: enfant.sexe?.toUpperCase() === "FÉMININ" ? "FEMININ" : enfant.sexe?.toUpperCase(),

                lieuNaissance: enfant.lieuNaissance,
            });
        }
    }, [enfant]);

    const onSubmit = async (data: EnfantFormData) => {
        if (!enfant) return;

        setIsSubmitting(true);
        try {
            const payload = {
                enfant: { ...data },
            };

            await apiService.put(
                {
                    url: `${apiRoutes.admin.app.employee.children.update}/${enfant.id}`,
                    body: JSON.stringify(payload),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: false,
                    onTokenExpired: logout,
                }
            );

            if (onUpdated) onUpdated();
            setIsOpen(false);
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { form });
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
            <DialogContent className="sm:max-w-lg lg:max-w-[400px]">
                <DialogHeader>
                    <DialogTitle className="text-lg font-semibold">Modifier un enfant</DialogTitle>
                    <DialogDescription className="text-sm">
                        Mettez à jour les informations de l'enfant.
                    </DialogDescription>
                </DialogHeader>

                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                        <div className="grid grid-cols-12 gap-1">
                            {fields.map((option, index) => (
                                <div key={index} className={option.size}>
                                    <BindFormItem
                                        index={index}
                                        option={option as FieldOption}
                                        form={form}
                                        tag={option.tag}
                                        readonly={false}
                                    />
                                </div>
                            ))}
                        </div>

                        <div className="flex justify-end gap-2">
                            <Button
                                type="button"
                                variant="ghost"
                                onClick={() => setIsOpen(false)}
                                className="w-fit"
                            >
                                Annuler
                            </Button>

                            <ButtonWithLoading
                                type="submit"
                                classList="w-fit"
                                title="Enregistrer"
                                loading={isSubmitting}
                            />
                        </div>
                    </form>
                </Form>
            </DialogContent>
        </Dialog>
    );
}
