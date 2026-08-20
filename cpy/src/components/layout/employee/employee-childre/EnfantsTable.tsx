import {Pencil, Baby, Trash2} from "lucide-react";
import {
    Table,
    TableHeader,
    TableRow,
    TableHead,
    TableBody,
    TableCell,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import {EnfantType} from "@/types/employee/EmployeeType.ts";
import {EditChildreModal} from "@/components/layout/employee/employee-childre/edit-childre-modal.tsx";
import {useEffect, useState} from "react"; // Assurez-vous que ce composant existe

interface EnfantsTableProps {
    enfants: EnfantType[];
    isLoading: boolean;
}

export function EnfantsTable({ enfants, isLoading }: EnfantsTableProps) {
    const [isOpenEditChildrenModal, setIsOpenEditChildrenModal] = useState(false);
    const [selectedEnfant, setSelectedEnfant] = useState<EnfantType | null>(null);

    const handleEditClick = (enfant: EnfantType) => {
        setSelectedEnfant(enfant);
        setIsOpenEditChildrenModal(true);
    };

    const handleCloseModal = () => {
        setIsOpenEditChildrenModal(false);
        setSelectedEnfant(null);
    };

    useEffect(() => {
        if (!isOpenEditChildrenModal) {
            setSelectedEnfant(null);
        }
    }, [isOpenEditChildrenModal]);

    if (isLoading) {
        return (
            <div className="w-full overflow-x-auto rounded-lg border border-muted shadow-sm">
                <Skeleton className="h-4 w-full" />
            </div>
        );
}
    return (

        <div className="w-full overflow-x-auto rounded-lg border border-muted shadow-sm">
            <EditChildreModal isOpen={isOpenEditChildrenModal} setIsOpen={handleCloseModal} enfant={selectedEnfant} onUpdated={handleCloseModal} />
            <Table className="min-w-full text-sm">
                <TableHeader className="bg-muted/40">
                    <TableRow className="h-5">
                        <TableHead className="py-1 font-medium text-muted-foreground">
                            Nom & Prénoms
                        </TableHead>
                        <TableHead className="py-1 text-center font-medium text-muted-foreground">
                            Sexe
                        </TableHead>
                        <TableHead className="py-1 text-center font-medium text-muted-foreground">
                            Date de naissance
                        </TableHead>
                        <TableHead className="py-1 text-center font-medium text-muted-foreground">
                            Actions
                        </TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {isLoading ? (
                        // Skeleton Rows
                        Array.from({ length: 3 }).map((_, idx) => (
                            <TableRow key={idx} className="h-9">
                                <TableCell className="py-1">
                                    <Skeleton className="h-4 w-32" />
                                </TableCell>
                                <TableCell className="py-1 text-center">
                                    <Skeleton className="h-4 w-12 mx-auto" />
                                </TableCell>
                                <TableCell className="py-1 text-center">
                                    <Skeleton className="h-4 w-8 mx-auto" />
                                </TableCell>
                            </TableRow>
                        ))
                    ) : enfants.length === 0 ? (
                        <TableRow>
                            <TableCell
                                colSpan={3}
                                className="text-center py-2 text-muted-foreground italic text-xs"
                            >
                                Aucun enfant enregistré.
                            </TableCell>
                        </TableRow>
                    ) : (
                        enfants.map((enfant) => (
                            <TableRow key={enfant.id} className="hover:bg-muted/10 h-9">
                                <TableCell className="py-1 flex items-center gap-2 text-sm font-normal">
                                    <Baby className="w-4 h-4 text-blue-500" />
                                    {enfant.nom} {enfant.prenom}
                                </TableCell>
                                <TableCell className="py-1 text-center text-sm">
                                    {enfant.sexe}
                                </TableCell>
                                <TableCell className="py-1 text-center text-sm">
                                    {enfant.dateNaissance}
                                </TableCell>
                                <TableCell className="py-1">
                                    <div className="flex items-center justify-center gap-2">
                                        <Button
                                            size="icon"
                                            className="h-8 w-8 bg-blue-50 shadow-none hover:border-blue-600 hover:bg-blue-100"
                                            onClick={() => handleEditClick(enfant)}
                                        >
                                            <Pencil className="h-3 w-3 text-blue-600 " />
                                        </Button>
                                        <Button
                                            size="icon"
                                            className="h-8 w-8 bg-red-50 text-red-600 hover:text-red-600  shadow-none hover:border-red-600 hover:bg-red-100"
                                          //  onClick={() => handleEditClick(enfant)}
                                        >
                                            <Trash2 className="h-3 w-3 " />
                                        </Button>
                                    </div>
                                </TableCell>
                            </TableRow>
                        ))
                    )}
                </TableBody>
            </Table>
        </div>
    );
}
