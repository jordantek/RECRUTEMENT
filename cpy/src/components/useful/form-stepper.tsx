// src/components/ui/form-stepper.tsx
import {
    Stepper,
    StepperItem,
    StepperTrigger,
    StepperIndicator,
    StepperSeparator,
} from "@/components/ui/stepper";

interface FormStepperProps {
    steps: { title: string }[];
    currentStep: number;
    onStepChange: (step: number) => void;
    className?: string;
}

export function FormStepper({ steps, currentStep, onStepChange, className = "" }: FormStepperProps) {
    return (
        <div className={`flex justify-center w-full px-6 mb-2 ${className}`}>
            <Stepper value={currentStep} onValueChange={onStepChange}
                     className="w-full max-w-4xl flex justify-between items-center">
                {steps.map((step, index) => (
                    <StepperItem key={index} step={index}>
                        <StepperTrigger asChild>
                            <div className="flex flex-col items-center">
                                <StepperIndicator />
                                <span className="text-xs mt-2 text-center text-gray-600 dark:text-gray-300 font-medium">
                                    {step.title}
                                </span>
                            </div>
                        </StepperTrigger>
                        {index < steps.length - 1 && (
                            <StepperSeparator
                                className="w-full max-w-[120px] h-0.5 bg-gray-200 transition-all duration-500 group-data-[state=active]/stepper-separator:bg-primary"
                            />
                        )}
                    </StepperItem>
                ))}
            </Stepper>
        </div>
    );
}