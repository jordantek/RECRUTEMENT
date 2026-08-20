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

import ButtonWithLoading from "@/components/ui/button-with-loading.tsx";

interface DeleteModalProps {
    title: string
    description: string
    isOpen: boolean
    isetIsOpen: (isOpen: boolean) => void
    isDeleteLoading: boolean
    onDelete: () => void
    onCancel: () => void
}

export default function DeleteModal(
    {
        title,
        description,
        isOpen,
        isetIsOpen,
        isDeleteLoading,
        onDelete,
        onCancel,
    }: DeleteModalProps

) {
    return (
        <AlertDialog open={isOpen} onOpenChange={isetIsOpen}>
            <AlertDialogContent>
                <AlertDialogHeader>
                    <AlertDialogTitle>{title}</AlertDialogTitle>
                    <AlertDialogDescription>
                        {description}
                    </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                    <AlertDialogCancel onClick={onCancel}>Annuler</AlertDialogCancel>
                    <AlertDialogAction onClick={onDelete} className={"w-fit bg-transparent hover:bg-transparent p-0 shadow-none"}>
                        <ButtonWithLoading
                        type="button"
                        classList="w-fit flex items-center gap-2 text-white bg-red-500 hover:bg-red-600 dark:bg-red-600 dark:hover:bg-red-700"
                        title="Supprimer"
                        loading={isDeleteLoading ?? false}
                        />
                    </AlertDialogAction>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    )
}
