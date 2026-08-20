import {
    AlertDialog,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle, AlertDialogTrigger,
} from "@/components/ui/alert-dialog";

import { Input } from "@/components/ui/input";
import { useState } from "react";
import {Select,SelectContent,SelectItem,SelectTrigger,SelectValue} from "@/components/ui/select";
import { DiplomeType } from "@/types/UtilsTypes";
import clsx from "clsx";
import {Button} from "@/components/ui/button.tsx";
import {Plus} from "lucide-react";

interface AddEmployeeDiplomaModalProps {
    diplomas: DiplomeType[];
    onSubmit: (newDiploma: {
        employeId?: number;
        diplomeId: number;
        denomination: string;
        anneeObtention: number;
    }) => void;
}

export function AddEmployeeDiplomaModal({diplomas,onSubmit}: AddEmployeeDiplomaModalProps) {
    const [diplomeId, setDiplomeId] = useState("");
    const [denomination, setDenomination] = useState("");
    const [anneeObtention, setAnneeObtention] = useState("");
    const [isOpen, setIsOpen] = useState(false);
    const [errors, setErrors] = useState({
        diplomeId: "",
        denomination: "",
        anneeObtention: "",
    });

    const validate = (): boolean => {
        const currentYear = new Date().getFullYear();

        const newErrors = {
            diplomeId: !diplomeId ? "Sélectionnez un diplôme." : "",
            denomination: !denomination.trim() ? "L'intitulé est requis." : "",
            anneeObtention:
                !anneeObtention ||
                !/^\d{4}$/.test(anneeObtention) ||
                Number(anneeObtention) < 1900 ||
                Number(anneeObtention) > currentYear
                    ? `Année invalide (entre 1900 et ${currentYear}).`
                    : "",
        };

        setErrors(newErrors);

        // Retourne true si aucune erreur, false sinon
        return Object.values(newErrors).every((err) => err === "");
    };

    const handleConfirm = () => {
        const isValid = validate();
        if (!isValid) {
            return;
            setIsOpen(true)
        }
        onSubmit({
            diplomeId: Number(diplomeId),
            denomination,
            anneeObtention: Number(anneeObtention),
        });

        setDiplomeId("");
        setDenomination("");
        setAnneeObtention("");
        setErrors({ diplomeId: "", denomination: "", anneeObtention: "" });
        setIsOpen(false);
    };

    return (
        <AlertDialog open={isOpen} onOpenChange={setIsOpen} >
            <AlertDialogTrigger asChild>
                <Button
                    size={"sm"}
                    title="Ajouter un diplôme"
                >
                    <Plus className="h-4 w-4" /> Ajouter un diplôme
                </Button>
            </AlertDialogTrigger>
            <AlertDialogContent>
                <AlertDialogHeader>
                    <AlertDialogTitle>Ajouter un diplôme</AlertDialogTitle>
                    <AlertDialogDescription>
                        <div className="space-y-4">
                            {/* Diplôme */}
                            <div>
                                <p className="mb-1 text-sm">Type de diplôme</p>
                                <Select value={diplomeId} onValueChange={setDiplomeId}>
                                    <SelectTrigger className={clsx({ "border-red-500": errors.diplomeId })}>
                                        <SelectValue placeholder="Sélectionnez un diplôme" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {diplomas.map((diplome) => (
                                            <SelectItem key={diplome.id} value={diplome.id.toString()}>
                                                {diplome.name}
                                            </SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                                {errors.diplomeId && <p className="text-xs text-red-500 mt-1">{errors.diplomeId}</p>}
                            </div>

                            {/* Intitulé */}
                            <div>
                                <p className="mb-1 text-sm">Intitulé</p>
                                <Input
                                    placeholder="Ex : Informatique"
                                    className={clsx({ "border-red-500": errors.denomination })}
                                    value={denomination}
                                    onChange={(e) => setDenomination(e.target.value)}
                                />
                                {errors.denomination && (
                                    <p className="text-xs text-red-500 mt-1">{errors.denomination}</p>
                                )}
                            </div>

                            {/* Année */}
                            <div>
                                <p className="mb-1 text-sm">Année d'obtention</p>
                                <Input
                                    type="number"
                                    placeholder="Ex : 2023"
                                    min={1900}
                                    max={new Date().getFullYear()}
                                    className={clsx({ "border-red-500": errors.anneeObtention })}
                                    value={anneeObtention}
                                    onChange={(e) => setAnneeObtention(e.target.value)}
                                />
                                {errors.anneeObtention && (
                                    <p className="text-xs text-red-500 mt-1">{errors.anneeObtention}</p>
                                )}
                            </div>
                        </div>
                    </AlertDialogDescription>
                </AlertDialogHeader>

                <AlertDialogFooter>
                    <AlertDialogCancel>Annuler</AlertDialogCancel>
                    < Button onClick={handleConfirm}>Ajouter</Button>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    );
}
