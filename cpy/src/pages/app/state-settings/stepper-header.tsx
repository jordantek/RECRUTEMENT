"use client"

import {
  Stepper,
  StepperIndicator,
  StepperItem,
  StepperSeparator,
  StepperTrigger,
} from "@/components/ui/stepper"

interface StepperHeaderProps {
  currentStep: number
  setCurrentStep: (step: number) => void
}

const steps = [1, 2, 3]

export default function StepperHeader({
  currentStep,
  setCurrentStep,
}: StepperHeaderProps) {
  return (
    <div className="mx-auto max-w-5xl space-y-4 text-center px-4">
      <p className="pl-14 w-full max-w-3xl text-muted-foreground text-xs pr-44" role="region" aria-live="polite">
        Étape {currentStep} sur {steps.length}
      </p>
      <Stepper
        value={currentStep}
        onValueChange={setCurrentStep}
        className="flex justify-center"
      >
        {steps.map((step, index) => (
          <StepperItem key={step} step={step} className="flex-1 text-center ">
            <StepperTrigger asChild >
            <StepperIndicator className="!bg-blue-900 !text-white !border-blue-900" />
            </StepperTrigger >
            {index < steps.length - 1 && (
              <StepperSeparator className="w-full h-0.5 bg-muted mx-2 !bg-blue-300" />
            )}
          </StepperItem>
        ))}
      </Stepper>
    </div>
  )
}
