import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent, AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogTrigger,
} from "@/components/ui/alert-dialog";

import { ReactNode } from "react";
import { Button } from "@/components/ui/button";
import clsx from "clsx";

interface DetailDialogProps {
    triggerLabel?: string;
    title?: string;
    description?: string;
    children?: ReactNode;
    confirmText?: string;
    cancelText?: string;
    onConfirm?: () => void;
    showCancel?: boolean;
    triggerButton?: ReactNode;
    size?: "sm" | "md" | "lg" | "xl"|"xxl"|"xxxl";
    isOpen?: boolean;
    setIsOpen?: (isOpen: boolean) => void;
}

// ✅ Map des tailles vers des classes
const sizeClasses = {
    sm: "max-w-sm",
    md: "max-w-md",
    lg: "max-w-2xl",
    xl: "max-w-4xl",
    xxl: "max-w-5xl",
    xxxl: "max-w-7xl",
};

export function DetailDialog({
                                triggerLabel = "Voir détails",
                                title = "Détails",
                                description,
                                children,
                                confirmText = "Fermer",
                                cancelText = "Annuler",
                                onConfirm,
                                showCancel = false,
                                triggerButton,
                                size = "md",
                                isOpen,
                                setIsOpen
                             }: DetailDialogProps) {
    return (
        <AlertDialog open={isOpen} onOpenChange={setIsOpen}>
            <AlertDialogTrigger asChild className="hidden">
                {triggerButton || <Button variant="outline">{triggerLabel}</Button>}
            </AlertDialogTrigger>

            <AlertDialogContent
                className={clsx(
                    sizeClasses[size],
                    "flex flex-col p-0 sm:max-h-[min(640px,80vh)]"
                )}
            >
                {/* Header */}
                <div className="px-6 pt-6">
                    <AlertDialogHeader className={"space-y-0"}>
                        <AlertDialogTitle className={"p-0 text-black font-bold"}>{title}</AlertDialogTitle>
                        {description && (
                            <AlertDialogDescription className="text-xs italic text-muted-foreground p-0">{description}</AlertDialogDescription>
                        )}
                    </AlertDialogHeader>
                </div>

                {/* Contenu scrollable */}
                <div className="px-6 overflow-y-auto flex-1">
                    <div className=" text-sm">{children}</div>
                </div>

                {/* Footer fixé */}
                <AlertDialogFooter className="border-t px-6 py-4 mt-auto">
                    {showCancel && <AlertDialogCancel>{cancelText}</AlertDialogCancel>}
                    <AlertDialogAction onClick={onConfirm}>{confirmText}</AlertDialogAction>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
);
}
