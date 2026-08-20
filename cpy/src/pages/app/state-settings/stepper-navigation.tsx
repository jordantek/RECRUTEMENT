"use client"

import { Button } from "@/components/ui/button"

interface StepperNavigationProps {
  currentStep: number
  stepsLength: number
  onPrev: () => void
  onNext: () => void
}

export default function StepperNavigation({
  currentStep,
  stepsLength,
  onPrev,
  onNext,
}: StepperNavigationProps) {
  return (
    <div className="flex w-full max-w-3xl justify-center space-x-28 mt-8">
      <Button
        variant="outline"
        className="w-32"
        onClick={onPrev}
        disabled={currentStep === 1}
      >
        Étape précédente
      </Button>
      <Button
        variant="outline"
        className="w-32"
        onClick={onNext}
        disabled={currentStep === stepsLength}
        style={{ backgroundColor: "#1E3A8A", color: "white" }}
      >
        Étape suivante
      </Button>
    </div>
  )
}
