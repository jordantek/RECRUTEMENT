import {  AlertDialog,AlertDialogAction,AlertDialogCancel,AlertDialogContent,AlertDialogDescription,AlertDialogFooter,AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger
} from "@/components/ui/alert-dialog.tsx";
import { Button } from "@/components/ui/button.tsx";
import { Plus } from "lucide-react";
import { RubriqueSalaireType } from "@/types/UtilsTypes.ts";
import { useState } from "react";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue
} from "@/components/ui/select.tsx";
import { Input } from "@/components/ui/input.tsx";

interface AddElementSalaireModalProps {
    isOpen: boolean;
    setIsOpen: (open: boolean) => void;
    rubriques: RubriqueSalaireType[];
    onSubmit: (element: { rubriqueId: number; montant: number }) => void;
}

export function AddElementSalaireModal({setIsOpen,rubriques,onSubmit}: AddElementSalaireModalProps) {

    const [selectedId, setSelectedId] = useState<string | null>(null);
    const [montant, setMontant] = useState<string>("");

    const handleConfirm = () => {
        if (selectedId && montant) {
            onSubmit({
                rubriqueId: Number(selectedId),
                montant: Number(montant),
            });
            setSelectedId(null);
            setMontant("");
            setIsOpen(false);
        }
    };

    return (
        <AlertDialog/* open={isOpen} onOpenChange={setIsOpen}*/>
            <AlertDialogTrigger asChild>
                <Button

                    size="sm"
                    title="Ajouter un élément"
                >
                    <Plus /> Ajouter un élément de salaire
                </Button>
            </AlertDialogTrigger>
            <AlertDialogContent>
                <AlertDialogHeader>
                    <AlertDialogTitle>Ajouter un élément</AlertDialogTitle>
                    <AlertDialogDescription>
                        <div>
                            <p className="mb-2">Sélectionnez une rubrique :</p>
                            <Select value={selectedId ?? ""} onValueChange={setSelectedId}>
                                <SelectTrigger className="w-full">
                                    <SelectValue placeholder="Sélectionnez une rubrique" />
                                </SelectTrigger>
                                <SelectContent>
                                    {rubriques.map((ru) => (
                                        <SelectItem key={ru.id} value={ru.id.toString()}>
                                            {ru.libelle}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>

                            <p className="mt-4 mb-2">Montant</p>
                            <Input
                                type="number"
                                value={montant}
                                onChange={(e) => setMontant(e.target.value)}
                                placeholder="Entrez un montant"
                            />
                        </div>
                    </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                    <AlertDialogCancel>Annuler</AlertDialogCancel>
                    <AlertDialogAction onClick={handleConfirm}>
                        Ajouter
                    </AlertDialogAction>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    );
}
