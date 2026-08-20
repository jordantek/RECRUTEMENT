import { useState } from "react"
import { FolderPlusIcon } from "lucide-react"
import { Button } from "@/components/ui/button.tsx"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog.tsx"
import { Input } from "@/components/ui/input.tsx"
import { Label } from "@/components/ui/label.tsx"
import apiService from "@/api/apiService.ts"
import apiRoutes from "@/api/apiRoutes.ts"
import { useUser } from "@/contexts/UserContext.ts"

type AddDepartementModalProps = {
    isOpen: boolean
    setIsOpen: (open: boolean) => void
    companyId: number
    refresh: () => void
}

type Errors = {
    libelle?: string
}

export default function AddDepartementCompanyModal({
                                                isOpen,
                                                setIsOpen,
                                                companyId,
                                                refresh
                                            }: AddDepartementModalProps) {
    const user = useUser()
    const [libelle, setLibelle] = useState("")
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [errors, setErrors] = useState<Errors>({})

    const validateForm = () => {
        const newErrors: Errors = {}
        if (!libelle.trim()) newErrors.libelle = "Le nom du département est requis."
        return newErrors
    }

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        const validationErrors = validateForm()
        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors)
            return
        }

        try {
            setIsSubmitting(true)
            setErrors({})

            await apiService.post(
                {
                    url: apiRoutes.admin.app.company.departement.create,
                    body: {
                        libelle,
                        company_id: companyId
                    }
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: false
                }
            )

            refresh()
            setIsOpen(false)
            setLibelle("") // Reset field
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: true })
            }
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
            <DialogContent className="max-w-md p-6">
                <DialogHeader>
                    <DialogTitle className="text-left flex items-center gap-2">
                        <FolderPlusIcon className="opacity-80" size={16} />
                        Ajouter un Département
                    </DialogTitle>
                    <DialogDescription className="text-left">
                        Entrez un nom pour ajouter un nouveau département à l'entreprise.
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit} className="space-y-2">
                    <div className="space-y-1">
                        <Label htmlFor="libelle">Nom du département</Label>
                        <Input
                            id="libelle"
                            type="text"
                            placeholder="Ex : Informatique"
                            value={libelle}
                            onChange={(e) => setLibelle(e.target.value)}
                            disabled={isSubmitting}
                        />
                        {errors.libelle && (
                            <p className="text-sm text-red-500">{errors.libelle}</p>
                        )}
                    </div>

                    <DialogFooter className="flex justify-end gap-2 pt-4">
                        <Button type="button" variant="outline" onClick={() => setIsOpen(false)}>
                            Annuler
                        </Button>
                        <Button type="submit" disabled={isSubmitting}>
                            {isSubmitting ? "Ajout en cours..." : "Ajouter"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}
