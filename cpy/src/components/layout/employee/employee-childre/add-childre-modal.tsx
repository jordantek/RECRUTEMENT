import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog.tsx";

import { EnfantFormData, enfantSchema } from "@/components/layout/employee/employee-childre/childrenValidator.ts";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Form } from "@/components/ui/form.tsx";
import {BindFormItem, FieldOption} from "@/components/forms/bind-form-item.tsx";
import ButtonWithLoading from "@/components/ui/button-with-loading.tsx";
import { useEffect, useState } from "react";
import { useAuth } from "@/lib/auth.ts";
import apiService from "@/api/apiService.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import {Button} from "@/components/ui/button.tsx";

interface AddChildreProps {
    isOpen: boolean;
    setIsOpen: (isOpen: boolean) => void;
    employeId: number;
}

export function AddChildreModal({ isOpen, setIsOpen, employeId }: AddChildreProps) {
    const { user, logout } = useAuth();
    const [isSubmitting, setIsSubmitting] = useState(false);

    const form = useForm<EnfantFormData>({
        resolver: zodResolver(enfantSchema),
        defaultValues: {},
        mode: "onSubmit",
        shouldFocusError: true,
    });

    const enfantFild = [
        {
            tag: "nom",
            label: "Nom",
            input_type: "text",
            size: "col-span-12",
            required: true,
        },
        {
            tag: "prenom",
            label: "Prénom",
            input_type: "text",
            size: "col-span-12",
            required: true,
        },
        {
            tag: "sexe",
            label: "Sexe",
            input_type: "select",
            size: "col-span-12",
            required: true,
            options: ["MASCULIN", "FEMININ"].map((v) => ({ label: v, value: v })),
        },
        {
            tag: "dateNaissance",
            label: "Date de naissance",
            input_type: "date",
            size: "col-span-6",
            required: true,
        },
        {
            tag: "lieuNaissance",
            label: "Lieu de naissance",
            input_type: "text",
            size: "col-span-6",
            placeholder: "Ex: Cotonou",
            required: true,
        },
    ];

    const onSubmit = async (data: EnfantFormData) => {
        setIsSubmitting(true);
        try {
            const payload = {
                employeId,
                enfant: { ...data,dateNaissance: new Date(data.dateNaissance).toISOString().split("T")[0] },
                estDecede: false,
            };

            await apiService.post(
                {
                    url: apiRoutes.admin.app.employee.children.create,
                    body: JSON.stringify(payload),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: false,
                    onTokenExpired: logout,
                }
            );
            form.reset();
            setIsOpen(false);
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { form });
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    useEffect(() => {
        form.reset({ ...form.getValues() });
    }, [employeId]);

    return (
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
            <DialogContent className="sm:max-w-lg lg:max-w-[400px]">
                <DialogHeader >
                    <DialogTitle className="text-lg font-semibold">Ajouter un enfant</DialogTitle>
                    <DialogDescription className="text-sm">
                        Remplissez les informations de l'enfant à rattacher à l’employé.
                    </DialogDescription>
                </DialogHeader>

                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                        <div className="grid grid-cols-12 gap-1">
                            {enfantFild.map((option, index) => (
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
                                className="w-fit "
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
