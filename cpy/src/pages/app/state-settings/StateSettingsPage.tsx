import { useState } from "react"
import { motion, AnimatePresence } from "framer-motion"
import PageTitle from "@/components/seo/pageTitle"
import StepperHeader from "./stepper-header"
import StepperNavigation from "./stepper-navigation"
import StateSettingsForm from "./state-settings-form"
import StateSettingsFormStep2 from "./state-settings-form-2"
import StateSettingsFormStep3 from "./state-settings-form-3"
import { Button } from "@/components/ui/button"

const steps = [1, 2, 3]

export function StateSettingsPage() {
  const [currentStep, setCurrentStep] = useState(1)
  const [wantsToProcessOwnCompanySalary, setWantsToProcessOwnCompanySalary] = useState<null | boolean>(null)
  const [wantsToSendPayslipsByEmail, setWantsToSendPayslipsByEmail] = useState<null | boolean>(null)

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

  const renderFormForStep = () => {
    if (currentStep === 1) return <StateSettingsForm />

    if (currentStep === 2) {
      if (wantsToProcessOwnCompanySalary === null) {
        return (
          <div className="space-y-4">
            <p className="mt-6 pl-64 text-sm text-muted-foreground">
              Voulez-vous traiter le salaire de votre propre entreprise ?
            </p>
            <div className="flex gap-4 justify-center">
              <Button variant="outline" onClick={() => setWantsToProcessOwnCompanySalary(true)}>
                Oui
              </Button>
              <Button variant="secondary" onClick={() => setWantsToProcessOwnCompanySalary(false)}>
                Non
              </Button>
            </div>
          </div>
        )
      }

      if (wantsToProcessOwnCompanySalary) {
        return <StateSettingsFormStep2 />
      }

      return (
        <p className="mt-6 pl-56 text-sm text-muted-foreground">
          Vous avez choisi de ne pas traiter le salaire de votre entreprise.
        </p>
      )
    }

    if (currentStep === 3) {
      if (wantsToSendPayslipsByEmail === null) {
        return (
          <div className="space-y-4">
            <p className="mt-6 pl-64 text-sm text-muted-foreground">
              Voulez-vous envoyer les bulletins de paie par mail ?
            </p>
            <div className="flex gap-4 justify-center">
              <Button variant="outline" onClick={() => setWantsToSendPayslipsByEmail(true)}>
                Oui
              </Button>
              <Button variant="secondary" onClick={() => setWantsToSendPayslipsByEmail(false)}>
                Non
              </Button>
            </div>
          </div>
        )
      }

      if (wantsToSendPayslipsByEmail) {
        return <StateSettingsFormStep3 />
      }

      return (
        <p className="mt-6 pl-56 text-sm text-muted-foreground">
          Vous avez choisi de ne pas envoyer les bulletins de paie par mail.
        </p>
      )
    }

    return null
  }

  return (
    <>
      <PageTitle title="Tableau de bord" />
      <div className="flex flex-col items-center px-4 pb-16 scale-[0.9]">
        <div className="w-full max-w-5xl mt-8">
          <div className="flex justify-center items-start">
            <div className="flex-1 flex flex-col items-center">
              <motion.h2
                className="text-2xl font-semibold tracking-tight"
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6 }}
              >  <h1 className="mb-4 text-3xl font-extrabold text-gray-900 dark:text-white md:text-5xl lg:text-5xl"><span className="text-transparent bg-clip-text bg-gradient-to-r to-blue-600 from-sky-700">Configuration</span> </h1>  

              </motion.h2>
              <motion.p
                className="text-muted-foreground mt-1 text-sm"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ delay: 0.2, duration: 0.6 }}
              >
                Suivez les étapes pour configurer votre environnement RH.
              </motion.p>

              {/* Stepper en haut */}
              <motion.div
                className="mt-6 pl-32 w-full max-w-xl mx-auto"
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.6 }}
              >
                <StepperHeader
                  currentStep={currentStep}
                  setCurrentStep={(step) => {
                    setCurrentStep(step)
                    setWantsToProcessOwnCompanySalary(null)
                    setWantsToSendPayslipsByEmail(null)
                  }}
                />
              </motion.div>

              {/* Formulaire au centre */}
              <AnimatePresence mode="wait">
                <motion.div
                  key={`form-${currentStep}-${wantsToProcessOwnCompanySalary}-${wantsToSendPayslipsByEmail}`}
                  className="mt-2 w-full pl-20"
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: -10 }}
                  transition={{ duration: 0.4 }}
                >
                  {renderFormForStep()}
                </motion.div>
              </AnimatePresence>

              {/* Navigation en bas */}
              <StepperNavigation
                currentStep={currentStep}
                stepsLength={steps.length}
                onPrev={handlePrevStep}
                onNext={handleNextStep}
              />
            </div>
          </div>
        </div>
      </div>
    </>
  )
}
