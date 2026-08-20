"use client"

import { Button } from "@/components/ui/button"
import {
Stepper,
StepperIndicator,
StepperItem,
StepperSeparator,
StepperTrigger,
} from "@/components/ui/stepper"

interface StepperProps {
currentStep: number
setCurrentStep: (step: number) => void
}

const steps = [1, 2, 3]

export default function StepperStateSettings({
currentStep,
setCurrentStep,
}: StepperProps) {
const handleNextStep = () => {
    if (currentStep < steps.length) {
    setCurrentStep(currentStep + 1)
    }
}

const handlePrevStep = () => {
    if (currentStep > 1) {
    setCurrentStep(currentStep - 1)
    }
}

return (
    <div className="mx-auto max-w-5xl space-y-8 text-center px-4">
    {/* Affichage de l'étape */}
    <p
        className="w-full max-w-3xl text-muted-foreground text-xs pr-44"
        role="region"
        aria-live="polite"
    >
        Étape {currentStep} sur {steps.length}
    </p>
    {/* Stepper horizontal centré */}
    <Stepper
        value={currentStep}
        onValueChange={setCurrentStep}
        className="flex justify-center  "
    >
        {steps.map((step, index) => (
        <StepperItem key={step} step={step} className="flex-1 text-center ">
            <StepperTrigger asChild>
            <StepperIndicator />
            </StepperTrigger>
            {index < steps.length - 1 && (
            <StepperSeparator className="w-full h-0.5 bg-muted mx-2" />
            )}
        </StepperItem>
        ))}
    </Stepper>

    {/* Boutons de navigation */}
    <div className="flex w-full max-w-3xl justify-center space-x-28 pr-28">
        <Button
        variant="outline"
        className="w-32"
        onClick={handlePrevStep}
        disabled={currentStep === 1}
        >
        Étape précédente
        </Button>
        <Button
        variant="outline"
        className="w-32"
        onClick={handleNextStep}
        disabled={currentStep === steps.length}
        style={{ backgroundColor: "#1E3A8A", color: "white" }}
        >
        Étape suivante
        </Button>
    </div>

    </div>
)
}
