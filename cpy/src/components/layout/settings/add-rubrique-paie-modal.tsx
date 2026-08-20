
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,

} from "@/components/ui/alert-dialog"
import { Button } from "@/components/ui/button"
import {Form} from "@/components/ui/form.tsx";
import {BindFormItem, FieldOption} from "@/components/forms/bind-form-item.tsx";
import ButtonWithLoading from "@/components/ui/button-with-loading.tsx";
import {z} from "zod";
import {useForm} from "react-hook-form";
import {zodResolver} from "@hookform/resolvers/zod";


interface AddRubriquePaieModalProps {
    isOpen: boolean;
    setIsOpen: (isOpen: boolean) => void;
}

export default function AddRubriquePaieModal({ isOpen, setIsOpen,}: AddRubriquePaieModalProps) {
    const rubriqueFild = [
        {
            tag: "libelle",
            label: "Libellé de la rubrique",
            input_type: "text",
            size: "col-span-6",
            required: true,
        },
        {
            tag: "nature",
            label: "Nature de la rubrique",
            input_type: "text",
            size: "col-span-6",
            required: true,
        },
        {
            tag: "niveau_affichage_id",
            label: "Niveau d'affichage",
            input_type: "text",
            size: "col-span-6",
            required: true,
        },
        {
            tag: "gain",
            label: "Gain ou perte",
            input_type: "text",
            size: "col-span-6",
            required: true,
        },
        {
            tag: "choix",
            label: "Choisissez une option",
            input_type: "select",
            size: "col-span-6",
            required: true,
            options: [
                { value: "OUI", label: "Oui" },
                { value: "NON", label: "Non" }
            ]
        }

    ];

    const RubriquePaieSchema = z.object({
        libelle: z.string().min(1, "Le libellé est requis"),
        nature: z.string().min(1, "La nature est requise"),
        niveau_affichage_id: z.string().min(1, "Le niveau d'affichage est requis"),
        gain: z.string().min(1, "Gain ou perte est requis"),
        choix: z.string().min(1, "Le choix est requis"),
        /*retenue_legale: z.string().min(1, "La retenue légale est requise"),
        autre_retenue: z.string().min(1, "Autre retenue est requise"),
        charge_patronale: z.string().min(1, "La charge patronale est requise"),
        est_imposable: z.string().min(1, "L'imposabilité est requise"),
        calcul_au_prorata_temps_travail: z.string().min(1, "Le prorata temps de travail est requis"),*/
    });



    const onSubmit = async (data: z.infer<typeof RubriquePaieSchema>) => {
        console.log("Form data submitted:", data);
    };

    const form = useForm<z.infer<typeof RubriquePaieSchema>>({
        resolver: zodResolver(RubriquePaieSchema),
        defaultValues: {},
    });

    return (
        <AlertDialog open={isOpen} onOpenChange={setIsOpen}>

            <AlertDialogContent>
                <AlertDialogHeader>
                    <AlertDialogTitle></AlertDialogTitle>

                    <AlertDialogDescription>
                        <Form {...form}>
                            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                                {rubriqueFild.map((option, index) => (
                                    <div key={index} className={option.size}>
                                        <BindFormItem
                                            key={index}
                                            index={index}
                                            option={option as FieldOption}
                                            form={form}
                                            tag={option.tag}
                                            readonly={false}
                                        />
                                    </div>
                                ))}

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
                                        loading={false}
                                    />
                                </div>
                            </form>
                        </Form>
                    </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                    <AlertDialogCancel>Cancel</AlertDialogCancel>
                    <AlertDialogAction>Okay</AlertDialogAction>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    )
}
