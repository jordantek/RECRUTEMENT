// C:/Users/Dev/Documents/GitHub/TGP_/talentsgestpaie/tpcsirh-front/src/components/inputs/input-number-with-chevrons.tsx

"use client";

import { ChevronDown, ChevronUp } from "lucide-react";
// Importez NumberFieldProps pour un typage plus robuste
import {
    Button,
    Group,
    Input,
    Label,
    NumberField,
    type NumberFieldProps,
} from "react-aria-components";
import { cn } from "@/lib/utils.ts";

// Étendez les props de base de NumberField pour hériter de `value`, `onChange`, etc.
interface InputNumberWithChevronsProps extends NumberFieldProps {
    label?: string;
    currency?: string;
    className?: string;
    // La prop onValueChange était manquante.
    // Nous la mappons sur le `onChange` de react-aria, qui passe directement le nombre.
    onValueChange?: (value: number) => void;
}

// Destructurez les props explicitement pour plus de clarté
export default function InputNumberWithChevrons2({
                                                    label,
                                                    currency,
                                                    className,
                                                    onValueChange,
                                                    ...props // Le reste des props de NumberField comme `value`, `defaultValue`, `minValue`
                                                }: InputNumberWithChevronsProps) {
    return (
        <NumberField
            {...props} // Passe toutes les props restantes (value, defaultValue, etc.)
            onChange={onValueChange} // Connecte notre prop au gestionnaire interne onChange
            {...(currency && { formatOptions: { style: "currency", currency } })}
            className={cn("group/number-field", className)}
        >
            <div className="space-y-2">
                {label && (
                    <Label className="text-sm font-medium text-foreground">{label}</Label>
                )}

                <Group className="relative inline-flex h-9 w-full items-center overflow-hidden whitespace-nowrap rounded-md border border-input text-sm shadow-sm transition-colors data-[focus-within]:border-ring data-[disabled]:opacity-50 data-[focus-within]:outline-none data-[focus-within]:ring-2 data-[focus-within]:ring-ring/20">
                    <Input className="w-full flex-1 bg-background px-3 py-2 tabular-nums text-foreground focus:outline-none" />
                    <div className="flex h-full flex-col border-l border-input">
                        <Button
                            slot="increment"
                            className="h-1/2 flex flex-1 items-center justify-center rounded-none bg-background px-1 text-muted-foreground/80 transition-colors hover:bg-accent hover:text-foreground disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50"
                        >
                            <ChevronUp size={14} strokeWidth={2} aria-hidden="true" />
                        </Button>
                        <Button
                            slot="decrement"
                            className="h-1/2 flex flex-1 items-center justify-center rounded-none border-t border-input bg-background px-1 text-muted-foreground/80 transition-colors hover:bg-accent hover:text-foreground disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50"
                        >
                            <ChevronDown size={14} strokeWidth={2} aria-hidden="true" />
                        </Button>
                    </div>
                </Group>
            </div>
        </NumberField>
    );
}