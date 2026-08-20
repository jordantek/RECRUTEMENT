
import React from "react"
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip"

type InfoTooltipProps = {
    title: string
    description: string
    children: React.ReactNode
    side?: "top" | "right" | "bottom" | "left"
    align?: "start" | "center" | "end"
}

export function InfoTooltip({
                                title,
                                description,
                                children,
                                side = "top",
                                align = "center"
                            }: InfoTooltipProps) {
    return (
        <TooltipProvider delayDuration={0}>
            <Tooltip>
                <TooltipTrigger asChild>{children}</TooltipTrigger>
                <TooltipContent side={side} align={align} className="py-3 w-80 bg-white border">
                    <div className="space-y-1">
                        <p className="text-[13px] font-medium leading-none text-black">{title}</p>
                        <p className="text-muted-foreground text-xs">{description}</p>
                    </div>
                </TooltipContent>
            </Tooltip>
        </TooltipProvider>
    )
}