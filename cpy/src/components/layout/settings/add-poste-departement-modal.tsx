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

type AddPosteModalProps = {
    isOpen: boolean
    setIsOpen: (isOpen: boolean) => void
    departementId: number
    refresh: () => void
}

export default function AddPosteDepartementModal({ isOpen, setIsOpen, departementId, refresh }: AddPosteModalProps) {
    const user = useUser()
    const [libelle, setLibelle] = useState("")
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        if (!libelle.trim()) {
            setError("Le nom du poste est requis.")
            return
        }

        try {
            setIsSubmitting(true)
            setError(null)

            await apiService.post(
                {
                    url: apiRoutes.admin.app.company.departement.poste.create,
                    body: {
                        libelle,
                        departement_id: departementId
                    }
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: false
                }
            )

            refresh()
            setLibelle("")
            setIsOpen(false)
        } catch (err) {
            setError("Erreur lors de la création du poste.")
            console.error(err)
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
            <DialogContent className="max-w-md p-6">
                <DialogHeader>
                    <DialogTitle className="flex items-center gap-2 text-left">
                        <FolderPlusIcon size={16} />
                        Ajouter un Poste
                    </DialogTitle>
                    <DialogDescription className="text-left">
                        Remplissez le champ pour ajouter un poste à ce département.
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit} className="space-y-2">
                    <div className="space-y-1">
                        <Label htmlFor="poste-libelle">Libellé du poste</Label>
                        <Input
                            id="poste-libelle"
                            type="text"
                            placeholder="Ex : Responsable RH"
                            value={libelle}
                            onChange={(e) => setLibelle(e.target.value)}
                            disabled={isSubmitting}
                        />
                        {error && <p className="text-sm text-red-500">{error}</p>}
                    </div>

                    <DialogFooter className="flex justify-end pt-4 gap-2">
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
