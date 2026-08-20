"use client"

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

type AddCategoryProps = {
    isOpen: boolean
    setIsOpen: (isOpen: boolean) => void
    refresh: () => void
}

type Errors = {
    name?: string
    description?: string
}

export default function AddEmployeeCategoryModal({ isOpen, setIsOpen, refresh }: AddCategoryProps) {
    const user = useUser()
    const [name, setName] = useState("")
    const [description, setDescription] = useState("")
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [errors, setErrors] = useState<Errors>({})

    const validateForm = () => {
        const newErrors: Errors = {}
        if (!name.trim()) newErrors.name = "Le nom de la catégorie est requis."
        if (!description.trim()) newErrors.description = "La description est requise."
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
                    url: apiRoutes.admin.app.employee.categories.create,
                    body: { name, description }
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: false
                }
            )

            refresh()
            setIsOpen(false)

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
                        Ajouter une catégorie
                    </DialogTitle>
                    <DialogDescription className="text-left">
                        Définissez une nouvelle catégorie d'employé
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit} className="space-y-2">
                    {/* Nom */}
                    <div className="space-y-1">
                        <Label htmlFor="name">Nom de la catégorie</Label>
                        <Input
                            id="name"
                            type="text"
                            placeholder="Ex : Développeur"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            disabled={isSubmitting}
                        />
                        {errors.name && (
                            <p className="text-sm text-red-500">{errors.name}</p>
                        )}
                    </div>

                    {/* Description */}
                    <div className="space-y-1">
                        <Label htmlFor="description">Description</Label>
                        <Input
                            id="description"
                            type="text"
                            placeholder="Ex : Travaille sur les projets frontend"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            disabled={isSubmitting}
                        />
                        {errors.description && (
                            <p className="text-sm text-red-500">{errors.description}</p>
                        )}
                    </div>

                    <DialogFooter className="flex justify-end gap-2 pt-4">
                        <Button type="button" variant="outline" onClick={() => setIsOpen(false)}>
                            Annuler
                        </Button>
                        <Button type="submit" disabled={isSubmitting}>
                            {isSubmitting ? "Enregistrement..." : "Enregistrer"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}
