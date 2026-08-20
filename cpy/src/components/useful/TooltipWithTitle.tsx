import { ReactNode } from "react"
import {
    Tooltip,
    TooltipContent,
    TooltipProvider,
    TooltipTrigger,
} from "@/components/ui/tooltip"

type TooltipPlacement = "top" | "bottom" | "left" | "right"

type TooltipTriggerType = "hover" | "click" | "focus" | "manual"

interface CustomTooltipProps {
    trigger: ReactNode
    title?: string
    children: ReactNode
    delayDuration?: number
    className?: string
    placement?: TooltipPlacement
    triggerType?: TooltipTriggerType
}

export function TooltipWithTitle({
                                     trigger,
                                     title,
                                     children,
                                     delayDuration = 0,
                                     className = "",
                                     placement = "top",

                                 }: CustomTooltipProps) {
    return (
        <TooltipProvider delayDuration={delayDuration}>
            <Tooltip>
                <TooltipTrigger asChild>
                    {trigger}
                </TooltipTrigger>
                <TooltipContent side={placement} className={`py-3 ${className}`}>
                    <div className="space-y-1 max-w-xs">
                        {title && <p className="text-[13px] font-medium">{title}</p>}
                        <div className="text-muted-foreground text-xs">{children}</div>
                    </div>
                </TooltipContent>
            </Tooltip>
        </TooltipProvider>
    )
}
